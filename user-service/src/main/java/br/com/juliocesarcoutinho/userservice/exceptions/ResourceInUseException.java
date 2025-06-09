package br.com.juliocesarcoutinho.userservice.exceptions;

public class ResourceInUseException extends RuntimeException {
    public ResourceInUseException(String message) {
        super(message);
    }
}