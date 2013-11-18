/*
 * DTree.java
 * 
 * Author: Robb Dooling (robbdooling@gmail.com)
 * 
 */

import java.io.File;
import java.util.Scanner;

public class DTree {

    // user-specified input file, output file, and percentage of the data to be used for training
    static File input;
    static File output;
    static int percent;
    
    // handle input and validation
    public static void main (String[] args) {
        int argc = args.length;
        
        // make sure user entered 3 arguments
        if (argc != 3) {
            System.out.println("Usage: java DTree inputfile outputfile percent");
            System.exit(0);
        }
        
        // assign and vaidate input filename
        input = new File(args[0]);
        if (!input.exists()) {
            input = new File(args[0] + ".txt");
            if (!input.exists()) {
                System.out.println("File not found: " + args[0]);
                System.exit(0);
            }
        }
        
        // assign output filename
        output = new File(args[1]);
        
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
    }

}

