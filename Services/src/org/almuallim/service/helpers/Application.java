/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.almuallim.service.helpers;

/**
 *
 * @author Naveed
 */
public class Application {

    public static String getHome() {
        return System.getProperty("netbeans.user");
    }
}
