package com.nimbleways.springboilerplate.services;


import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.enums.ProductType;
import com.nimbleways.springboilerplate.exceptions.OrderNotFoundException;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.services.strategies.ProductHandlingStrategy;
import com.nimbleways.springboilerplate.services.strategies.ProductHandlingStrategyResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderProcessingService {

    private final OrderRepository orderRepository;
    private final ProductHandlingStrategyResolver strategyResolver;

    public ProcessOrderResponse processOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));

        for (Product product : order.getItems()) {
            ProductType productType = product.getType();
            ProductHandlingStrategy strategy = strategyResolver.resolve(productType);
            strategy.handle(product);
        }

        return new ProcessOrderResponse(order.getId());
    }
}
