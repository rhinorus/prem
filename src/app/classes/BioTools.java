package app.classes;

/**
 * Provides access to bioinformatics methods.
 */
public abstract class BioTools{

    /**
     * Makes compimentary sequence.
     * @param sequence - source sequence
     * @return - complimentary sequence
     */
    public String getComplimentarySequence(String sequence){
        StringBuilder builder = new StringBuilder();

        for(char nucleotide : sequence.toUpperCase().toCharArray()){
            switch(nucleotide){
                case 'A':
                    builder.insert(0, 'T');
                    break;
                case 'T':
                    builder.insert(0, 'A');
                    break;
                case 'C':
                    builder.insert(0, 'G');
                    break;
                case 'G':
                    builder.insert(0, 'C');
                    break;
                default:
                    break;
            }
        }

        return builder.toString(); 
    }
 
}