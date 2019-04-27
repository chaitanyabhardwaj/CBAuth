/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cbauth;

/**
 *
 * @author chaitanyabhardwaj
 */
public class TextField extends Field<String> {
    
    private TextField(String name, String sqlType) {
        super(name, sqlType);
    }
    
    public static TextField create(String fieldName, String text) {
        return create(fieldName, text, 500);
    }
    
    public static TextField create(String fieldName, String text, int maxLength) {
        String sqlType = "VARCHAR(" + maxLength + ")";
        TextField textField = new TextField(fieldName, sqlType);
        textField.setValue(text);
        return textField;
    }
    
}
