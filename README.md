# Eclipse Glassfish Full Profile Docker Image

## Frequently Asked Questions

### How to Use Eclipse GlassFish Docker Image?

* Try [Examples](https://github.com/eclipse-ee4j/glassfish.docker/wiki/Examples) on our Wiki.
* Read [Eclipse GlassFish Documentation](https://glassfish.org/documentation.html)
* Browse Eclipse GlassFish own tests
  * They don't use the docker image, however these tests use similar principles.
  * These tests use `glassfish-itest-tools` instead of docker.
  * [Eclipse GlassFish Application Tests](https://github.com/eclipse-ee4j/glassfish/tree/master/appserver/tests/application/src/test/java/org/glassfish/main/test/app) are covering application compatibility with GlassFish.
  * [Eclipse GlassFish Admin Tests](https://github.com/eclipse-ee4j/glassfish/tree/master/appserver/tests/admin/tests/src/test/java/org/glassfish/main/admin/test) are covering usage of `asadmin` and the REST Admin Interface.
* Browse Jakarta EE resources
  * [Jakarta EE Examples](https://github.com/eclipse-ee4j/jakartaee-examples)

### Where to Get Help?

* [GlassFish Mailing List](https://accounts.eclipse.org/mailing-list/glassfish-dev) for questions
* [GlassFish GitHub](https://github.com/eclipse-ee4j/glassfish) for well defined and repeatable issues
* [OmniFish Commercial Support](https://omnifish.ee/solutions/#support) for paid active help and other services

### What About GlassFish 6?

Eclipse GlassFish 6 contains many changes, however some of important start/stop issues were fixed
with redesign and refactoring done for Eclipse GlassFish 7. Simply said, 6 is not ready for Docker.

### Why Folder Per Version?

Repository contains copy-pastes. The reason is that Docker Hub Container Image Library uses just and only Dockerfiles from this repository, but before we push them out, we already have to know that they are correct and we have to commit them. DockerHub then links its distribution image to the commit id in this repository.

## License

See https://www.eclipse.org/legal/epl-2.0/
