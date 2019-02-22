Reproducer
---

# Prepare environment

1. Update `native-image-example/etc/graalvm/env.sh`
    1. Make sure `GRAALVM_HOME` points to a valid installation

# Build

1. Run `mvn clean install` from project root
2. Go to `native-image-example` directory
    1. Invoke `./etc/graalvm/native-image-compile.sh` (must be done from this directory due to script configuration)
    2. Now this fails with _Error: Class that is marked for delaying initialization to run time got initialized during image building: io.netty.buffer.UnpooledUnsafeDirectByteBuf_

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

Also the endpoint `http://localhost:8099` should produce:
`Hello from YAML configuration`
