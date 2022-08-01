package org.crue.hercules.sgi.csp.service.impl;

import java.util.Objects;
import java.util.Optional;

import org.crue.hercules.sgi.csp.exceptions.ProyectoDocumentoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.model.ModeloTipoDocumento;
import org.crue.hercules.sgi.csp.model.ModeloTipoFase;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoDocumento;
import org.crue.hercules.sgi.csp.repository.ModeloTipoDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoFaseRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoDocumentoSpecifications;
import org.crue.hercules.sgi.csp.service.ProyectoDocumentoService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link ProyectoDocumento}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ProyectoDocumentoServiceImpl implements ProyectoDocumentoService {

  private final ProyectoDocumentoRepository repository;
  private final ProyectoRepository proyectoRepository;
  private final ModeloTipoFaseRepository modeloTipoFaseRepository;
  private final ModeloTipoDocumentoRepository modeloTipoDocumentoRepository;

  /**
   * {@link ProyectoDocumentoServiceImpl}.
   * 
   * @param proyectoDocumentoRepository   {@link ProyectoDocumentoRepository}.
   * @param proyectoRepository            {@link ProyectoRepository}.
   * @param modeloTipoFaseRepository      {@link ModeloTipoFaseRepository}.
   * @param modeloTipoDocumentoRepository {@link ModeloTipoDocumentoRepository}.
   */
  public ProyectoDocumentoServiceImpl(ProyectoDocumentoRepository proyectoDocumentoRepository,
      ProyectoRepository proyectoRepository, ModeloTipoFaseRepository modeloTipoFaseRepository,
      ModeloTipoDocumentoRepository modeloTipoDocumentoRepository) {
    this.repository = proyectoDocumentoRepository;
    this.proyectoRepository = proyectoRepository;
    this.modeloTipoFaseRepository = modeloTipoFaseRepository;
    this.modeloTipoDocumentoRepository = modeloTipoDocumentoRepository;
  }

  /**
   * Guardar un nuevo {@link ProyectoDocumento}.
   *
   * @param proyectoDocumento la entidad {@link ProyectoDocumento} a guardar.
   * @return la entidad {@link ProyectoDocumento} persistida.
   */
  @Override
  @Transactional
  public ProyectoDocumento create(ProyectoDocumento proyectoDocumento) {
    log.debug("create(ProyectoDocumento proyectoDocumento) - start");

    Assert.isNull(proyectoDocumento.getId(),
        "ProyectoDocumento id tiene que ser null para crear un nuevo ProyectoDocumento");

    validarRequeridosProyectoDocumento(proyectoDocumento);
    validarProyectoDocumento(proyectoDocumento, null);

    ProyectoDocumento returnValue = repository.save(proyectoDocumento);

    log.debug("create(ProyectoDocumento proyectoDocumento) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link ProyectoDocumento}.
   *
   * @param proyectoDocumentoActualizar la entidad {@link ProyectoDocumento} a
   *                                    actualizar.
   * @return la entidad {@link ProyectoDocumento} persistida.
   */
  @Override
  @Transactional
  public ProyectoDocumento update(ProyectoDocumento proyectoDocumentoActualizar) {
    log.debug("update(ProyectoDocumento proyectoDocumentoActualizar) - start");

    Assert.notNull(proyectoDocumentoActualizar.getId(),
        "ProyectoDocumento id no puede ser null para actualizar un ProyectoDocumento");

    validarRequeridosProyectoDocumento(proyectoDocumentoActualizar);

    return repository.findById(proyectoDocumentoActualizar.getId()).map(proyectoDocumento -> {

      proyectoDocumentoActualizar.setProyectoId(proyectoDocumento.getProyectoId());
      validarProyectoDocumento(proyectoDocumentoActualizar, proyectoDocumento);

      proyectoDocumento.setTipoFase(proyectoDocumentoActualizar.getTipoFase());
      proyectoDocumento.setTipoDocumento(proyectoDocumentoActualizar.getTipoDocumento());
      proyectoDocumento.setNombre(proyectoDocumentoActualizar.getNombre());
      proyectoDocumento.setVisible(proyectoDocumentoActualizar.getVisible());
      proyectoDocumento.setComentario(proyectoDocumentoActualizar.getComentario());
      proyectoDocumento.setDocumentoRef(proyectoDocumentoActualizar.getDocumentoRef());

      ProyectoDocumento returnValue = repository.save(proyectoDocumento);
      log.debug("update(ProyectoDocumento proyectoDocumentoActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ProyectoDocumentoNotFoundException(proyectoDocumentoActualizar.getId()));
  }

  /**
   * Elimina el {@link ProyectoDocumento}.
   *
   * @param id Id del {@link ProyectoDocumento}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id, "ProyectoDocumento id no puede ser null para eliminar un ProyectoDocumento");
    if (!repository.existsById(id)) {
      throw new ProyectoDocumentoNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene {@link ProyectoDocumento} por su id.
   *
   * @param id el id de la entidad {@link ProyectoDocumento}.
   * @return la entidad {@link ProyectoDocumento}.
   */
  @Override
  public ProyectoDocumento findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ProyectoDocumento returnValue = repository.findById(id)
        .orElseThrow(() -> new ProyectoDocumentoNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link ProyectoDocumento} para una {@link Proyecto}.
   *
   * @param proyectoId el id de la {@link Proyecto}.
   * @param query      la información del filtro.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link ProyectoDocumento} de la
   *         {@link Proyecto} paginadas.
   */
  public Page<ProyectoDocumento> findAllByProyectoId(Long proyectoId, String query, Pageable pageable) {
    log.debug("findAllByProyecto(Long proyectoId, String query, Pageable pageable) - start");
    Specification<ProyectoDocumento> specs = ProyectoDocumentoSpecifications.byProyectoId(proyectoId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<ProyectoDocumento> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByProyecto(Long proyectoId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * valida los datos requeridos de la {@link ProyectoDocumento}
   *
   * @param datosProyectoDocumento
   */
  private void validarRequeridosProyectoDocumento(ProyectoDocumento datosProyectoDocumento) {
    log.debug("validarRequeridosProyectoDocumento(ProyectoDocumento datosProyectoDocumento) - start");

    /** Obligatorios */
    Assert.isTrue(datosProyectoDocumento.getProyectoId() != null, "Id Proyecto no puede ser null en ProyectoDocumento");

    Assert.isTrue(!StringUtils.isEmpty(datosProyectoDocumento.getNombre()),
        "Es necesario indicar el nombre del documento");

    Assert.isTrue(datosProyectoDocumento.getVisible() != null, "Es necesario indicar si el documento es público");

    Assert.isTrue(!StringUtils.isEmpty(datosProyectoDocumento.getDocumentoRef()),
        "Es necesario indicar la referencia al documento");

    log.debug("validarRequeridosProyectoDocumento(ProyectoDocumento datosProyectoDocumento) - end");
  }

  /**
   * Comprueba, valida y tranforma los datos de la {@link ProyectoDocumento} antes
   * de utilizados para crear o modificar la entidad
   *
   * @param datosProyectoDocumento
   * @param datosOriginales
   */
  private void validarProyectoDocumento(ProyectoDocumento datosProyectoDocumento, ProyectoDocumento datosOriginales) {
    log.debug(
        "validarProyectoDcoumento(ProyectoDocumento proyectoDocumento, ProyectoDocumento datosOriginales) - start");

    Proyecto proyecto = proyectoRepository.findById(datosProyectoDocumento.getProyectoId())
        .orElseThrow(() -> new ProyectoNotFoundException(datosProyectoDocumento.getProyectoId()));

    // Se recupera el Id de ModeloEjecucion para las siguientes validaciones
    Long modeloEjecucionId = (proyecto.getModeloEjecucion() != null && proyecto.getModeloEjecucion().getId() != null)
        ? proyecto.getModeloEjecucion().getId()
        : null;

    /**
     * El TipoFase no es obligatorio, pero si tiene valor y existe un TipoDocumento,
     * es necesario recuperar el ModeloTipoFase para validar el TipoDocumento
     * asignado
     */

    ModeloTipoFase proyectoDocumentoModeloTipoFase = null;
    if (datosProyectoDocumento.getTipoFase() != null && datosProyectoDocumento.getTipoFase().getId() != null) {

      Optional<ModeloTipoFase> modeloTipoFase = modeloTipoFaseRepository
          .findByModeloEjecucionIdAndTipoFaseId(modeloEjecucionId, datosProyectoDocumento.getTipoFase().getId());

      // TipoFase está asignado al ModeloEjecucion
      Assert.isTrue(modeloTipoFase.isPresent(), "TipoFase '" + datosProyectoDocumento.getTipoFase().getNombre()
          + "' no disponible para el ModeloEjecucion '"
          + ((modeloEjecucionId != null) ? proyecto.getModeloEjecucion().getNombre() : "Proyecto sin modelo asignado")
          + "'");

      // Comprobar solamente si estamos creando o se ha modificado el TipoFase
      if (datosOriginales == null || datosOriginales.getTipoFase() == null
          || (!Objects.equals(modeloTipoFase.get().getTipoFase().getId(), datosOriginales.getTipoFase().getId()))) {

        // La asignación al ModeloEjecucion está activa
        Assert.isTrue(modeloTipoFase.get().getActivo(),
            "ModeloTipoFase '" + modeloTipoFase.get().getTipoFase().getNombre()
                + "' no está activo para el ModeloEjecucion '" + modeloTipoFase.get().getModeloEjecucion().getNombre()
                + "'");

        // El TipoFase está activo
        Assert.isTrue(modeloTipoFase.get().getTipoFase().getActivo(),
            "TipoFase '" + modeloTipoFase.get().getTipoFase().getNombre() + "' no está activo");
      }
      proyectoDocumentoModeloTipoFase = modeloTipoFase.get();

    } else {
      datosProyectoDocumento.setTipoFase(null);
    }

    /**
     * El TipoDocumento no es obligatorio, si hay TipoFase asignado hay que
     * validarlo con el ModeloTipoFase
     */
    if (datosProyectoDocumento.getTipoDocumento() != null
        && datosProyectoDocumento.getTipoDocumento().getId() != null) {

      // TipoDocumento
      Optional<ModeloTipoDocumento> modeloTipoDocumento = modeloTipoDocumentoRepository
          .findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(modeloEjecucionId,
              proyectoDocumentoModeloTipoFase == null ? null : proyectoDocumentoModeloTipoFase.getId(),
              datosProyectoDocumento.getTipoDocumento().getId());

      // Está asignado al ModeloEjecucion y ModeloTipoFase
      Assert.isTrue(modeloTipoDocumento.isPresent(),
          "TipoDocumento '" + datosProyectoDocumento.getTipoDocumento().getNombre()
              + "' no disponible para el ModeloEjecucion '"
              + ((modeloEjecucionId != null) ? proyecto.getModeloEjecucion().getNombre()
                  : "Proyecto sin modelo asignado")
              + "' y TipoFase '"
              + ((proyectoDocumentoModeloTipoFase != null) ? proyectoDocumentoModeloTipoFase.getTipoFase().getNombre()
                  : "Sin Asignar")
              + "'");

      // Comprobar solamente si estamos creando o se ha modificado el documento
      if (datosOriginales == null || datosOriginales.getTipoDocumento() == null
          || (!Objects.equals(modeloTipoDocumento.get().getTipoDocumento().getId(),
              datosOriginales.getTipoDocumento().getId()))) {

        // La asignación al ModeloEjecucion está activa
        Assert.isTrue(modeloTipoDocumento.get().getActivo(),
            "ModeloTipoDocumento '" + modeloTipoDocumento.get().getTipoDocumento().getNombre()
                + "' no está activo para el ModeloEjecucion '"
                + modeloTipoDocumento.get().getModeloEjecucion().getNombre() + "'");

        // El TipoDocumento está activo
        Assert.isTrue(modeloTipoDocumento.get().getTipoDocumento().getActivo(),
            "TipoDocumento '" + modeloTipoDocumento.get().getTipoDocumento().getNombre() + "' no está activo");

      }
      datosProyectoDocumento.setTipoDocumento(modeloTipoDocumento.get().getTipoDocumento());

    } else {
      datosProyectoDocumento.setTipoDocumento(null);
    }

    log.debug("validarProyectoDcoumento(ProyectoDocumento proyectoDocumento, ProyectoDocumento datosOriginales) - end");
  }

  /**
   * Comprueba si existen datos vinculados a {@link Proyecto} de
   * {@link ProyectoDocumento}
   *
   * @param proyectoId Id del {@link Proyecto}.
   * @return si existe o no el proyecto
   */
  @Override
  public boolean existsByProyecto(Long proyectoId) {
    return repository.existsByProyectoId(proyectoId);
  }
}
