package controllers;

import account.Account;
import domain.entities.Homework;
import domain.entities.Student;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.GradeService;
import service.HomeworkService;
import utils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static controllers.AdminController.makeButtonGlow;
import static controllers.AdminController.stopButtonGlow;

public class StudentController implements Observable<GUIEvent>, AuthenticationInterface{
    @FXML
    private TableView<Homework> hwTable;
    @FXML
    private TableColumn<DTOGrade, String> profCol;
    @FXML
    private TableColumn<DTOGrade, String> feedbackCol;
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
    private Button logOutBtn;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label timeLbl;
    @FXML
    private TableView<DTOGrade> studTable;
    @FXML
    private Label  studLabel;
    @FXML
    private TableColumn<DTOGrade, Integer> hwIdCol;
    @FXML
    private TableColumn<DTOGrade, Double> gradeValCol;
    @FXML
    private TableColumn<DTOGrade, String> hwDescCol;
    @FXML
    private TableColumn<Homework, Integer> hIdCol;
    @FXML
    private TableColumn<Homework, String> hDescCol;
    @FXML
    private TableColumn<Homework, Integer> hwRecvWeekCol;
    @FXML
    private TableColumn<Homework, Integer> hwDeadWeekCol;
    @FXML
    private TableColumn<Homework, String> hwCurrWeekCol;
    @FXML
    private Label hwLabel;
    @FXML
    private TextField descFilterTxt;
    @FXML
    private Button clearBtn;
    @FXML
    private Button orderBtn;

    private ObservableList<DTOGrade> observableList = FXCollections.observableArrayList();
    private ObservableList<Homework> hwModel = FXCollections.observableArrayList();
    private GradeService grService;
    private HomeworkService hwService;
    private Account loggedAccount;

    /**
     * Default constructor for TeacherController class
     */
    public StudentController(){

    }


    @FXML
    public void initialize(){
        accountBtn.getStylesheets().add(this.getClass().getResource(
                "../view/style/greyScaled/buttonAccountImageGScaled.css"
        ).toExternalForm());

        studentBtn.getStylesheets().add(this.getClass().getResource(
                "../view/style/greyScaled/buttonStudentImageGscaled.css"
        ).toExternalForm());

        homeworkBtn.getStylesheets().add(this.getClass().getResource(
                "../view/style/normal/buttonHomeworkImage.css"
        ).toExternalForm());

        gradeBtn.getStylesheets().add(this.getClass().getResource(
                "../view/style/normal/buttonGradeImage.css"
        ).toExternalForm());
        String style = "-fx-background-color: rgb(93,69,255);";
        btnsVBox.setStyle(style);
        startClock();
        this.gradeMenuHandler(null);
    }

    /**
     * Function to set the time in a separate thread
     */
    private void startClock() {
        AdminController.setClock(timeLbl);
    }

    /**
     * Function to set the services for this controller
     * @param grService - gradeService object
     */
    public void setService(GradeService grService, HomeworkService hwService){
        this.grService = grService;
        this.hwService = hwService;
        initModel();
        initHwModel();
    }

    private void initHwModel() {
        List<Homework> lst = StreamSupport.stream(hwService.getAllHw().spliterator(), false).collect(Collectors.toList());
        hwModel.setAll(lst);
    }

    /**
     * Function to init the model
     */
    private void initModel() {
        Student st = grService.getStudentById(grService.getStudentIdByName(loggedAccount.getUsername()));
        List<DTOGrade> lst = grService.getStudentGradesById(st.getId());
        observableList.setAll(lst);
    }


    /**
     * Function to add the logInController to observers list
     * @param logInController  LogInController object
     */
    public void setLogInController(LogInController logInController){
        this.addObserver(logInController);
    }

    @FXML
    private void studentMenuHandler(ActionEvent ae){
        showInfo("Sorry you can't see information about other students in Student mode!");
    }

    /**
     * Function to initialize Student Table
     */
    private void initStudTable() {
        hwIdCol.setCellValueFactory(new PropertyValueFactory<DTOGrade, Integer>("hwId"));
        hwDescCol.setCellValueFactory(new PropertyValueFactory<DTOGrade, String>("hwDesc"));
        gradeValCol.setCellValueFactory(new PropertyValueFactory<DTOGrade, Double>("gradeValue"));
        profCol.setCellValueFactory(new PropertyValueFactory<DTOGrade, String>("profName"));
        feedbackCol.setCellValueFactory(new PropertyValueFactory<DTOGrade, String>("feedBack"));

        studTable.setItems(observableList);
    }

    @FXML
    private void orderHandler(ActionEvent ae){
        observableList.sort((x, y)-> (int) (x.getGradeValue() - y.getGradeValue()));
    }

    @FXML
    private void clearHandler(ActionEvent ae){
        initModel();
    }

    @FXML
    private void accountMenuHandler(ActionEvent ae){
        showInfo("Sorry you can't see information about accounts in Student mode!");
    }

    @FXML
    private void homeworkMenuHandler(ActionEvent ae){
        initHwTable();
        studTable.setVisible(false);
        studLabel.setVisible(false);
        orderBtn.setVisible(false);
        clearBtn.setVisible(false);
        descFilterTxt.setVisible(false);
        hwLabel.setVisible(true);
        hwTable.setVisible(true);
    }

    /**
     * Function to set the homework table
     */
    private void initHwTable() {
        hIdCol.setCellValueFactory(new PropertyValueFactory<Homework, Integer>("id"));
        hDescCol.setCellValueFactory(new PropertyValueFactory<Homework, String>("description"));
        hwDeadWeekCol.setCellValueFactory(new PropertyValueFactory<Homework, Integer>("deadlineWeek"));
        hwRecvWeekCol.setCellValueFactory(new PropertyValueFactory<Homework, Integer>("recvWeek"));
        hwCurrWeekCol.setCellValueFactory(
                cellData -> {
                    return new SimpleStringProperty(Utility.getCurrWeek().toString());
                }
        );

        hwTable.setItems(hwModel);
    }

    public void gradeMenuHandler(ActionEvent actionEvent) {
        initStudTable();
        descFilterTxt.textProperty().addListener((observableValue, s, t1) -> {
            initModel();
            FilteredList<DTOGrade> flt = observableList.filtered(dtoGrade -> true);

            flt.setPredicate(
                    x -> {
                        return x.getHwDesc().toLowerCase().contains(t1);
                    }
            );
            List<DTOGrade> lst = new ArrayList<>(flt);
            observableList.setAll(lst);
        });
        studLabel.setVisible(true);
        studTable.setVisible(true);
        orderBtn.setVisible(true);
        clearBtn.setVisible(true);
        descFilterTxt.setVisible(true);
        hwTable.setVisible(false);
        hwLabel.setVisible(false);

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
     * Function to get the controller's stage
     * @return - current stage
     */
    @Override
    public Stage getStage() {
        return (Stage)studentBtn.getScene().getWindow();
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
     * Function to set the current logged account
     * @param retVal - currently connected account
     */
    public void setConnectedAccount(Account retVal) {
        this.loggedAccount = retVal;
        this.welcomeLabel.setText("Hi, " + retVal.getUsername());
    }


}
