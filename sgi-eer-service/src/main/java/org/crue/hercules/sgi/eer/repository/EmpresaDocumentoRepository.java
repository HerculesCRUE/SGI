package org.crue.hercules.sgi.eer.repository;

import org.crue.hercules.sgi.eer.model.EmpresaDocumento;
import org.crue.hercules.sgi.framework.data.jpa.repository.support.SgiRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EmpresaDocumentoRepository
    extends SgiRepository<EmpresaDocumento, Long>, JpaSpecificationExecutor<EmpresaDocumento> {

}
