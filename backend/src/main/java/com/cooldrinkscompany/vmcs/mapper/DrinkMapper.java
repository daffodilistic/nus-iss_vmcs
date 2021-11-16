package com.cooldrinkscompany.vmcs.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cooldrinkscompany.vmcs.pojo.Drink;

import io.helidon.dbclient.DbColumn;
import io.helidon.dbclient.DbMapper;
import io.helidon.dbclient.DbRow;

public class DrinkMapper implements DbMapper<Drink> {
 
    @Override
    public Drink read(DbRow row) {
        DbColumn id = row.column("id");
        DbColumn name = row.column("name");
        DbColumn quantity = row.column("quantity");
        DbColumn price = row.column("price");

        return new Drink(
            id.as(Integer.class),
            name.as(String.class),
            quantity.as(Integer.class),
            price.as(Double.class)
        );
    }

    @Override
    public Map<String, Object> toNamedParameters(Drink value) {
        Map<String, Object> map = new HashMap<>(4);
        map.put("id", value.getName());
        map.put("name", value.getName());
        map.put("quantity", value.getQuantity());
        map.put("price", value.getPrice());
        return map;
    }

    @Override
    public List<Object> toIndexedParameters(Drink value) {
        List<Object> list = new ArrayList<>(4);
        list.add(value.getId());
        list.add(value.getName());
        list.add(value.getQuantity());
        list.add(value.getPrice());
        return list;
    }
}
