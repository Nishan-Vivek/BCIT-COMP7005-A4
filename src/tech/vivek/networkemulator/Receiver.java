package tech.vivek.networkemulator;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Created by Nishan on 11/28/2015.
 */
public class Receiver {

    public static boolean gotconfig = Config.setConfigParameters();


    private static final int ReceiverPort = Config.receiverPort;
    private static final String TransmitterIP = Config.NetworkSimIP;
    private static final int TransmitterPort = Config.NetworkListenForReceiverPort;

    private static final Logger Log = Config.createlogger("Receiver");

    public static void main(String[] args) {
        Log.info("Receiver Started");

        byte[] bytePayload = MyPacket.unpacketize(receievePacketArray());
        String receviedString = new String(bytePayload);

        Log.info("Received all data from transmitter:");
        Log.info(receviedString);


    }


    private static ArrayList<MyPacket> receievePacketArray() {
        ArrayList<MyPacket> packetArray = new ArrayList<MyPacket>();
        DatagramSocket socket = null;
        DatagramPacket incomingDatagram = null;
        MyPacket incomingPacket;
        int previousSeqNo = -1;
        byte[] incomingData = new byte[1024];
        byte[] data;
        try {
            socket = new DatagramSocket(ReceiverPort);
            incomingDatagram = new DatagramPacket(incomingData, incomingData.length);
        } catch (Exception e) {
            e.printStackTrace();
        }


        do {
            try {
                socket.receive(incomingDatagram);
            } catch (IOException e) {
                e.printStackTrace();
            }
            incomingPacket = MyPacket.datagramToMyPacket(incomingDatagram);
            Log.info("Received packet with "+MyPacket.getNums(incomingPacket));

            //do not add duplicate SeqNum
            if(incomingPacket.SeqNum != previousSeqNo) {
                packetArray.add(incomingPacket);
                previousSeqNo = incomingPacket.SeqNum;
            }else{
                Log.info("Incorrect SeqNo re-requesting correct packet");
            }
            sendACK(incomingPacket.SeqNum,socket);

        } while (incomingPacket.PacketType != 2); //while last added packet was not EOT
        Log.info("Received packet with " + MyPacket.getNums(incomingPacket));
        return packetArray;
    }

    private static void sendACK(int AckNum, DatagramSocket socket) {
        MyPacket ACKPacket = new MyPacket();
        ACKPacket.PacketType = 0;
        ACKPacket.AckNum = AckNum;

        try {
            InetAddress IPAddress = InetAddress.getByName(TransmitterIP);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(outputStream);
            os.writeObject(ACKPacket);
            byte[] data = outputStream.toByteArray();
            DatagramPacket sendDatagram = new DatagramPacket(data, data.length, IPAddress, TransmitterPort);
            socket.send(sendDatagram);
            Log.info("Sent Packet with " + MyPacket.getNums(ACKPacket) + " to " +TransmitterIP+":"+TransmitterPort);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}