package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadConvocante;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ProyectoEntidadConvocante}.
 */
public interface ProyectoEntidadConvocanteService {

  /**
   * Guardar un nuevo {@link ProyectoEntidadConvocante}.
   *
   * @param proyectoEntidadConvocante la entidad {@link ProyectoEntidadConvocante}
   *                                  a guardar.
   * @return la entidad {@link ProyectoEntidadConvocante} persistida.
   */
  ProyectoEntidadConvocante create(ProyectoEntidadConvocante proyectoEntidadConvocante);

  /**
   * Establece el {@link Programa} de {@link ProyectoEntidadConvocante}.
   *
   * @param idProyectoEntidadConvocante el id de la entidad
   *                                    {@link ProyectoEntidadConvocante} a
   *                                    actualizar.
   * @param programa                    el {@link Programa} a fijar.
   * @return la entidad {@link ProyectoEntidadConvocante} persistida.
   */
  ProyectoEntidadConvocante setPrograma(Long idProyectoEntidadConvocante, Programa programa);

  /**
   * Elimina el {@link ProyectoEntidadConvocante}.
   *
   * @param id Id del {@link ProyectoEntidadConvocante}.
   */
  void delete(Long id);

  /**
   * Obtiene las {@link ProyectoEntidadConvocante} para una {@link Proyecto}.
   *
   * @param idProyecto el id de la {@link Proyecto}.
   * @param query      la información del filtro.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link ProyectoEntidadConvocante} de la
   *         {@link Proyecto} paginadas.
   */
  Page<ProyectoEntidadConvocante> findAllByProyecto(Long idProyecto, String query, Pageable pageable);

  /**
   * Busca un {@link ProyectoEntidadConvocante} por su {@link Proyecto} y
   * entidadRef.
   * 
   * @param proyectoId Id del {@link Proyecto}
   * @param entidadRef Id de la Entidad Convocante
   * @return true si existe la {@link ProyectoEntidadConvocante} y false en caso
   *         contrario
   */
  boolean existsByProyectoIdAndEntidadRef(Long proyectoId, String entidadRef);

  /**
   * Actualiza la entidad {@link ProyectoEntidadConvocante}.
   *
   * @param proyectoEntidadConvocante la entidad {@link ProyectoEntidadConvocante}
   *                                  a guardar.
   * @return la entidad {@link ProyectoEntidadConvocante} persistida.
   */
  ProyectoEntidadConvocante update(ProyectoEntidadConvocante proyectoEntidadConvocante);

  /**
   * Devuelve un {@link ProyectoEntidadConvocante} por su {@link Proyecto} y
   * entidadRef.
   * 
   * @param proyectoId Id del {@link Proyecto}
   * @param entidadRef Id de la Entidad Convocante
   * @return true si existe la {@link ProyectoEntidadConvocante} y false en caso
   *         contrario
   */
  ProyectoEntidadConvocante findByProyectoIdAndEntidadRef(Long proyectoId, String entidadRef);

}