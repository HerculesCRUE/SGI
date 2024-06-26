package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SolicitudProyectoEquipoRepository
    extends JpaRepository<SolicitudProyectoEquipo, Long>, JpaSpecificationExecutor<SolicitudProyectoEquipo> {

  /**
   * Recupera los {@link SolicitudProyectoEquipo} asociados a un
   * {@link SolicitudProyecto} cuya persona ref sea la recibida por parámetro.
   * 
   * @param idSolicitudProyecto Id {@link SolicitudProyecto}
   * @param personaRef          persona ref
   * @return listado {@link SolicitudProyectoEquipo}
   */
  List<SolicitudProyectoEquipo> findAllBySolicitudProyectoIdAndPersonaRef(Long idSolicitudProyecto, String personaRef);

  /**
   * Obtiene las {@link SolicitudProyectoEquipo} asociadas a una {@link Solicitud}
   * 
   * @param solicitudProyectoId Identificador de la {@link Solicitud}
   * @return Listado de solicitudes modalidad
   */
  List<SolicitudProyectoEquipo> findAllBySolicitudProyectoId(Long solicitudProyectoId);

  /**
   * Obtiene las {@link SolicitudProyectoEquipo} con RolProyecto con rol principal
   * asociadas a una
   * {@link Solicitud}
   * 
   * @param solicitudProyectoId Identificador de la {@link Solicitud}
   * @return Listado de solicitudes modalidad
   */
  List<SolicitudProyectoEquipo> findAllBySolicitudProyectoIdAndRolProyectoRolPrincipalTrue(Long solicitudProyectoId);

  /**
   * Compruba si existe una {@link SolicitudProyectoEquipo} con el
   * solicitudProyectoId y personaRef indicados
   * 
   * @param solicitudProyectoId id del {@link SolicitudProyecto}.
   * @param personaRef          ref de la persona.
   * @return true si existe false si no existe.
   */
  boolean existsBySolicitudProyectoIdAndPersonaRef(Long solicitudProyectoId, String personaRef);

  boolean existsBySolicitudProyectoId(Long solicitudProyectoId);

}
