# Glassfish Full Profile Docker Image Distribution

## Table of Contents

## Short History of the Official Image

The original official GlassFish image was created in 2014 by the AWS under the Elastic Beanstalk project and contained Oracle GlassFish distributions version 4.0 and 4.1. However later the official image was abandoned and marked as deprecated.

Since 2023 the official support is provided by the company OmniFish, which is highly active in the Eclipse GlassFish project and also Jakarta EE.
In fact the individual developers of the company supported the Eclipse GlassFish long before they created the OmniFish company. After the release of Eclipse GlassFish 7.0.0 there was a discussion where several users asked for the official Eclipse GlassFish Docker Image.

So ... here it is!

## Examples

* Links to examples will be added soon ...

## FAQ

### Where to Get Help?

* GlassFish Mailing List for questions
* GlassFish GitHub for well defined and repeatable issues
* OmniFish Commercial Support for paid active help and other services

### What About GlassFish 6?

Eclipse GlassFish 6 contains many changes, however some of important start/stop issues were fixed with redesign and refactoring done for Eclipse GlassFish 7. Simply said, 6 is not ready for Docker.

## License

See https://www.eclipse.org/legal/epl-2.0/

## Internal: Updates

* The following instructions will be improved, now it is rather expected way.
* This repository reflects just supported versions.
* Changes are done here first, then you create a PR for docker-images and a PR for docker-images-docs
* Repository contains copy-pastes. The reason is that Official Docker Images use just and only Dockerfiles from this repository, but before we push them out, we already wanna know that they are correct.

### New Version

1. Add the directory named after the version
2. Add <module> element to the pom.xml. It's value is the name of the directory.
3. Create pom.xml in the directory. Use older version as an example, don't forget to change the artifactId.
4. Create a Dockerfile. Use the latest existing version as an example.
5. Update GLASSFISH_VERSION in Dockerfile
6. Update GLASSFISH_DOWNLOAD_SHA1 in Dockerfile - download the zip file and compute the hash locally:
   ```
   sha512sum glassfish-7.0.1.zip
   ```
7. Run the build for changed images: mvn clean verify -pl :<artifactId>,:tests -Dglassfish.version

### Deprecation

You don't need to change anything here.
Visit the docker-images-docs repository, update the README.md file and create a PR.

### Removal

1. Visit the docker-images-docs repository, update the README.md file and create a PR.
2. Remove the <module> from the root pom.xml and the directory, then commit and push.
