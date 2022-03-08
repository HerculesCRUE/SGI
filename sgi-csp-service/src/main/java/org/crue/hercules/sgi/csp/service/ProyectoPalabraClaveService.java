package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPalabraClave;
import org.crue.hercules.sgi.csp.repository.ProyectoPalabraClaveRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoPalabraClaveSpecifications;
import org.crue.hercules.sgi.csp.util.ProyectoHelper;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service para gestionar {@link ProyectoPalabraClave}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ProyectoPalabraClaveService {

  private final ProyectoPalabraClaveRepository repository;
  private final ProyectoHelper proyectoHelper;

  public ProyectoPalabraClaveService(ProyectoPalabraClaveRepository proyectoPalabraClaveRepository,
      ProyectoHelper proyectoHelper) {
    this.repository = proyectoPalabraClaveRepository;
    this.proyectoHelper = proyectoHelper;
  }

  /**
   * Obtiene los {@link ProyectoPalabraClave} para una entidad {@link Proyecto}
   * paginadas y/o filtradas.
   *
   * @param proyectoId el id de la {@link Proyecto}.
   * @param query      la información del filtro.
   * @param pageable   la información de la paginación.
   * @return la lista de {@link ProyectoPalabraClave} de la
   *         {@link Proyecto} paginadas y/o filtradas.
   */
  public Page<ProyectoPalabraClave> findByProyectoId(Long proyectoId, String query, Pageable pageable) {
    log.debug("findByProyectoId(Long proyectoId, String query, Pageable pageable) - start");

    proyectoHelper.checkCanAccessProyecto(proyectoId);
    Specification<ProyectoPalabraClave> specs = ProyectoPalabraClaveSpecifications.byProyectoId(proyectoId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<ProyectoPalabraClave> returnValue = repository.findAll(specs, pageable);
    log.debug("findByProyectoId(Long proyectoId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Actualiza la lista de {@link ProyectoPalabraClave} de la entidad
   * {@link Proyecto}, elimina los pre-existentes y añade la nueva lista.
   * 
   * @param proyectoId    el id del {@link Proyecto}.
   * @param palabrasClave la lista con las nuevas palabras claves.
   * @return La lista actualizada de {@link ProyectoPalabraClave}.
   */
  @Transactional
  public List<ProyectoPalabraClave> updatePalabrasClave(Long proyectoId,
      List<ProyectoPalabraClave> palabrasClave) {
    log.debug("updatePalabrasClave(Long proyectoId, List<ProyectoPalabraClave> palabrasClave) - start");

    // Las Palabras Clave tienen el proyectoId especificado
    Assert.isTrue(
        palabrasClave.stream()
            .allMatch(palabraClave -> palabraClave.getProyectoId() == null
                || palabraClave.getProyectoId().equals(proyectoId)),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key("org.crue.hercules.sgi.csp.exceptions.NoRelatedEntitiesException.message")
            .parameter("entity", ApplicationContextSupport.getMessage(ProyectoPalabraClave.class))
            .parameter("related", ApplicationContextSupport.getMessage(Proyecto.class)).build());

    // Eliminamos las ProyectoPalabraClave existentes para el
    // proyectoId dado
    repository.deleteInBulkByProyectoId(proyectoId);

    List<ProyectoPalabraClave> returnValue = new ArrayList<>();
    if (!palabrasClave.isEmpty()) {
      // Eliminamos duplicados de la nueva lista
      List<ProyectoPalabraClave> uniquePalabrasClave = palabrasClave.stream().distinct()
          .collect(Collectors.toList());
      // Guardamos la nueva lista
      returnValue = repository.saveAll(uniquePalabrasClave);
    }

    log.debug("updatePalabrasClave(Long proyectoId, List<ProyectoPalabraClave> palabrasClave) - end");
    return returnValue;
  }

}
