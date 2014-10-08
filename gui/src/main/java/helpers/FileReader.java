package helpers;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import constants.LabelConstants;
import fileManagers.InfoFileManager;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class FileReader
{

    public static String readFile(File filepath)
    {
        String output = "";

        if (filepath == null || filepath.isDirectory())
            return output;
        
//        try
//        {
//
//            FileInputStream fstream = new FileInputStream(filepath);
//            DataInputStream in = new DataInputStream(fstream);
//            BufferedReader br = new BufferedReader(new InputStreamReader(in));
//            String strLine;
//
//            while ((strLine = br.readLine()) != null)
//            {
//                output += strLine;
//
//            }
//
//            in.close();
//        }
//        catch (Exception e)
//        {
//            System.err.println("Error: " + e.getMessage());
//        }
        try {
            output = FileUtils.readFileToString(filepath);
        } catch (IOException ex) {
            Logger.getLogger(FileReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return output;
    }

    public static String getTestDescription(File test)
    {
        return "Test start: " +
                InfoFileManager.getInfo(test, InfoFileManager.FORMATTED_START_TEST_TIME) + "</br>" +
                "SessionId: " +
                InfoFileManager.getInfo(test, InfoFileManager.SESSIONID) + "</br>" +
                "Test result: " +
                (Boolean.parseBoolean(InfoFileManager.getInfo(test, InfoFileManager.TEST_EXECUTION_STATUS)) ? LabelConstants.TEST_PASSED : LabelConstants.TEST_FAILED);
    }

}
