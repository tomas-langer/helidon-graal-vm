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
package io.helidon.svm;

import java.util.Map;

import com.oracle.svm.core.annotate.AutomaticFeature;
import com.oracle.svm.hosted.FeatureImpl;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.graalvm.nativeimage.Feature;
import org.graalvm.nativeimage.RuntimeReflection;

/**
 * Register Helidon reflection.
 */
@AutomaticFeature
public class HelidonSvmFeature implements Feature {
    @Override
    public void beforeAnalysis(BeforeAnalysisAccess access) {
        /*
        FeatureImpl.BeforeAnalysisAccessImpl impl = (FeatureImpl.BeforeAnalysisAccessImpl) access;
        List<Class<?>> allClasses = impl.findSubclasses(Object.class);
        for (Class<?> clazz : allClasses) {
            //TODO here we can add JAX-RS resources for reflection
            // if annotated by known (Authenticated, RolesAllowed etc.)
            clazz.getDeclaredAnnotations();
        }
        */

        // logging
        // this should work eventually out of the box
        // need to initialize Logging in static class initializer
//        registerInstantiation(ConsoleHandler.class);
//        RuntimeReflection.register(ConsoleHandler.class.getDeclaredMethods());
//        RuntimeReflection.register(ConsoleHandler.class.getMethods());
//        RuntimeReflection.register(ConsoleHandler.class.getDeclaredConstructors());
//        RuntimeReflection.register(ConsoleHandler.class.getConstructors());
//        registerInstantiation(SimpleFormatter.class);
//        RuntimeReflection.register(SimpleFormatter.class.getDeclaredMethods());
//        RuntimeReflection.register(SimpleFormatter.class.getMethods());
//        RuntimeReflection.register(SimpleFormatter.class.getDeclaredConstructors());
//        RuntimeReflection.register(SimpleFormatter.class.getConstructors());

        // needed for YAML parsing
        RuntimeReflection.register(Map.class);
        RuntimeReflection.register(Map.class.getDeclaredMethods());
        RuntimeReflection.register(Map.class.getMethods());

        // web server - Netty
        registerInstantiation(NioServerSocketChannel.class);
    }

    private static void registerInstantiation(Class<?> aClass) {
        // can do Class.forName()
        RuntimeReflection.register(aClass);
        // can do clazz.newInstance()
        RuntimeReflection.registerForReflectiveInstantiation(aClass);
    }
}
