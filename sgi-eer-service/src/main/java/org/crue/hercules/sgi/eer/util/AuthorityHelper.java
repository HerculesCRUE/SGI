package org.crue.hercules.sgi.eer.util;

import org.crue.hercules.sgi.framework.security.core.context.SgiSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthorityHelper {

  /**
   * Recupera el personaRef del usuario actual
   * 
   * @return el personaRef
   */
  public String getAuthenticationPersonaRef() {
    return SecurityContextHolder.getContext().getAuthentication().getName();
  }

  /**
   * Comprueba si el usuario actual tiene permisos como client para eer
   * 
   * @return <code>true</code> Si tiene permisos como client para eer,
   *         <code>false</code> en cualquier otro caso.
   */
  public boolean isClientUser() {
    return SgiSecurityContextHolder.hasAuthority("SCOPE_sgi-eer");
  }

}
