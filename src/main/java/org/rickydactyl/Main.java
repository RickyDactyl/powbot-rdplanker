package org.rickydactyl;

import com.google.common.eventbus.Subscribe;
import org.powbot.api.Condition;
import org.powbot.api.Notifications;
import org.powbot.api.event.SkillLevelUpEvent;
import org.powbot.api.rt4.Player;
import org.powbot.api.rt4.Players;
import org.powbot.api.rt4.World;
import org.powbot.api.rt4.Worlds;
import org.powbot.api.rt4.walking.model.Skill;
import org.powbot.api.script.*;
import org.powbot.api.script.paint.Paint;
import org.powbot.api.script.paint.PaintBuilder;
import org.powbot.mobile.service.ScriptUploader;
import org.rickydactyl.tasks.BankTask;
import org.rickydactyl.tasks.LumberyardTask;
import org.rickydactyl.tasks.SetupTask;
import org.rickydactyl.tasks.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

@ScriptConfiguration(name = "Plank type:", description = "What plank type do you want to make?", allowedValues = {"Plank","Oak plank","Teak plank","Mahogany plank"},optionType = OptionType.STRING)
@ScriptConfiguration(name = "Plank Location:", description = "Location for processing planks?", allowedValues = {"Woodcutting guild","Varrock lumberyard"},optionType = OptionType.STRING)
@ScriptConfiguration(name = "Travel type:", description = "What travel method do you want to use?", allowedValues = {"Walk","Lumberyard teleport","Ring of the elements","Balloon transport system"},optionType = OptionType.STRING)
@ScriptConfiguration(name = "Banking method:", description = "How are we going to bank?", allowedValues = {
        "Walk (Varrock East)",
        "Walk (Varrock West)",
        "Varrock Teleport (Varrock East)",
        "Varrock Teleport (Varrock West)",
        "Camelot Teleport (Seers Village)",
        "Camelot Teleport (Catherby)",
        "Ring of dueling (Al Kharid PvP Arena)",
        "Ring of dueling (Castle Wars Arena)",
        "Ring of dueling (Ferox Enclave)",
        "Amulet of glory (Edgeville)",
        "Amulet of glory (Draynor Village)",
        "Amulet of glory (Al Kharid)",},optionType = OptionType.STRING)
@ScriptConfiguration(name = "Chop logs?", description = "Chop down logs at Lumberyard? (Must have axe equipped)",optionType = OptionType.BOOLEAN)
@ScriptConfiguration(name = "Upgrade axe?", description = "Progressively upgrade to higher tier? (Must have all required axes in bank)",optionType = OptionType.BOOLEAN)
@ScriptConfiguration(name = "Continue walking without travel teleport?", description = "Continue when travel teleport run out? (Walks to Varrock East)",optionType = OptionType.BOOLEAN)
@ScriptConfiguration(name = "Continue walking without bank teleport?", description = "Continue when bank teleports run out? (Walks to Varrock East)",optionType = OptionType.BOOLEAN)
//@ScriptConfiguration(name = "Monsters:", description = "List of monsters to train on", defaultValue = "Baby, Young, Gourmet, Earth, Essence, Eclectic, Nature, Magpie, Ninja, Crystal, Dragon, Lucky",optionType = OptionType.STRING)
@ScriptManifest(name="rLumberyardPlanker", description="Processes logs at the Varrock Lumberyard", author = "RickyDactyl", category = ScriptCategory.Combat)
public class Main extends AbstractScript {

    public static void main(String[] args) {
        new ScriptUploader().uploadAndStart("rLumberyardPlanker", "planker", "192.168.1.88:5555", true, false);
    }

    private ArrayList<Task> taskList;

    private String node;

    public static Player local;

    public static String plankOption,travelOption,bankOption, logType;

    public static boolean terminate, chopOption, upgradeOption, continueOption, setup;


    private int getPlankID() {
        switch(plankOption) {
            case "Plank" : {
                return 	960;
            }
            case "Oak plank" : {
                return 8778;
            }
            case "Teak plank" : {
                return 8780;
            }
            case "Mahogany plank" : {
                return 	8782;
            }
        }
        return -1;
    }

    @Override
    public void onStart() {
        terminate = false;
        setup = false;
        plankOption = getOption("Plank type:");
        travelOption = getOption("Travel type:");
        bankOption = getOption("Banking method:");
        chopOption = getOption("Chop logs?");
        upgradeOption = getOption("Upgrade axe?");
        continueOption = getOption("Continue walking without travel teleport?");
        logType = convertLogType(plankOption);
        Paint paint = PaintBuilder.newBuilder()
                .y(350)
                .x(400)
                .addString("Node:", new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return node;
                    }
                })
                .addString("Plank type:", new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return plankOption;
                    }
                })
                .addString("Travel type:", new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return travelOption;
                    }
                })
                .addString("Banking method:", new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return bankOption;
                    }
                })
                .trackInventoryItem(getPlankID(),plankOption)
//                .trackSkill(Skill.Hitpoints, TrackSkillOption.Exp,TrackSkillOption.LevelProgressBar)
//                .trackSkill(currentSkill,TrackSkillOption.Exp,TrackSkillOption.LevelProgressBar)
                .build();
        addPaint(paint);
        local = Players.local();
//        Register tasks
        taskList = new ArrayList<>();
        //Checks if should bank and handles it
        taskList.add(new SetupTask()); // handle settings
        taskList.add(new BankTask()); // handle settings
        taskList.add(new LumberyardTask()); // handle settings
//        taskList.add(new LumberyardTask()); // handle settings
    }

    private String convertLogType(String plankOption) {
        String logType = "Logs";
        switch(plankOption) {
            case "Plank" : {
                return "Logs";
            }
            case "Oak plank" : {
                return "Oak logs";
            }
            case "Teak plank" : {
                return "Teak logs";
            }
            case "Mahogany plank" : {
                return "Mahogany logs";
            }
        }
        return logType;
    }

    public static void hopWorld(boolean members) {
        List<World.Specialty> WORLD_SPECIALITY_FILTER = Arrays.asList(World.Specialty.BOUNTY_HUNTER, World.Specialty.TARGET_WORLD,
                World.Specialty.FRESH_START, World.Specialty.HIGH_RISK, World.Specialty.BETA, World.Specialty.DEAD_MAN,
                World.Specialty.LEAGUE, World.Specialty.PVP_ARENA, World.Specialty.SKILL_REQUIREMENT,
                World.Specialty.SPEEDRUNNING, World.Specialty.TWISTED_LEAGUE, World.Specialty.PVP);
        List<World> newWorlds = Worlds.get(world -> world.getType().equals(members ? World.Type.MEMBERS : World.Type.FREE)  && world.getNumber() != Worlds.current().getNumber() && !WORLD_SPECIALITY_FILTER.contains(world.specialty()));
        World newWorld = newWorlds.get(new Random().nextInt(newWorlds.size()-1));
        if(newWorld.hop()) {
            Condition.wait(() -> Worlds.current() == newWorld, 200, 50);
        }
    }

    @Override
    public void poll() {
        if(terminate) {
            onStop();

        }
//        hopWorld(true);
        for(Task task : taskList) {
            if(task.can()) {
                node = task.getNode();
                task.run();
                break;
            }
        }
    }

    public static void warnAndExit(String message) {
        Notifications.showNotification(message);
//        exit needs to be aded
    }


    @Subscribe
    public void onSkillLevelUpEvent(SkillLevelUpEvent event) {
        Skill skill = event.getSkill();
        int level = event.getNewLevel();
    }

//    @Subscribe
//    public void onRender(RenderEvent r){
//
//    }
}