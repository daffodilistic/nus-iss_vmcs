package com.cooldrinkscompany.vmcs.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.cooldrinkscompany.vmcs.service.CoinsService.InsertCoin;

public final class Session {
    public transient int id;
    public UUID sessionId;
    public ArrayList<InsertCoin> coins;

    public Session(InsertCoin coin) {
        this.id = -1;
        this.sessionId = UUID.randomUUID();
        coin.quantity += 1;
        this.coins = new ArrayList<InsertCoin>();
        this.coins.add(coin);
    }

    public void save() {
    }

    public void addCoin(InsertCoin coin) {
        for (InsertCoin c : coins) {
            if (c.value == coin.value) {
                c.quantity += 1;
                return;
            }
        }
        coins.add(coin);
    }
}
