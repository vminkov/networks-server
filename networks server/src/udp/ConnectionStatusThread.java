package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;
import java.util.logging.Logger;

import tcp.SecureNetworkMessenger;


public class ConnectionStatusThread extends Thread {
	public static boolean outputEnabled = false;
	private static int sentDatagrams = 0;
	private static int recievedDatagrams = 0;
	private DatagramSocket socket = new DatagramSocket(1616);
    
	public double getResponsePercentage(){
		if(sentDatagrams == 0)
			return 0;
		return (double) recievedDatagrams / (double) sentDatagrams;
	}
	
    public ConnectionStatusThread(String name) throws IOException {
        super(name);
    }

    public void run() {
        try {
        	if(SecureNetworkMessenger.clientAddress != null){
	            // send request
	        	
		        byte[] buffer = new byte[512];
		        InetAddress address = SecureNetworkMessenger.clientAddress;
		        //InetAddress address = InetAddress.getByName("95.111.25.233");
		        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 1516);
		        socket.send(packet);
	        	//System.out.println("server waiting for responses");
		        sentDatagrams++;
		        // get response
		        packet = new DatagramPacket(buffer, buffer.length);
	
		        socket.setSoTimeout(500);
		        socket.receive(packet);
		        recievedDatagrams++;
		        
			    // display response
		        String received = new String(packet.getData(), 0, packet.getLength());
		       
		        if(outputEnabled){
		        	 System.out.println("Client time: " + received);
				     System.out.println("Connection status: " + ((float) recievedDatagrams / (float) sentDatagrams)*100
				        		+ "% packages returned" );
		        }
		        
			    if(sentDatagrams > 50) {sentDatagrams = recievedDatagrams = 0;}
        	}
        } catch (Exception e) {
			Logger.getLogger("There are problems with the connection (status thread)");
		}
  
        //socket.close();
    } 
} 
