package utils;

public class DTOGrade {
    private Double gradeValue;
    private String hwDesc;
    private Integer hwId;
    private String profName;
    private String feedBack;

    public String getProfName() {
        return profName;
    }

    public void setProfName(String profName) {
        this.profName = profName;
    }

    public String getFeedBack() {
        return feedBack;
    }

    public void setFeedBack(String feedBack) {
        this.feedBack = feedBack;
    }

    public DTOGrade(Double gradeValue, String hwDesc, Integer hwId, String profName, String feedBack) {
        this.gradeValue = gradeValue;
        this.hwDesc = hwDesc;
        this.hwId = hwId;
        this.profName = profName;
        this.feedBack = feedBack;
    }

    public DTOGrade(Double gradeValue, String hwDesc, Integer hwId) {
        this.gradeValue = gradeValue;
        this.hwDesc = hwDesc;
        this.hwId = hwId;
    }

    public Double getGradeValue() {
        return gradeValue;
    }

    public void setGradeValue(Double gradeValue) {
        this.gradeValue = gradeValue;
    }

    public String getHwDesc() {
        return hwDesc;
    }

    public void setHwDesc(String hwDesc) {
        this.hwDesc = hwDesc;
    }

    public Integer getHwId() {
        return hwId;
    }

    public void setHwId(Integer hwId) {
        this.hwId = hwId;
    }
}
