import java.io.*;
import java.util.*;
/**
 * Write a description of class Exporter here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Exporter
{
    // instance variables - replace the example below with your own
    String[] listOfFileNames;
    int numberOfCoinsExported;
    FileUtils fileUtils;

    /**
     * Constructor for objects of class Exporter
     */
    public Exporter( FileUtils fileUtils )
    {
        this.fileUtils = fileUtils;
    }

    /**
     * Method exportJpeg Exports a bunch of JPG images. 
     *
     * @param m1 How many jpgs to create of the 1s denomination. 
     * @param m5 How many jpgs to create of the 5s denomination. 
     * @param m25 How many jpgs to create of the 25s denomination. 
     * @param m100 How many jpgs to create of the 100s denomination. 
     * @param m250 How many jpgs to create of the 250s denomination. 
     * @param tag A string that will be included in the jpg file name.
     * @param directory to put the jpeg into. 
     */
    public void writeJPEGFiles(int m1, int m5, int m25, int m100, int m250, String tag ){
        boolean jsonExported = true;
        int totalSaved = m1 + ( m5 * 5 ) + ( m25 * 25 ) + (m100 * 100 ) + ( m250  * 250 );//Track the total coins
        int coinCount = m1 + m5 + m25 + m100 + m250;
        /* CONSRUCT JSON STRING FOR SAVING */
        String[] coinsToDelete =  new String[coinCount];
        String[] bankedFileNames = fileUtils.selectFileNamesInFolder( fileUtils.bankFolder );//list all file names with bank extension
        String[] frackedFileNames = fileUtils.selectFileNamesInFolder( fileUtils.frackedFolder );//list all file names with bank extension
        bankedFileNames = fileUtils.concatArrays( bankedFileNames, frackedFileNames);//Add the two arrays together

        String path = fileUtils.exportFolder;
        /* SET JPEG, WRITE JPEG and DELETE CLOUDCOINS*/
        int c = 0;//c= counter
        String d =""; //Denomination
        CloudCoin jpgCoin = null;
        byte[] jpeg;
        //Look at all the money files and choose the ones that are needed.
        for(int i =0; i< bankedFileNames.length; i++ ){
            String bankFileName = fileUtils.bankFolder + bankedFileNames[i];
            String frackedFileName = fileUtils.frackedFolder + bankedFileNames[i];
            System.out.println("Reading from "+ bankFileName);
            String denomination = bankedFileNames[i].split("\\.")[0];//Get's denominiation
            try{
                if( denomination.equals("1") && m1 > 0 ){ 
                     jpegWriteOne( path, tag, bankFileName, frackedFileName);
                     m1--;
                }else if( denomination.equals("5") && m5 > 0 ){ 
                    jpegWriteOne(  path, tag,  bankFileName, frackedFileName);
                     m5--;
                }else if( denomination.equals("25") && m25 > 0 ){ 
                   jpegWriteOne(   path, tag, bankFileName, frackedFileName);
                     m25--;
                }else if( denomination.equals("100") && m100 > 0 ){ 
                    jpegWriteOne(  path, tag,  bankFileName, frackedFileName);
                     m100--;
                }else if( denomination.equals("250") && m250 > 0 ){ 
                    jpegWriteOne(  path, tag,  bankFileName, frackedFileName);
                     m250--;
                }else {/*skip it because it is not needed*/}//end if file is needed for stack
                
                if( m1 ==0 && m5 ==0 && m25 == 0 && m100 == 0 && m250 == 0 ){break;}//Break if all the coins have been called for.     
            
            }catch(FileNotFoundException ex){
            }catch(IOException ioex){ }
        }//for each 1 note  
    }//end export

    public void jpegWriteOne( String path, String tag, String bankFileName, String frackedFileName) throws IOException, FileNotFoundException{
                    if( fileUtils.ifFileExists( bankFileName )){//Is it a bank file or fracked file
                        CloudCoin jpgCoin = fileUtils.cloudCoinFromFile( bankFileName );
                        byte[] jpeg = fileUtils.makeJpeg( jpgCoin); 
                        if( fileUtils.writeJpeg(path, tag, jpeg, jpgCoin.fileName )){ 
                            fileUtils.deleteCoin( bankFileName); 
                        } 
                    }else{
                        CloudCoin jpgCoin = fileUtils.cloudCoinFromFile( frackedFileName );   
                        byte[] jpeg = fileUtils.makeJpeg( jpgCoin); 
                        if( fileUtils.writeJpeg(path, tag, jpeg, jpgCoin.fileName )){ 
                            fileUtils.deleteCoin( frackedFileName ); 
                        }
                    }
    }//end jpeg prep
    
    
    /**
     * Method writeJSONFile Exports a JSON file with a .stack exension with a lot of CloudCoins in it. 
     *
     * @param m1 How many json to create of the 1s denomination. 
     * @param m5 How many json to create of the 5s denomination. 
     * @param m25 How many json to create of the 25s denomination. 
     * @param m100 How many json to create of the 100s denomination. 
     * @param m250 How many json to create of the 250s denomination. 
     * @param tag A string that will be included in the json file name.
     * @param directory to put the json into. 
     */
    public boolean writeJSONFile( int m1, int m5, int m25, int m100, int m250, String tag ){
        boolean jsonExported = true;
        int totalSaved = m1 + ( m5 * 5 ) + ( m25 * 25 ) + (m100 * 100 ) + ( m250  * 250 );//Track the total coins
        int coinCount = m1 + m5 + m25 + m100 + m250;
        /* CONSRUCT JSON STRING FOR SAVING */
        String[] coinsToDelete =  new String[coinCount];
        String[] bankedFileNames = fileUtils.selectFileNamesInFolder( fileUtils.bankFolder );//list all file names with bank extension
        String[] frackedFileNames = fileUtils.selectFileNamesInFolder( fileUtils.frackedFolder );//list all file names with bank extension
        bankedFileNames = fileUtils.concatArrays(bankedFileNames, frackedFileNames);//Add the two arrays together

        //Check to see the denomination by looking at the file start
        int c = 0;//c= counter

        String json = "{" + System.getProperty("line.separator");
        json +=   "\t\"cloudcoin\": [" + System.getProperty("line.separator") ;
        String d ="";   
        //Put all the JSON together and add header and footer
        String f = "";
        for(int i =0; i< bankedFileNames.length; i++ ){
            d = bankedFileNames[i].split("\\.")[0];
            String bname = fileUtils.bankFolder + bankedFileNames[i];
            String fname = fileUtils.frackedFolder + bankedFileNames[i];
            if( d.equals("1") && m1 > 0 ){ 
                if( c !=0 ){ json += ",\n";} 
                if( fileUtils.ifFileExists( bname )){//Is it a bank file or fracked file
                    json += getOneJSON( bname );
                    coinsToDelete[c]= bname; c++; 
                }else{
                    json += getOneJSON( fname );
                    coinsToDelete[c]= fname; c++; 
                }
                m1--;   //Get the clean JSON of the coin
            }//end if coin is a 1
            if( d.equals("5") && m5 > 0 ){  
                if( c !=0 ){ json += ",\n";}
                if( fileUtils.ifFileExists( bname )){//Is it a bank file or fracked file
                    json += getOneJSON( bname );
                    coinsToDelete[c]= bname; c++; 
                }else{
                    json += getOneJSON( fname );
                    coinsToDelete[c]= fname; c++; 
                }
                m5--;   
            }//end if coin is a 5
            if( d.equals("25") && m25 > 0 ){ 
                if( c !=0 ){ json += ",\n";} 
                if( fileUtils.ifFileExists( bname )){//Is it a bank file or fracked file
                    json += getOneJSON( bname );
                    coinsToDelete[c]= bname; c++; 
                }else{
                    json += getOneJSON( fname );
                    coinsToDelete[c]= fname; c++; 
                }
                m25--  ; 
            }//end if coin is a 25
            if( d.equals("100") && m100 > 0 ){  
                if( c !=0 ){ json += ",\n";}
                if( fileUtils.ifFileExists( bname )){//Is it a bank file or fracked file
                    json += getOneJSON( bname );
                    coinsToDelete[c]= bname; c++; 
                }else{
                    json += getOneJSON( fname );
                    coinsToDelete[c]= fname; c++; 
                }
                m100--;   
            }//end if coin is a 100
            if( d.equals("250") && m250 > 0 ){  
                if( c !=0 ){ json += ",\n";}
                coinsToDelete[c]=bankedFileNames[i]; c++;  
                if( fileUtils.ifFileExists( bname )){//Is it a bank file or fracked file
                    json += getOneJSON( bname );
                    coinsToDelete[c]= bname; c++; 
                }else{
                    json += getOneJSON( fname );
                    coinsToDelete[c]= fname; c++; 
                }
                m250--;   
            }//end if coin is a 250
            if( m1 ==0 && m5 ==0 && m25 == 0 && m100 == 0 && m250 == 0 ){break;}//Break if all the coins have been called for.     
        }//for each 1 note
        json += "\t] "+ System.getProperty("line.separator"); 
        json += "}";  

        /* FIGURE OUT NEW STACK NAME AND SAVE TO FILE */
        String filename = fileUtils.exportFolder + File.separator + totalSaved +".CloudCoins." + tag + ".stack";
        if(  fileUtils.ifFileExists(filename)){//tack on a random number if a file already exists with the same tag
            //Add random 
            Random rnd = new Random();
            int tagrand = rnd.nextInt(999);
            filename = fileUtils.exportFolder   + File.separator + totalSaved +".CloudCoins." + tag + tagrand + ".stack";
        }//end if file exists

        if ( fileUtils.stringToFile( json, filename ) ){
            /* DELETE EXPORTED CC FROM BANK */ 
            for(int cc = 0; cc < coinsToDelete.length; cc++){
                // System.out.println("Deleting "+ path + coinsToDelete[cc].fileName + "bank");
                fileUtils.deleteCoin( coinsToDelete[cc] );
            }//end for

        }else{
            //Write Failed
            jsonExported = false;
        }//end if write was good
        return jsonExported;
    }//end export

    /**
     * Method getOneJSON Extracts a single CloudCoin from a JSON file. 
     *
     * @param fileName A parameter
     * @return The JSON of the coin.
     */
    public String getOneJSON( String fileName){
        try{
            String jsonData = fileUtils.loadFileToString( fileName );
            //extract single coin. 
            int indexOfFirstSquareBracket = fileUtils.ordinalIndexOf( jsonData, "[", 0);
            int indexOfLastSquareBracket = fileUtils.ordinalIndexOf( jsonData, "]", 2);
            return jsonData.substring( indexOfFirstSquareBracket, indexOfLastSquareBracket );

        }catch( FileNotFoundException ex){
            return "";
        }

    }//end get one json


}
