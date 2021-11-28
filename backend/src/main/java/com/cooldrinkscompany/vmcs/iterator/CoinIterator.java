package com.cooldrinkscompany.vmcs.iterator;

import com.cooldrinkscompany.vmcs.pojo.ProductDAOImpl;

import javax.json.JsonObject;
import java.util.List;
import java.util.logging.Logger;

public class CoinIterator implements AbstractProductIterator{
    private final Logger LOGGER = Logger.getLogger(CoinIterator.class.getName());
    private final List<JsonObject> allCoins;
    JsonObject currObj;
    int head;

    public CoinIterator(ProductDAOImpl dao) {
        this.allCoins = dao.getAllCoins();
        LOGGER.info("Getting " + allCoins.size() + " Coins");
    }

    @Override
    public JsonObject first(){
        if (allCoins.size() > 0){
            this.currObj = allCoins.get(0);
            this.head = 0;
            return currObj;
        }else{
            return null;
        }
    }

    public JsonObject next(){
        this.head += 1;
        this.currObj = allCoins.get(this.head);
        return this.currObj;
    }

    public boolean isDone() {
        return this.head == allCoins.size() - 1;
    }

}
