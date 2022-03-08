package org.crue.hercules.sgi.com.repository;

import org.crue.hercules.sgi.com.model.EmailParamDeferrable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link EmailParamDeferrable}.
 */
@Repository
public interface EmailParamDeferrableRepository
    extends JpaRepository<EmailParamDeferrable, Long>, JpaSpecificationExecutor<EmailParamDeferrable> {

  long deleteAllById(Long id);
}
