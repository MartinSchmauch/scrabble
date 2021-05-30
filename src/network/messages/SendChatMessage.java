package network.messages;

import java.time.LocalDateTime;

/**
 * This message is send, when a player writes text a message via the chat.
 *
 * @author mschmauc
 */

public class SendChatMessage extends Message {
  private static final long serialVersionUID = 1L;
  private String text;
  private LocalDateTime dateTime;
  private String sender;

  /**
   * This method creates an instance of the class.
   */
  public SendChatMessage(String from, String content, LocalDateTime timeStamp) {
    super(MessageType.SEND_CHAT_TEXT, from);
    this.setText(content);
    this.setDateTime(timeStamp);
    this.setSender(from);
  }

  /**
   * gets the variable text of the current instance.
   */
  public String getText() {
    return text;
  }

  /**
   * sets the variable text of the current instance.
   */
  public void setText(String text) {
    this.text = text;
  }

  /**
   * gets the variable dateTime of the current instance.
   */
  public LocalDateTime getDateTime() {
    return dateTime;
  }

  /**
   * sets the variable dateTime of the current instance.
   */
  public void setDateTime(LocalDateTime dateTime) {
    this.dateTime = dateTime;
  }

  /**
   * gets the variable sender of the current instance.
   */
  public String getSender() {
    return sender;
  }

  /**
   * sets the variable sender of the current instance.
   */
  public void setSender(String sender) {
    this.sender = sender;
  }

}
