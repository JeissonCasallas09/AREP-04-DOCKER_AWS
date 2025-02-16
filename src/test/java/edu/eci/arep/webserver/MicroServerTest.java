package edu.eci.arep.webserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MicroServerTest {
    private MicroServer server;

    @BeforeEach
    void setUp() {
        server = new MicroServer();
        server.registerRoutes();
    }

    @Test
    void testRegisterRoutes() {
        Map<String, Method> routes = server.getRoutes();
        assertNotNull(routes, "Routes should not be null");
        assertFalse(routes.isEmpty(), "Routes should be registered");
    }

    @Test
    void testInvokeControllerMethod() {
        String response = server.invokeControllerMethod("/app/greeting", "name=John");
        assertEquals("Hola John", response);
    }

    @Test
    void testParseQuery() {
        Map<String, String> params = MicroServer.parseQuery("name=John&age=25");
        assertEquals("John", params.get("name"), "Name should be parsed correctly");
        assertEquals("25", params.get("age"), "Age should be parsed correctly");
    }

    @Test
    void testSendResponse() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        server.sendResponse(outputStream, "200 OK", "application/json", "{\"status\":\"ok\"}");
        String response = outputStream.toString();
        assertTrue(response.contains("HTTP/1.1 200 OK"), "Response should contain status code");
        assertTrue(response.contains("application/json"), "Response should have correct content type");
        assertTrue(response.contains("{\"status\":\"ok\"}"), "Response body should be correct");
    }

    @Test
    void testConcurrentRequests() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try (Socket socket = new Socket("localhost", 8080);
                     BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                     BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                    writer.write("GET /app/greeting?name=Test HTTP/1.1\r\n\r\n");
                    writer.flush();

                    String responseLine;
                    boolean containsExpectedResponse = false;
                    while ((responseLine = reader.readLine()) != null) {
                        if (responseLine.contains("Hello, Test")) {
                            containsExpectedResponse = true;
                            break;
                        }
                    }
                    assertTrue(containsExpectedResponse, "Each thread should receive a proper response");
                } catch (IOException e) {
                    fail("Exception in concurrent test: " + e.getMessage());
                }
            });
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS), "All requests should complete");
    }

    @Test
    void testServerShutdown() {
        server.stopServer();
        assertTrue(server.isShutdown(), "Server should be properly shut down");
    }

    @Test
    void testInvokeNonExistentRoute() {
        String response = server.invokeControllerMethod("/app/notfound", "");
        assertEquals("{\"error\": \"Ruta no encontrada\"}", response, "Debe indicar que la ruta no existe");
    }

}
