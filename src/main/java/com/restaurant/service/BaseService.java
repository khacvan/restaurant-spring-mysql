package com.restaurant.service;

import org.springframework.data.domain.Page;
import java.util.List;

/**
 * This interface represents the base service for CRUD operations.
 *
 * @param <Request> The request DTO type for creating or updating entities.
 * @param <Response> The response DTO type for fetching entities.
 */
public interface BaseService<Request, Response> {

    /**
     * Fetches all entities.
     *
     * @return A list of all entities in response DTO format.
     */
    List<Response> findAll();

    /**
     * Finds an entity by its ID.
     *
     * @param id The ID of the entity to find.
     * @return The entity in response DTO format if found, otherwise null.
     */
    Response findById(int id);

    /**
     * Find entities by page and applies sorting and filtering based on the provided parameters.
     *
     * @param pageNum   The page number to fetch.
     * @param pageSize
     * @param sortBy    The field to use for sorting the result.
     * @param sortDir   The sorting direction ('asc' for ascending, 'desc' for descending).
     * @param searchKey The keyword to filter entities based on certain criteria.
     * @return A Page object containing a list of entities in response DTO format.
     */
    Page<Response> findByPage(int pageNum, int pageSize, String sortBy, String sortDir, String searchKey);

    /**
     * Saves a new entity or updates an existing one based on the provided request data.
     *
     * @param data The request DTO containing data for creating or updating the entity.
     * @return The created or updated entity in response DTO format.
     */
    Response save(Request data);

    /**
     * Deletes an entity by its ID.
     *
     * @param id The ID of the entity to delete.
     */
    void delete(int id);
}
