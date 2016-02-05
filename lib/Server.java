package lib;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


import java.net.InetAddress;

//aspects of this class are based on the auctioneer practical
public class Server
{
	//if the server unexpectedly shutsdown then this shutdown hook will be called
	static class ServerShutDownThread extends Thread {
		
		public void run() {
			System.out.println("Server has shutdown.");
		}
	}

	public static void main(String args[]) throws RemoteException{

		//initiate the shutdown hook
		Runtime r1 = Runtime.getRuntime();
	    r1.addShutdownHook(new ServerShutDownThread());
	    if (args.length < 2) { 
	    	System.err.println("Usage:\njava ServerMainline <registryport> <serverport>"); return; 
	    }
		try {
		    // String hostname = (InetAddress.getLocalHost()).getCanonicalHostName() ;
		    int registryport = Integer.parseInt(args[0]);
		    int serverport = Integer.parseInt(args[1]);
		    
		    System.setProperty("java.security.policy", "world.policy");
		    System.setSecurityManager(new RMISecurityManager());

	        ServerImpl worldServ = new ServerImpl();
		    ServerInterface stub = (ServerInterface)UnicastRemoteObject.exportObject(worldServ, serverport);

		    String regURL = "rmi://localhost:" + registryport + "/World";
	        System.out.println("Registering " + regURL);
	        Naming.rebind(regURL, stub);
		}
		// catch(java.net.UnknownHostException e){
		//     System.err.println("Cannot get local host name.");
		//     System.err.println(e.getMessage());
		//     System.exit(0);
		// }
		catch (java.io.IOException e){
	        System.err.println("Failed to register.");
		    System.err.println(e.getMessage());
		    System.exit(0);
	    }	
	}	
}
