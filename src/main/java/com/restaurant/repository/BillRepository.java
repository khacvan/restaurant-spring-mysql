package com.restaurant.repository;

import com.restaurant.model.entity.bill.Bill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


/**
 * Spring Data JPA repository for the Bill entity.
 */
public interface BillRepository extends JpaRepository<Bill, Long> {

    /**
     * Custom query to search for bills based on a given keyword in the name and description of the associated menu items.
     *
     * @param keyword  The search term to be used for filtering.
     * @param pageable The pagination information.
     * @return A Page of Bill entities that match the search criteria.
     */
    @Query("SELECT b FROM Bill b JOIN b.billDetails oi JOIN oi.menuItem mi WHERE CONCAT(mi.name, ' ', mi.description) LIKE %?1%")
    Page<Bill> findAll(String keyword, Pageable pageable);

}
