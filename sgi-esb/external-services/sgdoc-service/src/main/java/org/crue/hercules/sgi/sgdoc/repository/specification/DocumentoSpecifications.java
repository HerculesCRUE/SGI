package org.crue.hercules.sgi.sgdoc.repository.specification;

import java.util.List;

import org.crue.hercules.sgi.sgdoc.model.Documento;
import org.crue.hercules.sgi.sgdoc.model.Documento_;
import org.springframework.data.jpa.domain.Specification;

public class DocumentoSpecifications {

  public static Specification<Documento> byDocumentoRefs(List<String> documentoRefs) {
    return (root, query, cb) -> {
      return root.get(Documento_.documentoRef).in(documentoRefs);
    };
  }

}