/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.example.server;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.apache.camel.example.server.model.IncrementRequest;
import org.apache.camel.example.server.model.IncrementRequestLegacy;
import org.apache.camel.example.server.model.IncrementResponse;
import org.apache.camel.example.server.model.IncrementResponseLegacy;

public class IncrementRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        JaxbDataFormat jaxb = new JaxbDataFormat("org.apache.camel.example.server.model");
        from("spring-ws:rootqname:{http://camel.apache.org/example/increment}incrementRequestLegacy?endpointMapping=#endpointMapping")
                .unmarshal(jaxb)
                .process(new IncrementProcessor())
                .to("spring-ws:http://localhost:8080/increment")
                .unmarshal(jaxb)
                .process(new IncrementProcessorResponse())
                .marshal(jaxb);
    }

    private static final class IncrementProcessor implements Processor {
        public void process(Exchange exchange) throws Exception {
            IncrementRequestLegacy request = exchange.getIn().getBody(IncrementRequestLegacy.class);
            IncrementRequest incrementRequest = new IncrementRequest();
            incrementRequest.setInpu(request.getInputLegacy());
            exchange.getIn().setBody(incrementRequest);
            System.out.println(exchange.getIn().getBody());
        }
    }

    private static final class IncrementProcessorResponse implements Processor {
        public void process(Exchange exchange) throws Exception {
            IncrementResponse incrementResponse = exchange.getIn().getBody(IncrementResponse.class);
            IncrementResponseLegacy incrementResponseLegacy = new IncrementResponseLegacy();
            incrementResponseLegacy.setResultLegacy(incrementResponse.getResult());
            exchange.getOut().setBody(incrementResponseLegacy);
            System.out.println(exchange.getIn().getBody());
        }
    }

}