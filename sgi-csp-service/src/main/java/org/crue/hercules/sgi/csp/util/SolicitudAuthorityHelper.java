package org.crue.hercules.sgi.csp.util;

import org.crue.hercules.sgi.csp.exceptions.SolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToAccessSolicitudException;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.framework.security.core.context.SgiSecurityContextHolder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SolicitudAuthorityHelper extends AuthorityHelper {

  private final SolicitudRepository repository;

  public boolean isUserInvestigador() {
    return hasAuthorityCreateInvestigador() || hasAuthorityDeleteInvestigador() || hasAuthorityEditInvestigador();
  }

  /**
   * Comprueba si el usuario logueado tiene permiso para ver la {@link Solicitud}
   * 
   * @param solicitud la {@link Solicitud}
   * 
   * @throws UserNotAuthorizedToAccessSolicitudException si el usuario no esta
   *                                                     autorizado para ver la
   *                                                     {@link Solicitud}
   */
  public void checkUserHasAuthorityViewSolicitud(Solicitud solicitud)
      throws UserNotAuthorizedToAccessSolicitudException {
    if (!(hasAuthorityViewInvestigador(solicitud) || hasAuthorityViewUnidadGestion(solicitud))) {
      throw new UserNotAuthorizedToAccessSolicitudException();
    }
  }

  /**
   * Comprueba si el usuario logueado tiene los permisos globales de edición, el
   * de la unidad de gestión de la solicitud o si es tiene el permiso de
   * investigador y es el creador de la solicitud.
   * 
   * @param solicitudId identificador de la {@link Solicitud}
   * @return <code>true</code> si tiene el permiso de edición; <code>false</code>
   *         caso contrario.
   */
  public boolean hasPermisosEdicion(Long solicitudId) {
    return hasPermisosEdicion(repository.findById(solicitudId)
        .orElseThrow(() -> new SolicitudNotFoundException(solicitudId)));
  }

  /**
   * Comprueba si el usuario logueado tiene los permisos globales de edición, el
   * de la unidad de gestión de la solicitud o si es tiene el permiso de
   * investigador y es el creador de la solicitud.
   * 
   * @param solicitud La solicitud
   * @return <code>true</code> si tiene el permiso de edición; <code>false</code>
   *         caso contrario.
   */
  public boolean hasPermisosEdicion(Solicitud solicitud) {
    return hasAuthorityEditUnidadGestion(solicitud.getUnidadGestionRef())
        || (hasAuthorityEditInvestigador() && solicitud.getCreadorRef().equals(
            getAuthenticationPersonaRef()));
  }

  public boolean hasAuthorityCreateInvestigador() {
    return SgiSecurityContextHolder.hasAuthorityForAnyUO("CSP-SOL-INV-C");
  }

  public boolean hasAuthorityDeleteInvestigador() {
    return SgiSecurityContextHolder.hasAuthorityForAnyUO("CSP-SOL-INV-BR");
  }

  public boolean hasAuthorityEditInvestigador() {
    return SgiSecurityContextHolder.hasAuthorityForAnyUO("CSP-SOL-INV-ER");
  }

  public boolean hasAuthorityEditUnidadGestion(String unidadGestion) {
    return SgiSecurityContextHolder.hasAuthorityForUO("CSP-SOL-E", unidadGestion);
  }

  public boolean hasAuthorityViewInvestigador(Solicitud solicitud) {
    return SgiSecurityContextHolder.hasAuthorityForAnyUO("CSP-SOL-INV-ER")
        && solicitud.getSolicitanteRef().equals(getAuthenticationPersonaRef());
  }

  public boolean hasAuthorityViewUnidadGestion(Solicitud solicitud) {
    return SgiSecurityContextHolder.hasAuthorityForUO("CSP-SOL-E", solicitud.getUnidadGestionRef())
        || SgiSecurityContextHolder.hasAuthorityForUO("CSP-SOL-V", solicitud.getUnidadGestionRef());
  }

}
