
package cbauth;

import cbauth.exception.DuplicateFieldException;
import cbauth.exception.DuplicateUserException;
import cbauth.exception.InvalidFieldException;
import cbauth.exception.NullFieldException;
import cbauth.exception.RequiredFieldException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class Auth {
    
    final private AuthInitialiser INIT;
    final public static String AUTH_TABLE_NAME = "table_auth_001", USER_TABLE_NAME = "table_";
    private String userTableName;
    final private Field PRIMARY_FIELD, PASSWORD_FIELD, REQUIRED_FIELDS[], FIELDS[];
    final static Pattern VARCHAR_PATTERN = Pattern.compile("VARCHAR\\(\\d+\\)", Pattern.CASE_INSENSITIVE); //Compiling a Pattern to search for varchar types
    
    private Auth(AuthInitialiser init) {
        INIT = init;
        PRIMARY_FIELD = init.getPrimaryField();
        PASSWORD_FIELD = init.getPasswordField();
        REQUIRED_FIELDS = init.getAllRequiredFields();
        FIELDS = init.getAllFields();
    }
    
    public static Auth initAuth(AuthInitialiser init)throws SQLException {
        //create Auth object
        Auth auth = new Auth(init);
        
        //create databases if it does not already exists
        if(!auth.INIT.DATABASE_MANAGER.databaseExists()) {
            //create Database
            auth.INIT.DATABASE_MANAGER.createDatabase();
        }
        
        //open the database connection
        auth.INIT.DATABASE_MANAGER.openConnection();
        
        //create AUTH TABLE if it does not already exists
        if(!auth.INIT.DATABASE_MANAGER.tableExists(AUTH_TABLE_NAME)) {
            //create AUTH TABLE
            auth.INIT.DATABASE_MANAGER.executeUpdate("CREATE TABLE " + AUTH_TABLE_NAME + " ("
                    + "ID int NOT NULL AUTO_INCREMENT,"
                    + "NAME VARCHAR(50),"
                    + "PRIMARY KEY(ID));");
        }
        
        //get count of records in the AUTH TABLE
        /*ResultSet set = auth.INIT.DATABASE_MANAGER.executeQuery("SELECT COUNT(*) FROM " + AUTH_TABLE_NAME + ";");
        set.next();
        final int noOfAuthTables = set.getInt(1) + 1;*/
        
        //create USER TABLE if it does not already exists
        auth.userTableName = USER_TABLE_NAME + auth.INIT.KEY;
        if(!auth.INIT.DATABASE_MANAGER.tableExists(auth.userTableName)) {
            //create USER TABLE for this Auth object
            //constructing column names and datatypes from the AuthInitialiser provided Field data
            String userTableQuery = "CREATE TABLE " + auth.userTableName + "( ";
            for(Field f : auth.FIELDS) {
                userTableQuery += f.getName() + " " + f.getSqlType();
                if(auth.INIT.isRequiredField(f))
                    userTableQuery += " NOT NULL";
                userTableQuery += ", ";
            }
            if(auth.PRIMARY_FIELD != null) {
                userTableQuery += "PRIMARY KEY (" + auth.PRIMARY_FIELD.getName() + ")";
            }
            //remove last ','
            else {
                userTableQuery = userTableQuery.substring(0, userTableQuery.length() - 2);
            }
            userTableQuery += ");";
            //execute update - create USER TABLE
            auth.INIT.DATABASE_MANAGER.executeUpdate(userTableQuery);
            //insert the new object into the AUTH TABLE
            auth.INIT.DATABASE_MANAGER.executeUpdate("INSERT INTO " + AUTH_TABLE_NAME + "(NAME) VALUES('" + auth.userTableName + "');");
        }
        //close the database connection
        auth.INIT.DATABASE_MANAGER.closeConnection();
        return auth;
    }
    
    public User changeUserFieldValue(User user, Field newField)throws NullFieldException {
        return changeUserFieldValue(user.PRIMARY_ID, newField);
    }
    
    public User changeUserFieldValue(Field primaryID, Field newField)throws NullFieldException {
        //throw exception if field does not exists
        if(!INIT.containsField(newField))
            throw new NullFieldException("The specific field can't be found! Please make sure to add the new field before setting it as 'required field'");
        //insert new field value in user database
        try {
            INIT.DATABASE_MANAGER.openConnection();
            INIT.DATABASE_MANAGER.executeUpdate("UPDATE " + userTableName + " SET " + newField.getName() + "=" + newField.getValue() + " WHERE " + primaryID.getName() + "=" + primaryID.getValue());
            INIT.DATABASE_MANAGER.closeConnection();
        }
        catch(SQLException ex) {
            System.out.println(ex.toString());
        }
        return null;
    }
    
    public User createUser(Field fields[])throws DuplicateFieldException, DuplicateUserException, InvalidFieldException, NullFieldException, RequiredFieldException {
        String columnNameStr = "", valueStr = "";
        //length of the parameter fields[] must be greater than or equal to REQUIRED_FIELDS length
        int requiredFieldCount = REQUIRED_FIELDS.length;
        if(fields.length < requiredFieldCount)
            throw new RequiredFieldException("All RequiredFields must be present in the Field[] paramter of the method createUser(Field[]).");
        //length of the parameter fields must be less than or equal to FIELDS length
        if(fields.length > FIELDS.length)
            throw new ArrayIndexOutOfBoundsException("The size of Field[] paramter of the method createUser(Field[]) exceeds the expected size. Expected : " + FIELDS.length + ". Passed : " + fields.length);
        //check if primaryId is enabled
        Field primaryField = null;
        if(PRIMARY_FIELD != null) {
            //check for all required_fields and the primary_field
            for(Field f : fields) {
                if(INIT.isRequiredField(f)) {
                    requiredFieldCount--;
                    if(f.equals(PRIMARY_FIELD)) {
                        //check for duplicate primaryId
                        if(primaryField != null)
                            throw new DuplicateFieldException("Duplicate Primary Field. There can be only one Primary Field.");
                        primaryField = f;
                        //check if user already exists
                        if(getUser(primaryField) != null)
                            throw new DuplicateUserException("Duplicate User. The user with primary field " + primaryField + " already exists.");
                    }
                }
                //construct columnNameStr and valueStr
                columnNameStr += f.getName() + ", ";
                if(VARCHAR_PATTERN.matcher(f.getSqlType()).matches())
                    valueStr += "'" + f.getValue() + "', ";
                else
                    valueStr += f.getValue() + ", ";
            }
        }
        else {
            //check for all required_fields
            for(Field f : fields) {
                if(INIT.isRequiredField(f))
                    requiredFieldCount--;
                //construct columnNameStr and valueStr
                columnNameStr += f.getName() + ", ";
                if(VARCHAR_PATTERN.matcher(f.getSqlType()).matches()) {
                    valueStr += "'" + f.getValue() + "', ";
                }
                else
                    valueStr += f.getValue() + ", ";
            }
        }
        if(requiredFieldCount != 0)
                throw new RequiredFieldException("All RequiredFields must be present in the Field[] paramter of the method createUser(Field[]).");
        //remove last ','
        columnNameStr = columnNameStr.substring(0, columnNameStr.length() - 2);
        valueStr = valueStr.substring(0, valueStr.length() - 2);
        //insert the new user record into the table
        try {
            INIT.DATABASE_MANAGER.openConnection();
            INIT.DATABASE_MANAGER.executeUpdate("INSERT INTO " + userTableName + "(" + columnNameStr + ") VALUES(" + valueStr + ")");
            INIT.DATABASE_MANAGER.closeConnection();
        }
        catch(SQLException ex) {
            System.out.println(ex.toString());
        }
        User user = new User(primaryField, fields);
        return user;
    }
    
    public void deleteUser(Field primaryField)throws InvalidFieldException, NullFieldException {
        //check if primaryId is enabled
        if(PRIMARY_FIELD == null)
            throw new NullFieldException("The Auth has 'null' primary field i.e. this Auth object has not been configured to accept primary fields");
        //validate primaryField
        if(!primaryField.equals(PRIMARY_FIELD))
            throw new InvalidFieldException("The parameter Field of the method deleteUser(Field) does not matches the expected value. Expected : (Field)" + PRIMARY_FIELD.getName() + ". Passes : (Field)" + primaryField.getName());
        //check if user does not exists
        if(getUser(primaryField) == null)
            return;
        //else drop user record from database
        try {
            INIT.DATABASE_MANAGER.openConnection();
            INIT.DATABASE_MANAGER.executeUpdate("DELETE FROM " + userTableName + " WHERE " + PRIMARY_FIELD.getName() + "="+ primaryField.getValue());
            INIT.DATABASE_MANAGER.closeConnection();
        }
        catch(SQLException ex) {
            System.out.println(ex.toString());
        }
    }
    
    public User getUser(Field primaryField)throws InvalidFieldException, NullFieldException {
        //check if primaryId is enabled
        if(PRIMARY_FIELD == null)
            throw new NullFieldException("The Auth has 'null' primary field i.e. this Auth object has not been configured to accept primary fields");
        //validate primaryField
        if(!primaryField.equals(PRIMARY_FIELD))
            throw new InvalidFieldException("The parameter Field of the method deleteUser(Field) does not matches the expected value. Expected : (Field)" + PRIMARY_FIELD.getName() + ". Passes : (Field)" + primaryField.getName());
        //get user data from the database
        Field userFields[] = new Field[FIELDS.length];
        try {
            INIT.DATABASE_MANAGER.openConnection();
            String primaryFieldValue = primaryField.getValue() + "";
            if(VARCHAR_PATTERN.matcher(primaryField.getSqlType()).matches())
                primaryFieldValue = "'" + primaryFieldValue + "'";
            ResultSet set = INIT.DATABASE_MANAGER.executeQuery("SELECT * FROM " + userTableName + " WHERE " + PRIMARY_FIELD.getName() + "="+ primaryFieldValue + ";");
            if(!set.next()) // if empty set
                return null;
            //else construct user field array
            int i = 0;
            for(Field f : FIELDS) {
                Field newF = f;
                newF.setValue(set.getObject(f.getName(), String.class)); //DOUBT IF IT'LL WORK
                userFields[i++] = newF;
            }
            INIT.DATABASE_MANAGER.closeConnection();
        }
        catch(SQLException ex) {
            System.out.println(ex.toString());
        }
        //return user
        return (new User(primaryField, userFields));
    }
    
    public User getUser(Field primaryID, Field passwd) {
        //VALIDATE USER PASSWORD
        //IF VALIDATED, getUser(primaryID)
        //return user
        return null;
    }
    
}
