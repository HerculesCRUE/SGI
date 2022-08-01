package org.crue.hercules.sgi.eer.util;

import org.crue.hercules.sgi.eer.exceptions.UserNotAuthorizedToAccessEmpresaException;
import org.crue.hercules.sgi.eer.model.Empresa;
import org.crue.hercules.sgi.eer.model.EmpresaEquipoEmprendedor;
import org.crue.hercules.sgi.eer.repository.EmpresaRepository;
import org.crue.hercules.sgi.eer.repository.specification.EmpresaSpecifications;
import org.crue.hercules.sgi.framework.security.core.context.SgiSecurityContextHolder;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Primary
@Component
@RequiredArgsConstructor
public class EmpresaAuthorityHelper extends AuthorityHelper {

  private final EmpresaRepository repository;

  /**
   * Comprueba si el usuario logueado tiene permiso para ver el {@link Empresa}
   * 
   * @param empresaId Identifiacdor del {@link Empresa}
   * @throws UserNotAuthorizedToAccessEmpresaException si el usuario no esta
   *                                                   autorizado para ver el
   *                                                   {@link Empresa}
   */
  public void checkUserHasAuthorityViewEmpresa(Long empresaId) throws UserNotAuthorizedToAccessEmpresaException {
    if (!(isUserInEmpresa(empresaId) || hasAuthorityViewUnidadGestion()
        || isClientUser())) {
      throw new UserNotAuthorizedToAccessEmpresaException();
    }
  }

  /**
   * Specification para filtrar los empresas a los que un usuario de tipo
   * investigador puede acceder.
   * 
   * Los investigadores pueden ver los empresas en los que forman parte del equipo
   * ({@link EmpresaEquipoEmprendedor})
   * 
   * @return Specifications cuando el usuario es un investigador
   */
  public Specification<Empresa> getSpecificationsUserInvestigadorEmpresasCanView() {
    String personaRef = getAuthenticationPersonaRef();

    return EmpresaSpecifications.byMiembroEquipoInEmpresaEquipoEmprendedor(personaRef);
  }

  public boolean hasAuthorityViewUnidadGestion() {
    return SgiSecurityContextHolder.hasAuthorityForAnyUO("EER-EER-E")
        || SgiSecurityContextHolder.hasAuthorityForAnyUO("EER-EER-V");
  }

  public boolean hasAuthorityEditUnidadGestion() {
    return SgiSecurityContextHolder.hasAuthorityForAnyUO("EER-EER-E");
  }

  /**
   * Comprueba si el usuario actual pertenece al {@link Empresa}
   * 
   * @param empresaId Iddentifiacdor del {@link Empresa}
   * @return <code>true</code> Si pertenece, <code>false</code> en cualquier otro
   *         caso.
   */
  private boolean isUserInEmpresa(Long empresaId) {
    Specification<Empresa> specs = EmpresaSpecifications.byId(empresaId).and(
        getSpecificationsUserInvestigadorEmpresasCanView());

    return repository.count(specs) > 0;
  }

}
