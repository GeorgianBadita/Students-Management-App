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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.lang.Math.abs;

public class HomeworkAddFormController {
    @FXML
    private TextField hwIdTxt;
    @FXML
    private TextField hwDescTxt;
    @FXML
    private DatePicker hwRecWDate;
    @FXML
    private DatePicker hwDWDate;
    @FXML
    private Button addHwBtn;

    private HomeworkService hwService;

    private ObservableList<Homework> obs = FXCollections.observableArrayList();

    /**
     * Constructor without parameters
     */
    public HomeworkAddFormController(){

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
     * Function which disables all days which aren't a tuesday in a datepicker
     * @param date - datepicker
     */
    public static void disableDatePicker(DatePicker date){
        date.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.getDayOfWeek() != DayOfWeek.TUESDAY);
            }
        });
        date.setEditable(false);
    }

    @FXML
    public void initialize(){
       disableDatePicker(hwDWDate);
       disableDatePicker(hwRecWDate);

    }

    /**
     * Function to add a new homework
     * @param id - id of the homework to be added
     * @param desc - description of the homework
     * @param recWeek - receiving week
     * @param deadWeek - deadline week
     * @return - null if the homework was added, a homework otherwise
     */
    private Homework addHomework(Integer id, String desc, Integer recWeek, Integer deadWeek) throws ValidatorException {
        return this.hwService.addHomework(new Homework(id, desc, recWeek, deadWeek));
    }

    /**
     * Function to add a homework
     * @param ae - actionEvent
     */
    @FXML
    private void homeworkAddHandler(ActionEvent ae){
        String idStr = hwIdTxt.getText();
        String desc = hwDescTxt.getText();
        if(hwRecWDate.getValue() == null || hwDWDate.getValue() == null){
            showInfo("Please select dates for the homework!");
            return;
        }
        Date recDate = Utility.convertLocalDateTodate(hwRecWDate.getValue());
        Date deadDate = Utility.convertLocalDateTodate(hwDWDate.getValue());
        Long receivingWeek = numWeeks("01 10 2018", recDate);
        Long deadlineWeek = numWeeks("01 10 2018", deadDate);
        if(idStr.isEmpty() || desc.isEmpty() || receivingWeek == null || deadlineWeek == null){
            showInfo("Sorry, Incorrect input!");
        }

        try{
            Integer id = Integer.parseInt(idStr);
            Homework retValue = this.addHomework(id, desc, Math.toIntExact(receivingWeek), Math.toIntExact(deadlineWeek));
            if(retValue != null){
                showInfo("Homework already exists!");
                return;
            }
            showInfo("Homework added successfully!");
            obs.setAll(StreamSupport.stream(hwService.getAllHw().spliterator(), false).collect(Collectors.toList()));
            closeStage();
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
        Stage toClose = (Stage) this.addHwBtn.getScene().getWindow();
        toClose.close();
    }

    /**
     * Function to get the number of weeks between two dates
     * @param s - string date
     * @param deadDate - date
     * @return - number of weeks between the two dates
     */
    public static Long numWeeks(String s, Date deadDate) {
        SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy");
        try {
            Date yearStart =  myFormat.parse(s);
            return abs(Utility.numWeeksBetweenTwoDates(yearStart, deadDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
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
