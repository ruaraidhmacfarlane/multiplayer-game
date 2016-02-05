package lib;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote
{
	public void printInfo(String function, int index) throws RemoteException;
	public String getName() throws RemoteException;
    public String getLocation() throws RemoteException;
    public String getPrevLoc() throws RemoteException;
    public int getIndex() throws RemoteException;
    public void setName(String name) throws RemoteException;
    public void setLocation(String location) throws RemoteException;
    public void setPrevLoc(String location) throws RemoteException;
    public void setIndex(int index) throws RemoteException;
    public void addItem(String thing) throws RemoteException;
    public String getInventory() throws RemoteException;
    public void quitUser() throws RemoteException;
}
