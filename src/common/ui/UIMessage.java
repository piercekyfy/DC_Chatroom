package common.ui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.*;

import common.models.TextMessage;

public class UIMessage {	
	public UIMessage() {}
	
	public JPanel Initialize(TextMessage message) {
		JPanel root = new JPanel(new BorderLayout());
		JLabel text = new JLabel(message.getSender() + ": " + message.getContent());
		text.setForeground(message.getSender().toLowerCase() == "system" ? Color.RED : Color.BLACK);
		root.add(text, BorderLayout.WEST);
		return root;
	}
}
