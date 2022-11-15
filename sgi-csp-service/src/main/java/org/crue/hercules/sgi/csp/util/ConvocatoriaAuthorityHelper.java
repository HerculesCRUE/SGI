package org.crue.hercules.sgi.csp.util;

import java.util.List;

import org.crue.hercules.sgi.csp.enums.FormularioSolicitud;
import org.crue.hercules.sgi.csp.exceptions.ConfiguracionSolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToAccessConvocatoriaException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToCreateConvocatoriaException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToModifyConvocatoriaException;
import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.Convocatoria.Estado;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.repository.ConfiguracionSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.framework.security.core.context.SgiSecurityContextHolder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ConvocatoriaAuthorityHelper extends AuthorityHelper {
  public static final String CSP_CON_B = "CSP-CON-B";
  public static final String CSP_CON_C = "CSP-CON-C";
  public static final String CSP_CON_E = "CSP-CON-E";
  public static final String CSP_CON_INV_V = "CSP-CON-INV-V";
  public static final String CSP_CON_R = "CSP-CON-R";
  public static final String CSP_CON_V = "CSP-CON-V";

  private final ConvocatoriaRepository repository;
  private final ConfiguracionSolicitudRepository configuracionSolicitudRepository;

  /**
   * Comprueba si el usuario logueado tiene permiso para ver la
   * {@link Convocatoria}
   * 
   * @param convocatoriaId Identificador de la {@link Convocatoria}
   * 
   * @throws UserNotAuthorizedToAccessConvocatoriaException si el usuario no esta
   *                                                        autorizado para ver la
   *                                                        {@link Convocatoria}
   * @throws ConvocatoriaNotFoundException                  si no existe la
   *                                                        {@link Convocatoria}
   */
  public void checkUserHasAuthorityViewConvocatoria(Long convocatoriaId)
      throws UserNotAuthorizedToAccessConvocatoriaException {
    checkUserHasAuthorityViewConvocatoria(repository.findById(convocatoriaId)
        .orElseThrow(() -> new ConvocatoriaNotFoundException(convocatoriaId)));
  }

  /**
   * Comprueba si el usuario logueado tiene permiso para ver la
   * {@link Convocatoria}
   * 
   * @param convocatoria la {@link Convocatoria}
   * 
   * @throws UserNotAuthorizedToAccessConvocatoriaException si el usuario no esta
   *                                                        autorizado para ver la
   *                                                        {@link Convocatoria}
   */
  public void checkUserHasAuthorityViewConvocatoria(Convocatoria convocatoria)
      throws UserNotAuthorizedToAccessConvocatoriaException {
    if (!(hasPublicAccess(convocatoria)
        || hasAuthorityViewUnidadGestion(convocatoria)
        || hasAuthorityViewInvestigador(convocatoria))) {
      throw new UserNotAuthorizedToAccessConvocatoriaException();
    }
  }

  /**
   * Comprueba si el usuario logueado tiene permiso para modificar la
   * {@link Convocatoria}
   * 
   * @param convocatoriaId Identificador de la {@link Convocatoria}
   * 
   * @throws UserNotAuthorizedToModifyConvocatoriaException si el usuario no esta
   *                                                        autorizado para
   *                                                        modificar
   *                                                        la {@link Solicitud}
   */
  public void checkUserHasAuthorityModifyConvocatoria(Long convocatoriaId)
      throws UserNotAuthorizedToModifyConvocatoriaException {
    checkUserHasAuthorityModifyConvocatoria(repository.findById(convocatoriaId)
        .orElseThrow(() -> new SolicitudNotFoundException(convocatoriaId)));
  }

  /**
   * Comprueba si el usuario logueado tiene permiso para modificar la
   * {@link Convocatoria}
   * 
   * @param convocatoria la {@link Convocatoria}
   * 
   * @throws UserNotAuthorizedToModifyConvocatoriaException si el usuario no esta
   *                                                        autorizado para
   *                                                        modificar la
   *                                                        {@link Convocatoria}
   */
  public void checkUserHasAuthorityModifyConvocatoria(Convocatoria convocatoria)
      throws UserNotAuthorizedToModifyConvocatoriaException {
    if (!hasAuthorityEditUnidadGestion(convocatoria)) {
      throw new UserNotAuthorizedToModifyConvocatoriaException();
    }
  }

  /**
   * Comprueba si el usuario logueado tiene permiso para crear la
   * {@link Convocatoria}
   * 
   * @param convocatoria la {@link Convocatoria}
   * 
   * @throws UserNotAuthorizedToCreateConvocatoriaException si el usuario no esta
   *                                                        autorizado para crear
   *                                                        la
   *                                                        {@link Convocatoria}
   */
  public void checkUserHasAuthorityCreateConvocatoria(Convocatoria convocatoria) {
    if (!SgiSecurityContextHolder.hasAuthorityForUO(CSP_CON_C, convocatoria.getUnidadGestionRef())) {
      throw new UserNotAuthorizedToCreateConvocatoriaException();
    }
  }

  /**
   * Comprueba si el usuario tiene el permiso para ver la {@link Convocatoria} y
   * si esta {@link Estado#REGISTRADA} y
   * {@link ConfiguracionSolicitud#tramitacionSGI} es <code>true</code>
   * 
   * @param convocatoria la {@link Convocatoria}
   * @return <code>true</code> si tiene permiso, <code>false</code> si no lo tiene
   */
  private boolean hasAuthorityViewInvestigador(Convocatoria convocatoria) {
    if (!SgiSecurityContextHolder.hasAuthorityForAnyUO(CSP_CON_INV_V)) {
      return false;
    }

    ConfiguracionSolicitud configuracionSolicitud = configuracionSolicitudRepository
        .findByConvocatoriaId(convocatoria.getId())
        .orElseThrow(() -> new ConfiguracionSolicitudNotFoundException(convocatoria.getId()));

    return convocatoria.getEstado().equals(Estado.REGISTRADA)
        && Boolean.TRUE.equals(configuracionSolicitud.getTramitacionSGI());
  }

  public boolean hasAuthorityViewUnidadGestion(Convocatoria convocatoria) {
    return SgiSecurityContextHolder.hasAuthorityForUO(CSP_CON_E, convocatoria.getUnidadGestionRef())
        || SgiSecurityContextHolder.hasAuthorityForUO(CSP_CON_V, convocatoria.getUnidadGestionRef());
  }

  public boolean hasAuthorityEditUnidadGestion(Convocatoria convocatoria) {
    return SgiSecurityContextHolder.hasAuthorityForUO(CSP_CON_E, convocatoria.getUnidadGestionRef())
        && convocatoria.getActivo().equals(Boolean.TRUE);
  }

  /**
   * Lista de unidades de gestion para las que el usuario tienen algun permiso de
   * convocaria (CSP_CON_)
   * 
   * @return la lista de unidades de gestion
   */
  public List<String> getUserUOsConvocatoria() {
    return SgiSecurityContextHolder.getUOsForAnyAuthority(
        new String[] { CSP_CON_C, CSP_CON_V, CSP_CON_E, CSP_CON_B, CSP_CON_R });
  }

  /**
   * Comprueba si es una {@link Convocatoria} acesible publicamente.
   * 
   * @param convocatoria la {@link Convocatoria}
   * @return <code>true</code> si es acesible publicamente, <code>false</code> si
   *         no lo es
   */
  private boolean hasPublicAccess(Convocatoria convocatoria) {
    if (!convocatoria.getActivo().booleanValue()
        || !convocatoria.getFormularioSolicitud().equals(FormularioSolicitud.RRHH)) {
      return false;
    }

    ConfiguracionSolicitud configuracionSolicitud = configuracionSolicitudRepository
        .findByConvocatoriaId(convocatoria.getId())
        .orElse(null);

    return configuracionSolicitud != null
        && convocatoria.getEstado().equals(Estado.REGISTRADA)
        && Boolean.TRUE.equals(configuracionSolicitud.getTramitacionSGI());
  }

}
