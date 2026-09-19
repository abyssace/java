public class RpgException extends Exception {

    public RpgException(String message){
        super(message);
    }

    public RpgException(String message, Throwable causa){
        super(message, causa);
    }
    
}
