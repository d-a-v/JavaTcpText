
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class NeTextClient extends Thread
{
	private String serverName;
	private int serverPort;
	private Socket sock;
	private DataOutputStream out;
	NeTextReceiverIntf receiver;
	
	public NeTextClient (String remoteName, int remotePort, NeTextReceiverIntf receiveFromRemote)
	{
		serverName = remoteName;
		serverPort = remotePort;
		receiver = receiveFromRemote;
	}

	public boolean connect ()
	{
		try
		{
			sock = new Socket(serverName, serverPort);
			new NeTextReceiverThread("outgoing connection to " + serverName + ":" + serverPort, sock, receiver);
		} catch (IOException e)
		{
			// no remote server listening
			return false;
		}
		
		try
		{
			out = new DataOutputStream(sock.getOutputStream());
		} catch (IOException e)
		{
			close();
			e.printStackTrace(); // this should not happen
			return false;
		}
		
		// start receiver thread
		
		return true;
	}
	
	public boolean send (String string)
	{
		if (out == null)
		{
			System.err.println("Connection was broken (NeTextClient.send() has returned false)");
			return false;
		}
		try
		{
			out.writeBytes(string + '\n');
		} catch (IOException e)
		{
			out = null;
			sock = null;
			System.err.println("connection to " + serverName + ":" + serverPort + " broken !");
			return false;
		}		
		return true;
	}

	public void close ()
	{
		try
		{
			if (sock != null)
				sock.close();
			out = null;
			sock = null;
		} catch (IOException e)
		{
			System.err.println("While closing with " + serverName + ":" + serverPort);
			e.printStackTrace(); // this should not happen
		}
	}
}
