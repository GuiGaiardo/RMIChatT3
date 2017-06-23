/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Guilherme Gaiardo
 */
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.List;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.DefaultListModel;

public class ServerRoomChat implements IServerRoomChat {
	
	private TreeMap<String, IRoomChat> roomList;
	
	private Registry registry;
        
        private DefaultListModel listRooms;
	
	public ServerRoomChat(DefaultListModel list) {
		super();
                listRooms = list;
		roomList = new TreeMap<>();
		
		try {
			registry = LocateRegistry.getRegistry();
                        
                        createRoom("Sala Default");
			
//			RoomChat defaultRoom = new RoomChat("Sala Default");
//			IRoomChat room_stub = (IRoomChat) UnicastRemoteObject.exportObject(defaultRoom, 2020);
//			
//			registry.rebind("Sala Default", room_stub);
//			roomList.add(defaultRoom);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public TreeMap<String, IRoomChat> getRooms() {
		return roomList;
	}

	@Override
	public void createRoom(String roomName) {
            for (Map.Entry<String, IRoomChat> entry : roomList.entrySet()){
                String name = entry.getKey();
                if (name.equalsIgnoreCase(roomName))
                    return;
            }
		try {
			RoomChat newRoom = new RoomChat(roomName);
			IRoomChat room_stub = (IRoomChat) UnicastRemoteObject.exportObject(newRoom, 2020);
			registry.rebind(roomName, room_stub);
			roomList.put(roomName, room_stub);
                        listRooms.addElement(roomName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
        
        public boolean deleteRoom(String roomName){
            for (Map.Entry<String, IRoomChat> entry : roomList.entrySet()){
                String name = entry.getKey();
                IRoomChat room = entry.getValue();
                if (name.equalsIgnoreCase(roomName)){
                    try {
                        registry.unbind(roomName);
                        UnicastRemoteObject.unexportObject(room, true);
                        room.closeRoom();
                        roomList.remove(name);
                        return true;
                    } catch (RemoteException | NotBoundException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            return false;
        }
}