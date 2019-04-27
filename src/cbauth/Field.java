package cbauth;

public abstract class Field<T extends Comparable> {
    
    private T value;
    final private String NAME, SQL_TYPE;
            
    public Field(String fieldName, String sqlType) {
        NAME = fieldName;
        SQL_TYPE = sqlType;
    }
    
    public String getName() {
        return NAME;
    }
    public String getSqlType() {
        return SQL_TYPE;
    }
    
    public T getValue() {
        return value;
    }
    
    public void setValue(T t) {
        value = t;
    }
    
    @Override
    public boolean equals(Object obj) {
        Field f;
        if(obj instanceof Field) {
            f = (Field) obj;
            return (f.getName().equals(getName()));
        }
        return false;
            
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }
    
}
