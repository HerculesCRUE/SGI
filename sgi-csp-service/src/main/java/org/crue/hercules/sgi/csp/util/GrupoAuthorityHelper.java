package org.crue.hercules.sgi.csp.util;

import java.time.Instant;

import org.crue.hercules.sgi.csp.config.SgiConfigProperties;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToAccessGrupoException;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoEquipo;
import org.crue.hercules.sgi.csp.model.GrupoPersonaAutorizada;
import org.crue.hercules.sgi.csp.model.GrupoResponsableEconomico;
import org.crue.hercules.sgi.csp.repository.GrupoRepository;
import org.crue.hercules.sgi.csp.repository.specification.GrupoSpecifications;
import org.crue.hercules.sgi.framework.security.core.context.SgiSecurityContextHolder;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Primary
@Component
@RequiredArgsConstructor
public class GrupoAuthorityHelper extends AuthorityHelper {

  private final SgiConfigProperties sgiConfigProperties;
  private final GrupoRepository repository;

  public boolean isUserInvestigador() {
    return hasAuthorityViewInvestigador();
  }

  /**
   * Comprueba si el usuario logueado tiene permiso para ver el {@link Grupo}
   * 
   * @param grupoId Identifiacdor del {@link Grupo}
   * @throws UserNotAuthorizedToAccessGrupoException si el usuario no esta
   *                                                 autorizado para ver el
   *                                                 {@link Grupo}
   */
  public void checkUserHasAuthorityViewGrupo(Long grupoId) throws UserNotAuthorizedToAccessGrupoException {
    if (!((hasAuthorityViewInvestigador() && isUserInGrupo(grupoId)) || hasAuthorityViewUnidadGestion()
        || isClientUser())) {
      throw new UserNotAuthorizedToAccessGrupoException();
    }
  }

  /**
   * Specification para filtrar los grupos a los que un usuario de tipo
   * investigador puede acceder.
   * 
   * Los investigadores pueden ver los grupos en los que forman parte del equipo
   * ({@link GrupoEquipo}) o es una persona autorizada
   * ({@link GrupoPersonaAutorizada}) o un responsable economico
   * ({@link GrupoResponsableEconomico}) en la fecha actual.
   * 
   * @return Specifications cuando el usuario es un investigador
   */
  public Specification<Grupo> getSpecificationsUserInvestigadorGruposCanView() {
    String personaRef = getAuthenticationPersonaRef();
    Instant fechaActual = Instant.now().atZone(this.sgiConfigProperties.getTimeZone().toZoneId()).toInstant();

    return GrupoSpecifications.byPersonaInGrupoEquipo(personaRef)
        .or(GrupoSpecifications.byResponsableEconomico(personaRef, fechaActual))
        .or(GrupoSpecifications.byPersonaAutorizada(personaRef, fechaActual));
  }

  public boolean hasAuthorityViewInvestigador() {
    return SgiSecurityContextHolder.hasAuthorityForAnyUO("CSP-GIN-INV-VR");
  }

  public boolean hasAuthorityViewUnidadGestion() {
    return SgiSecurityContextHolder.hasAuthorityForAnyUO("CSP-GIN-E")
        || SgiSecurityContextHolder.hasAuthorityForAnyUO("CSP-GIN-V");
  }

  public boolean hasAuthorityEditUnidadGestion() {
    return SgiSecurityContextHolder.hasAuthorityForAnyUO("CSP-GIN-E");
  }

  /**
   * Comprueba si el usuario actual pertenece al {@link Grupo}
   * 
   * @param grupoId Iddentifiacdor del {@link Grupo}
   * @return <code>true</code> Si pertenece, <code>false</code> en cualquier otro
   *         caso.
   */
  private boolean isUserInGrupo(Long grupoId) {
    Specification<Grupo> specs = GrupoSpecifications.byId(grupoId).and(
        getSpecificationsUserInvestigadorGruposCanView());

    return repository.count(specs) > 0;
  }

}
