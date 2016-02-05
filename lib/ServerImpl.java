package lib;

import java.rmi.RemoteException;

import java.util.Vector;
import java.util.List;
import java.util.ArrayList;

public class ServerImpl implements ServerInterface
{
	String edgesFile = "mymud.edg";
	String messageFile = "mymud.msg";
	String thingFile = "mymud.thg";

    

    List<World> mudList = new ArrayList<World>();
    int mudNum = 3;
    int maxMuds = 4;
    int mudIndex = -1;

	List<ClientInterface> playerList = new ArrayList<ClientInterface>();
    //picked this number to allow 2 players per mud (if a new mud is created)
    int maxPlayer = 8;
    int numPlayer = 0;



    public ServerImpl() throws RemoteException {
    	World myMud1 = new World("mymud.edg", "mymud.msg", "mymud.thg");
        World myMud2 = new World("mymud.edg", "mymud.msg", "mymud.thg");
        World myMud3 = new World("mymud.edg", "mymud.msg", "mymud.thg");
        mudList.add(myMud1);
        mudList.add(myMud2);
        mudList.add(myMud3);
        System.out.println("Initiated.");

    }

    public synchronized void addUser(ClientInterface player, String name, int index) throws RemoteException {	
        player.setIndex(index);
        String start = mudList.get(player.getIndex()).startLocation();
    	player.setName(name);
    	player.setLocation(start);
    	numPlayer++;

    	playerList.add(player);
    	mudList.get(player.getIndex()).addThing(start, player.getName());

    	System.out.println(player.getName() + " was added to mud " + index);

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
        mudList.get(player.getIndex()).delThing(player.getLocation(), player.getName());

        System.out.println(player.getName() + " has left the game");
        this.update(player, "remove");
    }

    public void moveUser(ClientInterface player, String dir) throws RemoteException {
        //prev location to allow users in the prev location to be notified the user has moved
        player.setPrevLoc(player.getLocation());
    	String location = mudList.get(player.getIndex()).moveThing(player.getLocation(), dir, player.getName()); 
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
    	if(mudList.get(player.getIndex()).locationInfo(player.getLocation()).contains(item)){ 		
    		mudList.get(player.getIndex()).delThing(player.getLocation(), item);
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
        World newMud = new World("mymud.edg", "mymud.msg", "mymud.thg");
        mudList.add(newMud);
        mudNum++;
        System.out.println("A new server has been created.");
        return (mudList.size());
    }
    //this formats the string so that there is not a comma at the end of the list.
    public String getMudList() throws RemoteException{
        String s = "";
        int j;
        for(int i = 0; i < mudList.size(); i++){
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
    //error checking to make sure the user is trying to connect to a mud that exists
    public boolean checkMud(int index) throws RemoteException{
        boolean truth = false;
        if(mudList.get(index-1) != null){
            truth = false;
            System.out.println("Mud okay");
        }
        return truth;
    }

    public int getMudNum() throws RemoteException{
        return mudNum;
    }
    public int getMudMax() throws RemoteException{
        return maxMuds;
    }
    public int getPlayerNum() throws RemoteException{
        return numPlayer;
    }
    public int getPlayerMax() throws RemoteException{
        return maxPlayer;
    }
    public String getInfo(ClientInterface player) throws RemoteException{
        String s = mudList.get(player.getIndex()).locationInfo(player.getLocation()).replace(player.getName(), "");
       
        return s;
    }
    public boolean checkName(String name, int index) throws RemoteException{
        return mudList.get(index).checkThings(name);
    }

    public void update(ClientInterface player, String function) throws RemoteException {
        for(ClientInterface p : playerList){
            //fix this so it doesnt send all users the new location info (one info for prev location and another for another)
            String mes = mudList.get(player.getIndex()).locationInfo(player.getLocation()).replace(p.getName(), "");
            if(function.equals("move")){
                if(player.getPrevLoc().equals(p.getLocation())){
                    p.printInfo(mudList.get(p.getIndex()).locationInfo(p.getLocation()).replace(p.getName(), ""), player.getIndex());
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
        World newMud = new World("mymud.edg", "mymud.msg", "mymud.thg");
        //replaces mud
        mudList.set(player.getIndex(), newMud);
        System.out.println(player.getName() + " won and the server will be reset");
        String mes = player.getName() + " won the game! The game will now close...";
        for(ClientInterface p : playerList){
            p.printInfo(mes, player.getIndex());
            if(p.getIndex() == player.getIndex()){
                //call system.exit(0) on all users in the mud server of the player that won
                p.quitUser();
            }
        }
    }
}
