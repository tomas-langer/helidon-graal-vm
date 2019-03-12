Helidon Graal Native Image POC
---

# Prepare environment

1. Install Graal VM - download a release from https://github.com/oracle/graal/releases or from https://www.graalvm.org/downloads/
2. Update the `pom.xml` - configure `native.image` property to point to the `native-image` executable
3. Update the `pom.xml` - configure `version.graalvm` property to the version downloaded
    _Helidon has its own integration module for Graal VM `helidon-graal-native-image-extension` 
        make sure the versions of Graal VM are compatible_
        
# Build
1. Run `mvn clean install` to build the application to a java jar
2. Run `mvn exec:exec@build-native-image` to build the native image
    1. See `native.image`, `native.name`, `native.resources` and `main.class` configuration in `pom.xml`
    2. See `exec` plugin configuration execution with id `build-native-image` for details about invoking the `native-image` build
    
   
# Run

You can run both the java and the native image from maven:

- `mvn exec:exec` to run the java library
- `mvn exec:exec@run-native-image` to run the native image

The native image can also be executed from command line (if the `native.name` property is unchanged):
`./target/helidon-example-native-image`

## Endpoints

| Endpoint | Expected Output | Description
| :-------- |---- | --- |
| http://localhost:8099       | Hello from YAML configuration | Default endpoint that reads message from yaml file |
| http://localhost:8099/hello | Hello World | Protected endpoint, requires `admin` role (user `jack`/`jackIsGreat`) |
| http://localhost:8099/json  | `{"key":"value","time":currentTime,"parameter":"default"}` | JSON-P endpoint, requires `user` role (user `jill`/`jillToo`, or `jack`) |
| http://localhost:8099/json?param=v | `{"key":"value","time":currentTime,"parameter":"v"}` | JSON-P endpoint using parameter from requeset
| http://localhost:8099/jsonb | `{"key":"value","parameter":"default","time":currentTime}` | JSON-B endpoint |
| http://localhost:8099/metrics | Prometheus format metrics | Metrics endpoint providing prometheus data, has `/application`, `/base` and `/vendor` subpaths |
| http://localhost:8099/health | `{"outcome":"UP","checks":[{"name":"test","state":"UP","data":{"time":currentTime}}]}` | Health check endpoint providing JSON data in Microprofile format
 

The JSON-P and JSON-B endpoints also support PUT requests that echo the sent data:

JSON-B:
`curl -i -X PUT -d '{"time":49, "key":"aValue", "parameter":"theParam"}' http://localhost:8099/jsonb`

JSON-P:
`curl -i -u jack:jackIsGreat -X PUT -d '{"time":49, "key":"aValue", "parameter":"theParam"}' http://localhost:8099/json`


# Next steps

1. Add support to build the native image in a docker (to produce Linux native image)
2. Add support to build a docker image with the native image built in step 1
