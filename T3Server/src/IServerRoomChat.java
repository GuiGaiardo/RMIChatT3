/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Guilherme Gaiardo
 */
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.TreeMap;

public interface IServerRoomChat extends Remote {
	public TreeMap<String, IRoomChat> getRooms() throws RemoteException;
	public void createRoom(String roomName) throws RemoteException;
}
