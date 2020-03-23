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
 
    static final DateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
    static final Integer    DEFAULT_KMER_SIZE = 12;
 
    static String FASTQ_FILE_PATH   = "test/IonXpress_047.fastq.gz";  
    static String PRIMERS_FILE_PATH = "test/primers.csv";

    static HashMap<String, Set<FastqLine>>              readsIndex; 
    static HashMap<String, Set<Primer>>                 primersIndex; 
    static HashMap<FastqLine, HashMap<Primer, Integer>> candidates;  

    public static void main(String[] args) throws Exception { 
        printLog("PREM started.");

        printLog("Reading primers file.");
        PrimersFile primersFile = new PrimersFile(PRIMERS_FILE_PATH);
        printLog("Min primer length: \t"   + primersFile.getMinPrimerLength());
        printLog("Max primer length: \t"  + primersFile.getMaxPrimerLength());
        printLog("Total pairs: \t\t"      + primersFile.getForwardPrimers().size());
        printLog("done.");

        printLog("Indexing primers.");
        buildKmerIndex(DEFAULT_KMER_SIZE, primersFile.getForwardPrimers());
        buildKmerIndex(DEFAULT_KMER_SIZE, primersFile.getReversePrimers()); 
        printLog("done.");
 
        printLog("Reading .fastq file.");
        FastqFile fastqFile = new FastqFile(FASTQ_FILE_PATH);
        printLog("done.");

        printLog("Searching for forward primer.");
        buildReadsFirstPrimerIndex(primersFile.getMinPrimerLength(), fastqFile.getFastqLines());
        lookForFirstPrimer(primersFile.getMinPrimerLength(), primersFile.getForwardPrimers());
        lookForFirstPrimer(primersFile.getMinPrimerLength(), primersFile.getReversePrimers());
        printLog("done.");
 
        printLog("Searching for forward primer by indexed k-mer's."); 
        candidatesSelection(DEFAULT_KMER_SIZE, fastqFile.getFastqLines(), primersFile.getMaxPrimerLength());
        printLog("done..");

        printLog("Setting known forward primers");
        ArrayList<FastqLine> 
                        zeroPrimersReads = new ArrayList<>(),
                        moreThanOnePrimersReads = new ArrayList<>();

        for(FastqLine read : fastqFile.getFastqLines()){
            int numberOfPrimers = 1;

            if(candidates.containsKey(read)){
                numberOfPrimers = candidates.get(read).size();

                switch(numberOfPrimers){
                    case 0:
                        zeroPrimersReads.add(read); 
                        break;
                    case 1: 
                        for(Primer primer : candidates.get(read).keySet()) // always one entry in collection. But I don't know how to access it by index :)
                            read.setForwardPrimer(primer);
                        break;
                    default:
                        moreThanOnePrimersReads.add(read);
                        break;
                }
            }
        }

        printLog("done."); 

        int 
            zero = 0, 
            one = 0,
            moreThanOne = 0,
            firstFound = 0,
            ttl = fastqFile.getFastqLines().size();

        for(FastqLine read : fastqFile.getFastqLines()){
            // Если рида нет в перечислении, то первый праймер уже был найден.
            int size = 1; 

            if(candidates.containsKey(read))
                size = candidates.get(read).size();


            if(read.getIsFirstPrimerFound())
                firstFound++;

            if(size == 0)
                zero++;
            else if(size == 1 || read.getIsFirstPrimerFound()) 
                one++;
            else
                moreThanOne++;
        }

        printLog("Stats:");
        System.out.println("=====================");
        System.out.println("Total reads: " + ttl);
        System.out.println("Zero: "         + zero          + " (" + (zero          / (double)ttl * 100.0) + "%)");
        System.out.println("One: "          + one           + " (" + (one           / (double)ttl * 100.0) + "%)");
        System.out.println("firstFound: "   + firstFound    + " (" + (firstFound    / (double)ttl * 100.0) + "%)");
        System.out.println("Two+: "         + moreThanOne   + " (" + (moreThanOne   / (double)ttl * 100.0) + "%)");
        System.out.println("=====================");


        printLog("Trimming done.");
    }  

    /**
     * Indexing reads for quick forward primer selection.
     * @param size - min primer length.
     * @param reads
     */
    private static void buildReadsFirstPrimerIndex(Integer size, ArrayList<FastqLine> reads){
        if(readsIndex == null)
            readsIndex = new HashMap<>();

        for(FastqLine read : reads){
            StringBuilder builder = new StringBuilder();

            for(int i = 0; i < size; i++)
                builder.append(read.getSequenceChars()[i]);

            String index = builder.toString();
            
            if(readsIndex.containsKey(index)){
                readsIndex.get(index).add(read);
            }
            else{
                Set<FastqLine> lines = new HashSet<>();
                lines.add(read);

                readsIndex.put(index, lines);
            }
        }
    }

    /**
     * Marks reads, which has forward primer.
     * @param size - min primer length.
     * @param primers
     */
    private static void lookForFirstPrimer(Integer size, ArrayList<Primer> primers) {
        for(Primer primer : primers){
            StringBuilder builder = new StringBuilder();

            for(int i = 0; i < size; i++)
                builder.append(primer.getSequenceChars()[i]);

            String index = builder.toString();

            if(readsIndex.containsKey(index)){
                for(FastqLine read : readsIndex.get(index)){
                    read.setIsFirstPrimerFound(true);
                    read.setForwardPrimer(primer);
                }
            }

        }
    }

    /**
     * Indexes primers by given k-mer length.
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
     * Candidates selection for each read.
     * @param kmerSize - k-mer length.
     * @param reads
     * @param length - max primer length.
     */
    private static void candidatesSelection(Integer kmerSize, ArrayList<FastqLine> reads, Integer length){
        if(candidates == null)
            candidates = new HashMap<>();  

        for(FastqLine read : reads){

            if(read.getIsFirstPrimerFound())
                continue;

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
     * Logs message with timestamp.
     */
    private static void printLog(String message){
        String time = sdf.format(new Date());
        System.out.println(time + ": " + message);
    }
}      