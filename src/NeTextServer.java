import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;


public class NeTextServer extends Thread
{
	private ServerSocket welcomer;
	private NeTextReceiverIntf receiver;
	private ArrayList<DataOutputStream> outputs = new ArrayList<DataOutputStream>();
	Boolean closing = false;

	private synchronized void addNewOutgoingChannel (DataOutputStream dataOutputStream)
	{
		outputs.add(dataOutputStream);
	}
	
	public synchronized void sendToAllPeer (String text)
	{
		for (int i = 0; i < outputs.size(); i++)
		{
			DataOutputStream out = outputs.get(i);
			if (out != null)
				sendToPeer(out, i, text);
		}
	}
	
	public synchronized boolean sendToPeer (int index, String text)
	{
		DataOutputStream out = outputs.get(index);
		return 	sendToPeer(out, index, text);
	}

	public synchronized boolean sendToPeer (DataOutputStream out, int index, String text)
	{
		if (out == null)
		{
			System.err.println("output: index " + index + ": connexion is broken, don't use it");
			return false;
		}
		else
			try
			{
				out.writeBytes(text + '\n');
			} catch (SocketException e) // improved error type detection
			{
				System.err.println("output: sending to (index " + index + "): socket closed");
				outputs.set(index, null);				
			} catch (IOException e)
			{
				System.err.println("output: sending to (index " + index + "): connection broken");
				e.printStackTrace();
				outputs.set(index, null);	
				if (outputs.get(index) != null) System.out.println("blob");
			}
		return true;
	}

	public NeTextServer (int localPort, NeTextReceiverIntf globalReceiver) 
	{
		receiver = globalReceiver;
		try
		{
			// create server socket for dealing with new incoming connections
			welcomer = new ServerSocket(localPort);
		} catch (IOException e)
		{
			System.err.println("Cannot listen on port " + localPort);
			e.printStackTrace();
		}
		// start server thread
		start();
	}

	public void run ()
	{ 
		try
		{
			while (true)
			{
				// wait for new connection
				Socket sock = welcomer.accept();
				// get connection ID (XXX improveme)
				String remoteId = sock.getRemoteSocketAddress().toString(); // this is not good enough
				// start receiver thread for this new connection
				new NeTextReceiverThread("incoming connection from " + remoteId, sock, receiver);
				
				// store output stream in a list for send* functions
				addNewOutgoingChannel(new DataOutputStream(sock.getOutputStream()));
			}
		} catch (IOException e)
		{
			System.err.println("Server: stopped listening");
			if (!closing)
				e.printStackTrace();
		}
	}
	

	public void close ()
	{
		System.out.println("Closing server on port " + welcomer.getLocalPort());
		try
		{
			closing = true;
			welcomer.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
