package common.ui;

public class UIMessageContent {
	private String username;
	private String text;
	
	public UIMessageContent(String text) {
		this.username = null;
		this.text = text;
	}
	
	public UIMessageContent(String username, String text) {
		this.username = username;
		this.text = text;
	}
	
	public String formatAs(UIMessageType type) {
		if(type == UIMessageType.SYSTEM) {
			return "[SYSTEM]: " + text;
		} else {
			return username + ": " + text;
		}
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
