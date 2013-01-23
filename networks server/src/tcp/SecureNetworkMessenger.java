package tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import tcp.MessageResponder;
import tcp.Messenger;
import tcp.NetworkMessage;
import tcp.AddTicketResponder;

/**
 * Should we make it singleton?
 * @author Vicho
 *
 */
public class SecureNetworkMessenger implements Runnable, Messenger {
	private static Map<String, MessageResponder> responders = new HashMap<String, MessageResponder>();
	private static ObjectOutputStream outgoingSerial;
	private static ObjectInputStream incomingSerial;
	private static InetAddress thisHost;
	private static final int PORT = 2343;
	private static MessageQueue incomingMessagesQueue = MessageQueue.getInstance();
	private static SecureNetworkMessenger instance;// = new SecureNetworkMessenger();
	
	public static InetAddress clientAddress = null;
	
	public static SecureNetworkMessenger getSecureInstance(){
		if(instance == null)
			instance = new SecureNetworkMessenger();
		return SecureNetworkMessenger.instance;
	}
	private SecureNetworkMessenger(){
        /*
         * 	DON'T CREATE HERE REFERENCES TO THE SECURENETWORKMESSENGER!!!
         * 	it causes selfinstation infinie loop when the snm is requested
         * 	in the reference constructor
         */
	}
	
	private void registerResponder(MessageResponder responder) {
		responders.put(responder.getType(), responder);
	}
	public static MessageResponder getResponder(String msgType){
		return responders.get(msgType);
	}
	@Override 
	public void run(){	
		registerResponder(new AddTicketResponder());
		registerResponder(new UpdateRequestResponder());
		registerResponder(new SuccessResponseHandler()); 
		registerResponder(new FailureResponseHandler());
		
		try {
			thisHost = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		SSLServerSocketFactory sslserversocketfactory =
                    (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        SSLServerSocket sslserversocket;
		try {
			System.out.println("SERVER starting at port " + PORT);
			sslserversocket = (SSLServerSocket) sslserversocketfactory.createServerSocket();
			//certificate is made with: "keytool -genkey keystore -keyalg RSA"
			//this below is a 'hack' (the hack was adding all cipher suites, this is the only one we actually need)
			sslserversocket.setEnabledCipherSuites(new String[]{"SSL_DH_anon_WITH_3DES_EDE_CBC_SHA"});
			
			sslserversocket.bind(new InetSocketAddress(thisHost, PORT));
			
			SSLSocket sslsocket = (SSLSocket) sslserversocket.accept();
			
			clientAddress = sslsocket.getInetAddress();
            
			InputStream inputstream = sslsocket.getInputStream();
            OutputStream outputstream  = sslsocket.getOutputStream();
    	    
			outgoingSerial = new ObjectOutputStream(outputstream);
    		incomingSerial = new ObjectInputStream(inputstream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("WAITING FOR MESSAGES");
		//testSendNewTicketType();
		
//		executor.scheduleAtFixedRate(new Runnable() {
//			
//			@Override
//			public void run() {
		while(true){
				try {
					waitForMessages();
				} catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
					break;
				}
		}
//		}, 0, 50, TimeUnit.MILLISECONDS);		
	}
//	private static void testSendNewTicketType() {
//		TicketType tt = new TicketType("the reason", 5, 3);
//		NetworkMessage tobesent = new NetworkMessage(Messages.ADD_TICKET_TYPE, tt);
//		try {
//			outgoingSerial.writeObject(tobesent);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	private void waitForMessages() throws ClassNotFoundException, IOException{
		Object message = null;

//		try {
			if ((message = incomingSerial.readObject()) != null) {
				NetworkMessage incomingMessage = (NetworkMessage) message;

				if(incomingMessage == null || //incomingMessage.getData() == null ||
						incomingMessage.getType() == null || incomingMessage.getType() == ""){
						System.out.println(" corrupt message - type is null or empty string");
					return;
				}
				try {
					incomingMessagesQueue.put( incomingMessage );
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
//		} catch (Exception e) {
//			e.printStackTrace();
//        }
	}
	
	public boolean sendMessage(NetworkMessage nm){
		try {
			outgoingSerial.writeObject(nm);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean sendMessage(String msgType, Serializable data){
		NetworkMessage message = new NetworkMessage(msgType,data);
		return this.sendMessage(message);
	}
	@Override
	public void sendGreetings() {
		
	}
}