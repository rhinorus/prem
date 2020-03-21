package app;

import app.classes.FastqFile;

public class App {

    static String FASTQ_FILE_PATH = "test/IonXpress_047.fastq"; 

    public static void main(String[] args) throws Exception {
        FastqFile file = new FastqFile(FASTQ_FILE_PATH);
        System.err.println(file.toString());   
    }
}