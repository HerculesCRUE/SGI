package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud_;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.DocumentoRequeridoSolicitud;
import org.crue.hercules.sgi.csp.model.DocumentoRequeridoSolicitud_;
import org.crue.hercules.sgi.csp.model.Convocatoria_;
import org.springframework.data.jpa.domain.Specification;

public class DocumentoRequeridoSolicitudSpecifications {

  /**
   * {@link DocumentoRequeridoSolicitud} de la {@link Convocatoria} con el id
   * indicado.
   * 
   * @param id identificador de la {@link Convocatoria}.
   * @return specification para obtener los {@link DocumentoRequeridoSolicitud} de
   *         la {@link Convocatoria} con el id indicado.
   */
  public static Specification<DocumentoRequeridoSolicitud> byConvocatoriaId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(DocumentoRequeridoSolicitud_.configuracionSolicitud)
          .get(ConfiguracionSolicitud_.convocatoria).get(Convocatoria_.id), id);
    };
  }

}