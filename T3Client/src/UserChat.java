/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Guilherme Gaiardo
 */
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserChat implements IUserChat {
	
	private String name;
	
	private IServerRoomChat server;
	
	private TreeMap<String, IRoomChat> roomList;
        
        private TreeMap<String, IUserChat> roomUserList;
	
	private IRoomChat joinedRoom;
	
	private Registry registry;
        
        private IUserChat stub;
        
        private javax.swing.JTextArea chatArea;
	
	public UserChat(String name, String host, javax.swing.JTextArea chatArea){
            this.name = name;
            this.chatArea = chatArea;

            try {
                registry = LocateRegistry.getRegistry(host, 1099);
                server = (IServerRoomChat) registry.lookup("Servidor");

            } catch (Exception e) {
                System.err.println("Client exception: " + e.toString());
                e.printStackTrace();
            }
            
//            IUserChat stub;
            try {
                this.stub = (IUserChat) UnicastRemoteObject.exportObject(this, 2020);
//                Registry localRegistry = LocateRegistry.getRegistry(1099);
//                localRegistry.rebind(name, stub);
            } catch (RemoteException ex) {
                Logger.getLogger(UserChat.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            updateRoomList();
	}

	@Override
	public void deliverMsg(String senderUsrName, String msg, Integer[][] clockMatrix) throws RemoteException {
            chatArea.append("\n\n" + senderUsrName + ":\n" + msg);
	}
        
        @Override
        public void updateUserList(TreeMap<String, IUserChat> userList) throws RemoteException {
            this.roomUserList = userList;
        }
	
	public void updateRoomList(){
            try {
                roomList = server.getRooms();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
	}
	
	public void SendMsg(String msg){
            try {
                for (Map.Entry<String, IUserChat> entry : roomUserList.entrySet()){
                    entry.getValue().deliverMsg(this.name, msg, new Integer[20][20]);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
	}
	
	public void JoinRoom(String room){
            if (joinedRoom != null){
                chatArea.append("\n\nVoce ja esta em uma sala!\n\n");
                return;
            }
            try {
                IRoomChat room_stub = (IRoomChat) registry.lookup(room);
                room_stub.joinRoom(name, this.stub);
                joinedRoom = room_stub;
                chatArea.append("\n\nVoce entrou na sala " + room + "!\n\n");
            } catch (Exception e) {
                System.err.println("Client exception: " + e.toString());
                e.printStackTrace();
            }
	}
	
	public void LeaveRoom(){
            try {
                joinedRoom.leaveRoom(name);
                chatArea.append("\n\nVoce saiu da sala!\n\n");
                joinedRoom = null;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
	}
        
        public void CreateRoom(String name){
            try {
                server.createRoom(name);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        }
}
