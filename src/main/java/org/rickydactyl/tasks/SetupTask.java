package org.rickydactyl.tasks;

import org.powbot.api.Condition;
import org.powbot.api.rt4.Bank;
import org.powbot.api.rt4.Equipment;
import org.powbot.api.rt4.Inventory;
import org.powbot.api.rt4.Movement;

public class SetupTask extends Task {

    private boolean setup;

    public SetupTask() {
        super("Setting up");
        setup = false;
    }

    @Override
    public boolean can() {
        return !setup;
    }

    @Override
    public void run() {
        if(Bank.open() || Condition.wait(Bank::opened, 300, 10)) {
            if(!Inventory.isEmpty() && Condition.wait(Bank::depositInventory, 300, 10)) {
                Condition.wait(Inventory::isEmpty, 300, 10);
            } else if(!Equipment.stream().isEmpty() && Condition.wait(Bank::depositEquipment, 300, 10)) {
                Condition.wait(() -> Equipment.stream().isEmpty(), 300, 10);
            } else {
                setup = true;
            }
        } else {
            Movement.moveToBank();
        }
    }
}
