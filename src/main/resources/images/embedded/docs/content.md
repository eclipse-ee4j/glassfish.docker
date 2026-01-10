# Eclipse GlassFish Embedded Docker Image

[Eclipse GlassFish](https://glassfish.org) is a Jakarta EE compatible implementation. This is the **Embedded** version that runs as a single JAR file without requiring a full server installation.

%%LOGO%%

**Source code repository of the Docker image:** https://github.com/eclipse-ee4j/glassfish.docker

## Quick start

### Start GlassFish Embedded

Run GlassFish Embedded with the following command:

```
docker run -p 8080:8080 @docker.glassfish.embedded.repository@
```

Or with a command for a specific tag (GlassFish version):

```
docker run -p 8080:8080 @docker.glassfish.embedded.image@
```

Open the following URL in the browser to access the HTTP port:

* http://localhost:8080

**Note:** GlassFish Embedded does not include the Administration Console. Use the full GlassFish Server image if you need administrative capabilities.

### Stop GlassFish Embedded

Stop GlassFish Embedded with the following command:

```
docker stop CONTAINER_ID
```

CONTAINER_ID can be found from the output of the following command:

```
docker ps
```

## Run an application with GlassFish Embedded in Docker

You can run an application located in your filesystem with GlassFish Embedded in a Docker container.

Follow these steps:

1. Create an empty directory on your filesystem, e.g. `/deployments`
2. Copy the application package to this directory - so that it's for example on the path `/deployments/application.war`
3. Run the following command to start GlassFish Embedded in Docker with your application, where /deployments is the directory created in step 1 and /deploy is the directory inside the container where Embedded GlassFish expects applications:

```
docker run -p 8080:8080 -v /deployments:/deploy @docker.glassfish.embedded.repository@
```

Then you can open the application in the browser with:

* http://localhost:8080

If there's a single application, if will be available under the roo (`/`) context root. If there are multiple applications, each will be available under a context root derived from the name of the application file (e.g. `application.war` would be deployed under the `/application` context root).

## Debug GlassFish Embedded inside a Docker container

You can enable debug mode by specifying JVM debug arguments in the standard `JAVA_TOOL_OPTIONS` environment variable and expose the debug port. For example:

```
docker run -p 9009:9009 -p 8080:8080 -e 'JAVA_TOOL_OPTIONS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:9009' @docker.glassfish.embedded.repository@ 
```

Then connect your debugger to port 9009 on `localhost`.

If you need to suspend GlassFish startup until you connect the debugger, change `suspend=n` to `suspend=y`:

```
docker run -p 9009:9009 -p 8080:8080 -e 'JAVA_TOOL_OPTIONS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:9009' @docker.glassfish.embedded.repository@
```

## Environment variables

The following environment variables are available:

* `PATH_GF_HOME` - directory that contains Embedded GlassFish JAR and is used as the default working directory  (`/opt/glassfish`)
* `PATH_GF_JAR` - path to the Embedded GlassFish JAR file
* `GLASSFISH_VERSION` - version of GlassFish

## Additional configuration

### Custom JVM arguments

You can pass custom JVM arguments by setting them in the standard `JAVA_TOOL_OPTIONS` variable

```
docker run -p 8080:8080 -e 'JAVA_TOOL_OPTIONS=-Xmx512m -Dmy.property=value' @docker.glassfish.embedded.repository@
```

### Custom application deployment

Applications placed in the `/deploy` directory will be automatically deployed at startup:

```
docker run -p 8080:8080 -v /path/to/apps:/deploy @docker.glassfish.embedded.repository@
```

Paths to applications in different locations can be passed on command line, e.g.:

```
docker run -p 8080:8080 @docker.glassfish.embedded.repository@ /mydeployments/myapp.war
```

NOTE: If you point to a path on the command line, the path must exist inside the container. If it's not there, mount a local directory, e.g. using the `-v` option.

### Using with custom Dockerfile

You can create a custom Docker image based on GlassFish Embedded:

```dockerfile
FROM @docker.glassfish.embedded.repository@

# Copy your application
COPY myapp.war /deploy/

# Set custom JVM options
ENV JAVA_TOOL_OPTIONS=-Xmx512m
```

## Examples of advanced usage

### Running with specific JVM settings

```bash
docker run -p 8080:8080 -e JAVA_TOOL_OPTIONS='-Xmx1g -XX:+UseG1GC' @docker.glassfish.embedded.repository@
```

### Running in background with logs

```bash
docker run -d -p 8080:8080 @docker.glassfish.embedded.repository@
CONTAINER_ID=$(docker ps -q --filter ancestor=@docker.glassfish.embedded.repository@)
docker logs -f $CONTAINER_ID
```

### Running with custom user (useful for Kubernetes)

```bash
docker run --user 1000 -p 8080:8080 @docker.glassfish.embedded.repository@
```

## TestContainers

This is a simple test example using [Embedded GlassFish ](https://glassfish.org/) and [TestContainers](https://www.testcontainers.org/):

```java
@Testcontainers
public class EmbeddedGlassFishITest {

    @Container
    private final GenericContainer server = new GenericContainer<>("@docker.glassfish.embedded.image@")
            .withExposedPorts(8080);

    @Test
    void testServerStartup() throws Exception {
        URL url = new URL("http://localhost:" + server.getMappedPort(8080) + "/");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            connection.setRequestMethod("GET");
            assertEquals(200, connection.getResponseCode());
        } finally {
            connection.disconnect();
        }
    }
}
```