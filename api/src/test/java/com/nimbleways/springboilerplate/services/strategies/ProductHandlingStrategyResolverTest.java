package com.nimbleways.springboilerplate.services.strategies;

import com.nimbleways.springboilerplate.enums.ProductType;
import com.nimbleways.springboilerplate.exceptions.UnsupportedProductTypeException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductHandlingStrategyResolverTest {

    @Test
    void shouldReturnMatchingStrategy() {

        ProductHandlingStrategy normalStrategy = mock(ProductHandlingStrategy.class);
        ProductHandlingStrategy seasonalStrategy = mock(ProductHandlingStrategy.class);

        when(normalStrategy.supports(ProductType.NORMAL)).thenReturn(true);
        when(seasonalStrategy.supports(ProductType.NORMAL)).thenReturn(false);

        ProductHandlingStrategyResolver resolver = new ProductHandlingStrategyResolver(List.of(normalStrategy, seasonalStrategy));

        ProductHandlingStrategy result = resolver.resolve(ProductType.NORMAL);

        assertEquals(normalStrategy, result);
    }

    @Test
    void shouldThrowExceptionWhenNoStrategyMatches() {

        ProductHandlingStrategy strategy1 = mock(ProductHandlingStrategy.class);
        ProductHandlingStrategy strategy2 = mock(ProductHandlingStrategy.class);

        when(strategy1.supports(ProductType.SEASONAL)).thenReturn(false);
        when(strategy2.supports(ProductType.SEASONAL)).thenReturn(false);

        ProductHandlingStrategyResolver resolver = new ProductHandlingStrategyResolver(List.of(strategy1, strategy2));

        assertThrows(UnsupportedProductTypeException.class, () -> resolver.resolve(ProductType.SEASONAL));
    }
}