package org.crue.hercules.sgi.csp.service.impl;

import java.util.List;
import java.util.Optional;

import org.crue.hercules.sgi.csp.exceptions.ProrrogaDocumentoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoProrrogaNotFoundException;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoDocumento;
import org.crue.hercules.sgi.csp.model.ProrrogaDocumento;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoProrroga;
import org.crue.hercules.sgi.csp.repository.ModeloTipoDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.ProrrogaDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoProrrogaRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProrrogaDocumentoSpecifications;
import org.crue.hercules.sgi.csp.service.ProrrogaDocumentoService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link ProrrogaDocumento}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)

public class ProrrogaDocumentoServiceImpl implements ProrrogaDocumentoService {

  private final ProrrogaDocumentoRepository repository;
  private final ProyectoProrrogaRepository proyectoProrrogaRepository;
  private final ModeloTipoDocumentoRepository modeloTipoDocumentoRepository;

  public ProrrogaDocumentoServiceImpl(ProrrogaDocumentoRepository prorrogaDocumentoRepository,
      ProyectoProrrogaRepository proyectoProrrogaRepository,
      ModeloTipoDocumentoRepository modeloTipoDocumentoRepository) {
    this.repository = prorrogaDocumentoRepository;
    this.proyectoProrrogaRepository = proyectoProrrogaRepository;
    this.modeloTipoDocumentoRepository = modeloTipoDocumentoRepository;
  }

