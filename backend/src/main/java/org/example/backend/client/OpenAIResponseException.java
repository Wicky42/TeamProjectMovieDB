package org.example.backend.client;

public class OpenAIResponseException extends RuntimeException{

    public OpenAIResponseException(String message){
        super(message);
    }
}
