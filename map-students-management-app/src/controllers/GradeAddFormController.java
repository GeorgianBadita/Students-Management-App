package controllers;

import account.Account;
import domain.entities.Grade;
import domain.entities.Homework;
import domain.entities.Student;
import domain.validators.ValidatorException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import service.GradeService;
import service.HomeworkService;
import service.StudentService;
import utils.Utility;
import utils.mail.GoogleMail;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GradeAddFormController {
    @FXML
    private TableView<Homework> hwTable;
    @FXML
    private TableColumn<Homework, Integer> hwIdCol;
    @FXML
    private TableColumn<Homework, String> hwDescCol;
    @FXML
    private Button addGradeBtn;
    @FXML
    private TextArea feedBackArea;
    @FXML
    private TextField gradeValTxt;
    @FXML
    private RadioButton mailSendBtn;
    @FXML
    private TextField studAutoTxt;
    @FXML
    private RadioButton hasReasonBtn;

    private ObservableList<Grade> grModel = FXCollections.observableArrayList();
    private ObservableList<Student> stModel = FXCollections.observableArrayList();
    private ObservableList<Homework> hwModel = FXCollections.observableArrayList();
    private StudentService stService;
    private HomeworkService hwService;
    private GradeService grService;
    private Account loggedAccount;

    /**
     * Constructor without parameters
     */
    public GradeAddFormController() {

    }

    @FXML
    private void initialize() {
        initHwTable();
    }


    private List<String> getAllStudentsNames() {
        List<String> result = new ArrayList<>();
        for(Student st : stService.getAllSt()){
            result.add(st.getName());
        }
        return result;
    }

    /**
     * Initialize homework table
     */
    private void initHwTable() {
        hwIdCol.setCellValueFactory(new PropertyValueFactory<Homework, Integer>("id"));
        hwDescCol.setCellValueFactory(new PropertyValueFactory<Homework, String>("description"));
        hwTable.setItems(hwModel);
    }



    /**
     * Function to set the services
     * @param studentService - student service
     * @param homeworkService - homework service
     * @param gradeService - grade service
     */
    public void setServices(StudentService studentService, HomeworkService homeworkService, GradeService gradeService){
        this.hwService = homeworkService;
        this.stService = studentService;
        this.grService = gradeService;
        initStModel();
        initHwModel();
        List<String> lst = getAllStudentsNames();
        //TextFields.bindAutoCompletion(studAutoTxt, lst);
    }

    /**
     * Function to initialize student model
     */
    private void initStModel() {
        List<Student> lst = StreamSupport.stream(this.stService.getAllSt().spliterator(), false).collect(Collectors.toList());;
        stModel.setAll(lst);
    }

    /**
     * Functiont to add a grade
     * @param stId - Student id
     * @param hwId - Homework id
     * @param gradeVal - Value of the grade
     * @param prof - Professor's name
     * @param feedBack - Feedback
     * @param flag - flag
     * @return null if the grade was added, a grade otherwise
     */
    private Grade addGrade(Integer stId, Integer hwId, Double gradeVal, String prof, String feedBack, boolean flag) throws ValidatorException {
        return grService.addGrade(new Grade(stId, hwId, gradeVal, prof, feedBack), flag);
    }

    /**
     * Function to initialize homework model
     */
    private void initHwModel() {
        List<Homework> lst = StreamSupport.stream(this.hwService.getAllHw().spliterator(), false).collect(Collectors.toList());
        hwModel.setAll(lst);
    }

    @FXML
    private void gradeAddHandler(ActionEvent ae){
        String stName = studAutoTxt.getText();
        if(stName.isEmpty()){
            showInfo("Please select a student first!");
            return;
        }
        Integer stId = grService.getStudentIdByName(stName);
        if(stId == null){
            showInfo("There is no student named " + stName);
            return;
        }
        Student selectedSt = grService.getStudentById(stId);
        Homework selectedHw = hwTable.getSelectionModel().getSelectedItem();
        Integer idSt = selectedSt.getId();
        Integer idHw = selectedHw.getId();

        String feedBack = feedBackArea.getText();
        if(feedBack.isEmpty()){
            showInfo("Please give a feedback for the grade!");
            return;
        }
        String grade = gradeValTxt.getText();
        if(grade.isEmpty()){
            showInfo("Please give a grade value!");
            return;
        }

        Alert message = new Alert(Alert.AlertType.CONFIRMATION);
        message.setTitle("Grade confirmation!");
        message.setHeaderText("Are you sure you want to add this grade?");
        message.setContentText("Grade: " + grade + " Name: " + selectedSt.getName() + " Hw: " + selectedHw.getDescription());
        message.showAndWait().filter(ButtonType.OK::equals).ifPresent(b-> {
            try {
                boolean flag = false;
                if(hasReasonBtn.isSelected()){
                    flag = true;
                }
                Grade retValue = this.addGrade(idSt, idHw, Double.parseDouble(grade), loggedAccount.getUsername(), feedBack, flag);
                if(retValue != null){
                    showInfo("Grade already exists!");
                }else{
                    if(mailSendBtn.isSelected()){
                        mailHandler(selectedSt);
                    }
                    showInfo("Grade was added successfully!");
                    updateGradeTable();
                }
            } catch (NumberFormatException e){
                showInfo("Grade value must be a double!");
            } catch (ValidatorException | IllegalArgumentException e) {
                showInfo("Invalid parameters!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Function to send a mail
     * @param st - student to send mail to
     */
    private void mailHandler(Student st) throws IOException {
        String toSend = Utility.getFileContent("src/student_files" + st.getName() + ".txt");
        ExecutorService executorService =
                Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                GoogleMail.Send("mtest2469", "testmail123", st.getEmail(), "Grade notification!", toSend);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        });
        executorService.shutdown();
    }


    /**
     * Function to close the stage
     */
    private void closeStage() {
        Stage toClose = (Stage)this.addGradeBtn.getScene().getWindow();
        toClose.close();
    }

    /**
     * Function to set the grade model
     * @param grModel
     */
    public void setModel(ObservableList<Grade> grModel) {
        this.grModel = grModel;
    }

    /**
     * Function to set the logged account
     * @param loggedAccount - Account object
     */
    public void setLoggedAccount(Account loggedAccount){
        this.loggedAccount = loggedAccount;
    }

    /**
     * Function to update the grade table
     */
    public void updateGradeTable(){
        grModel.setAll(StreamSupport.stream(grService.getAllGradesSrv().spliterator(), false).collect(Collectors.toList()));
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
