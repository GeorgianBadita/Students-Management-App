package controllers;

import account.Account;
import account.AccountType;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import service.AccountService;
import service.GradeService;
import service.HomeworkService;
import service.StudentService;
import utils.GUIEvent;
import utils.Observable;
import utils.Observer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class AdminController implements Observable<GUIEvent>, AuthenticationInterface {
    @FXML
    private ToggleButton studentBtn;
    @FXML
    private ToggleButton accountBtn;
    @FXML
    private ToggleButton homeworkBtn;
    @FXML
    private ToggleButton gradeBtn;
    @FXML
    private VBox btnsVBox;
    @FXML
    private TableView<Account> accountTable;
    @FXML
    private TableColumn<Account, String> userCol;
    @FXML
    private TableColumn<Account, AccountType> typeCol;
    @FXML
    private Button logOutBtn;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Button deleteBtn;
    @FXML
    private Button updateBtn;
    @FXML
    private Label timeLbl;
    @FXML
    private Button addAccBtn;
    @FXML
    private VBox tableVBox;


    private AccountService accountService;
    private StudentService studService;
    private HomeworkService hwService;
    private GradeService grService;
    private ObservableList<Account> model = FXCollections.observableArrayList();
    private Account account;
    private ScrollPane scrollPane;

    public AdminController(){

    }

    @FXML
    public void initialize(){


        userCol.setCellValueFactory(new PropertyValueFactory<Account, String>("username"));
        typeCol.setCellValueFactory(new PropertyValueFactory<Account, AccountType>("accType"));
        accountTable.setItems(model);
        this.accountTable.setItems(model);
        accountTable.setVisible(true);
        deleteBtn.setVisible(true);
        updateBtn.setVisible(true);
        scrollPane = new ScrollPane();
        scrollPane.setContent(accountTable);
        scrollPane.setPrefSize(600, 200);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        setClock();
    }

    @FXML
    private void addAccHandler(ActionEvent ae) throws IOException {
        Stage stage;
        stage = new Stage();
        FXMLLoader signUpLoader = new FXMLLoader();
        signUpLoader.setLocation(getClass().getResource("../view/signUpView.fxml"));
        AnchorPane  signUpLayout = (AnchorPane)signUpLoader.load();

        SignUpController signUpController = signUpLoader.getController();
        signUpController.setServices(accountService, studService);
        signUpController.setAccount(this.account);
        signUpController.setModel(this.model);
        //logInController.setService(accServ);
        Scene signUpScene = new Scene(signUpLayout);
        stage.setTitle("Add Account");
        stage.setScene(signUpScene);
        stage.show();

    }

    /**
     * Function to set time in separate thread
     */
    private void setClock() {
        setClock(timeLbl);
    }

    static void setClock(Label timeLbl) {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            int second = LocalDateTime.now().getSecond();
            int minute = LocalDateTime.now().getMinute();
            int hour = LocalDateTime.now().getHour();
            timeLbl.setText("Time: " + hour + ":" + (minute) + ":" + second);
        }),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    /**
     * Function to set the service of the controller
     * @param accService accountService object
     */
    public void setServices(AccountService accService, StudentService studService, HomeworkService hwService, GradeService grService){
        accountService = accService;
        this.hwService = hwService;
        this.studService = studService;
        this.grService = grService;
        initModel();
    }

    /**
     * Function to delete an account from the file
     * @param userName username of the account to delete
     * @return the deleted Account if it was deleted, null otherwise
     */
    private Account deleteAccount(String userName){
        return accountService.deleteAccount(userName);
    }

    /**
     * Function to initialize the model
     */
    private void initModel(){
        List<Account> lst = StreamSupport.stream(accountService.getAllAccounts().spliterator(), false).collect(Collectors.toList());
        model.setAll(lst);
    }


    /**
     * Function to make add the glow effect to a button
     * @param btn - Toggle button to apply glow effect on
     */
    public static void makeButtonGlow(ToggleButton btn){
        DropShadow borderGlow = new DropShadow();
        borderGlow.setColor(Color.RED);
        borderGlow.setOffsetX(0f);
        borderGlow.setOffsetY(0f);
        btn.setEffect(borderGlow);
    }

    /**
     * Function to remove glow effect from a button
     * @param btn - button to remove glow effect from
     */
    public static void stopButtonGlow(ToggleButton btn) {
        btn.setEffect(null);
    }

    @FXML
    private void studentMenuHandler(ActionEvent ae){
        showInfo("Sorry you can't see information about students in Administrator mode!");
    }

    @FXML
    private void accountMenuHandler(ActionEvent ae){
        accountTable.setVisible(true);
        deleteBtn.setVisible(true);
        updateBtn.setVisible(true);
    }

    @FXML
    private void homeworkMenuHandler(ActionEvent ae){
        showInfo("Sorry you can't see information about homework in Administrator mode!");
    }

    @FXML
    private void gradeMenuHandler(ActionEvent ae){

        showInfo("Sorry you can't see information about grades in Administrator mode!");
    }

    @FXML
    private void mouseEnteredAcc(MouseEvent me){
        makeButtonGlow(accountBtn);
    }

    @FXML
    private void mouseExitedAcc(MouseEvent me){
        stopButtonGlow(accountBtn);
    }

    @FXML
    private void mouseEnteredStud(MouseEvent me){
        makeButtonGlow(studentBtn);
    }

    @FXML
    private void mouseExitedStud(MouseEvent me){
        stopButtonGlow(studentBtn);
    }

    @FXML
    private void mouseEnteredHomework(MouseEvent me){
        makeButtonGlow(homeworkBtn);
    }

    @FXML
    private void mouseExitedHomework(MouseEvent me){
        stopButtonGlow(homeworkBtn);
    }

    @FXML
    private void mouseEnteredGrade(MouseEvent me){
        makeButtonGlow(gradeBtn);
    }

    @FXML
    private void mouseExitedGrade(MouseEvent me){
        stopButtonGlow(gradeBtn);
    }

    @FXML
    private void logOutHandler(ActionEvent ae){
        notifyObservers(new GUIEvent(this));
    }

    @FXML
    private void deleteHandler(ActionEvent ae){
        int index = accountTable.getSelectionModel().getSelectedIndex();
        if(index < 0){
            showInfo("Please select an account before deleting!");
            return;
        }
        Account selected = accountTable.getSelectionModel().getSelectedItem();
        Alert message = new Alert(Alert.AlertType.CONFIRMATION);
        message.setTitle("Account delete confirmation!");
        message.setHeaderText("Are you sure you want to delete this account?\nIf it's a student account, " +
                "his grades will be deleted too!");
        message.setContentText(selected.getUsername() + " " + selected.getAccType().toString());
        message.showAndWait().filter(ButtonType.OK::equals).ifPresent(b-> {
            Account retValue = this.deleteAccount(selected.getUsername());
            if(retValue == null){
                showInfo("The selected account does not exist!");
            }
            else{
                if(retValue.getAccType().equals(AccountType.STUDENT)){
                    cascadeDelete(retValue);

                }
               // model.setAll(StreamSupport.stream(accountService.getAllAccounts().spliterator(), false).collect(Collectors.toList()));
                updateTable();
                showInfo("The account has been successfully deleted!");
            }
        });

    }

    /**
     * Function to update table after modification
     */
    private void updateTable() {
        model.setAll(StreamSupport.stream(accountService.getAllAccounts().spliterator(), false).
                collect(Collectors.toList()));
        this.accountTable.setItems(model);
    }

    /**
     * Function to cascade delete the student and its grades
     * @param acc - account of the student to be deleted
     */
    private void cascadeDelete(Account acc) {
        if(studService.getStudentByName(acc.getUsername()) != null) {
            this.grService.deleteGradesOfStudent(studService.getStudentByName(acc.getUsername()));
            this.studService.deleteStudent(studService.getStudentByName(acc.getUsername()));
        }
    }

    @FXML
    private void updateHandler(ActionEvent ae) throws IOException {
        int index = this.accountTable.getSelectionModel().getSelectedIndex();
        if(index < 0){
            showInfo("Please select an account before changing its password!");
            return;
        }
        Account selected = this.accountTable.getSelectionModel().getSelectedItem();
        Stage newStage = new Stage();
        //newStage.initStyle(StageStyle.TRANSPARENT);
        FXMLLoader mainLoader = new FXMLLoader();
        mainLoader.setLocation(getClass().getResource("../view/AdminUpdateView.fxml"));
        AnchorPane mainLayout = (AnchorPane)mainLoader.load();
        AdminUpdateController adminController = mainLoader.getController();
        adminController.setServices(accountService, studService);
        adminController.setAccount(selected);
        Scene mainScene = new Scene(mainLayout);
        //mainScene.setFill(Color.TRANSPARENT);
        newStage.setTitle("Administrator");
        newStage.setScene(mainScene);
        newStage.show();
    }

    /**
     * Function to set the current logInController
     * @param logInController - LogInController object
     */
    public void setLogInController(LogInController logInController){
        observers.add(logInController);
    }

    /**
     * Function to set the current connected account
     * @param conAccount - current session connected account
     */
    public void setConnectedAccount(Account conAccount){
        this.account = conAccount;
        this.welcomeLabel.setText("Hi, " + account.getUsername());
    }

    @Override
    public Stage getStage() {
        return (Stage)logOutBtn.getScene().getWindow();
    }

    private List<Observer<GUIEvent>> observers = new ArrayList<>();

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
     * Function to print information to the GUI
     * @param info - string to print
     */
    private void showInfo(String info){
        Alert message = new Alert(Alert.AlertType.INFORMATION);
        message.setTitle("Success!");
        message.setContentText(info);
        message.showAndWait();
        DialogPane dialogPane = message.getDialogPane();
        dialogPane.getStylesheets().add(
                getClass().getResource("../view/style/dialog_style.css").toExternalForm()
        );

        dialogPane.getStyleClass().add("../view/style/dialog_style");
    }

}
