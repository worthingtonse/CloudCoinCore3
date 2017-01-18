import java.util.Random;
import java.io.*;
import java.nio.file.Files;

public class CommandInterpreter{
    /* Load items for all methods to share*/
    /**
     * Keyboard Reader is used to accept user input for the console apps
     */
    public static KeyboardReader reader = new KeyboardReader();
    /**
     * used to manage console apps command availablility
     */

    // public static String rootFolder = System.getProperty("user.dir") + File.separator +"bank" + File.separator ;
    public static String rootFolder = "C:\\CloudCoins" + File.separator ;
    public static String importFolder = rootFolder + "Import" + File.separator;
    public static String importedFolder = rootFolder +  "Imported" + File.separator;
    public static String trashFolder = rootFolder +  "Trash" + File.separator;
    public static String suspectFolder = rootFolder +  "Suspect" + File.separator;
    public static String frackedFolder = rootFolder +  "Fracked" + File.separator;
    public static String bankFolder = rootFolder +  "Bank" + File.separator;
    public static String templateFolder = rootFolder +  "Templates" + File.separator;
    public static String counterfeitFolder = rootFolder +  "Counterfeit" + File.separator;
    public static String directoryFolder = rootFolder +  "Directory" + File.separator;;
    public static String exportFolder = rootFolder +  "Export" + File.separator;

    public static String prompt = "CloudCoin Bank";
    public static String[] commandsAvailable = new String[]{"import","show coins", "export", "fix fracked","quit", "show folders"};
    public static int timeout = 10000;//Milliseconds to wait until the request is ended. 
    public static FileUtils fileUtils = new FileUtils( rootFolder, importFolder, importedFolder, trashFolder, suspectFolder, frackedFolder, bankFolder, templateFolder, counterfeitFolder, directoryFolder, exportFolder  );

    public static Random myRandom = new Random();//This is used for naming new chests

    public static void main(String[] args) {

        printWelcome();

        run();//Makes all commands available and loops

        System.out.println("Thank you for using CloudCoin Core 3. Goodbye.");
    }//End main

    public static void run() {
        boolean restart = false;
        while( ! restart )
        {
            System.out.println( "=======================");
            System.out.println( prompt + " Commands Available:");
            int commandCounter = 1;
            for ( String command : commandsAvailable){
                System.out.println( commandCounter + ". "+ command );
                commandCounter++;
            }

            System.out.print( prompt+">");
            String commandRecieved = reader.readString( commandsAvailable );          

            switch( commandRecieved.toLowerCase() ){     
                case "show coins": showCoins(); break;
                case "import": importCoins();  break;
                case "export": export(); break;
                case "fix fracked": fixFracked(); break;
                case "show folders": showFolders(); break;
                case "quit": System.out.println("Goodbye!"); System.exit(0); break;
                default: System.out.println("Command failed. Try again."); break;
            }//end switch
        }//end while
    }//end run method

    /**
     * Print out the opening message for the player. 
     */
    public static void printWelcome() {
        System.out.println("Welcome to CloudCoin Foundation Opensource.");
        System.out.println("The Software is provided as is, with all faults, defects and errors,");
        System.out.println("and without warranty of any kind.");
    }//End print welcome

    public static void showCoins(){
        //This is for consol apps.
        Banker bank =  new Banker( fileUtils );
        int[] bankTotals = bank.countCoins( bankFolder );
        int[] frackedTotals = bank.countCoins( frackedFolder);
        //int[] counterfeitTotals = bank.countCoins( counterfeitFolder );

        System.out.println("Your Bank Inventory:");            
        int grandTotal = bankTotals[0] + frackedTotals[0]  ; 
        System.out.println("Total: " + grandTotal );
        System.out.print("  1s: "+ (bankTotals[1] + frackedTotals[1]) + " || ");
        System.out.print("  5s: "+ (bankTotals[2] + frackedTotals[2]) + " ||");
        System.out.print(" 25s: "+ (bankTotals[3] + frackedTotals[3]) + " ||" );
        System.out.print("100s: "+ (bankTotals[4] + frackedTotals[4]) + " ||");
        System.out.println("250s: "+ (bankTotals[5] + frackedTotals[5])  );

    }//end show

    public static void showFolders(){
        System.out.println("Your Root folder is " + rootFolder );
        System.out.println("Your Import folder is " + importFolder );
        System.out.println("Your Imported folder is " + importedFolder );
        System.out.println("Your Suspect folder is " + suspectFolder );
        System.out.println("Your Trash folder is " + trashFolder );
        System.out.println("Your Bank folder is " + bankFolder );
        System.out.println("Your Fracked folder is " + frackedFolder );
        System.out.println("Your Templates folder is " + templateFolder );
        System.out.println("Your Directory folder is " + directoryFolder );
        System.out.println("Your Counterfeits folder is " + counterfeitFolder );
        System.out.println("Your Export folder is " + exportFolder ); 
    }//end show folders

