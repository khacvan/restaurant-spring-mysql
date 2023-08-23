package com.restaurant.service;

import com.restaurant.model.dto.request.BillRequestDTO;
import com.restaurant.model.dto.request.BillDetailsRequestDTO;
import com.restaurant.model.dto.res.BillResponseDTO;
import java.util.List;

public interface BillService extends BaseService<BillRequestDTO, BillResponseDTO> {
    void payment(Long billId);
    void removeBillDetails(Long billId, List<BillDetailsRequestDTO> orderItems);
    void addBillDetails(Long billId, List<BillDetailsRequestDTO> orderItems);
}
