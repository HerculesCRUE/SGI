package org.crue.hercules.sgi.com.repository;

import java.util.List;

import org.crue.hercules.sgi.com.model.Recipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link Recipient}.
 */
@Repository
public interface RecipientRepository
    extends JpaRepository<Recipient, Long>, JpaSpecificationExecutor<Recipient> {

  List<Recipient> findByEmailId(Long id);

  Long deleteByEmailId(Long id);
}
