package edu.eci.arep.webserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MicroServer {

    private static final int PORT = 8080;
    private static final String BASE_PATH = "/app";
    private static final String STATIC_FOLDER = "src/main/java/edu/eci/arep/webserver/static/";
    private static final Map<String, Method> routes = new HashMap<>();
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);
    private static final AtomicBoolean running = new AtomicBoolean(true);

    public static void main(String[] args) {
        MicroServer server = new MicroServer();
        Runtime.getRuntime().addShutdownHook(new Thread(server::stopServer));
        server.startServer();
    }

    public void startServer() {
        registerRoutes();
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server listening on port " + PORT);
            while (running.get()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    executor.submit(() -> handleClient(clientSocket));
                } catch (IOException e) {
                    if (running.get()) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            OutputStream out = clientSocket.getOutputStream()
        ) {
            String requestLine = in.readLine();
            if (requestLine == null) return;
            System.out.println("Request: " + requestLine);

            String[] parts = requestLine.split(" ");
            if (parts.length < 2) return;

            String url = parts[1];
            String[] urlParts = url.split("\\?", 2);
            String path = urlParts[0];
            String queryString = urlParts.length > 1 ? urlParts[1] : "";

            if (path.equals("/")) {
                serveStaticFile(out, "index.html");
            } else if (routes.containsKey(path)) {
                String response = invokeControllerMethod(path, queryString);
                sendResponse(out, "200 OK", "application/json", response);
            } else {
                serveStaticFile(out, path.substring(1));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopServer() {
        running.set(false);
        executor.shutdown();
        System.out.println("Server shutting down... Bye!");
        
        // Mensaje adicional después de la confirmación de terminación
        try {
            Thread.sleep(1000); // Pequeña pausa para simular una salida ordenada
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("Todos los procesos han finalizado. Gracias por usar MicroServer.");
    }
    

    public void registerRoutes() {
        try {
            Class<?> controllerClass = GreetingController.class;
            for (Method method : controllerClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(GetMapping.class)) {
                    String route = BASE_PATH + method.getAnnotation(GetMapping.class).value();
                    routes.put(route, method);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String invokeControllerMethod(String path, String queryString) {
        try {
            Method method = routes.get(path);
            if (method == null) return "{\"error\": \"Ruta no encontrada\"}";

            Object[] params = extractParameters(method, queryString);
            return method.invoke(null, params).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error en el servidor";
        }
    }

    private Object[] extractParameters(Method method, String queryString) {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];

        Map<String, String> queryParams = parseQuery(queryString);
        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            RequestParam requestParam = param.getAnnotation(RequestParam.class);

            if (requestParam != null) {
                String paramName = requestParam.value();
                String defaultValue = requestParam.defaultValue();
                String value = queryParams.getOrDefault(paramName, defaultValue);
                args[i] = value;
            }
        }
        return args;
    }

    public static Map<String, String> parseQuery(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.isEmpty()) return params;

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        return params;
    }

    public void sendResponse(OutputStream out, String status, String contentType, String body) throws IOException {
        String response = "HTTP/1.1 " + status + "\r\n" +
                          "Content-Type: " + contentType + "\r\n" +
                          "Content-Length: " + body.length() + "\r\n" +
                          "\r\n" +
                          body;
        out.write(response.getBytes());
    }

    private void serveStaticFile(OutputStream out, String filePath) {
        try {
            File file = new File(STATIC_FOLDER + filePath);
            if (!file.exists() || file.isDirectory()) {
                sendResponse(out, "404 Not Found", "text/plain", "File not found");
                return;
            }

            String contentType = getContentType(filePath);
            byte[] fileData = Files.readAllBytes(Paths.get(file.getAbsolutePath()));

            String header = "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + contentType + "\r\n" +
                            "Content-Length: " + fileData.length + "\r\n" +
                            "\r\n";

            out.write(header.getBytes());
            out.write(fileData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getContentType(String filePath) {
        if (filePath.endsWith(".html")) return "text/html";
        if (filePath.endsWith(".css")) return "text/css";
        if (filePath.endsWith(".js")) return "application/javascript";
        if (filePath.endsWith(".json")) return "application/json";
        if (filePath.endsWith(".png")) return "image/png";
        if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) return "image/jpeg";
        return "text/plain";
    }

    public Map<String, Method> getRoutes() {
        return routes;
    }

    public boolean isShutdown() {
        return executor.isShutdown();
    }
    
}
