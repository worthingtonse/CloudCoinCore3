import java.io.*;
import java.util.*;
/**
 * Takes everything in the Fracked folder and tries to fix it. 
 * 
 * @author Sean H. Worthington
 * @version 1/14/2017
 */
public class Frack_Fixer
{
    // instance variables - replace the example below with your own
    FileUtils fileUtils;
    int totalValueToBank;
    int totalValueToFractured;
    int totalValueToCounterfeit;
    RAIDA raida;

    /**
     * Constructor for objects of class Frack_Fixer
     */
    public Frack_Fixer( FileUtils fileUtils, int timeout )
    {
        this.fileUtils = fileUtils;
        raida = new RAIDA( timeout );
    }

    public int[] fixAll(){
        int[] results = new int[3];
        String[] frackedFileNames = fileUtils.selectFileNamesInFolder( fileUtils.frackedFolder );
        int totalValueToBank = 0;
        int totalValueToCounterfeit = 0;
        int totalValueToFractured = 0;
        CloudCoin newCC;
        for(int j = 0; j < frackedFileNames.length; j++){
            try{
                //System.out.println("Construct Coin: "+rootFolder + suspectFileNames[j]);
                newCC = fileUtils.cloudCoinFromFile( fileUtils.frackedFolder + frackedFileNames[j]);
                System.out.println("UnFracking SN #"+ newCC.sn +", Denomination: "+ newCC.getDenomination() );
                CloudCoin fixedCC =  raida.fixCoin( newCC );//Will attempt to unfrack the coin. 
                fixedCC.consoleReport();
                switch( fixedCC.extension ){
                    case "bank": totalValueToBank++; fileUtils.writeTo( fileUtils.bankFolder, fixedCC ); break;
                    case "fractured": totalValueToFractured++; fileUtils.writeTo( fileUtils.frackedFolder, fixedCC  ); break;//fracked still ads value to the bank
                    case "counterfeit": totalValueToCounterfeit++; fileUtils.writeTo( fileUtils.counterfeitFolder, fixedCC  ); break;
                }//end switch on the place the coin will go 
                deleteCoin( fileUtils.frackedFolder + frackedFileNames[j] );
            }catch(FileNotFoundException ex){
            }catch(IOException ioex){            }//end try catch
        }//end for each coin to import
        //System.out.println("Results of Import:");
        results[0] = totalValueToBank; 
        results[1] = totalValueToCounterfeit; //System.out.println("Counterfeit and Moved to trash: "+totalValueToCounterfeit);
        results[2] = totalValueToFractured;//System.out.println("Fracked and Moved to Fracked: "+ totalValueToFractured);
        return results;
    }//end fix all

    /***
     * Given directory path return an array of strings of all the files in the directory.
     * @parameter directoryPath The location of the directory to be scanned
     * @return filenames The names of all the files in the directory
     */
    public String[] selectFileNamesInFolder(String directoryPath) {
        File dir = new File(directoryPath);
        String candidateFileExt = "";
        Collection<String> files  =new ArrayList<String>();
        if(dir.isDirectory()){
            File[] listFiles = dir.listFiles();

            for(File file : listFiles){
                if(file.isFile()) {//Only add files with the matching file extension
                    files.add(file.getName());
                }
            }
        }
        return files.toArray(new String[]{});
    }//End select all file names in a folder

    /**
     * Method deleteCoin
     *
     * @param path The folder that the coin is located in
     * @return true if the coin is deleted and false if it does not get deleted. 
     */
    public boolean deleteCoin( String path ){
        boolean deleted = false;
        File f  = new File( path ); //System.out.println("Deleteing Coin: "+path + this.fileName + extension);
        try {
            deleted = f.delete(); if(deleted){ }else{
                // System.out.println("Delete operation is failed.");
            }//end else
        }catch(Exception e){
            e.printStackTrace();
        }
        return deleted;
    }//end delete file
}//End Frack Fixer

