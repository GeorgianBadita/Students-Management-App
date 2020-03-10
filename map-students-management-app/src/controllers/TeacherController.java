package controllers;

import account.Account;
import domain.entities.Grade;
import domain.entities.Homework;
import domain.entities.Student;
import domain.validators.IllegalArgumentException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import service.GradeService;
import service.HomeworkService;
import service.StudentService;
import utils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static controllers.AdminController.makeButtonGlow;
import static controllers.AdminController.stopButtonGlow;

public class TeacherController implements Observable<GUIEvent>, AuthenticationInterface {
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
    private TableView<Student> studTable;
    @FXML
    private TableColumn<Student, Integer> studIdCol;
    @FXML
    private TableColumn<Student, String> studNameCol;
    @FXML
    private TableColumn<Student, Integer> groupCol;
    @FXML
    private TableColumn<Student, String> emailCol;
    @FXML
    private Button hwAddBtn;
    @FXML
    private Button hwDeleteBtn;
    @FXML
    private Button hwUpdateBtn;
    @FXML
    private TableView<Homework> hwTable;
    @FXML
    private TableColumn<Homework, Integer> hwIdCol;
    @FXML
    private TableColumn<Homework, String> hwDescCol;
    @FXML
    private TableColumn<Homework, Integer> recvWCol;
    @FXML
    private TableColumn<Homework, Integer> deadlWCol;
    @FXML
    private TableColumn<Homework, String> currWCol;
    @FXML
    private Label studLabel;
    @FXML
    private Label hwLabel;
    @FXML
    private Label grLabel;
    @FXML
    private TableView<Grade> grTable;
    @FXML
    private TableColumn<Grade, String> grStudNameCol;
    @FXML
    private TableColumn<Grade, Integer> grHwIdCol;
    @FXML
    private TableColumn<Grade, Double> gradeValueCol;
    @FXML
    private TableColumn<Grade, String> professorCol;
    @FXML
    private TableColumn<Grade, String> feedbackCol;
    @FXML
    private Button grAddBtn;
    @FXML
    private Button grUpdateBtn;
    @FXML
    private Button grDeleteBtn;
    @FXML
    private ToggleButton reportBtn;
    @FXML
    private Label timeLbl;
    @FXML
    private TextField studNameFltTxt;
    @FXML
    private ComboBox<Groups> grFilterCb;
    @FXML
    private Label grFltLbl;
    @FXML
    private Button clearBtn;
    @FXML
    private Button grClearBtn;
    @FXML
    private TextField hwFltTxt;
    @FXML
    private TextField grFltTxt;
    @FXML
    private TextField grStudFltTxt;
    @FXML
    private Label filtersLbl;
    @FXML
    private Button extendBtn;

    private StudentService studService;
    private HomeworkService hwService;
    private GradeService grService;
    private Account loggedAccount;
    private ObservableList<Student> studModel = FXCollections.observableArrayList();
    private ObservableList<Homework> hwModel = FXCollections.observableArrayList();
    private ObservableList<Grade> grModel = FXCollections.observableArrayList();

    /**
     * Default constructor for TeacherController class
     */
    public TeacherController(){

    }

    /**
     * Function to delete a homework by id
     * @param id - id of the homework to be deleted
     * @return - the deleted homework if it was deleted, null otherwise
     */
    private Homework deleteHomework(Integer id){
        return hwService.deleteHomework(id);
    }

    /**
     * Function to delete a grade
     * @param id - id of the grade to be deleted
     * @return - the deleted grade if it was deleted, null otherwise
     */
    private Grade deleteGrade(Pair<Integer, Integer> id){
        return this.grService.deleteGrade(id);
    }


    @FXML
    public void initialize(){
        accountBtn.getStylesheets().add(this.getClass().getResource(
                "../view/style/greyScaled/buttonAccountImageGScaled.css"
        ).toExternalForm());

        studentBtn.getStylesheets().add(this.getClass().getResource(
                "../view/style/normal/buttonStudentImage.css"
        ).toExternalForm());

        homeworkBtn.getStylesheets().add(this.getClass().getResource(
                "../view/style/normal/buttonHomeworkImage.css"
        ).toExternalForm());

        gradeBtn.getStylesheets().add(this.getClass().getResource(
                "../view/style/normal/buttonGradeImage.css"
        ).toExternalForm());

        reportBtn.getStylesheets().add(this.getClass().getResource(
                "../view/style/normal/buttonReport.css"
        ).toExternalForm());


        String style = "-fx-background-color: rgb(93,69,255);";
        btnsVBox.setStyle(style);
        startClock();
        this.studentMenuHandler(null);
    }

