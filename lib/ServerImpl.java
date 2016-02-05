package lib;

import java.rmi.RemoteException;

import java.util.Vector;
import java.util.List;
import java.util.ArrayList;

public class ServerImpl implements ServerInterface
{
	String edgesFile = "myworld.edg";
	String messageFile = "myworld.msg";
	String thingFile = "myworld.thg";

    

    List<World> worldList = new ArrayList<World>();
    int worldNum = 3;
    int maxWorlds = 4;
    int worldIndex = -1;

	List<ClientInterface> playerList = new ArrayList<ClientInterface>();
    //picked this number to allow 2 players per world (if a new world is created)
    int maxPlayer = 8;
    int numPlayer = 0;



    public ServerImpl() throws RemoteException {
    	World myWorld1 = new World("myworld.edg", "myworld.msg", "myworld.thg");
        World myWorld2 = new World("myworld.edg", "myworld.msg", "myworld.thg");
        World myWorld3 = new World("myworld.edg", "myworld.msg", "myworld.thg");
        worldList.add(myWorld1);
        worldList.add(myWorld2);
        worldList.add(myWorld3);
        System.out.println("Initiated.");

    }

    public synchronized void addUser(ClientInterface player, String name, int index) throws RemoteException {	
        player.setIndex(index);
        String start = worldList.get(player.getIndex()).startLocation();
    	player.setName(name);
    	player.setLocation(start);
    	numPlayer++;

    	playerList.add(player);
    	worldList.get(player.getIndex()).addThing(start, player.getName());

    	System.out.println(player.getName() + " was added to world " + index);

        this.update(player, "add");
    }

    public synchronized void removeUser(ClientInterface player) throws RemoteException {
        for(int i = 0; i < playerList.size(); i++){
            String tempName = playerList.get(i).getName();
            if(tempName.equals(player.getName())){
                playerList.remove(i);
            }
        }
        numPlayer--;
        worldList.get(player.getIndex()).delThing(player.getLocation(), player.getName());

        System.out.println(player.getName() + " has left the game");
        this.update(player, "remove");
    }

    public void moveUser(ClientInterface player, String dir) throws RemoteException {
        //prev location to allow users in the prev location to be notified the user has moved
        player.setPrevLoc(player.getLocation());
    	String location = worldList.get(player.getIndex()).moveThing(player.getLocation(), dir, player.getName()); 
    	player.setLocation(location);
        if(!player.getPrevLoc().equals(player.getLocation())){
    	   System.out.println(player.getName() + " has moved " + dir);
            this.update(player, "move");
        }
    }

    public int pickUp(ClientInterface player, String item) throws RemoteException {
        //-1 is the error flag to not allow players to pick up other players
    	for(ClientInterface p : playerList){
    		if(item.equals(p.getName())){
    			return -1;
    		}
    	}
        //checks the item exists in the current location and then allows the user to pick it up
    	if(worldList.get(player.getIndex()).locationInfo(player.getLocation()).contains(item)){ 		
    		worldList.get(player.getIndex()).delThing(player.getLocation(), item);
	    	System.out.println(player.getName() + " picked up " + item);
            //will tell all players in that area that the user picked up an item
            this.update(player, "pickup");
	    	return 1;
    	}
        //if the item does not exist in the location then it will return the error flag 0
    	else{
	    	return 0;
	    }

        
    }
    //returns the index of the new server to allow the user to join it.
    public int newServer() throws RemoteException{
        World newWorld = new World("myworld.edg", "myworld.msg", "myworld.thg");
        worldList.add(newWorld);
        worldNum++;
        System.out.println("A new server has been created.");
        return (worldList.size());
    }
    //this formats the string so that there is not a comma at the end of the list.
    public String getWorldList() throws RemoteException{
        String s = "";
        int j;
        for(int i = 0; i < worldList.size(); i++){
            if(i == 0){
                j = i + 1;
                s += j;
            }
            else{
                j = i +1;
                s += ", " + j;
            }
        }
        return s;
    }
    //error checking to make sure the user is trying to connect to a world that exists
    public boolean checkWorld(int index) throws RemoteException{
        boolean truth = false;
        if(worldList.get(index-1) != null){
            truth = false;
            System.out.println("World okay");
        }
        return truth;
    }

    public int getWorldNum() throws RemoteException{
        return worldNum;
    }
    public int getWorldMax() throws RemoteException{
        return maxWorlds;
    }
    public int getPlayerNum() throws RemoteException{
        return numPlayer;
    }
    public int getPlayerMax() throws RemoteException{
        return maxPlayer;
    }
    public String getInfo(ClientInterface player) throws RemoteException{
        String s = worldList.get(player.getIndex()).locationInfo(player.getLocation()).replace(player.getName(), "");
       
        return s;
    }
    public boolean checkName(String name, int index) throws RemoteException{
        return worldList.get(index).checkThings(name);
    }

    public void update(ClientInterface player, String function) throws RemoteException {
        for(ClientInterface p : playerList){
            //fix this so it doesnt send all users the new location info (one info for prev location and another for another)
            String mes = worldList.get(player.getIndex()).locationInfo(player.getLocation()).replace(p.getName(), "");
            if(function.equals("move")){
                if(player.getPrevLoc().equals(p.getLocation())){
                    p.printInfo(worldList.get(p.getIndex()).locationInfo(p.getLocation()).replace(p.getName(), ""), player.getIndex());
                }
            }
            if(player.getLocation().equals(p.getLocation())){
                p.printInfo(mes, player.getIndex());
            }
        }
    }

    public boolean messagePlayer(ClientInterface player, String name, String message) throws RemoteException {
        boolean success = false;
        String mes = player.getName() + " says: " + message;
        //loops through all players in the same server and will messsage them
        for(ClientInterface p : playerList){
            if(name.equals(p.getName()) && player.getIndex() == p.getIndex()){
                p.printInfo(mes, player.getIndex());
                success = true;
                System.out.println(player.getName() + " messaged " + name + " saying: " + message);
            }
        }
        return success;
    }
    //when the user picks up the treasure they will win and players will be removed from server and then server will be restarted
    public void win(ClientInterface player) throws RemoteException {
        World newWorld = new World("myworld.edg", "myworld.msg", "myworld.thg");
        //replaces world
        worldList.set(player.getIndex(), newWorld);
        System.out.println(player.getName() + " won and the server will be reset");
        String mes = player.getName() + " won the game! The game will now close...";
        for(ClientInterface p : playerList){
            p.printInfo(mes, player.getIndex());
            if(p.getIndex() == player.getIndex()){
                //call system.exit(0) on all users in the world server of the player that won
                p.quitUser();
            }
        }
    }
}
