package tcp.tickets;

import java.util.ArrayList;
import java.util.List;



public class TicketsDB {
	private TicketsDB(){
		//empty
	}
	private List<Ticket> tickets = new ArrayList<Ticket>();
	private int uniqueOrderID = 0;
	private static final TicketsDB instance = new TicketsDB();
	
	public static TicketsDB getInstance(){
		return instance;
	}

	/**
	 * 
	 * @param t the Ticket to add
	 * @return true if successful, or false if not possible?
	 */
	public boolean addTicket(final Ticket ticket_arg){
		for(Ticket t : tickets){
			if(t.overlaps(ticket_arg))
				return false;
		}
		
		new TicketIdSetter().setId(ticket_arg, getNextUniqueId());
		
		tickets.add(ticket_arg);	
		return true;
	}
	
	private int getNextUniqueId() {
		uniqueOrderID++;
		return uniqueOrderID;

	}

	public List<Ticket> getAllTickets(){
		return new ArrayList<Ticket>(tickets);
	}
}
