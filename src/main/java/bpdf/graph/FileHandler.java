package bpdf.graph;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FileHandler {

    public String readFile(File file) {
        StringBuffer fileBuffer;
        String fileString = null;
        String line;

        try {
            FileReader in = new FileReader(file);
            BufferedReader dis = new BufferedReader(in);
            fileBuffer = new StringBuffer();

            while ((line = dis.readLine()) != null) {
                fileBuffer.append(line + "\n");
            }
            in.close();
            fileString = fileBuffer.toString();
        } catch  (IOException e) {
            return null;
        }
        return fileString;
    }

    public static boolean writeFile(File file, String dataString) {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            out.print(dataString);
            out.flush();
            out.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}

