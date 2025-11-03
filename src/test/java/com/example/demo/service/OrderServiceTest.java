package com.example.demo.service;

import com.example.demo.dao.OrderDAO;
import com.example.demo.entity.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderDAO orderDAO;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getOrder_WhenOrderExists_ShouldReturnOrder() {
        // Given
        String orderId = "12345";
        Order expectedOrder = new Order();
        expectedOrder.setId(orderId);
        expectedOrder.setAmount(100.0);

        when(orderDAO.getOrder(orderId)).thenReturn(expectedOrder);

        // When
        Order actualOrder = orderService.getOrder(orderId);

        // Then
        assertNotNull(actualOrder);
        assertEquals(orderId, actualOrder.getId());
        assertEquals(100.0, actualOrder.getAmount());
        verify(orderDAO, times(1)).getOrder(orderId);
    }

    @Test
    void getOrder_WhenOrderNotExists_ShouldReturnNull() {
        // Given
        String orderId = "nonexistent";

        when(orderDAO.getOrder(orderId)).thenReturn(null);

        // When
        Order actualOrder = orderService.getOrder(orderId);

        // Then
        assertNull(actualOrder);
        verify(orderDAO, times(1)).getOrder(orderId);
    }
}