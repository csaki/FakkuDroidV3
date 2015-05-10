package com.devsaki.fakku.exception;

/**
 * Created by DevSaki on 09/05/2015.
 */
public class FakkuException extends Exception{

    private Exception cause;

    public FakkuException(){
        super();
    }

    public FakkuException(String message){
        super(message);
    }

    public FakkuException(Exception cause){
        super(cause.getMessage());
        this.cause = cause;
    }

    @Override
    public Exception getCause() {
        return cause;
    }
}
