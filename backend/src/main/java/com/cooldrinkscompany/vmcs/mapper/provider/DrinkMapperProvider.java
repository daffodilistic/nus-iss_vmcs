package com.cooldrinkscompany.vmcs.mapper.provider;

import java.util.Optional;

import javax.annotation.Priority;

import com.cooldrinkscompany.vmcs.mapper.DrinkMapper;
import com.cooldrinkscompany.vmcs.pojo.Drink;

import io.helidon.dbclient.DbMapper;
import io.helidon.dbclient.spi.DbMapperProvider;


@Priority(1000)
public class DrinkMapperProvider implements DbMapperProvider {
    private static final DrinkMapper MAPPER = new DrinkMapper();

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<DbMapper<T>> mapper(Class<T> type) {
        if (type.equals(Drink.class)) {
            return Optional.of((DbMapper<T>) MAPPER);
        }
        return Optional.empty();
    }
}
