package app;

import app.classes.FastqFile;

public class App {

    static String FASTQ_FILE_PATH   = "test/IonXpress_047.fastq.gz";  
    static String PRIMERS_FILE_PATH = "test/primers.csv";

    public static void main(String[] args) throws Exception { 

        FastqFile fastq = new FastqFile(FASTQ_FILE_PATH);
        System.out.println(fastq.toString()); 
    }  
} 