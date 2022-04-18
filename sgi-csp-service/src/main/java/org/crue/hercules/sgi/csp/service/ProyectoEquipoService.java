package org.crue.hercules.sgi.csp.service;

import java.time.Instant;
import java.util.List;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Servicio interfaz para la gestión de {@link ProyectoSocioService}.
 */
public interface ProyectoEquipoService {

  /**
   * Actualiza el listado de {@link ProyectoEquipo} de la {@link Proyecto} con el
   * listado convocatoriaPeriodoJustificaciones añadiendo, editando o eliminando
   * los elementos segun proceda.
   *
   * @param proyectoId      Id de la {@link Proyecto}.
   * @param proyectoEquipos lista con los nuevos {@link ProyectoEquipo} a guardar.
   * @return la entidad {@link ProyectoEquipo} persistida.
   */
  List<ProyectoEquipo> update(Long proyectoId, List<ProyectoEquipo> proyectoEquipos);

  /**
   * Obtiene una entidad {@link ProyectoEquipo} por id.
   * 
   * @param id Identificador de la entidad {@link ProyectoEquipo}.
   * @return la entidad {@link ProyectoEquipo}.
   */
  ProyectoEquipo findById(final Long id);

  /**
   * Obtiene todas las entidades {@link ProyectoEquipo} paginadas y filtradas para
   * un {@link Proyecto}.
   *
   * @param proyectoId el id de la {@link Proyecto}.
   * @param query      información del filtro.
   * @param paging     información de paginación.
   * @return el listado de entidades {@link ProyectoEquipo} del {@link Proyecto}
   *         paginadas y filtradas.
   */
  Page<ProyectoEquipo> findAllByProyecto(Long proyectoId, String query, Pageable paging);

  /**
   * Devuelve un listado de {@link ProyectoEquipo} asociados a un {@link Proyecto}
   * y una fecha de fin
   * 
   * @param proyectoId Identificador de {@link Proyecto}.
   * @param fechaFin   Fecha de fin del miembro de equipo
   * @return listado de {@link ProyectoEquipo}.
   */
  List<ProyectoEquipo> findAllByProyectoIdAndFechaFin(Long proyectoId, Instant fechaFin);

  /**
   * Devuelve un listado de {@link ProyectoEquipo} asociados a un {@link Proyecto}
   * y una fecha de fin mayor a la indicada
   * 
   * @param proyectoId Identificador de {@link Proyecto}.
   * @param fechaFin   Fecha de fin del miembro de equipo
   * @return listado de {@link ProyectoEquipo}.
   */
  List<ProyectoEquipo> findAllByProyectoIdAndFechaFinGreaterThan(Long proyectoId, Instant fechaFin);

  /**
   * Obtiene los {@link ProyectoEquipo} para una {@link Proyecto}.
   *
   * @param proyectoId el id de la {@link Proyecto}.
   * @return la lista de entidades {@link ProyectoEquipo} del {@link Proyecto}
   * 
   */
  List<ProyectoEquipo> findAllByProyectoId(Long proyectoId);

  /**
   * Devuelve una lista filtrada de investigadores principales del
   * {@link Proyecto} en el momento actual.
   *
   * Son investiador principales los {@link ProyectoEquipo} que a fecha actual
   * tiene el {@link RolProyecto} con el flag {@link RolProyecto#rolPrincipal} a
   * <code>true</code>.
   * 
   * @param proyectoId Identificador del {@link Proyecto}.
   * @return la lista de personaRef de los investigadores principales del
   *         {@link Proyecto} en el momento actual.
   */
  public List<String> findPersonaRefInvestigadoresPrincipales(Long proyectoId);

}
