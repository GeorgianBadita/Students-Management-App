package utils;

import domain.entities.Student;

public class StudentEvent implements Event{
    private PossibleOperations type;
    private Student data;

    public StudentEvent(PossibleOperations type, Student data){
        this.type = type;
        this.data = data;
    }

    public PossibleOperations getType() {return this.type;}

    public Student getData() {return this.data;}


}
