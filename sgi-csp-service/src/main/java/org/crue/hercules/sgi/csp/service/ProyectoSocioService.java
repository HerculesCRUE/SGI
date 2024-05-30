package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioEquipo;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacionDocumento;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoPago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Servicio interfaz para la gestión de {@link ProyectoSocioService}.
 */
public interface ProyectoSocioService {

  /**
   * Guarda la entidad {@link ProyectoSocio}.
   * 
   * @param proyectoSocio la entidad {@link ProyectoSocioService} a guardar.
   * @return la entidad {@link ProyectoSocio} persistida.
   */
  ProyectoSocio create(ProyectoSocio proyectoSocio);

  /**
   * Actualiza los datos del {@link ProyectoSocio}.
   * 
   * @param proyectoSocio {@link ProyectoSocio} con los datos actualizados.
   * @return {@link ProyectoSocio} actualizado.
   */
  ProyectoSocio update(final ProyectoSocio proyectoSocio);

  /**
   * Elimina el {@link ProyectoSocio}.
   *
   * @param id Id del {@link ProyectoSocio}.
   */
  void delete(Long id);

  /**
   * Comprueba la existencia del {@link ProyectoSocio} por id.
   *
   * @param id el id de la entidad {@link ProyectoSocio}.
   * @return true si existe y false en caso contrario.
   */
  boolean existsById(Long id);

  /**
   * Obtiene una entidad {@link ProyectoSocio} por id.
   * 
   * @param id Identificador de la entidad {@link ProyectoSocio}.
   * @return la entidad {@link ProyectoSocio}.
   */
  ProyectoSocio findById(final Long id);

  /**
   * Obtiene todas las entidades {@link ProyectoSocio} paginadas y filtradas para
   * un {@link Proyecto}.
   *
   * @param proyectoId el id de la {@link Proyecto}.
   * @param query      información del filtro.
   * @param paging     información de paginación.
   * @return el listado de entidades {@link ProyectoSocio} del {@link Proyecto}
   *         paginadas y filtradas.
   */
  Page<ProyectoSocio> findAllByProyecto(Long proyectoId, String query, Pageable paging);

  /**
   * Obtiene todas las entidades {@link ProyectoSocio} para un {@link Proyecto}.
   *
   * @param proyectoId el id de la {@link Proyecto}.
   * @return el listado de entidades {@link ProyectoSocio} del {@link Proyecto}.
   */
  List<ProyectoSocio> findAllByProyecto(Long proyectoId);

  /**
   * Comprueba si existe algun {@link ProyectoSocio} que tenga un rol con el flag
   * coordinador a true para el proyecto.
   * 
   * @param proyectoId Identificador del {@link Proyecto}.
   * @return true si el proyecto tiene algun socio coordinador o false en caso
   *         contrario.
   */
  boolean existsProyectoSocioCoordinador(Long proyectoId);

  /**
   * Indica si {@link ProyectoSocio} tiene {@link ProyectoSocioEquipo},
   * {@link ProyectoSocioPeriodoPago},
   * {@link ProyectoSocioPeriodoJustificacionDocumento} y/o
   * {@link ProyectoSocioPeriodoJustificacion} relacionadas.
   *
   * @param id Id de la {@link Proyecto}.
   * @return True si tiene {@link ProyectoSocioEquipo},
   *         {@link ProyectoSocioPeriodoPago},
   *         {@link ProyectoSocioPeriodoJustificacionDocumento} y/o
   *         {@link ProyectoSocioPeriodoJustificacion} relacionadas. En caso
   *         contrario false
   */
  Boolean vinculaciones(Long id);

  /**
   * Comprueba si hay algún objeto ProyectoSocio con la propiedad coordinador a
   * true en las especificaciones de su rol
   * 
   * @param proyectoId Id del {@link Proyecto}.
   * @return True si hay algún objeto ProyectoSocio con la propiedad coordinador a
   *         true en las especificaciones de su rol. En caso contrario false
   */
  boolean hasAnyProyectoSocioWithRolCoordinador(Long proyectoId);

  /**
   * Comprueba si hay algún socio asignado al proyecto
   * 
   * @param proyectoId Id del {@link Proyecto}.
   * @return True si hay algún socio asignado al proyecto. En caso contrario false
   */
  boolean hasAnyProyectoSocioWithProyectoId(Long proyectoId);

  boolean existsProyectoSocioPeriodoPagoByProyectoSocioId(Long proyectoId);

  boolean existsProyectoSocioPeriodoJustificacionByProyectoSocioId(Long proyectoId);

  /**
   * Comprueba si alguno de los {@link ProyectoSocio} del {@link Proyecto}
   * tienen fechas
   * 
   * @param proyectoId el id del {@link Proyecto}.
   * @return true si existen y false en caso contrario.
   */
  boolean proyectoHasSociosWithDates(Long proyectoId);

  /**
   * Comprueba si el rango de fechas del socio se solapa con alguno de los rangos
   * de ese mismo socio en el proyecto.
   * 
   * @param proyectoSocio un {@link ProyectoSocio}.
   * @return true si se solapa o false si no hay solapamiento.
   */
  boolean isRangoFechasSolapado(ProyectoSocio proyectoSocio);

}
