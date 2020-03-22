package app.classes;

/**
 * Строка fastq файла
 */
public class FastqLine{
    
    private String 
                sequenceId,
                sequence,
                plus,
                qualities;

    private char[] sequenceChars;
    private Boolean firstPrimerFound;

    //================================
    //  Конструкторы
    //================================

    /**
     * Создание объекта на основе 4-х строк, представляющих одну запись в fastq файле.
     * 
     * @param sequenceId    - строковый идентификатор последовательности
     * @param sequence      - последовательность
     * @param plus          - строка, содержащая '+'
     * @param qualities     - строка, содержащая информацию о качестве чтения 
     */
    public FastqLine(String sequenceId, String sequence, String plus, String qualities){
        this.sequenceId     = sequenceId;
        this.sequence       = sequence;
        this.plus           = plus;
        this.qualities      = qualities;
        this.sequenceChars  = sequence.toCharArray();
    }

    //================================
    //  Свойства
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

    public Boolean getFirstPrimerFound(){
        return firstPrimerFound;
    }
 
    public void setFirstPrimerFound(Boolean firstPrimerFound){
        this.firstPrimerFound = firstPrimerFound;
    }

    //================================
    //  Методы
    //================================

    @Override
    public String toString() {
        return  getSequenceId() + "\n" +
                getSequence()   + "\n" +
                getPlus()       + "\n" +
                getQualities(); 
    }
} 