    public static void importCoins( ){
        System.out.println("Loading all CloudCoins in your import folder:" + importFolder);
        Importer importer = new Importer( fileUtils );
        if( ! importer.importAll() ){
         return;//There were no file to import
        };//Move all coins to seperate JSON files in the the suspect folder.
        Detector detector = new Detector( fileUtils, 10000 );
        int[] detectionResults = detector.detectAll(); 
        System.out.println("Total Received in bank: " + (detectionResults[0] + detectionResults[2]) );//And the bank and the fractured for total
        System.out.println("Total Counterfeit: " + detectionResults[1]);
        showCoins();
    }//end import

    public static void export(){
        Banker bank = new Banker( fileUtils );
        int[] bankTotals = bank.countCoins( bankFolder );
        int[] frackedTotals = bank.countCoins( frackedFolder );

        System.out.println("Your Bank Inventory:");            
        int grandTotal = bankTotals[0] + frackedTotals[0]  ; 
        System.out.println("Total: " + grandTotal );
        System.out.print("  1s: "+ (bankTotals[1] + frackedTotals[1]) + " || ");
        System.out.print("  5s: "+ (bankTotals[2] + frackedTotals[2]) + " ||");
        System.out.print(" 25s: "+ (bankTotals[3] + frackedTotals[3]) + " ||" );
        System.out.print("100s: "+ (bankTotals[4] + frackedTotals[4]) + " ||");
        System.out.println("250s: "+ (bankTotals[5] + frackedTotals[5])  );

        //state how many 1, 5, 25, 100 and 250
        int exp_1 = 0;
        int exp_5 = 0;
        int exp_25 = 0;
        int exp_100 = 0;
        int exp_250 = 0;

        System.out.println("Do you want to export your CloudCoin to (1)jpgs or (2) stack (JSON) file?");
        int file_type = reader.readInt(1,2 ); //1 jpg 2 stack

        if( bankTotals[1] + frackedTotals[1] > 0 ){
            System.out.println("How many 1s do you want to export?");
            exp_1 = reader.readInt(0,bankTotals[1] + frackedTotals[1]  );
        }//if 1s not zero 
        if( bankTotals[2] + frackedTotals[2] > 0 ){
            System.out.println("How many 5s do you want to export?");
            exp_5 = reader.readInt(0,bankTotals[2] + frackedTotals[2]  );
        }//if 1s not zero 
        if( bankTotals[3] + frackedTotals[3] > 0 ){
            System.out.println("How many 25s do you want to export?");
            exp_25 = reader.readInt(0,bankTotals[3] + frackedTotals[3]  );
        }//if 1s not zero 
        if( bankTotals[4] + frackedTotals[4] > 0 ){
            System.out.println("How many 100s do you want to export?");
            exp_100 = reader.readInt(0,bankTotals[4] + frackedTotals[4]  );
        }//if 1s not zero 
        if( bankTotals[5] + frackedTotals[5] > 0 ){
            System.out.println("How many 250s do you want to export?");
            exp_250 = reader.readInt(0,bankTotals[5] + frackedTotals[5]  );
        }//if 1s not zero 

        //move to export
        Exporter exporter = new Exporter( fileUtils );

        System.out.println("What tag will you add to the file?");
        String tag = reader.readString(false);
        System.out.println("Exporting to:" + exportFolder);
        if( file_type == 1){
            exporter.writeJPEGFiles(exp_1, exp_5, exp_25, exp_100, exp_250, tag);
            //stringToFile( json, "test.txt");
        }else{
            exporter.writeJSONFile(exp_1, exp_5, exp_25, exp_100, exp_250, tag );
        }//end if type jpge or stack
        System.out.println("Exporting CloudCoins Completed.");
    }//end export One

    public static void fixFracked(){
        //Load coins from file in to banks fracked array
        Frack_Fixer frack_fixer = new Frack_Fixer( fileUtils, timeout );
        Banker bank = new Banker( fileUtils );
        int[] frackedTotals = bank.countCoins(rootFolder + frackedFolder );
        System.out.println("You  have " + frackedTotals[0] + " fracked coins.");
        int[] frackedResults = frack_fixer.fixAll(); 
        //REPORT ON DETECTION OUTCOME
        System.out.println("Results of Fix Fractured:");
        System.out.println("Good and Moved in Bank: " + frackedResults[0] );
        System.out.println("Counterfeit and Moved to trash: " + frackedResults[1] );
        System.out.println("Still Fracked and Moved to Fracked: " + frackedResults[2] );
    }//end fix fracked
}//EndMain
