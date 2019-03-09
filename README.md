Helidon Graal Native Image POC
---

# Prepare environment

1. Update `native-image-example/etc/graalvm/env.sh`
    1. Make sure `GRAALVM_HOME` points to a valid installation

# Build

1. Run `mvn clean install` from project root
2. Go to `native-image-example` directory
    1. Invoke `./etc/graalvm/native-image-compile.sh`

# Modify

If the extension is modified, go through all steps in Build chapter.
If the example or script is modified, go to the `native-image-example` directory and invoke

```bash
mvn clean package
./etc/graalvm/native-image-compile.sh
```

# Run

From the `native-image-example` directory.

Once all steps are successful, you can run the image:
`./helidon-examples-graalvm-native-image-full`

Compare output with the java version:
`java -jar ./target/helidon-examples-graalvm-native-image-full.jar`


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