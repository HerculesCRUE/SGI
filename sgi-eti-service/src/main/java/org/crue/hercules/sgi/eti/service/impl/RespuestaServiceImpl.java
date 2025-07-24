package org.crue.hercules.sgi.eti.service.impl;

import java.util.List;
import java.util.Optional;

import org.crue.hercules.sgi.eti.dto.RespuestaRetrospectivaFormulario;
import org.crue.hercules.sgi.eti.exceptions.RespuestaNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.RespuestaRetrospectivaFormularioNotValidException;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.Respuesta;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.repository.FormularioRepository;
import org.crue.hercules.sgi.eti.repository.RespuestaRepository;
import org.crue.hercules.sgi.eti.service.MemoriaService;
import org.crue.hercules.sgi.eti.service.RespuestaService;
import org.crue.hercules.sgi.eti.service.RetrospectivaService;
import org.crue.hercules.sgi.eti.util.Constantes;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.util.AssertHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link Respuesta}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class RespuestaServiceImpl implements RespuestaService {
  private static final String SI = "si";

  private final RespuestaRepository respuestaRepository;
  private final MemoriaService memoriaService;
  private final RetrospectivaService retrospectivaService;
  private final FormularioRepository formularioRepository;

  public RespuestaServiceImpl(RespuestaRepository respuestaRepository,
      MemoriaService memoriaService, RetrospectivaService retrospectivaService,
      FormularioRepository formularioRepository) {
    this.respuestaRepository = respuestaRepository;
    this.memoriaService = memoriaService;
    this.retrospectivaService = retrospectivaService;
    this.formularioRepository = formularioRepository;
  }

  /**
   * Guarda la entidad {@link Respuesta}.
   *
   * @param respuesta la entidad {@link Respuesta} a guardar.
   * @return la entidad {@link Respuesta} persistida.
   */
  @Override
  @Transactional
  public Respuesta create(Respuesta respuesta) {
    log.debug("Petición a create Respuesta : {} - start", respuesta);
    AssertHelper.idIsNull(respuesta.getId(), Respuesta.class);

    Respuesta respuestaNew = respuestaRepository.save(respuesta);

    log.debug("create(Respuesta respuesta) - end");

    return respuestaNew;
  }

  /**
   * Obtiene todas las entidades {@link Respuesta} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link Respuesta} paginadas y filtradas.
   */
  @Override
  public Page<Respuesta> findAll(String query, Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Specification<Respuesta> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<Respuesta> returnValue = respuestaRepository.findAll(specs, paging);
    log.debug("findAll(String query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link Respuesta} por id.
   *
   * @param id el id de la entidad {@link Respuesta}.
   * @return la entidad {@link Respuesta}.
   * @throws RespuestaNotFoundException Si no existe ningún {@link Respuesta} con
   *                                    ese id.
   */
  @Override
  public Respuesta findById(final Long id) throws RespuestaNotFoundException {
    log.debug("Petición a get Respuesta : {}  - start", id);
    final Respuesta respuesta = respuestaRepository.findById(id).orElseThrow(() -> new RespuestaNotFoundException(id));
    log.debug("Petición a get Respuesta : {}  - end", id);
    return respuesta;

  }

  /**
   * Elimina una entidad {@link Respuesta} por id.
   *
   * @param id el id de la entidad {@link Respuesta}.
   */
  @Override
  @Transactional
  public void delete(Long id) throws RespuestaNotFoundException {
    log.debug("Petición a delete Respuesta : {}  - start", id);
    AssertHelper.idNotNull(id, Respuesta.class);
    if (!respuestaRepository.existsById(id)) {
      throw new RespuestaNotFoundException(id);
    }
    respuestaRepository.deleteById(id);
    log.debug("Petición a delete Respuesta : {}  - end", id);
  }

  /**
   * Elimina todos los registros {@link Respuesta}.
   */
  @Override
  @Transactional
  public void deleteAll() {
    log.debug("Petición a deleteAll de Respuesta: {} - start");
    respuestaRepository.deleteAll();
    log.debug("Petición a deleteAll de Respuesta: {} - end");

  }

  /**
   * Actualiza los datos del {@link Respuesta}.
   * 
   * @param respuestaActualizar {@link Respuesta} con los datos actualizados.
   * @return El {@link Respuesta} actualizado.
   * @throws RespuestaNotFoundException Si no existe ningún {@link Respuesta} con
   *                                    ese id.
   * @throws IllegalArgumentException   Si el {@link Respuesta} no tiene id.
   */
  @Override
  @Transactional
  public Respuesta update(final Respuesta respuestaActualizar) {
    log.debug("update(Respuesta RespuestaActualizar) - start");

    AssertHelper.idNotNull(respuestaActualizar.getId(), Respuesta.class);

    return respuestaRepository.findById(respuestaActualizar.getId()).map(respuesta -> {
      respuesta.setMemoriaId(respuestaActualizar.getMemoriaId());
      respuesta.setApartadoId(respuestaActualizar.getApartadoId());
      respuesta.setValor(respuestaActualizar.getValor());
      respuesta.setTipoDocumento(respuestaActualizar.getTipoDocumento());

      Respuesta returnValue = respuestaRepository.save(respuesta);

      log.debug("update(Respuesta RespuestaActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new RespuestaNotFoundException(respuestaActualizar.getId()));
  }

  @Override
  public Respuesta findByMemoriaIdAndApartadoId(Long id, Long idApartado) {
    return respuestaRepository.findByMemoriaIdAndApartadoId(id, idApartado);
  }

  /**
   * Actualiza los datos de la restrospectiva en la memoria con los valores de la
   * respuesta si el formulario es de tipo M20.
   * 
   * @param id identificador de la {@link Respuesta} con los datos de la
   *           retrospectiva
   * @throws RespuestaNotFoundException                        si no existe la
   *                                                           respuesta con el id
   *                                                           dado
   * @throws RespuestaRetrospectivaFormularioNotValidException si la respuesta no
   *                                                           cumple con el
   *                                                           esquema de
   *                                                           {@link RespuestaRetrospectivaFormulario}
   */
  @Transactional
  public void updateDatosRetrospectiva(Long id)
      throws RespuestaNotFoundException, RespuestaRetrospectivaFormularioNotValidException {
    Respuesta respuesta = respuestaRepository.findById(id).orElseThrow(() -> new RespuestaNotFoundException(id));
    Memoria memoria = memoriaService.findById(respuesta.getMemoriaId());

    if (!memoria.getComite().getRequiereRetrospectiva().equals(Boolean.TRUE)) {
      return;
    }

    RespuestaRetrospectivaFormulario respuestaRetrospectiva = null;

    try {
      respuestaRetrospectiva = new ObjectMapper().readValue(respuesta.getValor(),
          RespuestaRetrospectivaFormulario.class);
    } catch (JsonProcessingException e) {
      log.error("evaluateRespuestaRetrospectiva(Long id) - No se ha podido transformar el json a objeto: "
          + respuesta.getValor(), e);
      throw new RespuestaRetrospectivaFormularioNotValidException();
    }

    boolean requiereRetrospectivaUpdated = false;
    Retrospectiva retrospectivaUpdated = null;

    if (respuestaRetrospectiva.getEvaluacionRetrospectivaRadio().equalsIgnoreCase(SI)) {
      requiereRetrospectivaUpdated = true;
      Retrospectiva retrospectiva = memoria.getRetrospectiva();
      if (retrospectiva == null) {
        retrospectiva = new Retrospectiva();
      }

      EstadoRetrospectiva estadoRetrospectiva = new EstadoRetrospectiva();
      estadoRetrospectiva.setId(Constantes.ESTADO_RETROSPECTIVA_PENDIENTE);
      retrospectiva.setEstadoRetrospectiva(estadoRetrospectiva);
      retrospectiva.setFechaRetrospectiva(respuestaRetrospectiva.getFechaEvRetrospectiva() != null
          ? respuestaRetrospectiva.getFechaEvRetrospectiva().toInstant()
          : null);

      if (retrospectiva.getId() == null) {
        retrospectivaUpdated = retrospectivaService.create(retrospectiva);
      } else {
        retrospectivaUpdated = retrospectivaService.update(retrospectiva);
      }

    } else {
      if (memoria.getRetrospectiva() != null) {
        retrospectivaService.delete(memoria.getRetrospectiva().getId());
        retrospectivaUpdated = null;
      }
      requiereRetrospectivaUpdated = false;
    }

    memoriaService.updateDatosRetrospectiva(respuesta.getMemoriaId(), requiereRetrospectivaUpdated,
        retrospectivaUpdated);

  }

  @Override
  public Page<Respuesta> findByMemoriaId(Long idMemoria, Pageable page) {
    return respuestaRepository.findByMemoriaIdAndTipoDocumentoIsNotNull(idMemoria, page);
  }

  @Override
  public Optional<Respuesta> findLastByMemoriaId(Long idMemoria) {
    return respuestaRepository.findTopByMemoriaIdOrderByApartadoBloqueOrdenDescApartadoOrdenDesc(idMemoria);
  }

  /**
   * Lista de {@link Respuesta} de la memoria
   * 
   * @param id Identificador de la {@link Memoria}
   * @return la lista de {@link Respuesta} de la memoria
   */
  @Override
  public List<Respuesta> findByMemoriaId(Long id) {
    log.debug("findByMemoriaId({}) - start");
    List<Respuesta> respuestas = respuestaRepository.findByMemoriaId(id);
    log.debug("findByMemoriaId({}) - end");
    return respuestas;
  }

}
