Helidon Graal Native Image POC
---

# Prepare environment

General prerequisites:

1. Maven
2. Java 11
3. Docker

To build the local native image:

1. Install Graal VM - download a release 21.x from https://www.graalvm.org/downloads/

# Build the application

## Java jar

Run `mvn clean package` to build the application to a java jar.

Run `mvn exec:java` to run the jar built in previous step

## Local native image

Run `mvn package -Pnative-image` to build the native image

Run `./target/helidon-graalvm-native-image` to run the native image

## Linux native image in Docker

Run `docker build -t helidon-graalvm-native-image -f Dockerfile.native .` to build a Linux docker image.

Run `docker run --name helidon-native -p 8099:8099 helidon-graalvm-native-image:latest` to run the Docker container based on the
image

See `Dockerfile.native` in project root

# Endpoints

| Endpoint | Expected Output                                                                         | Description
| :-------- |-----------------------------------------------------------------------------------------| --- |
| http://localhost:8099       | Hello from YAML configuration                                                           | Default endpoint that reads message from yaml file |
| http://localhost:8099/hello | Hello World                                                                             | Protected endpoint, requires `admin` role (user `jack`/`jackIsGreat`) |
| http://localhost:8099/json  | `{"key":"value","time":currentTime,"parameter":"default"}`                              | JSON-P endpoint, requires `user` role (user `jill`/`jillToo`, or `jack`) |
| http://localhost:8099/json?param=v | `{"key":"value","time":currentTime,"parameter":"v"}`                                    | JSON-P endpoint using parameter from requeset
| http://localhost:8099/jsonb | `{"key":"value","parameter":"default","time":currentTime}`                              | JSON-B endpoint |
| http://localhost:8099/metrics | Prometheus format metrics                                                               | Metrics endpoint providing prometheus data, has `/application`, `/base` and `/vendor` subpaths |
| http://localhost:8099/health | `{"outcome":"UP","checks":[{"name":"test","status":"UP","data":{"time":currentTime}}]}` | Health check endpoint providing JSON data in Microprofile format

The JSON-P and JSON-B endpoints also support PUT requests that echo the sent data:

JSON-B:
`curl -i -X PUT -d '{"time":49, "key":"aValue", "parameter":"theParam"}' http://localhost:8099/jsonb`

JSON-P:
`curl -i -u jack:jackIsGreat -X PUT -d '{"time":49, "key":"aValue", "parameter":"theParam"}' http://localhost:8099/json`
