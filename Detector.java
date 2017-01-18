import java.io.*;
import java.util.*;
/**
 * Reads files from the suspect folder, detects them.
 * Writes them to either the bank, counterfeit or fracked 
 * 
 * @author Sean H. Worthington
 * @version 1/14/2017
 */
public class Detector
{
    // instance variables - replace the example below with your own
    RAIDA raida;
    FileUtils fileUtils;
    
    Detector( FileUtils fileUtils, int timeout )
    {
        raida = new RAIDA( timeout );
        this.fileUtils = fileUtils;
    }//end Detect constructor

    public int[] detectAll(){
    //LOAD THE .suspect COINS ONE AT A TIME AND TEST THEM
        int[] results = new int[3];//[0] Coins to bank, [1] Coins to fracked [1] Coins to Counterfeit
        String[] suspectFileNames  = fileUtils.selectFileNamesInFolder( fileUtils.suspectFolder );
        int totalValueToBank = 0;
        int totalValueToCounterfeit = 0;
        int totalValueToFractured = 0;
        CloudCoin newCC;
        for(int j = 0; j < suspectFileNames.length; j++){
            try{
                //System.out.println("Construct Coin: "+rootFolder + suspectFileNames[j]);
                newCC = new CloudCoin( fileUtils.suspectFolder + suspectFileNames[j]);
                System.out.println("" );
                System.out.println("" );
                System.out.println("Detecting SN #"+ newCC.sn +", Denomination: "+ newCC.getDenomination() );
                CloudCoin detectedCC =  raida.detectCoin( newCC );//Checks all 25 GUIDs in the Coin and sets the status. 
                
                //detectedCC.saveCoin( detectedCC.extension );//save coin as bank
                detectedCC.consoleReport();
                switch( detectedCC.extension ){
                    case "bank": totalValueToBank++; detectedCC.writeTo( fileUtils.bankFolder ); break;
                    case "fractured": totalValueToFractured++; detectedCC.writeTo( fileUtils.frackedFolder ); break;//fracked still ads value to the bank
                    case "counterfeit": totalValueToCounterfeit++; detectedCC.writeTo( fileUtils.counterfeitFolder ); break;
                }//end switch on the place the coin will go 
                fileUtils.deleteCoin( fileUtils.suspectFolder + suspectFileNames[j] );
            }catch(FileNotFoundException ex){
            }catch(IOException ioex){            }//end try catch
        }//end for each coin to import
        //System.out.println("Results of Import:");
        results[0] = totalValueToBank; 
        results[1] = totalValueToCounterfeit; //System.out.println("Counterfeit and Moved to trash: "+totalValueToCounterfeit);
        results[2] = totalValueToFractured;//System.out.println("Fracked and Moved to Fracked: "+ totalValueToFractured);
        return results;
    }//end detect all
}
