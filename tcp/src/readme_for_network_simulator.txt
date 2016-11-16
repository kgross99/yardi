--------------------------------------------------------------
Notes for CS425Network class:
--------------------------------------------------------------

--------------------------------------------------------------
Files:
CS425Network.java
CS425NetworkInterface.java (provided by instructor, do not modify)
DatagramReceiver.java
DatagramReceiverThread.java
TestWithNetwork.java (simple demo of network class)
tc001recv.java (simple demo of using network to receive packets)
tc001send.java (simple demo of using network to send packets)
tc001/ (regression test folder with files)
--------------------------------------------------------------

--------------------------------------------------------------
Using the simple demo of network)
Usage:
compile with:
javac TestWithNetwork.java

Usage: java TestWithNetwork
    <local receiver port number>
    <remote receiver host name>
    <remote receiver port number>
    <buffer size>
    <"s" to be the sender or "r" to be the receiver>
--------------------------------------------------------------
Using the regression tester:
See the readme_for_regression_test_harness.txt
--------------------------------------------------------------

--------------------------------------------------------------
NOTES:
--------------------------------------------------------------
A sending program wishing to close a receiving socket
may do so by sending a it packet with the message "quit".
This applies to closing both a remote receiving socket and the
local one.

If a "quit" message isn't used to close a receiving socket, then
when the test harness is done sending or receiving, it will be
necessary for the user to manually close the running program by
pressing "ctrl+c". This is because the receiver blocks waiting
for a packet. At the end of the program, if no "quit" message has
been received, the receiving socket will still be waiting.

Note also that even though a program may have only sent packets,
it is still running a receiver thread. That's part of the network
layer. The network is always ready to receive.
--------------------------------------------------------------

--------------------------------------------------------------
Example:
In one terminal window or one machine, run with:
java TestWithNetwork 65002 127.0.0.1 64002 100 s
    
In a different terminal window or machine, run with:
java TestWithNetwork 64002 127.0.0.1 65002 100 r

Sender sends 10 packets to receiver, displaying "packet sent" for
each one. Sender sends a "quit" packet to both its own
receiver and to the remote receiver.

Receiver displays packet data (a text message) and
"packet received" for each packet received.

Sample output from sender:
$ java TestWithNetwork 65002 127.0.0.1 64002 100 s
packet sent
packet sent
packet sent
packet sent
packet sent
packet sent
packet sent
packet sent
packet sent
packet sent
packet sent
packet sent
$ 
 
Sample output from receiver:
$ java TestWithNetwork 64002 127.0.0.1 65002 100 r
packet received: Number:  0
packet received: Number:  1
packet received: Number:  2
packet received: Number:  3
packet received: Number:  4
packet received: Number:  5
packet received: Number:  6
packet received: Number:  7
packet received: Number:  8
packet received: Number:  9
packet received: quit
$ 
--------------------------------------------------------------
--------------------------------------------------------------

