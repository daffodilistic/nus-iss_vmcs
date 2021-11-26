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
    void queryCoinQty() {
        when(mockDao.getCoinQuantity("50c")).thenReturn("10");
        int actualVal = ControllerManageCoin.queryCoinQty(mockDao, "50c");
        assertEquals(10, actualVal);

        when(mockDao.getCoinQuantity("bitcoin")).thenReturn("NA");
        actualVal = ControllerManageCoin.queryCoinQty(mockDao, "bitcoin");
        assertEquals(Integer.MAX_VALUE, actualVal);

    }

    @Test
    void setCoinQty() {
    }
}