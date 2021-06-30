package org.crue.hercules.sgi.usr.service.impl;

import java.util.List;

import com.nimbusds.oauth2.sdk.util.CollectionUtils;

import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.security.core.context.SgiSecurityContextHolder;
import org.crue.hercules.sgi.usr.exceptions.UnidadNotFoundException;
import org.crue.hercules.sgi.usr.model.Unidad;
import org.crue.hercules.sgi.usr.repository.UnidadRepository;
import org.crue.hercules.sgi.usr.repository.specification.UnidadSpecifications;
import org.crue.hercules.sgi.usr.service.UnidadService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link Unidad}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class UnidadServiceImpl implements UnidadService {

  private final UnidadRepository repository;

  public UnidadServiceImpl(UnidadRepository unidadRepository) {
    this.repository = unidadRepository;
  }

  /**
   * Obtener todas las entidades {@link Unidad} activas paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link Unidad} paginadas y/o filtradas.
   */
  @Override
  public Page<Unidad> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<Unidad> specByQuery = SgiRSQLJPASupport.toSpecification(query);
    Specification<Unidad> specActivos = UnidadSpecifications.activos();

    Specification<Unidad> specs = Specification.where(specActivos).and(specByQuery);

    Page<Unidad> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Recupera una lista de paginada de {@link Unidad} restringidas por los
   * permisos del usuario logueado.
   * 
   * @param query    datos de búsqueda
   * @param pageable datos de la paginación
   * @return listado paginado de {@link Unidad}
   */
  @Override
  public Page<Unidad> findAllRestringidos(String query, Pageable pageable) {
    log.debug("findAllTodosRestringidos(String query, Object credentials, Pageable pageable) - start");

    Specification<Unidad> specs = SgiRSQLJPASupport.toSpecification(query);

    List<String> unidadesGestion = SgiSecurityContextHolder
        .getUOsForAnyAuthority(new String[] { "CSP-CON-V", "CSP-CON-C", "CSP-CON-E", "CSP-SOL-C", "CSP-SOL-E",
            "CSP-SOL-V", "CSP-PRO-V", "CSP-PRO-C", "CSP-PRO-E", "CSP-PRO-B", "CSP-PRO-R" });

    if (!CollectionUtils.isEmpty(unidadesGestion)) {
      Specification<Unidad> specByUnidadGestionRefIn = UnidadSpecifications.acronimosIn(unidadesGestion);
      specs = specs.and(specByUnidadGestionRefIn);
    }

    Page<Unidad> returnValue = repository.findAll(specs, pageable);

    log.debug("findAllTodosRestringidos(String query, Object credentials, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link Unidad} por su id.
   *
   * @param id el id de la entidad {@link Unidad}.
   * @return la entidad {@link Unidad}.
   */
  @Override
  public Unidad findById(Long id) {
    log.debug("findById(Long id)  - start");
    final Unidad returnValue = repository.findById(id).orElseThrow(() -> new UnidadNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;

  }
}