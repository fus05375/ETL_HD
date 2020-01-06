package mainApp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TxtFile {
    private String fullHtmlCode;
    String fileName;

    public TxtFile(String fileName, String plainText) {

        this.fileName = fileName;
        this.fullHtmlCode = plainText;
    }


    void createFile() {
        String path = "C:" + File.separator + "OLX" + File.separator + this.fileName + ".txt";
        File f = new File(path);
        f.getParentFile().mkdirs();
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    void writeToFile() {
        try {
            FileWriter fw = new FileWriter("C:\\OLX/" + this.fileName + ".txt");
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(fullHtmlCode);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void removeFile() {
        File file = new File("C:\\OLX/"+this.fileName+".txt");
        if (file.delete()) {
            System.out.println("File deleted successfully");
        } else {
            System.out.println("Failed to delete the file");
        }
    }
}
