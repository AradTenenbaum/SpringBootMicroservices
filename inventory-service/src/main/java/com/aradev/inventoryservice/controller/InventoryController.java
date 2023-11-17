package com.aradev.inventoryservice.controller;

import com.aradev.inventoryservice.dto.ProductInStockRequest;
import com.aradev.inventoryservice.dto.ProductInStockResponse;
import com.aradev.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductInStockResponse> isInStock (@RequestBody List<ProductInStockRequest> productsInStockRequest) {
        return inventoryService.isInStock(productsInStockRequest);
    }
}
