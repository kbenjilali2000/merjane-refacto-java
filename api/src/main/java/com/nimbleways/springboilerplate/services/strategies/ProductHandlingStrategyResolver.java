package com.nimbleways.springboilerplate.services.strategies;

import com.nimbleways.springboilerplate.enums.ProductType;
import com.nimbleways.springboilerplate.exceptions.UnsupportedProductTypeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductHandlingStrategyResolver {

    private final List<ProductHandlingStrategy> strategies;

    public ProductHandlingStrategy resolve(ProductType productType) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(productType))
                .findFirst()
                .orElseThrow(() ->
                        new UnsupportedProductTypeException("No strategy found for type: " + productType));
    }
}