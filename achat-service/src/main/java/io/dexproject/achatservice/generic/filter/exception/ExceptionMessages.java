package io.dexproject.achatservice.generic.filter.exception;

public class ExceptionMessages {

    public static String VALUE_IS_NOT_LOCAL_DATE = "La valeur fournie n'est pas du type Date, elle doit être conforme à : AAAA-MM-JJ";
    public static String VALUE_IS_NOT_LOCAL_DATE_TIME = "La valeur fournie n'est pas du type LocalDateTime, elle doit être conforme à : AAAA-MM-JJ T hh:mm:ss";
    public static String VALUE_IS_NOT_DOUBLE = "La valeur fournie n'est pas de type Double";

    private ExceptionMessages() {
        // not initializable
    }
}
