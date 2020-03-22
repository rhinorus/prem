package app.classes;

/**
 * Представление праймера, который может быть обнаружен в прочтении.
 */
public class PrimerCandidate{

    private Primer primer;
    private int count;

    //=========================
    // Конструкторы
    //=========================

    /**
     * Создает кандидата по праймеру.
     * @param primer - праймер.
     */
    public PrimerCandidate(Primer primer){
        this.primer = primer;
    }

    //=========================
    // Свойства
    //=========================

    public Primer getPrimer(){
        return primer;
    }

    public int getCount(){
        return count;
    }

    //=========================
    // Методы
    //=========================

    /**
     * Увеличивает счетчик появлений на единицу.
     */
    public void incrementCount(){
        count++;
    }

}