  /**
   * Guardar un nuevo {@link ProrrogaDocumento}.
   *
   * @param prorrogaDocumento la entidad {@link ProrrogaDocumento} a guardar.
   * @return la entidad {@link ProrrogaDocumento} persistida.
   */
  @Override
  @Transactional
  public ProrrogaDocumento create(ProrrogaDocumento prorrogaDocumento) {
    log.debug("create(ProrrogaDocumento prorrogaDocumento) - start");

    Assert.isNull(prorrogaDocumento.getId(),
        "ProrrogaDocumento id tiene que ser null para crear un nuevo ProrrogaDocumento");

    validarRequeridosProrrogaDocumento(prorrogaDocumento);
    validarProrrogaDcoumento(prorrogaDocumento);

    ProrrogaDocumento returnValue = repository.save(prorrogaDocumento);

    log.debug("create(ProrrogaDocumento prorrogaDocumento) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link ProrrogaDocumento}.
   *
   * @param prorrogaDocumentoActualizar la entidad {@link ProrrogaDocumento} a
   *                                    actualizar.
   * @return la entidad {@link ProrrogaDocumento} persistida.
   */
  @Override
  @Transactional
  public ProrrogaDocumento update(ProrrogaDocumento prorrogaDocumentoActualizar) {
    log.debug("update(ProrrogaDocumento prorrogaDocumentoActualizar) - start");

    Assert.notNull(prorrogaDocumentoActualizar.getId(),
        "ProrrogaDocumento id no puede ser null para actualizar un ProrrogaDocumento");

    validarRequeridosProrrogaDocumento(prorrogaDocumentoActualizar);

    return repository.findById(prorrogaDocumentoActualizar.getId()).map(prorrogaDocumento -> {

      validarProrrogaDcoumento(prorrogaDocumentoActualizar);

      prorrogaDocumento.setNombre(prorrogaDocumentoActualizar.getNombre());
      prorrogaDocumento.setDocumentoRef(prorrogaDocumentoActualizar.getDocumentoRef());
      prorrogaDocumento.setTipoDocumento(prorrogaDocumentoActualizar.getTipoDocumento());
      prorrogaDocumento.setComentario(prorrogaDocumentoActualizar.getComentario());
      prorrogaDocumento.setVisible(prorrogaDocumentoActualizar.getVisible());

      ProrrogaDocumento returnValue = repository.save(prorrogaDocumento);
      log.debug("update(ProrrogaDocumento prorrogaDocumentoActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ProrrogaDocumentoNotFoundException(prorrogaDocumentoActualizar.getId()));
  }

  /**
   * Elimina el {@link ProrrogaDocumento}.
   *
   * @param id Id del {@link ProrrogaDocumento}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id, "ProrrogaDocumento id no puede ser null para eliminar un ProrrogaDocumento");
    if (!repository.existsById(id)) {
      throw new ProrrogaDocumentoNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene {@link ProrrogaDocumento} por su id.
   *
   * @param id el id de la entidad {@link ProrrogaDocumento}.
   * @return la entidad {@link ProrrogaDocumento}.
   */
  @Override
  public ProrrogaDocumento findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ProrrogaDocumento returnValue = repository.findById(id)
        .orElseThrow(() -> new ProrrogaDocumentoNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link ProrrogaDocumento} para una
   * {@link ProyectoProrroga} paginadas y/o filtradas.
   * 
   * @param idProrroga id de {@link ProyectoProrroga}
   * @param query      la información del filtro.
   * @param paging     la información de la paginación.
   * @return la lista de entidades {@link ProrrogaDocumento} paginadas y/o
   *         filtradas.
   */
  @Override
  public Page<ProrrogaDocumento> findAllByProyectoProrroga(Long idProrroga, String query, Pageable paging) {
    log.debug("findAllByProyectoProrroga(Long idProrroga, String query, Pageable pageable) - start");
    Specification<ProrrogaDocumento> specs = ProrrogaDocumentoSpecifications.byProyectoProrrogaId(idProrroga)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<ProrrogaDocumento> returnValue = repository.findAll(specs, paging);
    log.debug("findAllByProyectoProrroga(Long idProrroga, String query, Pageable pageable) - end");
    return returnValue;

  }

  /**
   * Obtener todas las entidades {@link ProrrogaDocumento}.
   * 
   * @param idProyecto id del {@link Proyecto}
   * 
   * @return la lista de entidades {@link ProrrogaDocumento} .
   */
  @Override
  public List<ProrrogaDocumento> findAllByProyecto(Long idProyecto) {
    log.debug("findAllByProyecto(Long idProyecto) - start");

    Specification<ProrrogaDocumento> specByProyecto = ProrrogaDocumentoSpecifications.byProyectoId(idProyecto);
    Specification<ProrrogaDocumento> specs = Specification.where(specByProyecto);

    List<ProrrogaDocumento> returnValue = repository.findAll(specs);
    log.debug("findAllByProyecto(Long idProyecto) - end");
    return returnValue;
  }

  /**
   * Comprueba, valida y tranforma los datos de la {@link ProrrogaDocumento} antes
   * de utilizados para crear o modificar la entidad
   *
   * @param datosProrrogaDocumento
   */
  private void validarProrrogaDcoumento(ProrrogaDocumento datosProrrogaDocumento) {
    log.debug(
        "validarProrrogaDcoumento(ProrrogaDocumento prorrogaDocumento) - start");

    // Se comprueba la existencia del proyecto
    Long proyectoProrrogaId = datosProrrogaDocumento.getProyectoProrrogaId();
    if (!proyectoProrrogaRepository.existsById(proyectoProrrogaId)) {
      throw new ProyectoProrrogaNotFoundException(proyectoProrrogaId);
    }

    if (datosProrrogaDocumento.getTipoDocumento() != null) {
      // TipoDoc Activo sin fase asociada y asociados al modelo ejecucion del proyectp

      Optional<ModeloEjecucion> modeloEjecucion = proyectoProrrogaRepository.getModeloEjecucion(proyectoProrrogaId);
      Assert.isTrue(modeloEjecucion.isPresent(),
          "El Proyecto de la prórroga no cuenta con un modelo de ejecución asignado");
      Assert.isTrue(modeloEjecucion.get().getActivo(), "El modelo de ejecución asignado al proyecto no está activo");

      Optional<ModeloTipoDocumento> modeloTipoDocumento = modeloTipoDocumentoRepository
          .findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(modeloEjecucion.get().getId(), null,
              datosProrrogaDocumento.getTipoDocumento().getId());

      Assert.isTrue(modeloTipoDocumento.isPresent(),
          "El TipoDocumento no está asociado al modelo de ejecución del proyecto");

      Assert.isTrue(modeloTipoDocumento.get().getTipoDocumento().getActivo(), "El TipoDocumento no está activo");

      Assert.isTrue(modeloTipoDocumento.get().getActivo(),
          "El TipoDocumento no está activo para el modelo de ejecución del proyecto");

    }

    log.debug("validarProrrogaDcoumento(ProrrogaDocumento prorrogaDocumento) - end");
  }

  /**
   * valida los datos requeridos de la {@link ProrrogaDocumento}
   *
   * @param datosProrrogaDocumento
   */
  private void validarRequeridosProrrogaDocumento(ProrrogaDocumento datosProrrogaDocumento) {
    log.debug("validarRequeridosProrrogaDocumento(ProrrogaDocumento datosProrrogaDocumento) - start");

    Assert.isTrue(datosProrrogaDocumento.getProyectoProrrogaId() != null,
        "Id ProyectoProrroga no puede ser null para realizar la acción sobre ProrrogaDocumento");

    Assert.isTrue(StringUtils.isNotBlank(datosProrrogaDocumento.getNombre()),
        "Es necesario indicar el nombre del documento");

    Assert.isTrue(datosProrrogaDocumento.getDocumentoRef() != null, "Es necesario indicar la referencia al documento");

    Assert.isTrue(datosProrrogaDocumento.getVisible() != null,
        "Visible no puede ser null para realizar la acción sobre ProrrogaDocumento");

    log.debug("validarRequeridosProrrogaDocumento(ProrrogaDocumento datosProrrogaDocumento) - end");
  }

  /**
   * Comprueba si existen datos vinculados a {@link Proyecto} de
   * {@link ProrrogaDocumento}
   *
   * @param proyectoId Id del {@link Proyecto}.
   * @return true si existe y false en caso contrario.
   */
  @Override
  public boolean existsByProyecto(Long proyectoId) {
    return repository.existsByProyectoProrrogaProyectoId(proyectoId);
  }

}
