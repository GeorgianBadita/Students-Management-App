package controllers;

import account.Account;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import service.AccountService;
import service.GradeService;
import service.HomeworkService;
import service.StudentService;
import utils.GUIEvent;
import utils.Observer;

import java.io.IOException;

/**
 * LogInController class
 */
public class LogInController implements AuthenticationInterface, Observer<GUIEvent>{
    @FXML
    private TextField usernameTxt;
    @FXML
    private PasswordField passwdTxt;
    @FXML
    private Button logInBtn;
    @FXML
    private Button signUpBtn;

    private AccountService accServ;

    private StudentService studService;

    private HomeworkService hwService;

    private GradeService grService;

    /**
     * Constructor for LogInController class
     */
    public LogInController() {

    }

    /**
     * Function to return currentStage
     * @return - current Stage
     */
    public Stage getStage(){
        return (Stage)logInBtn.getScene().getWindow();
    }

    /**
     * Function to set the service for this controller
     * @param accServ - AccountService object
     * @param studServ - StudentService object
     */
    public void setServices(AccountService accServ, StudentService studServ, HomeworkService hwService, GradeService grService){
        this.accServ = accServ;
        this.studService = studServ;
        this.hwService = hwService;
        this.grService = grService;
    }


    /**
     * Function to check the correctness of an user account
     * @param userName -userName of the account
     * @param password -password given by the user
     * @return the Account if it's correct, null otherwise
     */
    private Account checkPassword(String userName, String password){
        return accServ.checkAccount(userName, password);
    }


    @FXML
    public void initialize(){
    }

    @FXML
    private void logInHandler(ActionEvent ae) throws IOException {
        String userName = usernameTxt.getText();
        String password = passwdTxt.getText();
        if(userName.isEmpty() || password.isEmpty()){
            showError("Username and password can't be empty!");
            return;
        }
        Account retVal = checkPassword(userName, password);
        if(retVal != null){
            switch (retVal.getAccType()){
                case STUDENT:
                    setStudentInterface(retVal);
                    break;
                case PROFESSOR:
                    setProfessorInterface(retVal);
                    break;
                case ADMINISTRATOR:
                    setAdminInterface(retVal);
                    break;
            }
        }
        else{
            showError("Sorry, username or password are incorrect!");
        }
    }

    /**
     * Function to set the interface for student use
     * @param retVal - account to be logged in
     */
    private void setStudentInterface(Account retVal) throws IOException {
        Stage stageToHide = (Stage) this.logInBtn.getScene().getWindow();
        stageToHide.hide();
        Stage newStage = new Stage();
        FXMLLoader mainLoader = new FXMLLoader();
        mainLoader.setLocation(getClass().getResource("../view/StudentView.fxml"));
        AnchorPane mainLayout = (AnchorPane)mainLoader.load();
        StudentController studentController = mainLoader.getController();
        studentController.setConnectedAccount(retVal);
        studentController.setService(grService, hwService);
        studentController.setLogInController(this);
        Scene mainScene = new Scene(mainLayout);
        //mainScene.setFill(Color.TRANSPARENT);
        newStage.setTitle("Student");
        newStage.setScene(mainScene);
        newStage.show();

    }

    /**
     * Function to set the interface in professor mode
     * @param retVal - account which has been logged in
     */
    private void setProfessorInterface(Account retVal) throws IOException {
        Stage stageToHide = (Stage) this.logInBtn.getScene().getWindow();
        stageToHide.hide();
        Stage newStage = new Stage();
        FXMLLoader mainLoader = new FXMLLoader();
        mainLoader.setLocation(getClass().getResource("../view/TeacherView.fxml"));
        AnchorPane mainLayout = (AnchorPane)mainLoader.load();
        TeacherController teacherController = mainLoader.getController();
        teacherController.setServices(studService, hwService, grService);
        teacherController.setLogInController(this);
        teacherController.setConnectedAccount(retVal);
        Scene mainScene = new Scene(mainLayout);
        //mainScene.setFill(Color.TRANSPARENT);
        newStage.setTitle("Teacher");
        newStage.setScene(mainScene);
        newStage.show();
    }

    /**
     * Function to set the interface in administrator mode
     * @param retVal - account which has been logged in
     */
    private void setAdminInterface(Account retVal) throws IOException {
        Stage thisStage = (Stage)this.logInBtn.getScene().getWindow();
        thisStage.hide();
        Stage newStage = new Stage();
        //newStage.initStyle(StageStyle.TRANSPARENT);
        FXMLLoader mainLoader = new FXMLLoader();
        mainLoader.setLocation(getClass().getResource("../view/AdminView.fxml"));
        AnchorPane mainLayout = (AnchorPane)mainLoader.load();
        AdminController adminController = mainLoader.getController();
        adminController.setServices(accServ, studService, hwService, grService);
        adminController.setLogInController(this);
        adminController.setConnectedAccount(retVal);
        Scene mainScene = new Scene(mainLayout);
        //mainScene.setFill(Color.TRANSPARENT);
        newStage.setTitle("Administrator");
        newStage.setScene(mainScene);
        newStage.show();
    }

    @FXML
    private void signUpHandler(ActionEvent ae) throws IOException {
        Stage toClose = (Stage) signUpBtn.getScene().getWindow();
        toClose.hide();
        Stage stage = new Stage();
        FXMLLoader signUpLoader = new FXMLLoader();
        signUpLoader.setLocation(getClass().getResource("../view/signUpView.fxml"));
        AnchorPane  signUpLayout = (AnchorPane)signUpLoader.load();

        SignUpController signUpController = signUpLoader.getController();
        signUpController.setLogInController(this);
        signUpController.setServices(accServ, studService);
        //logInController.setService(accServ);
        Scene signUpScene = new Scene(signUpLayout);
        stage.setTitle("Sign Up");
        stage.setScene(signUpScene);
        stage.show();
    }


    /**
     * Function to print errors to the GUI
     * @param err - error to print
     */
    private void showError(String err){
        Alert message = new Alert(Alert.AlertType.INFORMATION);
        message.setTitle("Error message!");
        message.setContentText(err);
        message.showAndWait();
    }

    /**
     * Function to clear all fields
     */
    private void clearFields(){
        this.passwdTxt.clear();
        this.usernameTxt.clear();
    }

    /**
     * Update function for an observer object
     *
     * @param guiEvent - GUIEvent type object
     */
    @Override
    public void update(GUIEvent guiEvent) {
        AuthenticationInterface anInterface;
        anInterface = guiEvent.getData();
        anInterface.getStage().close();
        this.getStage().show();
        clearFields();
    }
}
