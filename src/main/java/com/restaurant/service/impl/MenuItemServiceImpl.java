package com.restaurant.service.impl;

import com.restaurant.constant.AppConstant;
import com.restaurant.constant.MenuItemConstant;
import com.restaurant.exception.ApplicationRuntimeException;
import com.restaurant.model.dto.request.MenuRequestDTO;
import com.restaurant.model.dto.res.MenuItemResponseDTO;
import com.restaurant.model.entity.bill.Bill;
import com.restaurant.model.entity.bill.BillDetails;
import com.restaurant.model.entity.menu.AdditionalDetails;
import com.restaurant.model.entity.menu.MenuItem;
import com.restaurant.repository.MenuItemRepository;
import com.restaurant.service.MenuItemService;
import com.restaurant.validation.MenuItemValidator;
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
import java.util.stream.Stream;

@Service
@Transactional
public class MenuItemServiceImpl implements MenuItemService {
    private static final Logger logger = LoggerFactory.getLogger(MenuItemServiceImpl.class);
    private final MenuItemRepository menuItemRepository;
    private final MenuItemValidator menuItemValidator;
    private final ModelMapper mapper;

    @Autowired
    public MenuItemServiceImpl(MenuItemRepository menuItemRepository, MenuItemValidator menuItemValidator, ModelMapper mapper) {
        this.menuItemRepository = menuItemRepository;
        this.menuItemValidator = menuItemValidator;
        this.mapper = mapper;
    }

