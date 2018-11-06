package com.wdq.rpc.serializable;

import java.io.Serializable;

/**
 * 请求参数的封装
 */
public class RpcRequest implements Serializable {

    private String requestId;
    private String className;
    private String methodName;
    private Object[] paramValue;
    private Class[] paramType;

    public Object[] getParamValue() {
        return paramValue;
    }

    public void setParamValue(Object[] paramValue) {
        this.paramValue = paramValue;
    }

    public Class[] getParamType() {
        return paramType;
    }

    public void setParamType(Class[] paramType) {
        this.paramType = paramType;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

}
