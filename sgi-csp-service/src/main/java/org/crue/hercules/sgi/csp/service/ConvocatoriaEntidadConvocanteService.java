package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ConvocatoriaEntidadConvocante}.
 */
public interface ConvocatoriaEntidadConvocanteService {

  /**
   * Actualiza el listado de {@link ConvocatoriaEntidadConvocante} de la
   * {@link Convocatoria} con el listado entidadesConvocantes
   * creando, editando o eliminando los elementos segun proceda.
   *
   * @param convocatoriaId       Id de la {@link Convocatoria}.
   * @param entidadesConvocantes lista con los nuevos
   *                             {@link ConvocatoriaEntidadConvocante} a guardar.
   * @return la lista de entidades {@link ConvocatoriaEntidadConvocante}
   *         persistida.
   */
  List<ConvocatoriaEntidadConvocante> updateEntidadesConvocantesConvocatoria(Long convocatoriaId,
      List<ConvocatoriaEntidadConvocante> entidadesConvocantes);

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

  /**
   * Obtiene las {@link ConvocatoriaEntidadConvocante} de la {@link Convocatoria}
   * para una {@link Solicitud} si el usuario que realiza la peticion es el
   * solicitante o el tutor de la {@link Solicitud}.
   *
   * @param solicitudId el id de la {@link Convocatoria}.
   * @param pageable    la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaEntidadConvocante} de la
   *         {@link Convocatoria} paginadas.
   */
  Page<ConvocatoriaEntidadConvocante> findAllBySolicitudAndUserIsSolicitanteOrTutor(Long solicitudId,
      Pageable pageable);

}