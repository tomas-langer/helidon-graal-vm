#!/usr/bin/env bash
#
# Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

# run this script from project directory
source ${DIR}/env.sh

# Configuration of reflection, needed for custom classes that should be instantiated or access by reflection
NATIVE_IMAGE_OPTIONS="-H:ReflectionConfigurationFiles=${DIR}/reflection-config.json"
# NATIVE_IMAGE_OPTIONS=""

# Configure all resources that should be available in runtime (except for META-INF/services - those are added
# by Helidon SVM Extension)
INCLUDE_RES="application.yaml"
INCLUDE_RES="${INCLUDE_RES}|logging.properties"
NATIVE_IMAGE_OPTIONS="${NATIVE_IMAGE_OPTIONS} -H:IncludeResources=${INCLUDE_RES}"

# This should be "set in stone" - this is to prevent compilation errors due to incomplete classpath for optional features of
# Netty.
DELAY_INIT='io.netty.buffer.UnpooledByteBufAllocator$InstrumentedUnpooledUnsafeNoCleanerDirectByteBuf'
DELAY_INIT="${DELAY_INIT},io.netty.buffer.UnreleasableByteBuf"
DELAY_INIT="${DELAY_INIT},io.netty.handler.codec.http2.Http2ConnectionHandler"
DELAY_INIT="${DELAY_INIT},io.netty.handler.codec.http2.Http2CodecUtil"
DELAY_INIT="${DELAY_INIT},io.netty.handler.codec.http.HttpObjectEncoder"
DELAY_INIT="${DELAY_INIT},io.netty.handler.codec.http2.CleartextHttp2ServerUpgradeHandler"
DELAY_INIT="${DELAY_INIT},io.netty.handler.codec.http2.DefaultHttp2FrameWriter"
DELAY_INIT="${DELAY_INIT},io.netty.handler.codec.http2.Http2ServerUpgradeCodec"

NATIVE_IMAGE_OPTIONS="${NATIVE_IMAGE_OPTIONS} --delay-class-initialization-to-runtime=${DELAY_INIT}"

# And this is to prevent compilation errors that are caused by some specific Netty classes (io/netty/internal/tcnative/SSL)
NATIVE_IMAGE_OPTIONS="${NATIVE_IMAGE_OPTIONS} --report-unsupported-elements-at-runtime"

NATIVE_IMAGE_OPTIONS="${NATIVE_IMAGE_OPTIONS} --allow-incomplete-classpath"

# Required for tracing
NATIVE_IMAGE_OPTIONS="${NATIVE_IMAGE_OPTIONS} --enable-url-protocols=http"

# Resource bundle for yasson (JSON-B)
RESOURCE_BUNDLES="yasson-messages"
# Resource bundle for Subject.toString()
RESOURCE_BUNDLES="${RESOURCE_BUNDLES},sun.security.util.Resources"

NATIVE_IMAGE_OPTIONS="${NATIVE_IMAGE_OPTIONS}  -H:IncludeResourceBundles=${RESOURCE_BUNDLES}"


echo "GraalVM native image options: ${NATIVE_IMAGE_OPTIONS}"

${GRAALVM_HOME}/bin/native-image -jar target/helidon-examples-graalvm-native-image-full.jar ${NATIVE_IMAGE_OPTIONS}
