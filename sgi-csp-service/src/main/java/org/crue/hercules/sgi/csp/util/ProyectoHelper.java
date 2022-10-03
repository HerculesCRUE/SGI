package org.crue.hercules.sgi.csp.util;

import java.util.List;

import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToAccessProyectoException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToCreateProyectoException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToModifyProyectoException;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.repository.ProyectoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoResponsableEconomicoRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoEquipoSpecifications;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoResponsableEconomicoSpecifications;
import org.crue.hercules.sgi.framework.security.core.context.SgiSecurityContextHolder;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Primary
@Component
@RequiredArgsConstructor
public class ProyectoHelper extends AuthorityHelper {
  public static final String CSP_PRO_B = "CSP-PRO-B";
  public static final String CSP_PRO_C = "CSP-PRO-C";
  public static final String CSP_PRO_E = "CSP-PRO-E";
  public static final String CSP_PRO_INV_VR = "CSP-PRO-INV-VR";
  public static final String CSP_PRO_MOD_V = "CSP-PRO-MOD-V";
  public static final String CSP_PRO_R = "CSP-PRO-R";
  public static final String CSP_PRO_V = "CSP-PRO-V";

  private final ProyectoRepository repository;
  private final ProyectoEquipoRepository proyectoEquipoRepository;
  private final ProyectoResponsableEconomicoRepository proyectoResponsableEconomicoRepository;

  /**
   * Comprueba que el proyecto pertenece a una unidad de gestión que el usuario
   * actual pueda gestionar.
   * 
   * @param proyecto el {@link Proyecto} sobre el que realizar las comprobaciones
   */
  public void checkCanAccessProyecto(Proyecto proyecto) {
    if (!hasUserAuthorityViewMod()
        && !hasUserAuthorityViewUO(proyecto)
        && !(hasUserAuthorityViewInvestigador()
            && !checkUserPresentInEquipos(proyecto.getId())
            && !checkUserIsResponsableEconomico(proyecto.getId()))) {
      throw new UserNotAuthorizedToAccessProyectoException();
    }
  }

  protected boolean hasUserAuthorityViewInvestigador() {
    return SgiSecurityContextHolder.hasAuthorityForAnyUO(CSP_PRO_INV_VR);
  }

  public boolean hasUserAuthorityViewUO(Proyecto proyecto) {
    return SgiSecurityContextHolder.hasAnyAuthorityForUO(
        new String[] {
            CSP_PRO_V,
            CSP_PRO_E,
            CSP_PRO_MOD_V,
            CSP_PRO_INV_VR
        },
        proyecto.getUnidadGestionRef());
  }

  public void checkCanAccessProyecto(Long proyectoId) {
    checkCanAccessProyecto(
        repository.findById(proyectoId).orElseThrow(() -> new ProyectoNotFoundException(proyectoId)));
  }

  public void checkCanCreateProyecto(Proyecto proyecto) {
    if (!SgiSecurityContextHolder.hasAuthorityForUO(CSP_PRO_C, proyecto.getUnidadGestionRef())) {
      throw new UserNotAuthorizedToCreateProyectoException();
    }
  }

  public void checkUserHasAuthorityModifyProyecto(Long proyectoId) {
    checkUserHasAuthorityModifyProyecto(
        repository.findById(proyectoId).orElseThrow(() -> new ProyectoNotFoundException(proyectoId)));
  }

  public void checkUserHasAuthorityModifyProyecto(Proyecto proyecto) {
    if (!SgiSecurityContextHolder.hasAuthorityForUO(CSP_PRO_E, proyecto.getUnidadGestionRef())) {
      throw new UserNotAuthorizedToModifyProyectoException();
    }
  }

  protected boolean hasUserAuthorityModifyProyecto(Long proyectoId) {
    return hasUserAuthorityModifyProyecto(
        repository.findById(proyectoId).orElseThrow(() -> new ProyectoNotFoundException(proyectoId)));
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

  /**
   * Lista de unidades de gestion para las que el usuario tienen algun permiso de
   * PROYECTO (CSP_PRO_)
   * 
   * @return la lista de unidades de gestion
   */
  public List<String> getUserUOsProyecto() {
    return SgiSecurityContextHolder.getUOsForAnyAuthority(
        new String[] { CSP_PRO_B, CSP_PRO_C, CSP_PRO_E, CSP_PRO_R, CSP_PRO_V });
  }

  private boolean hasUserAuthorityModifyProyecto(Proyecto proyecto) {
    return SgiSecurityContextHolder.hasAuthorityForUO(CSP_PRO_E, proyecto.getUnidadGestionRef());
  }

  private boolean hasUserAuthorityViewMod() {
    return SgiSecurityContextHolder.hasAuthority(CSP_PRO_MOD_V);
  }

}
