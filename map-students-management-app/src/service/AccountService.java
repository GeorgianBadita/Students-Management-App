package service;

import account.Account;
import account.AccountXMLRepository;
import account.password.PasswordUtils;
import domain.validators.ValidatorException;

public class AccountService {
    private AccountXMLRepository manager;

    /**
     * Constructor for AccountService
     * @param acManager - AccountRepository object
     */
    public AccountService(AccountXMLRepository acManager){
        this.manager = acManager;
    }

    /**
     * Function which checks if an account exists
     * @param userName - userName of the account to find
     * @return - true if the object exists, false otherwise
     */
    public Account existsAccount(String userName){
        return manager.findOne(userName);
    }

    /**
     * Function to add an account into the file
     * @param acc - account object
     * @return null if the account was saved, the given account otherwise
     */
    public Account addAccount(Account acc) throws ValidatorException {
        return manager.save(acc);
    }

    /**
     * Function to check an account
     * @param userName - userName of the account
     * @param pass password provided by the user
     * @return the account if it's correct, null otherwise
     */
    public Account checkAccount(String userName, String pass){
        Account acc = existsAccount(userName);
        if(acc == null){
            return null;
        }
        String securedPassword = acc.getPassword().getPassValue();
        String salt = acc.getPassword().getSaltValue();

        boolean correct = PasswordUtils.verifyUserPassword(pass, securedPassword, salt);
        if(correct){
            return acc;
        }
        return null;
    }

    /**
     * Function to get an iterable containing all accounts
     * @return - iterable containing all accounts
     */
    public Iterable<Account> getAllAccounts(){
        return manager.findAll();
    }

    /**
     * Function to delete an account
     * @param userName - username of the account to delete
     * @return the deleted account if the account is deleted, null otherwise
     */
    public Account deleteAccount(String userName){
        return manager.delete(userName);
    }

    /**
     * Function to update an account
     * @param acc - new account with the same userName
     * @return - null if the account was updated, the given account otherwise
     */
    public Account updateAccount(Account acc) throws ValidatorException {
        return this.manager.update(acc);
    }
}
