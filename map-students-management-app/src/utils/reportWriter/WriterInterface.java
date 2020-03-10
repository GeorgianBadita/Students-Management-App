package utils.reportWriter;

public interface WriterInterface {
    /**
     * Function which oblige Writers to write a document
     * @param path - where to write
     * @param toWrite - what to write
     * @param author - author of the report
     * @param combination - what combination to write
     */
    void writeReport(String path, String[] toWrite, String author, Integer combination);

}
