package utils.reportWriter;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import utils.Utility;

import java.io.FileOutputStream;

public class TextFilePdf implements WriterInterface{
    private static TextFilePdf textFilePdf;

    /**
     * Pricate constructor
     */
    private TextFilePdf(){}


    public static TextFilePdf getInstance(){
        if(textFilePdf == null){
            textFilePdf = new TextFilePdf();
        }
        return textFilePdf;
    }

    /**
     * Function which oblige Writers to write a document
     *
     * @param path    - where to write
     * @param toWrite - what to write
     * @param author - author of the report
     * @param combination - what combination of rerpots to write
     */
    @Override
    public void writeReport(String path, String[] toWrite, String author, Integer combination) {
        Document output = null;
        try {

            //    see com.lowagie.text.PageSize for a complete list of page-size constants.
            output = new Document(PageSize.LETTER, 40, 40, 40, 40);
            PdfWriter.getInstance(output, new FileOutputStream(path));

            output.open();
            output.addAuthor(author);

            int poz = 0;
            for(String text : toWrite) {
                String[] lines = text.split("\n");
                if(Utility.ithBitOne(combination, poz)) {
                    Paragraph p;
                    p = new Paragraph(lines[0] + ":\n");
                    p.setAlignment(Element.ALIGN_CENTER);
                    output.add(p);
                    for (int i = 1; i<lines.length; i++) {
                        p = new Paragraph(lines[i] + "\n");
                        p.setAlignment(Element.ALIGN_LEFT);
                        output.add(p);
                    }
                    p = new Paragraph("\n\n");
                    p.setAlignment(Element.ALIGN_JUSTIFIED);
                    output.add(p);
                }
                poz ++;
            }
            output.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}