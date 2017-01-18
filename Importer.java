import java.io.*;
import org.json.*;
import java.util.*;
/**
 * Everytime you want to import some files into the suspect folder, you create a new importer
 * It pops up and imports the files. It takes jpegs and turns them into JSON. It takes stack files
 * and divides them into individual files. The files will then be ready to be detected.
 * 
 * @author Sean H. Worthington
 * @version 1/14/2017
 */
public class Importer
{
    // instance variables - replace the example below with your own
    FileUtils fileUtils;

    /**
     * Constructor for objects of class Importer
     */
    public Importer( FileUtils fileUtils )
    {
        this.fileUtils = fileUtils;
    }

    public boolean importAll(){
        String[] fnames = fileUtils.selectFileNamesInFolder( fileUtils.importFolder);//Get a list of all in the folder except the directory "imported"
        System.out.println("Importing the following files:");
        for(int i=0; i< fnames.length; i++){//Loop through each file. 
            System.out.println( fnames[i]);
            if( ! importOneFile( fnames[i] ) ){
                fileUtils.moveToTrashFolder( fnames[i]);
            }//If file failed to be loaded - Move to trash
        }//end for each file name
        if( fnames.length == 0){ 
            System.out.println("There were not CloudCoins to import. Please place our CloudCoin .jpg and .stack files in your imports folder at " + fileUtils.importFolder);
            return false;
        }else{
            return true;
        }
    }//end import

    public boolean importOneFile( String fname ){
        /*WHAT IS THE FILE'S EXTENSION??*/
        String extension = "";
        int indx = fname.lastIndexOf('.');
        if (indx > 0) {
            extension = fname.substring(indx+1);
        }
        extension = extension.toLowerCase();
        if( extension.equals("jpeg") || extension.equals("jpg") ){
            if( ! importJPEG( fname )){ 
                fileUtils.moveToTrashFolder( fname );
                return false;// System.out.println("Failed to load JPEG file");
            }
        }else{
            if( ! importStack( fname )){ 
                fileUtils.moveToTrashFolder( fname );
                return false;// System.out.println("Failed to load .stack file");
            }
        }//end if jpg
        //change imported file to have a .imported extention
        fileUtils.writeToSuspectFolder( fname );
        fileUtils.moveToImportedFolder( fname );
        return true;
    }

    public boolean importJPEG(String fileName)
    {
        boolean isSuccessful = false;// System.out.println("Trying to load: " + importFolder + fileName );
        try{
            CloudCoin tempCoin = new CloudCoin( fileUtils.importFolder + fileName );
            tempCoin.writeTo( fileUtils.suspectFolder );
            //System.out.println("File saved to " + importFolder + fileName);
            fileUtils.moveToImportedFolder( fileName );
            return true;
        }catch(FileNotFoundException ex){ System.out.println("File not found: " + fileName);
        }catch(IOException ioex){ System.out.println("IO Exception:" + fileName);
        }//end try catch
        return isSuccessful;
    }

    /**
     * This method is used to load .chest and .stack files that are in JSON notation.
     * 
     * @param  loadFilePath: The path to the Bank file and the name of the file. 
     * @param  Security: How the ANs are going to be changed during import (Random, Keep, password).
     */
    public boolean importStack( String fileName ) {  
        boolean isSuccessful = false; // System.out.println("Trying to load: " + directory + loadFilePath );
        String incomeJson = ""; // String new fileName = coinCount +".CloudCoin.New"+ rand.nextInt(5000) + "";
        try{
            incomeJson = fileUtils.importJSON( fileName ); //  System.out.println(incomeJson);
        }catch( IOException ex ){
            System.out.println( "Error importing stack " + ex );
            return false;
        }
        JSONArray incomeJsonArray;
        try{
            JSONObject o = new JSONObject( incomeJson );
            incomeJsonArray = o.getJSONArray("cloudcoin");
            CloudCoin tempCoin = null;
            for (int i = 0; i < incomeJsonArray.length(); i++) {  // **line 2**
                JSONObject childJSONObject = incomeJsonArray.getJSONObject(i);
                int nn     = childJSONObject.getInt("nn");
                int sn     = childJSONObject.getInt("sn");
                JSONArray an = childJSONObject.getJSONArray("an");
                String[] ans = fileUtils.toStringArray(an);
                String ed     = childJSONObject.getString("ed");//JSONArray aoid = childJSONObject.getJSONArray("aoid"); // String[] aoids = toStringArray(aoid) DO NOT IMPORT ANY AOID INFO
                Dictionary aoid = null;//Wipe any old owner notes //this.newCoins[i] = new CloudCoin( nn, sn, toStringArray(an), ed, aoid, security );//This could cause memory issues.   
                tempCoin = new CloudCoin( nn, sn, ans, ed, aoid, "suspect" );//security should be change or keep for pans. //tempCoin.consoleReport();
                tempCoin.writeTo( fileUtils.suspectFolder  );//Put in bank folder with suspect extension
                fileUtils.moveToImportedFolder( fileName );
            }//end for each coin
            isSuccessful = true;
        }catch( JSONException ex){
            System.out.println("Stack File "+ fileName+ " Corrupt. See CloudCoin file api and edit your stack file: " + ex);
            return isSuccessful;
        }//try 
        return isSuccessful;
    }//end load income

}//End Importer Class
