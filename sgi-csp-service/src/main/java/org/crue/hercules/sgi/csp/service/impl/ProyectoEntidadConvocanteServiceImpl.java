package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.exceptions.ProgramaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoEntidadConvocanteNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadConvocante;
import org.crue.hercules.sgi.csp.repository.ProgramaRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoEntidadConvocanteRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoEntidadConvocanteSpecifications;
import org.crue.hercules.sgi.csp.service.ProyectoEntidadConvocanteService;
import org.crue.hercules.sgi.csp.util.ProyectoHelper;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link ProyectoEntidadConvocante}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ProyectoEntidadConvocanteServiceImpl implements ProyectoEntidadConvocanteService {

  private final ProyectoEntidadConvocanteRepository repository;
  private final ProyectoRepository proyectoRepository;
  private final ProgramaRepository programaRepository;
  private final ProyectoHelper proyectoHelper;

  public ProyectoEntidadConvocanteServiceImpl(ProyectoEntidadConvocanteRepository proyectoEntidadConvocanteRepository,
      ProyectoRepository proyectoRepository, ProgramaRepository programaRepository, ProyectoHelper proyectoHelper) {
    this.repository = proyectoEntidadConvocanteRepository;
    this.proyectoRepository = proyectoRepository;
    this.programaRepository = programaRepository;
    this.proyectoHelper = proyectoHelper;
  }

  /**
   * Guardar un nuevo {@link ProyectoEntidadConvocante}.
   *
   * @param proyectoEntidadConvocante la entidad {@link ProyectoEntidadConvocante}
   *                                  a guardar.
   * @return la entidad {@link ProyectoEntidadConvocante} persistida.
   */
  @Override
  @Transactional
  public ProyectoEntidadConvocante create(ProyectoEntidadConvocante proyectoEntidadConvocante) {
    log.debug("create(ProyectoEntidadConvocante proyectoEntidadConvocante) - start");
    Assert.isNull(proyectoEntidadConvocante.getId(), "ProyectoEntidadConvocante id tiene que ser null");
    Assert.notNull(proyectoEntidadConvocante.getProyectoId(), "Proyecto id no puede ser null");
    Proyecto proyecto = proyectoRepository.findById(proyectoEntidadConvocante.getProyectoId())
        .orElseThrow(() -> new ProyectoNotFoundException(proyectoEntidadConvocante.getProyectoId()));
    proyectoHelper.checkCanRead(proyecto);

    Assert
        .isTrue(
            !repository.existsByProyectoIdAndEntidadRef(proyectoEntidadConvocante.getProyectoId(),
                proyectoEntidadConvocante.getEntidadRef()),
            "Ya existe una asociación activa para esa Proyecto y Entidad");
    if (proyectoEntidadConvocante.getPrograma() != null) {
      if (proyectoEntidadConvocante.getPrograma().getId() == null) {
        proyectoEntidadConvocante.setPrograma(null);
      } else {
        proyectoEntidadConvocante
            .setPrograma(programaRepository.findById(proyectoEntidadConvocante.getPrograma().getId())
                .orElseThrow(() -> new ProgramaNotFoundException(proyectoEntidadConvocante.getPrograma().getId())));
        Assert.isTrue(proyectoEntidadConvocante.getPrograma().getActivo(), "El Programa debe estar Activo");
      }
    }
    ProyectoEntidadConvocante returnValue = repository.save(proyectoEntidadConvocante);
    log.debug("create(ProyectoEntidadConvocante proyectoEntidadConvocante) - end");
    return returnValue;
  }

  /**
   * Establece el {@link Programa} de {@link ProyectoEntidadConvocante}.
   *
   * @param idProyectoEntidadConvocante el id de la entidad
   *                                    {@link ProyectoEntidadConvocante} a
   *                                    actualizar.
   * @param programa                    el {@link Programa} a fijar.
   * @return la entidad {@link ProyectoEntidadConvocante} persistida.
   */
  @Override
  @Transactional
  public ProyectoEntidadConvocante setPrograma(Long idProyectoEntidadConvocante, Programa programa) {
    log.debug("setPrograma(Long idProyectoEntidadConvocante, Programa programa) - start");
    Assert.notNull(idProyectoEntidadConvocante, "ProyectoEntidadConvocante id no puede ser null");
    return repository.findById(idProyectoEntidadConvocante).map(proyectoEntidadConvocante -> {
      Proyecto proyecto = proyectoRepository.findById(proyectoEntidadConvocante.getProyectoId())
          .orElseThrow(() -> new ProyectoNotFoundException(proyectoEntidadConvocante.getProyectoId()));
      proyectoHelper.checkCanRead(proyecto);
      if (programa == null || programa.getId() == null) {
        proyectoEntidadConvocante.setPrograma(null);
      } else {
        Long idPrograma = programa.getId();
        proyectoEntidadConvocante.setPrograma(
            programaRepository.findById(idPrograma).orElseThrow(() -> new ProgramaNotFoundException(idPrograma)));
        Assert.isTrue(proyectoEntidadConvocante.getPrograma().getActivo(), "El Programa debe estar Activo");
      }
      ProyectoEntidadConvocante returnValue = repository.save(proyectoEntidadConvocante);
      log.debug("setPrograma(Long idProyectoEntidadConvocante, Programa programa) - end");
      return returnValue;
    }).orElseThrow(() -> new ProyectoEntidadConvocanteNotFoundException(idProyectoEntidadConvocante));
  }

  /**
   * Elimina el {@link ProyectoEntidadConvocante}.
   *
   * @param id Id del {@link ProyectoEntidadConvocante}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");
    Assert.notNull(id, "ProyectoEntidadConvocante id no puede ser null");
    repository.findById(id).map(proyectoEntidadConvocante -> {
      Proyecto proyecto = proyectoRepository.findById(proyectoEntidadConvocante.getProyectoId())
          .orElseThrow(() -> new ProyectoNotFoundException(proyectoEntidadConvocante.getProyectoId()));
      proyectoHelper.checkCanRead(proyecto);
      repository.deleteById(id);
      return proyectoEntidadConvocante;
    }).orElseThrow(() -> new ProyectoEntidadConvocanteNotFoundException(id));

    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene las {@link ProyectoEntidadConvocante} para una {@link Proyecto}.
   *
   * @param idProyecto el id de la {@link Proyecto}.
   * @param query      la información del filtro.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link ProyectoEntidadConvocante} de la
   *         {@link Proyecto} paginadas.
   */
  public Page<ProyectoEntidadConvocante> findAllByProyecto(Long idProyecto, String query, Pageable pageable) {
    log.debug("findAllByProyecto(Long idProyecto, String query, Pageable pageable) - start");
    Assert.notNull(idProyecto, "Proyecto id no puede ser null");
    Proyecto proyecto = proyectoRepository.findById(idProyecto)
        .orElseThrow(() -> new ProyectoNotFoundException(idProyecto));
    proyectoHelper.checkCanRead(proyecto);

    Specification<ProyectoEntidadConvocante> specs = ProyectoEntidadConvocanteSpecifications.byProyectoId(idProyecto)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<ProyectoEntidadConvocante> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByProyecto(Long idProyecto, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Busca un {@link ProyectoEntidadConvocante} por su {@link Proyecto} y
   * entidadRef.
   * 
   * @param proyectoId Id del {@link Proyecto}
   * @param entidadRef Id de la Entidad Convocante
   * @return true si existe la {@link ProyectoEntidadConvocante} y false en caso
   *         contrario
   */
  @Override
  public boolean existsByProyectoIdAndEntidadRef(Long proyectoId, String entidadRef) {
    log.debug("existsByProyectoIdAndEntidadRef(Long proyectoId, String entidadRef)");
    return repository.existsByProyectoIdAndEntidadRef(proyectoId, entidadRef);
  }

  /**
   * Devuelve un {@link ProyectoEntidadConvocante} por su {@link Proyecto} y
   * entidadRef.
   * 
   * @param proyectoId Id del {@link Proyecto}
   * @param entidadRef Id de la Entidad Convocante
   * @return true si existe la {@link ProyectoEntidadConvocante} y false en caso
   *         contrario
   */
  @Override
  public ProyectoEntidadConvocante findByProyectoIdAndEntidadRef(Long proyectoId, String entidadRef) {
    log.debug("findByProyectoIdAndEntidadRef(Long proyectoId, String entidadRef)");
    return repository.findByProyectoIdAndEntidadRef(proyectoId, entidadRef)
        .orElseThrow(() -> new ProyectoNotFoundException(proyectoId));
  }

  /**
   * Actualiza la entidad {@link ProyectoEntidadConvocante}.
   *
   * @param proyectoEntidadConvocanteActualizar la entidad
   *                                            {@link ProyectoEntidadConvocante}
   *                                            a guardar.
   * @return la entidad {@link ProyectoEntidadConvocante} persistida.
   */
  @Override
  @Transactional
  public ProyectoEntidadConvocante update(ProyectoEntidadConvocante proyectoEntidadConvocanteActualizar) {
    log.debug("update(ProyectoEntidadConvocante proyectoEntidadConvocanteActualizar) - start");
    Assert.notNull(proyectoEntidadConvocanteActualizar.getProyectoId(), "Proyecto id no puede ser null");
    Assert.notNull(proyectoEntidadConvocanteActualizar.getEntidadRef(), "EntidadRef no puede ser null");
    Proyecto proyecto = proyectoRepository.findById(proyectoEntidadConvocanteActualizar.getProyectoId())
        .orElseThrow(() -> new ProyectoNotFoundException(proyectoEntidadConvocanteActualizar.getProyectoId()));
    proyectoHelper.checkCanRead(proyecto);
    Assert.isTrue(
        repository.existsByProyectoIdAndEntidadRef(proyectoEntidadConvocanteActualizar.getProyectoId(),
            proyectoEntidadConvocanteActualizar.getEntidadRef()),
        "No existe una asociación activa para ese Proyecto y Entidad");

    return repository.findByProyectoIdAndEntidadRef(proyectoEntidadConvocanteActualizar.getProyectoId(),
        proyectoEntidadConvocanteActualizar.getEntidadRef()).map((data) -> {
          // Actualizamos el programa
          if (proyectoEntidadConvocanteActualizar.getPrograma() != null) {
            if (proyectoEntidadConvocanteActualizar.getPrograma().getId() == null) {
              data.setPrograma(null);
            } else {
              data.setPrograma(
                  programaRepository.findById(proyectoEntidadConvocanteActualizar.getPrograma().getId()).orElseThrow(
                      () -> new ProgramaNotFoundException(proyectoEntidadConvocanteActualizar.getPrograma().getId())));
              Assert.isTrue(data.getPrograma().getActivo(), "El Programa debe estar Activo");
            }
          }

          // Actualizamos el programa convocatoria
          if (proyectoEntidadConvocanteActualizar.getProgramaConvocatoria() != null) {
            if (proyectoEntidadConvocanteActualizar.getProgramaConvocatoria().getId() == null) {
              data.setProgramaConvocatoria(null);
            } else {
              data.setProgramaConvocatoria(
                  programaRepository.findById(proyectoEntidadConvocanteActualizar.getProgramaConvocatoria().getId())
                      .orElseThrow(() -> new ProgramaNotFoundException(
                          proyectoEntidadConvocanteActualizar.getProgramaConvocatoria().getId())));
              Assert.isTrue(data.getProgramaConvocatoria().getActivo(), "El Programa debe estar Activo");
            }
          }
          data.setEntidadRef(proyectoEntidadConvocanteActualizar.getEntidadRef());
          ProyectoEntidadConvocante returnValue = repository.save(data);
          log.debug("update(ProyectoEntidadConvocante proyectoEntidadConvocanteActualizar) - end");
          return returnValue;
        })
        .orElseThrow(() -> new ProyectoEntidadConvocanteNotFoundException(proyectoEntidadConvocanteActualizar.getId()));
  }

}
