package common.ui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class UIUser {
	private String username;
	private int sessionId;
	
	private JPanel root;
	private JLabel text;
	
	public UIUser(String username, int sessionId) {
		this.username = username;
		this.sessionId = sessionId;
		
		root = new JPanel(new BorderLayout());
		text = new JLabel(username + "(" + sessionId + ")");
		text.setForeground(Color.BLACK);
		root.add(text, BorderLayout.CENTER);
	}
	
	public JPanel getRoot() {
		return root;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public int getSessionId() {
		return sessionId;
	}
	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}
}
