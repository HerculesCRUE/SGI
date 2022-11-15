package org.crue.hercules.sgi.csp.util;

import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToModifyConvocatoriaException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToModifyProyectoException;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoFacturacion;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.repository.ProyectoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoResponsableEconomicoRepository;
import org.springframework.stereotype.Component;

@Component
public class ProyectoFacturacionAuthorityHelper extends ProyectoHelper {

  public ProyectoFacturacionAuthorityHelper(
      ProyectoRepository proyectoRepository,
      ProyectoEquipoRepository proyectoEquipoRepository,
      ProyectoResponsableEconomicoRepository proyectoResponsableEconomicoRepository) {
    super(proyectoRepository, proyectoEquipoRepository, proyectoResponsableEconomicoRepository);
  }

  /**
   * Comprueba si el usuario logueado tiene permiso para modificar los
   * {@link ProyectoFacturacion} del {@link Proyecto}
   * 
   * @param proyectoId Identificador de la {@link ProyectoFacturacion}
   * 
   * @throws UserNotAuthorizedToModifyConvocatoriaException si el usuario no esta
   *                                                        autorizado para
   *                                                        modificar
   *                                                        la {@link Solicitud}
   */
  public void checkUserHasAuthorityValidateIPProyectoFacturacion(Long proyectoId)
      throws UserNotAuthorizedToModifyProyectoException {
    if (!hasUserAuthorityViewInvestigador()
        || !(checkUserPresentInEquipos(proyectoId) || checkUserIsResponsableEconomico(proyectoId))) {
      throw new UserNotAuthorizedToModifyProyectoException();
    }

  }

}
