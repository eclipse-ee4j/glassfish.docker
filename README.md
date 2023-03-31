# Glassfish Full Profile Docker Image Distribution

## Table of Contents

## Short History of the Official Image

The original official GlassFish image was created in 2014 by the AWS under the Elastic Beanstalk project
and contained Oracle GlassFish distributions version 4.0 and 4.1. However later the official image
was abandoned and marked as deprecated.

Since 2023 the official support is provided by the company OmniFish, which is highly active
in the Eclipse GlassFish project and also Jakarta EE.
In fact the individual developers of the company supported the Eclipse GlassFish long before
they created the OmniFish company.
After the release of Eclipse GlassFish 7.0.0 there was a discussion where several users asked
for the official Eclipse GlassFish Docker Image.

So ... here it is!

## Examples

* Links to examples will be added soon ...

## FAQ

### Where to Get Help?

* GlassFish Mailing List for questions
* GlassFish GitHub for well defined and repeatable issues
* OmniFish Commercial Support for paid active help and other services

### What About GlassFish 6?

Eclipse GlassFish 6 contains many changes, however some of important start/stop issues were fixed
with redesign and refactoring done for Eclipse GlassFish 7. Simply said, 6 is not ready for Docker.

## License

See https://www.eclipse.org/legal/epl-2.0/

## Internal: Updates

* The following instructions will be improved, now it is rather expected way.
* This repository reflects just supported versions.
* Changes are done here first, then you create a PR for docker-images and a PR for docker-images-docs
* Repository contains copy-pastes.
  The reason is that Official Docker Images use just and only Dockerfiles from this repository,
  but before we push them out, we already wanna know that they are correct.

### New Version

1. Run the build for changed images, example:
   ```
   mvn clean verify -Dglassfish.version=7.0.3
   ```
2. Commit and push
3. Visit the `docker-library/official-images` repository and replace the `library/glassfish` file
   with the file from the `target` directory
   * Don't forget to update the `GitCommit` line with the commit id
   * Don't forget to add the `latest` tag to the latest version (as the first one) and remove it
     from the previous version.

### Deprecation

You don't need to change anything here.
Visit the docker-images-docs repository, update the README.md file and create a PR.

### Removal

1. Visit the `docker-library/official-images` and remove the block in `library/glassfish` you want to be removed.
2. Visit the `docker-library/docker-images-docs` repository, update the README.md file and create a PR.
2. Remove the directory here, then commit and push.
