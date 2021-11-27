package com.cooldrinkscompany.vmcs.controller;

import com.cooldrinkscompany.vmcs.pojo.ProductDAOImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ControllerManageDrinkTest {

    @Mock
    ProductDAOImpl mockDao = mock(ProductDAOImpl.class);;

    @Test
    void testQueryDrinkPrice_1() {
        when(mockDao.getDrinkPrice("Fanta")).thenReturn("80");
        double actualVal = ControllerManageDrink.queryDrinkPrice(mockDao, "Fanta");
        assertEquals(80, actualVal);
    }

    @Test
    void testQueryDrinkPrice_2() {
        when(mockDao.getDrinkPrice("KFC")).thenReturn("NA");
        double actualVal = ControllerManageDrink.queryDrinkPrice(mockDao, "KFC");
        assertEquals(Double.MAX_VALUE, actualVal);
    }

    @Test
    void testQueryDrinkQty_1() {
        when(mockDao.getDrinkQuantity("Fanta")).thenReturn("10");
        int actualVal = ControllerManageDrink.queryDrinkQty(mockDao, "Fanta");
        assertEquals(10, actualVal);
    }

    @Test
    void testQueryDrinkQty_2() {
        when(mockDao.getDrinkQuantity("KFC")).thenReturn("NA");
        int actualVal = ControllerManageDrink.queryDrinkQty(mockDao, "KFC");
        assertEquals(Integer.MAX_VALUE, actualVal);
    }

    @Test
    void testSetDrinkPrice() {
        when(mockDao.setDrinkPrice("Fanta", "95")).thenReturn("95");
        String actualVal = ControllerManageDrink.setDrinkPrice(mockDao, "Fanta", "95");
        assertEquals("95", actualVal);

    }

    @Test
    void testSetDrinkQty() {
        when(mockDao.setDrinkQuantity("Fanta", "5")).thenReturn("5");
        String actualVal = ControllerManageDrink.setDrinkQty(mockDao, "Fanta", "5");
        assertEquals("5", actualVal);
    }
}
