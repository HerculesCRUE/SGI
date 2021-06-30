package org.crue.hercules.sgi.csp.util;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.framework.security.core.context.SgiSecurityContextHolder;
import org.springframework.util.Assert;

public class ProyectoHelper {

  /**
   * Comprueba que el proyecto pertenece a una unidad de gestión que el usuario
   * actual pueda gestionar.
   * 
   * @param proyecto el {@link Proyecto} sobre el que realizar las comprobaciones
   */
  public static void checkCanRead(Proyecto proyecto) {
    Assert.isTrue(
        SgiSecurityContextHolder.hasAnyAuthorityForUO(new String[] { "CSP-PRO-V", "CSP-PRO-E" },
            proyecto.getUnidadGestionRef()),
        "El proyecto no pertenece a una Unidad de Gestión gestionable por el usuario");
  }
}
