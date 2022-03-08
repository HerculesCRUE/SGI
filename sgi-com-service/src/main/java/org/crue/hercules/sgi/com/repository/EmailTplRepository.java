package org.crue.hercules.sgi.com.repository;

import java.util.Optional;

import org.crue.hercules.sgi.com.model.EmailTpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link EmailTpl}.
 */
@Repository
public interface EmailTplRepository
    extends JpaRepository<EmailTpl, Long>, JpaSpecificationExecutor<EmailTpl> {

  Optional<EmailTpl> findByName(String name);
}
