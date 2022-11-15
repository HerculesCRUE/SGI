package org.crue.hercules.sgi.csp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.EntidadConvocanteDuplicatedException;
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
import org.crue.hercules.sgi.csp.util.AssertHelper;
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
   * Actualiza el listado de {@link ProyectoEntidadConvocante} del
   * {@link Proyecto} con el listado entidadesConvocantes
   * creando, editando o eliminando los elementos segun proceda.
   *
   * @param proyectoId           Id del {@link Proyecto}.
   * @param entidadesConvocantes lista con los nuevos
   *                             {@link ProyectoEntidadConvocante} a guardar.
   * @return la lista de entidades {@link ProyectoEntidadConvocante} persistida.
   */
  @Override
  @Transactional
  public List<ProyectoEntidadConvocante> updateEntidadesConvocantesProyecto(Long proyectoId,
      List<ProyectoEntidadConvocante> entidadesConvocantes) {
    log.debug("update(Long proyectoId, List<ProyectoEntidadConvocante> entidadesConvocantes) - start");

    AssertHelper.idNotNull(proyectoId, Proyecto.class);
    List<ProyectoEntidadConvocante> entidadesConvocantesUpdated = entidadesConvocantes.stream()
        .map(entidadConvocante -> {
          checkDuplicated(entidadConvocante, entidadesConvocantes);

          if (entidadConvocante.getPrograma() == null || entidadConvocante.getPrograma().getId() == null) {
            entidadConvocante.setPrograma(null);
          } else {
            entidadConvocante
                .setPrograma(programaRepository.findById(entidadConvocante.getPrograma().getId())
                    .orElseThrow(
                        () -> new ProgramaNotFoundException(entidadConvocante.getPrograma().getId())));
            Assert.isTrue(entidadConvocante.getPrograma().getActivo(), "El Programa debe estar Activo");
          }

          entidadConvocante.setProyectoId(proyectoId);

          return entidadConvocante;
        }).collect(Collectors.toList());

    Proyecto proyecto = proyectoRepository.findById(proyectoId)
        .orElseThrow(() -> new ProyectoNotFoundException(proyectoId));
    proyectoHelper.checkCanAccessProyecto(proyecto);

    Specification<ProyectoEntidadConvocante> specs = ProyectoEntidadConvocanteSpecifications.byProyectoId(proyectoId);
    List<ProyectoEntidadConvocante> entidadesConvocantesBD = repository.findAll(specs);

    // Entidades Convocantes eliminados
    List<ProyectoEntidadConvocante> entiadesConvocantesEliminar = entidadesConvocantesBD.stream()
        .filter(entidadConvocante -> entidadesConvocantesUpdated.stream().map(ProyectoEntidadConvocante::getId)
            .noneMatch(id -> Objects.equals(id, entidadConvocante.getId())))
        .collect(Collectors.toList());

    if (!entiadesConvocantesEliminar.isEmpty()) {
      repository.deleteAll(entiadesConvocantesEliminar);
    }

    if (entidadesConvocantes.isEmpty()) {
      return new ArrayList<>();
    }

    // Entidades Convocantes crear/actualizar
    List<ProyectoEntidadConvocante> entidadesConvocantesCreateOrUpdate = entidadesConvocantesUpdated.stream()
        .map(entidadConvocanteUpdated -> {
          if (entidadConvocanteUpdated.getId() == null) {
            return entidadConvocanteUpdated;
          }

          ProyectoEntidadConvocante entidadConvocanteBD = entidadesConvocantesBD.stream()
              .filter(entidad -> entidad.getId().equals(entidadConvocanteUpdated.getId()))
              .findFirst().get();

          return copyUpdatedValues(entidadConvocanteBD, entidadConvocanteUpdated);
        })
        .collect(Collectors.toList());

    List<ProyectoEntidadConvocante> returnValue = repository.saveAll(entidadesConvocantesCreateOrUpdate);

    log.debug("update(Long proyectoId, List<ProyectoEntidadConvocante> entidadesConvocantes) - end");
    return returnValue;
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
    AssertHelper.idNotNull(proyectoEntidadConvocante.getProyectoId(), Proyecto.class);
    Proyecto proyecto = proyectoRepository.findById(proyectoEntidadConvocante.getProyectoId())
        .orElseThrow(() -> new ProyectoNotFoundException(proyectoEntidadConvocante.getProyectoId()));
    proyectoHelper.checkCanAccessProyecto(proyecto);

    Specification<ProyectoEntidadConvocante> specs = ProyectoEntidadConvocanteSpecifications
        .byProyectoId(proyectoEntidadConvocante.getId());
    List<ProyectoEntidadConvocante> entidadesConvocantes = repository.findAll(specs);

    checkDuplicated(proyectoEntidadConvocante, entidadesConvocantes);

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
      proyectoHelper.checkCanAccessProyecto(proyecto);
      if (programa == null || programa.getId() == null) {
        proyectoEntidadConvocante.setPrograma(null);
      } else {
        Long idPrograma = programa.getId();
        proyectoEntidadConvocante.setPrograma(
            programaRepository.findById(idPrograma).orElseThrow(() -> new ProgramaNotFoundException(idPrograma)));
        Assert.isTrue(proyectoEntidadConvocante.getPrograma().getActivo(), "El Programa debe estar Activo");
      }

      Specification<ProyectoEntidadConvocante> specs = ProyectoEntidadConvocanteSpecifications
          .byProyectoId(proyectoEntidadConvocante.getId());
      List<ProyectoEntidadConvocante> entidadesConvocantes = repository.findAll(specs);

      checkDuplicated(proyectoEntidadConvocante, entidadesConvocantes);

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
    Optional<ProyectoEntidadConvocante> entidadConvocante = repository.findById(id);
    if (entidadConvocante.isPresent()) {
      Proyecto proyecto = proyectoRepository.findById(entidadConvocante.get().getProyectoId())
          .orElseThrow(() -> new ProyectoNotFoundException(entidadConvocante.get().getProyectoId()));
      proyectoHelper.checkCanAccessProyecto(proyecto);
      repository.deleteById(id);
    } else {
      throw new ProyectoEntidadConvocanteNotFoundException(id);
    }

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
    AssertHelper.idNotNull(idProyecto, Proyecto.class);
    Proyecto proyecto = proyectoRepository.findById(idProyecto)
        .orElseThrow(() -> new ProyectoNotFoundException(idProyecto));
    proyectoHelper.checkCanAccessProyecto(proyecto);

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
   * @param proyectoId           Id del {@link Proyecto}
   * @param entidadRef           Id de la Entidad Convocante
   * @param programaConvocatoria {@link ProyectoEntidadConvocante#programaConvocatoria}
   * @return true si existe la {@link ProyectoEntidadConvocante} y false en caso
   *         contrario
   */
  @Override
  public boolean existsByProyectoIdAndEntidadRefAndProgramaConvocatoria(
      Long proyectoId, String entidadRef, Programa programaConvocatoria) {
    log.debug(
        "existsByProyectoIdAndEntidadRefAndProgramaConvocatoriaId(Long proyectoId, String entidadRef, Long programaConvocatoriaId) - start");

    Long programaConvocatoriaId = programaConvocatoria != null ? programaConvocatoria.getId() : null;

    Specification<ProyectoEntidadConvocante> specs = ProyectoEntidadConvocanteSpecifications
        .byProyectoId(proyectoId)
        .and(ProyectoEntidadConvocanteSpecifications.byEntidadRef(entidadRef))
        .and(ProyectoEntidadConvocanteSpecifications.byProgramaConvocatoriaId(programaConvocatoriaId));

    boolean returnValue = repository.count(specs) > 0;

    log.debug(
        "existsByProyectoIdAndEntidadRefAndProgramaConvocatoriaId(Long proyectoId, String entidadRef, Long programaConvocatoriaId) - end");
    return returnValue;
  }

  private void checkDuplicated(ProyectoEntidadConvocante entidadConvocante,
      List<ProyectoEntidadConvocante> entidadesConvocantes) {

    if (entidadesConvocantes.isEmpty()) {
      return;
    }

    List<ProyectoEntidadConvocante> otherEntidadesConvocantes = entidadesConvocantes.stream()
        .filter(entidad -> !(Objects.equals(entidad.getEntidadRef(), entidadConvocante.getEntidadRef())
            && Objects.equals(
                entidad.getPrograma() != null ? entidad.getPrograma().getId() : null,
                entidadConvocante.getPrograma() != null ? entidadConvocante.getPrograma().getId() : null)
            && Objects.equals(
                entidad.getProgramaConvocatoria() != null ? entidad.getProgramaConvocatoria().getId() : null,
                entidadConvocante.getProgramaConvocatoria() != null
                    ? entidadConvocante.getProgramaConvocatoria().getId()
                    : null)))
        .collect(Collectors.toList());

    if (otherEntidadesConvocantes.size() != (entidadesConvocantes.size() - 1)) {
      throw new EntidadConvocanteDuplicatedException();
    }

    boolean duplicated = otherEntidadesConvocantes.stream()
        .anyMatch(entidad -> isDuplicated(entidad, entidadConvocante));

    if (duplicated) {
      throw new EntidadConvocanteDuplicatedException();
    }
  }

  private boolean isDuplicated(ProyectoEntidadConvocante entidadConvocante1,
      ProyectoEntidadConvocante entidadConvocante2) {

    Programa programaEntidadConvocante1 = entidadConvocante1.getPrograma() != null ? entidadConvocante1.getPrograma()
        : entidadConvocante1.getProgramaConvocatoria();
    Programa programaEntidadConvocante2 = entidadConvocante2.getPrograma() != null ? entidadConvocante2.getPrograma()
        : entidadConvocante2.getProgramaConvocatoria();

    boolean entidadRefEquals = entidadConvocante1.getEntidadRef().equals(entidadConvocante2.getEntidadRef());
    boolean programaNull = programaEntidadConvocante1 == null || programaEntidadConvocante2 == null;

    boolean inProgramaParentsEntidad1 = programaEntidadConvocante2 != null
        && getProgramaParentsIds(programaEntidadConvocante1, new ArrayList<>())
            .contains(programaEntidadConvocante2.getId());
    boolean inProgramaParentsEntidad2 = programaEntidadConvocante1 != null
        && getProgramaParentsIds(programaEntidadConvocante2, new ArrayList<>())
            .contains(programaEntidadConvocante1.getId());

    return entidadRefEquals && (programaNull || inProgramaParentsEntidad1 || inProgramaParentsEntidad2);
  }

  private List<Long> getProgramaParentsIds(Programa programa, List<Long> programaIds) {
    if (programa == null) {
      return programaIds;
    }

    programaIds.add(programa.getId());

    if (programa.getPadre() != null) {
      this.getProgramaParentsIds(programa.getPadre(), programaIds);
    }

    return programaIds;
  }

  private ProyectoEntidadConvocante copyUpdatedValues(ProyectoEntidadConvocante entidadConvocante,
      ProyectoEntidadConvocante entidadConvocanteUpdated) {
    entidadConvocante.setPrograma(entidadConvocanteUpdated.getPrograma());
    return entidadConvocante;
  }

}
