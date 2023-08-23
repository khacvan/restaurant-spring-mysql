package com.restaurant.service.impl;

import com.restaurant.constant.AppConstant;
import com.restaurant.exception.ApplicationRuntimeException;
import com.restaurant.model.dto.request.BillRequestDTO;
import com.restaurant.model.dto.request.BillDetailsRequestDTO;
import com.restaurant.model.dto.res.BillResponseDTO;
import com.restaurant.model.entity.bill.Bill;
import com.restaurant.model.entity.bill.BillDetails;
import com.restaurant.model.entity.menu.MenuItem;
import com.restaurant.repository.BillRepository;
import com.restaurant.service.BillService;
import com.restaurant.service.MenuItemService;
import com.restaurant.validation.BillValidator;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the BillService interface that manages bills and order items.
 */
@Service
@Transactional
public class BillServiceImpl implements BillService {
    private static final Logger logger = LoggerFactory.getLogger(BillServiceImpl.class);
    private final BillRepository billRepo;
    private final MenuItemService menuItemService;
    private final BillValidator billValidator;
    private final ModelMapper mapper;

    /**
     * Constructs a new BillServiceImpl with the given repositories and services.
     *
     * @param billRepository    The repository for bills.
     * @param menuItemService   The service for menu items.
     * @param mapper            The model mapper for entity-DTO mapping.
     */
    @Autowired
    public BillServiceImpl(BillRepository billRepository, MenuItemService menuItemService, BillValidator billValidator,ModelMapper mapper) {
        this.billRepo = billRepository;
        this.menuItemService = menuItemService;
        this.billValidator = billValidator;
        this.mapper = mapper;
    }

