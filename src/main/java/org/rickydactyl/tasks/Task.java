package org.rickydactyl.tasks;


public class Task {

    private final String node;
    public Task(String node) {
        this.node = node;
    }

    public boolean can() {
        return false;
    }

    public void run() {

    }

    public String getNode() {
        return node;
    }

}
