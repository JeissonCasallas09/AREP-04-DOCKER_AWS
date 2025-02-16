## AREP-04 MODULATION THROUGH VIRTUALIZATION USING DOCKER AND AWS

For this project, a concurrent microserver was developed using technologies such as Java, JavaScript, CSS, and HTML. The goal was to test functionalities such as REST requests, server concurrency, virtualization using Docker, and deployments with the help of DockerHub and AWS.

![](images/DESPLIEGUE.gif)



## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

You need to install the following tools to run the project:
1. Java

   Try using the following command
    ```
    java -version
    ```
    You should see something like this:

    ![](/images/1.png)

2. Maven

   We need maven too, so use the following command.

    ```
    mvn -version
    ```
    You should see something like this:
    
    ![](/images/2.png)

3. Git

   we must have git. You can check it with this command.
    ```
    git --version
    ```
    It should appear something like this:
    
    ![](/images/3.png)

4. Docker

   to finish, we need to have the lastest docker version.

   ```
    docker --version
    ```
   ![](/images/19.png)
   

### Installing The MicroServer

1. First, we need to open a terminal and put the following command to clone the project:

    ```
    git clone https://github.com/JeissonCasallas09/AREP-04-DOCKER_AWS
    ```
2. Open the folder with the project in a new terminal and build it with the following command:
    ```
    mvn package
    ```
    This message will tell you that it was successfull:

     ![](/images/4.png)

3. Now just run the project using:
    ```
    mvn exec:java
    ```
    The project is now running:

    ![](/images/5.png)
    
    
4. Enter from your browser to the local server with port 8080

   ![](/images/20.png)
 

5. if you want to close the server in a professional way, you just have to type "ctrl+c" and see how it works. 

   ![](/images/6.png)


### Installing on AWS EC2 using Docker

   ## Part 1 Docker images
   1. Inside your project you must have this dependencies, properties and builds in the file pom.xml
      ```
      <dependencies>
         <dependency>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-starter-web</artifactId>
               <version>3.3.3</version>
         </dependency>
      </dependencies>

      <properties>
            <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
            <maven.compiler.source>17</maven.compiler.source>
            <maven.compiler.target>17</maven.compiler.target>
      </properties>.

         <!-- build configuration -->
         <build>
            <plugins>
                  <plugin>
                     <groupId>org.apache.maven.plugins</groupId>
                     <artifactId>maven-dependency-plugin</artifactId>
                     <version>3.0.1</version>
                     <executions>
                        <execution>
                              <id>copy-dependencies</id>
                              <phase>package</phase>
                              <goals><goal>copy-dependencies</goal></goals>
                        </execution>
                     </executions>
                  </plugin>
            </plugins>
         </build>
      ```
   2. Now compile the project again.
      ```
      mvn clean install
      ```
      In the target directory you must find a new one called dependency

      ![](/images/21.png)

   3. Now run the project using the following command:

      ![](/images/7.png)

      ![](/images/20.png)

   4. In the main directory of the project you need the following file called "DockerFile":
      
      ![](/images/8.png)

      It should look similar to the image.

   5. Now using the following commands we are going to build an imagen in docker.
      * Create a image.

         ![](/images/9.png)

      * Create a container instance of the image by specifying the physical port and mock port.

         ![](/images/10.png)
   
      * Make sure that container are running.

         ![](/images/11.png)

      * See how it looks.

         ![](/images/12.png)

   6. Now we are going to use docker compose to generate an automatic configuration, for this we need a new file called "docker-compose.yml"
      
      * It should look similar.

         ![](/images/22.png)
      
      * Now ejecute the command docker-compose.

         ![](/images/13.png)

      * verify if the new services are running.

         ![](/images/14.png)

   ## Part 2 DockerHub
   
   1. Now we need an account on Docker Hub, after having one, let's create a repository where we can upload the image.

   ![](/images/15.png)

   2. Now we will create a reference to the image, check if the creation was successful and log in with docker as shown in the image.

      ![](/images/16.png)

   3. Now push the image to the repository.

      ![](/images/17.png)

   4. See how it looks.

   ![](/images/23.png)

   ## Part 3 AWS

   1.  First, we need con create an instante into our AWS Account, while we are creating it, we need to generate a pair of keys, and put into a local directory. It should look something similar to this. 
      ![](/images/24.png)
      ![](/images/26.png)
   
   2. Now we have to go to the security settings of the instance and open the port 42000.

      ![](/images/25.png)
   3. and using the button "conectar" we will conect to the instante by "SSH CLIENT"
      ![](/images/27.png)

   4. We are going to enter the directory where we have the key and we are going to open a terminal and we will enter the indicated command.
      ```
      ssh -i "clavefinallab04.pem" ec2-user@ec2-44-201-227-96.compute-1.amazonaws.com
      ```
      ![](/images/18.png)
   5. Now that we are inside the virtual machine, we will put the following commands:

      * we look for updates
      ```
      sudo yum update -y
      ```
      * we install docker

      ```
      sudo yum install docker
      ```

      * we start the docker service

      ```
      sudo service docker start
      ```

      * we start the docker image that we have in the repository

      ```
      docker run -d -p 42000:6000 --name firstdockerimageaws jeissonc/labarep04
      ```

      * If everything went well we should be able to join the microserver.

      ```
      http://ec2-44-201-227-96.compute-1.amazonaws.com:42000/
      ```

      ![](/images/28.png)

