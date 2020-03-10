package controllers;

import account.Account;
import account.AccountType;
import account.password.Password;
import account.password.PasswordUtils;
import domain.validators.ValidatorException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import service.AccountService;
import service.StudentService;

import java.io.IOException;

public class AdminUpdateController {
    @FXML
    private PasswordField passTxt;
    @FXML
    private PasswordField rePassTxt;
    @FXML
    private Button changePassBtn;

    private AccountService accService;
    private StudentService studentService;
    private Account tobeUpdated;

    /**
     * Function to update an account
     * @param usernameTxt -username of the account
     * @param passwdTxt - account password
     * @param accType - type of the account
     */
    private Account updateAccount(String usernameTxt, String passwdTxt, AccountType accType) {
        Password toCheck = new Password(passwdTxt, "");
        try {
            PasswordUtils.verifyPasswordStrength(toCheck);
        } catch (ValidatorException e) {
            showInfo(e.getMessage());
            return new Account(usernameTxt, accType, toCheck);
        }
        String salt = PasswordUtils.getSalt(30);
        String securePass = PasswordUtils.generateSecurePassword(passwdTxt, salt);
        Password pwd = new Password(securePass, salt);
        Account acc = new Account(usernameTxt, accType, pwd);
        try {
            return this.accService.updateAccount(acc);
        } catch (ValidatorException e) {
            showInfo("Wrong password!");
            return acc;
        }
    }

    /**
     * Default constructor for AdminUpdateController class
     */
    public AdminUpdateController(){

    }

    /**
     * Function to set the services for admin update controller
     * @param accService - accountService object
     * @param studServ - StudentService object
     */
    public void setServices(AccountService accService, StudentService studServ){
        this.accService = accService;
        this.studentService = studServ;
    }

    /**
     * Function to set the account to be updated
     * @param acc - account to be updated
     */
    public void setAccount(Account acc){
        this.tobeUpdated = acc;
    }

    @FXML
    public void initialize(){

    }

    @FXML
    public void passChangeHandler(ActionEvent ae) throws IOException, ValidatorException {
        String pass = passTxt.getText();
        String rePass = rePassTxt.getText();
        if(!pass.equals(rePass)){
            showInfo("Passwords must match!");
            return;
        }
        Account retValue = this.updateAccount(tobeUpdated.getUsername(), pass, tobeUpdated.getAccType());
        if(retValue == null){
            showInfo("Password has been changed!");
            Stage toClose = (Stage)this.changePassBtn.getScene().getWindow();
            toClose.close();
        }
    }
    /**
     * Function to print information to the GUI
     * @param info - string to print
     */
    private void showInfo(String info){
        Alert message = new Alert(Alert.AlertType.INFORMATION);
        message.setTitle("Success!");
        message.setContentText(info);
        message.showAndWait();
    }
}