    /**
     * Function to set the time in a separate thread
     */
    private void startClock() {
        AdminController.setClock(timeLbl);
    }

    /**
     * Function to set the services for this controller
     * @param studService - Student Service object
     * @param hwServ - homework service object
     * @param grService - gradeService object
     */
    public void setServices(StudentService studService, HomeworkService hwServ, GradeService grService){
        this.studService = studService;
        this.hwService = hwServ;
        this.grService = grService;
        initStudModel();
        initHwModel();
        initGrModel();
    }

    /**
     * Function to initialize Grade Model
     */
    private void initGrModel() {
        List<Grade> list = StreamSupport.stream(grService.getAllGradesSrv().spliterator(), false).collect(Collectors.toList());;
        grModel.setAll(list);
    }

    /**
     * Function to initialize the model for homework table
     */
    private void initHwModel() {
        List<Homework> list = StreamSupport.stream(hwService.getAllHw().spliterator(), false).collect(Collectors.toList());
        hwModel.setAll(list);
    }

    /**
     * Function to initialize the student model
     */
    private void initStudModel() {
        List<Student> lst = StreamSupport.stream(studService.getAllSt().spliterator(), false).collect(Collectors.toList());
        studModel.setAll(lst);
    }

    /**
     * Function to add the logInController to observers list
     * @param logInController  LogInController object
     */
    public void setLogInController(LogInController logInController){
        this.addObserver(logInController);
    }

    @FXML
    private void emailHandler(ActionEvent ae){

    }

    @FXML
    private void clearHandler(ActionEvent ae){
        studNameFltTxt.setText("");
        initStudModel();
    }

    @FXML
    private void studentMenuHandler(ActionEvent ae){
        initStudTable();
        studTable.setVisible(true); grClearBtn.setVisible(false);
        studLabel.setVisible(true); hwFltTxt.setVisible(false);
        hwTable.setVisible(false); grStudFltTxt.setVisible(false);
        hwAddBtn.setVisible(false); grFltTxt.setVisible(false);
        hwDeleteBtn.setVisible(false);  filtersLbl.setVisible(true);
        hwUpdateBtn.setVisible(false);  extendBtn.setVisible(false);
        hwLabel.setVisible(false);
        grTable.setVisible(false);
        grLabel.setVisible(false);
        grAddBtn.setVisible(false);
        grDeleteBtn.setVisible(false);
        grUpdateBtn.setVisible(false);
        clearBtn.setVisible(true);
        grFltLbl.setVisible(true);
        studNameFltTxt.setVisible(true);
        grFilterCb.setVisible(true);
        grFilterCb.getItems().setAll(Groups.values());
    }

    /**
     * Function to initialize the student table
     */
    private void initStudTable() {
        studIdCol.setCellValueFactory(new PropertyValueFactory<Student, Integer>("id"));
        studNameCol.setCellValueFactory(new PropertyValueFactory<Student, String>("name"));
        groupCol.setCellValueFactory(new PropertyValueFactory<Student, Integer>("group"));
        emailCol.setCellValueFactory(new PropertyValueFactory<Student, String>("email"));
        studTable.setItems(studModel);
        studNameFltTxt.textProperty().addListener((observableValue, s, t1) -> {
            initStudModel();
            FilteredList<Student> flt = studModel.filtered(dtoGrade -> true);

            flt.setPredicate(
                    x -> {
                        int index = grFilterCb.getSelectionModel().getSelectedIndex();
                        if(index <0 ) {
                            return x.getName().toLowerCase().contains(t1);
                        }else{
                            String grString = grFilterCb.getSelectionModel().getSelectedItem().toString();
                            Integer group = Integer.parseInt(grString.substring(2));
                            return x.getName().toLowerCase().contains(t1) && group.equals(x.getGroup());
                        }
                    }
            );
            List<Student> lst = new ArrayList<>(flt);
            studModel.setAll(lst);
        });

        grFilterCb.getSelectionModel().selectedItemProperty().addListener((observableValue, groupsSingleSelectionModel, t1) -> {
            initStudModel();
            FilteredList<Student> flt = studModel.filtered(dtoGrade -> true);
            flt.setPredicate(
                    x -> {
                        if(t1 != null) {
                            String grString = t1.toString();
                            Integer group = Integer.parseInt(grString.substring(2));
                            if(studNameFltTxt.getText().isEmpty()) {
                                return x.getGroup().equals(group);
                            }
                            else{
                                return x.getGroup().equals(group) && x.getName().toLowerCase().contains(studNameFltTxt.getText());
                            }
                        }
                        return true;
                    }
            );
            List<Student> lst = new ArrayList<>(flt);
            studModel.setAll(lst);
        });
    }


