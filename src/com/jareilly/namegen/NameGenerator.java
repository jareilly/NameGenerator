package com.jareilly.namegen;

import java.util.Arrays;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;

import com.opencsv.*; // http://opencsv.sourceforge.net/
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Takes a column of a CSV file and generates pairwise terms from all words as output.
 * The column entries are separated into a unique set of words.
 * Then alliterative and nonalliterative pair combinations are generated.
 *
 * @author James Reilly
 */
public class NameGenerator {

    // 2nd column of CSV has all candidate words
    private static final int CSV_COLUMN = 1; // 0, 1, ...
    private static final String CSV_FILE = "/tmp/inputWords.csv";
    private static final char CSV_SEPARTATOR = ';'; // could be ',' depending on locale
    
    private static final String PARSED_WORDS_FILE = "/tmp/parsedWords.txt";
    private static final String ALLITERATIVE_FILE = "/tmp/alliterativePairs.txt";
    private static final String NONALLITERATIVE_FILE = "/tmp/nonAlliterativePairs.txt";
    
    private static String capitalize(final String line) {
        String retval = "";
        if (line.length() > 0)
           retval = Character.toUpperCase(line.charAt(0)) + line.substring(1);

        return retval;
    }
        
    public static void main(String[] args) {     
        try (CSVReader reader = new CSVReader(new FileReader(CSV_FILE), CSV_SEPARTATOR)) {
            
            // Create input word hashset
            String [] nextLine;
            ArrayList<String> words = new ArrayList<>();
            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                if (!nextLine[1].isEmpty()){
                    String [] wds = nextLine[CSV_COLUMN].replaceAll("\\s+", " ").split(" ");
                    for(String s : wds) {
                        if (!s.isEmpty()) {
                            String tmp = capitalize(s);
                            if (!words.contains(s)) {
                                words.add(capitalize(s));
                            }
                        }
                    }
                }
            }
            
            // clean up the list
            HashSet hs = new HashSet();
            hs.addAll(words);
            words.clear();
            words.addAll(hs);
            words.sort(null);

            //String str = "Puppy kitten lion tiger lamb power fun rythm love you her him positive emotion sweet find get go higher strong faster new safe strong";
            System.out.println("Input words: " + words.size() + " from " + CSV_FILE);
            for (String w : words) {
                if (w != null)
                    System.out.println(w);
            }
            System.out.println();

            
            // Create output files

            File wordsFile = new File(PARSED_WORDS_FILE);
            
            // alliterative output
            try (FileWriter wordsFw = new FileWriter(wordsFile)) {
                File alliterativeNamesFile = new File(ALLITERATIVE_FILE);
                try (FileWriter alliterativeFw = new FileWriter(alliterativeNamesFile)) {
                    for (String w : words) {
                        for (String y : words) {
                            if (!w.equals(y) && w.charAt(0) == y.charAt(0)) {
                                alliterativeFw.write(w + " " + y + "\t\t" + y + " " + w + "\n");
                            }
                        }
                        alliterativeFw.write("\n");
                    }                   
                    alliterativeFw.flush();
                }
                wordsFw.flush();
            } catch (IOException e) {
                System.err.println("Writer IOException: " + e.getMessage());
	    }
            
            // non-alliterative output
            try (FileWriter wordsFw = new FileWriter(wordsFile)) {
                File nonAlliterativeNamesFile = new File(NONALLITERATIVE_FILE);
                try (FileWriter nonAlliterativeFw = new FileWriter(nonAlliterativeNamesFile)) {
                    for (String w : words) {
                        for (String y : words) {
                            if (!w.equals(y) && w.charAt(0) != y.charAt(0)) {
                                nonAlliterativeFw.write(w + " " + y + "\t\t" + y + " " + w + "\n");
                            }
                        }
                        nonAlliterativeFw.write("\n");
                    }                   
                    nonAlliterativeFw.flush();
                }
                wordsFw.flush();
            } catch (IOException e) {
                System.err.println("Writer IOException: " + e.getMessage());
	    }
            
        } catch (IOException e) {
            System.err.println("Reader IOException: " + e.getMessage());
	}
        
        System.out.println("Finished !");
    }
}