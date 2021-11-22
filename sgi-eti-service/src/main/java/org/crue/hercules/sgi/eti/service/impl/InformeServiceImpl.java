package org.crue.hercules.sgi.eti.service.impl;

import java.util.Optional;

import org.crue.hercules.sgi.eti.exceptions.InformeNotFoundException;
import org.crue.hercules.sgi.eti.model.Informe;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.eti.repository.InformeRepository;
import org.crue.hercules.sgi.eti.service.InformeService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link Informe}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class InformeServiceImpl implements InformeService {
  private final InformeRepository informeRepository;

  public InformeServiceImpl(InformeRepository informeRepository) {
    this.informeRepository = informeRepository;
  }

  /**
   * Guarda la entidad {@link Informe}.
   *
   * @param informe la entidad {@link Informe} a guardar.
   * @return la entidad {@link Informe} persistida.
   */
  @Transactional
  public Informe create(Informe informe) {
    log.debug("Petición a create Informe : {} - start", informe);
    Assert.isNull(informe.getId(), "Informe id tiene que ser null para crear un nuevo informe");

    return informeRepository.save(informe);
  }

  /**
   * Obtiene todas las entidades {@link Informe} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link Informe} paginadas y filtradas.
   */
  public Page<Informe> findAll(String query, Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Specification<Informe> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<Informe> returnValue = informeRepository.findAll(specs, paging);
    log.debug("findAll(String query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link Informe} por id.
   *
   * @param id el id de la entidad {@link Informe}.
   * @return la entidad {@link Informe}.
   * @throws InformeNotFoundException Si no existe ningún {@link Informe} con ese
   *                                  id.
   */
  public Informe findById(final Long id) throws InformeNotFoundException {
    log.debug("Petición a get Informe : {}  - start", id);
    final Informe Informe = informeRepository.findById(id).orElseThrow(() -> new InformeNotFoundException(id));
    log.debug("Petición a get Informe : {}  - end", id);
    return Informe;

  }

  /**
   * Elimina una entidad {@link Informe} por id.
   *
   * @param id el id de la entidad {@link Informe}.
   */
  @Transactional
  public void delete(Long id) throws InformeNotFoundException {
    log.debug("Petición a delete Informe : {}  - start", id);
    Assert.notNull(id, "El id de Informe no puede ser null.");
    if (!informeRepository.existsById(id)) {
      throw new InformeNotFoundException(id);
    }
    informeRepository.deleteById(id);
    log.debug("Petición a delete Informe : {}  - end", id);
  }

  /**
   * Elimina todos los registros {@link Informe}.
   */
  @Transactional
  public void deleteAll() {
    log.debug("Petición a deleteAll de Informe: {} - start");
    informeRepository.deleteAll();
    log.debug("Petición a deleteAll de Informe: {} - end");

  }

  /**
   * Actualiza los datos del {@link Informe}.
   * 
   * @param informeActualizar {@link Informe} con los datos actualizados.
   * @return El {@link Informe} actualizado.
   * @throws InformeNotFoundException Si no existe ningún {@link Informe} con ese
   *                                  id.
   * @throws IllegalArgumentException Si el {@link Informe} no tiene id.
   */

  @Transactional
  public Informe update(final Informe informeActualizar) {
    log.debug("update(Informe informeActualizar) - start");

    Assert.notNull(informeActualizar.getId(), "Informe id no puede ser null para actualizar un informe");

    return informeRepository.findById(informeActualizar.getId()).map(informe -> {
      informe.setDocumentoRef(informeActualizar.getDocumentoRef());
      informe.setMemoria(informeActualizar.getMemoria());
      informe.setVersion(informeActualizar.getVersion());

      Informe returnValue = informeRepository.save(informe);
      log.debug("update(Informe informeActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new InformeNotFoundException(informeActualizar.getId()));
  }

  /**
   * Devuelve un listado paginado de {@link Informe} filtrado por la
   * {@link Memoria}
   * 
   * @param idMemoria identificador de la {@link Memoria}
   */
  @Override
  public void deleteInformeMemoria(Long idMemoria) {
    Optional<Informe> informe = informeRepository.findFirstByMemoriaIdOrderByVersionDesc(idMemoria);

    if (informe.isPresent()) {
      informeRepository.delete(informe.get());
    }

  }

  /**
   * Devuelve un listado paginado de {@link Informe} filtrado por la
   * {@link Memoria}
   * 
   * @param id       identificador de la {@link Memoria}
   * @param pageable paginación
   * @return el listado paginado de {@link Informe}
   */
  @Override
  public Page<Informe> findByMemoria(Long id, Pageable pageable) {
    Assert.notNull(id, "Memoria id no puede ser null para actualizar un informe");
    Page<Informe> returnValue = informeRepository.findByMemoriaId(id, pageable);
    return returnValue;
  }

  /**
   * Devuelve la última versión del {@link Informe} filtrado por la
   * {@link Memoria}
   * 
   * @param id identificador de la {@link Memoria}
   * @return el {@link Informe}
   */
  @Override
  public Optional<Informe> findFirstByMemoriaOrderByVersionDesc(Long id) {
    Assert.notNull(id, "Memoria id no puede ser null para buscar un informe");
    return informeRepository.findFirstByMemoriaIdOrderByVersionDesc(id);
  }

  /**
   * Devuelve el {@link Informe} filtrado por la {@link Memoria} su versión y su
   * tipo de evaluación
   * 
   * @param id               identificador de la {@link Memoria}
   * @param idTipoEvaluacion identificador del {@link TipoEvaluacion}
   * @return el {@link Informe}
   */
  @Override
  public Optional<Informe> findByMemoriaAndTipoEvaluacion(Long id, Long idTipoEvaluacion) {
    Assert.notNull(id, "Memoria id no puede ser null para buscar un informe");
    Assert.notNull(idTipoEvaluacion,
        "El id TipoEvaluacion no puede ser null para buscar un informe por su tipo de evaluación");
    return informeRepository.findFirstByMemoriaIdAndTipoEvaluacionIdOrderByVersionDesc(id, idTipoEvaluacion);
  }

}
