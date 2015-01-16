

class ExampleServer implements NeTextReceiverIntf
{
	static Boolean stop = false;
	static NeTextServer local;
	
	// receive() must be implemented when implementing interface NeTextReceiverIntf
	// all received strings will come here
	public void receive (String from, String line)
	{
		System.out.println("received from '" + from + "': " + line);
		local.sendToAllPeer(from + " said " + line);
		if (line.compareTo("stop") == 0)
		{
			local.sendToAllPeer("server is leaving");
			stop = true;
		}
		
	}

	public static void main (String args [])
	{
		try
		{
			if (args.length != 1)
			{
				System.err.println("need 1 args: localServerPort");
				return;
			}

			// parse command line
			int localPort = new Integer(args[0]).intValue();

			// instantiate interface NeTextReceiverIntf for receiver
			ExampleServer receiver = new ExampleServer();

			// start local server in a thread
			local = new NeTextServer(localPort, receiver);

			// we are doing no more in this example, close server
			while (!stop)
				Thread.sleep(1000);

			local.close();	// close server

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		//return 0;
	}
}
