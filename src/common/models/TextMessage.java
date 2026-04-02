package common.models;

public class TextMessage {
	private int id;
	private String sender;
	private String content;
	
	public TextMessage(int id, String sender, String content) {
		this.id = id;
		this.sender = sender;
		this.content = content;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
