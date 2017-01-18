import java.io.*;
import org.json.*;
import java.util.*;
/**
 * Help to read, write and change files
 * 
 * @author Sean H. Worthington
 * @version 1/17/2017
 */
public class FileUtils
{
    // instance variables
    public  String rootFolder; 
    public  String importFolder;
    public  String importedFolder; 
    public  String trashFolder;
    public  String suspectFolder; 
    public  String frackedFolder; 
    public  String bankFolder; 
    public  String templateFolder; 
    public  String counterfeitFolder; 
    public  String directoryFolder; 
    public  String exportFolder; 

    /**
     * Constructor for objects of class FileUtils
     */
    public FileUtils( String rootFolder, String importFolder, String importedFolder, String trashFolder, String suspectFolder, String frackedFolder, String bankFolder, String templateFolder, String counterfeitFolder, String directoryFolder, String exportFolder)
    {
        // initialise instance variables
        this.rootFolder = rootFolder ;
        this.importFolder = importFolder;
        this.importedFolder = importedFolder;
        this.trashFolder = trashFolder;
        this.suspectFolder = suspectFolder;
        this.frackedFolder = frackedFolder;
        this.bankFolder = bankFolder;
        this.templateFolder = templateFolder;
        this.counterfeitFolder = counterfeitFolder;
        this.directoryFolder = directoryFolder;
        this.exportFolder = exportFolder;
    }//End constructor

    
    public String importJSON( String jsonfile) throws FileNotFoundException {
        String jsonData = "";
        BufferedReader br = null;
        try {
            String line;
            br = new BufferedReader(new FileReader( importFolder + jsonfile ));
            while ((line = br.readLine()) != null) {
                jsonData += line + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                return "";
            }
        }
        return jsonData;
    }//en d json test

    public void moveToTrashFolder(String fileName){
        String source = importFolder + fileName;
        String target = trashFolder + fileName;
        new File(source).renameTo(new File(target));
    }

    public void moveToImportedFolder( String fileName ){
        String source = importFolder + fileName;
        String target = importedFolder + fileName;
        new File(source).renameTo(new File(target));
    }

    public void writeToSuspectFolder( String fileName ){
        String source = importFolder + fileName;
        String target = suspectFolder + fileName;
        new File(source).renameTo(new File(target));
    }

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

    public static String[] toStringArray(JSONArray array) {
        if(array==null)
            return null;

        String[] arr=new String[array.length()];
        for(int i=0; i<arr.length; i++) {
            arr[i]=array.optString(i);
        }
        return arr;
    }//end toStringArray

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

    /**
     * Method concatArrays Takes two arrays and adds them together
     *
     * @param a The first array
     * @param b The second array
     * @return The arrays together
     */
    public String[] concatArrays(String[] a, String[] b) {
        int aLen = a.length;
        int bLen = b.length;
        String[] c= new String[aLen+bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }

    /**
     * Method ifFileExists Checks to see if a file exists
     *
     * @param filePathString A parameter
     * @return True if the file exists, false if the file does not exist.
     */
    public boolean ifFileExists( String filePathString ){
        File f = new File(filePathString);
        if(f.exists() && !f.isDirectory()) { 
            return true;
        }
        return false;
    }//end if file Exists

    /**
     * Method loadFileToString
     *
     * @param jsonfile The path and anme of a file that contains CloudCoins in the form of JSON.
     * @return The return value
     */
    public String loadFileToString( String jsonfile) throws FileNotFoundException {
        String jsonData = "";
        BufferedReader br = null;
        try {
            String line;
            br = new BufferedReader(new FileReader( jsonfile ));
            while ((line = br.readLine()) != null) {
                jsonData += line + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return jsonData;
    }//end load file to string

    public int ordinalIndexOf(String str, String substr, int n) {
        int pos = str.indexOf(substr);
        while (--n > 0 && pos != -1)
            pos = str.indexOf(substr, pos + 1);
        return pos;
    }

    /***
     * Given a string and a file name, write the string to the harddrive.
     * @parameter text The string to go into the file
     * @paramerter filename The name to be given to the file.
     */
    public boolean stringToFile( String text, String filename) {
        boolean writeGood =  false;
        try(  PrintWriter out = new PrintWriter( filename )  ){
            out.println( text );
            writeGood = true;
        }catch( FileNotFoundException ex){
            System.out.println(ex);
        }
        return writeGood;
    }//end string to file 
}
