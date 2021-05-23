package mechanic;

/**
 * Datatyp for word of Wordlist. Have a look at WordlistHandler.
 *
 * @author pkoenig
 *
 */
public class WordOnList {

  private String wordString;
  private String definition;

  public WordOnList(String wordString, String definition) {
    this.setWordString(wordString);
    this.setDefinition(definition);
  }

  /**
   * @return the wordString
   */
  public String getWordString() {
    return wordString;
  }

  /**
   * @param wordString the wordString to set
   */
  public void setWordString(String wordString) {
    this.wordString = wordString;
  }

  /**
   * @return the definition
   */
  public String getDefinition() {
    return definition;
  }

  /**
   * @param definition the definition to set
   */
  public void setDefinition(String definition) {
    this.definition = definition;
  }

  @Override
  public String toString() {
    // TODO Auto-generated method stub
    return "Word: " + this.wordString + "\n Definition: " + this.definition + "\n";
  }

}
