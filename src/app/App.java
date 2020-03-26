package app;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import app.classes.DistanceMetrics;
import app.classes.FastqFile;
import app.classes.FastqLine;
import app.classes.KmerOffset;
import app.classes.Primer;
import app.classes.PrimersFile;
import app.classes.ReadOffset;
 
public class App {
 
    static final DateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
    static final Integer    DEFAULT_KMER_SIZE = 12;

    static final boolean    DEBUG_MODE = true;
 
    static String FASTQ_FILE_PATH   = "test/IonXpress_047.fastq.gz";  
    static String PRIMERS_FILE_PATH = "test/primers.csv";

    static HashMap<String, Set<FastqLine>>              readsIndex; 
    static HashMap<String, Set<KmerOffset>>                 primersIndex; 
    static HashMap<FastqLine, HashMap<ReadOffset, Integer>> candidates;  

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

        ArrayList<FastqLine> zeroPrimersReads = setForwardReadAndGetZeroCandidateReads(fastqFile.getFastqLines());

        if(zeroPrimersReads.size() > 0){
            printLog("Starting deep forward primer search.");
            Integer kmerSize = DEFAULT_KMER_SIZE - 1;
    
            while(kmerSize > 8 && zeroPrimersReads.size() > 0){
                buildKmerIndex(kmerSize, primersFile.getForwardPrimers());
                buildKmerIndex(kmerSize, primersFile.getReversePrimers());
    
                candidatesSelection(kmerSize, zeroPrimersReads, primersFile.getMaxPrimerLength());
    
                zeroPrimersReads = setForwardReadAndGetZeroCandidateReads(zeroPrimersReads); 
                kmerSize--;
            }
            printLog("done.");
        }
 
        // That means, what zeroPrimerReads contains >3 errors at start position.
        if(zeroPrimersReads.size() > 0){
            printLog("Detected " + zeroPrimersReads.size() + " low-quality reads. They will be removed.");
            for(FastqLine read : zeroPrimersReads) // twice faster, than removeAll() function
                fastqFile.getFastqLines().remove(read); 
            printLog("done.");
        } 
         
        // Коррекция весов Дамерау-Левенштейна. Нулевая цена вставки в маску спереди и сзади. 
        // алгоритм нужен для поиска первого праймера у ридов, для которых
        // было определено более одного кандидата.
 
        printLog("Filtering forward-primer candidates.");

        printLog("done");
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
                    read.setForwardPrimerEndPosition(primer.getSequence().length() - 1); 
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
                    primersIndex.get(kmer).add(new KmerOffset(i, primer)); 
                }  
                else{
                    Set<KmerOffset> primersSet = new HashSet<>();
                    primersSet.add(new KmerOffset(i, primer));

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

            HashMap<ReadOffset, Integer> readCandidates; 
            
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
                
                Set<KmerOffset> currentCandidates = primersIndex.get(kmerBuilder.toString());

                for(KmerOffset candidate : currentCandidates){
                    Integer value = 0; 
                    ReadOffset foundedReadOffset = null;
 
                    for(ReadOffset key : readCandidates.keySet()){
                        if(key.getKmerOffset().getPrimer().getSequence().equals(candidate.getPrimer().getSequence())){ 
                            foundedReadOffset = key;
                            value = readCandidates.get(key);
                        }
                    }
                    if(foundedReadOffset != null)
                        readCandidates.put(foundedReadOffset, value + 1);
                    else
                        readCandidates.put(new ReadOffset(i, candidate), value + 1 );
                }
            }
        }
    }
  
    /**
     * Setting ForwardPrimer for reads, searching reads without any forward primer.
     * @param reads - reads to be processed.
     * @return a collection of reads without primer-candidates.
     */
    private static ArrayList<FastqLine> setForwardReadAndGetZeroCandidateReads(ArrayList<FastqLine> reads){
        
        ArrayList<FastqLine> zeroPrimersReads = new ArrayList<>();

        for(FastqLine read : reads){
            int numberOfPrimers = 1;

            if(candidates.containsKey(read)){
                numberOfPrimers = candidates.get(read).size();

                switch(numberOfPrimers){
                    case 0:
                        zeroPrimersReads.add(read); 
                        break;
                    case 1: 
                        if(read.getForwardPrimer() == null){
                            for(ReadOffset readOffset : candidates.get(read).keySet()) { // always one entry in collection. But I don't know how to access it by index :)
                            read.setForwardPrimer(readOffset.getKmerOffset().getPrimer());
                            Integer totalOffset = 
                                                readOffset.getOffset()                                          + 
                                                readOffset.getKmerOffset().getPrimer().getSequence().length()   -
                                                readOffset.getKmerOffset().getOffset()                          - 1;
                            read.setForwardPrimerEndPosition(totalOffset);  
                        } 
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        return zeroPrimersReads;
    }

    /**
     * Returns Damerau-Levenstein distance.
     * @param mask
     * @param sequence
     * @return
     */
    private static Integer getDamerauLevenshteinDistance(String mask, String sequence){

        int 
            n = mask.length() + 1,
            m = sequence.length() + 1;

        char[]
            maskChars       = mask.toCharArray(),
            sequenseChars   = sequence.toCharArray();

        int[][] matrix = new int[n][m];
        
        for(int i = 0; i < n; i++) // first row
            matrix[i][0] = i;
        for(int i = 1; i < m; i++) // first column
            matrix[0][i] = i;
 
        for(int i = 1; i < n; i++){
            for(int j = 1; j < m; j++){
                int replacementPayment = maskChars[i - 1] == sequenseChars[j - 1] ? 0 : DistanceMetrics.Replacement.getValue();

                int 
                    insertion   = matrix[i][j - 1]      + DistanceMetrics.Insertion.getValue(),
                    deletion    = matrix[i - 1][j]      + DistanceMetrics.Deletion.getValue(),
                    replacement = matrix[i - 1][j - 1]  + replacementPayment;

                matrix[i][j] = getMinOfThree(insertion, deletion, replacement);
            }
        }

        return matrix[n - 1][m - 1]; 
    }

    /**
     * Returns minimum of three integer numbers.
     */
    private static int getMinOfThree(int x, int y, int z){
        return x < y ? (x < z ? x : z) : y < z ? y : z;
    }

    /**
     * Logs message with timestamp.
     */
    private static void printLog(String message){
        if(DEBUG_MODE){
            String time = sdf.format(new Date());
            System.out.println(time + ": " + message);
        }
    }
}         