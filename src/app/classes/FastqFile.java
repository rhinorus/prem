package app.classes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

/**
 * Представление fastq файла
 */
public class FastqFile {

    private String path;
    private ArrayList<FastqLine> fastqLines;   


    //=======================
    //  Конструкторы
    //=======================
        
    /**
     * Строит объект по пути к файлу.
     * @param path - путь к файлу
     */
    public FastqFile(String path) throws Exception { 
        this.path = path;
        readFile();
    }

    //=======================
    //  Свойства
    //=======================

    public String getPath(){
        return path;
    }

    public ArrayList<FastqLine> getFastqLines(){
        return fastqLines;
    }
 
    //=======================
    //  Методы
    //=======================
    
    /**
     * Производит чтение данных
     */
    void readFile() throws Exception {
        
        fastqLines = new ArrayList<>();
        BufferedReader reader;

        if(path.endsWith(".gz")){
            GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(path));
            reader = new BufferedReader(new InputStreamReader(gzip));
        }
        else{
            File file = new File(path);
            reader = new BufferedReader(new FileReader(file));
        } 
        String line = reader.readLine();
        while(line != null) {
            fastqLines.add( 
                new FastqLine(
                    line,               // sequenceId
                    reader.readLine(),  // sequence 
                    reader.readLine(),  // plus
                    reader.readLine())  // qualities
                );
            // читаем sequenceId следующей строки, если есть.
            line = reader.readLine();
        }

        reader.close(); 
    }

    @Override
    public String toString() {
        return fastqLines.stream().map(x -> x.toString()).collect(Collectors.joining("\n"));
    } 

}