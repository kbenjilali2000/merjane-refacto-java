package com.nimbleways.springboilerplate.services;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.enums.ProductType;
import com.nimbleways.springboilerplate.exceptions.OrderNotFoundException;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.services.strategies.ProductHandlingStrategy;
import com.nimbleways.springboilerplate.services.strategies.ProductHandlingStrategyResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderProcessingServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductHandlingStrategyResolver strategyResolver;

    @Mock
    private ProductHandlingStrategy strategy;

    @InjectMocks
    private OrderProcessingService orderProcessingService;

    @Test
    void shouldProcessOrderAndReturnResponse() {
        // GIVEN
        Product product = new Product(null, 10, 5, ProductType.NORMAL, "USB Cable", null, null, null);

        Order order = new Order();
        order.setId(1L);
        order.setItems(Set.of(product));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(strategyResolver.resolve(ProductType.NORMAL)).thenReturn(strategy);

        // WHEN
        ProcessOrderResponse response = orderProcessingService.processOrder(1L);

        // THEN
        assertEquals(1L, response.id());
        verify(strategyResolver).resolve(ProductType.NORMAL);
        verify(strategy).handle(product);
    }

    @Test
    void shouldCallStrategyForEachProductInOrder() {
        // GIVEN
        Product normalProduct = new Product(null, 10, 5, ProductType.NORMAL, "USB Cable", null, null, null);

        Product seasonalProduct = new Product(null, 5, 2, ProductType.SEASONAL, "Watermelon", null, null, null);

        Order order = new Order();
        order.setId(2L);
        order.setItems(Set.of(normalProduct, seasonalProduct));

        ProductHandlingStrategy normalStrategy = org.mockito.Mockito.mock(ProductHandlingStrategy.class);
        ProductHandlingStrategy seasonalStrategy = org.mockito.Mockito.mock(ProductHandlingStrategy.class);

        when(orderRepository.findById(2L)).thenReturn(Optional.of(order));
        when(strategyResolver.resolve(ProductType.NORMAL)).thenReturn(normalStrategy);
        when(strategyResolver.resolve(ProductType.SEASONAL)).thenReturn(seasonalStrategy);

        // WHEN
        ProcessOrderResponse response = orderProcessingService.processOrder(2L);

        // THEN
        assertEquals(2L, response.id());
        verify(strategyResolver).resolve(ProductType.NORMAL);
        verify(strategyResolver).resolve(ProductType.SEASONAL);
        verify(normalStrategy).handle(normalProduct);
        verify(seasonalStrategy).handle(seasonalProduct);
    }

    @Test
    void shouldThrowExceptionWhenOrderIsNotFound() {
        // GIVEN
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(OrderNotFoundException.class, () -> orderProcessingService.processOrder(99L));
    }
}