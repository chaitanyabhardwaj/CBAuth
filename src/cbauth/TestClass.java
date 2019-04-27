
package cbauth;

import cbauth.exception.*;
import java.sql.*;

public class TestClass {
    
    public static void main(String args[]) {
        //Testing CBAuth
        //Create a Field name
        AutoIncrementId id = AutoIncrementId.getInstance("ID");
        TextField name = TextField.create("fname", "My_name");
        TextField email = TextField.create("email", "foo@bar.com");
        TextField password = TextField.create("password", "pass12345");
        //Create a database
        DatabaseManager dbmanager = null;
        try {
            dbmanager = DatabaseManager.getInstance("//localhost:3306/my_db_test", "<username>", "<password>");
        }
        catch(InvalidURLException ex) {
            System.out.print(ex.toString());
        }
        AuthInitialiser init = null;
        if(dbmanager != null)
            init = new AuthInitialiser(dbmanager, "my_key_1234", new Field[]{id,email,name,password});
        Auth auth = null;
        try {
            init.setRequiredField(email);
            init.setRequiredField(password);
            init.setPrimaryField(email);
            init.setPasswordField(password);
        }
        catch(NullFieldException | RequiredFieldException ex) {
            System.out.println(ex.toString());
        }
        try {
            auth = Auth.initAuth(init);
        }
        catch(SQLException ex) {
            System.out.println(ex.toString());
        }
        if(auth != null) {
            try {
                auth.createUser(new Field[]{email,name,password});
            }
            catch(DuplicateFieldException | DuplicateUserException | InvalidFieldException | NullFieldException | RequiredFieldException ex) {
                System.out.println(ex.toString());
            }
        }
    }
    
}
