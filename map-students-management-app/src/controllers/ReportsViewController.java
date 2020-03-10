package controllers;

import account.Account;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import service.GradeService;
import utils.PieChartTypeEnum;
import utils.RepTypes;
import utils.reportWriter.TextFilePdf;
import utils.reportWriter.TextFileWriter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReportsViewController {
    @FXML
    private RadioButton labAvgBtn;
    @FXML
    private RadioButton hardHwBtn;
    @FXML
    private RadioButton examStudBtn;
    @FXML
    private RadioButton grAvgBtn;
    @FXML
    private Button genRepBtn;
    @FXML
    private Button showMoreBtn;
    @FXML
    private ComboBox<RepTypes> repTypeCb;
    @FXML
    private PieChart pieChart;
    @FXML
    private ComboBox<PieChartTypeEnum> pieChartCb;
    @FXML
    private ImageView chartImage;
    @FXML
    private Button showLessBtn;


    private FileChooser fileChooser = new FileChooser();

    private GradeService gradeService;
    private Account logged;
    private ObservableList<PieChart.Data> studentsPie = FXCollections.observableArrayList();
    private ObservableList<PieChart.Data> groupGradesPie = FXCollections.observableArrayList();
    private ObservableList<PieChart.Data> homeworkPie = FXCollections.observableArrayList();

    /**
     * Constructor without parameters
     */
    public ReportsViewController(){}

    @FXML
    private void initialize(){
        repTypeCb.getItems().setAll(RepTypes.values());
        pieChartCb.getItems().setAll(PieChartTypeEnum.values());
        pieChartCb.getSelectionModel().selectedIndexProperty().addListener(
                (options, oldVal, newVal) -> {
                    if(newVal.equals(PieChartTypeEnum.Student_Average.ordinal())){
                        studentChart();
                    }else if(newVal.equals(PieChartTypeEnum.Homework_Average.ordinal())){
                        homeworkChart();
                    }else{
                        groupChart();
                    }
                }
        );
    }

    /**
     * Function to draw the homework chart
     */
    private void homeworkChart() {
        homeworkPie = FXCollections.observableArrayList();
        gradeService.hardestHomework().forEach((k, v) -> homeworkPie.add(new PieChart.Data(v.getDescription(),k)));
        this.updatePieChart("Homework average", homeworkPie);
    }

    /**
     * Function to draw the group chart
     */
    private void groupChart() {
        groupGradesPie = FXCollections.observableArrayList();
        gradeService.groupsAverage().forEach((k, v)-> groupGradesPie.add(new PieChart.Data(k.toString(), v)));
        this.updatePieChart("Group grades", groupGradesPie);
    }

    /**
     * Function to draw the student chart
     */
    private void studentChart() {
        studentsPie = FXCollections.observableArrayList();
        gradeService.studentsLabGrade().forEach((k, v) -> studentsPie.add(new PieChart.Data(k.getName(), v)));
        this.updatePieChart("Students average", studentsPie);
    }

    /**
     * Function to set the service for the current controller
     * @param gradeService - gradeService Object
     */
    public void setService(GradeService gradeService){
        this.gradeService = gradeService;
    }

    /**
     * Function which sets the currently connected account
     * @param logged - account object
     */
    public void setLogged(Account logged){
        this.logged = logged;
    }

    @FXML
    public void showMoreHandler(ActionEvent ae){
        showMoreBtn.setVisible(false);
        pieChartCb.setVisible(true);
        showLessBtn.setVisible(true);
    }

    @FXML
    public void showLessHandler(ActionEvent ae){
        pieChartCb.setVisible(false);
        showMoreBtn.setVisible(true);
        chartImage.setVisible(true);
        showLessBtn.setVisible(false);
        pieChart.setVisible(false);
    }

    @FXML
    private void genRepHandler(ActionEvent ae){
        int combination = 0;
        if(labAvgBtn.isSelected()){
            combination |= 1;
        }
        if(hardHwBtn.isSelected()){
            combination |= 2;
        }
        if(examStudBtn.isSelected()){
            combination |= 4;
        }
        if(grAvgBtn.isSelected()){
            combination |= 8;
        }


        if(combination == 0){
            showError("Please select a report first!");
            return;
        }

        int index = repTypeCb.getSelectionModel().getSelectedIndex();
        if(index < 0){
            showError("Please select a report type first!");
            return;
        }
        RepTypes selected = repTypeCb.getSelectionModel().getSelectedItem();
        if(selected.toString().equals("PDF")){
            String[] repTexts = getRepTexts(4);
            TextFilePdf pdfWriter = TextFilePdf.getInstance();
            ExecutorService executorService =
                    Executors.newSingleThreadExecutor();
            fileChooser.setTitle("Report File");
            Stage thisStage = (Stage)examStudBtn.getScene().getWindow();
            String filePath = fileChooser.showOpenDialog(thisStage).getAbsolutePath();
            int finalCombination = combination;
            executorService.execute(new Runnable() {
                public void run() {
                    pdfWriter.writeReport(filePath, repTexts, logged.getUsername(), finalCombination);
                }
            });
            executorService.shutdown();
        }else if(selected.toString().equals("TXT")){
            TextFileWriter tf = TextFileWriter.getInstance();
            String[] repTexts = getRepTexts(4);
            ExecutorService executorService =
                    Executors.newSingleThreadExecutor();
            int finalCombination;
            finalCombination = combination;
            fileChooser.setTitle("Report File");
            Stage thisStage;
            thisStage = (Stage)examStudBtn.getScene().getWindow();
            String filePath = fileChooser.showOpenDialog(thisStage).getAbsolutePath();
            executorService.execute(new Runnable() {
                public void run() {
                    tf.writeReport(filePath, repTexts, logged.getUsername(), finalCombination);
                }
            });
            executorService.shutdown();
        }
    }

    /**
     * Function which returns the report texts
     * @return - String[] - representing the report texts
     */
    private String[] getRepTexts(Integer numReports){
        String[] result = new String[numReports];
        StringBuilder studAverage = new StringBuilder();
        studAverage.append("Students Average\n");
        gradeService.studentsLabGrade().forEach((k, v) -> studAverage.append(k).append(" --> ").append(v).append("\n"));
        result[0] = studAverage.toString();

        StringBuilder homework = new StringBuilder();
        homework.append("Task Ordering\n");
        gradeService.hardestHomework().forEach((k, v) -> homework.append(v).append("-->").append(k).append("\n"));
        result[1] = homework.toString();

        StringBuilder studentsName = new StringBuilder();
        studentsName.append("Students taking the exam\n");
        gradeService.canTakeExam().forEach(k -> studentsName.append(k).append("\n"));
        result[2] = studentsName.toString();

        StringBuilder grAverage = new StringBuilder();
        grAverage.append("Average grade per group\n");
        gradeService.groupsAverage().forEach((k, v) -> grAverage.append("Gr: ").append(k).append("-->").append(v).append("\n"));
        result[3] = grAverage.toString();

        return result;
    }

    public void updatePieChart(String title, ObservableList<PieChart.Data> list){
        if(chartImage.isVisible()) {
            chartImage.setVisible(false);
        }
        if(!pieChart.isVisible()){
            pieChart.setVisible(true);
        }
        pieChart.setTitle(title);
        pieChart.setOpacity(1);
        pieChart.getData().clear();

        double angle = Math.random()*360;
        pieChart.setStartAngle(angle);

        pieChart.getData().addAll(list);

        pieChart.setLabelsVisible(true);

        //TODO make white labels
        pieChart.setLegendSide(Side.BOTTOM);
        pieChart.setLegendVisible(true);

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
}


