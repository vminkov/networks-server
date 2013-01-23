package tcp;

import java.io.Serializable;
import java.util.List;

import tcp.tickets.Ticket;
import tcp.tickets.TicketsDB;

public class UpdateRequestResponder implements MessageResponder {
	private List<Ticket> tickets;
	private TicketsDB tdb;
	
	public UpdateRequestResponder(){
		tdb = TicketsDB.getInstance();
	}
	
	@Override
	public String getType() {
		return Messages.UPDATE_TICKETS_LIST;
	}

	@Override
	public void handleMessage(Serializable data) {
		if(data == null){
			tickets = tdb.getAllTickets();
			System.out.println();
		}else{
			//data is a query
		}
		
		UpdateClientTicketsCommand updateCommand = new UpdateClientTicketsCommand(tickets);
		updateCommand.execute();
	}

}
