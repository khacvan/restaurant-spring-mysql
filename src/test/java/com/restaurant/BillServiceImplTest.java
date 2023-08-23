//package com.restaurant;
//
//import java.util.ArrayList;
//import java.util.List;
//import com.restaurant.model.dto.request.BillRequestDTO;
//import com.restaurant.model.dto.request.OrderItemRequestDTO;
//import com.restaurant.model.dto.res.BillResponseDTO;
//import com.restaurant.model.entity.bill.Bill;
//import com.restaurant.model.entity.bill.OrderItem;
//import com.restaurant.repository.BillRepository;
//import com.restaurant.repository.MenuItemRepository;
//import com.restaurant.service.BillService;
//import com.restaurant.service.MenuItemService;
//import org.junit.jupiter.api.*;
//import org.modelmapper.ModelMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//import org.springframework.boot.test.context.SpringBootTest;
//import static org.junit.jupiter.api.Assertions.*;
//
//
//@SpringBootTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//public class BillServiceImplTest {
//    @Autowired
//    private BillService billService;
//
//
//    @Autowired
//    private MenuItemService menuItemService;
//
//    @Autowired
//    private ModelMapper modelMapper;
//    @Autowired
//    private BillRepository billRepository;
//    @Autowired
//    private MenuItemRepository menuItemRepository;
//    @Test
//    public void testCreate() {
//        System.out.println("Running testCreate...");
//
//        List<OrderItemRequestDTO> orderItemRequestDTOS = new ArrayList<>();
//        orderItemRequestDTOS.add(new OrderItemRequestDTO(1, 2));
//        orderItemRequestDTOS.add(new OrderItemRequestDTO(3, 1));
//
//        BillRequestDTO billRequestDTO = new BillRequestDTO();
//        billRequestDTO.setOrderItemRequests(orderItemRequestDTOS);
//        BillResponseDTO billResponseDTO = billService.save(billRequestDTO);
//
//        assertNotNull(billResponseDTO);
//        assertNotNull(billResponseDTO.getId());
//        assertEquals(orderItemRequestDTOS.size(), billResponseDTO.getOrderItems().size());
//        for (int i = 0; i < orderItemRequestDTOS.size(); i++) {
//            OrderItemRequestDTO expectedOrderItem = orderItemRequestDTOS.get(i);
//            OrderItem actualOrderItem = billResponseDTO.getOrderItems().get(i);
//            assertEquals(expectedOrderItem.getMenuItemId(), actualOrderItem.getMenuItem().getId());
//            assertEquals(expectedOrderItem.getQuantity(), actualOrderItem.getQuantity());
//        }
//    }
//    @Test
//    public void testGet() {
//        BillResponseDTO result = billService.findById(2);
//        assertNotNull(result);
//        assertEquals(2, result.getId());
//        assertEquals(1, result.getOrderItems().size());
//    }
//    @Test
//    public void testUpdate() {
//        Bill bill = billRepository.findById(3L).get();
//        List<OrderItem> orderItems = new ArrayList<>();
//        OrderItem orderItem1 = new OrderItem();
//        orderItem1.setMenuItem(menuItemRepository.findById(2).get());
//        orderItem1.setQuantity(2);
//        OrderItem orderItem2 = new OrderItem();
//        orderItem2.setMenuItem(menuItemRepository.findById(1).get());
//        orderItem2.setQuantity(1);
//        orderItems.add(orderItem1);
//        orderItems.add(orderItem2);
//        bill.setOrderItems(orderItems);
//
//        // When: Perform the actual operation you want to test (update order items)
//        BillRequestDTO billRequestDTO = new BillRequestDTO();
//        List<OrderItemRequestDTO> updatedOrderItems = new ArrayList<>();
//        updatedOrderItems.add(new OrderItemRequestDTO(4, 3));
//        updatedOrderItems.add(new OrderItemRequestDTO(5, 2));
//        billRequestDTO.setOrderItemRequests(updatedOrderItems);
//        billService.addOrderItem(Long.valueOf(bill.getId()), updatedOrderItems);
//
//        // Then: Retrieve the updated bill and assert the expected outcome
//        BillResponseDTO result = billService.findById(bill.getId());
//        assertNotNull(result);
//        assertEquals(updatedOrderItems.size() + orderItems.size(), result.getOrderItems().size());
//        for (int i = 0; i < updatedOrderItems.size(); i++) {
//            OrderItemRequestDTO expectedOrderItem = updatedOrderItems.get(i);
//            OrderItem actualOrderItem = result.getOrderItems().get(i + orderItems.size());
//            assertEquals(expectedOrderItem.getMenuItemId(), actualOrderItem.getMenuItem().getId());
//            assertEquals(expectedOrderItem.getQuantity(), actualOrderItem.getQuantity());
//        }
//    }
//
//
//
//
//    @Test
//    public void testList() {
//    }
//
//    @Test
//    public void testDelete() {
//    }
//
//
//
//}
