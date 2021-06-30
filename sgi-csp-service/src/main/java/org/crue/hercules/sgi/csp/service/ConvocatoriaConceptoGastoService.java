package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ConvocatoriaConceptoGasto}.
 */

public interface ConvocatoriaConceptoGastoService {

  /**
   * Guarda la entidad {@link ConvocatoriaConceptoGasto}.
   * 
   * @param convocatoriaConceptoGasto la entidad {@link ConvocatoriaConceptoGasto}
   *                                  a guardar.
   * @return ConvocatoriaConceptoGasto la entidad
   *         {@link ConvocatoriaConceptoGasto} persistida.
   */
  ConvocatoriaConceptoGasto create(ConvocatoriaConceptoGasto convocatoriaConceptoGasto);

  /**
   * Actualiza la entidad {@link ConvocatoriaConceptoGasto}.
   * 
   * @param convocatoriaConceptoGastoActualizar la entidad
   *                                            {@link ConvocatoriaConceptoGasto}
   *                                            a guardar.
   * @return ConvocatoriaConceptoGasto la entidad
   *         {@link ConvocatoriaConceptoGasto} persistida.
   */
  ConvocatoriaConceptoGasto update(ConvocatoriaConceptoGasto convocatoriaConceptoGastoActualizar);

  /**
   * Elimina la {@link ConvocatoriaConceptoGasto}.
   *
   * @param id Id del {@link ConvocatoriaConceptoGasto}.
   */
  void delete(Long id);

  /**
   * Obtiene {@link ConvocatoriaConceptoGasto} por su id.
   *
   * @param id el id de la entidad {@link ConvocatoriaConceptoGasto}.
   * @return la entidad {@link ConvocatoriaConceptoGasto}.
   */
  ConvocatoriaConceptoGasto findById(Long id);

  /**
   * Comprueba la existencia del {@link ConvocatoriaConceptoGasto} por id.
   *
   * @param id el id de la entidad {@link ConvocatoriaConceptoGasto}.
   * @return true si existe y false en caso contrario.
   */
  boolean existsById(Long id);

  /**
   * Obtiene todas las entidades {@link ConvocatoriaConceptoGasto} paginadas y
   * filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link ConvocatoriaConceptoGasto} paginadas y
   *         filtradas.
   */
  Page<ConvocatoriaConceptoGasto> findAll(String query, Pageable paging);

  /**
   * Obtiene las {@link ConvocatoriaConceptoGasto} permitidos para una
   * {@link Convocatoria}
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaConceptoGasto} de la
   *         {@link Convocatoria} paginadas.
   */
  Page<ConvocatoriaConceptoGasto> findAllByConvocatoriaAndPermitidoTrue(Long convocatoriaId, Pageable pageable);

  /**
   * Obtiene las {@link ConvocatoriaConceptoGasto} NO permitidos para una
   * {@link Convocatoria}
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaConceptoGasto} de la
   *         {@link Convocatoria} paginadas.
   */
  Page<ConvocatoriaConceptoGasto> findAllByConvocatoriaAndPermitidoFalse(Long convocatoriaId, Pageable pageable);
}
