package ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import javax.swing.*;

import common.ui.UIMessage;
import common.ui.UIMessageContent;
import common.ui.UIMessageType;
import common.ui.UIUser;

public class Interface {
	private JFrame rootFrame;
	private JPanel topPanel;
	private JPanel mainPanel;
	private JPanel userScrollPanel;
	private JPanel messageScrollPanel;
	
	private JTextField portInputField;
	private JButton toggleStartButton;
	
	private JTextField textInputField;
	private JButton confirmButton;
	
	public Interface(String title) {
		rootFrame = new JFrame(title);
		rootFrame.setSize(600,300);
		rootFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		rootFrame.setLayout(new BorderLayout());
		
		topPanel = new JPanel();
		topPanel.setLayout(new GridBagLayout());
		GridBagConstraints topGrid = new GridBagConstraints();
		topGrid.fill = GridBagConstraints.BOTH;
		topGrid.weighty = 1;
		
		portInputField = new JTextField();
		portInputField.setText("6540");
		toggleStartButton = new JButton("Start");
		
		topGrid.weightx = 0.2;
		topGrid.gridx = 0;
		topPanel.add(portInputField, topGrid);
		topGrid.weightx = 0.2;
		topGrid.gridx = 1;
		topPanel.add(toggleStartButton, topGrid);
		
		rootFrame.add(topPanel, BorderLayout.NORTH);
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints mainGrid = new GridBagConstraints();
		mainGrid.fill = GridBagConstraints.BOTH;
		mainGrid.weighty = 1;
		
		userScrollPanel = new JPanel();
		userScrollPanel.setLayout(new BoxLayout(userScrollPanel, BoxLayout.PAGE_AXIS));
		JPanel userContainer = new JPanel(new BorderLayout());
		userContainer.add(userScrollPanel, BorderLayout.NORTH);
		JScrollPane userScrollPane = new JScrollPane(userContainer);
		userScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		userScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		messageScrollPanel = new JPanel();
		messageScrollPanel.setLayout(new BoxLayout(messageScrollPanel, BoxLayout.PAGE_AXIS));
		JPanel messageContainer = new JPanel(new BorderLayout());
		messageContainer.add(messageScrollPanel, BorderLayout.NORTH);
		JScrollPane messageScrollPane = new JScrollPane(messageContainer);
		messageScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		messageScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		mainGrid.weightx = 0.4;
		mainGrid.gridx = 0;
		mainPanel.add(userScrollPane, mainGrid);
		
		mainGrid.weightx = 0.6;
		mainGrid.gridx = 1;
		mainPanel.add(messageScrollPane, mainGrid);
		
		rootFrame.add(mainPanel, BorderLayout.CENTER);
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());
		
		textInputField = new JTextField();
		confirmButton = new JButton("Send");

		confirmButton.setEnabled(false);
		
		bottomPanel.add(textInputField, BorderLayout.CENTER);
		bottomPanel.add(confirmButton, BorderLayout.EAST);
		
		rootFrame.add(bottomPanel, BorderLayout.SOUTH);
		
		appendMessage(new UIMessage(UIMessageType.SYSTEM, new UIMessageContent("Waiting...")));
	}
	
	public void registerOnToggleStart(Consumer<String> callback) {
		toggleStartButton.addActionListener(e -> {
		    String port = portInputField.getText().trim();
			
		    callback.accept(port);
		});
	}
	
	public void setStarted(boolean started) {
		portInputField.setEnabled(!started);
		toggleStartButton.setText(started ? "Stop" : "Start");
		
		topPanel.revalidate();
	}

	public void appendUser(UIUser user) {
		userScrollPanel.add(user.getRoot());
		userScrollPanel.revalidate();
		userScrollPanel.repaint();
	}
	
	public void appendMessage(UIMessage message) {
		messageScrollPanel.add(message.getRoot());
		messageScrollPanel.revalidate();
		messageScrollPanel.repaint();
	}
	
	public void setVisible(boolean visible) {
		rootFrame.setVisible(visible);
	}
}
