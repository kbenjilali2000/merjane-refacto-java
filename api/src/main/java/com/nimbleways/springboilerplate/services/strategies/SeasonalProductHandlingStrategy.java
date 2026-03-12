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
public class SeasonalProductHandlingStrategy implements ProductHandlingStrategy {

    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    @Override
    public boolean supports(ProductType productType) {
        return productType == ProductType.SEASONAL;
    }

    @Override
    public void handle(Product product) {
        if (isInSeason(product) && isAvailable(product)) {
            decreaseAvailability(product);
            productRepository.save(product);
            return;
        }

        if (leadTimeExceedsSeasonEnd(product)) {
            notificationService.sendOutOfStockNotification(product.getName());
            product.setAvailable(0);
            productRepository.save(product);
            return;
        }

        if (seasonHasNotStartedYet(product)) {
            notificationService.sendOutOfStockNotification(product.getName());
            productRepository.save(product);
            return;
        }

        if (hasLeadTime(product)) {
            notificationService.sendDelayNotification(product.getLeadTime(), product.getName());
            productRepository.save(product);
        }
    }

    private boolean isInSeason(Product product) {
        LocalDate today = LocalDate.now();
        return today.isAfter(product.getSeasonStartDate()) && today.isBefore(product.getSeasonEndDate());
    }

    private boolean isAvailable(Product product) {
        return product.getAvailable() != null && product.getAvailable() > 0;
    }

    private boolean leadTimeExceedsSeasonEnd(Product product) {
        return hasLeadTime(product)
                && LocalDate.now().plusDays(product.getLeadTime()).isAfter(product.getSeasonEndDate());
    }

    private boolean seasonHasNotStartedYet(Product product) {
        return product.getSeasonStartDate().isAfter(LocalDate.now());
    }

    private boolean hasLeadTime(Product product) {
        return product.getLeadTime() != null && product.getLeadTime() > 0;
    }

    private void decreaseAvailability(Product product) {
        product.setAvailable(product.getAvailable() - 1);
    }
}