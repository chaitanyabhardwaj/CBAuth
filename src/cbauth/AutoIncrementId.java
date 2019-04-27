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
public class AutoIncrementId extends Field {
    
    private AutoIncrementId(String fieldName, String sqlType) {
        super(fieldName, sqlType);
    }
    
    public static AutoIncrementId getInstance(String fieldName) {
        return new AutoIncrementId(fieldName, "INT NOT NULL AUTO_INCREMENT, KEY (" + fieldName + ")");
    }
    
}
