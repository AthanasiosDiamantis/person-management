package com.saki.personmanagement.repository;

import com.saki.personmanagement.model.Order;
import com.saki.personmanagement.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository for Order database access.
 *
 * @author saki
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // ====================== BASIC QUERY METHODS ====================

    /**
     * Finds order by status.
     * SQL: SELECT * FROM orders WHERE status = ?
     *
     * @param status enum of OrderStatus
     * @return List of orders
     */
    List<Order> findByStatus(OrderStatus status);

    /**
     * Finds order by person ID
     * SQL: SELECT * FROM orders WHERE person_id = ?.
     *
     * @param personId person ID
     * @return List of orders
     */
    List<Order> findByPersonId(Long personId);

    /**
     * Finds order by person email.
     * SQL: SELECT * FROM orders WHERE person.email = ?
     *
     * @param email person email
     * @return List of orders
     */
    List<Order> findByPersonEmail(String email);

    /**
     * Finds order by price greater than.
     * SQL: SELECT * FROM orders WHERE price > ?
     *
     * @param price price in BigDecimal
     * @return List of orders
     */
    List<Order> findByPriceGreaterThan(BigDecimal price);

    /**
     * Finds order by status and order date descending.
     * SQL: SELECT * FROM orders WHERE status = ? ORDER BY order_date DESC
     *
     * @param status enum of OrderStatus
     * @return List of orders
     */
    List<Order> findByStatusOrderByOrderDateDesc(OrderStatus status);

    /**
     * Finds order by person ID and status.
     * SQL: SELECT * FROM orders WHERE person_id = ? AND status = ?
     *
     * @param personId person ID
     * @param status   enum of OrderStatus
     * @return List of orders
     */
    List<Order> findByPersonIdAndStatus(Long personId, OrderStatus status);

    /**
     * Finds top 10 orders by order date descending.
     * SQL: SELECT * FROM orders ORDER BY order_date DESC LIMIT 10
     *
     * @return TOP 10 List of orders
     */
    List<Order> findTop10ByOrderByOrderDateDesc();

    // ====================== CUSTOM JPQL QUERIES ====================

    /**
     * Loads orders with person data in a single query (solves N+1 problem).
     *
     * @param status enum of OrderStatus
     * @return List of orders
     */
    @Query("SELECT o FROM Order o JOIN FETCH o.person WHERE o.status = :status")
    List<Order> findByStatusWithPerson(@Param("status") OrderStatus status);
}
