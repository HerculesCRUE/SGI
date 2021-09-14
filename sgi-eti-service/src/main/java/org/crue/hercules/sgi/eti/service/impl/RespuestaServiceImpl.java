package org.crue.hercules.sgi.eti.service.impl;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.crue.hercules.sgi.eti.dto.RespuestaRetrospectivaFormulario;
import org.crue.hercules.sgi.eti.exceptions.RespuestaNotFoundException;
import org.crue.hercules.sgi.eti.model.Apartado;
import org.crue.hercules.sgi.eti.model.Bloque;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.Respuesta;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.repository.ApartadoRepository;
import org.crue.hercules.sgi.eti.repository.BloqueRepository;
import org.crue.hercules.sgi.eti.repository.RespuestaRepository;
import org.crue.hercules.sgi.eti.service.MemoriaService;
import org.crue.hercules.sgi.eti.service.RespuestaService;
import org.crue.hercules.sgi.eti.service.RetrospectivaService;
import org.crue.hercules.sgi.eti.util.Constantes;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link Respuesta}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class RespuestaServiceImpl implements RespuestaService {
  private static final String SI = "si";
  private static final String ID_FORMULARIO_SEG_ANUAL = "4";
  private static final String ID_FORMULARIO_SEG_FINAL = "5";
  private static final String ID_FORMULARIO_RETROSPECTIVA = "6";

  private final RespuestaRepository respuestaRepository;
  private final BloqueRepository bloqueRepository;
  private final MemoriaService memoriaService;
  private final RetrospectivaService retrospectivaService;
  private final ApartadoRepository apartadoRepository;

  public RespuestaServiceImpl(RespuestaRepository respuestaRepository, BloqueRepository bloqueRepository,
      MemoriaService memoriaService, RetrospectivaService retrospectivaService, ApartadoRepository apartadoRepository) {
    this.respuestaRepository = respuestaRepository;
    this.bloqueRepository = bloqueRepository;
    this.memoriaService = memoriaService;
    this.retrospectivaService = retrospectivaService;
    this.apartadoRepository = apartadoRepository;
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
    Assert.isNull(respuesta.getId(), "Respuesta id tiene que ser null para crear un nuevo Respuesta");

    Respuesta respuestaNew = respuestaRepository.save(respuesta);

    if (this.isFormularioCompletado(respuestaNew)) {
      this.setEstadoMemoriaCompletada(respuestaNew);
    }

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
    Assert.notNull(id, "El id de Respuesta no puede ser null.");
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

    Assert.notNull(respuestaActualizar.getId(), "Respuesta id no puede ser null para actualizar un Respuesta");

    return respuestaRepository.findById(respuestaActualizar.getId()).map(respuesta -> {
      respuesta.setMemoria(respuestaActualizar.getMemoria());
      respuesta.setApartado(respuestaActualizar.getApartado());
      respuesta.setValor(respuestaActualizar.getValor());
      respuesta.setTipoDocumento(respuestaActualizar.getTipoDocumento());

      Respuesta returnValue = respuestaRepository.save(respuesta);

      if (this.isFormularioCompletado(returnValue)) {
        this.setEstadoMemoriaCompletada(returnValue);
      }

      log.debug("update(Respuesta RespuestaActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new RespuestaNotFoundException(respuestaActualizar.getId()));
  }

  @Override
  public Respuesta findByMemoriaIdAndApartadoId(Long id, Long idApartado) {
    return respuestaRepository.findByMemoriaIdAndApartadoId(id, idApartado);
  }

  /**
   * Comprueba si se han rellenado todos los bloques del formulario, si está
   * 
   * completo
   * 
   * @param respuesta la {@link Respuesta} del formulario
   * @return boolean true/false si está o no completo
   */
  private Boolean isFormularioCompletado(Respuesta respuesta) {
    Boolean completado = Boolean.FALSE;
    Formulario formulario = null;
    Memoria memoria = memoriaService.findById(respuesta.getMemoria().getId());

    Optional<Apartado> apartado = apartadoRepository.findById(respuesta.getApartado().getId());

    if (apartado.isPresent()) {
      formulario = apartado.get().getBloque().getFormulario();
    }

    if (formulario != null && formulario.getId() != null) {
      // Si el formulario es de tipo M20 y existen datos de retroespectiva se guardan
      // en sus correspondientes tablas.
      guardarDatosRetrospectiva(formulario, respuesta.getId(), memoria);
      Bloque lastBloque = bloqueRepository.findFirstByFormularioIdOrderByOrdenDesc(formulario.getId());
      List<Apartado> ultimosApartados = apartadoRepository.findFirst2ByBloqueIdOrderByOrdenDesc(lastBloque.getId());
      Apartado lastApartado = CollectionUtils.isEmpty(ultimosApartados) ? null
          : ultimosApartados.get(ultimosApartados.size() - 1);
      Respuesta respuestaUltimoBloqueApartado = null;
      if (formulario.getId().toString().equals(ID_FORMULARIO_SEG_FINAL) && lastBloque != null) {
        respuestaUltimoBloqueApartado = respuestaRepository
            .findByApartadoBloqueOrdenAndApartadoPadreIsNullAndApartadoOrdenAndApartadoBloqueFormularioIdAndMemoriaId(
                lastBloque.getOrden(), 1, formulario.getId(), memoria.getId());
      } else if (lastBloque != null && lastApartado != null) {
        respuestaUltimoBloqueApartado = respuestaRepository
            .findByApartadoBloqueOrdenAndApartadoPadreIsNullAndApartadoOrdenAndApartadoBloqueFormularioIdAndMemoriaId(
                lastBloque.getOrden(), lastApartado.getOrden(), formulario.getId(), memoria.getId());
      }

      if (respuestaUltimoBloqueApartado != null && respuestaUltimoBloqueApartado.getValor() != null
          && !respuestaUltimoBloqueApartado.getValor().contains("null")) {
        completado = Boolean.TRUE;
      }
    }

    return completado;
  }

  /**
   * Actualiza el estado de la memoria a completada
   * 
   * @param respuesta la {@link Respuesta} del formulario
   */
  private void setEstadoMemoriaCompletada(Respuesta respuesta) {
    // Se pasa la memoria al estado completada
    Memoria memoria = memoriaService.findById(respuesta.getMemoria().getId());
    Optional<Apartado> apartado = apartadoRepository.findById(respuesta.getApartado().getId());

    if (apartado.isPresent()) {
      switch (apartado.get().getBloque().getFormulario().getId().toString()) {
        case ID_FORMULARIO_SEG_ANUAL:
          if (memoria.getEstadoActual().getId() < Constantes.TIPO_ESTADO_MEMORIA_COMPLETADA_SEGUIMIENTO_ANUAL) {
            memoriaService.updateEstadoMemoria(memoria, Constantes.TIPO_ESTADO_MEMORIA_COMPLETADA_SEGUIMIENTO_ANUAL);
          }
          break;
        case ID_FORMULARIO_SEG_FINAL:
          if (memoria.getEstadoActual().getId() < Constantes.TIPO_ESTADO_MEMORIA_COMPLETADA_SEGUIMIENTO_FINAL) {
            memoriaService.updateEstadoMemoria(memoria, Constantes.TIPO_ESTADO_MEMORIA_COMPLETADA_SEGUIMIENTO_FINAL);
          }
          break;
        case ID_FORMULARIO_RETROSPECTIVA:
          if (memoria.getRetrospectiva() != null && memoria.getRetrospectiva().getEstadoRetrospectiva()
              .getId() < Constantes.ESTADO_RETROSPECTIVA_COMPLETADA) {
            retrospectivaService.updateEstadoRetrospectiva(memoria.getRetrospectiva(),
                Constantes.ESTADO_RETROSPECTIVA_COMPLETADA);
          }
          break;
        default:
          if (memoria.getEstadoActual().getId() < Constantes.TIPO_ESTADO_MEMORIA_COMPLETADA) {
            memoriaService.updateEstadoMemoria(memoria, Constantes.TIPO_ESTADO_MEMORIA_COMPLETADA);
          }
          break;
      }
    }
  }

  /**
   * Guarda los datos de retrospectiva del formulario M20 que se encuentran en el
   * bloque 5 apartado 3.
   * 
   * @param formulario  l {@link Formulario}
   * @param idRespuesta el identificador de la {@link Respuesta}
   * @param memoria     la {@link Memoria} de la {@link Respuesta}
   */
  private void guardarDatosRetrospectiva(Formulario formulario, Long idRespuesta, Memoria memoria) {
    if (formulario.getNombre().equals("M20")) {
      // Bloque 5 apartado 3 - Evaluación retrospectiva
      if (respuestaRepository.existsByIdAndApartadoBloqueOrden(idRespuesta, 5)) {
        Respuesta respuesta = respuestaRepository
            .findByApartadoBloqueOrdenAndApartadoPadreIsNullAndApartadoOrdenAndApartadoBloqueFormularioIdAndMemoriaId(5,
                3, formulario.getId(), memoria.getId());
        if (respuesta != null) {
          ObjectMapper mapper = new ObjectMapper();
          try {
            RespuestaRetrospectivaFormulario retrospectivaForm = mapper.readValue(respuesta.getValor(),
                RespuestaRetrospectivaFormulario.class);

            if (retrospectivaForm.getEvaluacionRetrospectivaRadio().toLowerCase().equals(SI)) {
              memoria.setRequiereRetrospectiva(true);
              Retrospectiva retrospectiva = memoria.getRetrospectiva();
              if (retrospectiva == null) {
                retrospectiva = new Retrospectiva();
              }
              EstadoRetrospectiva estadoRetrospectiva = new EstadoRetrospectiva();
              estadoRetrospectiva.setId(Constantes.ESTADO_RETROSPECTIVA_PENDIENTE);
              retrospectiva.setEstadoRetrospectiva(estadoRetrospectiva);
              retrospectiva.setFechaRetrospectiva(retrospectivaForm.getFechaEvRetrospectiva().toInstant());
              if (retrospectiva.getId() == null) {
                memoria.setRetrospectiva(retrospectivaService.create(retrospectiva));
              } else {
                memoria.setRetrospectiva(retrospectivaService.update(retrospectiva));
              }

            } else {
              if (memoria.getRetrospectiva() != null) {
                retrospectivaService.delete(memoria.getRetrospectiva().getId());
                memoria.setRetrospectiva(null);
              }
              memoria.setRequiereRetrospectiva(false);
            }

            memoriaService.update(memoria);
          } catch (Exception e) {
            log.error("No se ha podido transformar el json a objeto: " + respuesta.getValor());
          }
        }
      }
    }
  }

  @Override
  public Page<Respuesta> findByMemoriaId(Long idMemoria, Pageable page) {
    return respuestaRepository.findByMemoriaIdAndTipoDocumentoIsNotNull(idMemoria, page);
  }

}
