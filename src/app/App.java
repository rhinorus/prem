package app;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import app.classes.FastqFile;
import app.classes.FastqLine;
import app.classes.Primer;
import app.classes.PrimersFile;

public class App {

    private static final DateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
 
    static String FASTQ_FILE_PATH   = "test/IonXpress_047.fastq.gz";  
    static String PRIMERS_FILE_PATH = "test/primers.csv";

    static HashMap<String, Set<FastqLine>> readsIndex; 
    static HashMap<String, Set<Primer>> primersIndex; 
    static HashMap<FastqLine, HashMap<Primer, Integer>> candidates;  

    public static void main(String[] args) throws Exception { 
        printLog("Начало работы.");

        printLog("Чтение файла праймеров.");
        PrimersFile primersFile = new PrimersFile(PRIMERS_FILE_PATH);
        System.out.println("Минимальная длина праймера: " + primersFile.getMinPrimerLength() + ", максимальная: " + primersFile.getMaxPrimerLength());
        printLog("Чтение завершено.");

        printLog("Построение индекса по праймерам.");
        buildKmerIndex(13, primersFile.getForwardPrimers());
        buildKmerIndex(13, primersFile.getReversePrimers()); 
        printLog("Завершено построение индекса.");
 
        printLog("Чтение fastq файла.");
        FastqFile fastqFile = new FastqFile(FASTQ_FILE_PATH);
        printLog("Чтение завершено.");

        printLog("Предварительный поиск первого праймера");
        
        printLog("Предварительный поиск завершен");

        printLog("Подбор наиболее вероятных праймеров для ридов."); 
        candidatesSelection(13, fastqFile.getFastqLines(), primersFile.getMaxPrimerLength());
        printLog("Подбор завершен.");

        printLog("Информация о подборе:");

        int 
            zero = 0, 
            one = 0,
            moreThanOne = 0,
            ttl = fastqFile.getFastqLines().size();

        for(FastqLine read : fastqFile.getFastqLines()){
            int size = candidates.get(read).size();
            if(size == 0)
                zero++;
            else if(size == 1)
                one++;
            else
                moreThanOne++;
        }

        System.out.println("=====================");
        System.out.println("Total reads: " + ttl);
        System.out.println("Zero: "     + zero          + " (" + (zero          / (double)ttl * 100.0) + "%)");
        System.out.println("One: "      + one           + " (" + (one           / (double)ttl * 100.0) + "%)");
        System.out.println("Two+: "     + moreThanOne   + " (" + (moreThanOne   / (double)ttl * 100.0) + "%)");
        System.out.println("=====================");


        printLog("Завершение работы.");
    }  

    /**
     * Добавляет в HashMap индексы праймеров по k-mer'ам указанного размера.
     */
    private static void buildKmerIndex(Integer kmerSize, ArrayList<Primer> primers){
        if(primersIndex == null)
            primersIndex = new HashMap<>();

        for(Primer primer : primers){
            for(int i = 0; i <= primer.getSequenceChars().length - kmerSize; i++){
                StringBuilder kmerBuilder = new StringBuilder();

                for(int j = i; j < i + kmerSize; j++)
                    kmerBuilder.append(primer.getSequenceChars()[j]);

                String kmer = kmerBuilder.toString(); 

                if(primersIndex.containsKey(kmer)){
                    primersIndex.get(kmer).add(primer);
                }  
                else{
                    Set<Primer> primersSet = new HashSet<>();
                    primersSet.add(primer);

                    primersIndex.put(kmer, primersSet);
                }
            }
        }
    } 
  
    /**
     * Осуществляет подбор праймеров-кандидатов для каждого прочтения.
     * @param kmerSize - длина k-mer'a
     * @param reads - коллекция прочтений.
     * @param length - максимальная длина праймера.
     */
    private static void candidatesSelection(Integer kmerSize, ArrayList<FastqLine> reads, Integer length){
        if(candidates == null)
            candidates = new HashMap<>();  

        for(FastqLine read : reads){

            HashMap<Primer, Integer> readCandidates;
            
            if(candidates.containsKey(read)){
                readCandidates = candidates.get(read);
            } 
            else{
                readCandidates = new HashMap<>();
                candidates.put(read, readCandidates);
            }
             
            for(int i = 0; i <= read.getSequence().length() - kmerSize && i <= length - kmerSize; i++){
                StringBuilder kmerBuilder = new StringBuilder();
                for(int j = i; j < i + kmerSize; j++)
                    kmerBuilder.append(read.getSequenceChars()[j]);

                if(!primersIndex.containsKey(kmerBuilder.toString()))
                    continue; 

                Set<Primer> currentCandidates = primersIndex.get(kmerBuilder.toString());

                for(Primer candidate : currentCandidates){
                    Integer value = 0;

                    if(readCandidates.containsKey(candidate))
                        value = readCandidates.get(candidate);
                    
                    readCandidates.put(candidate, value + 1);
                }
            }
        }
    }

    /**
     * Пишет в консоль сообщение с указанием времени.
     */
    private static void printLog(String message){
        String time = sdf.format(new Date());
        System.out.println(time + ": " + message);
    }
}    