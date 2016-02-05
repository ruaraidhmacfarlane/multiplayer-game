package lib;

import java.rmi.RemoteException;

import java.util.ArrayList;

public class ClientImpl implements ClientInterface
{
    String name = "";
    String prevLoc = "";
    String location = "";
    int mudIndex = -1;
    ArrayList<String> inventory = new ArrayList<>();

    public ClientImpl() throws RemoteException {
    	
    }

    //this will print to the users specific message if needed
    public void printInfo(String message, int index) throws RemoteException {
        if(index == mudIndex){
            System.out.println(message);
        }
    }

    //getters and setters

    public String getName() throws RemoteException {
        return name;
    }

    public String getLocation() throws RemoteException {
        return location;
    }

    public String getPrevLoc() throws RemoteException {
        return prevLoc;
    }

    public int getIndex() throws RemoteException {
        return mudIndex;
    }

    public void setName(String name) throws RemoteException {
    	this.name = name;
    }

    public void setLocation(String location) throws RemoteException {
        this.location = location;
    }

    public void setPrevLoc(String location) throws RemoteException {
        this.prevLoc = location;
    }

    public void setIndex(int index) throws RemoteException {
        mudIndex = index - 1;
    }

    public void addItem(String thing) throws RemoteException {
    	inventory.add(thing);
    }

    public String getInventory() throws RemoteException {
    	String result = "";
        if(inventory.size() == 0){
            return "empty";
        }
        else{
        	for(String s : inventory){
        		result += " * " + s + "\n";
        	}
        	return result;
        }
    }

    public void quitUser() throws RemoteException {
        System.exit(0);
    }
}
