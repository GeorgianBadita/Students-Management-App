package domain.validators;

import account.Account;


public class AccountValidator implements Validator<Account> {

    /**
     * Overriding Account validator method
     * @param entity - Account object to be validated
     * @throws ValidatorException - throws ValidatorException if the given entity is not well constructed
     */
    @Override
    public void validate(Account entity) throws ValidatorException{
        String errs = "";

        if(entity.getUsername().equals("")){
            errs += "Username can't be empty!\n";
        }
        if(entity.getAccType().toString().isEmpty()){
            errs += "Account type can't be empty!\n";
        }
        if(entity.getPassword().getPassValue().isEmpty()){
            errs += "Password string can't be empty!\n";
        }

        if(!errs.isEmpty()){
            throw new ValidatorException(errs);
        }
    }
}
