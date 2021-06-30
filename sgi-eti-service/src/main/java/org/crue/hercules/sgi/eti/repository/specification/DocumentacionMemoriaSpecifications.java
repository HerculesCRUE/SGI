package org.crue.hercules.sgi.eti.repository.specification;

import org.crue.hercules.sgi.eti.model.DocumentacionMemoria;
import org.crue.hercules.sgi.eti.model.DocumentacionMemoria_;
import org.crue.hercules.sgi.eti.model.Formulario_;
import org.crue.hercules.sgi.eti.model.Memoria_;
import org.crue.hercules.sgi.eti.model.TipoDocumento_;
import org.springframework.data.jpa.domain.Specification;

public class DocumentacionMemoriaSpecifications {

  public static Specification<DocumentacionMemoria> memoriaId(Long idMemoria) {
    return (root, query, cb) -> {
      return cb.equal(root.get(DocumentacionMemoria_.memoria).get(Memoria_.id), idMemoria);
    };
  }

  public static Specification<DocumentacionMemoria> tipoDocumentoFormularioId(Long idFormulario) {
    return (root, query, cb) -> {
      return cb.equal(root.get(DocumentacionMemoria_.tipoDocumento).get(TipoDocumento_.formulario).get(Formulario_.id),
          idFormulario);
    };
  }

  public static Specification<DocumentacionMemoria> tipoDocumento(Long idTipoDocumento) {
    return (root, query, cb) -> {
      return cb.equal(root.get(DocumentacionMemoria_.tipoDocumento).get(TipoDocumento_.id), idTipoDocumento);
    };
  }

}