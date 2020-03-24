package app.classes;

/**
 * Representation of a k-mer position offset in read sequence
 */
public class ReadOffset{

    private Integer offset;
    private KmerOffset kmerOffset;

    //=========================
    // Constructors
    //=========================

    /**
     * Creates object by offset in sequence and KmerOffset object.
     * @param offset - k-mer offset in read sequence.
     * @param kmerOffset - kmer, bound to primer, which was detected in read.
     */
    public ReadOffset(Integer offset, KmerOffset kmerOffset){
        this.offset = offset;
        this.kmerOffset = kmerOffset;
    }

    //=========================
    // Properties
    //=========================
 
    public Integer getOffset(){
        return offset;
    }

    public KmerOffset getKmerOffset(){
        return kmerOffset;
    }

}