// $Id$
package games.stendhal.server.entity.npc;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.TurnListener;

/**
 * causes the speaker npc to loop a repeated monologue while he is not attending a player.
 * the text for repeating can have more than one option, in which case he says each in turn.
 * 
 * @author kymara
 */
public final class MonologueBehaviour implements TurnListener {


	private final SpeakerNPC speakerNPC;
	private final String[] repeatedText;
	private int i = 0;
	
	/**
	 * Creates a new MonologueBehaviour.
	 * 
	 * @param speakerNPC
	 *            SpeakerNPC
	 * @param repeatedText
	 *            text to repeat
	 */
	public MonologueBehaviour(final SpeakerNPC speakerNPC,
			final String[] repeatedText) {
		this.speakerNPC = speakerNPC;
		this.repeatedText = repeatedText;
		SingletonRepository.getTurnNotifier().notifyInTurns(1, this);
	}

	public void onTurnReached(final int currentTurn) {
		if (speakerNPC.getEngine().getCurrentState() == ConversationStates.IDLE) {
			speakerNPC.say(repeatedText[i % repeatedText.length]);
			speakerNPC.setCurrentState(ConversationStates.IDLE);
			if (i == Integer.MAX_VALUE) {
				// deal with overflow (only takes 9 hours :P)
				// probably means there is a better way to do it, but this should work...
				i = 0;
			} else { 
				i = i + 1;
			}
		}
		// Schedule so we are notified again in 60 seconds
		SingletonRepository.getTurnNotifier().notifyInTurns(60, this);
	}
}
