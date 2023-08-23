//package com.restaurant;
//
//import com.restaurant.model.dto.request.MenuRequestDTO;
//import com.restaurant.model.dto.res.MenuItemResponseDTO;
//import com.restaurant.model.entity.menu.AdditionalDetails;
//import com.restaurant.model.entity.menu.MenuItem;
//import com.restaurant.repository.MenuItemRepository;
//import com.restaurant.service.impl.MenuItemServiceImpl;
//import com.restaurant.validation.MenuItemValidator;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.modelmapper.ModelMapper;
//import org.springframework.data.domain.Sort;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//public class MenuItemServiceImplTest {
//
//    @Mock
//    private MenuItemRepository menuItemRepository;
//
//    @Mock
//    private MenuItemValidator menuItemValidator;
//
//    @Mock
//    private ModelMapper mapper;
//
//    @InjectMocks
//    private MenuItemServiceImpl menuItemService;
//
//    @BeforeEach
//    public void setup() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    public void testFindAll() {
//        // Mock the data to be returned by the repository
//// Create instances of MenuItem with specific data using the builder pattern
//        MenuItem menuItem1 = MenuItem.builder()
//                .name("Pizza")
//                .image("pizza.jpg")
//                .description("Delicious pizza with various toppings.")
//                .price(10.99)
//                .inStock(50)
//                .enabled(true)
//                .build();
//
//        MenuItem menuItem2 = MenuItem.builder()
//                .name("Burger")
//                .image("burger.jpg")
//                .description("Juicy burger with cheese and veggies.")
//                .price(8.49)
//                .inStock(30)
//                .enabled(true)
//                .build();
//
//        MenuItem menuItem3 = MenuItem.builder()
//                .name("Salad")
//                .image("salad.jpg")
//                .description("Healthy salad with fresh greens.")
//                .price(6.99)
//                .inStock(20)
//                .enabled(true)
//                .build();
//
//        List<MenuItem> menuItems = Arrays.asList(menuItem1, menuItem2,menuItem3);
//
//        when(menuItemRepository.findAll(Sort.by("id").ascending())).thenReturn(menuItems);
//
//        // Mock the mapping
//        MenuItemResponseDTO menuItemResponse1 = new MenuItemResponseDTO();
//        MenuItemResponseDTO menuItemResponse2 = new MenuItemResponseDTO();
//        when(mapper.map(menuItem1, MenuItemResponseDTO.class)).thenReturn(menuItemResponse1);
//        when(mapper.map(menuItem2, MenuItemResponseDTO.class)).thenReturn(menuItemResponse2);
//
//        // Test the service method
//        List<MenuItemResponseDTO> result = menuItemService.findAll();
//
//        // Verify the results
//        assertEquals(2, result.size());
//        assertSame(menuItemResponse1, result.get(0));
//        assertSame(menuItemResponse2, result.get(1));
//    }
//
//
//}
