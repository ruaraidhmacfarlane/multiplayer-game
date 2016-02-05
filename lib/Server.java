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
	    
		try {
		    String hostname = (InetAddress.getLocalHost()).getCanonicalHostName() ;
		    int registryport = 50014;
		    int serverport = 50015;
		    
		    System.setProperty("java.security.policy", "mud.policy");
		    System.setSecurityManager(new RMISecurityManager());

	        ServerImpl mudServ = new ServerImpl();
		    ServerInterface stub = (ServerInterface)UnicastRemoteObject.exportObject(mudServ, serverport);

		    String regURL = "rmi://" + hostname + ":" + registryport + "/Mud";
	        System.out.println("Registering " + regURL);
	        Naming.rebind(regURL, stub);
		}
		catch(java.net.UnknownHostException e){
		    System.err.println("Cannot get local host name.");
		    System.err.println(e.getMessage());
		    System.exit(0);
		}
		catch (java.io.IOException e){
	        System.err.println("Failed to register.");
		    System.err.println(e.getMessage());
		    System.exit(0);
	    }	
	}	
}
