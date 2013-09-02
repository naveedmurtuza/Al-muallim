/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.almuallim.theholyquran.parsers;

import java.io.Reader;
import java.util.List;

/**
 *
 * @author Naveed
 */
public abstract class TanzilParser {


    
    public  abstract List<String> parse(Reader stream);
}
