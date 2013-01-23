

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import tcp.SecureNetworkMessenger;
import tcp.TasksManager;
import udp.ConnectionStatusThread;

public class Backbone {
	/**
	 * Launch the application.
	 */
	public static ConnectionStatusThread statusThread = null;
	
	public static void main(String[] args) {
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
		        
		//we want dependency injection here?
		executor.execute((Runnable) SecureNetworkMessenger.getSecureInstance());
		System.out.println("netowork messenger running");
				
		TasksManager tm = TasksManager.getInstance();
		
		try{
			executor.scheduleWithFixedDelay(tm, 0, 200, TimeUnit.MILLISECONDS );
			System.out.println("tasks manager running");	
		} catch (Exception e) {
			System.out.println("TAKS MANAGER CRASHED!");
		}
		try {
			statusThread = new ConnectionStatusThread("QOperator");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		executor.scheduleAtFixedRate(statusThread, 0, 20, TimeUnit.MILLISECONDS );
//		System.out.println("connection status thread running");	
	}
}
