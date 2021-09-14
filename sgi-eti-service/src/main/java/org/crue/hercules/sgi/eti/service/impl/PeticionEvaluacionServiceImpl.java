package org.crue.hercules.sgi.eti.service.impl;

import java.time.Instant;
import java.time.temporal.ChronoField;

import org.apache.commons.lang3.StringUtils;
import org.crue.hercules.sgi.eti.config.SgiConfigProperties;
import org.crue.hercules.sgi.eti.dto.PeticionEvaluacionWithIsEliminable;
import org.crue.hercules.sgi.eti.exceptions.PeticionEvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion.TipoValorSocial;
import org.crue.hercules.sgi.eti.repository.PeticionEvaluacionRepository;
import org.crue.hercules.sgi.eti.repository.predicate.PeticionEvaluacionPredicateResolver;
import org.crue.hercules.sgi.eti.repository.specification.PeticionEvaluacionSpecifications;
import org.crue.hercules.sgi.eti.service.PeticionEvaluacionService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link PeticionEvaluacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class PeticionEvaluacionServiceImpl implements PeticionEvaluacionService {
  private final SgiConfigProperties sgiConfigProperties;
  private final PeticionEvaluacionRepository peticionEvaluacionRepository;

  public PeticionEvaluacionServiceImpl(SgiConfigProperties sgiConfigProperties,
      PeticionEvaluacionRepository peticionEvaluacionRepository) {
    this.sgiConfigProperties = sgiConfigProperties;
    this.peticionEvaluacionRepository = peticionEvaluacionRepository;
  }

  /**
   * Guarda la entidad {@link PeticionEvaluacion}.
   *
   * @param peticionEvaluacion la entidad {@link PeticionEvaluacion} a guardar.
   * @return la entidad {@link PeticionEvaluacion} persistida.
   */
  @Transactional
  public PeticionEvaluacion create(PeticionEvaluacion peticionEvaluacion) {
    log.debug("Petición a create PeticionEvaluacion : {} - start", peticionEvaluacion);
    Assert.isNull(peticionEvaluacion.getId(),
        "PeticionEvaluacion id tiene que ser null para crear un nuevo peticionEvaluacion");
    // Inicialización de campos no especificados a sus valores por defecto
    if (peticionEvaluacion.getActivo() == null) {
      peticionEvaluacion.setActivo(Boolean.TRUE);
    }

    String anioInicio;
    if (peticionEvaluacion.getFechaInicio() != null) {
      anioInicio = String.valueOf(peticionEvaluacion.getFechaInicio()
          .atZone(sgiConfigProperties.getTimeZone().toZoneId()).get(ChronoField.YEAR));
    } else {
      // Si no existe fecha de inicio se utiliza el año de la fecha actual
      // (la petición viene de CSP)
      anioInicio = String
          .valueOf(Instant.now().atZone(sgiConfigProperties.getTimeZone().toZoneId()).get(ChronoField.YEAR));
    }
    PeticionEvaluacion peticionEvaluacionAnio = peticionEvaluacionRepository
        .findFirstByCodigoStartingWithOrderByCodigoDesc(anioInicio);

    Long numEvaluacion = 1L;
    if (peticionEvaluacionAnio != null) {
      numEvaluacion = Long.valueOf(peticionEvaluacionAnio.getCodigo().split("/")[1]);
      numEvaluacion++;
    }

    StringBuffer codigoPeticionEvaluacion = new StringBuffer();

    codigoPeticionEvaluacion.append(anioInicio).append("/").append(String.format("%03d", numEvaluacion));

    peticionEvaluacion.setCodigo(codigoPeticionEvaluacion.toString());

    if (peticionEvaluacion.getExisteFinanciacion()) {
      Assert.notNull(peticionEvaluacion.getFuenteFinanciacion(),
          "PeticionEvaluacion fuenteFinanciacion no puede ser null si existeFinanciacion");
      Assert.notNull(peticionEvaluacion.getEstadoFinanciacion(),
          "PeticionEvaluacion estadoFinanciacion no puede ser null si existeFinanciacion");
    }

    if (peticionEvaluacion.getValorSocial() != null
        && peticionEvaluacion.getValorSocial().equals(TipoValorSocial.OTRA_FINALIDAD)) {
      Assert.notNull(peticionEvaluacion.getOtroValorSocial(),
          "PeticionEvaluacion otroValorSocial no puede ser null si TipoValorSocial.OTRA_FINALIDAD");
    }

    return peticionEvaluacionRepository.save(peticionEvaluacion);
  }

  /**
   * Obtiene todas las entidades {@link PeticionEvaluacion} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link PeticionEvaluacion} paginadas y
   *         filtradas.
   */
  public Page<PeticionEvaluacion> findAll(String query, Pageable paging) {
    log.debug("findAllPeticionEvaluacion(String query,Pageable paging) - start");
    Specification<PeticionEvaluacion> specs = PeticionEvaluacionSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<PeticionEvaluacion> returnValue = peticionEvaluacionRepository.findAll(specs, paging);
    log.debug("findAllPeticionEvaluacion(String query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link PeticionEvaluacion} por id.
   *
   * @param id el id de la entidad {@link PeticionEvaluacion}.
   * @return la entidad {@link PeticionEvaluacion}.
   * @throws PeticionEvaluacionNotFoundException Si no existe ningún
   *                                             {@link PeticionEvaluacion}e con
   *                                             ese id.
   */
  public PeticionEvaluacion findById(final Long id) throws PeticionEvaluacionNotFoundException {
    log.debug("Petición a get PeticionEvaluacion : {}  - start", id);
    final PeticionEvaluacion PeticionEvaluacion = peticionEvaluacionRepository.findById(id)
        .orElseThrow(() -> new PeticionEvaluacionNotFoundException(id));
    log.debug("Petición a get PeticionEvaluacion : {}  - end", id);
    return PeticionEvaluacion;

  }

  /**
   * Elimina una entidad {@link PeticionEvaluacion} por id.
   *
   * @param id el id de la entidad {@link PeticionEvaluacion}.
   */
  @Transactional
  public void delete(Long id) throws PeticionEvaluacionNotFoundException {
    log.debug("Petición a delete PeticionEvaluacion : {}  - start", id);
    Assert.notNull(id, "El id de PeticionEvaluacion no puede ser null.");
    if (!peticionEvaluacionRepository.existsById(id)) {
      throw new PeticionEvaluacionNotFoundException(id);
    }
    peticionEvaluacionRepository.deleteById(id);
    log.debug("Petición a delete PeticionEvaluacion : {}  - end", id);
  }

  /**
   * Elimina todos los registros {@link PeticionEvaluacion}.
   */
  @Transactional
  public void deleteAll() {
    log.debug("Petición a deleteAll de PeticionEvaluacion: {} - start");
    peticionEvaluacionRepository.deleteAll();
    log.debug("Petición a deleteAll de PeticionEvaluacion: {} - end");

  }

  /**
   * Actualiza los datos del {@link PeticionEvaluacion}.
   * 
   * @param peticionEvaluacionActualizar {@link PeticionEvaluacion} con los datos
   *                                     actualizados.
   * @return El {@link PeticionEvaluacion} actualizado.
   * @throws PeticionEvaluacionNotFoundException Si no existe ningún
   *                                             {@link PeticionEvaluacion} con
   *                                             ese id.
   * @throws IllegalArgumentException            Si el {@link PeticionEvaluacion}
   *                                             no tiene id.
   */

  @Transactional
  public PeticionEvaluacion update(final PeticionEvaluacion peticionEvaluacionActualizar) {
    log.debug("update(PeticionEvaluacion peticionEvaluacionActualizar) - start");

    Assert.notNull(peticionEvaluacionActualizar.getId(),
        "PeticionEvaluacion id no puede ser null para actualizar una petición de evaluación");

    if (peticionEvaluacionActualizar.getExisteFinanciacion()) {
      Assert.notNull(peticionEvaluacionActualizar.getFuenteFinanciacion(),
          "PeticionEvaluacion fuenteFinanciacion no puede ser null si existeFinanciacion");
      Assert.notNull(peticionEvaluacionActualizar.getEstadoFinanciacion(),
          "PeticionEvaluacion estadoFinanciacion no puede ser null si existeFinanciacion");
      Assert.notNull(peticionEvaluacionActualizar.getImporteFinanciacion(),
          "PeticionEvaluacion importeFinanciacion no puede ser null si existeFinanciacion");
    }

    if (peticionEvaluacionActualizar.getValorSocial() != null
        && peticionEvaluacionActualizar.getValorSocial().equals(TipoValorSocial.OTRA_FINALIDAD)) {
      Assert.notNull(peticionEvaluacionActualizar.getOtroValorSocial(),
          "PeticionEvaluacion otroValorSocial no puede ser null si TipoValorSocial.OTRA_FINALIDAD");
    }

    return peticionEvaluacionRepository.findById(peticionEvaluacionActualizar.getId()).map(peticionEvaluacion -> {
      peticionEvaluacion.setCodigo(peticionEvaluacionActualizar.getCodigo());
      peticionEvaluacion.setDisMetodologico(peticionEvaluacionActualizar.getDisMetodologico());
      peticionEvaluacion.setExterno(peticionEvaluacionActualizar.getExterno());
      peticionEvaluacion.setFechaFin(peticionEvaluacionActualizar.getFechaFin());
      peticionEvaluacion.setFechaInicio(peticionEvaluacionActualizar.getFechaInicio());
      peticionEvaluacion.setFuenteFinanciacion(peticionEvaluacionActualizar.getFuenteFinanciacion());
      peticionEvaluacion.setObjetivos(peticionEvaluacionActualizar.getObjetivos());
      peticionEvaluacion.setResumen(peticionEvaluacionActualizar.getResumen());
      peticionEvaluacion.setSolicitudConvocatoriaRef(peticionEvaluacionActualizar.getSolicitudConvocatoriaRef());
      peticionEvaluacion.setTieneFondosPropios(peticionEvaluacionActualizar.getTieneFondosPropios());
      peticionEvaluacion.setTipoActividad(peticionEvaluacionActualizar.getTipoActividad());
      peticionEvaluacion.setTipoInvestigacionTutelada(peticionEvaluacionActualizar.getTipoInvestigacionTutelada());
      peticionEvaluacion.setTitulo(peticionEvaluacionActualizar.getTitulo());
      peticionEvaluacion.setPersonaRef(peticionEvaluacionActualizar.getPersonaRef());
      peticionEvaluacion.setValorSocial(peticionEvaluacionActualizar.getValorSocial());
      peticionEvaluacion.setOtroValorSocial(peticionEvaluacionActualizar.getOtroValorSocial());
      peticionEvaluacion.setActivo(peticionEvaluacionActualizar.getActivo());
      peticionEvaluacion.setExisteFinanciacion(peticionEvaluacionActualizar.getExisteFinanciacion());
      peticionEvaluacion.setEstadoFinanciacion(peticionEvaluacionActualizar.getEstadoFinanciacion());
      peticionEvaluacion.setImporteFinanciacion(peticionEvaluacionActualizar.getImporteFinanciacion());

      PeticionEvaluacion returnValue = peticionEvaluacionRepository.save(peticionEvaluacion);
      log.debug("update(PeticionEvaluacion peticionEvaluacionActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new PeticionEvaluacionNotFoundException(peticionEvaluacionActualizar.getId()));
  }

  /**
   * Obtiene todas las entidades {@link PeticionEvaluacion} paginadas y filtadas.
   *
   * @param paging     la información de paginación.
   * @param query      información del filtro.
   * @param personaRef Referencia de la persona
   * @return el listado de entidades {@link PeticionEvaluacion} paginadas y
   *         filtradas.
   */
  public Page<PeticionEvaluacion> findAllByPersonaRef(String query, Pageable paging, String personaRef) {
    log.debug("findAllPeticionEvaluacion(String query,Pageable paging) - start");
    Specification<PeticionEvaluacion> specs = PeticionEvaluacionSpecifications.activos()
        .and(PeticionEvaluacionSpecifications.byPersonaRef(personaRef)).and(SgiRSQLJPASupport.toSpecification(query));

    Page<PeticionEvaluacion> returnValue = peticionEvaluacionRepository.findAll(specs, paging);
    log.debug("findAllPeticionEvaluacion(String query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una lista paginada y filtrada {@link PeticionEvaluacion} de una
   * persona responsable de memorias o creador de peticiones de evaluacion
   * 
   * @param query      Criterios de búsqueda
   * @param pageable   datos paginación
   * @param personaRef usuario
   * @return las entidades {@link PeticionEvaluacion}
   */
  @Override
  public Page<PeticionEvaluacionWithIsEliminable> findAllPeticionesWithPersonaRefCreadorPeticionesEvaluacionOrResponsableMemoria(
      String query, Pageable pageable, String personaRef) {
    log.debug("findAllPeticionEvaluacionMemoria(String query, Pageable pageable, String personaRef) - start");
    // TODO: Eliminar esta comprobación cuando se controlen los Predicate == null
    // dentro del custom repository
    Specification<Memoria> specsMem = null;
    Specification<PeticionEvaluacion> specsPet = null;
    if (StringUtils.isNotBlank(query)) {
      specsPet = SgiRSQLJPASupport.toSpecification(query, PeticionEvaluacionPredicateResolver.getInstance());
      specsMem = SgiRSQLJPASupport.toSpecification(query);
    }

    Page<PeticionEvaluacionWithIsEliminable> returnValue = peticionEvaluacionRepository
        .findAllPeticionEvaluacionMemoria(specsMem, specsPet, pageable, personaRef);
    log.debug("findAllPeticionEvaluacionMemoria(String query, Pageable pageable,  String personaRef) - end");
    return returnValue;
  }

}
