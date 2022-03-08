package org.crue.hercules.sgi.com.repository;

import java.util.List;

import org.crue.hercules.sgi.com.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link Attachment}.
 */
@Repository
public interface AttachmentRepository
    extends JpaRepository<Attachment, Long>, JpaSpecificationExecutor<Attachment> {

  List<Attachment> findByEmailId(Long id);

  Long deleteByEmailId(Long id);
}
