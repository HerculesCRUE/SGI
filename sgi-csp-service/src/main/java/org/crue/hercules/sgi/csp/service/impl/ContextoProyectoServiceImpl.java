package org.crue.hercules.sgi.csp.service.impl;

import java.util.Optional;

import org.crue.hercules.sgi.csp.exceptions.ContextoProyectoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.model.ContextoProyecto;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.repository.ContextoProyectoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.service.ContextoProyectoService;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link ContextoProyecto}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ContextoProyectoServiceImpl implements ContextoProyectoService {

  private final ContextoProyectoRepository repository;
  private final ProyectoRepository proyectoRepository;

  public ContextoProyectoServiceImpl(ContextoProyectoRepository repository, ProyectoRepository proyectoRepository) {
    this.repository = repository;
    this.proyectoRepository = proyectoRepository;
  }

  /**
   * Guarda la entidad {@link ContextoProyecto}.
   * 
   * @param contextoProyecto la entidad {@link ContextoProyecto} a guardar.
   * @return ContextoProyecto la entidad {@link ContextoProyecto} persistida.
   */
  @Override
  @Transactional
  public ContextoProyecto create(ContextoProyecto contextoProyecto) {
    log.debug("create(ContextoProyecto contextoProyecto) - start");

    AssertHelper.idIsNull(contextoProyecto.getId(), ContextoProyecto.class);
    AssertHelper.idNotNull(contextoProyecto.getProyectoId(), Proyecto.class);

    AssertHelper.entityExists(!repository.existsByProyectoId(contextoProyecto.getProyectoId()),
        Proyecto.class, ContextoProyecto.class);

    ContextoProyecto returnValue = repository.save(contextoProyecto);

    log.debug("create(ContextoProyecto contextoProyecto) - end");
    return returnValue;
  }

  /**
   * Actualiza la entidad {@link ContextoProyecto}.
   * 
   * @param contextoProyectoActualizar la entidad {@link ContextoProyecto} a
   *                                   guardar.
   * @param idProyecto                 identificado de la {@link Proyecto} a
   *                                   guardar.
   * @return ContextoProyecto la entidad {@link ContextoProyecto} persistida.
   */
  @Override
  @Transactional
  public ContextoProyecto update(ContextoProyecto contextoProyectoActualizar, Long idProyecto) {
    log.debug("update(ContextoProyecto contextoProyectoActualizar) - start");

    AssertHelper.idNotNull(idProyecto, Proyecto.class);

    return repository.findByProyectoId(idProyecto).map(contextoProyecto -> {

      contextoProyecto.setAreaTematica(contextoProyectoActualizar.getAreaTematica());
      contextoProyecto.setIntereses(contextoProyectoActualizar.getIntereses());
      contextoProyecto.setObjetivos(contextoProyectoActualizar.getObjetivos());
      contextoProyecto.setPropiedadResultados(contextoProyectoActualizar.getPropiedadResultados());
      contextoProyecto.setResultadosPrevistos(contextoProyectoActualizar.getResultadosPrevistos());

      ContextoProyecto returnValue = repository.save(contextoProyecto);
      log.debug("update(ContextoProyecto contextoProyectoActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ContextoProyectoNotFoundException(contextoProyectoActualizar.getId()));

  }

  /**
   * Obtiene el {@link ContextoProyecto} para una {@link Proyecto}.
   *
   * @param id el id de la {@link Proyecto}.
   * @return la entidad {@link ContextoProyecto} de la {@link Proyecto}
   */
  @Override
  public ContextoProyecto findByProyecto(Long id) {
    log.debug("findByProyecto(Long id) - start");

    if (proyectoRepository.existsById(id)) {
      final Optional<ContextoProyecto> returnValue = repository.findByProyectoId(id);
      log.debug("findByProyectoId(Long id) - end");
      return (returnValue.isPresent()) ? returnValue.get() : null;
    } else {
      throw new ProyectoNotFoundException(id);
    }
  }

  /**
   * Indica si existe el {@link ContextoProyecto} de un {@link Proyecto}
   * 
   * @param id identificador de la {@link Proyecto}
   * @return si existe la entidad {@link ContextoProyecto}
   */
  @Override
  public Boolean existsByProyecto(Long id) {
    log.debug("existsByProyecto(Long id) - start");
    return repository.existsByProyectoId(id);
  }

}
