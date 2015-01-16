
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;


public class NeTextReceiverThread extends Thread
{
	private String remoteId;
	private BufferedReader in;
	private NeTextReceiverIntf receiver;

	public NeTextReceiverThread (String remoteName, Socket remote, NeTextReceiverIntf receiveFromRemote) throws IOException
	{
		remoteId = remoteName;
		receiver = receiveFromRemote;
		in = new BufferedReader(new InputStreamReader(remote.getInputStream()));

		start();
		System.out.println("start receiver for remote " + remoteId);
	}

	public void run ()
	{
		String receive;
		try
		{
			while (in != null && (receive = in.readLine()) != null)
				receiver.receive(remoteId, receive);
		} catch (SocketException e) //XXX improve close detection
		{
			System.err.println("Connection closed from " + remoteId);
		} catch (IOException e)
		{
			System.err.println("Connection broken with " + remoteId);
			e.printStackTrace();
		}
		in = null;
	}
}
