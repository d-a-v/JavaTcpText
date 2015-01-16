

class ExampleClient implements NeTextReceiverIntf
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
			if (args.length != 2)
			{
				System.err.println("need 2 args: remoteServerName/IP remoteServerPort");
				return;
			}

			// parse command line
			String remoteName = args[0];
			int remotePort = new Integer(args[1]).intValue();

			// instantiate interface NeTextReceiverIntf for receiver
			ExampleClient receiver = new ExampleClient();

			// try to connect to the remote client
			NeTextClient remote = new NeTextClient(remoteName, remotePort, receiver);
			while (true)
			{
				System.out.println("Trying to connect to " + remoteName + ":" + remotePort);
				if (remote.connect())
					break;
				Thread.sleep(1000);
			}

			// once connected, send some blobs 
			for (int i = 0; i < 10; i++)
			{
				Thread.sleep(1000);
				remote.send("hello number " + i);
			}

			// once blobs sent, close client
			remote.close();	// close connection to client
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		//return 0;
	}
}
