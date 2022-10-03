package org.crue.hercules.sgi.prc.util;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.framework.security.core.context.SgiSecurityContextHolder;
import org.crue.hercules.sgi.prc.dto.csp.GrupoDto;
import org.crue.hercules.sgi.prc.exceptions.UserNotAuthorizedToAccessProduccionCientificaException;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.repository.ProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.specification.ProduccionCientificaSpecifications;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiCspService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProduccionCientificaAuthorityHelper extends AuthorityHelper {
  public static final String PRC_VAL_E = "PRC-VAL-E";
  public static final String PRC_VAL_V = "PRC-VAL-V";
  public static final String PRC_VAL_INV_ER = "PRC-VAL-INV-ER";

  private final ProduccionCientificaRepository repository;
  private final SgiApiCspService sgiApiCspService;

  /**
   * Comprueba si el usuario logueado tiene permiso para ver la
   * {@link ProduccionCientifica}
   * 
   * @param produccionCientificaId Identificador de la
   *                               {@link ProduccionCientifica}
   * @throws UserNotAuthorizedToAccessProduccionCientificaException si el usuario
   *                                                                no esta
   *                                                                autorizado
   *                                                                para ver la
   *                                                                {@link ProduccionCientifica}
   */
  public void checkUserHasAuthorityViewProduccionCientifica(Long produccionCientificaId)
      throws UserNotAuthorizedToAccessProduccionCientificaException {
    if (!hasAuthorityViewProduccionCientifica(produccionCientificaId)) {
      throw new UserNotAuthorizedToAccessProduccionCientificaException();
    }
  }

  /**
   * Comprueba si el usuario logueado tiene permiso para ver la
   * {@link ProduccionCientifica} como investigador.
   * 
   * @param produccionCientificaId Identificador de la
   *                               {@link ProduccionCientifica}
   * @throws UserNotAuthorizedToAccessProduccionCientificaException si el usuario
   *                                                                no esta
   *                                                                autorizado
   *                                                                para ver la
   *                                                                {@link ProduccionCientifica}
   */
  public void checkUserHasAuthorityViewProduccionCientificaInvestigador(Long produccionCientificaId)
      throws UserNotAuthorizedToAccessProduccionCientificaException {
    if (!hasAuthorityViewProduccionCientificaInvestigador(produccionCientificaId)) {
      throw new UserNotAuthorizedToAccessProduccionCientificaException();
    }
  }

  /**
   * Comprueba si el usuario logueado tiene permiso para ver la
   * {@link ProduccionCientifica}
   * 
   * @param produccionCientificaId Identificador de la
   *                               {@link ProduccionCientifica}
   * @return <code>true</code> si el usuario esta autorizado para ver la
   *         {@link ProduccionCientifica}, <code>false</code> en caso contrario
   */
  public boolean hasAuthorityViewProduccionCientifica(Long produccionCientificaId) {
    return hasAuthorityViewUnidadGestion() || hasAuthorityViewProduccionCientificaInvestigador(produccionCientificaId);
  }

  /**
   * Comprueba si el investigador tiene el permiso para modificar la
   * {@link ProduccionCientifica}
   * 
   * @param produccionCientificaId de la {@link ProduccionCientifica}
   * @return <code>true</code> si tiene permiso, <code>false</code> si no lo tiene
   */
  public boolean hasAuthorityModifyProduccionCientificaInvestigador(Long produccionCientificaId) {
    if (!SgiSecurityContextHolder.hasAuthority(PRC_VAL_INV_ER)) {
      return false;
    }

    final Specification<ProduccionCientifica> spec = ProduccionCientificaSpecifications.byId(produccionCientificaId)
        .and(ProduccionCientificaSpecifications.isInEstadoEditable())
        .and(ProduccionCientificaSpecifications
            .byAutorGrupoEstadoAndAutorGrupoInGrupoRef(TipoEstadoProduccion.PENDIENTE, getUserGrupos()));

    return repository.count(spec) > 0;
  }

  /**
   * Comprueba si el investigador tiene el permiso para ver la
   * {@link ProduccionCientifica}
   * 
   * @param produccionCientificaId de la {@link ProduccionCientifica}
   * @return <code>true</code> si tiene permiso, <code>false</code> si no lo tiene
   */
  public boolean hasAuthorityViewProduccionCientificaInvestigador(Long produccionCientificaId) {
    if (!SgiSecurityContextHolder.hasAuthority(PRC_VAL_INV_ER)) {
      return false;
    }

    final Specification<ProduccionCientifica> spec = ProduccionCientificaSpecifications.byId(produccionCientificaId)
        .and(getSpecificationsUserInvestigadorProduccionCientificaCanView());

    return repository.count(spec) > 0;
  }

  /**
   * Recupera los grupos a los que pertenece el usuario actual
   * 
   * @return lista de identifiacdor de los grupos a los que pertenece
   */
  public List<Long> getUserGrupos() {
    return sgiApiCspService.findAllGruposByPersonaRef(getAuthenticationPersonaRef())
        .stream().map(GrupoDto::getId).collect(Collectors.toList());
  }

  /**
   * Construye la Specification para comprobar si la {@link ProduccionCientifica}
   * esta asociada a alguno de los grupos a los que pertenece el usuario actual.
   * 
   * @return Specification para comprobar si el usuario tiene acceso a la
   *         {@link ProduccionCientifica}.
   */
  private Specification<ProduccionCientifica> getSpecificationsUserInvestigadorProduccionCientificaCanView() {
    return ProduccionCientificaSpecifications.byExistsInGrupoRef(getUserGrupos());
  }

  private boolean hasAuthorityViewUnidadGestion() {
    return SgiSecurityContextHolder.hasAuthority(PRC_VAL_E) || SgiSecurityContextHolder.hasAuthority(PRC_VAL_V);
  }

}
