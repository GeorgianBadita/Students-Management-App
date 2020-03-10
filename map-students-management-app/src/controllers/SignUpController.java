package controllers;

import account.Account;
import account.AccountType;
import account.password.Password;
import account.password.PasswordUtils;
import domain.entities.Student;
import domain.validators.IllegalArgumentException;
import domain.validators.ValidatorException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import service.AccountService;
import service.StudentService;
import utils.GUIEvent;
import utils.Groups;
import utils.Observable;
import utils.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class SignUpController implements Observable<GUIEvent>, AuthenticationInterface {
    @FXML
    private TextField studentIdTxt;
    @FXML
    private TextField emailTxt;
    @FXML
    private TextField usernameTxt;
    @FXML
    private PasswordField passwdTxt;
    @FXML
    private PasswordField passwdReTxt;
    @FXML
    private Button signUpBtn;
    @FXML
    private Button logInBtn;
    @FXML
    private ComboBox<Groups> groupCBox;
    @FXML
    private ComboBox<AccountType> accTypeCBox;
    @FXML
    private ImageView emailPng;
    @FXML
    private ImageView idPng;
    @FXML
    private Text qText;
    private AccountService accServ;
    private StudentService studSrv;
    private ObservableList<Account> obs = FXCollections.observableArrayList();


    /**
     * Constructor for SignUpController class
     */
    public SignUpController(){
    }

    @FXML
    public void initialize(){
        accTypeCBox.getItems().setAll(AccountType.values());
        accTypeCBox.getSelectionModel().selectedIndexProperty().addListener(
                (options, oldVal, newVal) -> {
                    if(newVal.equals(AccountType.STUDENT.ordinal())){
                        showStudentFields(true);
                    }else{
                        showStudentFields(false);
                    }
                }
        );
        groupCBox.getItems().setAll(Groups.values());
    }

    /**
     * Function which adds a student to the repository
     * @param id: Integer - id of the student
     * @param name: String - Student's name
     * @param group: Integer - Student's group
     * @param email: String - Student's email
     * @throws ValidatorException - if the arguments are invalid
     * @throws IllegalArgumentException - if the arguments are null
     */
    private Student addStudent(Integer id, String name, Integer group, String email) throws ValidatorException, IllegalArgumentException {
        Student st = new Student(id, name, group, email);
        return this.studSrv.addStudent(st);
    }

    /**
     * Function to set services for the signUpController
     * @param accServ - account service object
     * @param studServ - student service object
     */
    public void setServices(AccountService accServ, StudentService studServ){
        this.accServ = accServ;
        this.studSrv = studServ;
    }

    /**
     * Function to show student fields
     * @param visibility - true for making student fields visible, false for hiding them
     */
    private void showStudentFields(boolean visibility) {
        this.emailPng.setVisible(visibility);
        this.emailTxt.setVisible(visibility);
        this.idPng.setVisible(visibility);
        this.studentIdTxt.setVisible(visibility);
        this.groupCBox.setVisible(visibility);
    }

    /**
     * Function to get the current stage
     * @return current stage
     */
    public Stage getStage(){
        return (Stage) logInBtn.getScene().getWindow();
    }

    /**
     * Function to set the loginController to the singUpController
     * @param logInController - logInController object
     */
    public void setLogInController(LogInController logInController){
        this.addObserver(logInController);
    }

    @FXML
    public void logInHandler(ActionEvent ae){
        notifyObservers(new GUIEvent(this));
    }

    private List<Observer<GUIEvent>> observers = new ArrayList<>();

    @FXML
    public void signUpHandler(ActionEvent ae) throws ValidatorException {
        AccountType accType = this.accTypeCBox.getSelectionModel().getSelectedItem();
        if(accType == null){
            showError("Please select the type of the account you want to create :)");
            return;
        }
        if(accType.equals(AccountType.STUDENT)){
            addStudentAcc();
        }else if(accType.equals(AccountType.PROFESSOR)){
            addProfAcc();
        }else if(accType.equals(AccountType.ADMINISTRATOR)){
            addAdminAcc();
        }
    }

    /**
     * Function to add a professor account
     */
    private void addProfAcc() throws ValidatorException {
        String name = usernameTxt.getText();
        String passwd = passwdTxt.getText();
        String repasswd = passwdReTxt.getText();
        if (checkUserPas(name, passwd, repasswd)) {
            return;
        }
        Account ret = this.addAccount(name, passwd, AccountType.PROFESSOR);
        if(ret == null){
            showInfo("Account added successfully!");
            updateTable();
        }else {
            showInfo("Something went wrong! Please try again!");
        }
    }

    /**
     * Function to check username and password matching
     * @param name - username given by the user
     * @param passwd - provided password
     * @param repasswd - other password field
     * @return true if the username and passwords are well given, false otherwise
     */
    private boolean checkUserPas(String name, String passwd, String repasswd) {
        if(passwd.isEmpty() || repasswd.isEmpty() || name.isEmpty()){
            showError("All fields must be complete!");
            return true;
        }else if(!passwd.equals(repasswd)){
            showError("Your passwords don't match");
            return true;
        }
        return false;
    }

    /**
     * Function to add an administrator account
     */
    private void addAdminAcc() throws ValidatorException {
        String name = usernameTxt.getText();
        String passwd = passwdTxt.getText();
        String repasswd = passwdReTxt.getText();
        if (checkUserPas(name, passwd, repasswd)) {
            return;
        }
        Account ret = this.addAccount(name, passwd, AccountType.ADMINISTRATOR);
        if(ret == null){
            showInfo("Account added successfully!");
            updateTable();
        }else {
            showInfo("Something went wrong, please try again!");
        }
    }

    /**
     * Function to update th table
     */
    private void updateTable() {
        obs.setAll(StreamSupport.stream(accServ.getAllAccounts().spliterator(), false).collect(Collectors.toList()));
    }

    /**
     * Function to add a student account
     */
    private void addStudentAcc() {
        String idTxt = studentIdTxt.getText();
        String name = usernameTxt.getText();
        String email = emailTxt.getText();
        String paswd = passwdTxt.getText();
        String repaswd = passwdReTxt.getText();
        Groups gr = groupCBox.getSelectionModel().getSelectedItem();
        if(idTxt.isEmpty() || name.isEmpty() || email.isEmpty() || paswd.isEmpty() || repaswd.isEmpty() || gr == null){
            showError("Sorry, all fields must be completed!");
            return;
        }
        if(!paswd.equals(repaswd)){
            showError("Sorry, your passwords doesn't match");
            return;
        }
        try{
            Account acc = accServ.existsAccount(name);
            Student exists = studSrv.getStudentByUserName(name);
            if(acc != null || exists != null){
                showError("There's already an account with that name!");
            }
            Integer id = Integer.parseInt(idTxt);
            Integer group = Integer.parseInt(gr.toString().substring(2));
            Student retVal = this.addStudent(id, name, group, email);
            if(retVal != null){
                showError("The student already has an account!");
                return;
            }else{
               Account ret =  addAccount(name, paswd, AccountType.STUDENT);
                if (ret == null) {
                    showInfo("Account has been added successfully!");
                    updateTable();
                } else {
                    studSrv.deleteStudent(id);
                    showError("Something went wrong, please try again");
                }
            }
        }catch(NumberFormatException ex){
            showError("Id must contain only digits!");
        } catch (ValidatorException e) {
            showError(e.getMessage());
        }
    }

    /**
     * Function to add an account
     * @param usernameTxt -username of the account
     * @param passwdTxt - account password
     * @param accType - type of the account
     */
    private Account addAccount(String usernameTxt, String passwdTxt, AccountType accType){
        String salt = PasswordUtils.getSalt(30);
        String securePass = PasswordUtils.generateSecurePassword(passwdTxt, salt);
        Password pwd = new Password(securePass, salt);
        Account acc = new Account(usernameTxt, accType, pwd);
        Password checkPwd = new Password(passwdTxt, "");
        try {
            PasswordUtils.verifyPasswordStrength(checkPwd);
        } catch (ValidatorException e) {
            showInfo(e.getMessage());
            return acc;
        }
        try {
            return this.accServ.addAccount(acc);
        } catch (ValidatorException e) {
            return  acc;
        }
    }

    @Override
    public void addObserver(Observer<GUIEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<GUIEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(GUIEvent t) {
        observers.forEach(x -> x.update(t));
    }

    /**
     * Function to print errors to the GUI
     * @param err - error to print
     */
    private void showError(String err){
        Alert message = new Alert(Alert.AlertType.WARNING);
        message.setTitle("Error message!");
        message.setContentText(err);
        message.showAndWait();
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


    /**
     * Function to set the account
     * @param account
     */
    public void setAccount(Account account) {
        if(account == null){
            return;
        }
        if(account.getAccType().toString().equals("ADMINISTRATOR")){
            this.signUpBtn.setText("Create");
            this.logInBtn.setVisible(false);
            this.qText.setVisible(false);

        }
    }

    public void setModel(ObservableList<Account> observableList){
        this.obs = observableList;

    }
}
