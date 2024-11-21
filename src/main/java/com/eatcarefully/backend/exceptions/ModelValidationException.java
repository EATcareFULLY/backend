package com.eatcarefully.backend.exceptions;

public class ModelValidationException extends RuntimeException{

    public ModelValidationException(String msg){
        super(msg);
    }

}
