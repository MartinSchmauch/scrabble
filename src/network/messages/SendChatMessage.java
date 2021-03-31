package network.messages;

import java.util.Date;

/** @author mschmauc */

public class SendChatMessage extends Message {
  private static final long serialVersionUID = 1L;
  private String text;
  private Date dateTime;
  private String sender;

  public SendChatMessage(String from, String content, Date timeStamp) {
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

  public Date getDateTime() {
    return dateTime;
  }

  public void setDateTime(Date dateTime) {
    this.dateTime = dateTime;
  }

  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

}
