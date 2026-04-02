package common.ui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;

import common.models.UserSession;

public class UIUser {
	public UIUser() {}
	
	public JPanel Initalize(UserSession user) {
		if(user == null)
			return null;
		
		JPanel root = new JPanel(new BorderLayout());
		JLabel text = new JLabel(user.getUsername() + "(" + user.getSessionId() + "): " + (user.isOnline() ? "Online" : "Offline."));
		text.setForeground(Color.BLACK);
		root.add(text, BorderLayout.CENTER);
		
		return root;
	}
}
