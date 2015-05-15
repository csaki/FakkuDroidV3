package com.devsaki.fakkudroid.exceptions;

/**
 * Created by DevSaki on 15/05/2015.
 */
public class HttpClientException extends Exception{

    private String result;
    private int code;

    public HttpClientException(String result, int code) {
        super();
        this.result = result;
        this.code = code;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
