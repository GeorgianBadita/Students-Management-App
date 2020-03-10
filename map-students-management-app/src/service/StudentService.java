package service;

import domain.entities.Student;
import domain.validators.IllegalArgumentException;
import domain.validators.ValidatorException;
import repository.AbstractRepository;
import repository.StudentXMLRepository;
import utils.Observable;
import utils.Observer;
import utils.PossibleOperations;
import utils.StudentEvent;

import java.util.ArrayList;
import java.util.List;

public class StudentService implements Observable<StudentEvent> {

    private AbstractRepository<Student, Integer> repo;


    /**
     * Constructor for StudentService Class
     * @param r - repository of students
     */
    public StudentService(AbstractRepository<Student, Integer> r){
        this.repo = (StudentXMLRepository) r;
    }

    /**
     * Function which adds a student to the repository
     * @param st - Student to be added
     * @return null if the student was saved, the given Student otherwise
     */
    public Student addStudent(Student st) throws ValidatorException, IllegalArgumentException {
        Student val = this.repo.save(st);
        if(val == null) {
            notifyObservers(new StudentEvent(PossibleOperations.ADD, st));
        }
        return val;
    }

    /**
     * Function to delete a student from the repository
     * @param id: Integer - id of the students we want to delete
     * @throws IllegalArgumentException - if the give id is null
     * @return the deleted student if it exists, null otherwise
     */
    public Student deleteStudent(Integer id) throws IllegalArgumentException {
        Student val = repo.delete(id);
        notifyObservers(new StudentEvent(PossibleOperations.DELETE, val));
        return val;
    }

    /**
     * Function which updates a student
     * @param newSt - the new student
     * @return - null if the Student is updated, the Student therwise
     */
    public Student updateStudent(Student newSt) throws ValidatorException, IllegalArgumentException {
        Student val = repo.update(newSt);
        if(val == null) {
            notifyObservers(new StudentEvent(PossibleOperations.UPDATE, newSt));
        }
        return val;
    }

    /**
     * @param id - id of the student to be searched
     * @throws IllegalArgumentException - if the give id is null
     * @return a student if it exists, null otherwise
     */
    public Student findStudent(Integer id) throws IllegalArgumentException {
        return this.repo.findOne(id);
    }

    /**
     * Function which returns an Iterable containing all the students from repository
     * @return Iterable containing all the students from the repository
     */
    public Iterable<Student> getAllSt(){
        return this.repo.findAll();
    }

    /**
     * Function to get the number of students from the repository
     * @return -number of students from the repository
     */
    public Integer getStudentDim(){
        return this.repo.size();
    }


    private List<Observer<StudentEvent>> observers = new ArrayList<>();

    /***
     * Function for adding an observer
     * @param e: -observer to be added
     */
    @Override
    public void addObserver(Observer<StudentEvent> e) {
        this.observers.add(e);
    }

    /**
     * Function to remove an observer
     * @param e - observer to be removed
     */
    @Override
    public void removeObserver(Observer<StudentEvent> e) {
        this.observers.remove(e);
    }

    /**
     * Function which notifies all observers from the list
     * @param t - item to be updated
     */
    @Override
    public void notifyObservers(StudentEvent t) {
        this.observers.forEach(x -> x.update(t));
    }

    /**
     * Function to retrieve the id of the student
     * @param userName -username of the student
     * @return the id of the student if it exists, null otherwise
     */
    public Integer getStudentByName(String userName){
        for(Student st : this.getAllSt()){
            if(st.getName().equals(userName)){
                return st.getId();
            }
        }
        return null;
    }

    /**
     * Function to get a student by userName
     * @param userName - function to get the account by the userName
     * @return the student by its name if it exists, null otherwise
     */
    public Student getStudentByUserName(String userName){
        Integer id = this.getStudentByName(userName);
        if(id == null){
            return null;
        }
        return this.findStudent(id);
    }

}

