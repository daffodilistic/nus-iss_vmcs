package com.cooldrinkscompany.vmcs.controller;

import com.cooldrinkscompany.vmcs.pojo.ProductDAOImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ControllerSetSystemStatusTest {

    @Mock
    ProductDAOImpl mockDao = mock(ProductDAOImpl.class);;

    @Test
    void testGetStatus() {
        when(mockDao.getStatus("isDoorUnlucked")).thenReturn(true);
        boolean actualVal = ControllerSetSystemStatus.getStatus(mockDao, "isDoorUnlucked");
        assertEquals(true, actualVal);

        when(mockDao.getStatus("isDoorUnlucked")).thenReturn(false);
        boolean actualVal_2 = ControllerSetSystemStatus.getStatus(mockDao, "isDoorUnlucked");
        assertEquals(false, actualVal_2);

    }

    
    @Test
    void testSetStatus() {
        when(mockDao.setStatus("isDoorUnlucked", true)).thenReturn("Success");
        String actualVal = ControllerSetSystemStatus.setStatus(mockDao, "isDoorUnlucked", true);
        assertEquals("Success", actualVal);

        when(mockDao.setStatus("isLoggedIn", true)).thenReturn("Success");
        String actualVal_2 = ControllerSetSystemStatus.setStatus(mockDao, "isLoggedIn", true);
        assertEquals("Success", actualVal_2);

    }
}
