/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.examples.graalvm;

import javax.json.Json;
import javax.json.JsonObject;

import io.helidon.common.LogConfig;
import io.helidon.config.Config;
import io.helidon.config.ConfigSources;
import io.helidon.health.HealthSupport;
import io.helidon.health.checks.HealthChecks;
import io.helidon.media.jsonb.JsonbSupport;
import io.helidon.media.jsonp.JsonpSupport;
import io.helidon.metrics.MetricsSupport;
import io.helidon.metrics.api.RegistryFactory;
import io.helidon.security.Security;
import io.helidon.security.integration.webserver.WebSecurity;
import io.helidon.tracing.TracerBuilder;
import io.helidon.webserver.Handler;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.WebServer;

import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.MetricRegistry;

/**
 * Runnable class for GraalVM native image integration example.
 * <p>
 * Steps:
 * <ol>
 * <li>Install GraalVM 21 for Java 11 with native-image</li>
 * <li>Set the {@code GRAALVM_HOME} environment variable to point to your installation</li>
 * <li>Run {@code mvn clean package -Pnative-image} in the example directory</li>
 * </ol>
 */
public final class GraalVMNativeImageMain {
    // private constructor
    private GraalVMNativeImageMain() {
    }

    /**
     * Start this example.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        long timestamp = System.currentTimeMillis();

        LogConfig.configureRuntime();

        Config config = createConfig();

        WebServer server = WebServer.builder()
                .config(config.get("server"))
                .tracer(TracerBuilder.create(config.get("tracing")).registerGlobal(true).build())
                .routing(routing(config))
                .addMediaSupport(JsonpSupport.create())
                .addMediaSupport(JsonbSupport.create())
                .build()
                .start()
                .await();

        long time = System.currentTimeMillis() - timestamp;
        System.out.println("Application started in " + time + " milliseconds");
        System.out.println("Application is available at:");
        System.out.println("http://localhost:" + server.port() + "/");
    }

    private static Config createConfig() {
        return Config.create(
                ConfigSources.file("conf/dev-application.yaml").optional(),
                ConfigSources.classpath("application.yaml")
        );
    }

    private static Routing routing(Config config) {
        /*
         * Config
         */
        String message = config.get("my-app.message")
                .asString()
                .orElse("Default message");

        /*
         * Metrics
         */
        // there is an ordering requirement for metric support in v 1.0.0 - to be fixed in later versions
        MetricsSupport metrics = MetricsSupport.create(config.get("metrics"));
        MetricRegistry registry = RegistryFactory.getInstance().getRegistry(MetricRegistry.Type.APPLICATION);
        Counter counter = registry.counter("counter");

        /*
         * Health
         */
        HealthSupport health = HealthSupport.builder()
                .config(config.get("health"))
                .addLiveness(() -> HealthCheckResponse.builder()
                        .name("test")
                        .up()
                        .withData("time", System.currentTimeMillis())
                        .build())
                .addLiveness(HealthChecks.heapMemoryCheck())
                .build();

        /*
         * Security
         */
        Config securityConfig = config.get("security");
        Security security = Security.create(securityConfig);
        WebSecurity webSecurity = WebSecurity.create(security, securityConfig);

        return Routing.builder()
                // register /metrics endpoint that serves metric information
                .register(metrics)
                // register security restrictions for our routing
                .register(webSecurity)
                // register /health endpoint that serves health checks
                .register(health)
                // simple routing
                .get("/", (req, res) -> res.send(message))
                // secured routing with metric
                .get("/hello", (req, res) -> {
                    res.send("Hello World");
                    counter.inc();
                })
                /*
                 * JSON-P
                 */
                // JSON-P endpoint
                .get("/json", GraalVMNativeImageMain::jsonResponse)
                // JSON-P echo endpoint
                .put("/json", Handler.create(JsonObject.class, GraalVMNativeImageMain::jsonRequest))
                /*
                 * JSON-B
                 */
                // JSON-B endpoint
                .get("/jsonb", GraalVMNativeImageMain::jsonbResponse)
                // JSON-B echo endpoint
                .put("/jsonb", Handler.create(JsonBHello.class, GraalVMNativeImageMain::jsonbRequest))
                // and now build the routing
                .build();
    }

    // JSON-B echo endpoint
    private static void jsonbRequest(ServerRequest request, ServerResponse serverResponse, JsonBHello hello) {
        serverResponse.send(hello);
    }

    // JSON-P echo endpoint
    private static void jsonRequest(ServerRequest request, ServerResponse serverResponse, JsonObject jsonObject) {
        serverResponse.send(jsonObject);
    }

    // JSON-B endpoint
    private static void jsonbResponse(ServerRequest req, ServerResponse res) {
        res.send(new JsonBHello(req.queryParams().first("param").orElse("default")));
    }

    // JSON-P endpoint
    private static void jsonResponse(ServerRequest req, ServerResponse res) {
        String param = req.queryParams().first("param").orElse("default");

        JsonObject theObject = Json.createObjectBuilder()
                .add("key", "value")
                .add("time", System.currentTimeMillis())
                .add("parameter", param)
                .build();

        res.send(theObject);
    }
}
