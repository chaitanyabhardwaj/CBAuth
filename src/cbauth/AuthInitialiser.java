
package cbauth;

import cbauth.exception.RequiredFieldException;
import cbauth.exception.NullFieldException;

import java.util.ArrayList;
import java.util.Arrays;

public class AuthInitialiser {
    
    final private ArrayList<Field> requiredFieldList;
    final private ArrayList<Field> fieldList;
    final public DatabaseManager DATABASE_MANAGER;
    private Field primaryField, passwdField;
    final public String KEY;
    
    public AuthInitialiser(DatabaseManager manager, String key) {
        DATABASE_MANAGER = manager;
        requiredFieldList = new ArrayList<>();
        fieldList = new ArrayList<>();
        KEY = key;
        primaryField = passwdField = null;
    }
    
    public AuthInitialiser(DatabaseManager manager, String key, Field fields[]) {
        DATABASE_MANAGER = manager;
        requiredFieldList = new ArrayList<>();
        fieldList = new ArrayList<>(Arrays.asList(fields));
        KEY = key;
        primaryField = passwdField = null;
    }
    
    public void setPrimaryField(Field f)throws RequiredFieldException {
        if(!isRequiredField(f))
            throw new RequiredFieldException("The Primary Key must be a 'required' field. Set the key field as 'required' field before making it Primary");
        primaryField = f;
    }
    
    public Field getPrimaryField() {
        return primaryField;
    }
    
    public void setPasswordField(Field f)throws  RequiredFieldException{
        if(!isRequiredField(f))
            throw new RequiredFieldException("The Password Field must be a 'required' field. Set the password field as 'required' field before making it Primary");
        passwdField = f;
    }
    
    public Field getPasswordField() {
        return passwdField;
    }
    
    public void addNewField(Field f) {
        if(!containsField(f))
            fieldList.add(f);
    }
    
    public void removeField(Field f) {
        if(containsField(f)) {
            removeRequiredField(f);
            fieldList.remove(f);
        }
    }
    
    public void setRequiredField(Field f)throws NullFieldException {
        if(!isRequiredField(f)) {
            if(!containsField(f))
                throw new NullFieldException("The specific field can't be found! Please make sure to add the new field before setting it as 'required field'");
            requiredFieldList.add(f);
        }
    }
    
    public void removeRequiredField(Field f) {
        if(isRequiredField(f))
            requiredFieldList.remove(f);
    }
    
    public boolean containsField(Field f) {
        return fieldList.contains(f);
    }
    
    public boolean isRequiredField(Field f) {
        return requiredFieldList.contains(f);
    }
    
    public Field[] getAllFields() {
        return fieldList.toArray(new Field[fieldList.size()]);
    }
    
    public Field[] getAllRequiredFields() {
        return requiredFieldList.toArray(new Field[requiredFieldList.size()]);
    }
    
}
