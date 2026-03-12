package com.nimbleways.springboilerplate.services.strategies;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.enums.ProductType;

public interface ProductHandlingStrategy {

    boolean supports(ProductType productType);

    void handle(Product product);
}

