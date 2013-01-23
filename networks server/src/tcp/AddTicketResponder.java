package tcp;

import java.io.Serializable;

import tcp.MessageResponder;
import tcp.Messages;
import tcp.AddNewTicketCommand;
import tcp.tickets.Ticket;

public class AddTicketResponder implements MessageResponder {
	private SecureNetworkMessenger snm;
	
	public AddTicketResponder(){
		snm = SecureNetworkMessenger.getSecureInstance();
	}
	@Override
	public String getType() {
		return Messages.NEW_QBOARD_TICKET;
	}

	@Override
	public void handleMessage(Serializable data) {
		Ticket t = (Ticket) data;
		if(t.getUniqueID() == 0){
			//we should change it anywhere here on the server
		}
		AddNewTicketCommand atc = new AddNewTicketCommand(t);
		if( atc.execute()){
			System.out.println("ticket accepted, sending positive acknowledgement");
			if(snm == null){
				System.out.println("wow , snm is null");
				return;
			}
			snm.sendMessage(new NetworkMessage(Messages.SUCCESS, new String("Ticket accepted!")));
		}else{
			System.out.println("ticket rejected, sending negative acknowledgement");
			snm.sendMessage(new NetworkMessage(Messages.FAILURE, new String("Ticket rejected!")));
		}
	}

}
