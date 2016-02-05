package lib;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote
{
    public void addUser(ClientInterface player, String name, int index) throws RemoteException;
    public void removeUser(ClientInterface player) throws RemoteException;
    public void moveUser(ClientInterface player, String dir) throws RemoteException;
    public int pickUp(ClientInterface player, String item) throws RemoteException;
    public boolean checkMud(int index) throws RemoteException;
    public int newServer() throws RemoteException;
    public String getMudList() throws RemoteException;
    public int getMudNum() throws RemoteException;
    public int getMudMax() throws RemoteException;
    public int getPlayerNum() throws RemoteException;
    public int getPlayerMax() throws RemoteException;
  	public String getInfo(ClientInterface player) throws RemoteException;
  	public boolean checkName(String name, int index) throws RemoteException;
    public void update(ClientInterface player, String function) throws RemoteException;
    public boolean messagePlayer(ClientInterface player, String name, String message) throws RemoteException;
    public void win(ClientInterface player) throws RemoteException;
}
