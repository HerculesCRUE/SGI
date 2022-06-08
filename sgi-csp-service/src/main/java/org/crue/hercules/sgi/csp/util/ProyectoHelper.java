package org.crue.hercules.sgi.csp.util;

import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToAccessProyectoException;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.repository.ProyectoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoResponsableEconomicoRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoEquipoSpecifications;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoResponsableEconomicoSpecifications;
import org.crue.hercules.sgi.framework.security.core.context.SgiSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProyectoHelper extends AuthorityHelper {

  private final ProyectoEquipoRepository proyectoEquipoRepository;
  private final ProyectoResponsableEconomicoRepository proyectoResponsableEconomicoRepository;

  /**
   * Comprueba que el proyecto pertenece a una unidad de gestión que el usuario
   * actual pueda gestionar.
   * 
   * @param proyecto el {@link Proyecto} sobre el que realizar las comprobaciones
   */
  public void checkCanRead(Proyecto proyecto) {
    // TODO buscar una manera de obtener proyectos de una invencion eliminando
    // "CSP-PRO-MOD-V".
    Assert.isTrue(
        SgiSecurityContextHolder.hasAnyAuthorityForUO(
            new String[] { "CSP-PRO-V", "CSP-PRO-E", "CSP-PRO-MOD-V", "CSP-PRO-INV-VR" },
            proyecto.getUnidadGestionRef()),
        "El proyecto no pertenece a una Unidad de Gestión gestionable por el usuario");
  }

  public boolean hasUserAuthorityInvestigador() {
    return SgiSecurityContextHolder.hasAuthorityForAnyUO("CSP-PRO-INV-VR");
  }

  public void checkCanAccessProyecto(Long proyectoId) {
    if (hasUserAuthorityInvestigador() && !checkUserPresentInEquipos(proyectoId)
        && !checkUserIsResponsableEconomico(proyectoId)) {
      throw new UserNotAuthorizedToAccessProyectoException();
    }
  }

  public boolean checkUserIsResponsableEconomico(Long proyectoId) {
    Long numeroResponsableEconomico = this.proyectoResponsableEconomicoRepository
        .count(ProyectoResponsableEconomicoSpecifications.byProyectoId(proyectoId)
            .and(ProyectoResponsableEconomicoSpecifications.byPersonaRef(getAuthenticationPersonaRef())));
    return numeroResponsableEconomico > 0;
  }

  public boolean checkUserPresentInEquipos(Long proyectoId) {
    Long numeroProyectoEquipo = this.proyectoEquipoRepository
        .count(ProyectoEquipoSpecifications.byProyectoId(proyectoId)
            .and(ProyectoEquipoSpecifications.byPersonaRef(getAuthenticationPersonaRef())
                .and(ProyectoEquipoSpecifications.byRolPrincipal(true))));
    return numeroProyectoEquipo > 0;
  }

  public boolean checkIfUserIsInvestigadorPrincipal(Long proyectoId) {
    return this.proyectoEquipoRepository.existsByProyectoIdAndPersonaRefAndRolProyectoRolPrincipalTrue(proyectoId,
        getAuthenticationPersonaRef());
  }
}
