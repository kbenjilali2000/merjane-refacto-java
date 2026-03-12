package com.nimbleways.springboilerplate.services.strategies;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.enums.ProductType;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ExpirableProductHandlingStrategy implements ProductHandlingStrategy {

    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    @Override
    public boolean supports(ProductType productType) {
        return productType == ProductType.EXPIRABLE;
    }

    @Override
    public void handle(Product product) {
        if (isSellable(product)) {
            decreaseAvailability(product);
            productRepository.save(product);
            return;
        }

        notificationService.sendExpirationNotification(product.getName(), product.getExpiryDate());
        product.setAvailable(0);
        productRepository.save(product);
    }

    private boolean isSellable(Product product) {
        return isAvailable(product) && isNotExpired(product);
    }

    private boolean isAvailable(Product product) {
        return product.getAvailable() != null && product.getAvailable() > 0;
    }

    private boolean isNotExpired(Product product) {
        return product.getExpiryDate().isAfter(LocalDate.now());
    }

    private void decreaseAvailability(Product product) {
        product.setAvailable(product.getAvailable() - 1);
    }
}
