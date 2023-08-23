package com.restaurant.controller;

import com.restaurant.constant.AppConstant;
import com.restaurant.model.dto.request.MenuRequestDTO;
import com.restaurant.model.dto.res.MenuItemResponseDTO;
import com.restaurant.service.MenuItemService;
import com.restaurant.util.ErrorHelper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menuitem")
public class MenuItemController {

    private final MenuItemService menuItemService;

    @Autowired
    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    /**
     * Find a list of all menu items.
     *
     * @return A list of {@link MenuItemResponseDTO} representing the menu items.
     */

    @GetMapping
    public ResponseEntity<List<MenuItemResponseDTO>> findAll() {
        return ResponseEntity.ok(menuItemService.findAll());
    }

    /**
     * Find a menu item by its ID.
     *
     * @param id The ID of the menu item to retrieve.
     * @return The {@link MenuItemResponseDTO} representing the menu item.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MenuItemResponseDTO> findById(@PathVariable(name = "id") Integer id) {
        return ResponseEntity.ok(menuItemService.findById(id));
    }

    /**
     * Find a paginated list of menu items based on the provided parameters.
     *
     * @param pageNum   The page number to retrieve.
     * @param sortBy The field to sort the results by.
     * @param sortDir   The sorting direction (ASC or DESC).
     * @param searchKey   A keyword to filter the results.
     * @return A list of {@link MenuItemResponseDTO} representing the menu items on the requested page.
     */
    @GetMapping("/page/{pageNum}")
    public ResponseEntity<?> findByPage(@PathVariable(name = "pageNum") int pageNum,
         @RequestParam(name = "size", defaultValue = AppConstant.DEFAULT_PAGE_SIZE) int pageSize,
         @RequestParam(name = "sortBy", defaultValue = AppConstant.DEFAULT_SORT_BY,required = false) String sortBy,
         @RequestParam(name = "sortDir", defaultValue = AppConstant.DEFAULT_SORT_DIRECTION,required = false) String sortDir,
         @RequestParam(name = "searchKey",defaultValue = AppConstant.EMPTY, required = false) String searchKey){

        Page<MenuItemResponseDTO> page = menuItemService.findByPage(pageNum, pageSize , sortBy, sortDir, searchKey);
        return new ResponseEntity<>(page.getContent(), HttpStatus.OK);
    }

    /**
     * Saves a new menu item.
     *
     * @param menuRequestDto The {@link MenuRequestDTO} containing the data of the new menu item.
     * @return A {@link ResponseEntity} containing the saved {@link MenuItemResponseDTO} and HTTP status CREATED.
     */
    @PostMapping
    public ResponseEntity<?> save(@RequestBody @Valid MenuRequestDTO menuRequestDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(ErrorHelper.getAllError(bindingResult), HttpStatus.BAD_REQUEST);
        }
        MenuItemResponseDTO menuItemResponseDto = menuItemService.save(menuRequestDto);
        return new ResponseEntity<>(menuItemResponseDto, HttpStatus.CREATED);
    }

    /**
     * Updates an existing menu item.
     *
     * @param id             The ID of the menu item to update.
     * @param menuRequestDto The {@link MenuRequestDTO} containing the updated data of the menu item.
     * @return A {@link ResponseEntity} containing the updated {@link MenuItemResponseDTO} and HTTP status OK.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable(name = "id") Integer id,
                                                      @Valid @RequestBody MenuRequestDTO menuRequestDto,
                                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(ErrorHelper.getAllError(bindingResult), HttpStatus.BAD_REQUEST);
        }
        menuRequestDto.setId(id);
        return new ResponseEntity<>(menuItemService.save(menuRequestDto), HttpStatus.OK);
    }

    /**
     * Deletes a menu item with the given ID.
     *
     * @param menuId The ID of the menu item to delete.
     * @return A {@link ResponseEntity} with a success message and HTTP status OK.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Integer menuId) {
        menuItemService.delete(menuId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
