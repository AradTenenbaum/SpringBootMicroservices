package com.aradev.inventoryservice.service;

import com.aradev.inventoryservice.dto.ProductInStockRequest;
import com.aradev.inventoryservice.dto.ProductInStockResponse;
import com.aradev.inventoryservice.model.Inventory;
import com.aradev.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<ProductInStockResponse> isInStock(List<ProductInStockRequest> productsInStockRequest) {
        List<Inventory> inventories = inventoryRepository.findBySkuCodeIn(
                productsInStockRequest
                        .stream()
                        .map(ProductInStockRequest::getSkuCode)
                        .collect(Collectors.toList()));

        List<ProductInStockResponse> productsInStockResponse = new ArrayList<>();

        productsInStockRequest.forEach(productInStockRequest -> {
            Optional<Inventory> optionalInventory = inventories
                    .stream()
                    .filter(inventory -> inventory
                            .getSkuCode()
                            .equals(productInStockRequest.getSkuCode()))
                    .findFirst();

            if(optionalInventory.isPresent() && optionalInventory.get().getQuantity() >= productInStockRequest.getQuantity()) {
                productsInStockResponse.add(new ProductInStockResponse(productInStockRequest.getSkuCode(), true));
            } else {
                productsInStockResponse.add(new ProductInStockResponse(productInStockRequest.getSkuCode(), false));
            }
        });

        return productsInStockResponse;
    }

}
