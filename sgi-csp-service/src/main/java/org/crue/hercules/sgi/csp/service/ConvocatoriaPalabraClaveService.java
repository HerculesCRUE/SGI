package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPalabraClave;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaPalabraClaveRepository;
import org.crue.hercules.sgi.csp.repository.specification.ConvocatoriaPalabraClaveSpecifications;
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
 * Service para gestionar {@link ConvocatoriaPalabraClave}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ConvocatoriaPalabraClaveService {

  private final ConvocatoriaPalabraClaveRepository repository;

  public ConvocatoriaPalabraClaveService(ConvocatoriaPalabraClaveRepository convocatoriaPalabraClaveRepository) {
    this.repository = convocatoriaPalabraClaveRepository;
  }

  /**
   * Obtiene los {@link ConvocatoriaPalabraClave} para una entidad
   * {@link Convocatoria} paginadas y/o filtradas.
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param query          la información del filtro.
   * @param pageable       la información de la paginación.
   * @return la lista de {@link ConvocatoriaPalabraClave} de la
   *         {@link Convocatoria} paginadas y/o filtradas.
   */
  public Page<ConvocatoriaPalabraClave> findByConvocatoriaId(Long convocatoriaId, String query, Pageable pageable) {
    log.debug("findByConvocatoriaId(Long convocatoriaId, String query, Pageable pageable) - start");

    Specification<ConvocatoriaPalabraClave> specs = ConvocatoriaPalabraClaveSpecifications
        .byConvocatoriaId(convocatoriaId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<ConvocatoriaPalabraClave> returnValue = repository.findAll(specs, pageable);
    log.debug("findByConvocatoriaId(Long convocatoriaId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Actualiza la lista de {@link ConvocatoriaPalabraClave} de la entidad
   * {@link Convocatoria}, elimina los pre-existentes y añade la nueva lista.
   * 
   * @param convocatoriaId el id del {@link Convocatoria}.
   * @param palabrasClave  la lista con las nuevas palabras claves.
   * @return La lista actualizada de {@link ConvocatoriaPalabraClave}.
   */
  @Transactional
  public List<ConvocatoriaPalabraClave> updatePalabrasClave(Long convocatoriaId,
      List<ConvocatoriaPalabraClave> palabrasClave) {
    log.debug("updatePalabrasClave(Long convocatoriaId, List<ConvocatoriaPalabraClave> palabrasClave) - start");

    // Las Palabras Clave tienen el convocatoriaId especificado
    Assert.isTrue(
        palabrasClave.stream()
            .allMatch(palabraClave -> palabraClave.getConvocatoriaId() == null
                || palabraClave.getConvocatoriaId().equals(convocatoriaId)),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key("org.crue.hercules.sgi.csp.exceptions.NoRelatedEntitiesException.message")
            .parameter("entity", ApplicationContextSupport.getMessage(ConvocatoriaPalabraClave.class))
            .parameter("related", ApplicationContextSupport.getMessage(Convocatoria.class)).build());

    // Eliminamos las ConvocatoriaPalabraClave existentes para el
    // convocatoriaId dado
    repository.deleteInBulkByConvocatoriaId(convocatoriaId);

    List<ConvocatoriaPalabraClave> returnValue = new ArrayList<>();
    if (palabrasClave != null && !palabrasClave.isEmpty()) {
      // Eliminamos duplicados de la nueva lista
      List<ConvocatoriaPalabraClave> uniquePalabrasClave = palabrasClave.stream().distinct()
          .collect(Collectors.toList());
      // Guardamos la nueva lista
      returnValue = repository.saveAll(uniquePalabrasClave);
    }

    log.debug("updatePalabrasClave(Long convocatoriaId, List<ConvocatoriaPalabraClave> palabrasClave) - end");
    return returnValue;
  }
}
