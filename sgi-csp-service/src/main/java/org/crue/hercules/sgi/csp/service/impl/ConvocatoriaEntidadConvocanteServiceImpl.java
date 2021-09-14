package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaEntidadConvocanteNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProgramaNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaEntidadConvocanteRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.ProgramaRepository;
import org.crue.hercules.sgi.csp.repository.specification.ConvocatoriaEntidadConvocanteSpecifications;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEntidadConvocanteService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de
 * {@link ConvocatoriaEntidadConvocante}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ConvocatoriaEntidadConvocanteServiceImpl implements ConvocatoriaEntidadConvocanteService {

  private final ConvocatoriaEntidadConvocanteRepository repository;
  private final ConvocatoriaRepository convocatoriaRepository;
  private final ProgramaRepository programaRepository;
  private final ConvocatoriaService convocatoriaService;

  public ConvocatoriaEntidadConvocanteServiceImpl(
      ConvocatoriaEntidadConvocanteRepository convocatoriaEntidadConvocanteRepository,
      ConvocatoriaRepository convocatoriaRepository, ProgramaRepository programaRepository,
      ConvocatoriaService convocatoriaService) {
    this.repository = convocatoriaEntidadConvocanteRepository;
    this.convocatoriaRepository = convocatoriaRepository;
    this.programaRepository = programaRepository;
    this.convocatoriaService = convocatoriaService;
  }

  /**
   * Guardar un nuevo {@link ConvocatoriaEntidadConvocante}.
   *
   * @param convocatoriaEntidadConvocante la entidad
   *                                      {@link ConvocatoriaEntidadConvocante} a
   *                                      guardar.
   * @return la entidad {@link ConvocatoriaEntidadConvocante} persistida.
   */
  @Override
  @Transactional
  public ConvocatoriaEntidadConvocante create(ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante) {
    log.debug("create(ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante) - start");

    Assert.isNull(convocatoriaEntidadConvocante.getId(),
        "ConvocatoriaEntidadConvocante id tiene que ser null para crear un nuevo ConvocatoriaEntidadConvocante");

    Assert.notNull(convocatoriaEntidadConvocante.getConvocatoriaId(),
        "Id Convocatoria no puede ser null para crear ConvocatoriaEntidadGestora");

    Convocatoria convocatoria = convocatoriaRepository.findById(convocatoriaEntidadConvocante.getConvocatoriaId())
        .orElseThrow(() -> new ConvocatoriaNotFoundException(convocatoriaEntidadConvocante.getConvocatoriaId()));

    // comprobar si convocatoria es modificable
    Assert.isTrue(
        convocatoriaService.isRegistradaConSolicitudesOProyectos(convocatoriaEntidadConvocante.getConvocatoriaId(),
            convocatoria.getUnidadGestionRef(), new String[] { "CSP-CON-E", "CSP-CON-C" }),
        "No se puede crear ConvocatoriaEntidadConvocante. No tiene los permisos necesarios o la convocatoria está registrada y cuenta con solicitudes o proyectos asociados");

    Assert.isTrue(
        !repository.findByConvocatoriaIdAndEntidadRef(convocatoriaEntidadConvocante.getConvocatoriaId(),
            convocatoriaEntidadConvocante.getEntidadRef()).isPresent(),
        "Ya existe una asociación activa para esa Convocatoria y Entidad");

    if (convocatoriaEntidadConvocante.getPrograma() != null) {
      if (convocatoriaEntidadConvocante.getPrograma().getId() == null) {
        convocatoriaEntidadConvocante.setPrograma(null);
      } else {
        convocatoriaEntidadConvocante
            .setPrograma(programaRepository.findById(convocatoriaEntidadConvocante.getPrograma().getId())
                .orElseThrow(() -> new ProgramaNotFoundException(convocatoriaEntidadConvocante.getPrograma().getId())));
        Assert.isTrue(convocatoriaEntidadConvocante.getPrograma().getActivo(), "El Programa debe estar Activo");
      }
    }

    ConvocatoriaEntidadConvocante returnValue = repository.save(convocatoriaEntidadConvocante);

    log.debug("create(ConvocatoriaEntidadConvocante convocatoriaEntidadConvocante) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link ConvocatoriaEntidadConvocante}.
   *
   * @param convocatoriaEntidadConvocanteActualizar la entidad
   *                                                {@link ConvocatoriaEntidadConvocante}
   *                                                a actualizar.
   * @return la entidad {@link ConvocatoriaEntidadConvocante} persistida.
   */
  @Override
  @Transactional
  public ConvocatoriaEntidadConvocante update(ConvocatoriaEntidadConvocante convocatoriaEntidadConvocanteActualizar) {
    log.debug("update(ConvocatoriaEntidadConvocante convocatoriaEntidadConvocanteActualizar) - start");

    Assert.notNull(convocatoriaEntidadConvocanteActualizar.getId(),
        "ConvocatoriaEntidadConvocante id no puede ser null para actualizar un ConvocatoriaEntidadConvocante");

    return repository.findById(convocatoriaEntidadConvocanteActualizar.getId()).map(convocatoriaEntidadConvocante -> {

      // comprobar si convocatoria es modificable
      Assert.isTrue(
          convocatoriaService.isRegistradaConSolicitudesOProyectos(convocatoriaEntidadConvocante.getConvocatoriaId(),
              null, new String[] { "CSP-CON-E" }),
          "No se puede modificar ConvocatoriaEntidadConvocante. No tiene los permisos necesarios o la convocatoria está registrada y cuenta con solicitudes o proyectos asociados");

      repository.findByConvocatoriaIdAndEntidadRef(convocatoriaEntidadConvocanteActualizar.getConvocatoriaId(),
          convocatoriaEntidadConvocanteActualizar.getEntidadRef()).ifPresent(convocatoriaR -> {
            Assert.isTrue(convocatoriaEntidadConvocante.getId() == convocatoriaR.getId(),
                "Ya existe una asociación activa para esa Convocatoria y Entidad");
          });

      if (convocatoriaEntidadConvocanteActualizar.getPrograma() != null) {
        if (convocatoriaEntidadConvocanteActualizar.getPrograma().getId() == null) {
          convocatoriaEntidadConvocanteActualizar.setPrograma(null);
        } else {
          convocatoriaEntidadConvocanteActualizar.setPrograma(
              programaRepository.findById(convocatoriaEntidadConvocanteActualizar.getPrograma().getId()).orElseThrow(
                  () -> new ProgramaNotFoundException(convocatoriaEntidadConvocanteActualizar.getPrograma().getId())));
          Assert.isTrue(convocatoriaEntidadConvocanteActualizar.getPrograma().getActivo(),
              "El Programa debe estar Activo");
        }
      }

      convocatoriaEntidadConvocante.setPrograma(convocatoriaEntidadConvocanteActualizar.getPrograma());

      ConvocatoriaEntidadConvocante returnValue = repository.save(convocatoriaEntidadConvocante);
      log.debug("update(ConvocatoriaEntidadConvocante convocatoriaEntidadConvocanteActualizar) - end");
      return returnValue;
    }).orElseThrow(
        () -> new ConvocatoriaEntidadConvocanteNotFoundException(convocatoriaEntidadConvocanteActualizar.getId()));
  }

  /**
   * Elimina el {@link ConvocatoriaEntidadConvocante}.
   *
   * @param id Id del {@link ConvocatoriaEntidadConvocante}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id,
        "ConvocatoriaEntidadConvocante id no puede ser null para desactivar un ConvocatoriaEntidadConvocante");

    repository.findById(id).map(convocatoriaEntidadConvocante -> {

      // comprobar si convocatoria es modificable
      Assert.isTrue(
          convocatoriaService.isRegistradaConSolicitudesOProyectos(convocatoriaEntidadConvocante.getConvocatoriaId(),
              null, new String[] { "CSP-CON-E" }),
          "No se puede eliminar ConvocatoriaEntidadConvocante. No tiene los permisos necesarios o la convocatoria está registrada y cuenta con solicitudes o proyectos asociados");

      return convocatoriaEntidadConvocante;
    }).orElseThrow(() -> new ConvocatoriaEntidadConvocanteNotFoundException(id));

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene {@link ConvocatoriaEntidadConvocante} por su id.
   *
   * @param id el id de la entidad {@link ConvocatoriaEntidadConvocante}.
   * @return la entidad {@link ConvocatoriaEntidadConvocante}.
   */
  @Override
  public ConvocatoriaEntidadConvocante findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ConvocatoriaEntidadConvocante returnValue = repository.findById(id)
        .orElseThrow(() -> new ConvocatoriaEntidadConvocanteNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link ConvocatoriaEntidadConvocante} para una
   * {@link Convocatoria}.
   *
   * @param idConvocatoria el id de la {@link Convocatoria}.
   * @param query          la información del filtro.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaEntidadConvocante} de la
   *         {@link Convocatoria} paginadas.
   */
  public Page<ConvocatoriaEntidadConvocante> findAllByConvocatoria(Long idConvocatoria, String query,
      Pageable pageable) {
    log.debug("findAllByConvocatoria(Long idConvocatoria, String query, Pageable pageable) - start");
    Specification<ConvocatoriaEntidadConvocante> specs = ConvocatoriaEntidadConvocanteSpecifications
        .byConvocatoriaId(idConvocatoria).and(SgiRSQLJPASupport.toSpecification(query));

    Page<ConvocatoriaEntidadConvocante> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByConvocatoria(Long idConvocatoria, String query, Pageable pageable) - end");
    return returnValue;
  }

}
