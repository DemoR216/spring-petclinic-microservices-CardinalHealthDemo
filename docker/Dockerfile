FROM eclipse-temurin:21 AS builder
WORKDIR application
ARG ARTIFACT_NAME
COPY ${ARTIFACT_NAME}.jar application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM eclipse-temurin:21-jre
WORKDIR application

ARG EXPOSED_PORT
EXPOSE ${EXPOSED_PORT}

ENV SPRING_PROFILES_ACTIVE=docker

LABEL org.opencontainers.image.source="https://github.com/DemoR216/spring-petclinic-microservices-CardinalHealthDemo"
LABEL org.opencontainers.image.description="Spring PetClinic Microservice"

COPY --from=builder application/dependencies/ ./
RUN true
COPY --from=builder application/spring-boot-loader/ ./
RUN true
COPY --from=builder application/snapshot-dependencies/ ./
RUN true
COPY --from=builder application/application/ ./

HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:${EXPOSED_PORT}/actuator/health || exit 1

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
