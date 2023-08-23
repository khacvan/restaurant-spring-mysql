package com.restaurant.controller;

import com.restaurant.constant.AppConstant;
import com.restaurant.model.dto.request.BillRequestDTO;
import com.restaurant.model.dto.request.BillDetailsRequestDTO;
import com.restaurant.model.dto.res.BillResponseDTO;
import com.restaurant.service.BillService;
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
@RequestMapping("/bill")
public class BillController {
    private final BillService billService;
    @Autowired
    public BillController(BillService billService) {
        this.billService = billService;
    }

    /**
     * Find a list of all bills.
     *
     * @return A list of {@link BillResponseDTO} representing the bills.
     */
    @GetMapping
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(billService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(billService.findById(id));
    }

    /**
     * Retrieves a paginated list of bills based on the provided parameters.
     *
     * @param pageNum   The page number to retrieve.
     * @param sortBy The field to sort the results by.
     * @param sortDir   The sorting direction (ASC or DESC).
     * @param searchKey   A keyword to filter the results.
     * @return A list of {@link BillResponseDTO} representing the bills on the requested page.
     */
    @GetMapping("/page/{pageNum}")
    public ResponseEntity<?> findByPage(@PathVariable(name = "pageNum") int pageNum,
        @RequestParam(name = "size", defaultValue = AppConstant.DEFAULT_PAGE_SIZE) int pageSize,
        @RequestParam(name = "sortBy", defaultValue = AppConstant.DEFAULT_SORT_BY,required = false) String sortBy,
        @RequestParam(name = "sortDir", defaultValue = AppConstant.DEFAULT_SORT_DIRECTION,required = false) String sortDir,
        @RequestParam(name = "searchKey",defaultValue = AppConstant.EMPTY, required = false) String searchKey) {

        Page<BillResponseDTO> page = billService.findByPage(pageNum, pageSize, sortBy, sortDir, searchKey);
        return new ResponseEntity<>(page.getContent(), HttpStatus.OK);
    }

    /**
     * Saves a new bill.
     *
     * @param billRequestDto The {@link BillRequestDTO} containing the data of the new bill.
     * @return A {@link ResponseEntity} containing the saved {@link BillResponseDTO} and HTTP status CREATED.
     */
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody BillRequestDTO billRequestDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(ErrorHelper.getAllError(bindingResult), HttpStatus.BAD_REQUEST);
        }
        BillResponseDTO billResponseDto = billService.save(billRequestDto);
        return new ResponseEntity<>(billResponseDto, HttpStatus.CREATED);
    }

    /**
     * Deletes a bill with the given ID.
     *
     * @param billId The ID of the bill to delete.
     * @return A {@link ResponseEntity} with a success message and HTTP status OK.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Integer billId) {
        billService.delete(billId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Marks a bill as paid.
     *
     * @param billId The ID of the bill to mark as paid.
     * @return A {@link ResponseEntity} with a success message and HTTP status OK.
     */
    @PutMapping("/{billId}/payment")
    public ResponseEntity<?> payment(@PathVariable Long billId) {
        billService.payment(billId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Adds order items to a bill.
     *
     * @param billId         The ID of the bill to add order items to.
     * @param billDetailsToAdd A list of {@link BillDetailsRequestDTO} containing the order items to add.
     * @return A {@link ResponseEntity} with a success message and HTTP status OK if order items are added successfully,
     * or a not found message and HTTP status NOT_FOUND if any issue occurs.
     */
    @PutMapping("/{billId}/add-order")
    public ResponseEntity<?> addBillDetails(@PathVariable Long billId,
                             @RequestBody List<BillDetailsRequestDTO> billDetailsToAdd) {
        billService.addBillDetails(billId, billDetailsToAdd);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Removes order items from a bill.
     *
     * @param billId            The ID of the bill to remove order items from.
     * @param billDetailsToRemove A list of {@link BillDetailsRequestDTO} containing the order items to remove.
     * @return A {@link ResponseEntity} with a success message and HTTP status OK if order items are removed successfully,
     * or a not found message and HTTP status NOT_FOUND if any issue occurs.
     */
    @DeleteMapping("/{billId}/remove-order")
    public ResponseEntity<?> removeBillDetails(@PathVariable Long billId,
                             @RequestBody List<BillDetailsRequestDTO> billDetailsToRemove) {
        billService.removeBillDetails(billId, billDetailsToRemove);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
