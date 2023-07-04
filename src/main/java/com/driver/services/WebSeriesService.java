package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WebSeriesService {

    WebSeriesRepository webSeriesRepository;
    ProductionHouseRepository productionHouseRepository;

    public WebSeriesService(WebSeriesRepository webSeriesRepository, ProductionHouseRepository productionHouseRepository) {
        this.webSeriesRepository = webSeriesRepository;
        this.productionHouseRepository = productionHouseRepository;
    }

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Dont forget to save the production and webseries Repo

        WebSeries webSeries = webSeriesRepository.findBySeriesName(webSeriesEntryDto.getSeriesName());
        if(webSeries != null) throw new Exception("Series is already present");

        Optional<ProductionHouse> productionHouseOptional = productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId());
        if(!productionHouseOptional.isPresent()) throw new Exception("Production house not available");

        ProductionHouse productionHouse = productionHouseOptional.get();

        webSeries = new WebSeries();
        webSeries.setSeriesName(webSeriesEntryDto.getSeriesName());
        webSeries.setAgeLimit(webSeriesEntryDto.getAgeLimit());
        webSeries.setRating(webSeriesEntryDto.getRating());
        webSeries.setSubscriptionType(webSeriesEntryDto.getSubscriptionType());
        webSeries.setProductionHouse(productionHouse);

        productionHouse.getWebSeriesList().add(webSeries);
        double prevRating = productionHouse.getRatings();
        int size = productionHouse.getWebSeriesList().size();
        double newRating = (prevRating * (size - 1) + webSeriesEntryDto.getRating()) / size;

        productionHouse.setRatings(newRating);

        productionHouseRepository.save(productionHouse);
        return webSeriesRepository.findBySeriesName(webSeries.getSeriesName()).getId();
    }
}
