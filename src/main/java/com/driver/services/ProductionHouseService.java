package com.driver.services;


import com.driver.EntryDto.ProductionHouseEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.repository.ProductionHouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductionHouseService {

    ProductionHouseRepository productionHouseRepository;

    public ProductionHouseService(ProductionHouseRepository productionHouseRepository) {
        this.productionHouseRepository = productionHouseRepository;
    }

    public Integer addProductionHouseToDb(ProductionHouseEntryDto productionHouseEntryDto){

        ProductionHouse productionHouse = new ProductionHouse();
        productionHouse.setName(productionHouse.getName());
        productionHouse.setRatings(0);
        return productionHouseRepository.save(productionHouse).getId();
    }



}
