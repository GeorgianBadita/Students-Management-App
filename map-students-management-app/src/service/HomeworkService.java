package service;

import domain.entities.Homework;
import domain.validators.IllegalArgumentException;
import domain.validators.ValidatorException;
import repository.AbstractRepository;
import repository.HomeworkXMLRepository;
import utils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class HomeworkService implements Observable<HomeworkEvent> {


    private AbstractRepository<Homework, Integer> repo;
    private Predicate<Homework> nullHomework = Objects::isNull;


    /**
     * Constructor for HomeworkService Class
     * @param r - repository of homework
     */
    public HomeworkService(AbstractRepository<Homework, Integer> r){
        this.repo = (HomeworkXMLRepository) r;
    }

    /**
     * Function which adds a homework
     * @param hw - homework to be added
     * @return - null if the homework was added, the homework otherwise
     * @throws ValidatorException - if homework attributes are incorrect
     * @throws IllegalArgumentException - if homework is null
     */
    public Homework addHomework(Homework hw) throws ValidatorException, IllegalArgumentException {
        Homework val =  this.repo.save(hw);
        if(val == null){
            notifyObservers(new HomeworkEvent(PossibleOperations.ADD, hw));
        }
        return val;
    }

    public Homework updateHomework(Homework hw) throws ValidatorException, IllegalArgumentException {
        Homework val = this.repo.update(hw);
        if (val == null){
            notifyObservers(new HomeworkEvent(PossibleOperations.UPDATE, hw));
        }
        return val;
    }

    /**
     * Function to delete a homework from the repository
     * @param id : Integer - id of the homework we want to delete
     * @throws IllegalArgumentException - if the give id is null
     * @return the deleted homework if it exists, null otherwise
     */
    public Homework deleteHomework(Integer id) throws IllegalArgumentException {
        Homework val =  repo.delete(id);
        notifyObservers(new HomeworkEvent(PossibleOperations.DELETE, val));
        return val;
    }

    /**
     * @param id - id of the homework to be searched
     * @throws IllegalArgumentException - if the give id is null
     * @return a homework if it exists, null otherwise
     */
    public Homework findHomework(Integer id) throws IllegalArgumentException {
        return this.repo.findOne(id);
    }

    /**
     * Function which returns an Iterable containing all the homeworks from repository
     * @return Iterable containing all the homework from the repository
     */
    public Iterable<Homework> getAllHw(){
        return this.repo.findAll();
    }

    /**
     * Function to get the number of homework from the repository
     * @return -number of homework from the repository
     */
    public Integer getHomeworkDim(){
        return this.repo.size();
    }

    /**
     * Extends the deadline for a given homework
     * @param id - id of the homework
     * @return - 2 if the homework doesn not exists, 1 if the homework was updated,
     * 2 otherwise
     * @throws IllegalArgumentException - if id is null
     */
    public Integer extendHomeworkServ(Integer id) throws IllegalArgumentException {
        Homework hw = this.repo.findOne(id);
        if(nullHomework.test(hw)){
            return 2;
        }
        else if(hw.getDeadlineWeek()+1 > 14){
            return 0;
        }
        hw.extendHomework();
        notifyObservers(new HomeworkEvent(PossibleOperations.UPDATE, hw));
        return 1;
    }

    List<Observer<HomeworkEvent>> observers = new ArrayList<>();
    @Override
    public void addObserver(Observer<HomeworkEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<HomeworkEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(HomeworkEvent t) {
        observers.forEach(x -> x.update(t));
    }


}
