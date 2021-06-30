package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ConvocatoriaEntidadConvocante}.
 */
public interface ConvocatoriaEntidadConvocanteService {

  /**
   * Guardar un nuevo {@link ConvocatoriaEntidadConvocante}.
   *
   * @param convocatoriaEntidadConvocante la entidad
   *                                      {@link ConvocatoriaEntidadConvocante} a
   *                                      guardar.
   * @return la entidad {@link ConvocatoriaEntidadConvocante} persistida.
   */
  ConvocatoriaEntidadConvocante create(ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante);

  /**
   * Actualizar {@link ConvocatoriaEntidadConvocante}.
   *
   * @param convocatoriaEntidadConvocanteActualizar la entidad
   *                                                {@link ConvocatoriaEntidadConvocante}
   *                                                a actualizar.
   * @return la entidad {@link ConvocatoriaEntidadConvocante} persistida.
   */
  ConvocatoriaEntidadConvocante update(ConvocatoriaEntidadConvocante convocatoriaEntidadConvocanteActualizar);

  /**
   * Elimina el {@link ConvocatoriaEntidadConvocante}.
   *
   * @param id Id del {@link ConvocatoriaEntidadConvocante}.
   */
  void delete(Long id);

  /**
   * Obtiene {@link ConvocatoriaEntidadConvocante} por su id.
   *
   * @param id el id de la entidad {@link ConvocatoriaEntidadConvocante}.
   * @return la entidad {@link ConvocatoriaEntidadConvocante}.
   */
  ConvocatoriaEntidadConvocante findById(Long id);

  /**
   * Obtiene las {@link ConvocatoriaEntidadConvocante} para una
   * {@link Convocatoria}.
   *
   * @param idConvocatoria el id de la {@link Convocatoria}.
   * @param query          la información del filtro.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaEntidadConvocante} de la
   *         {@link Convocatoria} paginadas.
   */
  Page<ConvocatoriaEntidadConvocante> findAllByConvocatoria(Long idConvocatoria, String query, Pageable pageable);

}