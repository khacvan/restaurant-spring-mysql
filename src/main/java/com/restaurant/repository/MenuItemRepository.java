package com.restaurant.repository;

import com.restaurant.model.entity.menu.AdditionalDetails;
import com.restaurant.model.entity.menu.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link MenuItem} entities.
 */
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    /**
     * Finds menu items based on a keyword present in their ID, name, or description.
     *
     * @param keyword  The keyword to search for in the menu items.
     * @param pageable The pagination information.
     * @return A {@link Page} of {@link MenuItem} matching the search criteria.
     */
    @Query("SELECT m FROM MenuItem m WHERE CONCAT(m.id, ' ', m.name, ' ', m.description) LIKE %?1%")
    Page<MenuItem> findByKeyword(String keyword, Pageable pageable);

    /**
     * Finds a menu item by its ID.
     *
     * @param id The ID of the menu item to find.
     * @return An {@link Optional} of {@link MenuItem} containing the menu item if found, or empty if not found.
     */
    Optional<MenuItem> findById(Integer id);

    /**
     * Finds additional details associated with a specific menu item.
     *
     * @param menuItemId The ID of the menu item to find additional details for.
     * @return A list of {@link AdditionalDetails} associated with the specified menu item.
     */
    @Query("SELECT a FROM AdditionalDetails a WHERE a.menuItem.id = :menuItemId")
    List<AdditionalDetails> findByAdditionalDetails(@Param("menuItemId") Integer menuItemId);
}
