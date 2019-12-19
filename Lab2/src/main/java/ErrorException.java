public class ErrorException extends Exception{

    int id;
    String message;



    public ErrorException(String errorMessage, int id)
    {
        super(errorMessage);
        this.id = id;
        this.message= errorMessage;
    }


    public String toString()
    {
        return "[ERROR "+id+" ]: "+message;
    }
}
