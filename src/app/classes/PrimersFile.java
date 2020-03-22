package app.classes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import app.classes.Primer.PrimerType;

/**
 * Представление файла праймеров в .csv формате
 */
public class PrimersFile {

    private String path;
    private ArrayList<Primer> 
                            forwardPrimers,
                            reversePrimers;

    //==========================
    // Конструкторы
    //==========================

    /**
     * Строит объект по пути к файлу.
     * 
     * @param path - путь к .csv файлу с праймерами
     * @throws Exception
     */
    public PrimersFile(String path) throws Exception {
        this.path = path;
        readFile();

    }

    //==========================
    // Свойства
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
 
    //==========================
    // Методы
    //==========================

    /**
     * Читает файл и строит по нему коллекции праймеров.
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

            forwardPrimers.add(forward);
            reversePrimers.add(reverse);

            line = reader.readLine();
        }

        reader.close();
    }
 
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        
        // заголовок
        builder.append("Forward,Reverse\n");

        for(int i = 0; i < forwardPrimers.size(); i++)
            builder.append(forwardPrimers.get(i).toString() + "," + reversePrimers.get(i).toString() + "\n");

        return builder.toString();
    }
}