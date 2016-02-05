package lib;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;

import java.net.*;

//aspects of this class are based on the Auctioneer practical
public class Client
{
	//when a user quits or is removed from the game this shutdown hook is run.
	static class ClientShutDownThread extends Thread {
		ClientInterface player;
		ServerInterface game;

		public ClientShutDownThread(ClientInterface p, ServerInterface g){
			player = p;
			game = g; 
		}

		public void run() {
			try{
				game.removeUser(player);
			}
			catch (RemoteException e){
	    		System.err.println(e.getMessage());
	    	}
		}
	}
	public static void main(String args[]) throws RemoteException{
		//Change hostname to your machine
		String hostname = "Ruaraidh";
		int port = 50014;
		int callbackport = Integer.parseInt(args[0]);

		System.setProperty("java.security.policy", "world.policy");
		System.setSecurityManager(new RMISecurityManager());

		Runtime r1 = Runtime.getRuntime();
		

		try{

			String regURL = "rmi://" + hostname + ":" + port + "/World";
			System.out.println("Looking up " + regURL);
			ServerInterface game = (ServerInterface)Naming.lookup(regURL);

			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			if(game.getPlayerNum() >= game.getPlayerMax()){
				System.out.println("There is too many players, try again later.");
				System.exit(0);
			}
			//This section is for allowing a user to join the server
			boolean joins = false;
			int indexWorld = -1;
			while(!joins){
				System.out.println("Do you want to join an existing server or create a new one? <join> <new>");
				String servReq = in.readLine().toLowerCase();
				if(servReq.equals("join")){
					System.out.println("Pick a Server " + game.getWorldList());
					indexWorld = Integer.parseInt(in.readLine()) ;
					while(game.checkWorld(indexWorld)){
						System.out.println("Pick a Server " + game.getWorldList());
						indexWorld = Integer.parseInt(in.readLine());
					}
					joins = true;
				}
				else if(servReq.equals("new")){
					if(game.getWorldNum() >= game.getWorldMax()){
						System.out.println("There is too many servers running, please join an existing server");
					}
					else{
						indexWorld = game.newServer();
						joins = true;	
					}
				}
			}

			//this section creates a player
			Boolean nameCheck = true;
			System.out.println("What is your username?");
			String username = in.readLine();

			while(nameCheck){
				nameCheck = game.checkName(username, indexWorld - 1);
				if(nameCheck || username.length() < 1){
					System.out.println("You cannot choose this username, please pick another:");
					username = in.readLine();
				}
				else{
					System.out.println("Username valid.");
				}
			}
			ClientImpl player = new ClientImpl();
			ClientInterface playerStub = (ClientInterface)UnicastRemoteObject.exportObject(player, callbackport);
			game.addUser(playerStub, username, indexWorld);

			r1.addShutdownHook(new ClientShutDownThread(playerStub, game));
			//This is a while loop that will run until the player quits
			String command = "";
			boolean com = false;
			while(true){
				System.out.println("Commands: move <direction>, pickup <item>, message <username>, inventory, quit");
				command = in.readLine();
				boolean corrArg = false;
				String[] arr = {"", ""};
				//this will handle input errors e.g if a user types "move" and not "move north"
				if (command.contains(" ")){
					corrArg = true;
					arr = command.split(" ", 2);
				}
				if(arr[0].toLowerCase().equals("move") || command.toLowerCase().equals("move")){
					String direction = "";
					if(corrArg){
						direction = arr[1];
					}
					while(!corrArg){
						System.out.println("What direction do you want to move?");
						direction = in.readLine();
						if(direction != null){
							corrArg = true;
						}
					}
					String prevLocation = playerStub.getLocation();
					
					corrArg = false;
					while(!corrArg){
						game.moveUser(playerStub, direction);
						
						if(playerStub.getLocation().equals(prevLocation)){
							corrArg = false;
							System.out.println("Please pick a correct direction:");
							direction = in.readLine();
						}
						else{
							corrArg = true;
						}			
					}
				}
				else if(arr[0].toLowerCase().equals("pickup") || command.toLowerCase().equals("pickup")){
					String itm = "";
					if(corrArg){
						itm = arr[1];
					}
					while(!corrArg){
						System.out.println("Who do you want to pickup?");
						itm = in.readLine();
						if(itm != null){
							corrArg = true;
						}
					}
					//condition if item exists

					int success = game.pickUp(playerStub, itm);
					if(success == 1){
						playerStub.addItem(itm);
						System.out.println("You picked up " + itm);
						if(itm.equals("treasure")){
							game.win(playerStub);
							// System.exit(0);
						}
					}
					else if(success == 0){
						System.out.println(itm + " does not exist in this location.");
					}
					else if(success == -1){
						System.out.println("You cannot pick up other players");
					}
				}
				else if(arr[0].toLowerCase().equals("message") || command.toLowerCase().equals("message")){
					String person = "";
					if(corrArg){
						person = arr[1];
					}
					while(!corrArg){
						System.out.println("Who do you want to message?");
						person = in.readLine();
						if(person != null){
							corrArg = true;
						}
					}
					System.out.print("write your message: ");
					String mes = in.readLine();
					boolean feedback = game.messagePlayer(playerStub, person, mes);
					if(feedback == true){
						System.out.println("Sent");
					}
					else{
						System.out.println("Cannot message " + person);
					}
				}
				else if(command.toLowerCase().equals("inventory")){
					System.out.println(playerStub.getInventory());
				}
				else if(command.toLowerCase().equals("quit")){
					
				    System.out.println("Leaving Game...");
				    game.update(playerStub, "quit");
				    System.exit(0);
				}
				else{
					System.out.println("Please enter a valid command");
				}
			}
		}
		catch (java.io.IOException e){
			System.err.println(e.getMessage());
	    	System.exit(0);
        }
		catch (java.rmi.NotBoundException e){
            System.err.println("Server not bound.");
	    	System.err.println(e.getMessage());
	    	System.exit(0);
        }
	}
}
