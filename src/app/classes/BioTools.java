package app.classes;

/**
 * Предоставляет доступ к биоинформатическим методам.
 */
public abstract class BioTools{

    /**
     * Возвращает последовательность, комплиментарную данной.
     * @param sequence - исходная последовательность
     * @return 
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