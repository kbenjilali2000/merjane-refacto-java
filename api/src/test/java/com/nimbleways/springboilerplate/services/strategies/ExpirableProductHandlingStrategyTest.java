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
import static org.mockito.Mockito.verifyNoInteractions;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ExpirableProductHandlingStrategyTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ExpirableProductHandlingStrategy strategy;

    @Test
    void shouldSellProductWhenNotExpired() {

        Product product = new Product(null, 10, 5, ProductType.EXPIRABLE, "Cocoa", LocalDate.now().plusDays(10), null, null);

        strategy.handle(product);

        assertEquals(4, product.getAvailable());

        verify(productRepository).save(product);
        verifyNoInteractions(notificationService);
    }

    @Test
    void shouldNotifyExpirationWhenExpired() {

        Product product = new Product(null, 10, 5, ProductType.EXPIRABLE, "Coffee", LocalDate.now().minusDays(2), null, null);

        strategy.handle(product);

        assertEquals(0, product.getAvailable());

        verify(notificationService).sendExpirationNotification("Coffee", product.getExpiryDate());

        verify(productRepository).save(product);
    }
}