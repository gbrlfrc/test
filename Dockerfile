FROM openjdk:11-jdk as builder
COPY . /source/
WORKDIR /source
RUN chmod +x gradlew
RUN ./gradlew clean build --info -x test


FROM jboss/keycloak:11.0.2
COPY --from=builder /source/build/libs/keycloak-password-check-spi.jar /opt/jboss/keycloak/standalone/deployments/
