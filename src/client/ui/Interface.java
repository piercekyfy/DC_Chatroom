package client.ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import javax.swing.*;

import common.models.TextMessage;
import common.ui.UIMessage;

public class Interface {
	private JFrame rootFrame;
	private JPanel topPanel;
	private JPanel mainScrollPanel;

	private JTextField hostInputField;
	private JTextField portInputField;
	private JButton connectButton;
	
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
		
		hostInputField = new JTextField();
		hostInputField.setText("127.0.0.1");
		portInputField = new JTextField();
		portInputField.setText("6540");
		connectButton = new JButton("Connect");
		
		topGrid.weightx = 0.6;
		topGrid.gridx = 0;
		topPanel.add(hostInputField, topGrid);
		topGrid.weightx = 0.2;
		topGrid.gridx = 1;
		topPanel.add(portInputField, topGrid);
		topGrid.weightx = 0.2;
		topGrid.gridx = 2;
		topPanel.add(connectButton, topGrid);
		
		rootFrame.add(topPanel, BorderLayout.NORTH);
		
		mainScrollPanel = new JPanel();
		mainScrollPanel.setLayout(new BoxLayout(mainScrollPanel, BoxLayout.PAGE_AXIS));
		
		JPanel north = new JPanel(new BorderLayout());
		north.add(mainScrollPanel, BorderLayout.NORTH);
		
		JScrollPane scrollPane = new JScrollPane(north);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		rootFrame.add(scrollPane, BorderLayout.CENTER);
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());
		
		textInputField = new JTextField();
		confirmButton = new JButton("Send");

		confirmButton.setEnabled(false);
		
		bottomPanel.add(textInputField, BorderLayout.CENTER);
		bottomPanel.add(confirmButton, BorderLayout.EAST);
		
		rootFrame.add(bottomPanel, BorderLayout.SOUTH);
	}
	
	public void registerOnToggleConnect(BiConsumer<String, String> callback) {
		connectButton.addActionListener(e -> {
			String host = hostInputField.getText().trim();
		    String port = portInputField.getText().trim();
			
		    callback.accept(host, port);
		});
	}
	
	public void registerOnSend(Consumer<String> callback) {
		confirmButton.addActionListener(e -> {
			
			String text = textInputField.getText().trim();
			
			callback.accept(text);
		});
	}

	public LoginResult doLoginPopup() {
		JTextField usernameField = new JTextField();
		JTextField passwordField = new JTextField();

		JPanel panel = new JPanel(new GridLayout(2, 2));
		panel.add(new JLabel("Username:"));
		panel.add(usernameField);
		panel.add(new JLabel("Password:"));
		panel.add(passwordField);

		int result = JOptionPane.showConfirmDialog(rootFrame, panel, "Connect", JOptionPane.OK_CANCEL_OPTION);
		return result == JOptionPane.OK_OPTION ? new LoginResult(usernameField.getText().trim(), passwordField.getText().trim()) : new LoginResult(null, null);
	}
	
	public void appendMessage(TextMessage message) {
		mainScrollPanel.add(new UIMessage().Initialize(message));
		mainScrollPanel.revalidate();
		mainScrollPanel.repaint();
	}
	
	public void appendSystemMessage(String text) {
		appendMessage(new TextMessage(-1, "System", text));
	}
	
	public void clearInput() {
		textInputField.setText("");
	}
	
	public void setConnected(boolean connected) {
		hostInputField.setEnabled(!connected);
		portInputField.setEnabled(!connected);
		connectButton.setText(connected ? "Disconnect" : "Connect");
		confirmButton.setEnabled(connected);
		
		topPanel.revalidate();
	}
	
	public void setVisible(boolean visible) {
		rootFrame.setVisible(visible);
	}
}
