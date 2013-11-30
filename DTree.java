/*
 * DTree.java
 * 
 * Author: Robb Dooling (robbdooling@gmail.com)
 * 
 */

import java.io.*;
import java.util.Scanner;
import java.util.Arrays;

public class DTree {

    // user calls this program with arguments for:
    // input file name, output file name, and percentage of the data to be used for training
    static File inputFile;
    static File outputFile;
    static int percent;
    
    // variables defined in input file:
    // attributes in decision tree and their fields, number of examples, output symbols for class designation
    static String[] attributes;
    static Character[][] aFields;
    static String[][] aFieldDescriptors;
    static Character[][] examples;
    static Character[] outputSymbols;
    static String[] oSymbolDescriptors;   
    
    /*
     * main
     * handle input and validation
     * @param   args        all arguments the user included along with the call to DTree
     * @return  None
     */
    public static void main (String[] args) {
        int argc = args.length;
        
        // make sure user entered 3 arguments
        if (argc != 3) {
            System.out.println("Usage: java DTree inputfile outputfile percent");
            System.exit(0);
        }
        
        // assign and vaidate input filename
        inputFile = new File(args[0]);
        if (!inputFile.exists()) {
            inputFile = new File(args[0] + ".txt");
            if (!inputFile.exists()) {
                System.out.println("File not found: " + args[0]);
                System.exit(0);
            }
        }
        
        // assign output filename
        outputFile = new File(args[1]);
        
        // assign and validate percentage
        try {
            percent = Integer.parseInt(args[2]);
        } catch (Exception e) {
            System.out.println("Value for percent must be between 1 and 100");
            System.exit(0);
        }
        
        if (percent < 1 || percent > 100) {
            System.out.println("Value for percent must be between 1 and 100");
            System.exit(0);
        }
        
        readInputFile(inputFile);
    }
    
    /*
     * readInputFile
     * create decision tree parameters based on input file
     * @param   f        File object to read from
     * @return  None
     */
    public static void readInputFile(File f) {
        try {

            BufferedReader br = new BufferedReader(new FileReader(f));

            // first line of file = number of attributes
            attributes = new String[Integer.parseInt(br.readLine())];
            aFields = new Character[attributes.length][];
            aFieldDescriptors = new String[attributes.length][];

            // second line of file = number of examples
            // each example has x + 1 attributes (+1 for the class designation)
            examples = new Character[Integer.parseInt(br.readLine())][attributes.length + 1];

            // third line of file = output symbols for class designations, in the format:
            // classes:t=true,f=false (here, the output symbols are t and f)
            String[] classes = br.readLine().split(",");
            outputSymbols = new Character[classes.length];
            oSymbolDescriptors = new String[classes.length];

            // skip over "classes:" in first comma-delimited string; get char at 8 instead of 0
            outputSymbols[0] = classes[0].charAt(8);
            oSymbolDescriptors[0] = classes[0].substring(classes[0].indexOf('=') + 1);
            
            // for all other comma-delimited strings, get char at 0 to determine symbol
            for (int i = 1; i < classes.length; i++) {
                outputSymbols[i] = classes[i].charAt(0);
                oSymbolDescriptors[i] = classes[i].substring(classes[i].indexOf('=') + 1);
            }
            
            // fourth line = skip
            br.readLine();
                       
            // starting at fifth line, get attributes
            for (int i = 0; i < attributes.length; i++) {
                                
                // split line wherever it contains ',' or ':'
                String[] attributeLine = br.readLine().split(",|\\:");
                            
                attributes[i] = attributeLine[0];
                                
                // add fields and descriptors for this line
                aFields[i] = new Character[attributeLine.length-1];
                aFieldDescriptors[i] = new String[attributeLine.length-1];

                // for each attribute field
                for (int j = 0; j < attributeLine.length - 1; j++) {
                    // first character only = attribute field synbol
                    aFields[i][j] = attributeLine[j+1].charAt(0);
                    // the rest of the string after the '=' is the attribute field descriptor
                    aFieldDescriptors[i][j] = attributeLine[j+1].substring(attributeLine[j+1].indexOf('=') + 1);
                }
            }
            
            // starting at line after attributes, get examples
            // (examples is an 2D array of characters, and each group of characters is one example)
            for (int i = 0; i < examples.length; i++) {
                String exampleLine = br.readLine();
                for (int j = 0; j < examples[i].length; j++) {
                    // skip over every second char (avoid commas)
                    examples[i][j] = exampleLine.charAt(j*2);
                }
            }
            
            System.out.println("output symbols: " + Arrays.toString(outputSymbols));
            System.out.println("output symbol descriptors: " + Arrays.toString(oSymbolDescriptors));
            System.out.println("attributes: " + Arrays.toString(attributes));
            System.out.println("aFields: ");
            
            for (int i = 0; i < aFields.length; i++) {
                for (int j = 0; j < aFields[i].length; j++) {
                    System.out.print(aFields[i][j] + " ");
                }
            }
                        System.out.println("");
            System.out.println("aFieldDescriptors: ");
            for (int i = 0; i < aFieldDescriptors.length; i++) {
                for (int j = 0; j < aFieldDescriptors[i].length; j++) {
                    System.out.print(aFieldDescriptors[i][j] + " ");
                }
            }
                                    System.out.println("");
            System.out.println("examples: ");
            for (int i = 0; i < examples.length; i++) {
                for (int j = 0; j < examples[i].length; j++) {
                    System.out.print(examples[i][j]);
                }
                                                    System.out.println("");
            }
            
            br.close();
                
        } catch (Exception e) {
            System.out.println("Error while reading input file. Exception: " + e.getMessage());
            System.exit(0);
        }
    }

}

