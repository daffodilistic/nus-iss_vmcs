package com.cooldrinkscompany.vmcs.iterator;

import io.helidon.dbclient.DbClient;

public class CoinIterator implements AbstractProductIterator{
    private DbClient dbClient;

    public CoinIterator(DbClient dbClient) {
        this.dbClient = dbClient;
    }

    public Object first(){
        return null;
    }

    public Object next(){
        return null;
    };

    public boolean isDone() {
        return true;
    };
}
