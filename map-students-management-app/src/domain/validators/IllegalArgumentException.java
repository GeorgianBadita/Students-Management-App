package domain.validators;

public class IllegalArgumentException extends RuntimeException {

    /**
     * Constructor for IllegalArgumentException class
     * @param err - error to output
     */
    public IllegalArgumentException(String err){
        super(err);
    }

    /**
     * Default Constructro for class
     */
    public IllegalArgumentException() {};
}
