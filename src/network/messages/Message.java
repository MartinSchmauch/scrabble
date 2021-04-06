/** @author lurny */
package network.messages;

import java.io.Serializable;

public abstract class Message implements Serializable {
  private static final long serialVersionUID = 1L;

  private MessageType mType;
  private String from;

  public Message(MessageType type, String from) {
    this.mType = type;
    this.from = new String(from);
  }

  public MessageType getMessageType() {
    return this.mType;
  }

  public String getFrom() {
    return this.from;
  }

  public void setFrom(String name) {
    this.from = name;
  }
}
