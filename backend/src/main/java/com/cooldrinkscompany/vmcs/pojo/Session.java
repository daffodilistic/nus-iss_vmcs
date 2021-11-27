package com.cooldrinkscompany.vmcs.pojo;

import java.util.List;
import java.util.UUID;

import com.cooldrinkscompany.vmcs.service.CoinsService.InsertCoin;

public final class Session {
    public int id;
    public UUID sessionId;
    public List<InsertCoin> coins;

    public Session(int id, UUID sessionId, List<InsertCoin> coins) {
        this.id = id;
        this.sessionId = sessionId;
        this.coins = coins;
    }

    public Session(InsertCoin coin) {
        this.id = -1;
        this.sessionId = UUID.randomUUID();
        this.coins = List.of(coin);
    }

    public void save() {
    }
}
