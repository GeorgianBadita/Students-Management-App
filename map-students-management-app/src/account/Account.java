package account;

import account.password.Password;
import domain.entities.HasID;

import java.util.Objects;

/**
 * Account class
 */
public class Account implements HasID<String> {
    private String username;
    private AccountType accType;
    private Password password;

    /**
     * Constructor for Account class
     * @param userName - name of the user
     * @param accType - account Type
     * @param pass - password
     */
    public Account(String userName, AccountType accType, Password pass){
        this.username = userName;
        this.accType = accType;
        this.password = pass;
    }

    /**
     * Getter for password field
     * @return - string form of the password
     */
    public Password getPassword() {
        return password;
    }

    /**
     * Getter for username
     * @return - username of the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setter for username
     * @param username - new username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * getter for user's account type
     * @return - user's account type
     */
    public AccountType getAccType() {
        return accType;
    }

    /**
     * Setter for users's account type
     * @param accType - new user's account type
     */
    public void setAccType(AccountType accType) {
        this.accType = accType;
    }

    /**
     * Overriding equals method
     * @param o - object to be verified
     * @return - true if the objects are equals, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;
        Account account = (Account) o;
        return getUsername().equals(account.getUsername()) &&
                getAccType() == account.getAccType();
    }

    /**
     * Overriding hashCode function
     * @return - hashCOde of the object
     */
    @Override
    public int hashCode() {
        return Objects.hash(getUsername(), getAccType());
    }


    @Override
    public String getId() {
        return username;
    }

    @Override
    public void setId(String s) {
        username = s;
    }
}
