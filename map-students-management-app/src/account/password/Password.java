package account.password;

/**
 * Password class
 */
public class Password {
    private String passValue;
    private String saltValue;

    /**
     * Constructor for Password class
     * @param passVal - password value
     * @param saltVal - salt value
     */
    public Password(String passVal, String saltVal){
        this.passValue = passVal;
        this.saltValue = saltVal;
    }

    /**
     * Getter for password value
     * @return - password value as string
     */
    public String getPassValue() {
        return passValue;
    }

    /**
     * Getter for salt value
     * @return - saltVlue of the password
     */
    public String getSaltValue() {
        return saltValue;
    }
}
