package app.classes;

/**
 * Представление праймера.
 */
public class Primer{

    private String sequence;
    private PrimerType type;
    private Primer pair;

    //==========================
    // Конструкторы
    //==========================

    public Primer(String sequence, PrimerType type) { 
        this.sequence = sequence;
        this.type = type;
    }

    //==========================
    // Свойства
    //==========================

    public String getSequence(){
        return sequence;
    }

    public PrimerType getType(){
        return type;
    }

    public Primer getPair(){
        return pair;
    }

    public void setPair(Primer pair){
        this.pair = pair; 
    }

    /**
     * Тип праймера - Прямой либо обратный.
     */
    enum PrimerType{
        Forward,
        Reverse;

        PrimerType() { }
    }

    @Override
    public String toString() {
        return sequence;
    }

}