## Architecture


### Overview
Project Architecture
This project follows a simple microserver-based architecture with three main components:

   ![](/images/29.png)
* ### Static Files

   * Contains essential frontend assets, including:

   * Styles: CSS files for styling the interface.

    * Script: JavaScript files for dynamic behavior.

   * Index: The main HTML file that serves as the entry point of the application.
   These files are served by the server to the client.

* ### Server

   * Implements a MicroServer responsible for handling requests and serving static files.
   * Acts as an intermediary between the client and backend logic.

* ### Controller

   * The GreetingController manages request handling and responses.
   * It processes user requests and provides appropriate responses.

* ### Workflow

   1. The client accesses the Static Files (HTML, CSS, JavaScript) through the MicroServer.
   2. The MicroServer communicates with the GreetingController to handle dynamic requests.
   3. The GreetingController processes incoming requests and returns the necessary data.
   
This architecture ensures a clear separation of concerns, allowing for easy maintenance and scalability.

![](/images/30.png)

* ### Interfaces
   1. RequestParam:
      
      * Used to handle parameters in HTTP requests.
      * Methods:
         1. value() : String → Returns the parameter's value.
         2. defaultValue() : String → Defines a default value.

   2. GetMapping:
      * Maps methods to HTTP GET routes.
      * Method:
         1. value() : String → Returns the assigned route.

   3. RestController:
      * Indicates that a class is a REST controller, used for Annotations.

* ### GreetingController Class
   This class acts as a controller within the web server, handling different routes and performing string operations.

   Main Methods:

   1. greeting(name: String) : String

      * Returns a personalized greeting with the received name.
      * Example: GET /app/greeting?name=John → "Hello, John!"
   
   2. pi() : String

      * return a predefined value, in this case the value of PI.
   
   3. index() : String

      *  Returns a  welcome message.
   
   4. countChars(word: String) : String

      * Returns the number of characters in the given word.
      * Example: GET /app/countChars?word=hello → "5"
   
   5. concat(a: String, b: String) : String

      * Concatenates two strings and returns the result.
      * Example: GET /app/concat?a=Hello&b=World → "HelloWorld"
   
   6. max(a: String, b: String, c: String) : String

      * Returns the maximum value among three strings (likely compared alphabetically or numerically).
      * Example: GET /app/max?a=3&b=5&c=4 → "5"

*  ### MicroServer Class
This is the core class of the server, managing HTTP requests and responses.

   #### Attributes:
   *  PORT : int = 8080 → Defines the port where the server listens.

   * BASE_PATH : String = "/app" → Base path for requests.

   * STATIC_FOLDER : String = "src/main/java/edu/eci/arep/webserver/static/"
   → Directory where static files (HTML, CSS, JS) are stored.
   
   * routes : Map<String, Method> → Maps routes to their corresponding methods.

   #### Main Methods:
   
   1. main(args: String[]) : void

      * Main entry point to start the server.
   
   2. startServer() : void

      * Starts the server on port 8080 and waits for requests.
   
   3. registerRoutes() : void

      * Registers the available routes in the controller.
   
   4. invokeControllerMethod(path: String, queryString: String) : String

      *  Calls the controller method associated with the requested route.
   
   5. extractParameters(method: Method, queryString: String) : Object[]

      * Extracts parameters from a request and assigns them to the controller methods.
   
   6. parseQuery(query: String) : Map<String,String>

      * Parses the query string (?param1=value1&param2=value2) into a key-value map.
   
   7. sendResponse(out: OutputStream, status: String, contentType: String, body: String) : void

      * Sends an HTTP response to the client with the generated content.
   
   8. serveStaticFile(out: OutputStream, filePath: String) : void

      * Serves static files like HTML, CSS, or images.
   
   9. getContentType(filePath: String) : String

      * Determines the content type of a requested file (e.g., text/html, application/json, etc.).
   
   10. getRoutes() : Map<String,Method>

   * Determines the content type of a requested file (e.g., text/html, application/json, etc.).

   11. stopServer() : Void

   * Allows you to shut down the server in an elegant way

## Running the tests

