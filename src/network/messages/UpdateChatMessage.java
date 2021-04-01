package network.messages;

import java.time.LocalDate;

/** @author mschmauc */

public class UpdateChatMessage extends Message {
  private static final long serialVersionUID = 1L;
  private String text;
  private LocalDate dateTime;
  private String sender;

  public UpdateChatMessage(String from, String content, LocalDate timeStamp) {
    super(MessageType.UPDATE_CHAT_TEXT, from);
    // TODO Auto-generated constructor stub
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
