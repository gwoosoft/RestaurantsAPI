package org.example.model;

import java.util.Map;

public class ProxyEvent {

    public enum Method {
        GET, POST
    }
    public ProxyEvent(Map<String, Object> event) {
        this.httpMethod=event.get("httpMethod");
        this.resource=event.get("resource");
    }

    public Object getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(Object httpMethod) {
        this.httpMethod = httpMethod;
    }

    public Object getResource() {
        return resource;
    }

    public void setResource(Object resource) {
        this.resource = resource;
    }

    Object httpMethod;
    Object resource;
}
