package tests;

import account.password.Password;
import account.password.PasswordUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class PasswordUtilsTest {

    @Test
    public void verifyUserPassword() {
        String myPassword = "myPassword123";

        //generat Salt value
        String salt = PasswordUtils.getSalt(30);

        //Protect user's password
        String mySecurePassword = PasswordUtils.generateSecurePassword(myPassword, salt);
        String almostPass = "MyPassword123";
        assert !PasswordUtils.verifyUserPassword(almostPass, mySecurePassword, salt);
        assert PasswordUtils.verifyUserPassword("myPassword123", mySecurePassword, salt);
    }
}