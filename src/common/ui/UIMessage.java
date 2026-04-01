package common.ui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.*;

public class UIMessage {
	private UIMessageType type;
	private UIMessageContent content;
	
	private JPanel root;
	private JLabel text;
	
	public UIMessage(UIMessageType type, UIMessageContent content) {
		this.type = type;
		this.content = content;
		
		root = new JPanel(new BorderLayout());
		text = new JLabel(content.formatAs(type));
		text.setForeground(type == UIMessageType.SYSTEM ? Color.RED : Color.BLACK);
		root.add(text, BorderLayout.WEST);
	}
	
	public JPanel getRoot() {
		return root;
	}
}
