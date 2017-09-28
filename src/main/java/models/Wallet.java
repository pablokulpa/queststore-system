package models;

import java.util.ArrayList;

public class Wallet {

    private Integer money;
    private Integer experience;
    private ArrayList<BasicItem> artifactList;

    Wallet() {

        this.money = 0;
        this.experience = 0;
        artifactList = new ArrayList<>();
    }

    void add(Integer amount) {

        this.money += amount;
    }

    boolean substract(Integer amount) {

        if (this.money >= amount) {
            this.money -= amount;
            return true;
        }
        return false;
    }

    public Integer getBalance() {

        return money;
    }

    void add(BasicItem item) {

        this.artifactList.add(item);
    }
}