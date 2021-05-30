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

  /**
   * creats an instance of the class.
   */
  public Message(MessageType type, String from) {
    this.meType = type;
    this.from = new String(from);
  }

  /**
   * gets the enum MessageType of the current instance.
   */
  public MessageType getMessageType() {
    return this.meType;
  }

  /**
   * gets the variable from of the current instance.
   */
  public String getFrom() {
    return this.from;
  }

  /**
   * sets the variable from of the current instance.
   */
  public void setFrom(String name) {
    this.from = name;
  }
}
