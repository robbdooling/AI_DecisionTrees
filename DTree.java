/*
 * DTree.java
 * 
 * Author: Robb Dooling (robbdooling@gmail.com)
 * 
 */

import java.io.*;
import java.lang.Double;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;
import java.text.*;

public class DTree {

    // user calls this program with arguments for:
    // input file name, output file name, and percentage of the data to be used for training
    static File inputFile;
    static File outputFile;
    static int percent;
    
    // variables defined in input file:
    // attributes in decision tree and their fields, number of examples, output symbols for class designation
    static int numOfAttributes;
    static ArrayList<String> attributes;
    static ArrayList<ArrayList<Character>> aFields;
    static ArrayList<ArrayList<String>> aFieldDescriptors;
    
    static int numOfExamples;
    static ArrayList<ArrayList<Character>> examples;
    
    static ArrayList<Character> outputSymbols;
    static ArrayList<String> oSymbolDescriptors;
    
    static DecimalFormat df = new DecimalFormat("0.000");
        
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
        
        /*
        System.out.print("\noutput symbols: ");
        for (int i = 0; i < outputSymbols.size(); i++) {
            System.out.print(outputSymbols.get(i) + " ");
        }
        System.out.println("\noutput symbol descriptors: ");
        for (int i = 0; i < oSymbolDescriptors.size(); i++) {
            System.out.print(oSymbolDescriptors.get(i) + " ");
        }
        System.out.print("\nattributes: ");
        for (int i = 0; i < attributes.size(); i++) {
            System.out.print(attributes.get(i) + " ");
        }
        System.out.print("\naFields: ");
        for (int i = 0; i < aFields.size(); i++) {
            System.out.print(aFields.get(i) + " ");
        }
        
        for (int i = 0; i < aFields.size(); i++) {
            for (int j = 0; j < aFields.get(i).size(); j++) {
                System.out.print(aFields.get(i).get(j) + " ");
            }
        }
        
        System.out.println("\naFieldDescriptors: ");
        for (int i = 0; i < aFieldDescriptors.size(); i++) {
            for (int j = 0; j < aFieldDescriptors.get(i).size(); j++) {
                System.out.print(aFieldDescriptors.get(i).get(j) + " ");
            }
        }
        
        System.out.println("\nexamples: ");
        for (int i = 0; i < examples.size(); i++) {
            for (int j = 0; j < examples.get(i).size(); j++) {
                System.out.print(examples.get(i).get(j));
            }
                                                System.out.println("");
        }
        */
        
        System.out.println("The Decision Tree Built With " + (percent / examples.size()) + " Traning Samples");
        decideNextTreeLevel(0, attributes, aFields, aFieldDescriptors, examples);
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
            numOfAttributes = Integer.parseInt(br.readLine());
            attributes = new ArrayList<String>();
            aFields = new ArrayList<ArrayList<Character>>();
            aFieldDescriptors = new ArrayList<ArrayList<String>>();

            // second line of file = number of examples
            // each example has x + 1 attributes (+1 for the class designation)
            numOfExamples = Integer.parseInt(br.readLine());
            examples = new ArrayList<ArrayList<Character>>();

            // third line of file = output symbols for class designations, in the format:
            // classes:t=true,f=false (here, the output symbols are t and f)
            String[] classes = br.readLine().split(",");
            outputSymbols = new ArrayList<Character>();
            oSymbolDescriptors = new ArrayList<String>();

            // skip over "classes:" in first comma-delimited string; get char at 8 instead of 0
            outputSymbols.add(classes[0].charAt(8));
            oSymbolDescriptors.add(classes[0].substring(classes[0].indexOf('=') + 1));
            
            // for all other comma-delimited strings, get char at 0 to determine symbol
            for (int i = 1; i < classes.length; i++) {
                outputSymbols.add(classes[i].charAt(0));
                oSymbolDescriptors.add(classes[i].substring(classes[i].indexOf('=') + 1));
            }
            
            // fourth line = skip
            br.readLine();
                      
