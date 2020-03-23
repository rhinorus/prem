package app.classes;

/**
 * Representation of the .fastq file line.
 */
public class FastqLine{
    
    private String 
                sequenceId,
                sequence,
                plus,
                qualities;

    private char[] sequenceChars;
    private Boolean isFirstPrimerFound;

    //================================
    //  Constructors
    //================================

    /**
     * Creates an object, which includes four inform lines.
     * <p> First line - sequenceId </p>
     * <p> Second - read sequence </p>
     * <p> Third - string, usually contains only '+' symbol. </p>
     * <p> Fourth - read qualities </p>
     * 
     * @param sequenceId    - string sequence id
     * @param sequence      - read sequence
     * @param plus          - string, which usually contains only '+' symbol. 
     * @param qualities     - read qualities
     */
    public FastqLine(String sequenceId, String sequence, String plus, String qualities){
        this.sequenceId         = sequenceId;
        this.sequence           = sequence;
        this.plus               = plus;
        this.qualities          = qualities;
        this.sequenceChars      = sequence.toCharArray();
        this.isFirstPrimerFound = false; 
    }

    //================================
    //  Properties
    //================================
 
    public String getSequenceId(){
        return sequenceId;
    }

    public void setSequenceId(String sequenceId){
        this.sequenceId = sequenceId;
    }

    public String getSequence(){
        return sequence;
    }

    public void setSequence(String sequence){
        this.sequence = sequence;
    }

    public String getPlus(){
        return plus;
    }

    public void setPlus(String plus){
        this.plus = plus;
    }

    public String getQualities(){
        return qualities;
    }

    public void setQualities(String qualities){
        this.qualities = qualities;
    }
 
    public char[] getSequenceChars() {
        return sequenceChars;
    }

    public Boolean getIsFirstPrimerFound(){
        return isFirstPrimerFound;
    }
 
    public void setIsFirstPrimerFound(Boolean isFirstPrimerFound){
        this.isFirstPrimerFound = isFirstPrimerFound;
    }

    //================================
    //  Other methods
    //================================

    @Override
    public String toString() {
        return  getSequenceId() + "\n" +
                getSequence()   + "\n" +
                getPlus()       + "\n" +
                getQualities(); 
    }
} 