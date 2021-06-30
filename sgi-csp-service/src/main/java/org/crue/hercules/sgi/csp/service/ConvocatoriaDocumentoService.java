package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.ConvocatoriaDocumento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ConvocatoriaDocumento}.
 */
public interface ConvocatoriaDocumentoService {

  /**
   * Guardar un nuevo {@link ConvocatoriaDocumento}.
   *
   * @param convocatoriaDocumento la entidad {@link ConvocatoriaDocumento} a
   *                              guardar.
   * @return la entidad {@link ConvocatoriaDocumento} persistida.
   */
  ConvocatoriaDocumento create(ConvocatoriaDocumento convocatoriaDocumento);

  /**
   * Actualizar {@link ConvocatoriaDocumento}.
   *
   * @param convocatoriaDocumentoActualizar la entidad
   *                                        {@link ConvocatoriaDocumento} a
   *                                        actualizar.
   * @return la entidad {@link ConvocatoriaDocumento} persistida.
   */
  ConvocatoriaDocumento update(ConvocatoriaDocumento convocatoriaDocumentoActualizar);

  /**
   * Elimina el {@link ConvocatoriaDocumento}.
   *
   * @param id Id del {@link ConvocatoriaDocumento}.
   */
  void delete(Long id);

  /**
   * Obtiene {@link ConvocatoriaDocumento} por su id.
   *
   * @param id el id de la entidad {@link ConvocatoriaDocumento}.
   * @return la entidad {@link ConvocatoriaDocumento}.
   */
  ConvocatoriaDocumento findById(Long id);

  /**
   * Obtener todas las entidades {@link ConvocatoriaDocumento} paginadas y/o
   * filtradas.
   * 
   * @param id     id de la convocatoria
   * @param query  la información del filtro.
   * @param paging la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaDocumento} paginadas y/o
   *         filtradas.
   */

  Page<ConvocatoriaDocumento> findAllByConvocatoria(Long id, String query, Pageable paging);

  /**
   * Comprueba si existe algún {@link ConvocatoriaDocumento} relacionado con el Id
   * de Convocatoria recibido
   * 
   * @param convocatoriaId Id de Convocatoria
   * @return <code>true</code> Si existe algúna relación, <code>false</code> en
   *         cualquier otro caso.
   */
  boolean existsByConvocatoriaId(Long convocatoriaId);
}
