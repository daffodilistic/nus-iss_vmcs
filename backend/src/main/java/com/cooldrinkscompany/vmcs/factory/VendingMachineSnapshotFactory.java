package com.cooldrinkscompany.vmcs.factory;

import java.util.List;
import java.util.logging.Logger;

import javax.json.JsonObject;

import com.cooldrinkscompany.vmcs.pojo.Coin;
import com.cooldrinkscompany.vmcs.pojo.Drink;
import com.cooldrinkscompany.vmcs.pojo.ProductDAOImpl;
import com.cooldrinkscompany.vmcs.pojo.VendingMachineSnapshot;
import com.google.gson.Gson;

public final class VendingMachineSnapshotFactory {
    private static final Logger LOGGER = Logger.getLogger(ProductDAOImpl.class.getName());
    private static VendingMachineSnapshotFactory INSTANCE;

    private ProductDAOImpl productDAO;

    public static VendingMachineSnapshotFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new VendingMachineSnapshotFactory(null);
        }
        return INSTANCE;
    }

    public VendingMachineSnapshotFactory(ProductDAOImpl dao) {
        if (dao == null) {
            throw new IllegalArgumentException("ProductDAOImpl cannot be null");
        }
        this.productDAO = dao;
        INSTANCE = this;
    }

    public VendingMachineSnapshot getSnapshot() {
        VendingMachineSnapshot snapshot = new VendingMachineSnapshot();
        snapshot.isDoorLocked = this.productDAO.getStatus("isUnlocked");
        Gson gson = new Gson();
        for (JsonObject coin : productDAO.getAllCoins()) {
            snapshot.coins.add(gson.fromJson(coin.toString(), Coin.class));
        }
        for (JsonObject drink : productDAO.getAllDrinks()) {
            snapshot.drinks.add(gson.fromJson(drink.toString(), Drink.class));
        }
        return snapshot;
    }
}
