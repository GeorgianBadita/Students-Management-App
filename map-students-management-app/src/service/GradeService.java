package service;

import domain.entities.Grade;
import domain.entities.Homework;
import domain.entities.Student;
import domain.validators.IllegalArgumentException;
import domain.validators.NoStudHwException;
import domain.validators.ValidatorException;
import javafx.util.Pair;
import repository.AbstractRepository;
import repository.GradeXMLRepository;
import repository.HomeworkXMLRepository;
import repository.StudentXMLRepository;
import utils.*;
import utils.Observable;
import utils.Observer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.function.Predicate;

public class GradeService implements Observable<GradeEvent> {

    private AbstractRepository<Homework, Integer> hwRepo;
    private AbstractRepository<Student, Integer> studRepo;
    private AbstractRepository<Grade, Pair<Integer, Integer>> grRepo;
    private Predicate<Homework> nullHomework = Objects::isNull;
    private Predicate<Student> nullStudent = Objects::isNull;
    private Predicate<Grade> nullGrade = Objects::isNull;


    /**
     * GradeService constructor
     * @param sRepo - Student repository
     * @param hRepo - Homework repository
     * @param gRepo - Grade repository
     */
    public GradeService(AbstractRepository<Student, Integer> sRepo,
                        AbstractRepository<Homework, Integer> hRepo,
                        AbstractRepository<Grade, Pair<Integer, Integer>> gRepo){
        this.hwRepo = (HomeworkXMLRepository)hRepo;
        this.grRepo = (GradeXMLRepository)gRepo;
        this.studRepo = (StudentXMLRepository)sRepo;
    }


