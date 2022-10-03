package org.crue.hercules.sgi.csp.util;

import java.util.Objects;

import org.crue.hercules.sgi.csp.enums.FormularioSolicitud;
import org.crue.hercules.sgi.csp.exceptions.SolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToAccessSolicitudException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToChangeEstadoSolicitudException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToModifySolicitudException;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud.Estado;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudSpecifications;
import org.crue.hercules.sgi.framework.security.core.context.SgiSecurityContextHolder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SolicitudAuthorityHelper extends AuthorityHelper {
  public static final String CSP_SOL_E = "CSP-SOL-E";
  public static final String CSP_SOL_INV_BR = "CSP-SOL-INV-BR";
  public static final String CSP_SOL_INV_C = "CSP-SOL-INV-C";
  public static final String CSP_SOL_INV_ER = "CSP-SOL-INV-ER";
  public static final String CSP_SOL_V = "CSP-SOL-V";

  private final SolicitudRepository repository;

  public boolean isUserInvestigador() {
    return hasAuthorityCreateInvestigador() || hasAuthorityDeleteInvestigador() || hasAuthorityEditInvestigador();
  }

  /**
   * Comprueba si el usuario logueado tiene permiso para ver la {@link Solicitud}
   * 
   * @param solicitudId Identificador de la {@link Solicitud}
   * 
   * @throws UserNotAuthorizedToAccessSolicitudException si el usuario no esta
   *                                                     autorizado para ver la
   *                                                     {@link Solicitud}
   */
  public void checkUserHasAuthorityViewSolicitud(Long solicitudId)
      throws UserNotAuthorizedToAccessSolicitudException {
    checkUserHasAuthorityViewSolicitud(repository.findById(solicitudId)
        .orElseThrow(() -> new SolicitudNotFoundException(solicitudId)));
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
    if (!(hasAuthorityViewInvestigador(solicitud) || hasAuthorityViewUnidadGestion(solicitud)
        || hasAuthorityViewTutor(solicitud))) {
      throw new UserNotAuthorizedToAccessSolicitudException();
    }
  }

  /**
   * Comprueba si el usuario logueado tiene permiso para modificar la
   * {@link Solicitud}
   * 
   * @param solicitudId Identificador de la {@link Solicitud}
   * 
   * @throws UserNotAuthorizedToModifySolicitudException si el usuario no esta
   *                                                     autorizado para modificar
   *                                                     la {@link Solicitud}
   */
  public void checkUserHasAuthorityModifySolicitud(Long solicitudId)
      throws UserNotAuthorizedToModifySolicitudException {
    checkUserHasAuthorityModifySolicitud(repository.findById(solicitudId)
        .orElseThrow(() -> new SolicitudNotFoundException(solicitudId)));
  }

  /**
   * Comprueba si el usuario logueado tiene permiso para modificar la
   * {@link Solicitud}
   * 
   * @param solicitud la {@link Solicitud}
   * 
   * @throws UserNotAuthorizedToModifySolicitudException si el usuario no esta
   *                                                     autorizado para modificar
   *                                                     la {@link Solicitud}
   */
  public void checkUserHasAuthorityModifySolicitud(Solicitud solicitud)
      throws UserNotAuthorizedToModifySolicitudException {
    if (!hasPermisosEdicion(solicitud) || solicitud.getActivo().equals(Boolean.FALSE)) {
      throw new UserNotAuthorizedToModifySolicitudException();
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
        || hasAuthorityEditInvestigador(solicitud);
  }

  /**
   * Comprueba si el usuario logueado tiene los permisos globales de edición, el
   * de la unidad de gestión de la solicitud o si es tiene el permiso de
   * investigador y es el creador de la solicitud.
   * 
   * @param solicitud       La {@link Solicitud}
   * @param estadoSolicitud El {@link EstadoSolicitud}
   */
  public void checkUserHasAuthorityModifyEstadoSolicitud(Solicitud solicitud, EstadoSolicitud estadoSolicitud) {

    boolean hasAuthority = hasAuthorityEditUnidadGestion(solicitud.getUnidadGestionRef());

    if (!hasAuthority && hasAuthorityEditInvestigador(solicitud)) {
      hasAuthority = validateCambioEstadoInvestigador(solicitud.getEstado().getEstado(), estadoSolicitud.getEstado());
    }

    if (!hasAuthority && solicitud.getFormularioSolicitud().equals(FormularioSolicitud.RRHH)
        && hasAuthorityViewTutor(solicitud)) {
      hasAuthority = validateCambioEstadoTutor(solicitud.getEstado().getEstado(), estadoSolicitud.getEstado());
    }

    if (!hasAuthority) {
      throw new UserNotAuthorizedToModifySolicitudException();
    }
  }

  public boolean hasAuthorityCreateInvestigador() {
    return SgiSecurityContextHolder.hasAuthorityForAnyUO(CSP_SOL_INV_C);
  }

  public boolean hasAuthorityDeleteInvestigador() {
    return SgiSecurityContextHolder.hasAuthorityForAnyUO(CSP_SOL_INV_BR);
  }

  public boolean hasAuthorityViewInvestigador() {
    return hasAuthorityEditInvestigador();
  }

  public boolean hasAuthorityEditInvestigador() {
    return SgiSecurityContextHolder.hasAuthorityForAnyUO(CSP_SOL_INV_ER);
  }

  public boolean hasAuthorityEditUnidadGestion(String unidadGestion) {
    return SgiSecurityContextHolder.hasAuthorityForUO(CSP_SOL_E, unidadGestion);
  }

  public boolean hasAuthorityEditInvestigador(Solicitud solicitud) {
    return hasAuthorityEditInvestigador() && solicitud.getCreadorRef().equals(getAuthenticationPersonaRef());
  }

  public boolean hasAuthorityViewInvestigador(Solicitud solicitud) {
    return hasAuthorityViewInvestigador()
        && Objects.equals(solicitud.getSolicitanteRef(), getAuthenticationPersonaRef());
  }

  public boolean hasAuthorityViewUnidadGestion(Solicitud solicitud) {
    return SgiSecurityContextHolder.hasAuthorityForUO(CSP_SOL_E, solicitud.getUnidadGestionRef())
        || SgiSecurityContextHolder.hasAuthorityForUO(CSP_SOL_V, solicitud.getUnidadGestionRef());
  }

  public boolean hasAuthorityViewTutor(Solicitud solicitud) {
    return hasAuthorityViewInvestigador()
        && solicitud.getFormularioSolicitud().equals(FormularioSolicitud.RRHH)
        && isUserTutor(solicitud.getId());
  }

  /**
   * Comprueba si el usuario actual es el tutor de la {@link Solicitud}
   * 
   * @param solicitudId Iddentifiacdor de la {@link Solicitud}
   * @return <code>true</code> Si es el tutor, <code>false</code> en cualquier
   *         otro caso.
   */
  public boolean isUserTutor(Long solicitudId) {
    Specification<Solicitud> specs = SolicitudSpecifications.byId(solicitudId).and(
        SolicitudSpecifications.byTutor(getAuthenticationPersonaRef()));

    return repository.count(specs) > 0;
  }

  /**
   * Comprueba si el cambio de estado esta entre los permitidos para el
   * investigador
   * 
   * @param estadoActual Estado actual de la {@link Solicitud}
   * @param nuevoEstado  Nuevo estado al que se quiere cambiar la
   *                     {@link Solicitud}
   * 
   * @throws {@link UserNotAuthorizedToChangeEstadoSolicitudException} si el
   *                cambio de estado no esta permitido
   */
  private boolean validateCambioEstadoInvestigador(Estado estadoActual, Estado nuevoEstado) {
    boolean isCambioEstadoValido;
    switch (estadoActual) {
      case BORRADOR:
      case RECHAZADA:
        isCambioEstadoValido = nuevoEstado.equals(Estado.SOLICITADA) || nuevoEstado.equals(Estado.DESISTIDA);
        break;
      case SUBSANACION:
        isCambioEstadoValido = nuevoEstado.equals(Estado.PRESENTADA_SUBSANACION)
            || nuevoEstado.equals(Estado.DESISTIDA);
        break;
      case EXCLUIDA_PROVISIONAL:
        isCambioEstadoValido = nuevoEstado.equals(Estado.ALEGACION_FASE_ADMISION)
            || nuevoEstado.equals(Estado.DESISTIDA);
        break;
      case EXCLUIDA_DEFINITIVA:
        isCambioEstadoValido = nuevoEstado.equals(Estado.RECURSO_FASE_ADMISION) || nuevoEstado.equals(Estado.DESISTIDA);
        break;
      case DENEGADA_PROVISIONAL:
        isCambioEstadoValido = nuevoEstado.equals(Estado.ALEGACION_FASE_PROVISIONAL)
            || nuevoEstado.equals(Estado.DESISTIDA);
        break;
      case DENEGADA:
        isCambioEstadoValido = nuevoEstado.equals(Estado.RECURSO_FASE_CONCESION)
            || nuevoEstado.equals(Estado.DESISTIDA);
        break;
      default:
        isCambioEstadoValido = false;
    }

    if (!isCambioEstadoValido) {
      throw new UserNotAuthorizedToChangeEstadoSolicitudException(estadoActual, nuevoEstado);
    }

    return isCambioEstadoValido;
  }

  /**
   * Comprueba si el cambio de estado esta entre los permitidos para el
   * tutor
   * 
   * @param estadoActual Estado actual de la {@link Solicitud}
   * @param nuevoEstado  Nuevo estado al que se quiere cambiar la
   *                     {@link Solicitud}
   * 
   * @throws {@link UserNotAuthorizedToChangeEstadoSolicitudException} si el
   *                cambio de estado no esta permitido
   */
  private boolean validateCambioEstadoTutor(Estado estadoActual, Estado nuevoEstado) {
    boolean isCambioEstadoValido = Objects.equals(Estado.SOLICITADA, estadoActual)
        && (nuevoEstado.equals(Estado.RECHAZADA) || nuevoEstado.equals(Estado.VALIDADA));

    if (!isCambioEstadoValido) {
      throw new UserNotAuthorizedToChangeEstadoSolicitudException(estadoActual, nuevoEstado);
    }

    return isCambioEstadoValido;
  }

}
