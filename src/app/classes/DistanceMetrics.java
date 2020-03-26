package app.classes;

/**
 * Metrics for Damerau-Levenstein distance calculation. 
 */
public enum DistanceMetrics{

    Insertion(1),
    Deletion(1),
    Replacement(2),
    Equals(0);    

    Integer value;
 
    DistanceMetrics (Integer value){
        this.value = value;
    }

    public Integer getValue(){
        return value;
    }
 
    public void setValue(Integer value){
        this.value = value;
    }

}