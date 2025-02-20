package com.leonardobishop.quests.quests.tasktypes.types;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.quests.Task;
import com.leonardobishop.quests.quests.tasktypes.ConfigValue;
import com.leonardobishop.quests.quests.tasktypes.TaskType;

public final class DealDamageTaskType extends TaskType {
	
	private List<ConfigValue> creatorConfigValues = new ArrayList<>();
	
	public DealDamageTaskType() {
		super("dealdamage", "toasted", "Deal a certain amount of damage.");
		this.creatorConfigValues.add(new ConfigValue("amount", true, "Amount of damage you need to deal"));
	}
	
	@Override
	public List<ConfigValue> getCreatorConfigValues() {
		return creatorConfigValues;
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDamage(EntityDamageByEntityEvent e) {
		
		if (!(e.getDamager() instanceof Player)) {
			return;
		}
		
		Player player = (Player) e.getDamager();
		double damage = e.getDamage();
		
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
					
					Double progressDamage;
					int damageNeeded = (int) task.getConfigValue("amount");
					Double damageNeededDouble = (double) damageNeeded;
					
					if (taskProgress.getProgress() == null) {
						progressDamage = 0.0;
					} else {
						progressDamage = (double) taskProgress.getProgress();
					}
					
					taskProgress.setProgress(progressDamage + damage);
					
					if (((double) taskProgress.getProgress()) >= damageNeededDouble) {
						taskProgress.setCompleted(true);
					}					
				}
			}
		}
	}
}
