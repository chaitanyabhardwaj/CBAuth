
package cbauth;

public class User {
    
    public final Field PRIMARY_ID;
    private final Field[] FIELDS;
            
    public User(Field primaryID, Field[] fields) {
        PRIMARY_ID = primaryID;
        FIELDS = fields;
    }
    
    public Comparable getFieldValue(Field field) {
        for(Field f : FIELDS) {
            if(field.equals(f))
                return f.getValue();
        }
        return null;
    }
    
}
