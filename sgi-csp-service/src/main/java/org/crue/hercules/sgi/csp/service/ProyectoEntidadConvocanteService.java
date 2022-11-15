package org.crue.hercules.sgi.csp.service;

import java.util.List;

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
   * Actualiza el listado de {@link ProyectoEntidadConvocante} del
   * {@link Proyecto} con el listado entidadesConvocantes
   * creando, editando o eliminando los elementos segun proceda.
   *
   * @param proyectoId           Id del {@link Proyecto}.
   * @param entidadesConvocantes lista con los nuevos
   *                             {@link ProyectoEntidadConvocante} a guardar.
   * @return la lista de entidades {@link ProyectoEntidadConvocante} persistida.
   */
  List<ProyectoEntidadConvocante> updateEntidadesConvocantesProyecto(Long proyectoId,
      List<ProyectoEntidadConvocante> entidadesConvocantes);

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
   * @param proyectoId           Id del {@link Proyecto}
   * @param entidadRef           Id de la Entidad Convocante
   * @param programaConvocatoria {@link ProyectoEntidadConvocante#programaConvocatoria}
   * @return true si existe la {@link ProyectoEntidadConvocante} y false en caso
   *         contrario
   */
  boolean existsByProyectoIdAndEntidadRefAndProgramaConvocatoria(Long proyectoId, String entidadRef,
      Programa programaConvocatoria);

}