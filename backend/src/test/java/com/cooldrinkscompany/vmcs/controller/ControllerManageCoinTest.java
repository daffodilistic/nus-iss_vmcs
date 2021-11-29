package com.cooldrinkscompany.vmcs.controller;

import com.cooldrinkscompany.vmcs.pojo.ProductDAOImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ControllerManageCoinTest {

    @Mock
    ProductDAOImpl mockDao = mock(ProductDAOImpl.class);;

    @Test
    void testQueryCoinQty_1() {
        when(mockDao.getCoinQuantity("50c")).thenReturn("10");
        int actualVal = ControllerManageCoin.queryCoinQty(mockDao, "50c");
        assertEquals(10, actualVal);
    }

    @Test
    void testQueryCoinQty_2(){
        when(mockDao.getCoinQuantity("bitcoin")).thenReturn("NA");
        int actualVal = ControllerManageCoin.queryCoinQty(mockDao, "bitcoin");
        assertEquals(Integer.MAX_VALUE, actualVal);
    }

    @Test
    void testQueryCoinPrice_1() {
        when(mockDao.getCoinPrice("$1")).thenReturn("100");
        double actualVal = ControllerManageCoin.queryCoinPrice(mockDao, "$1");
        assertEquals(100, actualVal);
    }

    @Test
    void testQueryCoinPrice_2() {
        when(mockDao.getCoinPrice("bitcoin")).thenReturn("NA");
        double actualVal = ControllerManageCoin.queryCoinPrice(mockDao, "bitcoin");
        assertEquals(Double.MAX_VALUE, actualVal);
    }

    @Test
    void testSetCoinQty_1() {
        when(mockDao.setCoinQuantity("20c", "-1")).thenReturn("-1");
        String actualVal = ControllerManageCoin.setCoinQty(mockDao, "20c", "-1");
        assertEquals("Quantity cannot less than 0 or greater than 40", actualVal);

        when(mockDao.setCoinQuantity("20c", "41")).thenReturn("41");
        String actualVal_2 = ControllerManageCoin.setCoinQty(mockDao, "20c", "41");
        assertEquals("Quantity cannot less than 0 or greater than 40", actualVal_2);
    }
    
    @Test
    void testSetCoinQty_2() {
        when(mockDao.setCoinQuantity("20c", "0.5")).thenReturn("0.5");
        String actualVal = ControllerManageCoin.setCoinQty(mockDao, "20c", "0.5");
        assertEquals("Failed. Input qty cannot convert to integer.", actualVal);
    }

    @Test
    void testSetCoinQty_3() {
        when(mockDao.setCoinQuantity("50c", "0")).thenReturn("0");
        String actualVal = ControllerManageCoin.setCoinQty(mockDao, "50c", "0");
        assertEquals("0", actualVal);
    }

    @Test
    void testSetCoinQty_4() {
        when(mockDao.setCoinQuantity("10c", "40")).thenReturn("40");
        String actualVal = ControllerManageCoin.setCoinQty(mockDao, "10c", "40");
        assertEquals("40", actualVal);
    }

}