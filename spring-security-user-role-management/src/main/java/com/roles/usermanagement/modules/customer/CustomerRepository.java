package com.roles.usermanagement.modules.customer;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
public interface CustomerRepository extends JpaRepository<Customer,Long> {
 @Lock(LockModeType.PESSIMISTIC_WRITE)
 @Query("select e from Customer e where e.id=:id")
 Optional<Customer> findForUpdate(@Param("id") Long id);
}