    @FXML
    private void grClearHandler(ActionEvent ae){
        initGrModel();
    }

    @FXML
    private void accountMenuHandler(ActionEvent ae){
        showInfo("Sorry you can't see information about accounts in Teacher mode!");
    }

    @FXML
    private void extendHandler(ActionEvent ae){
        int index = hwTable.getSelectionModel().getSelectedIndex();
        if(index < 0){
            showInfo("Please select a homework before extending it!");
            return;
        }
        Homework selected = hwTable.getSelectionModel().getSelectedItem();
        int retValue = hwService.extendHomeworkServ(selected.getId());
        if(retValue != 1){
            showInfo("The homework can no longer be extended!");
        }else {
            resetHwTable();
        }
    }

    @FXML
    private void homeworkMenuHandler(ActionEvent ae){
        initHwTable();
        grFltTxt.setVisible(false); grStudFltTxt.setVisible(false);
        hwLabel.setVisible(true); hwFltTxt.setVisible(false);
        hwTable.setVisible(true); filtersLbl.setVisible(false);
        hwAddBtn.setVisible(true);extendBtn.setVisible(true);
        hwDeleteBtn.setVisible(true);
        hwUpdateBtn.setVisible(true);
        studTable.setVisible(false);
        studLabel.setVisible(false);
        grTable.setVisible(false);
        grLabel.setVisible(false);
        clearBtn.setVisible(false);
        grAddBtn.setVisible(false);
        grDeleteBtn.setVisible(false);
        grClearBtn.setVisible(false);
        grUpdateBtn.setVisible(false);
        grFltLbl.setVisible(false);
        grFilterCb.setVisible(false);
        studNameFltTxt.setVisible(false);
    }

    /**
     * Function to set homework table
     */
    private void initHwTable() {
        hwIdCol.setCellValueFactory(new PropertyValueFactory<Homework, Integer>("id"));
        hwDescCol.setCellValueFactory(new PropertyValueFactory<Homework, String>("description"));
        deadlWCol.setCellValueFactory(new PropertyValueFactory<Homework, Integer>("deadlineWeek"));
        recvWCol.setCellValueFactory(new PropertyValueFactory<Homework, Integer>("recvWeek"));
        currWCol.setCellValueFactory(
                cellData -> {
                   return new SimpleStringProperty(Utility.getCurrWeek().toString());
                }
        );
        hwTable.setItems(hwModel);
    }

    @FXML
    private void gradeMenuHandler(ActionEvent ae){
        initGrTable();
        studTable.setVisible(false); grClearBtn.setVisible(true);
        studLabel.setVisible(false); grStudFltTxt.setVisible(true);
        hwTable.setVisible(false);   grFltTxt.setVisible(true);
        hwAddBtn.setVisible(false);  hwFltTxt.setVisible(true);
        hwDeleteBtn.setVisible(false); filtersLbl.setVisible(true);
        hwUpdateBtn.setVisible(false); extendBtn.setVisible(false);
        hwLabel.setVisible(false);
        grTable.setVisible(true);
        grLabel.setVisible(true);
        clearBtn.setVisible(false);
        grAddBtn.setVisible(true);
        grDeleteBtn.setVisible(true);
        grUpdateBtn.setVisible(true);
        grFilterCb.setVisible(false);
        grFltLbl.setVisible(false);
        studNameFltTxt.setVisible(false);
    }

