package org.crue.hercules.sgi.com.repository;

import org.crue.hercules.sgi.com.model.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link Email}.
 */
@Repository
public interface EmailRepository
    extends JpaRepository<Email, Long>, JpaSpecificationExecutor<Email> {
}
