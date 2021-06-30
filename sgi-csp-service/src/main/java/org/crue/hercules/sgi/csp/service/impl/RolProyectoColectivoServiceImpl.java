package org.crue.hercules.sgi.csp.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.RolProyectoColectivo;
import org.crue.hercules.sgi.csp.repository.RolProyectoColectivoRepository;
import org.crue.hercules.sgi.csp.service.RolProyectoColectivoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link RolProyectoColectivo}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class RolProyectoColectivoServiceImpl implements RolProyectoColectivoService {

  private final RolProyectoColectivoRepository repository;

  public RolProyectoColectivoServiceImpl(RolProyectoColectivoRepository repository) {
    this.repository = repository;
  }

  /**
   * Obtiene un listado de colectivos ref a partir del identificador del
   * {@link RolProyecto}
   * 
   * @param rolProyectoId Identificador de la entidad {@link RolProyecto}.
   * @return listado colectivos ref
   */
  @Override
  public List<String> findAllColectivos(Long rolProyectoId) {
    log.debug("findAllColectivos(Long rolProyectoId) - start");
    List<String> returnValue = repository.findAllByRolProyectoId(rolProyectoId).stream()
        .map(result -> result.getColectivoRef()).collect(Collectors.toList());
    log.debug("findAllColectivos(Long rolProyectoId) - end");
    return returnValue;
  }

}
