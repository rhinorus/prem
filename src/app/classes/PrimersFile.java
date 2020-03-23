package app.classes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import app.classes.Primer.PrimerType;

/**
 * Representation of the primers .csv file.
 */
public class PrimersFile {

    private String path;
    private ArrayList<Primer> 
                            forwardPrimers,
                            reversePrimers;
    private int 
                minPrimerLength,
                maxPrimerLength;

    //==========================
    // Constructors
    //==========================

    /**
     * Creates object by .csv file path.
     * 
     * @param path - path to primers .csv file.
     * @throws Exception
     */
    public PrimersFile(String path) throws Exception {
        this.path = path;
        this.minPrimerLength = Integer.MAX_VALUE;
        readFile();

    }

    //==========================
    // Properties
    //==========================
 
    public String getPath(){
        return path;
    }

    public ArrayList<Primer> getForwardPrimers(){
        return forwardPrimers;
    }

    public ArrayList<Primer> getReversePrimers(){
        return reversePrimers;
    } 
  
    public int getMinPrimerLength(){
        return minPrimerLength;
    }

    public int getMaxPrimerLength(){
        return maxPrimerLength;
    }

    //==========================
    // Other methods
     //==========================

    /**
     * Builds a collection from file.
     */
    void readFile() throws Exception {
        forwardPrimers = new ArrayList<>();
        reversePrimers = new ArrayList<>();

        File file = new File(path);

        if(!file.exists())
            throw new Exception("Файл праймеров " + path + " не найден."); 

        BufferedReader reader = new BufferedReader(new FileReader(file));

        // заголовки .csv файла
        String[] headers = reader.readLine().split(",");
        
        // индексы стоблцов, в которых записаны праймеры в прямом и в обратном направлении.
        int 
            forwardIndex = -1,
            reverseIndex = -1;

        for(int i = 0; i < headers.length; i++){
            switch(headers[i].toUpperCase()){
                case "FORWARD":
                    forwardIndex = i;
                    break;
                case "REVERSE":
                    reverseIndex = i;
                    break;
                default:
                    break;
            }
        }

        if(forwardIndex == -1 || reverseIndex == -1){
            reader.close(); 
            throw new Exception("Неверный формат файла. Не удалось найти столбец Forward или Reverse.");
        }
            

        String line = reader.readLine();
        while(line != null){
            String[] values = line.split(",");

            Primer forward = new Primer(values[forwardIndex].toUpperCase(), PrimerType.Forward);
            Primer reverse = new Primer(values[reverseIndex].toUpperCase(), PrimerType.Reverse);

            forward.setPair(reverse);
            reverse.setPair(forward);

            if(forward.getSequence().length() < minPrimerLength)
                minPrimerLength = forward.getSequence().length();
            if(reverse.getSequence().length() < minPrimerLength)
                minPrimerLength = reverse.getSequence().length();

            if(forward.getSequence().length() > maxPrimerLength)
                maxPrimerLength = forward.getSequence().length();
            if(reverse.getSequence().length() > maxPrimerLength)
                maxPrimerLength = reverse.getSequence().length();

            forwardPrimers.add(forward);
            reversePrimers.add(reverse);

            line = reader.readLine();
        }

        reader.close();
    }
 
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        
        // header
        builder.append("Forward,Reverse\n");

        for(int i = 0; i < forwardPrimers.size(); i++)
            builder.append(forwardPrimers.get(i).toString() + "," + reversePrimers.get(i).toString() + "\n");

        return builder.toString();
    }
}