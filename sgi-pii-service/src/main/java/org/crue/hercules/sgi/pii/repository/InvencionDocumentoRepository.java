package org.crue.hercules.sgi.pii.repository;

import org.crue.hercules.sgi.pii.model.InvencionDocumento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvencionDocumentoRepository extends JpaRepository<InvencionDocumento, Long> {

  Page<InvencionDocumento> findByInvencionId(Long invencionId, Pageable pageable);

}
