

class ExampleClientServer implements NeTextReceiverIntf
{
	// receive() must be implemented when implementing interface NeTextReceiverIntf
	// all received strings will come here
	public void receive (String from, String line)
	{
		System.out.println("received from '" + from + "': " + line);
	}

	public static void main (String args [])
	{
		try
		{
			if (args.length != 3)
			{
				System.err.println("need 3 args: localServerPort remoteServerName/IP remoteServerPort");
				return;
			}

			// parse command line
			int localPort = new Integer(args[0]).intValue();
			String remoteName = args[1];
			int remotePort = new Integer(args[2]).intValue();

			// instantiate interface NeTextReceiverIntf for receiver
			ExampleClientServer receiver = new ExampleClientServer();

			// start local server in a thread
			NeTextServer local = new NeTextServer(localPort, receiver);

			// try to connect to the remote client
			NeTextClient remote = new NeTextClient(remoteName, remotePort, receiver);
			while (true)
			{
				System.out.println("Trying to connect to " + remoteName + ":" + remotePort);
				if (remote.connect())
					break;
				Thread.sleep(1000);
			}

			// in this example, TWO=2 TCP connection are established between here and remote
			// because we are server, and the remote connects to us
			// because we are client, and we connect to remote server
			// this is of course useless
			
			// once connected, send some blobs to both connection
			for (int i = 0; i < 10; i++)
			{
				Thread.sleep(1000);
				remote.send("hello number " + i);
				local.sendToAllPeer("holle number" + i);
			}

			// once blobs sent, close client
			remote.close();	// close connection to client
			
			// we are doing no more in this example, close server
			local.close();	// close server

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		//return 0;
	}
}
