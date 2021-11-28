package com.cooldrinkscompany.vmcs.factory;

import java.util.List;
import java.util.logging.Logger;

import javax.json.JsonObject;

import com.cooldrinkscompany.vmcs.pojo.Coin;
import com.cooldrinkscompany.vmcs.pojo.Drink;
import com.cooldrinkscompany.vmcs.pojo.ProductDAOImpl;
import com.cooldrinkscompany.vmcs.pojo.VendingMachineSnapshot;

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
        for (JsonObject coin : productDAO.getAllCoins()) {
            LOGGER.info(coin.toString());
            // coin.toString();
        }
        //snapshot.coins
        return snapshot;
    }
}
