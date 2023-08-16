# Eclipse GlassFish Docker images (by OmniFish)

**Source code repository:** https://github.com/OmniFish-EE/docker-library-glassfish

## Quick start

### Start GlassFish

Run GlassFish with the following command:

```
docker run -p 8080:8080 -p 4848:4848 omnifish/glassfish
```

Or with a command for a specific tag (GlassFish version):

```
docker run -p 8080:8080 -p 4848:4848 omnifish/glassfish:@glassfish.version@
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

## Run an application with GlassFish in Docker

You can run an application located in your filesystem with GlassFIsh in a Docker container.

Follow these steps:

1. Create an empty directory on your filesystem, e.g. `/deployment`
2. Copy the application package to this directory - so that it's for example on the path `/deployment/application.war`
3. Run the following command to start GlassFish in Docker with your application, where `/deployments` is path to the directory created in step 1:

```
docker run -p 8080:8080 -p 4848:4848 -v /deployments:/opt/glassfish7/glassfish/domains/domain1/autodeploy omnifish/glassfish:latest
```

Then you can open the application in the browser with:

* http://localhost:9080/application

The context root (`application`) is derived from the name of the application file (e.g. `application.war` would deployed under the `application` context root). If your application file has a different name, please adjust the contest root in the URL accordingly.

---