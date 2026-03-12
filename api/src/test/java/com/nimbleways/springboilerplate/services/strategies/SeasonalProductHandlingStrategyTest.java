package com.nimbleways.springboilerplate.services.strategies;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.enums.ProductType;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class SeasonalProductHandlingStrategyTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private SeasonalProductHandlingStrategy strategy;

    @Test
    void shouldSellProductWhenInSeasonAndAvailable() {

        Product product = new Product(null, 5, 4, ProductType.SEASONAL, "Orange", null,
                LocalDate.now().minusDays(2), LocalDate.now().plusDays(10));

        strategy.handle(product);

        assertEquals(3, product.getAvailable());

        verify(productRepository).save(product);
    }

    @Test
    void shouldNotifyOutOfStockWhenSeasonNotStarted() {

        Product product = new Product(null, 5, 0, ProductType.SEASONAL, "Grapes", null,
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(30));

        strategy.handle(product);

        verify(notificationService).sendOutOfStockNotification("Grapes");

        verify(productRepository).save(product);
    }
}