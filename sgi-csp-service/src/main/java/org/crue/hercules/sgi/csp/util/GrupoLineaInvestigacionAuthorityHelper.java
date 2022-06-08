package org.crue.hercules.sgi.csp.util;

import org.crue.hercules.sgi.csp.config.SgiConfigProperties;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToAccessGrupoException;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoLineaInvestigacion;
import org.crue.hercules.sgi.csp.repository.GrupoRepository;
import org.crue.hercules.sgi.csp.repository.specification.GrupoSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class GrupoLineaInvestigacionAuthorityHelper extends GrupoAuthorityHelper {

  private final GrupoRepository repository;

  public GrupoLineaInvestigacionAuthorityHelper(SgiConfigProperties sgiConfigProperties, GrupoRepository repository) {
    super(sgiConfigProperties, repository);
    this.repository = repository;
  }

  /**
   * Comprueba si el usuario logueado tiene permiso para ver el
   * {@link GrupoLineaInvestigacion}
   * 
   * @param grupoLineaInvestigacionId Identifiacdor del
   *                                  {@link GrupoLineaInvestigacion}
   * @throws UserNotAuthorizedToAccessGrupoException si el usuario no esta
   *                                                 autorizado para ver el
   *                                                 {@link Grupo}
   */
  public void checkUserHasAuthorityViewGrupoLineaInvestigacion(Long grupoLineaInvestigacionId)
      throws UserNotAuthorizedToAccessGrupoException {
    if (!((hasAuthorityViewInvestigador() && isUserInGrupoLineaInvestigacion(grupoLineaInvestigacionId))
        || hasAuthorityViewUnidadGestion() || isClientUser())) {
      throw new UserNotAuthorizedToAccessGrupoException();
    }
  }

  /**
   * Comprueba si el usuario actual pertenece al {@link Grupo} del
   * {@link GrupoLineaInvestigacion}
   * 
   * @param grupoLineaInvestigacionId Iddentifiacdor del
   *                                  {@link GrupoLineaInvestigacion}
   * @return <code>true</code> Si pertenece, <code>false</code> en cualquier otro
   *         caso.
   */
  private boolean isUserInGrupoLineaInvestigacion(Long grupoLineaInvestigacionId) {
    Specification<Grupo> specs = GrupoSpecifications.byGrupoLineaInvestigacionId(grupoLineaInvestigacionId)
        .and(getSpecificationsUserInvestigadorGruposCanView());

    return repository.count(specs) > 0;
  }

}
