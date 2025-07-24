package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.ActaDocumento;
import org.crue.hercules.sgi.eti.model.ActaDocumentoId;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ActaDocumento}.
 */
@Repository
public interface ActaDocumentoRepository
    extends JpaRepository<ActaDocumento, ActaDocumentoId>, JpaSpecificationExecutor<ActaDocumento> {

  ActaDocumento findByActaIdAndLang(Long idActa, Language language);

  boolean existsByActaIdAndLang(Long idActa, Language language);

}