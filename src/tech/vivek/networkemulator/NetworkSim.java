package tech.vivek.networkemulator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Created by Nishan on 11/29/2015.
 */
public class NetworkSim {


    public static boolean gotconfig = Config.setConfigParameters();

    private static final int listenForTransmitterPort = Config.NetworkListenForTransmitterPort;
    private static final int listenForReceiverPort = Config.NetworkListenForReceiverPort;
    private static final String receiverIP = Config.receiverIP;
    private static final int receiverPort = Config.receiverPort;
    private static final String transmitterIP = Config.transmitterIP;
    private static final int transmitterPort = Config.transmitterPort;
    private static final int DropPercentage = Config.PercentageOfDroppedPackets;

    private static final Logger Log = Config.createlogger("NetworkSim");


    public static void main(String[] args) {
        Log.info("Network Simulator started");


        NetworkSimRunnable R1 = new NetworkSimRunnable("Transmitter-Relay", listenForTransmitterPort, receiverPort, receiverIP, DropPercentage, Log);
        R1.start();

        NetworkSimRunnable R2 = new NetworkSimRunnable("Receiver-Relay ", listenForReceiverPort, transmitterPort, transmitterIP, DropPercentage, Log);
        R2.start();
    }

}

class NetworkSimRunnable implements Runnable {
    private Thread t;
    private final String threadName;
    private final int listeningPort;
    private final int targetPort;
    private InetAddress targetIP;
    private final int dropPercentage;
    private final Logger Log;

    NetworkSimRunnable(String name, int listeningPort, int targetPort, String targetIP, int dropPercentage, Logger log) {
        threadName = name;
        this.listeningPort = listeningPort;
        this.targetPort = targetPort;
        this.dropPercentage = dropPercentage;
        this.Log = log;
        try {
            this.targetIP = InetAddress.getByName(targetIP);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Log.info("Creating " + threadName);

    }

    public void run() {
        //Setup random chance generator
        //FasterChanceGenerator chanceGenerator = new FasterChanceGenerator();

        //Setup Socket
        DatagramSocket socket = null;
        DatagramPacket incomingDatagram = null;
        try {
            socket = new DatagramSocket(listeningPort);
            byte[] incomingData = new byte[1024];
            incomingDatagram = new DatagramPacket(incomingData, incomingData.length);
        } catch (Exception e) {
            e.printStackTrace();
        }


        while (true) {
            //Listen for incoming datagram
            try {
                socket.receive(incomingDatagram);
                //System.out.println(threadName + " got a packet.");
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Route incoming datagram to target IP and port
            incomingDatagram.setAddress(targetIP);
            incomingDatagram.setPort(targetPort);

            //Send datagram to target with Percentage chance of dropped packet.
            if (!chanceGenerator(dropPercentage)) {
                try {
                    socket.send(incomingDatagram);
                    Log.info(threadName + " relayed packet with " + MyPacket.getNums(incomingDatagram));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.info(threadName + " dropped packet with " + MyPacket.getNums(incomingDatagram));
            }
        }

    }

    public void start() {
        Log.info("Starting " + threadName);
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }

    static boolean chanceGenerator(int percentage) {
        Random rnd = new Random();
        return rnd.nextInt(100) < percentage;


    }





    }

