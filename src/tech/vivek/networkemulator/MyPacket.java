/**
 *
 */

package tech.vivek.networkemulator;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.util.ArrayList;

/**
 * Created by Nishan on 11/28/2015.
 */
public class MyPacket implements Serializable {

    // 0 = ACK. 1 = DATA. 2 = EOT
    public int PacketType;
    public int SeqNum;
    public int WindowSize;
    public int AckNum;
    public byte payload;


    public MyPacket() {

    }

    public MyPacket(int PacketType, int SeqNum, byte payload) {
        this.PacketType = PacketType;
        this.SeqNum = SeqNum;
        this.payload = payload;

    }


    public MyPacket(int PacketType, int SeqNum) {
        this.PacketType = PacketType;
        this.SeqNum = SeqNum;
    }


    public static ArrayList<MyPacket> packetize(byte[] bytePayloadArray) {
        ArrayList<MyPacket> packetArray = new ArrayList<MyPacket>();
        int nextSeqNumber = 0;

        for (byte bytePayload : bytePayloadArray) {
            packetArray.add(new MyPacket(1, nextSeqNumber, bytePayload));
            nextSeqNumber++;
        }

        //Add EOT packet to end of array, probably not the best place to do it.
        packetArray.add(new MyPacket(2, nextSeqNumber));

        return packetArray;

    }


    public static byte[] unpacketize(ArrayList<MyPacket> packetArray) {
        byte[] bytePayload = new byte[packetArray.size() - 1];

        for (int i = 0; i < packetArray.size() - 1; i++) {
            bytePayload[i] = packetArray.get(i).payload;
        }

        return bytePayload;

    }


    public static MyPacket datagramToMyPacket(DatagramPacket datagramPacket) {
        MyPacket myPacket = null;
        try {
            byte[] data = datagramPacket.getData();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            ObjectInputStream is = new ObjectInputStream(in);
            myPacket = (MyPacket) is.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return myPacket;

    }

    public static String getNums(DatagramPacket datagram) {
        MyPacket myPacket = datagramToMyPacket(datagram);
        return getNums(myPacket);
    }

    public static String getNums(MyPacket myPacket) {
        String type = "";
        switch (myPacket.PacketType) {
            case 0:
                type = "ACK";
                break;
            case 1:
                type = "DATA";
                break;
            case 2:
                type = "EOT";
                break;
        }

        return ("Type: " + type + " SeqNo: " + myPacket.SeqNum + " AckNo: " + myPacket.AckNum);
    }
}



