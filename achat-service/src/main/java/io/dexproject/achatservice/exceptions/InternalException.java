package io.dexproject.achatservice.exceptions;

public class InternalException extends InternalError {

    public InternalException(String message){
        super("Erreur interne : "+message+". Veillez reessayer plustard.");
    }
}