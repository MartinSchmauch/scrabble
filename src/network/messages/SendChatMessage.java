package network.messages;

import java.time.LocalDate;

/**
 * This message is send, when a player writes a sends a message via the chat.
 * 
 * @author mschmauc
 */

public class SendChatMessage extends Message {
  private static final long serialVersionUID = 1L;
  private String text;
  private LocalDate dateTime;
  private String sender;

  public SendChatMessage(String from, String content, LocalDate timeStamp) {
    super(MessageType.SEND_CHAT_TEXT, from);
    this.setText(content);
    this.setDateTime(timeStamp);
    this.setSender(from);
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public LocalDate getDateTime() {
    return dateTime;
  }

  public void setDateTime(LocalDate dateTime) {
    this.dateTime = dateTime;
  }

  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

}
