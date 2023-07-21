package org.crue.hercules.sgi.csp.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.RolProyectoColectivoNotFoundException;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.RolProyectoColectivo;
import org.crue.hercules.sgi.csp.repository.RolProyectoColectivoRepository;
import org.crue.hercules.sgi.csp.repository.RolProyectoRepository;
import org.crue.hercules.sgi.csp.service.RolProyectoColectivoService;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link RolProyectoColectivo}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class RolProyectoColectivoServiceImpl implements RolProyectoColectivoService {

  private final RolProyectoColectivoRepository repository;
  private final RolProyectoRepository rolProyectoRepository;

  public RolProyectoColectivoServiceImpl(RolProyectoColectivoRepository repository,
      RolProyectoRepository rolProyectoRepository) {
    this.repository = repository;
    this.rolProyectoRepository = rolProyectoRepository;
  }

  /**
   * Guardar un nuevo {@link RolProyectoColectivo}.
   *
   * @param rolProyectoColectivo la entidad {@link RolProyectoColectivo} a
   *                             guardar.
   * @return la entidad {@link RolProyectoColectivo} persistida.
   */
  @Override
  @Transactional
  public RolProyectoColectivo create(RolProyectoColectivo rolProyectoColectivo) {
    log.debug("create(RolProyectoColectivo rolProyectoColectivo) - start");

    AssertHelper
        .idIsNull(rolProyectoColectivo.getId(),
            RolProyectoColectivo.class);

    AssertHelper
        .idNotNull(rolProyectoColectivo.getRolProyectoId(),
            RolProyectoColectivo.class);

    RolProyectoColectivo returnValue = repository.save(rolProyectoColectivo);

    log.debug("create(RolProyectoColectivo rolProyectoColectivo) - end");
    return returnValue;
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

  /**
   * Obtiene un listado de colectivos ref a partir del identificador del
   * {@link RolProyecto}
   * 
   * @param rolProyectoId Identificador de la entidad {@link RolProyecto}.
   * @return listado colectivos ref
   */
  @Override
  public List<RolProyectoColectivo> findAllRolProyectoColectivos(Long rolProyectoId) {
    log.debug("findAllColectivos(Long rolProyectoId) - start");
    List<RolProyectoColectivo> returnValue = repository.findAllByRolProyectoId(rolProyectoId);
    log.debug("findAllColectivos(Long rolProyectoId) - end");
    return returnValue;
  }

  /**
   * Obtiene un listado de colectivos ref vinculados a {@link RolProyecto} activos
   *
   * @return listado colectivos ref
   */
  @Override
  public List<String> findColectivosActivos() {
    log.debug("findColectivosActivos() - start");
    List<String> returnValue = repository.findColectivosActivos();
    log.debug("findColectivosActivos() - end");
    return returnValue;
  }

  /**
   * Elimina una entidad {@link RolProyectoColectivo} por id.
   *
   * @param id el id de la entidad {@link RolProyectoColectivo}.
   */
  @Transactional
  public void delete(Long id) throws RolProyectoColectivoNotFoundException {
    log.debug("Petición a delete RolProyectoColectivo : {}  - start", id);
    AssertHelper
        .idNotNull(id, RolProyectoColectivo.class);
    if (!repository.existsById(id)) {
      throw new RolProyectoColectivoNotFoundException(id);
    }
    repository.deleteById(id);
    log.debug("Petición a delete RolProyectoColectivo : {}  - end", id);
  }

}
