package controllers;

import account.Account;
import domain.entities.Grade;
import domain.validators.ValidatorException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import service.GradeService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GradeUpdFormController {
    @FXML
    private TextField gradeTxt;
    @FXML
    private TextField feedBackTxt;
    @FXML
    private Button updGrBtn;

    private GradeService grService;
    private ObservableList<Grade> grModel = FXCollections.observableArrayList();
    private TableView<Grade> grTable;
    private Account logged;

    /**
     * Constructor without parameters
     */
    public GradeUpdFormController(){

    }

    /**
     * FUnction to set the grade service for the controller
     * @param grService - GradeService object
     */
    public void setService(GradeService grService){
        this.grService = grService;
    }

    /**
     * Function to set the model for the current controller
     * @param obs - observable List
     */
    public void setModel(ObservableList<Grade> obs){
        this.grModel = obs;
    }

    /**
     * Function to set the table for the current controller
     * @param grTable - grade table
     */
    public void setTable(TableView<Grade> grTable){
        this.grTable = grTable;
    }

    /**
     * Function to set the logged account
     * @param loggedAccount - currently logged account
     */
    public void setLoggedAccount(Account loggedAccount){
        this.logged = loggedAccount;
    }

    @FXML
    private void initialize(){

    }

    /**
     * Function to update a grade
     * @param stId - student id
     * @param hwId - homework id
     * @param grValue - value of the grade
     * @param prof - professor name
     * @param feedBack - feedback for the given grade
     * @return - null if the grade was updated, the given grade otherwise
     */
    private Grade updateGrade(Integer stId, Integer hwId, Double grValue, String prof, String feedBack) throws ValidatorException {
        return this.grService.updateGrade(new Grade(stId, hwId, grValue, prof, feedBack));
    }


    @FXML
    private void gradeUpdateHandler(ActionEvent ae){
        String grString = gradeTxt.getText();
        if(grString.isEmpty()){
            showInfo("Please give the new grade!");
        }
        String feedBack = feedBackTxt.getText();
        if(feedBack.isEmpty()){
            showInfo("Please give the new feedback!");
        }
        try {
            Double gradeValue = Double.parseDouble(grString);
            Grade selected = this.grTable.getSelectionModel().getSelectedItem();
            Grade retVal = this.updateGrade(selected.getStudId(), selected.getHwId(), gradeValue, logged.getUsername() ,feedBack);
            if(retVal != null){
                showInfo("Couldn't update the grade!");
            }else{
                showInfo("Grade was updated successfully!");
                updateGradeTable();
            }
        }catch (NumberFormatException e){
            showInfo("Grade value must be a double!");
        } catch (ValidatorException e) {
            showInfo("Incorrect parameters!");
        }
    }

    /**
     * Function to update the grade table
     */
    private void updateGradeTable() {
        List<Grade> list = StreamSupport.stream(grService.getAllGradesSrv().spliterator(), false).collect(Collectors.toList());
        grModel.setAll(list);
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
