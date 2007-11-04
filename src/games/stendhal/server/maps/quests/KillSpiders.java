package games.stendhal.server.maps.quests;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;

/**
 * QUEST: Kill Spiders
 *
 * PARTICIPANTS: - Morgrin
 *
 * STEPS: - Groundskeeper Morgrin ask you to clean up the school basement
 *        - You go kill the spiders in the basement and you get the reward from Morgrin
 *
 * REWARD: - magical egg - 5000 XP
 *
 * REPETITIONS: - None.
 */

public class KillSpiders extends AbstractQuest {

	private void step_1() {
		SpeakerNPC npc = npcs.get("Morgrin");

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, null,
				ConversationStates.QUEST_OFFERED, null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						if (!player.hasQuest("kill_all_spiders")
								|| player.getQuest("kill_all_spiders").equals(
										"rejected")) {
							engine
									.say("Have you ever been to the basement of the school? The room is full of spiders and some could be dangerous, since the students do some experiments! Did you like to help me with this 'little' problem?");
						} else if (!player.isQuestCompleted("kill_all_spiders")) {
							engine
									.say("I already asked you to kill all creatures in the basement!");
							engine
									.setCurrentState(ConversationStates.ATTENDING);
						} else {
							engine
									.say("Thanks for your help. Now i am sleep well again.");
							engine
									.setCurrentState(ConversationStates.ATTENDING);
						}
					}
				});

		npc
				.add(
						ConversationStates.QUEST_OFFERED,
						ConversationPhrases.YES_MESSAGES,
						null,
						ConversationStates.ATTENDING,
						"Fine. Go down to the basement and kill all creatures there!",
						new SpeakerNPC.ChatAction() {
							@Override
							public void fire(Player player, String text,
									SpeakerNPC engine) {
								player.addKarma(5.0);
								player.setQuest("kill_all_spiders", "start");
								player.removeKill("spider");
								player.removeKill("poisonous_spider");
								player.removeKill("giant_spider");
							}
						});

		npc.add(ConversationStates.QUEST_OFFERED, "no", null,
				ConversationStates.ATTENDING,
				"Ok, i have to find someone else to do this 'little' job!",
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						player.addKarma(-5.0);
						player.setQuest("kill_all_spiders", "rejected");
					}
				});
	}

	private void step_2() {
		/* Player has to kill the creatures*/
	}

	private void step_3() {

		SpeakerNPC npc = npcs.get("Morgrin");

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, String text,
							SpeakerNPC engine) {
						return player.hasQuest("kill_all_spiders")
								&& player.getQuest("kill_all_spiders").equals(
										"start");
					}
				}, ConversationStates.ATTENDING, null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						if (player.hasKilled("spider")
								&& player.hasKilled("poisonous_spider")
								&& player.hasKilled("giant_spider")) {
							engine
										.say("Oh thank you my friend. Here you have something special, i got it from a Magican. Who it was i do not know. What it's good for, i do not know. I Only know, it could be usefull for you.");
								Item mythegg = StendhalRPWorld.get()
										.getRuleManager().getEntityManager()
										.getItem("mythical_egg");
								mythegg.setBoundTo(player.getName());
								player.equip(mythegg, true);
								player.addKarma(5.0);
								player.addXP(5000);
								player.setQuest("kill_all_spiders", "done");
						} else {
							engine
									.say("Go down and kill the creatures, no time left.");
						}
		 			}			
				});
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		step_1();
		step_2();
		step_3();
	}
}
