- Install jdk and -> `java --version` to check if the installation is working or not.
- Install docker desktop and -> `docker --version` to check.

# How to use OpenAPI and Swagger for documentation

- https://springdoc.org/
- add as dependency

```xml
  <dependency>
      <groupId>org.springdoc</groupId>
      <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
   </dependency>
```

- Not working, somehow...
- If working, go to /v3/api-docs and then paste the output to Swagger UI Editor

# Not Working Stuff

- H2 Database Console 
- OpenAPI for Spring

# Solved By :
- Spring Boot Version 3
- True on env instead of true

# Preparation For Docker

- Name -> patient-service-db
- Container Name -> patient-service-db
- Pulling -> postgres:latest
- Environment Variables ->
  - POSTGRES_USER = 
  - POSTGRES_PASSWORD = 
  - POSTGRES_DB = db
- Bind ports -> Port: 5000 , Container Port 5432
- Bind mounts -> Host Path: /db_volumes/patient-service-db , Container Path: /var/lib/postgresql/data
- Run Options -> --network internal
- Add network for `internal`


# Docker commands for patient-service
- `docker build -t patient-service:latest .`
- `docker network connect internal patient-service-db`
- `docker network inspect internal`
- `docker run -d -p 4000:4000 --network internal  --env-file .env --name patient-container patient-service:latest`

# GRPC 
- GRPC is faster for internal service to service communication where high performance and low latency are required
- In enterprise setting there are more robust way to store the proto file since we need the same content for every gRPC client and server.
- `./mvnw.cmd compile` for generating protobuf

# Docker commands for grpc
- `docker build -t billing-service:latest .`
- `docker run -d -p 4001:4001 -p 9001:9001 --network internal --name billing-container billing-service:latest`

# Kafka
- gRPC and REST are synchronous - good for 1-1 microservice communication
- But what about 1-to-many microservice communication?
- gRPC: 1-1 microservices communication, when you need an immediate response (i.e. synchronous)
- Kafka: 1-to-many microservice communicate, do not need an immediate response (i.e. asynchronous)
- `docker run -d -p 9092:9092 -p 9094:9094 --network internal --env-file .env  --name kafka apache/kafka:latest`
- kexec -> `function kexec { docker exec -it kafka /opt/kafka/bin/$($args[0]) @($args[1..($args.Length-1)]) }`
- List Topics -> `kexec kafka-topics.sh --bootstrap-server localhost:9094 --list`
- Describe Topic -> `kexec kafka-topics.sh --bootstrap-server localhost:9094 --describe --topic patient`
-> Consume Topic -> `kexec kafka-console-consumer.sh --bootstrap-server localhost:9094 --topic patient --from-beginning`
-> Produce Topic -> `kexec kafka-console-producer.sh --bootstrap-server localhost:9094 --topic patient`