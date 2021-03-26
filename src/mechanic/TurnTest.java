package mechanic;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


public class TurnTest {
	private Field l1, l2, l3, l4 ,l5;
	private Letter letter1, letter2, letter3, letter4, letter5;
	private Turn t;
	
	@Before
	public void before() {
		l1 = new Field(1, 2, 1, 1);
		l2 = new Field(2, 1, 1, 1);
		l3 = new Field(1, 1, 1, 1);
		l4 = new Field(1, 1, 1, 1);
		l5 = new Field(1, 1, 1, 1);
		
		letter1 = new Letter('K',1,1,l1);
		letter2 = new Letter('A',1,1,l2);
		letter3 = new Letter('T',1,1,l3);
		letter4 = new Letter('Z',1,1,l4);
		letter5 = new Letter('E',1,1,l5);
		
		
		Letter[] laydDownLetterList = {letter1, letter2, letter3, letter4, letter5};
		
		t= new Turn(laydDownLetterList);
		Word[] word = {new Word(laydDownLetterList)};
		t.setWords(word);
	}

	   
	
	@Test
	public void calculateWordScoreTest() {
		t.calculateTurnScore();
		assertEquals(12, t.getTurnScore());
	}
	
	@Test
	public void print() {
		t.calculateWords();
	}

}
