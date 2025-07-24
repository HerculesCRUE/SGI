#The version
ARG VERSION

# Start with a base image containing Java runtime
FROM dockerregistry01.corp.treelogic.com/sgi/wso2-mi:${VERSION}

# Make ports 8290, 8253, 9164 available to the world outside this container
EXPOSE 9164

# Add all application's car to the container
COPY --chown=wso2carbon:wso2 ci/*.car ${WORKING_DIRECTORY}/base-capp/