    /**
     * Retrieves a list of all bills with detailed information of order items.
     *
     * @return List of BillResponseDto containing all the bills.
     */
    @Override
    public List<BillResponseDTO> findAll() {
        return billRepo.findAll().stream()
                .map(bill -> {
                    BillResponseDTO billResponseDto = mapper.map(bill, BillResponseDTO.class);
                    billResponseDto.setBillDetails(convertMenuResponseInBill(billResponseDto));
                    return billResponseDto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a specific bill by its ID with detailed information of order items.
     *
     * @param id The ID of the bill to retrieve.
     * @return BillResponseDto containing the requested bill.
     * @throws ApplicationRuntimeException if the bill with the specified ID is not found.
     */
    @Override
    public BillResponseDTO findById(int id) {
        Bill bill = billRepo.findById((long) id)
                .orElseThrow(() -> new ApplicationRuntimeException("Can not found bill with id: " + id));
        BillResponseDTO billResponseDto = mapper.map(bill, BillResponseDTO.class);
        billResponseDto.setBillDetails(convertMenuResponseInBill(billResponseDto));
        return billResponseDto;
    }

    /**
     * Retrieves a paginated list of bills with detailed information of order items,
     * filtered by keyword, and sorted by the specified field and direction.
     *
     * @param pageNum   The page number to retrieve.
     * @param pageSize  The size of page.
     * @param sortField The field to sort the results by.
     * @param sortDir   The sorting direction (asc or desc).
     * @param keyword   The keyword to filter bills by menu item name and description.
     * @return Page of BillResponseDto containing the list of bills on the requested page.
     * @throws ApplicationRuntimeException if no bill is found matching the keyword.
     */
    @Override
    public Page<BillResponseDTO> findByPage(int pageNum, int pageSize, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
        // return all bills
        if (keyword == null || keyword.equals(AppConstant.EMPTY)) {
            Page<Bill> bills = billRepo.findAll(pageable);
            return bills.map(bill -> {
                BillResponseDTO billResponseDto = mapper.map(bill, BillResponseDTO.class);
                billResponseDto.setBillDetails(convertMenuResponseInBill(billResponseDto));
                return billResponseDto;
            });
        }

        // return a list of bills whose menu (name, description) matches the keyword
        Page<Bill> bills = billRepo.findAll(keyword,pageable);
        if(bills.isEmpty()){
            throw new ApplicationRuntimeException("Can not found bill with menu item name: " + keyword);
        }
        return bills.map(bill -> {
            BillResponseDTO billResponseDto = mapper.map(bill, BillResponseDTO.class);
            billResponseDto.setBillDetails(convertMenuResponseInBill(billResponseDto));
            return billResponseDto;
        });
    }

    /**
     * Saves a new bill or updates an existing one with the provided data.
     *
     * @param billRequestDto The data of the bill to be created or updated.
     * @return BillResponseDto containing the saved or updated bill information.
     * @throws ApplicationRuntimeException if the provided order items are empty or the bill to update is not found.
     */
    @Override
    public BillResponseDTO save(BillRequestDTO billRequestDto) {
        if (billRequestDto.getBillDetailsRequestDTOS().isEmpty()) {
            throw new ApplicationRuntimeException("Can not create bill with none order items!");
        }

        Bill bill = new Bill();
        List<BillDetails> billDetails = new ArrayList<>();
        if(Objects.isNull(billRequestDto.getId())) {
            for (BillDetailsRequestDTO billDetailsRequestDto : billRequestDto.getBillDetailsRequestDTOS()) {
                MenuItem menuItem = mapper.map(menuItemService.findById(billDetailsRequestDto.getMenuItemId()), MenuItem.class);
                if (menuItem.getInStock() < billDetailsRequestDto.getQuantity()) {
                    throw new ApplicationRuntimeException("Not enough items in stock for menu item: " + menuItem.getName());
                }
                BillDetails billDetailsSave = createNewBillDetails(bill, menuItem.getId(), billDetailsRequestDto.getQuantity());
                billDetails.add(billDetailsSave);
            }
            bill.setIsPaid(false);
            bill.setCreatedDate(new Date());
            bill.setBillDetails(billDetails);
            bill.setTotalPrice(calculateTotalPrice(billDetails));
        }
        return mapper.map(billRepo.save(bill), BillResponseDTO.class);
    }

    /**
     * Deletes a bill with the specified ID if it's not paid and created more than 2 weeks ago.
     *
     * @param id The ID of the bill to delete.
     * @throws ApplicationRuntimeException if the bill is already paid or was created less than 2 weeks ago.
     */
    @Override
    public void delete(int id) {
        Bill bill = billRepo.findById((long) id)
                .orElseThrow(() -> new ApplicationRuntimeException("Cannot find bill with id: " + id));
        billValidator.validateDelete(bill);
        billRepo.delete(bill);
    }

    /**
     * Pays the bill with the given ID, updating the stock of the menu items accordingly.
     *
     * @param billId The ID of the bill to be paid.
     * @throws ApplicationRuntimeException if the bill is already paid or some menu items are out of stock.
     */
    @Override
    public void payment(Long billId) {
        Bill bill = billRepo.findById(billId).orElseThrow(() -> new ApplicationRuntimeException("Bill not found with id: " + billId));
        if(bill.isPaid()){
            throw new ApplicationRuntimeException("Bill id: " + billId + " has been paid");
        }
        for (BillDetails billDetails : bill.getBillDetails()){
            MenuItem menuItem = billDetails.getMenuItem();
            if (menuItem.getInStock() < billDetails.getQuantity()) {
                throw new ApplicationRuntimeException("Not enough items in stock for menu item: " + menuItem.getName());
            }else {
                menuItem.setInStock(menuItem.getInStock() - billDetails.getQuantity());
            }
        }
        bill.setIsPaid(true);
        billValidator.validateDataRequest(bill);
        billRepo.save(bill);
    }

    /**
     * Adds order items to the specified bill, updating the existing items if any.
     *
     * @param billId      The ID of the bill to add order items to.
     * @param billDetailsRequestDTOS  The list of order items to be added or updated.
     * @throws ApplicationRuntimeException if the bill is already paid or some menu items are out of stock.
     */
    @Override
    public void addBillDetails(Long billId, List<BillDetailsRequestDTO> billDetailsRequestDTOS) {
        Bill bill = billRepo.findById(billId)
                .orElseThrow(() -> new ApplicationRuntimeException("Bill not found with id: " + billId));
        if (bill.isPaid()) {
            throw new ApplicationRuntimeException("The order items in the paid bill cannot be updated!");
        }

        List<BillDetails> currentBillDetails = bill.getBillDetails();
        for (BillDetailsRequestDTO billDetailsRequestDTO : billDetailsRequestDTOS) {
            validateStockAvailability(billDetailsRequestDTO);
            BillDetails existingBillDetails = getBillDetailsByMenuItemId(currentBillDetails, billDetailsRequestDTO.getMenuItemId());
            if (existingBillDetails != null) {
                updateExistingBillDetails(existingBillDetails, billDetailsRequestDTO);
            } else {
                BillDetails newBillDetails = createNewBillDetails(bill, billDetailsRequestDTO.getMenuItemId(), billDetailsRequestDTO.getQuantity());
                currentBillDetails.add(newBillDetails);
            }
        }
        saveBillUpdates(bill, currentBillDetails);
    }

    /**
     * Validates the availability of stock for a menu item based on the requested quantity.
     *
     * @param billDetailsRequestDTO The BillDetailsRequestDTO containing menu item ID and quantity.
     * @throws ApplicationRuntimeException if the requested quantity exceeds available stock.
     */
    private void validateStockAvailability(BillDetailsRequestDTO billDetailsRequestDTO) {
        int inStock = menuItemService.findById(billDetailsRequestDTO.getMenuItemId()).getInStock();
        if (billDetailsRequestDTO.getQuantity() > inStock) {
            throw new ApplicationRuntimeException("Quantity to add exceeds available stock for menu item with ID: " + billDetailsRequestDTO.getMenuItemId());
        }
    }

    /**
     * Updates the quantity of existing bill details while ensuring stock availability.
     *
     * @param existingBillDetails The existing BillDetails object to update.
     * @param billDetailsRequestDTO The BillDetailsRequestDTO containing quantity and menu item ID.
     * @throws ApplicationRuntimeException if the updated quantity exceeds available stock.
     */
    private void updateExistingBillDetails(BillDetails existingBillDetails, BillDetailsRequestDTO billDetailsRequestDTO) {
        int updatedQuantity = existingBillDetails.getQuantity() + billDetailsRequestDTO.getQuantity();
        int inStock = menuItemService.findById(billDetailsRequestDTO.getMenuItemId()).getInStock();
        if (updatedQuantity > inStock) {
            throw new ApplicationRuntimeException("Total quantity exceeds available stock for menu item with ID: " + billDetailsRequestDTO.getMenuItemId());
        }
        existingBillDetails.setQuantity(updatedQuantity);
        existingBillDetails.setUpdatedDate(new Date());
    }

    /**
     * Updates the final bill with calculated total price and validates and saves it.
     *
     * @param bill The Bill object to update.
     * @param currentBillDetails The list of current BillDetails objects.
     */
    private void saveBillUpdates(Bill bill, List<BillDetails> currentBillDetails) {
        bill.setTotalPrice(calculateTotalPrice(currentBillDetails));
        bill.setUpdatedDate(new Date());
        billValidator.validateDataRequest(bill);
        billRepo.save(bill);
    }

    /**
     * Removes specified order items from the given bill.
     *
     * @param billId           The ID of the bill to remove order items from.
     * @param billDetailsToRemove The list of order items to be removed.
     * @throws ApplicationRuntimeException if the bill is already paid or some order items are not found.
     */
    @Override
    public void removeBillDetails(Long billId, List<BillDetailsRequestDTO> billDetailsToRemove) {
        if (billDetailsToRemove.isEmpty()) {
            throw new ApplicationRuntimeException("List of order items to remove is empty.");
        }
        Bill bill = billRepo.findById(billId).orElseThrow(() ->  new ApplicationRuntimeException("Bill not found with id: " + billId));
        if(bill.isPaid()){
            throw new ApplicationRuntimeException("The order item in the paid bill cannot be deleted!");
        }

        List<BillDetails> billDetails = bill.getBillDetails();
        for (BillDetailsRequestDTO billDetail : billDetailsToRemove) {
            int menuItemId = billDetail.getMenuItemId();
            int quantityToRemove = billDetail.getQuantity();

            BillDetails existingBillDetails = getBillDetailsByMenuItemId(billDetails, menuItemId);
            if (existingBillDetails != null) {
                int currentQuantity = existingBillDetails.getQuantity();
                int updatedQuantity = currentQuantity - quantityToRemove;
                if (updatedQuantity <= 0) {
                    billDetails.remove(existingBillDetails);
                }
                existingBillDetails.setQuantity(updatedQuantity);
            } else {
                logger.error("Failed to remove order items from bill with ID " + billId);
                throw new ApplicationRuntimeException("Can't find menu item id: " + menuItemId + " in bill "  + billId);
            }
        }
        if (billDetails.isEmpty()) {
            billRepo.delete(bill);
            logger.info("Removed all order items from bill with ID: " + billId);
        } else {
            bill.setTotalPrice(calculateTotalPrice(billDetails));
            billRepo.save(bill);
            logger.info("Removed order items from bill with ID: " + billId);
        }
    }

    /**
     * Calculates the total price for a list of order items.
     *
     * @param billDetails The list of order items for which to calculate the total price.
     * @return The total price of all order items in the list.
     */
    private Double calculateTotalPrice(List<BillDetails> billDetails) {
        return billDetails.stream()
                .mapToDouble(BillDetails -> BillDetails.getUnitPrice() * BillDetails.getQuantity())
                .sum();
    }

    /**
     * Extracts a list of order items from the given BillResponseDto.
     * Note: The price of each menu item in the order items will be set to the unit price.
     *
     * @param billResponseDto The BillResponseDto object containing order items to be extracted.
     * @return A list of BillDetails objects extracted from the BillResponseDto.
     */
    private List<BillDetails> convertMenuResponseInBill(BillResponseDTO billResponseDto) {
        return billResponseDto.getBillDetails().stream()
                .peek(BillDetails -> BillDetails.getMenuItem().setPrice(BillDetails.getUnitPrice()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves an BillDetails object from a list of order items based on a given menu item ID.
     *
     * @param billDetails The list of BillDetails objects to search through.
     * @param menuItemId The menu item ID to match against BillDetails objects.
     * @return The matching BillDetails object with the specified menu item ID, or null if not found.
     */
    private BillDetails getBillDetailsByMenuItemId(List<BillDetails> billDetails, Integer menuItemId) {
        Optional<BillDetails> foundBillDetails = billDetails.stream()
                .filter(BillDetails -> BillDetails.getMenuItem().getId().equals(menuItemId))
                .findFirst();
        return foundBillDetails.orElse(null);
    }

    /**
     * Creates a new BillDetails for the specified Bill and MenuItem.
     * The created BillDetails will have the given quantityToAdd and its createdTime will be set to the current date.
     *
     * @param bill The Bill for which the BillDetails is created.
     * @param menuItemId The ID of the MenuItem associated with the BillDetails.
     * @param quantityToAdd The quantity to add to the BillDetails.
     * @return The newly created BillDetails.
     * @throws ApplicationRuntimeException if the MenuItem with the specified menuItemId is not found.
     */
    private BillDetails createNewBillDetails(Bill bill, Integer menuItemId, Integer quantityToAdd) {
        MenuItem menuItem = mapper.map(menuItemService.findById(menuItemId), MenuItem.class);
        BillDetails billDetails = BillDetails.builder()
                .bill(bill)
                .menuItem(menuItem)
                .unitPrice(menuItem.getPrice())
                .quantity(quantityToAdd)
                .build();
        billDetails.setCreatedDate(new Date());
        return billDetails;
    }
}
