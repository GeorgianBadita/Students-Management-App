package utils;

import domain.entities.Homework;

public class HomeworkEvent implements Event {

    private PossibleOperations type;
    private Homework data;

    public HomeworkEvent(PossibleOperations type, Homework data){
        this.type = type;
        this.data = data;
    }

    public PossibleOperations getType() {return this.type;}

    public Homework getData() {return this.data;}
}
