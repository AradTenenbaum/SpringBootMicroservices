package com.aradev.orderservice.service;

import com.aradev.orderservice.dto.OrderLineItemDto;
import com.aradev.orderservice.dto.OrderRequest;
import com.aradev.orderservice.dto.ProductInStockRequest;
import com.aradev.orderservice.dto.ProductInStockResponse;
import com.aradev.orderservice.model.Order;
import com.aradev.orderservice.model.OrderLineItem;
import com.aradev.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${inventory.service.url}")
    private String InventoryServiceUrl;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItem> orderLineItemList = orderRequest.getOrderLineItemDtos()
                .stream()
                .map(this::mapFromDto)
                .collect(Collectors.toList());
        order.setOrderLineItemsList(orderLineItemList);

        List<ProductInStockResponse> result = webClientBuilder.build().post()
                .uri(InventoryServiceUrl + "/api/inventory")
                .bodyValue(orderLineItemList
                        .stream()
                        .map(orderLineItem ->
                                new ProductInStockRequest(
                                        orderLineItem.getSkuCode(),
                                        orderLineItem.getQuantity()
                                ))
                        .collect(Collectors.toList()))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ProductInStockResponse>>() {
                }).block();

        for (int i = 0; i < result.size(); i++) {
            if(!result.get(i).getInStock()) {
                throw new IllegalStateException("Product " + result.get(i).getSkuCode() + " does not have enough stock");
            }
        }
        log.info("Order was created " + order.getOrderNumber());
        orderRepository.save(order);
    }

    public OrderLineItem mapFromDto(OrderLineItemDto orderLineItemDto) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setPrice(orderLineItemDto.getPrice());
        orderLineItem.setSkuCode(orderLineItemDto.getSkuCode());
        orderLineItem.setQuantity(orderLineItemDto.getQuantity());
        return orderLineItem;
    }
}
