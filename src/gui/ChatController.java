package gui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import mechanic.Player;
import network.messages.Message;
import network.messages.SendChatMessage;
import network.messages.UpdateChatMessage;


/**
 * Class to handle all Chat Messages in Lobby and in game.
 *
 * @author nilbecke
 *
 */
public class ChatController {

  private Player player;

  public ChatController(Player p) {
    this.player = p;
  }

  /**
   * updates the chat field with a given message.
   *
   * @author mschmauch
   * 
   * @param message content of message
   * @param dateTime time, message was sent
   * @param sender username of player sending message
   */
  public String updateChat(String message, LocalDateTime dateTime, String sender) {
    String newChatMessage = "";
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
    if (!sender.equals("")) {
      newChatMessage = newChatMessage + sender + ", ";
    }
    newChatMessage = newChatMessage + dateTime.format(dtf) + ": ";
    newChatMessage += message;
    return newChatMessage;
  }

  /**
   * This method creates a new SendChatMessage that is supposed to inform the server that a client
   * has send a chat message in his Client UI. Therefore the new message is send to the server,
   * using the sendMessage() method. If the sender is the actual host, only an UpdateChatMessage is
   * created and send to the server.
   * 
   * @param sender
   * @param message
   */
  public void sendChatMessage(String sender, String message) {
    String chatRegex = ".*\\S+.*";
    if (message.matches(chatRegex)) {
      Message m;
      if (this.player.isHost()) {
        m = new UpdateChatMessage(sender, message, LocalDateTime.now());
      } else {
        m = new SendChatMessage(sender, message, LocalDateTime.now());
      }
      sendMessage(m);
    }
  }

  /**
   * Sends a given message to all players.
   *
   * @param m The Message to be sent
   */
  public void sendMessage(Message m) {
    if (this.player.isHost()) {
      this.player.getServer().sendToAll(m);
    } else {
      this.player.getClientProtocol().sendToServer(m);
    }
  }
}
