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

# run this script from project directory
source ./etc/graal/env.sh

# Configuration of reflection, needed for custom classes that should be instantiated or access by reflection
#GRAAL_OPTIONS="-H:ReflectionConfigurationResources=/Users/tomas/dev/helidon/helidon/examples/helidon-graal-vm/etc/graal/reflection-config.json"
GRAAL_OPTIONS=""

# Configure all resources that should be available in runtime (except for META-INF/services - those are added
# by Helidon SVM Extension)
INCLUDE_RES="application.yaml"
INCLUDE_RES="${INCLUDE_RES}|logging.properties"
GRAAL_OPTIONS="${GRAAL_OPTIONS} -H:IncludeResources=${INCLUDE_RES}"

# This should be "set in stone" - this is to prevent compilation errors due to incomplete classpath for optional features of
# Netty.
DELAY_INIT="io.netty.handler.codec.http.HttpObjectEncoder"
DELAY_INIT="${DELAY_INIT},io.netty.buffer.Unpooled"
DELAY_INIT="${DELAY_INIT},io.netty.buffer.UnpooledByteBufAllocator"
DELAY_INIT="${DELAY_INIT},io.netty.buffer.AbstractByteBuf"
DELAY_INIT="${DELAY_INIT},io.netty.handler.ssl.SslHandler"
DELAY_INIT="${DELAY_INIT},io.netty.handler.ssl.ReferenceCountedOpenSslEngine"
DELAY_INIT="${DELAY_INIT},io.netty.handler.codec.MessageAggregator"
DELAY_INIT="${DELAY_INIT},io.netty.handler.codec.http.DefaultFullHttpResponse"
DELAY_INIT="${DELAY_INIT},io.netty.handler.codec.http.HttpObjectDecoder"
DELAY_INIT="${DELAY_INIT},io.netty.handler.codec.http2.DefaultHttp2FrameWriter"
DELAY_INIT="${DELAY_INIT},io.netty.handler.codec.http2.CleartextHttp2ServerUpgradeHandler"
DELAY_INIT="${DELAY_INIT},io.netty.handler.codec.http2.DefaultHttp2HeadersEncoder"
DELAY_INIT="${DELAY_INIT},io.netty.handler.codec.http2.Http2CodecUtil"
DELAY_INIT="${DELAY_INIT},io.netty.handler.codec.http2.Http2ConnectionHandler"

GRAAL_OPTIONS="${GRAAL_OPTIONS} --delay-class-initialization-to-runtime=${DELAY_INIT}"

# And this is to prevent compilation errors that are caused by some specific Netty classes (io/netty/internal/tcnative/SSL)
GRAAL_OPTIONS="${GRAAL_OPTIONS} --report-unsupported-elements-at-runtime"

GRAAL_OPTIONS="${GRAAL_OPTIONS} --allow-incomplete-classpath"

echo "Graal options: ${GRAAL_OPTIONS}"

${GRAAL_HOME}/bin/native-image -jar target/helidon-examples-graal-full.jar ${GRAAL_OPTIONS}

