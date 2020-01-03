|                        |
| ---------------------- |
| COMP7005 Final Project |
| Network Emulator       |

<table>
<tbody>
<tr class="odd">
<td><p>Nishan Vivekanandan</p>
<p>12-1-2015</p></td>
</tr>
</tbody>
</table>

# Objective 

The objective of this project is to design and implement a basic
Send-And-Wait protocol simulator. The protocol will be half-duplex and
use sliding windows to send multiple packets between to hosts on a LAN
with an “unreliable network” between the two hosts.

The following diagram depicts the model:

![](.//media/image1.png)

# Initial Design

## State Machine Diagrams

### Transmitter Component

![](.//media/image2.png)

### Receiver Component

![](.//media/image3.png)

### Network Emulator Component

![](.//media/image4.png)

## Pseudo Code

### Transmitter Component

OPEN file

ENCODE file to BASE64 file\_buffer

PACKETIZE file to packet\_array

FOR EACH byte in file\_buffer

PACK byte

SEQ\_NUM ++

END FOR

ADD EOT packet to packet\_array

TRANSMIT packet\_array

WHILE current\_packet \<= packet\_array(last\_packet) DO

SEND packet(current\_packet)

WAITFORACK

IF ACK received THEN

last\_ACK ++

CONTINUE

ELSE TIMEOUT(CONTINUE)

### Receiver Component

RECEIVE packet\_array

DO

WAIT\_FOR\_PACKET

IF received\_packet IS Valid

STORE\_PACKET in packet\_array

ACKNum = recevied\_packet.SEQNUM

SEND\_ACK(ACKNum)

WHILE received\_packet IS NOT EOT

UNPACKETIZE packet\_array to file\_buffer

SAVE file\_buffer to file

### Network Emulator Component

WHILE RUNNING

WAIT\_FOR\_PACKET

IF PACKET\_SHOULD\_BE\_DROPPED

CONTINUE

FORWARD\_PACKET

# Implementation

The project was implemented in Java. Each component was represented by
its own class as well as the the class MyPacket such that each custom
packet was created as MyPacket object. A Configuration helper class was
also created to facilitate the loading of configuration parameters from
a config.properties file as well as hold several static utility methods.

![](.//media/image5.png)

## MyPacket Class

The MyPacket Class forms the basis of the custom packet. It has the
following fields.

public int PacketType;  
public int SeqNum;  
public int WindowSize;  
public int AckNum;  
public byte payload;

The current packet design carries a fixed payload size of 1 byte. The
MyPacket class also holds several utility methods for working with
packets including encapsulating and encapsulating individual packets as
well as converting byte arrays into arrays of packets ready to send.

## Transmitter Class

The Transmitter Class is responsible for getting the payload. Converting
to a byte array and then calls functions in the MyPacket class to
encapsulate the byte array in MyPackets. It then encapsulates packets
into UDP datagrams to send to the Receiver. The Transmitter class
contains the logic for the reliable sending of packets based on ACKS
coming back from the receiver and timeouts.

## 

## Receiver Class

The Receiver class is responsible for getting packets from the
Transmitter and extracting and reforming the original payload. It
contains the logic to store the correct packets in the correct order by
detecting duplicates based on sequence number and sending ACKS. It calls
functions from the MyPacket class to then de-encapsulate MyPackets from
the UDP packets as well as convert the resulting array of MyPackets back
to a byte array from which the payload can be saved.

## NetworkSim Class

The NetworkSim class is responsible for relaying packets between the
transmitter and receiver. It can be configured to drop a certain
percentage of packets to simulate an unreliable network. It creates two
threads one to relay packets in each direction.

## Config Class

The Config class is a helper class responsible for parsing configuration
parameters stored in config.properties that can then be read by the
other classes. It also contains shared static methods dealing with
logging and other misc utilities.

## Config.properties

This text files holds all the programs configuration parameters.

\#NetworkemulatorProperties  
receiverPort= port \# the receiver is listening on.  
receiverIP= IP address of the recevier  
transmitterPort= port \# the transmitter is listening on.  
transmitterIP= IP address of the transmitter  
NetworkSimIP= IP address of the NetworkSimulator  
NetworkListenForReceiverPort= port \# the networksim listens for
receiver packets.  
NetworkListenForTransmitterPort= port \# the networksim listens for
transmitter packets.  
ACKtimeout= Timeout in miliseconds that the transmitter waits before
resending  
PercentageOfDroppedPackets= Percentage of packets the networksim should
drop.  
stringPayload= A string to be used as the payload for the purposes of
demonstrating the program.

## Log Files

Each component generates a log file showing the current packet it is
working on and actions taken. Examples follow:

### Transmitter Log

Dec 01, 2015 6:10:40 PM tech.vivek.networkemulator.Transmitter main  
INFO: Transmitter Started  
Dec 01, 2015 6:10:40 PM tech.vivek.networkemulator.Transmitter
sendMyPacket  
INFO: Sent Packet with Type: DATA SeqNo: 0 AckNo: 0 to localhost:9868  
Dec 01, 2015 6:10:40 PM tech.vivek.networkemulator.Transmitter
transmit  
INFO: Timed out waiting for ACK: 0 resending packet  
Dec 01, 2015 6:10:40 PM tech.vivek.networkemulator.Transmitter
sendMyPacket  
INFO: Sent Packet with Type: DATA SeqNo: 0 AckNo: 0 to localhost:9868  
Dec 01, 2015 6:10:40 PM tech.vivek.networkemulator.Transmitter
transmit  
INFO: Timed out waiting for ACK: 0 resending packet  
Dec 01, 2015 6:10:40 PM tech.vivek.networkemulator.Transmitter
sendMyPacket  
INFO: Sent Packet with Type: DATA SeqNo: 0 AckNo: 0 to localhost:9868  
Dec 01, 2015 6:10:40 PM tech.vivek.networkemulator.Transmitter
transmit  
INFO: Timed out waiting for ACK: 0 resending packet  
Dec 01, 2015 6:10:40 PM tech.vivek.networkemulator.Transmitter
sendMyPacket  
INFO: Sent Packet with Type: DATA SeqNo: 0 AckNo: 0 to localhost:9868  
Dec 01, 2015 6:10:40 PM tech.vivek.networkemulator.Transmitter
transmit  
INFO: Received Packet with Type: ACK SeqNo: 0 AckNo: 0  
Dec 01, 2015 6:10:40 PM tech.vivek.networkemulator.Transmitter
transmit  
INFO: ACK is correct

### Receiver Log

Dec 01, 2015 6:10:36 PM tech.vivek.networkemulator.Receiver main  
INFO: Receiver Started  
Dec 01, 2015 6:10:40 PM tech.vivek.networkemulator.Receiver
receievePacketArray  
INFO: Received packet with Type: DATA SeqNo: 0 AckNo: 0  
Dec 01, 2015 6:10:40 PM tech.vivek.networkemulator.Receiver sendACK  
INFO: Sent Packet with Type: ACK SeqNo: 0 AckNo: 0 to localhost:9869  
Dec 01, 2015 6:10:40 PM tech.vivek.networkemulator.Receiver
receievePacketArray  
INFO: Received packet with Type: DATA SeqNo: 0 AckNo: 0  
Dec 01, 2015 6:10:40 PM tech.vivek.networkemulator.Receiver
receievePacketArray  
INFO: Incorrect SeqNo re-requesting correct packet  
Dec 01, 2015 6:10:40 PM tech.vivek.networkemulator.Receiver sendACK  
INFO: Sent Packet with Type: ACK SeqNo: 0 AckNo: 0 to localhost:9869  
Dec 01, 2015 6:10:40 PM tech.vivek.networkemulator.Receiver
receievePacketArray  
INFO: Received packet with Type: DATA SeqNo: 1 AckNo: 0  
Dec 01, 2015 6:10:40 PM tech.vivek.networkemulator.Receiver sendACK  
INFO: Sent Packet with Type: ACK SeqNo: 0 AckNo: 1 to localhost:9869  
Dec 01, 2015 6:10:40 PM tech.vivek.networkemulator.Receiver
receievePacketArray  
INFO: Received packet with Type: DATA SeqNo: 2 AckNo: 0  
Dec 01, 2015 6:10:40 PM tech.vivek.networkemulator.Receiver sendACK  
INFO: Sent Packet with Type: ACK SeqNo: 0 AckNo: 2 to localhost:9869  
Dec 01, 2015 6:10:40 PM tech.vivek.networkemulator.Receiver
receievePacketArray  
INFO: Received packet with Type: DATA SeqNo: 3 AckNo: 0  
Dec 01, 2015 6:10:40 PM tech.vivek.networkemulator.Receiver sendACK  
INFO: Sent Packet with Type: ACK SeqNo: 0 AckNo: 3 to localhost:9869  
Dec 01, 2015 6:10:40 PM tech.vivek.networkemulator.Receiver
receievePacketArray  
INFO: Received packet with Type: DATA SeqNo: 3 AckNo: 0  
Dec 01, 2015 6:10:40 PM tech.vivek.networkemulator.Receiver
receievePacketArray  
INFO: Incorrect SeqNo re-requesting correct packet  
Dec 01, 2015 6:10:40 PM tech.vivek.networkemulator.Receiver sendACK  
INFO: Sent Packet with Type: ACK SeqNo: 0 AckNo: 3 to localhost:9869

### Network Simulator Log

Dec 01, 2015 6:10:31 PM tech.vivek.networkemulator.NetworkSim main  
INFO: Network Simulator started  
Dec 01, 2015 6:10:31 PM tech.vivek.networkemulator.NetworkSimRunnable
\<init\>  
INFO: Creating Transmitter-Relay  
Dec 01, 2015 6:10:31 PM tech.vivek.networkemulator.NetworkSimRunnable
start  
INFO: Starting Transmitter-Relay  
Dec 01, 2015 6:10:31 PM tech.vivek.networkemulator.NetworkSimRunnable
\<init\>  
INFO: Creating Receiver-Relay  
Dec 01, 2015 6:10:31 PM tech.vivek.networkemulator.NetworkSimRunnable
start  
INFO: Starting Receiver-Relay  
Dec 01, 2015 6:10:40 PM tech.vivek.networkemulator.NetworkSimRunnable
run  
INFO: Transmitter-Relay dropped packet with Type: DATA SeqNo: 0 AckNo:
0  
Dec 01, 2015 6:10:40 PM tech.vivek.networkemulator.NetworkSimRunnable
run  
INFO: Transmitter-Relay dropped packet with Type: DATA SeqNo: 0 AckNo:
0  
Dec 01, 2015 6:10:40 PM tech.vivek.networkemulator.NetworkSimRunnable
run  
INFO: Transmitter-Relay relayed packet with Type: DATA SeqNo: 0 AckNo:
0  
Dec 01, 2015 6:10:40 PM tech.vivek.networkemulator.NetworkSimRunnable
run  
INFO: Receiver-Relay dropped packet with Type: ACK SeqNo: 0 AckNo: 0  
Dec 01, 2015 6:10:40 PM tech.vivek.networkemulator.NetworkSimRunnable
run  
INFO: Transmitter-Relay relayed packet with Type: DATA SeqNo: 0 AckNo:
0  
Dec 01, 2015 6:10:40 PM tech.vivek.networkemulator.NetworkSimRunnable
run  
INFO: Receiver-Relay relayed packet with Type: ACK SeqNo: 0 AckNo: 0  
Dec 01, 2015 6:10:40 PM tech.vivek.networkemulator.NetworkSimRunnable
run  
INFO: Transmitter-Relay relayed packet with Type: DATA SeqNo: 1 AckNo:
0  
Dec 01, 2015 6:10:40 PM tech.vivek.networkemulator.NetworkSimRunnable
run  
INFO: Receiver-Relay relayed packet with Type: ACK SeqNo: 0 AckNo: 1

## Execution Instructions

Extract the submitted zip file on the respective host machines and
navigate to the “App” folder. Here you will find the “config.properties”
file. Edit the file with your desired parameters and save. Navigate to
the “App” folder on the command line. Launch the modules with the
commands given below. Start the Network Sim module first, Receiver
second and the Transmitter last.

“java -cp . tech.vivek.networkemulator.NetworkSim”

“java -cp . tech.vivek.networkemulator.Receiver”

“java -cp . tech.vivek.networkemulator.Transmitter”

You may need to add Java.exe to your environment path if not already
done so.

This has been tested in a Windows environment however it should work in
linux and OSX as well.

The modules will out log entries to the console. Log files will also be
saved in the “App” folder.
