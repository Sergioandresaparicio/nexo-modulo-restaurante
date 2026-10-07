package com.roles.usermanagement.modules.sale;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
public interface SaleRepository extends JpaRepository<Sale,Long> {
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select s from Sale s where s.id=:id")
 Optional<Sale> findForUpdate(@Param("id") Long id);
}
