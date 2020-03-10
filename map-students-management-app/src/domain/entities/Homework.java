package domain.entities;

public class Homework implements HasID<Integer> {

    private Integer id;
    private String description;
    private Integer recvWeek;
    private Integer deadlineWeek;

    /**
     *
     * @param id - homework Id
     * @param description - homework description
     * @param recvWeek - current receiving week
     * @param deadlineWeek - deadline week
     */
    public Homework(Integer id, String description, Integer recvWeek, Integer deadlineWeek){
        this.id = id;
        this.description = description;
        this.recvWeek = recvWeek;
        this.deadlineWeek = deadlineWeek;
    }

    /**
     * Description getter
     * @return - description of the homework
     */
    public String getDescription(){
        return this.description;
    }



    /**
     * RecvWeek getter
     * @return - Receiving week
     */
    public Integer getRecvWeek(){
        return this.recvWeek;
    }

    /**
     * DeadLine Week getter
     * @return - The deadline week
     */
    public Integer getDeadlineWeek() {return this.deadlineWeek;}


    /**
       Function which extends a homework by one week
     */
    public void extendHomework() {
        if(this.deadlineWeek < 14){
            this.deadlineWeek ++;
        }
    }

    /**
     * Function to set the description
     * @param newDescription - the new Description
     */
    public void setDescription(String newDescription){
        this.description = newDescription;
    }


    /**
     * Function to set the description
     * @param newWeek - the new receiving week
     */
    public void setRecvWeek(Integer newWeek){
        this.recvWeek = newWeek;
    }

    /**
     * Function to set the deadline
     * @param newDeadline - the new Deadline Week
     */
    public void setDeadlineWeek(Integer newDeadline){
        this.deadlineWeek = newDeadline;
    }

    /**
     * Overriding Hash method
     * @return - id of the homework
     */
    @Override
    public Integer getId() {
        return this.id;
    }

    /**
     * Overriding setId method
     * @param integer - integer number, represents the new id
     */
    @Override
    public void setId(Integer integer) {
        this.id = integer;
    }


    /**
     * Overiding equals function
     * @param ot - other object
     * @return - true if ot == hw, false otherwise
     */
    @Override
    public boolean equals(Object ot){
        if(this == ot){
            return true;
        }

        if(ot == null){
            return false;
        }
        if(ot.getClass() != this.getClass()){
            return false;
        }
        Homework hw = (Homework)ot;
        return this.id.equals(hw.getId());
    }


    /**
     * Overriding toString() method
     */
    @Override
    public String toString(){
        return "HId: "+this.getId() + " Desc: " + this.getDescription() + " RcW: " + this.getRecvWeek() + " DlW: " + this.getDeadlineWeek();
    }
}
