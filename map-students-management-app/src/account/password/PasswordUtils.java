package account.password;

import domain.validators.PasswordValidator;
import domain.validators.ValidatorException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

public class PasswordUtils {
    private static final Random RANDOM = new SecureRandom();
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;

    /**
     * Function to get a random salt value
     * @param length - length of salt value
     * @return the salt value (secret key)
     */
    public static String getSalt(int length){
        StringBuilder retValue = new StringBuilder(length);

        for(int i = 0; i<length; i++) {
            retValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }

        return new String(retValue);
    }

    /**
     * Function for hashing the password + salt using Password Based Encryption algorithms
     * @param password - password as an array of chars
     * @param salt - salt value
     * @return - the hashed password
     */
    private static byte[] hash(char[] password, byte[] salt){
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);

        Arrays.fill(password, Character.MIN_VALUE);

        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
        } finally {
            spec.clearPassword();
        }
    }

    /**
     * Function to generate the String format of the secure password
     * @param password - password to be encrypted
     * @param salt - random generated secret value
     * @return - secure password as string
     */
    public static String generateSecurePassword(String password, String salt){
        String retValue = null;

        byte[] secPasswd = hash(password.toCharArray(), salt.getBytes());

        retValue = Base64.getEncoder().encodeToString(secPasswd);
        return retValue;
    }

    /**
     * Function to check if the password provided by the user is correct
     * @param userPass - password given by the user
     * @param securePass - corresponding password
     * @param salt - salt value
     * @return true if the given password is correct, false otherwise
     */
    public static boolean verifyUserPassword(String userPass, String securePass, String salt){
        boolean retValue;

        //Generate new secure password with the same salt
        String newSecurePass = generateSecurePassword(userPass, salt);

        //check if the two passwords are equal
        retValue = newSecurePass.equalsIgnoreCase(securePass);

        return retValue;
    }


    /**
     * Function to check passwords strength
     * @param pass - password to be validated
     * @return - true if the password has enough strength, false otherwise
     */
    public static boolean verifyPasswordStrength(Password pass) throws ValidatorException {
        PasswordValidator passwordValidator = new PasswordValidator();
        passwordValidator.validate(pass);
        return true;
    }
}
