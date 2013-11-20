/*
 * DTree.java
 * 
 * Author: Robb Dooling (robbdooling@gmail.com)
 * 
 */

import java.io.File;
import java.util.Scanner;

public class DTree {

    // user calls this program with arguments for:
    // input file name, output file name, and percentage of the data to be used for training
    static File inputFile;
    static File outputFile;
    static int percent;
    
    // variables defined in input file:
    // attributes in decision tree and their fields, number of examples, output symbols for class designation
    static String[] attributes;
    static Char[][] attributeFields;
    static String[][] attributeFieldDescriptors;
    static Char[][] examples;
    static Char[] outputSymbols;
    static String[] outputSymbolDescriptors;   
    
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
            // first line of file = number of attributes
            attributes = new String[Integer.parseInt(f.readLine())];
            
            // second line of file = number of examples
            // each example has x attributes
            examples = new Char[Integer.parseInt(f.readLine())][attributes.length];
            
            // third line of file = output symbols for class designations, in the format:
            // classes:t=true,f=false (here, the output symbols are t and f)
            String[] classes = f.readLine().split(",");
            outputSymbols = new Char[classes.length];
            
            // skip over "classes:" in first comma-delimited string; get char at 8 instead of 0
            outputSymbols[0] = classes[0][8];
            
            // for all other comma-delimited strings, get char at 0 to determine symbol
            for (int i = 1; i < classes.length; i++) {
                outputSymbols[i] = classes[i][0];  
            }
            
            // fourth line = skip
            f.readLine();
            
            // starting at fifth line, get attributes
            for (int i = 0; i < attributes.length; i++) {
                String[] attributeLine = f.readLine().split(",|\\:");
                
            }
            
        } catch (Exception e) {
            System.out.println("Error while reading input file. Exception: " + e.getMessage());
            System.exit(0);
        }
    }

}

