FROM eclipse-temurin:11 AS builder
WORKDIR /application
ARG ARTIFACT_NAME
COPY ${ARTIFACT_NAME}.jar application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM eclipse-temurin:11

RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

WORKDIR /application

ARG EXPOSED_PORT
EXPOSE ${EXPOSED_PORT}

ENV SPRING_PROFILES_ACTIVE=docker

COPY --from=builder /application/dependencies/ ./
RUN true
COPY --from=builder /application/spring-boot-loader/ ./
RUN true
COPY --from=builder /application/snapshot-dependencies/ ./
RUN true
COPY --from=builder /application/application/ ./

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:${EXPOSED_PORT}/actuator/health || exit 1

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
