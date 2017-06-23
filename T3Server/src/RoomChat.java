/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author DSET
 */
import java.rmi.RemoteException;
import java.util.Map;
import java.util.TreeMap;

public class RoomChat implements IRoomChat {
	public String name;
	
	private TreeMap<String, IUserChat> userList;

	public RoomChat(String name) {
            super();
            this.name = name;
            userList = new TreeMap<>();
	}

	@Override
	public int joinRoom(String usrName, IUserChat localObjRef) {
            userList.put(usrName, localObjRef);
            return 1;
	}

	@Override
	public void leaveRoom(String usrName) {
            userList.remove(usrName);
            try{
                for (Map.Entry<String, IUserChat> entry : userList.entrySet()){
                    entry.getValue().deliverMsg("Servidor", usrName + " saiu da sala.", new Integer[20][20]);
                    entry.getValue().updateUserList(userList);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
	}

	@Override
	public void closeRoom() {
//            sendMsg("Servidor", "Esta sala foi fechada.");
	}

	public String getName() {
            return name;
	}

	public void setName(String name) {
            this.name = name;
	}
}