The project includes unit tests to validate the functionality of the MicroServer, ensuring correct behavior when handling dinamic files, REST endpoints, concurrence and content types. Below is an explanation of each test:

### MicroServerTest

🔹 **1. testRegisterRoutes()**

   **Purpose:**
   Ensures that the server registers routes correctly.

   **Functionality:**

   Retrieves the registered routes map from the server.
   Checks that it is not null.
   Ensures that it contains at least one registered route.

   ✅ If the test passes: The server is correctly registering routes.

   ❌ If the test fails: Routes may not be properly added.

🔹 **2. testInvokeControllerMethod()**

   **Purpose:**
   Ensures that the server can correctly execute a controller method associated with a specific route.

   **Functionality:**

   Invokes the controller method for the /app/greeting route, passing the parameter name=John.
   Checks that the response is "Hola John".

   ✅ If the test passes: Controller method invocation works correctly.

   ❌ If the test fails: There may be an issue with routing logic or method execution.

🔹 **3. testParseQuery()**

   **Purpose:**
   Verifies that the parseQuery() function correctly extracts parameters from a key=value formatted query string.

   **Functionality:**

   Calls parseQuery("name=John&age=25").
   Ensures that name is stored as "John".
   Ensures that age is stored as "25".

   ✅ If the test passes: The query parsing function works correctly.

   ❌ If the test fails: There may be errors in query string parsing.

🔹 **4. testSendResponse()**

   **Purpose:**
   Ensures that the server correctly sends a valid HTTP response.

   **Functionality:**

   Simulates an HTTP response with status 200 OK, content type application/json, and body {"status":"ok"}.
   Checks that the generated response contains:
   "HTTP/1.1 200 OK"
   "application/json"
   {"status":"ok"}

   ✅ If the test passes: The server is correctly sending responses.

   ❌ If the test fails: There may be an issue in constructing the HTTP response.

🔹 **5. testConcurrentRequests()**

   **Purpose:**
   Ensures that the server can handle multiple concurrent requests without errors.

   **Functionality:**

   Creates 10 threads (threadCount = 10).
   Each thread establishes a connection with the server and sends a GET /app/greeting?name=Test request.
   Ensures that each response contains the expected message (Hello, Test).
   Waits up to 10 seconds to ensure all requests complete.

   ✅ If the test passes: The server correctly handles concurrent requests.

   ❌ If the test fails: There may be synchronization or blocking issues in the server.

🔹 **6. testServerShutdown()**

   **Purpose:**
   Ensures that the server can shut down correctly.

   **Functionality:**

   Calls server.stopServer().
   Checks that server.isShutdown() returns true.

   ✅ If the test passes: The server shuts down properly.

   ❌ If the test fails: There may be issues with the server shutdown implementation.

🔹 **7. testInvokeNonExistentRoute()**

   **Purpose:**
   Ensures that the server correctly handles a request to a non-existent route.

   **Functionality:**

   Calls server.invokeControllerMethod("/app/notfound", "").
   Ensures that the response is {"error": "Ruta no encontrada"}.

   ✅ If the test passes: The server correctly handles unknown routes.

   ❌ If the test fails: The server may not be properly managing invalid routes.





![](/images/31.png)

These tests ensure that the MicroServer works correctly in terms of route registration, method invocation, parameter handling, HTTP responses, concurrency, and server shutdown. 🚀



## Conclusions

 1. The implementation of concurrency in this project has significantly improved performance and processing efficiency. Tasks now run in parallel, reducing wait times and making better use of system resources. This demonstrates the importance of designing applications that can handle multiple operations simultaneously without affecting stability. 🚀


2. By using Docker, the deployment of the project has become much simpler and more reliable. Containerization has made the system independent of the environment in which it runs, eliminating configuration issues and making maintenance easier. This strategy ensures that the application can be replicated and executed on different platforms without complications. 🚀


3. Thanks to deployment on AWS, the system is now capable of scaling efficiently based on demand. The cloud infrastructure provides high availability and flexibility, ensuring that the service remains operational even under heavy loads. This represents a key improvement in the stability and adaptability of the project to different usage scenarios. 🚀


4. This project has successfully integrated key technologies such as concurrency, containerization with Docker, and cloud deployment with AWS. Each of these improvements has contributed to a more efficient, portable, and scalable system. The combination of these elements not only optimizes performance but also makes maintenance and future development easier. 🚀


## Built With

* [Maven](https://maven.apache.org/) - Dependency Control
* [GIT](https://git-scm.com) - Versioning
* [Docker](https://hub.docker.com/) -Virtualization


## Versioning

versioning made it by [GitHub](http://git-scm.com).

## Authors

* **Jeisson Steban Casallas Rozo** - [JeissonCasallas09](https://github.com/JeissonCasallas09)

Date: 16/02/2025
## License

This project is licensed by ECI.

