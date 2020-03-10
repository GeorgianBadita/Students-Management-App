package domain.validators;

import account.password.Password;

public class PasswordValidator implements Validator<Password> {
    private String specialChars = "?()><+=_-!@#$%^&*~`/-";
    private String smallLetters = "abcdefghijklmnopqrstuvwxyz";
    private String bigCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private String digits = "0123456789";
    /**
     * Function to validate a password's strength
     * @param password - Password to check
     * @throws ValidatorException
     * @throws IllegalArgumentException
     */
    @Override
    public void validate(Password password) throws ValidatorException, IllegalArgumentException {
        boolean lengthFlag = hasMinimumLength(password, 6);
        boolean smallLetterFlag = hasSmallLetter(password);
        boolean bigLetterFlag = hasBigLetter(password);
        boolean digitFlag = hasDigit(password);
        boolean specialCharFlag = hasSpecialChar(password);
        String errors = "";
        if(!lengthFlag){
            errors += "Password must have length of at least 6!\n";
        }
        if (!bigLetterFlag){
            errors += "Password must have at least one big letter!\n";
        }
        if(!smallLetterFlag){
            errors += "Password must have at least one small letter!\n";
        }
        if(!digitFlag){
            errors += "Password must have at least one digit!\n";
        }
        if(!specialCharFlag){
            errors += "Password must have at least one special character!\n";
        }
        if(errors.length() > 0){
            throw new ValidatorException(errors);
        }
    }

    /**
     * Function which checks if a password has a special character
     * @param password - Password to check
     * @return - true if the password contains a special character,
     * false otherwise
     */
    private boolean hasSpecialChar(Password password) {
        return checkExistance(password, specialChars);
    }

    /**
     * Function which checks if a password contains big letters
     * @param password - password to check
     * @return - true if the password contains at least one big letter,
     * false otherwise
     */
    private boolean hasBigLetter(Password password){
        return checkExistance(password, bigCharacters);
    }

    private boolean checkExistance(Password password, String bigCharacters) {
        String passVal = password.getPassValue();
        for(int i = 0; i<passVal.length(); i++){
            if(bigCharacters.contains(String.valueOf(passVal.charAt(i)))){
                return true;
            }
        }
        return false;
    }

    /**
     * Function which checks if a password has at least one small letter
     * @param password - password to check
     * @return - true if the password has at least one small letter, false otherwise
     */
    private boolean hasSmallLetter(Password password) {
        return checkExistance(password, smallLetters);
    }

    /**
     * Function to check if the length of a password is at least 6
     * @param password - password to check
     * @return - true if the length of the password is at least 6, false
     * otherwise
     */
    private boolean hasMinimumLength(Password password, int minLen) {
        return password.getPassValue().length() >= minLen;
    }

    /**
     * Function to check if a password contains least one digit
     * @param password - password to check
     * @return - true if the password contains at least one digit, false otherwise
     */
    private boolean hasDigit(Password password) {
        return checkExistance(password, digits);
    }
}
