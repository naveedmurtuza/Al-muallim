/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.almuallim.theholyquran.parsers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 *
 * @author Naveed
 */
public class TanzilTextParser extends TanzilParser {

    private boolean lineNumbers;

    public TanzilTextParser(boolean lineNumbers) {
        this.lineNumbers = lineNumbers;
    }

    public TanzilTextParser() {
        this(false);
    }

    @Override
    public List<String> parse(Reader r) {
        List<String> verses = new ArrayList<>(6236);
        try (BufferedReader br = new BufferedReader(r)) {
            Stream<String> lines = br.lines();
            Iterator<String> iterator = lines.iterator();
            while (iterator.hasNext()) {
                String line = iterator.next();
                if (line.startsWith("#") || line.isEmpty()) {
                    continue;
                }
                verses.add(
                        lineNumbers
                        ? line.split(Pattern.quote("|"))[2]
                        : line);
            }
        } catch (IOException iox) {
            //    TODO:// throw new Exception();
        }
//        try (Scanner scanner = new Scanner(r)) {
//            while (scanner.hasNextLine()) {
//                String line = scanner.nextLine();
//                if (line.startsWith("#") || line.isEmpty()) {
//                    continue;
//                }
//                verses.add(
//                        lineNumbers
//                        ? line.split(Pattern.quote("|"))[2]
//                        : line);
//            }
//        }
        if (verses.size() != 6236) {
            //    TODO:// throw new Exception();
        }
        return verses;
    }
}
