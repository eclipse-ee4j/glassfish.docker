# Eclipse GlassFish Docker images

[Eclipse GlassFish](https://glassfish.org) is a Jakarta EE compatible implementation sponsored by the Eclipse Foundation.

%%LOGO%%

**Source code repository of the Docker image:** https://github.com/eclipse-ee4j/glassfish.docker

## Quick start

### Start GlassFish

Run GlassFish with the following command:

```
docker run -p 8080:8080 -p 4848:4848 glassfish
```

Or with a command for a specific tag (GlassFish version):

```
docker run -p 8080:8080 -p 4848:4848 glassfish:7.0.18
```

Open the following URLs in the browser:

* **Welcome screen:** http://localhost:8080
* **Administration Console:** https://localhost:4848 - log in using `admin`/`admin` (User name/Password)

### Stop GlassFish

Stop GlassFish with the following command:

```
docker stop CONTAINER_ID
```

CONTAINER_ID can be found from the output of the following command:

```
docker ps
```

### Start GlassFish Embedded

Run GlassFish Embedded runnable JAR (static shell) with the following command:

```
docker run -it -p 8080:8080 glassfish runembedded
```

This will run GlassFish static shell JAR from the GlassFish installation, which is equivalent to running a standalone GlassFish Embedded JAR. If no applications are deployed, GlassFish Embedded will start an interactive prompt to input commands. For this to work in Docker, the `-it` arguments are needed.

To display usage instructions, run:

```
docker run -it -p 8080:8080 glassfish runembedded --help
```

To deploy an application, copy the application into the Docker image or mount the directory that contains it, and then pass the path to it as an argument. For example, if the application myapp.war is copied to the default `/opt/glassfish7` directory:

```
docker run -p 8080:8080 glassfish runembedded myapp.war
```


## Run an application with GlassFish in Docker

You can run an application located in your filesystem with GlassFIsh in a Docker container.

Follow these steps:

1. Create an empty directory on your filesystem, e.g. `/deployment`
2. Copy the application package to this directory - so that it's for example on the path `/deployment/application.war`
3. Run the following command to start GlassFish in Docker with your application, where `/deployments` is path to the directory created in step 1:

```
docker run -p 8080:8080 -p 4848:4848 -v /deployments:/opt/glassfish7/glassfish/domains/domain1/autodeploy glassfish
```

Then you can open the application in the browser with:

* http://localhost:9080/application

The context root (`application`) is derived from the name of the application file (e.g. `application.war` would deployed under the `application` context root). If your application file has a different name, please adjust the contest root in the URL accordingly.

## Debug GlassFish Server inside a Docker container

You can modify the start command of the Docker container to `startserv --debug` to enable debug mode. You should also map the debug port 9009.

```
docker run -p 9009:9009 -p 8080:8080 -p 4848:4848 glassfish startserv --debug
```

Then connect your debugger to the port 9009 on `localhost`.

If you need suspend GlassFish startup until you connect the debugger, use the `--suspend` argument instead:

```
docker run -p 9009:9009 -p 8080:8080 -p 4848:4848 glassfish startserv --suspend
```

## Examples of advanced usage

Let's try something more complicated.

* To modify startup arguments for GlassFish, just add `startserv` to the command line and then add any arguments supported by the `asadmin start-domain` command. The `startserv` script is an alias to the `asadmin start-domain` command but starts GlassFish in a more efficient way that is more suitable in Docker container. For example, to start in debug mode with a custom domain, run:

```bash
docker run glassfish startserv --debug mydomain
```

* Environment variable `AS_TRACE=true` enables tracing of the GlassFish startup. It is useful when the server doesn't start without any useful logs.

* `docker run` with the `--user` argument configures explicit user id for the container. It can be useful for K8S containers.

* `docker run` with `-d` starts the container as a daemon, so the shell doesn't print logs and finishes. Docker then returns the container id which you can use for further commands.

```bash
docker run -d glassfish
```

Example of running a Docker container in background, view the logs, and then stop it (with debug enabled, trace logging, and user `1000` convenient for Kubernetes ):

```bash
docker run -d -e AS_TRACE=true --user 1000 glassfish startserv --debug=true
5a11f2fe1a9dd1569974de913a181847aa22165b5015ab20b271b08a27426e72

docker logs 5a11f2fe1a9dd1569974de913a181847aa22165b5015ab20b271b08a27426e72
...

docker stop 5a11f2fe1a9dd1569974de913a181847aa22165b5015ab20b271b08a27426e72
```

## Running GlassFish Embedded

### Run an application

To deploy an application with GlassFish Embedded, copy the application into the Docker image or mount the directory that contains it, and then pass the path to it as an argument. For example, if the application myapp.war is copied to the default `/opt/glassfish7` directory:

```
docker run -p 8080:8080 glassfish runembedded myapp.war
```

You can also just copy applications into the /opt/glassfish7/autodeploy directory or mount a directory with applications to it. All applications in that directory will be automatically deployed. For example, if you have applications on a local directory `/deployments`:

```
docker run -p 8080:8080 -v /deployments:/opt/glassfish7/autodeploy glassfish runembedded
```


### Configure GlassFish Embedded

You can configure GlassFish Embedded by command line aguments after the command runembedded, or by a configuration file. Several options are supported, for example set a different HTTP port or disable HTTP listener, set path to a custom domain configuration, run asadmin commands at startup, deploy applications.

You can also just create a configuration file called `glassfish.properties` in the default directory `/opt/glassfish7`, with all the options, including commands to execute and applications to deploy. Or specify path to a different configuration file with the `--properties` option.

To display usage instructions, run:

```
docker run -it -p 8080:8080 glassfish runembedded --help
```

This Docker image also supports adding custom Java VM arguments, with the JVM_OPTS environments variable. FOr example, you can specify `-XX:MaxRAMPercentage=75` to set maximum heap size to 75% of RAM:

```
docker run -it -e JVM_OPTS=="-XX:MaxRAMPercentage=75" -p 8080:8080 glassfish runembedded
```


### Debug with GlassFish Embedded

To enable debugging, you can either add a custom debugging instruction for the JVM with the `JVM_OPTS` variable, or you can set one of the following environment variables to `true`:

* `DEBUG` - enables remote debugger on port 9009, doesn't suspend the server
* `SUSPEND` - suspends the server right at the startup and continues when a debugger connects on port 9009

Example:

```
docker run -e SUSPEND=true -p 8080:8080 glassfish:7.0.18 runembedded
```

## TestContainers

This is probably the simplest possible test with [GlassFish](https://glassfish.org/) and [TestContainers](https://www.testcontainers.org/). It automatically starts the GlassFish Docker Container and then stops it after the test. The test here is quite trivial - downloads the welcome page and verifies if it contains expected phrases.

If you want to run more complicated tests, the good path is to

1.  Write a singleton managing the GlassFish Docker Container or the whole test environment.
2.  Write your own Junit5 extension which would start the container before your test and ensure that everything stops after the test including failures.
3.  You can also implement direct access to the virtual network, containers, so you can change the environment configuration in between tests and simulate network failures, etc.

```java
@Testcontainers
public class WelcomePageITest {

    @Container
    private final GenericContainer server = new GenericContainer<>("glassfish:7.0.18").withExposedPorts(8080);

    @Test
    void getRoot() throws Exception {
          URL url = new URL("http://localhost:" + server.getMappedPort(8080) + "/");
          StringBuilder content = new StringBuilder();
          HttpURLConnection connection = (HttpURLConnection) url.openConnection();
          try {
              connection.setRequestMethod("GET");
              try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                  String inputLine;
                  while ((inputLine = in.readLine()) != null) {
                      content.append(inputLine);
                  }
              }
          } finally {
              connection.disconnect();
          }
          assertThat(content.toString(), stringContainsInOrder("Eclipse GlassFish", "index.html", "production-quality"));
      }

}
```
