package com.aradev.inventoryservice;

import com.aradev.inventoryservice.model.Inventory;
import com.aradev.inventoryservice.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Optional;

@SpringBootApplication
@EnableEurekaClient
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }


    @Bean
    CommandLineRunner loadData(InventoryRepository inventoryRepository) {
        return args -> {

            String iphoneSkuCode = "iphone_13";
            String asusSkuCode = "asus_vivo_book_10";

            List<Inventory> foundIphone = inventoryRepository.findBySkuCodeIn(List.of(iphoneSkuCode));
            if (foundIphone.size() == 0) {
                Inventory inventory1 = new Inventory();
                inventory1.setSkuCode(iphoneSkuCode);
                inventory1.setQuantity(100);
            	inventoryRepository.save(inventory1);
            }

            List<Inventory> foundAsus = inventoryRepository.findBySkuCodeIn(List.of(asusSkuCode));
			if(foundAsus.size() == 0) {
				Inventory inventory2 = new Inventory();
				inventory2.setSkuCode(asusSkuCode);
				inventory2.setQuantity(0);
				inventoryRepository.save(inventory2);
			}
        };
    }
}
