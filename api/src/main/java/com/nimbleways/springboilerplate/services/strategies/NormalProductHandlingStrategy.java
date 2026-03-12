package com.nimbleways.springboilerplate.services.strategies;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.enums.ProductType;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NormalProductHandlingStrategy implements ProductHandlingStrategy {

    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    @Override
    public boolean supports(ProductType productType) {
        return productType == ProductType.NORMAL;
    }

    @Override
    public void handle(Product product) {
        if (isAvailable(product)) {
            decreaseAvailability(product);
            productRepository.save(product);
            return;
        }

        if (hasLeadTime(product)) {
            notificationService.sendDelayNotification(product.getLeadTime(), product.getName());
            productRepository.save(product);
        }
    }

    private boolean isAvailable(Product product) {
        return product.getAvailable() != null && product.getAvailable() > 0;
    }

    private boolean hasLeadTime(Product product) {
        return product.getLeadTime() != null && product.getLeadTime() > 0;
    }

    private void decreaseAvailability(Product product) {
        product.setAvailable(product.getAvailable() - 1);
    }
}
