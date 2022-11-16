package com.tio.leo.paymentchain.customer.repository;

import com.tio.leo.paymentchain.customer.entitys.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author emedina
 */
public interface ICustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT c from Customer c where c.code = ?1")
    Customer findByCode(String code);
}
