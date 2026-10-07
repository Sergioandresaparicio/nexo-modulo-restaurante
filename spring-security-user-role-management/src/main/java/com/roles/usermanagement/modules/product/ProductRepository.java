package com.roles.usermanagement.modules.product;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
public interface ProductRepository extends JpaRepository<Product,Long> {
 @Lock(LockModeType.PESSIMISTIC_WRITE)
 @Query("select e from Product e where e.id=:id")
 Optional<Product> findForUpdate(@Param("id") Long id);
}
