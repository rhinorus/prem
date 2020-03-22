package app;

import app.classes.PrimersFile;

public class App {

    static String FASTQ_FILE_PATH   = "test/IonXpress_047.fastq"; 
    static String PRIMERS_FILE_PATH = "test/primers.csv";

    public static void main(String[] args) throws Exception { 
        PrimersFile primers = new PrimersFile(PRIMERS_FILE_PATH);
        System.out.println(primers.toString());  
    }  
} 