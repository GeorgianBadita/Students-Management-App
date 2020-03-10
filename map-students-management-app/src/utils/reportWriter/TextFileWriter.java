package utils.reportWriter;

import utils.Utility;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class TextFileWriter implements WriterInterface {

    private static TextFileWriter textFileWriter;

    /**
     * Private constructor
     */
    private TextFileWriter(){

    }

    public static TextFileWriter getInstance(){
        if(textFileWriter == null){
            textFileWriter = new TextFileWriter();
        }
        return textFileWriter;
    }

    /**
     * Function which oblige Writers to write a document
     *
     * @param path        - where to write
     * @param toWrite     - what to write
     * @param author      - author of the report
     * @param combination - what combination to write
     */
    @Override
    public void writeReport(String path, String[] toWrite, String author, Integer combination) {

            try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
                for(int i = 0; i<toWrite.length; i++) {
                    if (Utility.ithBitOne(combination, i)) {
                        pw.print(toWrite[i]);
                        pw.print("\n\n");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

    }
}
