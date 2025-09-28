package com.e_Commerce.order_service.feigns;

import org.springframework.cloud.openfeign.FeignClient;


@FeignClient(name = "inventory-service")
public interface InventoryServiceClient {

}
