package peer;

import java.util.ArrayList;

public class Peer {
	private String peerId;
	private String peerPseudo;
	private ArrayList<String> peersList;
	private ArrayList<String> filesNames;
	
	public Peer(String peerId, String peerPseudo, ArrayList<String> peersList, ArrayList<String> filesNames) {
		super();
		this.peerId = peerId;
		this.peerPseudo = peerPseudo;
		this.peersList = peersList;
		this.filesNames = filesNames;
	}

	public String getPeerId() {
		return peerId;
	}

	public void setPeerId(String peerId) {
		this.peerId = peerId;
	}
	
	public String getPeerPseudo() {
		return peerPseudo;
	}

	public void setPeerPseudo(String peerPseudo) {
		this.peerPseudo = peerPseudo;
	}

	public ArrayList<String> getPeersList() {
		return peersList;
	}

	public void setPeersList(ArrayList<String> peersList) {
		this.peersList = peersList;
	}

	public ArrayList<String> getFilesNames() {
		return filesNames;
	}

	public void setFilesNames(ArrayList<String> filesNames) {
		this.filesNames = filesNames;
	}
	
	
}
