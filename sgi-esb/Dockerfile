# Start with a base image containing Java runtime
FROM wso2/wso2mi:1.2.0

# Make ports 8290, 8253, 9164 available to the world outside this container
EXPOSE 9164

# Add the application's jar to the container
COPY --chown=wso2carbon:wso2 sgi-esb-common/sgi-auth-mediator/target/*.jar  ${WSO2_SERVER_HOME}/lib/

# Add the application's car to the container
COPY --chown=wso2carbon:wso2 sgi-esb-common/esb-composite-application/target/*.car ${WORKING_DIRECTORY}/base-capp/

# Add all application's car to the container
COPY --chown=wso2carbon:wso2 sgi-esb-sgdoc/sgdoc-composite-application/target/*.car ${WORKING_DIRECTORY}/base-capp/
COPY --chown=wso2carbon:wso2 sgi-esb-sge/sge-composite-application/target/*.car ${WORKING_DIRECTORY}/base-capp/
COPY --chown=wso2carbon:wso2 sgi-esb-sgemp/sgemp-composite-application/target/*.car ${WORKING_DIRECTORY}/base-capp/
COPY --chown=wso2carbon:wso2 sgi-esb-sgepii/sgepii-composite-application/target/*.car ${WORKING_DIRECTORY}/base-capp/
COPY --chown=wso2carbon:wso2 sgi-esb-sgi/sgi-composite-application/target/*.car ${WORKING_DIRECTORY}/base-capp/
COPY --chown=wso2carbon:wso2 sgi-esb-sgo/sgo-composite-application/target/*.car ${WORKING_DIRECTORY}/base-capp/
COPY --chown=wso2carbon:wso2 sgi-esb-sgp/sgp-composite-application/target/*.car ${WORKING_DIRECTORY}/base-capp/

# Copy deployment.toml with hot deployment - true
COPY --chown=wso2carbon:wso2 docker/deployment.toml ${WORKING_DIRECTORY}/wso2mi-1.2.0/conf/

# Add script to copy esb-composite before launch server
COPY --chown=wso2carbon:wso2 docker/sgi-esb-entrypoint.sh ${WORKING_DIRECTORY}

RUN chmod +x ${WORKING_DIRECTORY}/sgi-esb-entrypoint.sh

RUN chmod 777 ${WSO2_SERVER_HOME}/repository/deployment/server/carbonapps/

# Generate nginx.conf using environment variables
ENTRYPOINT ${WORKING_DIRECTORY}/sgi-esb-entrypoint.sh