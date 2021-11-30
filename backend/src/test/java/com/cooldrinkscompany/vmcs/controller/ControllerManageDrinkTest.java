package com.cooldrinkscompany.vmcs.controller;

import com.cooldrinkscompany.vmcs.pojo.ProductDAOImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

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
    void testSetDrinkQty_1() {
        when(mockDao.setDrinkQuantity("Fanta", "21")).thenReturn("21");
        String actualVal = ControllerManageDrink.setDrinkQty(mockDao, "Fanta", "21");
        assertEquals("Failed. Quantity cannot less than 0 or greater than 20", actualVal);

        when(mockDao.setDrinkQuantity("Fanta", "-1")).thenReturn("-1");
        String actualVal_2 = ControllerManageDrink.setDrinkQty(mockDao, "Fanta", "-1");
        assertEquals("Failed. Quantity cannot less than 0 or greater than 20", actualVal_2);
    }

    @Test
    void testSetDrinkQty_2() {
        when(mockDao.setDrinkQuantity("Fanta", "0.1")).thenReturn("0.1");
        String actualVal = ControllerManageDrink.setDrinkQty(mockDao, "Fanta", "0.1");
        assertEquals("Failed. Input qty cannot convert to integer.", actualVal);
    }

    @Test
    void testSetDrinkQty_3() {
        when(mockDao.setDrinkQuantity("Fanta", "20")).thenReturn("20");
        String actualVal = ControllerManageDrink.setDrinkQty(mockDao, "Fanta", "20");
        assertEquals("20", actualVal);
    }

    @Test
    void testSetDrinkQty_4() {
        when(mockDao.setDrinkQuantity("Fanta", "0")).thenReturn("0");
        String actualVal = ControllerManageDrink.setDrinkQty(mockDao, "Fanta", "0");
        assertEquals("0", actualVal);
    }
}
