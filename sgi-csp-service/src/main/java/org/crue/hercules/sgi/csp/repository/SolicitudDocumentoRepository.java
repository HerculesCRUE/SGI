package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudDocumento;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SolicitudDocumentoRepository
    extends JpaRepository<SolicitudDocumento, Long>, JpaSpecificationExecutor<SolicitudDocumento> {

  /**
   * Recupera la lista de {@link SolicitudDocumento} de la {@link Solicitud} cuyo
   * {@link TipoDocumento} se corresponde con alguno de los recibidos por
   * parámetro.
   * 
   * @param tiposDocumentoRequeridosSolicitud Listado de {@link TipoDocumento}.
   * @param idSolicitud                       Identificador de la
   *                                          {@link Solicitud}.
   * @return lista de {@link SolicitudDocumento}.
   */
  List<SolicitudDocumento> findAllByTipoDocumentoIdInAndSolicitudId(List<Long> tiposDocumentoRequeridosSolicitud,
      Long idSolicitud);

}
