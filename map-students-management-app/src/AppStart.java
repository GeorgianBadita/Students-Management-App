import account.AccountXMLRepository;
import controllers.LogInController;
import domain.validators.AccountValidator;
import domain.validators.GradeValidator;
import domain.validators.HomeworkValidator;
import domain.validators.StudentValidator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import repository.GradeXMLRepository;
import repository.HomeworkXMLRepository;
import repository.StudentXMLRepository;
import service.AccountService;
import service.GradeService;
import service.HomeworkService;
import service.StudentService;

import java.io.IOException;

public class AppStart extends Application {

    private AccountService accServ;
    private StudentService studServ;
    private HomeworkService hwServ;
    private GradeService grServ;

    @Override
    public void start(Stage stage) throws Exception {
        AccountXMLRepository acManager = new AccountXMLRepository(new AccountValidator(),
                "src/account/accountXMLFiles/accounts.xml");
        StudentXMLRepository studRepo = new StudentXMLRepository(new StudentValidator(),
                "src/repository/RepoXMLFiles/Students.xml");
        HomeworkXMLRepository hwRepo = new HomeworkXMLRepository(new HomeworkValidator(),
                "src/repository/RepoXMLFiles/Homework.xml");
        GradeXMLRepository grRepo = new GradeXMLRepository(new GradeValidator(),
                "src/repository/RepoXMLFiles/Grades.xml");
        grServ = new GradeService(studRepo, hwRepo, grRepo);
        studServ = new StudentService(studRepo);
        hwServ = new HomeworkService(hwRepo);
        accServ = new AccountService(acManager);
        /*List<String> profNames = new ArrayList<>();
        Random rand = new Random();
        for(Account acc : accServ.getAllAccounts()){
            if(acc.getAccType().equals(AccountType.PROFESSOR)){
                profNames.add(acc.getUsername());
            }
        }
        for(Student st : studServ.getAllSt()){
            for(Homework hw : hwServ.getAllHw()){
                int pox = rand.nextInt(profNames.size());
                String profName = profNames.get(pox);
                Double grade = rand.nextDouble()*9.0 + 1.0;
                String feedBack = PasswordUtils.getSalt(rand.nextInt(50));
                System.out.println(grade);
                Grade toAdd = new Grade(st.getId(), hw.getId(), grade, profName, feedBack);
                grServ.addGrade(toAdd, true);
            }
        }
        */
        initLogIn(stage);
    }


    private void initLogIn(Stage stage) throws IOException {
        FXMLLoader logInLoader = new FXMLLoader();
        logInLoader.setLocation(getClass().getResource("view/LogInView.fxml"));
        AnchorPane logInLayout = (AnchorPane) logInLoader.load();
        LogInController logInController = logInLoader.getController();
        logInController.setServices(accServ, studServ, hwServ, grServ);
        Scene logInScene = new Scene(logInLayout);
        stage.setTitle("Log In");
        stage.setScene(logInScene);
        stage.show();
    }
}
