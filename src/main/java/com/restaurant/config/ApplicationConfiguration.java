package com.restaurant.config;

import com.restaurant.model.dto.res.BillResponseDTO;
import com.restaurant.model.entity.bill.Bill;
import com.restaurant.validation.BillValidator;
import com.restaurant.validation.MenuItemValidator;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.modelmapper.ModelMapper;

/**
 * Configuration class for application beans and settings.
 */
@Configuration
public class ApplicationConfiguration {

    /**
     * Creates a new ModelMapper instance with strict matching strategy.
     *
     * @return The ModelMapper instance with strict matching strategy.
     */
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.typeMap(Bill.class, BillResponseDTO.class)
                .addMapping(Bill::isPaid, BillResponseDTO::setIsPaid);
        return modelMapper;
    }

    /**
     * Creates a new MenuItemValidator instance.
     *
     * @return The MenuItemValidator instance.
     */
    @Bean
    public MenuItemValidator menuItemValidator(){
        return new MenuItemValidator();
    }

    /**
     * Creates a new BillValidator instance.
     *
     * @return The BillValidator instance.
     */
    @Bean
    public BillValidator billValidator(){
        return new BillValidator();
    }
}
