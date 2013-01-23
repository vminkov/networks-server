package tcp;

import tcp.Command;
import tcp.tickets.Ticket;
import tcp.tickets.TicketsDB;


public class AddNewTicketCommand implements Command {
	private Ticket tick;
	private TicketsDB tdb = TicketsDB.getInstance();
	
	public AddNewTicketCommand(Ticket t){
		tick = t;
	}
	@Override
	public boolean execute() {
		return tdb.addTicket(tick);
	}
	
}
