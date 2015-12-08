package tech.vivek.networkemulator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Logger;


/**
 * Created by Nishan.
 */
public class Transmitter {

    public static boolean gotconfig = Config.setConfigParameters();

    private static final int receiverPort = Config.NetworkListenForTransmitterPort;
    private static final String receiverIP = Config.NetworkSimIP;
    private static final int transmitterPort = Config.transmitterPort;
    private static final int ACKtimeout = Config.ACKtimeout;
    public static int retryLimit = Config.RetryLimit;
    private static final String stringPayload = Config.stringPayload;

    private static final Logger Log = Config.createlogger("Tranmistter");


    public static void main(String[] args) {

        Log.info("Transmitter Started");
        MyPacket temp = new MyPacket();

        byte[] bytePayload = stringPayload.getBytes();
        ArrayList<MyPacket> packetArray = MyPacket.packetize(bytePayload);
        transmit(packetArray);

    }

    private static void transmit(ArrayList<MyPacket> packetArray) {
        //Setup socket and streams.
        DatagramSocket socketOutGoing = null;
        DatagramSocket socketIncoming = null;
        DatagramPacket incomingDatagram = null;
        MyPacket incomingMyPacket;
        MyPacket currentPacket;
        try {
            socketOutGoing = new DatagramSocket();
            socketIncoming = new DatagramSocket(transmitterPort);
            socketIncoming.setSoTimeout(ACKtimeout);
            byte[] incomingData = new byte[1024];
            incomingDatagram = new DatagramPacket(incomingData, incomingData.length);
        } catch (Exception e) {
            e.printStackTrace();
        }


        int currentPacketArrayIndex = 0;
        while (currentPacketArrayIndex < packetArray.size()) {

            currentPacket = packetArray.get(currentPacketArrayIndex);
            sendMyPacket(socketOutGoing, currentPacket);


            try {
                socketIncoming.receive(incomingDatagram);
            } catch (SocketTimeoutException e) {
                Log.info("Timed out waiting for ACK: "+currentPacket.SeqNum +" resending packet");
                continue;
            } catch (IOException e) {
                e.printStackTrace();
            }

            incomingMyPacket = MyPacket.datagramToMyPacket(incomingDatagram);
            Log.info("Received Packet with " + MyPacket.getNums(incomingMyPacket));
            if ((incomingMyPacket.PacketType == 0) && (incomingMyPacket.AckNum == currentPacket.SeqNum)) {
                Log.info("ACK is correct");
                currentPacketArrayIndex++;
            } else {
                Log.info("Unexpected ACK received resend last unACK'd packet");
            }
        }
        Log.info("Final Packet sent. Terminating Program");
    }

    static void sendMyPacket(DatagramSocket socket, MyPacket myPacket) {
        try {
            InetAddress IPAddress = InetAddress.getByName(receiverIP);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(outputStream);
            os.writeObject(myPacket);
            byte[] data = outputStream.toByteArray();
            DatagramPacket datagram = new DatagramPacket(data, data.length, IPAddress, receiverPort);
            socket.send(datagram);
            Log.info("Sent Packet with " + MyPacket.getNums(myPacket) +" to " +receiverIP+":"+receiverPort);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    static MyPacket waitForACK(DatagramSocket socketIncoming, int timeout) {
        MyPacket incomingACK;
        byte[] incomingData = new byte[1024];
        byte[] data;

        try {
            socketIncoming.setSoTimeout(timeout);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        DatagramPacket incomingDatagram = new DatagramPacket(incomingData, incomingData.length);
        incomingACK = MyPacket.datagramToMyPacket(incomingDatagram);


        return incomingACK;
    }


}



