# Start with a base image containing Java runtime
FROM jboss/keycloak:11.0.0

# Add Oracle JDBC
ADD --chown=1000:0 https://repo1.maven.org/maven2/com/oracle/database/jdbc/ojdbc10/19.3.0.0/ojdbc10-19.3.0.0.jar /opt/jboss/keycloak/modules/system/layers/base/com/oracle/jdbc/main/driver/ojdbc.jar

# Allow SID conection
RUN sed -e "s/:\${env.DB_DATABASE:XE}/\${env.DB_DATABASE:XE}/g" -i /opt/jboss/tools/cli/databases/oracle/change-database.cli

# Add the application's jar to the container
COPY broker-saml-mappers/target/broker-saml-mappers*.jar /opt/jboss/keycloak/standalone/deployments/

# Add the default realm configuration to the container
COPY realm /realm