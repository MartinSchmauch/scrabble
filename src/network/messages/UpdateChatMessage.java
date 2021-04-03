package network.messages;

import java.time.LocalDate;

/** @author mschmauc */

public class UpdateChatMessage extends Message {
  private static final long serialVersionUID = 1L;
  private String text;
  private LocalDate dateTime;


  public UpdateChatMessage(String from, String content, LocalDate timeStamp) {
    super(MessageType.UPDATE_CHAT, from);
    this.text =content;
    this.dateTime = timeStamp;
  }

  public String getText() {
    return text;
  }

  public LocalDate getDateTime() {
    return dateTime;
  }


}
