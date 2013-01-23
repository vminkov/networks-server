package tcp;

import java.util.List;

import tcp.tickets.Ticket;
import tcp.tickets.TicketsDB;

public class UpdateClientTicketsCommand implements Command {
	private List<Ticket> tickets;
	private SecureNetworkMessenger snm;
	private TicketsDB tdb;
	
	public UpdateClientTicketsCommand(List<Ticket> tickets_arg){
		snm = SecureNetworkMessenger.getSecureInstance();
		tickets = tickets_arg;
	}
	
	public UpdateClientTicketsCommand(){
		snm = SecureNetworkMessenger.getSecureInstance();
		tdb = TicketsDB.getInstance();
		tickets = tdb.getAllTickets();
	}
	
	@Override
	public boolean execute() {
		return snm.sendMessage(new NetworkMessage(Messages.UPDATE_TICKETS_LIST, tickets));
	}

}
