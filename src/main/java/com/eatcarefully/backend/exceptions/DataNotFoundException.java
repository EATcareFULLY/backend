package com.eatcarefully.backend.exceptions;

public class DataNotFoundException extends RuntimeException{

    public DataNotFoundException(String msg){
        super(msg);
    }
}
