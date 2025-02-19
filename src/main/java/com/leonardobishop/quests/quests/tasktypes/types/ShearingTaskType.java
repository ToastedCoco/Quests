package com.leonardobishop.quests.quests.tasktypes.types;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.player.questprogressfile.QuestProgressFile;
import com.leonardobishop.quests.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.quests.Task;
import com.leonardobishop.quests.quests.tasktypes.ConfigValue;
import com.leonardobishop.quests.quests.tasktypes.TaskType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerShearEntityEvent;

import java.util.ArrayList;
import java.util.List;

public final class ShearingTaskType extends TaskType {

    private List<ConfigValue> creatorConfigValues = new ArrayList<>();

    public ShearingTaskType() {
        super("shearing", "lmbishop", "Shear a set amount of sheep.");
        this.creatorConfigValues.add(new ConfigValue("amount", true, "Amount of cows to be milked."));
    }

    @Override
    public List<ConfigValue> getCreatorConfigValues() {
        return creatorConfigValues;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onShear(PlayerShearEntityEvent event) {
        if (!(event.getEntity() instanceof Sheep)) {
            return;
        }

        Player player = event.getPlayer();

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

                    int sheepNeeded = (int) task.getConfigValue("amount");

                    int progressSheared;
                    if (taskProgress.getProgress() == null) {
                        progressSheared = 0;
                    } else {
                        progressSheared = (int) taskProgress.getProgress();
                    }

                    taskProgress.setProgress(progressSheared + 1);

                    if (((int) taskProgress.getProgress()) >= sheepNeeded) {
                        taskProgress.setCompleted(true);
                    }
                }
            }
        }
    }

}
