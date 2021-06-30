package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ConvocatoriaFase}.
 */

public interface ConvocatoriaFaseService {

  /**
   * Guarda la entidad {@link ConvocatoriaFase}.
   * 
   * @param convocatoriaFase la entidad {@link ConvocatoriaFase} a guardar.
   * @return ConvocatoriaFase la entidad {@link ConvocatoriaFase} persistida.
   */
  ConvocatoriaFase create(ConvocatoriaFase convocatoriaFase);

  /**
   * Actualiza la entidad {@link ConvocatoriaFase}.
   * 
   * @param convocatoriaFaseActualizar la entidad {@link ConvocatoriaFase} a
   *                                   guardar.
   * @return ConvocatoriaFase la entidad {@link ConvocatoriaFase} persistida.
   */
  ConvocatoriaFase update(ConvocatoriaFase convocatoriaFaseActualizar);

  /**
   * Elimina la {@link ConvocatoriaFase}.
   *
   * @param id Id del {@link ConvocatoriaFase}.
   */
  void delete(Long id);

  /**
   * Obtiene {@link ConvocatoriaFase} por su id.
   *
   * @param id el id de la entidad {@link ConvocatoriaFase}.
   * @return la entidad {@link ConvocatoriaFase}.
   */
  ConvocatoriaFase findById(Long id);

  /**
   * Obtiene las {@link ConvocatoriaFase} para una {@link Convocatoria}.
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param query          la información del filtro.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaFase} de la
   *         {@link Convocatoria} paginadas.
   */
  Page<ConvocatoriaFase> findAllByConvocatoria(Long convocatoriaId, String query, Pageable pageable);

  /**
   * Comprueba si existe algún {@link ConvocatoriaFase} relacionado con el Id de
   * Convocatoria recibido
   * 
   * @param convocatoriaId Id de Convocatoria
   * @return <code>true</code> Si existe algúna relación, <code>false</code> en
   *         cualquier otro caso.
   */
  boolean existsByConvocatoriaId(Long convocatoriaId);
}