    /**
     * Fucntion to a add a grade
     * @param gr - grade to be added
     * @param flag - reason flag
     * @return - null if the grade was added, the given grade otherwise
     * @throws ValidatorException
     */
    public Grade addGrade(Grade gr, boolean flag) throws ValidatorException {
        double grade = gr.getGrade();
        Grade cmp = this.grRepo.findOne(new Pair<>(gr.getStudId(), gr.getHwId()));
        if(nullGrade.negate().test(cmp)){
            return gr;
        }

        Student stFound = this.studRepo.findOne(gr.getStudId());
        Homework hwFound = this.hwRepo.findOne(gr.getHwId());

        if(nullStudent.test(stFound) || nullHomework.test(hwFound)){
            throw new NoStudHwException("There is no Student or no Homework with given ids!");
        }

        if(!flag) {
            int diff = hwFound.getDeadlineWeek() - Utility.getCurrWeek();
            if (diff == -1 || diff == -2) {
                grade += (2.5d * diff);
            } else if (diff < -2) {
                grade = 1.0d;
            }
        }
        gr.setGrade(grade);

        String studFilesPath = "/home/geo/Desktop/Newlab3/lab3/src/student_files";
        String toWrite = "Tema: " + gr.getHwId();
        toWrite += "\nNota: " + grade;
        toWrite += "\nPredata in saptamana: " + Utility.getCurrWeek();
        toWrite += "\nDeadline: " + hwFound.getDeadlineWeek();
        toWrite += "\nFeedback: " + gr.getFeedback();

        try(PrintWriter pw = new PrintWriter(new FileWriter(studFilesPath + "/"+stFound.getName() + ".txt", true))) {
            pw.append(toWrite).append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Grade retVal =  this.grRepo.save(gr);
        if(retVal == null){
            notifyObservers(new GradeEvent(PossibleOperations.ADD, gr));
        }
        return retVal;
    }

    /**
     * Function which deletes a grade
     * @param id - id of the grade to be deleted
     * @return - null if the grade does not exists, the deleted grade otherwise
     * @throws IllegalArgumentException - if the id is null
     */

    public Grade deleteGrade(Pair<Integer, Integer> id) throws IllegalArgumentException {

        return this.grRepo.delete(id);
    }

    /**
     * Function which updates a grade
     * @param gr - new Grade
     * @return - null if the grade was updated, the given grade otherwise
     * @throws ValidatorException if grade parameters are incorrect
     * @throws IllegalArgumentException - if grade is null
     */
    public Grade updateGrade(Grade gr) throws ValidatorException, IllegalArgumentException {
        Grade retVal = this.grRepo.update(gr);
        if(retVal == null){
            notifyObservers(new GradeEvent(PossibleOperations.UPDATE, gr));
        }
        return retVal;
    }

    /**
     * Function to get all grades from file
     */
    public Iterable<Grade> getAllGradesSrv(){
        return this.grRepo.findAll();
    }


    /**
     *  Function to get Student's names by their id
     * @param id - id of the searched student
     * @return - Empty string if there is no student with that id, the students' name otherwise
     * @throws IllegalArgumentException - If id is null
     */
    public String getStudentNameById(Integer id) throws IllegalArgumentException {
        Student searched = this.studRepo.findOne(id);
        if (searched == null){
            return "";
        } else{
            return searched.getName();
        }
    }

    /**
     *  Function to get Student by their id
     * @param id - id of the searched student
     * @return - null if there is no student with that id, the students' name otherwise
     * @throws IllegalArgumentException - If id is null
     */
    public Student getStudentById(Integer id) throws IllegalArgumentException {
        return this.studRepo.findOne(id);
    }

    /**
     *  Function to get Student's Id by their name
     * @param name - name of the searched student
     * @return - Null if there is no student with that name, the student's id otherwise
     */
    public Integer getStudentIdByName(String name){
        Iterable<Student> all = this.studRepo.findAll();
        for (Student currentSt : all) {
            if(currentSt.getName().equals(name)){
                return currentSt.getId();
            }
        }
        return null;
    }

    /**
     * Function which returns a DTO containing a student and its grades
     * @param studId - id of the student
     * @throws IllegalArgumentException - if studId is null
     * @return - null if there is no student with the given id, a DTOGrade otherwise
     */
    public List<DTOGrade> getStudentGradesById(Integer studId) throws IllegalArgumentException {
        Student st = this.studRepo.findOne(studId);
        if(st == null){
            return null;
        }
        List<DTOGrade> gradeLst = new ArrayList<>();
        for(Grade gr : this.grRepo.findAll()){
            if(gr.getStudId().equals(studId)){
                Homework hw = hwRepo.findOne(gr.getHwId());
                DTOGrade dtoGrade = new DTOGrade(gr.getGrade(), hw.getDescription(), hw.getId(), gr.getProfName(), gr.getFeedback());
                gradeLst.add(dtoGrade);
            }
        }
        return gradeLst;
    }

    private List<Observer<GradeEvent>> observers = new ArrayList<>();
    @Override
    public void addObserver(Observer<GradeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<GradeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(GradeEvent t) {
        observers.forEach(x -> x.update(t));
    }

    /**
     * Function to delete all grades of a student
     * @param stId - if of the student whose grades we want to delete
     */
    public void deleteGradesOfStudent(Integer stId) {
        List<Pair<Integer, Integer>> gradesToDelete = new ArrayList<>();
        Iterable<Grade> lst = this.getAllGradesSrv();
        for(Grade gr : lst){
            if(gr.getStudId().equals(stId)){
                gradesToDelete.add(gr.getId());
            }
        }
      for(Pair<Integer, Integer> id : gradesToDelete){
          this.deleteGrade(id);
      }
    }

    public Map<Student, Double> studentsLabGrade(){
        Map<Student, Double> note = new HashMap<>();
        for(Student st : studRepo.findAll()){
            Double medie = 0.0;
            Integer numGrades = 0;
            for(Grade gr : grRepo.findAll()){
                if(gr.getStudId().equals(st.getId())){
                    medie += gr.getGrade();
                    numGrades ++;
                }
            }
            if(numGrades > 0){
                medie = medie/numGrades;
            }else{
                medie = 0.0;
            }
            note.put(st, medie);
        }

        return note;
    }

    /**
     * Function which returns the list with students which can participate in exam
     * @return
     */
    public List<Student> canTakeExam(){
        List<Student> canTake = new ArrayList<>();
        for(Student st : this.studRepo.findAll()){
            Double gradeSum;
            gradeSum = 0.0;
            Integer numGrades;
            numGrades = 0;
            for(Grade gr : grRepo.findAll()){
                if(gr.getStudId().equals(st.getId())){
                    gradeSum += gr.getGrade();
                    numGrades ++;
                }
            }
            if(numGrades > 0 && 4.0 <= gradeSum / numGrades){
                canTake.add(st);
            }
        }
        return canTake;
    }

    /**
     * Function which calculates the hardest homework
     * @return - map containing every homework and its average
     */
    public Map<Double, Homework> hardestHomework(){
        Map<Double, Homework> grades = new TreeMap<Double, Homework>();
        for(Homework hw : hwRepo.findAll()){
            Double sum = 0.0;
            Integer numGrades = 0;
            for(Grade gr : grRepo.findAll()){
                if(hw.getId().equals(gr.getHwId())){
                    sum += gr.getGrade();
                    numGrades ++;
                }
            }
            if(numGrades > 0){
                grades.put(sum/numGrades, hw);
            }else{
                grades.put(0.0, hw);
            }
        }
        return grades;
    }

    /**
     * Function which calculates the average for all groups
     * @return
     */
    public Map<Integer, Double> groupsAverage(){
        Map<Integer, Double> groupsAvg = new HashMap<>();
        for(Groups gr : Groups.values()){
            Integer group = Integer.parseInt(gr.toString().substring(2));
            Double sum = 0.0;
            Integer numGrades = 0;
            for(Grade grVal : grRepo.findAll()){
                Student st = getStudentById(grVal.getStudId());
                if(st.getGroup().equals(group)){
                    sum += grVal.getGrade();
                    numGrades ++;
                }
            }
            if(numGrades > 0){
                groupsAvg.put(group, sum/numGrades);
            }else{
                groupsAvg.put(group, 0.0);
            }
        }
        return groupsAvg;
    }
}
