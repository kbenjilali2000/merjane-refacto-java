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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class NormalProductHandlingStrategyTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NormalProductHandlingStrategy strategy;

    @Test
    void shouldDecreaseAvailabilityWhenProductIsAvailable() {

        Product product = new Product(null, 10, 5, ProductType.NORMAL, "USB Cable", null, null, null);

        strategy.handle(product);

        assertEquals(4, product.getAvailable());

        verify(productRepository).save(product);
        verifyNoInteractions(notificationService);
    }

    @Test
    void shouldNotifyDelayWhenOutOfStock() {

        Product product = new Product(null, 15, 0, ProductType.NORMAL, "USB Dongle", null, null, null);

        strategy.handle(product);

        verify(notificationService).sendDelayNotification(15, "USB Dongle");

        verify(productRepository).save(product);
    }
}