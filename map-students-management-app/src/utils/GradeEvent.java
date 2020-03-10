package utils;

import domain.entities.Grade;

public class GradeEvent implements Event {

    private PossibleOperations type;
    private Grade data;

    public GradeEvent(PossibleOperations type, Grade data){
        this.type = type;
        this.data = data;
    }

    public PossibleOperations getType() {return this.type;}

    public Grade getData() {return this.data;}
}