    /**
     * Function to initialize the grade table
     */
    private void initGrTable() {
        grStudNameCol.setCellValueFactory(
                cellData -> {
                    try {
                        return new SimpleStringProperty(this.grService.getStudentNameById(cellData.getValue().getStudId()));
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
        );
        grHwIdCol.setCellValueFactory(new PropertyValueFactory<Grade, Integer>("hwId"));
        gradeValueCol.setCellValueFactory(new PropertyValueFactory<Grade, Double>("grade"));
        professorCol.setCellValueFactory(new PropertyValueFactory<Grade, String>("profName"));
        feedbackCol.setCellValueFactory(new PropertyValueFactory<Grade, String>("feedback"));
        grTable.setItems(grModel);
        grStudFltTxt.textProperty().addListener((observableValue, s, t1) -> {
            initGrModel();
            FilteredList<Grade> flt = grModel.filtered(dtoGrade -> true);
            flt.setPredicate(
                    x -> {
                       return grService.getStudentNameById(x.getStudId()).toLowerCase().contains(grStudFltTxt.getText());
                    }
            );
            List<Grade> lst = new ArrayList<>(flt);
            grModel.setAll(lst);
        });

        hwFltTxt.textProperty().addListener((observableValue, s, t1) -> {
            initGrModel();
            FilteredList<Grade> flt = grModel.filtered(dtoGrade -> true);
            flt.setPredicate(
                    x -> {
                        try {
                            return x.getHwId().equals(Integer.parseInt(t1));
                        }catch (NumberFormatException e){
                            //hwFltTxt.setText(s);
                            return true;
                        }
                    }
            );
            List<Grade> lst = new ArrayList<>(flt);
            grModel.setAll(lst);
        });

        grFltTxt.textProperty().addListener((observableValue, s, t1) -> {
            initGrModel();
            FilteredList<Grade> flt = grModel.filtered(dtoGrade -> true);
            flt.setPredicate(
                    x -> {
                        try {
                            return x.getGrade() == (Double.parseDouble(t1));
                        }catch (NumberFormatException e){
                            //grFltTxt.setText(s);
                            return true;
                        }
                    }
            );
            List<Grade> lst = new ArrayList<>(flt);
            grModel.setAll(lst);
        });
    }

    @FXML
    private void grAddHandler(ActionEvent ae) throws IOException {
        Stage newStage = new Stage();
        //newStage.initStyle(StageStyle.TRANSPARENT);
        FXMLLoader mainLoader = new FXMLLoader();
        mainLoader.setLocation(getClass().getResource("../view/GradeAddForm.fxml"));
        AnchorPane mainLayout = (AnchorPane)mainLoader.load();
        GradeAddFormController gradeAddFormController = mainLoader.getController();
        gradeAddFormController.setServices(studService,hwService,grService);
        gradeAddFormController.setModel(grModel);
        gradeAddFormController.setLoggedAccount(this.loggedAccount);
        Scene mainScene = new Scene(mainLayout);
        //mainScene.setFill(Color.TRANSPARENT);
        newStage.setTitle("Grade add form");
        newStage.setScene(mainScene);
        newStage.show();
    }

    @FXML
    private void grUpdateHandler(ActionEvent ae) throws IOException {
        int index = grTable.getSelectionModel().getSelectedIndex();
        if(index < 0){
            showInfo("Please select a grade before updating it!");
            return;
        }
        Stage newStage = new Stage();
        //newStage.initStyle(StageStyle.TRANSPARENT);
        FXMLLoader mainLoader = new FXMLLoader();
        mainLoader.setLocation(getClass().getResource("../view/GradeUpdForm.fxml"));
        AnchorPane mainLayout = (AnchorPane)mainLoader.load();
        GradeUpdFormController gradeUpdFormController = mainLoader.getController();
        gradeUpdFormController.setService(grService);
        gradeUpdFormController.setModel(grModel);
        gradeUpdFormController.setTable(grTable);
        gradeUpdFormController.setLoggedAccount(loggedAccount);
        Scene mainScene = new Scene(mainLayout);
        //mainScene.setFill(Color.TRANSPARENT);
        newStage.setTitle("Grade update form");
        newStage.setScene(mainScene);
        newStage.show();
    }

    @FXML
    private void grDeleteHandler(ActionEvent ae){
        int index = grTable.getSelectionModel().getSelectedIndex();
        if(index < 0){
            showInfo("Please select a grade before deleting it!");
            return;
        }
        else{
            Grade selectedGrade = this.grTable.getSelectionModel().getSelectedItem();
            Grade retValue = this.deleteGrade(selectedGrade.getId());
            if(retValue == null){
                showInfo("There is no homework with the given id!");
            }
            else{
                showInfo("Grade was deleted successfully!");
                updateGradeTable();
            }
        }
    }

    /**
     * Function to update the grade table
     */
    private void updateGradeTable() {
        List<Grade> list = StreamSupport.stream(grService.getAllGradesSrv().spliterator(), false).collect(Collectors.toList());
        grModel.setAll(list);
    }

    @FXML
    private void reportHandler(ActionEvent ae) throws IOException {
        Stage newStage = new Stage();
        //newStage.initStyle(StageStyle.TRANSPARENT);
        FXMLLoader mainLoader = new FXMLLoader();
        mainLoader.setLocation(getClass().getResource("../view/ReportsView.fxml"));
        AnchorPane mainLayout = (AnchorPane)mainLoader.load();
        ReportsViewController reportsViewController = mainLoader.getController();
        reportsViewController.setService(grService);
        reportsViewController.setLogged(this.loggedAccount);
        Scene mainScene = new Scene(mainLayout);
        //mainScene.setFill(Color.TRANSPARENT);
        newStage.setTitle("Reports");
        newStage.setScene(mainScene);
        newStage.show();
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
    private void mouseEnteredRep(MouseEvent ne) {makeButtonGlow(reportBtn);}

    @FXML
    private void mouseExitedRep(MouseEvent ne) {stopButtonGlow(reportBtn);}

    @FXML
    private void logOutHandler(ActionEvent ae){
        notifyObservers(new GUIEvent(this));
    }


    @FXML
    private void hwAddHandler(ActionEvent ae) throws IOException {
        Stage newStage = new Stage();
        //newStage.initStyle(StageStyle.TRANSPARENT);
        FXMLLoader mainLoader = new FXMLLoader();
        mainLoader.setLocation(getClass().getResource("../view/HwAddForm.fxml"));
        AnchorPane mainLayout = (AnchorPane)mainLoader.load();
        HomeworkAddFormController homeworkAddFormController = mainLoader.getController();
        homeworkAddFormController.setService(hwService);
        homeworkAddFormController.setModel(hwModel);
        Scene mainScene = new Scene(mainLayout);
        //mainScene.setFill(Color.TRANSPARENT);
        newStage.setTitle("Homework Add Form");
        newStage.setScene(mainScene);
        newStage.show();
    }


    @FXML
    private void hwDeleteHandler(ActionEvent ae){
        int index = hwTable.getSelectionModel().getSelectedIndex();
        if(index < 0){
            showInfo("Please select a homework before deleting it!");
            return;
        }
        Homework selected = hwTable.getSelectionModel().getSelectedItem();
        Homework retValue = this.deleteHomework(selected.getId());
        if(retValue == null){
            showInfo("There is no homework with this id: " + selected.getId());
        }else{
            showInfo("Homework " + selected.getDescription() + "was deleted!");
            resetHwTable();
        }
    }

    /**
     * Function to reset homework table
     */
    private void resetHwTable() {
        hwModel.setAll(StreamSupport.stream(hwService.getAllHw().spliterator(), false).collect(Collectors.toList()));
    }

    @FXML
    private void hwUpdateHandler(ActionEvent ae) throws IOException {
        int index = hwTable.getSelectionModel().getSelectedIndex();
        if(index < 0){
            showInfo("Please select a homework before updating it!");
            return;
        }
        Stage newStage = new Stage();
        //newStage.initStyle(StageStyle.TRANSPARENT);
        FXMLLoader mainLoader = new FXMLLoader();
        mainLoader.setLocation(getClass().getResource("../view/HwUpdForm.fxml"));
        AnchorPane mainLayout = (AnchorPane)mainLoader.load();
        HomeworkUpdateFormController homeworkUpdateFormController = mainLoader.getController();
        homeworkUpdateFormController.setService(hwService);
        homeworkUpdateFormController.setModel(hwModel);
        homeworkUpdateFormController.setTable(hwTable);
        Scene mainScene = new Scene(mainLayout);
        //mainScene.setFill(Color.TRANSPARENT);
        newStage.setTitle("Homework Update Form");
        newStage.setScene(mainScene);
        newStage.show();
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
