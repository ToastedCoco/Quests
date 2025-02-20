package com.leonardobishop.quests.quests.tasktypes.types;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.quests.Task;
import com.leonardobishop.quests.quests.tasktypes.ConfigValue;
import com.leonardobishop.quests.quests.tasktypes.TaskType;

public final class BreedingTaskType extends TaskType {
	
	private List<ConfigValue> creatorConfigValues = new ArrayList<>();
	
	public BreedingTaskType() {
		super("breeding", "toasted", "Breed a set amount of animals.");
		this.creatorConfigValues.add(new ConfigValue("amount", true, "Amount of animals to be bred"));
	}
	
	@Override
	public List<ConfigValue> getCreatorConfigValues() {
		return creatorConfigValues;
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBreed(CreatureSpawnEvent e) {
		if (!e.getSpawnReason().equals(SpawnReason.BREEDING)) {
			return;
		}
		
		Entity ent = e.getEntity();
		List<Entity> entList = ent.getNearbyEntities(10, 10, 10);
		
		if (entList.isEmpty()) {
			return;
		}
		// Check if there is a player in the list, otherwise: return.
		for (Entity current : entList) {
			
			if (current instanceof Player) {
				Player player = (Player) current;
				QPlayer qPlayer = Quests.getPlayerManager().getPlayer(player.getUniqueId());
				QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile();
				
				for (Quest quest : super.getRegisteredQuests()) {
					if (questProgressFile.hasStartedQuest(quest)) {
						QuestProgress questProgress = questProgressFile.getQuestProgress(quest);
						
						for (Task task : quest.getTasksOfType(super.getType())) {
							TaskProgress taskProgress = questProgress.getTaskProgress(task.getId());
							
							if (taskProgress.isCompleted()) {
								continue;
							}
							
							int breedingNeeded = (int) task.getConfigValue("amount");
							int breedingProgress;
							
							if (taskProgress.getProgress() == null) {
								breedingProgress = 0;
							} else {
								breedingProgress = (int) taskProgress.getProgress();
							}
							
							taskProgress.setProgress(breedingProgress + 1);
							
							if (((int) taskProgress.getProgress()) >= breedingNeeded) {
								taskProgress.setCompleted(true);
							}							
						}
					}
				}
			}
		}
	}
}
