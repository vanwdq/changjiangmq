package com.wdq.rpc.serializable;

import java.io.Serializable;

/**
 * 响应参数的封装
 */
public class RpcResponse implements Serializable {

    private String responseId;
    private Object value;
    private int status;


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}
