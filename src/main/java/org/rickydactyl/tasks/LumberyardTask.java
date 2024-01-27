package org.rickydactyl.tasks;

import org.powbot.api.Area;
import org.powbot.api.Condition;
import org.powbot.api.Tile;
import org.powbot.api.rt4.*;
import org.powbot.mobile.input.Keyboard;
import org.rickydactyl.Main;

import java.util.Arrays;

public class LumberyardTask extends Task {

    private final Area lumberYardArea, surroundingArea, regionArea;
    private final Player local;
    private final String travelOption, logType, keyboardInput;
    private final boolean chopOption;

    //check inventory -> bank or go to lumberyard
    //check area -> talk to npc or chop trees

    public LumberyardTask() {
        super("Plank");
        local = Main.local;
        travelOption = Main.travelOption;
        chopOption = Main.chopOption;
        logType = Main.logType;
        keyboardInput = getKeyboardOption();
        regionArea = new Area(new Tile(3271, 3498), new Tile(3312, 3458));
        lumberYardArea = new Area(new Tile(3301, 3491), new Tile(3304, 3485));
        surroundingArea = new Area(new Tile(3309, 3490), new Tile(3288, 3462));
    }

    @Override
    public boolean can() {
        return surroundingArea.contains(local) || Inventory.emptySlotCount() <= 1;
    }

    @Override
    public void run() {
        if(Bank.opened()) {
            Bank.close();
        } else if(regionArea.contains(local)) {
            if(chopOption && !Inventory.isFull()) {
                //Chop trees
            } else if(lumberYardArea.contains(local)){
                //Speak to guy
                Npc operator = Npcs.stream().within(10).name("Sawmill operator").first();
                if(operator.valid() && Condition.wait(() -> operator.interact("Buy-plank"), 200, 50)) {
                    Condition.wait(() ->  !local.inMotion(),601,7);
                    if(!local.inMotion() && Condition.wait(() -> Keyboard.INSTANCE.type(keyboardInput),200,50)) {
                        Condition.wait(() -> !Inventory.stream().name(logType).first().valid(),100,50);
                    }
                }
            } else {
                Movement.walkTo(lumberYardArea.getRandomTile());
            }
        } else if(Arrays.asList(BankTask.teleportItems).contains(travelOption)) {
            Item item = Inventory.stream().name("Ring of the elements").first();
            if(item.valid() && Condition.wait(() -> item.interact("Wear"), 200, 50)) {
                Condition.wait(() -> Equipment.stream().name("Ring of the elements").first().valid(), 200, 50);
            } else {
                Item equipItem = Equipment.stream().name("Ring of the elements").first();
                if(equipItem.valid() && Condition.wait(() -> equipItem.interact("Earth Altar"), 200, 50)) {
                    Condition.wait(() -> surroundingArea.contains(local), 200, 50);
                }
            }
        } else {
            System.out.println("Walk");
            Movement.moveTo(surroundingArea.getRandomTile());
        }
    }

    private String getKeyboardOption() {
        String key = "1";
        switch(logType) {
            case "Oak logs" : {
                key = "2";
                break;
            }
            case "Teak logs" : {
                key = "3";
                break;
            }
            case "Mahogany logs" : {
                key = "4";
                break;
            }
        }
        return key;
    }
}
