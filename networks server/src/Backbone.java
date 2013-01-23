

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import tcp.SecureNetworkMessenger;
import tcp.TasksManager;
import udp.ConnectionStatusThread;

public class Backbone implements Runnable {
	/**
	 * Launch the application.
	 */
	public static ConnectionStatusThread statusThread = null;
	
	public static void main(String[] args) {
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(4);
		        
		//we want dependency injection here?
		executor.execute((Runnable) SecureNetworkMessenger.getSecureInstance());
		System.out.println("netowork messenger running");
				
		TasksManager tm = TasksManager.getInstance();
		
		try{
			executor.scheduleWithFixedDelay(tm, 0, 200, TimeUnit.MILLISECONDS );
			System.out.println("tasks manager running");	
		} catch (Exception e) {
			System.out.println("TASKS MANAGER CRASHED!");
		}
		try {
			statusThread = new ConnectionStatusThread("SERVER");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//ConnectionStatusThread.outputEnabled = true;

		executor.scheduleAtFixedRate(statusThread, 0, 20, TimeUnit.MILLISECONDS );
		System.out.println("connection status thread running");	
		
		executor.execute(new Backbone());
	}
	
	public void run(){
        Scanner sc = new Scanner(System.in);

		while(true){
	        ConnectionStatusThread.outputEnabled = (sc.nextInt() > 0);
		}
	}
}