    /**
     * Retrieves a list of all menu items.
     *
     * @return List of MenuResponseDto containing all the menu items.
     */
    @Override
    public List<MenuItemResponseDTO> findAll() {
        List<MenuItem> menuItems = menuItemRepository.findAll(Sort.by("id").ascending());
        return menuItems.stream()
                .map(menuItem -> mapper.map(menuItem, MenuItemResponseDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a paginated list of menu items filtered by keyword and sorted by the specified field and direction.
     *
     * @param pageNum   The page number to retrieve.
     * @param pageSize
     * @param sortField The field to sort the results by.
     * @param sortDir   The sorting direction (asc or desc).
     * @param keyword   The keyword to filter menu items by name and description.
     * @return Page of MenuResponseDto containing the list of menu items on the requested page.
     * @throws ApplicationRuntimeException if no menu item is found matching the keyword.
     */
    @Override
    public Page<MenuItemResponseDTO> findByPage(int pageNum, int pageSize, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
        if (keyword == null || keyword.equals(AppConstant.EMPTY)) {
            Page<MenuItem> menuItems = menuItemRepository.findAll(pageable);
            return menuItems.map(menuItem -> mapper.map(menuItem, MenuItemResponseDTO.class));
        }

        Page<MenuItem> menuItems = menuItemRepository.findByKeyword(keyword, pageable);
        if(menuItems.isEmpty()){
            throw new ApplicationRuntimeException("Can not found menu item with keyword: " + keyword);
        }
        return menuItems.map(menuItem -> mapper.map(menuItem, MenuItemResponseDTO.class));
    }

    /**
     * Retrieves a specific menu item by its ID.
     *
     * @param id The ID of the menu item to retrieve.
     * @return MenuResponseDto containing the requested menu item.
     * @throws ApplicationRuntimeException if the menu item with the specified ID is not found.
     */
    @Override
    public MenuItemResponseDTO findById(int id) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ApplicationRuntimeException("Can not found menu item with id: " + id));
        return mapper.map(menuItem, MenuItemResponseDTO.class);
    }

    /**
     * Saves a new menu item or updates an existing one with the provided data.
     *
     * @param menuItemRequest The data of the menu item to be created or updated.
     * @return MenuResponseDto containing the saved or updated menu item information.
     * @throws ApplicationRuntimeException if a menu item with the same name already exists or if duplicate additional details names are found.
     */
    @Override
    public MenuItemResponseDTO save(MenuRequestDTO menuItemRequest) {
        MenuItem menuSave;
        Map<String, AdditionalDetails> additionalDetailsMap = menuItemRequest.getAdditionalDetails().stream().collect(Collectors.toMap(AdditionalDetails::getName, additionalDetails -> additionalDetails, (existing, replacement) -> {
            throw new ApplicationRuntimeException("Duplicate additional detail  name: " + existing.getName());
        }));

        if (Objects.isNull(menuItemRequest.getId())) {
            // create new
            menuSave = createNewMenuItem(menuItemRequest, additionalDetailsMap);
        } else {
            // update
            menuSave = updateExistingMenuItem(menuItemRequest);
        }

        // validate data and save
        menuItemValidator.validateDataRequest(menuSave);
        checkDuplicateName(menuSave);
        checkDuplicateAdditionalDetailsName(menuSave);
        return mapper.map(menuItemRepository.save(menuSave), MenuItemResponseDTO.class);
    }

    /**
     * Deletes a menu item with the specified ID if it's not associated with any unpaid bills.
     * If associated with unpaid bills, the menu item will be disabled.
     *
     * @param id The ID of the menu item to delete.
     * @throws ApplicationRuntimeException if the menu item is associated with unpaid bills and cannot be deleted.
     */
    @Override
    public void delete(int id) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ApplicationRuntimeException("Can not found menu item with id: " + id));
        menuItemValidator.validateDelete(menuItem);
        //delete if there are no orders containing this menu item.
        if (menuItem.getBillDetails().isEmpty()) {
            menuItemRepository.delete(menuItem);
            logger.info("Menu item with ID " + id + " has been deleted successfully.");
        } else {
            // disable if there are paid orders containing this menu item.
            menuItem.setEnabled(false);
            menuItem.setUpdatedDate(new Date());
            menuItemRepository.save(menuItem);
            logger.info("Menu item with ID " + id + " has been updated successfully.");
        }
    }

    /**
     * Creates a new MenuItem based on the provided MenuRequestDto and additional details.
     *
     * @param menuItemRequest The MenuRequestDto containing the information for the new MenuItem.
     * @param additionalDetailsMap A map of additional details associated with the MenuItem. The keys in the map are
     *                             the names of the additional details, and the values are the AdditionalDetails objects.
     * @return The newly created MenuItem.
     */
    private MenuItem createNewMenuItem(MenuRequestDTO menuItemRequest, Map<String, AdditionalDetails> additionalDetailsMap) {
        MenuItem menuItem = mapper.map(menuItemRequest, MenuItem.class);
        List<AdditionalDetails> additionalDetails = additionalDetailsMap.values().stream()
                .peek(additionalDetail -> {
                    additionalDetail.setMenuItem(menuItem);
                    additionalDetail.setCreatedDate(new Date());
                })
                .collect(Collectors.toList());

        menuItem.setCreatedDate(new Date());
        menuItem.setImage(MenuItemConstant.URL_IMAGE_DEFAULT + menuItem.getImage());
        menuItem.setEnabled(true);
        menuItem.setAdditionalDetails(additionalDetails);
        return menuItem;
    }

    /**
     * Updates an existing MenuItem based on the provided MenuRequestDto.
     *
     * @param menuItemRequest The MenuRequestDto containing the updated information for the MenuItem.
     * @return The updated MenuItem.
     * @throws ApplicationRuntimeException if the MenuItem with the specified ID is not found in the repository.
     */
    private MenuItem updateExistingMenuItem(MenuRequestDTO menuItemRequest) {
        MenuItem menuItem = menuItemRepository.findById(menuItemRequest.getId())
                .orElseThrow(() -> new ApplicationRuntimeException("Menu item not found with ID: " + menuItemRequest.getId()));

        List<BillDetails> billDetails = menuItem.getBillDetails();
        if (billDetails.stream().anyMatch(orderItem -> {
            Bill bill = orderItem.getBill();
            return bill != null && !bill.isPaid();
        })) {
            throw new ApplicationRuntimeException("Unable to update menu item! It exists in the unpaid bill");
        }

        Date createdDate = menuItem.getCreatedDate();
        boolean enable = menuItem.getEnabled();
//        menuItem.setImage(menuItemRequest.getImage());
        menuItem = mapper.map(menuItemRequest, MenuItem.class);
        menuItem.setAdditionalDetails(updatedAdditionalDetails(menuItem));
        menuItem.setCreatedDate(createdDate);
        menuItem.setEnabled(enable);
        menuItem.setBillDetails(billDetails);
        menuItem.setUpdatedDate(new Date());
        return menuItem;
    }

    /**
     * Updates the additional details of a menu item based on the provided data.
     *
     * @param menuItem The menu item whose additional details are to be updated.
     * @return List of updated AdditionalDetails objects.
     */
    private List<AdditionalDetails> updatedAdditionalDetails(MenuItem menuItem) {
        List<AdditionalDetails> updatedAdditionalDetails = new ArrayList<>();
        for (AdditionalDetails additionalDetailsRequest : menuItem.getAdditionalDetails()) {
            if (additionalDetailsRequest.getId() != null) {
                // Update existing additional details
                AdditionalDetails existingDetails = menuItemRepository.findByAdditionalDetails(menuItem.getId())
                        .stream()
                        .filter(detail -> detail.getId().equals(additionalDetailsRequest.getId())).findFirst()
                        .orElseThrow(() -> new ApplicationRuntimeException("AdditionalDetails not found with ID: " + additionalDetailsRequest.getId()));
                existingDetails.setName(additionalDetailsRequest.getName());
                existingDetails.setValue(additionalDetailsRequest.getValue());
                existingDetails.setUpdatedDate(new Date());
                updatedAdditionalDetails.add(existingDetails);
            } else {
                // Create new additional details
                AdditionalDetails newDetails = AdditionalDetails.builder()
                        .name(additionalDetailsRequest.getName())
                        .value(additionalDetailsRequest.getValue())
                        .menuItem(menuItem)
                        .build();
                newDetails.setCreatedDate(new Date());
                updatedAdditionalDetails.add(newDetails);
            }
        }
        return updatedAdditionalDetails;
    }

    /**
     * Checks if there is any duplicate name among menu items, excluding the current menu item.
     *
     * @param menuItem The menu item to check for duplicate name.
     */
    private void checkDuplicateName(MenuItem menuItem) {
        Integer currentItemId = menuItem.getId();
        String currentName = menuItem.getName();
        if (menuItemRepository.findAll().stream().anyMatch(item -> !item.getId().equals(currentItemId) && item.getName().equals(currentName))) {
            throw new ApplicationRuntimeException("Menu item with name " + menuItem.getName() + " already exists.");
        }
    }

    /**
     * Checks if there are any duplicate additional details names for a menu item.
     * If duplicate names are found, a ApplicationRuntimeException will be thrown.
     *
     * @param menuItem The menu item to check for duplicate additional details names.
     * @throws ApplicationRuntimeException if duplicate additional details names are found.
     */
    public void checkDuplicateAdditionalDetailsName(MenuItem menuItem) {
        Set<Integer> processedIds = new HashSet<>();
        Set<String> nameSet = new HashSet<>();

        Stream.concat(menuItem.getAdditionalDetails().stream(), menuItemRepository.findByAdditionalDetails(menuItem.getId()).stream())
                .forEach(additionalDetail -> {
                    Integer additionalDetailId = additionalDetail.getId();
                    String name = additionalDetail.getName();
                    if (processedIds.contains(additionalDetailId)) {
                        return;
                    }
                    if (!nameSet.add(name)) {
                        throw new ApplicationRuntimeException("Duplicate additional details name found: " + name);
                    }
                    processedIds.add(additionalDetailId);
                });
    }
}
