FROM openjdk:11
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /rabobank-assignment-api/lib
COPY ${DEPENDENCY}/META-INF /rabobank-assignment-api/META-INF
COPY target/classes /rabobank-assignment-api
ENTRYPOINT ["java","-cp","rabobank-assignment-api:rabobank-assignment-api/lib/*","nl.rabobank.RaboAssignmentApplication"]