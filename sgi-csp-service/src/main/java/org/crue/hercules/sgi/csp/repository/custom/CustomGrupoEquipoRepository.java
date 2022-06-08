package org.crue.hercules.sgi.csp.repository.custom;

import java.time.Instant;
import java.util.List;

import org.crue.hercules.sgi.csp.dto.GrupoEquipoDto;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoEquipo;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.springframework.stereotype.Component;

/**
 * Custom repository para {@link GrupoEquipo}.
 */
@Component
public interface CustomGrupoEquipoRepository {

  /**
   * {@link GrupoEquipo} que son investigador o investigadores principales con
   * mayor porcentaje de particitacion del {@link Grupo} con el id indicado.
   * 
   * Son investiador principales los {@link GrupoEquipo} que a fecha actual
   * tiene el {@link RolProyecto} con el flag {@link RolProyecto#rolPrincipal} a
   * <code>true</code>. En caso de existir mas de un {@link GrupoEquipo}, se
   * recupera el que tenga el mayor porcentaje de dedicación al grupo
   * ({@link GrupoEquipo#participacion}) y en caso de que varios tengan la misma
   * participacion se devuelven todos los que coincidan.
   * 
   * @param grupoId identificador del {@link Grupo}.
   * @param fecha   fecha en la que se busca el investigador principal.
   * @return la lista de personaRef de los investigadores principales del
   *         {@link Grupo} en el momento actual.
   */
  List<String> findPersonaRefInvestigadoresPrincipalesWithMaxParticipacion(Long grupoId, Instant fecha);

  /**
   * {@link GrupoEquipo} que son investigador o investigadores principales del
   * {@link Grupo} con el id indicado.
   * 
   * Son investiador principales los {@link GrupoEquipo} que a fecha actual
   * tiene el {@link RolProyecto} con el flag {@link RolProyecto#rolPrincipal} a
   * <code>true</code>.
   * 
   * @param grupoId identificador del {@link Grupo}.
   * @param fecha   fecha en la que se busca el investigador principal.
   * @return la lista de personaRef de los investigadores principales del
   *         {@link Grupo} en el momento actual.
   */
  List<String> findPersonaRefInvestigadoresPrincipales(Long grupoId, Instant fecha);

  /**
   * Comprueba si personaRef pertenece a un grupo de investigación con un rol con
   * el flag de baremable a true a fecha 31 de diciembre del año que se esta
   * baremando y el grupo al que pertenecen los autores (tabla Grupo) este activo
   * y el campo "Grupo especial de investigación" a "No" el 31 de diciembre del
   * año que se esta baremando
   *
   * @param personaRef      personaRef
   * @param fechaBaremacion fecha de baremación
   * @return true/false
   */
  boolean isPersonaBaremable(String personaRef, Instant fechaBaremacion);

  /**
   * Devuelve una lista de {@link GrupoEquipoDto} pertenecientes a un determinado
   * grupo y que estén a 31 de diciembre del año de baremación
   *
   * @param grupoRef        grupoRef
   * @param fechaBaremacion fecha de baremación
   * 
   * @return Lista de {@link GrupoEquipoDto}
   */
  List<GrupoEquipoDto> findByGrupoIdAndAnio(Long grupoRef, Instant fechaBaremacion);

  /**
   * Lista de ids de {@link GrupoEquipo} cuyo personaRef está dentro de la fecha
   * de baremación
   *
   * @param personaRef            personaRef
   * @param fechaInicioBaremacion fecha inicio de baremación
   * @param fechaFinBaremacion    fecha fin de baremación
   * @return Lista de ids de {@link GrupoEquipo}
   */
  List<Long> findGrupoEquipoByPersonaRefAndFechaBaremacion(String personaRef, Instant fechaInicioBaremacion,
      Instant fechaFinBaremacion);

  /**
   * Devuelve una lista de personaRef de los {@link GrupoEquipo} que cumplan la
   * specification.
   *
   * @param personaRef persona ref de {@link GrupoEquipo}
   * @param fecha      fecha para la que se hace la comprobracion
   * @return la lista de personaRefs
   */
  List<String> findMiembrosEquipoUsuario(String personaRef, Instant fecha);

}
