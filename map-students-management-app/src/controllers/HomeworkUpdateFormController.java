package controllers;

import domain.entities.Homework;
import domain.validators.ValidatorException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import service.HomeworkService;
import utils.Utility;

import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static controllers.HomeworkAddFormController.disableDatePicker;
import static controllers.HomeworkAddFormController.numWeeks;

public class HomeworkUpdateFormController {
    @FXML
    private TextField hwDescTxt;
    @FXML
    private DatePicker hwRecWDate;
    @FXML
    private DatePicker hwDWDate;
    @FXML
    private Button updHwBtn;

    private HomeworkService hwService;

    private ObservableList<Homework> obs = FXCollections.observableArrayList();

    private TableView<Homework> hwTable;
    /**
     * Constructor without parameters
     */
    public HomeworkUpdateFormController(){

    }

    /**
     * Function to set the homework Service
     * @param hService - homework Service
     */
    public void setService(HomeworkService hService){
        this.hwService = hService;
    }

    /**
     * Function to set the table's model
     * @param obs - ObservableList<Homework> object
     */
    public void setModel(ObservableList<Homework> obs){
        this.obs = obs;
    }

    /**
     * Function to set the homework table
     * @param tbl - homework table
     */
    public void setTable(TableView<Homework> tbl){
        this.hwTable = tbl;
    }

    @FXML
    public void initialize(){
        disableDatePicker(hwDWDate);
        disableDatePicker(hwRecWDate);

    }

    private Homework updateHomework(Integer id, String desc, Integer recvWeek, Integer deadWeek) throws ValidatorException {
       return this.hwService.updateHomework(new Homework(id, desc, recvWeek, deadWeek));
    }

    /**
     * Function to add a homework
     * @param ae - actionEvent
     */
    @FXML
    private void homeworkUpdateHandler(ActionEvent ae){
        String desc = hwDescTxt.getText();
        Homework selected = this.hwTable.getSelectionModel().getSelectedItem();
        if(hwRecWDate.getValue() == null || hwDWDate.getValue() == null){
            showInfo("Please select dates for the homework!");
            return;
        }
        Date recDate = Utility.convertLocalDateTodate(hwRecWDate.getValue());
        Date deadDate;
        deadDate = Utility.convertLocalDateTodate(hwDWDate.getValue());
        Long receivingWeek = numWeeks("01 10 2018", recDate);
        Long deadlineWeek = numWeeks("01 10 2018", deadDate);
        if(desc.isEmpty() || receivingWeek == null || deadlineWeek == null){
            showInfo("Sorry, Incorrect input!");
        }

        try{
            Homework retValue = this.updateHomework(selected.getId(), desc, Math.toIntExact(receivingWeek), Math.toIntExact(deadlineWeek));
            if(retValue != null){
                showInfo("Homework already exists!");
                return;
            }
            showInfo("Homework updated successfully!");
            closeStage();
            updateObs();
        }catch(NumberFormatException ex){
            showInfo("Id must be integer!");
        } catch (ValidatorException e) {
            showInfo("Incorrect parameters!");
        }
    }

    /**
     * Function to close the current stage
     */
    private void closeStage() {
        Stage toClose = (Stage)this.updHwBtn.getScene().getWindow();
        toClose.close();
    }

    /**
     * Function to update the table
     */
    private void updateObs() {
        obs.setAll(StreamSupport.stream(hwService.getAllHw().spliterator(), false).collect(Collectors.toList()));
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
