package org.crue.hercules.sgi.com.repository;

import org.crue.hercules.sgi.com.model.EmailAttachmentDeferrable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link EmailAttachmentDeferrable}.
 */
@Repository
public interface EmailAttachmentDeferrableRepository
    extends JpaRepository<EmailAttachmentDeferrable, Long>, JpaSpecificationExecutor<EmailAttachmentDeferrable> {

  long deleteAllById(Long id);
}
