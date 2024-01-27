package org.rickydactyl.tasks;

import org.powbot.api.Condition;
import org.powbot.api.rt4.*;
import org.powbot.dax.api.models.RunescapeBank;
import org.rickydactyl.Main;

public class BankTask extends Task {

    private final String[] recommendedGear;

    private final String logType,teleportItem,plankName;

    public BankTask() {
        super("Bank");
        logType = Main.logType;
        String bankingMethod = Main.bankOption;
        String travelItem = Main.travelOption;
        plankName = Main.plankOption;
        teleportItem = bankingMethod.split(" \\(")[0];
        recommendedGear = new String[]{teleportItem, travelItem, "Coins", logType};
    }

    @Override
    public boolean can() {
        return Inventory.emptySlotCount() > 1 || !Inventory.stream().name(logType).first().valid();
    }

    public static String[] teleportItems = {"Coins","Ring of the elements","Ring of dueling (1)","Ring of dueling (2)","Ring of dueling (3)","Ring of dueling (4)","Ring of dueling (5)","Ring of dueling (6)","Ring of dueling (7)","Ring of dueling (8)"
            ,"Amulet of glory (1)","Amulet of glory (2)","Amulet of glory (3)","Amulet of glory (4)","Amulet of glory (5)","Amulet of glory (6)"};

    @Override
    public void run() {
        if(Bank.open() || Condition.wait(Bank::opened, 100, 50)) {
            if(Inventory.stream().name(plankName).first().valid()) {
                Bank.depositAllExcept(teleportItems);
            } else {
                for(String itemName : recommendedGear) {
                    if(!hasItem(itemName)) {
                        int amount = 1;
                        Item targetItem = itemName.equals(teleportItem) ? Bank.stream().nameContains(itemName).first() : Bank.stream().name(itemName).first();
                        if(targetItem.valid()) {
                            if(!itemName.equals(teleportItem)) {
                                amount = targetItem.stackSize();
                            }
                            Bank.withdraw(itemName, amount);
                        } else if(!Equipment.stream().name(itemName).first().valid() && !Inventory.stream().name(itemName).first().valid()){
                            Main.warnAndExit("Ran out of " + itemName);
                            Main.terminate = true;
                            return;
                        }
                    }
                }
            }
        } else {
            Movement.moveToBank(getBankOption());
        }
    }

    private boolean hasItem(String itemName) {
        if(itemName.contains("of")) {
            return Inventory.stream().nameContains(itemName).first().valid() || Equipment.stream().nameContains(itemName).first().valid();
        } else {
            return Inventory.stream().name(itemName).first().valid() || Equipment.stream().name(itemName).first().valid();
        }
    }

    private RunescapeBank getBankOption() {
        String bankString = Main.bankOption.split(" \\(")[1].replace(")","").replaceAll(" ","_").toUpperCase();
        if (bankString.equals("SEERS_VILLAGE")) {
            return RunescapeBank.CAMELOT;
        } else {
            for (RunescapeBank bank : RunescapeBank.values()) {
                if (bank.name().equals(bankString)) {
                    System.out.println("Found bank option:" + bankString + " matches -> " + bank.name());
                    return bank;
                }
                System.out.println("NotFound:" + bank.name());
            }
        }
        return null;
    }
}