            // starting at fifth line, get attributes
            for (int i = 0; i < numOfAttributes; i++) {        
                // split line wherever it contains ',' or ':'
                ArrayList<Character> newAttributeFields = new ArrayList<Character>();
                ArrayList<String> newFieldDescriptors = new ArrayList<String>();
                String[] attributeLine = br.readLine().split(",|\\:");
                                           
                attributes.add(attributeLine[0]);
                                            
                // for each attribute field
                for (int j = 0; j < attributeLine.length - 1; j++) {
                    // first character only = attribute field symbol
                    newAttributeFields.add(attributeLine[j+1].charAt(0));
                    // the rest of the string after the '=' is the attribute field descriptor
                    newFieldDescriptors.add(attributeLine[j+1].substring(attributeLine[j+1].indexOf('=') + 1));
                }
                
                aFields.add(newAttributeFields);
                aFieldDescriptors.add(newFieldDescriptors);
            }
            
            // starting at line after attributes, get examples
            // (examples is an 2D array of characters, and each group of characters is one example)
            for (int i = 0; i < numOfExamples; i++) {
                ArrayList<Character> examplesSet = new ArrayList<Character>();
                
                String exampleLine = br.readLine();
                for (int j = 0; j < numOfAttributes + 1; j++) {
                    // skip over every second char (avoid commas)
                    examplesSet.add(exampleLine.charAt(j*2));
                }
                
                examples.add(examplesSet);
            }            
            br.close();
                
        } catch (Exception e) {
            System.out.println("Error while reading input file. Exception: " + e.getMessage());
            System.exit(0);
        }
    }
    
    /*
     * decideTree
     * calculate entropies to help us figure out levels of the decision tree
     * @param   attributes
     * @param   aFields
     * @param   examples
     * @return  None
     */
    public static void decideNextTreeLevel(int treeLevel,
                                           ArrayList<String> c_attributes,
                                           ArrayList<ArrayList<Character>> c_aFields,
                                           ArrayList<ArrayList<String>> c_aFieldDescriptors,
                                           ArrayList<ArrayList<Character>> examples) {
        
        ArrayList<ArrayList<Double>> c_aFieldEntropies = new ArrayList<ArrayList<Double>>();
        ArrayList<ArrayList<Integer>> c_aFieldFrequencies = new ArrayList<ArrayList<Integer>>();
           
        ArrayList<Double> c_aWeightedEntropies = new ArrayList<Double>();
        
        // determine which attribute will be the root of the tree
        for (int i = 0; i < c_attributes.size(); i++) {
            
            //System.out.println("Let's try " + c_attributes.get(i) + ":");
            // find entropy of each field for this attribute
            
            ArrayList<Double> entropiesSet = new ArrayList<Double>();
            ArrayList<Integer> frequenciesSet = new ArrayList<Integer>();
            double weightedEntropy = 0.00;
            
            for (int j = 0; j < c_aFields.get(i).size(); j++) {
                
                int numTrue = 0;
                int numFalse = 0;
                int totalExamples = 0;
                
                // look for instances of the field in examples
                for (int k = 0; k < examples.size(); k++) {
                    // get k-th example and attribute field located at j+1
                    if (examples.get(k).get(i+1) == c_aFields.get(i).get(j)) {
                        
                        if (examples.get(k).get(0) == 't') {
                            numTrue++;
                        }
                        else {
                            numFalse++;
                        }
                    }
                }
                
                int newFrequency = numTrue + numFalse;
                double newEntropy = calculateEntropy(numTrue, numFalse, numTrue + numFalse);
                
                /*
                System.out.println("     Entropy of field " + c_aFields.get(i).get(j) + "=" +
                                   c_aFieldDescriptors.get(i).get(j) + " is " + df.format(newEntropy));
                                   */
                
                // add entropy * frequency to weighted entropies
                weightedEntropy += newEntropy * newFrequency;
                
                frequenciesSet.add(newFrequency);
                entropiesSet.add(newEntropy);
            }
            
            c_aFieldEntropies.add(entropiesSet);
            c_aFieldFrequencies.add(frequenciesSet);
            c_aWeightedEntropies.add(weightedEntropy);

            // divide weighted entropy by total number of examples to get average
            for (int j = 0; j < c_aWeightedEntropies.size(); j++) {
                c_aWeightedEntropies.set(j, c_aWeightedEntropies.get(j) / examples.size());   
            }
            
            //System.out.println("     Weighted entropy of attribute " + c_attributes.get(i) + " is " + df.format(c_aWeightedEntropies.get(i)));
            //System.out.println("     Information gain of attribute " + c_attributes.get(i) + " is " + df.format((1.000 - c_aWeightedEntropies.get(i))));
        }
        
        // find attribute with highest information gain (or lowest weighted entropy)
        int minIndex = 0;
        // start with maximum possible value so first candidate for min will pass
        double minValue = Double.MAX_VALUE;
        
        for (int i = 0; i < c_aWeightedEntropies.size(); i++) {
            if (c_aWeightedEntropies.get(i) < minValue) {
                minValue = c_aWeightedEntropies.get(i);
                minIndex = i;
            }
        }
        
        // attribute with highest info gain has been determined
        // this attribute is now the tree level
        
        System.out.print("\nLevel " + treeLevel + " Attribute: " + c_attributes.get(minIndex));
        
        // check each branch (field) under that attribute
        for (int i = 0; i < c_aFields.get(minIndex).size(); i++) {
        
            // if entropy is 0, we can give final decision right away
            if (c_aFieldEntropies.get(minIndex).get(i) == 0.000) {
                System.out.print("\nAt level " + (treeLevel + 1) + ", "
                                   + c_aFields.get(minIndex).get(i) + "=" + c_aFieldDescriptors.get(minIndex).get(i)
                                   + ", decision: " );
                // retrieve decision
                // look for any instance of the field in examples
                for (int k = 0; k < examples.size(); k++) {
                    // get k-th example and attribute field located at j+1
                    if (examples.get(k).get(minIndex+1) == c_aFields.get(minIndex).get(i)) {
                        if (examples.get(k).get(0) == 't') {
                            System.out.print("t=true\n");
                            examples.remove(k);
                            k--;
                        }
                        else {
                            System.out.print("f=false\n");
                            examples.remove(k);
                            k--;
                        }
                        break;
                    }
                }
            }
            
            // otherwise, investigate the branch more
            else {
                if (treeLevel < numOfAttributes) {
                    System.out.println("\nSplit tree on " + c_aFields.get(minIndex).get(i) + "=" + c_aFieldDescriptors.get(minIndex).get(i));
                    // recursively decide next level
                    
                    // create new attributes, aFields, aFieldDescriptors
                    ArrayList<String> n_attributes = new ArrayList<String>(c_attributes.size());
                    for (int j = 0; j < c_attributes.size(); j++)
                    {
                        if (j != minIndex) {
                            n_attributes.add(c_attributes.get(j));
                        }
                    }
                    
                    ArrayList<ArrayList<Character>> n_aFields = new ArrayList<ArrayList<Character>>(c_aFields.size());
                    for (int j = 0; j < c_aFields.size(); j++) {
                        if (j != minIndex) {
                            n_aFields.add(c_aFields.get(j));
                        }
                    }
                    
                    ArrayList<ArrayList<String>> n_aFieldDescriptors = new ArrayList<ArrayList<String>>(c_aFieldDescriptors.size());
                    for (int j = 0; j < c_aFields.size(); j++) {
                        if (j != minIndex) {
                            n_aFieldDescriptors.add(c_aFieldDescriptors.get(j));
                        }
                    }
                    
                    decideNextTreeLevel(treeLevel + 1,
                                        n_attributes,
                                        n_aFields,
                                        n_aFieldDescriptors,
                                        examples);
                }
            }
        }
    }
    
    /*
     * calculateEntropy
     * @param t     number of examples that returned "true" with this field
     * @param f     number of examples that returned "false" with this field
     * @param n     total number of examples
     * @return entropy of the attribute field based on t and n
     */
    public static double calculateEntropy(double t, double f, double n) {
        double entropy = ((-1 * t/n) * (Math.log(t/n) / Math.log(2)))
                         - ((f/n) * (Math.log(f/n) / Math.log(2)));
            
        // if the result is not a number, just return 0.000
        if (Double.isNaN(entropy)) {
            entropy = 0.000;
        }
        return entropy;
    }

}

