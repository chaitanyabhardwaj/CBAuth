
package cbauth.exception;

public class RequiredFieldException extends Exception {
    
    public RequiredFieldException() {
    }

    public RequiredFieldException(String msg) {
        super(msg);
    }
}
