/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.almuallim.theholyquran.api;



/**
 *
 * @author Naveed
 */
public class Sajda {
    private int[] sajdaVerses;
        private String type;

        public Sajda(int[] sajdaVerses, String type) {
            this.sajdaVerses = sajdaVerses;
            this.type = type;
        }

        public int[] getSajda() {
            return sajdaVerses;
        }

        public String getType() {
            return type;
        }
}
