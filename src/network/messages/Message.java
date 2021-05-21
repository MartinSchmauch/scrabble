package network.messages;

import java.io.Serializable;

/**
 * Every Messageclass extends this abstract class. If a Messsage is send, you can use the
 * getMessageType() Method to find out the Message type and cast the Object onto the correct Message
 * Class.
 *
 * @author lurny
 */
public abstract class Message implements Serializable {
  private static final long serialVersionUID = 1L;

  private MessageType meType;
  private String from;

  public Message(MessageType type, String from) {
    this.meType = type;
    this.from = new String(from);
  }

  public MessageType getMessageType() {
    return this.meType;
  }

  public String getFrom() {
    return this.from;
  }

  public void setFrom(String name) {
    this.from = name;
  }
}
