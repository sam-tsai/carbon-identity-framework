/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.gateway.api.request;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.identity.gateway.api.exception.GatewayRuntimeException;
import org.wso2.carbon.identity.gateway.common.util.Constants;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class GatewayRequest implements Serializable {

    private static Logger log = LoggerFactory.getLogger(GatewayRequest.class);
    private static final long serialVersionUID = 5418537216546873566L;

    protected Map<String, Serializable> headers = new HashMap();
    protected Map<String, Serializable> parameters = new HashMap();
    protected Map<String, Serializable> attributes = new HashMap();
    protected String httpMethod;
    protected String requestURI;
    protected String contentType;
    protected String queryString;

    protected GatewayRequest(GatewayRequestBuilder builder) {
        this.headers = builder.headers;
        this.parameters = builder.parameters;
        this.attributes = builder.attributes;
        this.httpMethod = builder.httpMethod;
        this.requestURI = builder.requestURI;
        this.contentType = builder.contentType;
        this.queryString = builder.queryString;
    }

    public Serializable getAttribute(String attributeName) {
        return parameters.get(attributeName);
    }

    public Map<String, Serializable> getAttributeMap() {
        return Collections.unmodifiableMap(attributes);
    }

    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(attributes.keySet());
    }

    public String getBodyParameter(String paramName) {
        Map<String, String> queryParams = (Map<String, String>) parameters.get(Constants.BODY_PARAMETERS);
        return queryParams.get(paramName);
    }

    public String getContentType() {
        return contentType;
    }

    public String getHeader(String name) {
        return (String) headers.get(name);
    }

    public Map<String, String> getHeaderMap() {
        return Collections.unmodifiableMap((Map) headers);
    }

    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(headers.keySet());
    }

    public Enumeration<String> getHeaders(String name) {
        String headerValue = (String) headers.get(name);
        String[] multiValuedHeader = headerValue.split(",");
        return Collections.enumeration(Arrays.asList(multiValuedHeader));
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getParameter(String paramName)  {

        String decode = null;
        Map<String, String> queryParams = (Map<String, String>) parameters.get(Constants.QUERY_PARAMETERS);
        Map<String, String> bodyParams = (Map<String, String>) parameters.get(Constants.BODY_PARAMETERS);

        if (bodyParams.get(paramName) != null) {
            return bodyParams.get(paramName);
        } else {
            if (StringUtils.isNotBlank(queryParams.get(paramName))) {
                try {
                    decode = URLDecoder.decode(queryParams.get(paramName), StandardCharsets.UTF_8.name());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return decode;
    }

    public Map<String, Serializable> getParameterMap() {
        return Collections.unmodifiableMap(parameters);
    }

    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(parameters.keySet());
    }

    public String getQueryParameter(String paramName) throws UnsupportedEncodingException {
        Map<String, String> queryParams = (Map<String, String>) parameters.get(Constants.QUERY_PARAMETERS);
        URLDecoder.decode(queryParams.get(paramName), StandardCharsets.UTF_8.name());

        return queryParams.get(paramName);
    }

    public String getQueryString() {
        return queryString;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public static class GatewayRequestBuilder {

        private Map<String, Serializable> headers = new HashMap();
        private Map<String, Serializable> parameters = new HashMap();
        private Map<String, Serializable> attributes = new HashMap();
        private String httpMethod;
        private String requestURI;
        private String contentType;
        private String queryString;

        public GatewayRequestBuilder() {
        }

        public GatewayRequestBuilder addAttribute(String name, Serializable value) {
            if (this.attributes.containsKey(name)) {
                throw new GatewayRuntimeException("Attributes map trying to override existing key " + name);
            }
            this.attributes.put(name, value);
            return this;
        }

        public GatewayRequestBuilder addAttributes(Map<String, Serializable> attributes) {
            for (Map.Entry<String, Serializable> attribute : attributes.entrySet()) {
                if (this.attributes.containsKey(attribute.getKey())) {
                    throw new GatewayRuntimeException("Attributes map trying to override existing key " + attribute
                            .getKey());
                }
                this.attributes.put(attribute.getKey(), attribute.getValue());
            }
            return this;
        }

        public GatewayRequestBuilder addHeader(String name, String value) {
            if (this.headers.containsKey(name)) {
                throw new GatewayRuntimeException("Headers map trying to override existing header " + name);
            }
            this.headers.put(name, value);
            return this;
        }

        public GatewayRequestBuilder addHeaders(Map<String, String> headers) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                if (this.headers.containsKey(header.getKey())) {
                    throw new GatewayRuntimeException("Headers map trying to override existing header " + header
                            .getKey());
                }
                this.headers.put(header.getKey(), header.getValue());
            }
            return this;
        }

        public GatewayRequestBuilder addParameter(String name, Serializable value) {
            if (this.parameters.containsKey(name)) {
                throw new GatewayRuntimeException("Parameters map trying to override existing key " + name);
            }
            this.parameters.put(name, value);
            return this;
        }

        public GatewayRequestBuilder addParameters(Map<String, Serializable> parameters) {
            for (Map.Entry<String, Serializable> parameter : parameters.entrySet()) {
                if (this.parameters.containsKey(parameter.getKey())) {
                    throw new GatewayRuntimeException("Parameters map trying to override existing key " + parameter
                            .getKey());
                }
                this.parameters.put(parameter.getKey(), parameter.getValue());
            }
            return this;
        }

        public GatewayRequest build(){
            return new GatewayRequest(this);
        }

        public GatewayRequestBuilder setAttributes(Map<String, Serializable> attributes) {

            for (Map.Entry<String, Serializable> entry : attributes.entrySet()) {
                if (entry instanceof Serializable) {
                    this.attributes.put(entry.getKey(), entry.getValue());
                }
            }
            return this;
        }

        public GatewayRequestBuilder setContentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public GatewayRequestBuilder setHeaders(Map<String, Serializable> responseHeaders) {
            this.headers = responseHeaders;
            return this;
        }

        public GatewayRequestBuilder setHttpMethod(String httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public GatewayRequestBuilder setParameters(Map<String, Serializable> parameters) {
            this.parameters = parameters;
            return this;
        }

        public GatewayRequestBuilder setQueryString(String queryString) {
            this.queryString = queryString;
            return this;
        }

        public GatewayRequestBuilder setRequestURI(String requestURI) {
            this.requestURI = requestURI;
            return this;
        }
    }

    public static class IdentityRequestConstants {

    }
}
