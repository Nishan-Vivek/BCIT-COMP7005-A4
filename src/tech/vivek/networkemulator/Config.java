package tech.vivek.networkemulator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.Properties;


/**
 * Created by Nishan on 11/29/2015.
 */
class Config {
    public static int receiverPort = 9871;
    public static String receiverIP = "localhost";
    public static int transmitterPort = 9870;
    public static String transmitterIP = "localhost";
    public static String NetworkSimIP = "localhost";
    public static int NetworkListenForReceiverPort = 9869;
    public static int NetworkListenForTransmitterPort = 9868;
    public static int ACKtimeout = 50;
    public static int RetryLimit = 10;
    public static int PercentageOfDroppedPackets = 30;
    public static int NetworkDelay = 0;
    public static String stringPayload = ("The British Columbia Institute of Technology (also referred to as BCIT), is a" +
            " public polytechnic institution of higher education. The post-secondary institute has five campuses located" +
            " in the Metro Vancouver region, with its main campus in Burnaby, British Columbia, Canada. There is also" +
            " the Aerospace Technology Campus in Richmond, the Marine Campus in the City of North Vancouver, Downtown" +
            " campus in Vancouver, and Annacis Island Campus in Delta.[2] It is provincially chartered through" +
            " legislation in the College and Institute Act.[3]");




    public static boolean setConfigParameters()  {
        InputStream inputStream = null;
        try {
            Properties prop = new Properties();
            String propFileName = "Config.properties";

            inputStream = new FileInputStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }



            // get the property value and assign
            receiverPort = Integer.parseInt(prop.getProperty("receiverPort"));
            //System.out.println(receiverPort);
            receiverIP = prop.getProperty("receiverIP");
            transmitterPort = Integer.parseInt(prop.getProperty("transmitterPort"));
            transmitterIP = prop.getProperty("transmitterIP");
            NetworkSimIP = prop.getProperty("NetworkSimIP");
            NetworkListenForReceiverPort = Integer.parseInt(prop.getProperty("NetworkListenForReceiverPort"));
            NetworkListenForTransmitterPort = Integer.parseInt(prop.getProperty("NetworkListenForTransmitterPort"));
            ACKtimeout = Integer.parseInt(prop.getProperty("ACKtimeout"));
            RetryLimit = Integer.parseInt(prop.getProperty("RetryLimit"));
            PercentageOfDroppedPackets = Integer.parseInt(prop.getProperty("PercentageOfDroppedPackets"));
            NetworkDelay = Integer.parseInt(prop.getProperty("NetworkDelay"));
            stringPayload = prop.getProperty("stringPayload");


        } catch (Exception e) {
            System.out.println("Exception: " + e);
            return false;
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }


    }


    public static Logger createlogger(String componentName) {
        Logger Log = Logger.getLogger(componentName);
        FileHandler logFileHandler = null;
        try {
            logFileHandler = new FileHandler(componentName + ".log");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.addHandler(logFileHandler);
        SimpleFormatter formatter = new SimpleFormatter();
        logFileHandler.setFormatter(formatter);
        return Log;
    }


}
