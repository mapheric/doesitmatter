import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class ProjectTwo
{
    /*
     * The main method of the lexical analyser.
     * The name of the source file is assumed to be in args[0].
     */
    public static void main(String[] args)
    {
        /*
         * This if-else block checks if a filename is given in the arguments.
         * If no filename is specified, the program terminates.
         */
        if(args.length < 1)
        {
            System.out.println("Enter a filename in the command line.");
            System.exit(1);
        }
        else
        {
            ArrayList<String> fileContents = readFile(args[0]);         // Get file contents.

            /*
             * This if-else block checks if fileContents is null.
             * If it is null, the readFile() method would have thrown a
             * FileNotFoundException, and the program should terminate.
             * Otherwise, the lexical analyser begins its work.
             */
            if(fileContents == null)
            {
                System.exit(1);
            }
            else
            {
                LexicalAnalyser lexer = new LexicalAnalyser();
                ArrayList<Token> masterTokenList = lexer.tokenise(fileContents);

                //System.out.println("Tokens: " + masterTokenList);

                try
                {
                    //ParserNew parser = new ParserNew(masterTokenList);
                	Parser2 parser = new Parser2(masterTokenList);
                    parser.parse();
                }
                catch(IllegalArgumentException e)
                {
                    System.out.println("ERROR: File is empty.");
                    System.exit(1);
                }
            }
        }
    }
    
    /**
     * This method reads in the contents of the source file provided to the lexical analyser.
     * The text is split by line, and added to an <code>ArrayList</code>.
     * @param filename - The name of the source file.
     * @return An <code>ArrayList</code> containing each line of text in the source file.
     */
    public static ArrayList<String> readFile(String filename)
    {
        ArrayList<String> contents = new ArrayList<String>();
        try
        {
            Scanner fileInput = new Scanner(new BufferedReader(new FileReader(filename)));

            while(fileInput.hasNextLine())
            {
                contents.add(fileInput.nextLine());
            }

            fileInput.close();
            return contents;
        }
        catch(FileNotFoundException e)
        {
            System.out.println("ERROR: File " + filename + " not found.");
            return null;
        }
    }
}