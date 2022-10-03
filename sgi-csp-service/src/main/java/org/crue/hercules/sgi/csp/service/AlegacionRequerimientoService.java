package org.crue.hercules.sgi.csp.service;

import java.util.Optional;

import org.crue.hercules.sgi.csp.exceptions.AlegacionRequerimientoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.RequerimientoJustificacionNotFoundException;
import org.crue.hercules.sgi.csp.model.AlegacionRequerimiento;
import org.crue.hercules.sgi.csp.model.RequerimientoJustificacion;
import org.crue.hercules.sgi.csp.repository.AlegacionRequerimientoRepository;
import org.crue.hercules.sgi.csp.repository.RequerimientoJustificacionRepository;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion
 * {@link AlegacionRequerimiento}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AlegacionRequerimientoService {

  private final AlegacionRequerimientoRepository repository;
  private final RequerimientoJustificacionRepository requerimientoJustificacionRepository;

  /**
   * Obtener la entidad {@link AlegacionRequerimiento}
   * perteneciente al {@link RequerimientoJustificacion}.
   *
   * @param requerimientoJustificacionId el identificador de un
   *                                     {@link RequerimientoJustificacion}
   * @return la entidad {@link AlegacionRequerimiento}
   */
  public AlegacionRequerimiento findByRequerimientoJustificacionId(
      Long requerimientoJustificacionId) {
    log.debug("findByRequerimientoJustificacionId(Long requerimientoJustificacionId) - start");
    AssertHelper.idNotNull(requerimientoJustificacionId, RequerimientoJustificacion.class);

    if (requerimientoJustificacionRepository.existsById(requerimientoJustificacionId)) {
      final Optional<AlegacionRequerimiento> returnValue = repository
          .findByRequerimientoJustificacionId(requerimientoJustificacionId);
      log.debug("findByRequerimientoJustificacionId(Long requerimientoJustificacionId) - end");
      return (returnValue.isPresent()) ? returnValue.get() : null;
    } else {
      throw new RequerimientoJustificacionNotFoundException(requerimientoJustificacionId);
    }
  }

  /**
   * Elimina las entidades {@link AlegacionRequerimiento} con el
   * requerimientoJustificacionId indicado.
   *
   * @param requerimientoJustificacionId identificador de la entidad
   *                                     {@link RequerimientoJustificacion}.
   */
  @Transactional
  public void deleteByRequerimientoJustificacionId(Long requerimientoJustificacionId) {
    log.debug("deleteByRequerimientoJustificacionId(Long requerimientoJustificacionId) - start");
    AssertHelper.idNotNull(requerimientoJustificacionId, RequerimientoJustificacion.class);

    repository.deleteInBulkByRequerimientoJustificacionId(requerimientoJustificacionId);
    log.debug("deleteByRequerimientoJustificacionId(Long requerimientoJustificacionId) - end");
  }

  /**
   * Guardar un nuevo {@link AlegacionRequerimiento}.
   *
   * @param alegacionRequerimiento la entidad
   *                               {@link AlegacionRequerimiento}
   *                               a
   *                               guardar.
   * @return la entidad {@link AlegacionRequerimiento} persistida.
   */
  @Transactional
  public AlegacionRequerimiento create(
      AlegacionRequerimiento alegacionRequerimiento) {
    log.debug("create(AlegacionRequerimiento alegacionRequerimiento) - start");
    AssertHelper.idIsNull(alegacionRequerimiento.getId(), AlegacionRequerimiento.class);

    AlegacionRequerimiento returnValue = repository.save(alegacionRequerimiento);

    log.debug("create(AlegacionRequerimiento alegacionRequerimiento) - end");
    return returnValue;
  }

  /**
   * Actualizar los campos de una entidad
   * {@link AlegacionRequerimiento}.
   *
   * @param alegacionRequerimiento la entidad
   *                               {@link AlegacionRequerimiento}
   *                               a actualizar.
   * @return la entidad {@link AlegacionRequerimiento} persistida.
   */
  @Transactional
  public AlegacionRequerimiento update(
      AlegacionRequerimiento alegacionRequerimiento) {
    log.debug("update(AlegacionRequerimiento alegacionRequerimiento) - start");

    AssertHelper.idNotNull(alegacionRequerimiento.getId(), AlegacionRequerimiento.class);

    return repository.findById(alegacionRequerimiento.getId())
        .map(alegacionRequerimientoExistente -> {

          // Establecemos los campos actualizables con los recibidos
          alegacionRequerimientoExistente.setFechaAlegacion(alegacionRequerimiento.getFechaAlegacion());
          alegacionRequerimientoExistente.setFechaReintegro(alegacionRequerimiento.getFechaReintegro());
          alegacionRequerimientoExistente.setImporteAlegado(alegacionRequerimiento.getImporteAlegado());
          alegacionRequerimientoExistente.setImporteAlegadoCd(alegacionRequerimiento.getImporteAlegadoCd());
          alegacionRequerimientoExistente.setImporteAlegadoCi(alegacionRequerimiento.getImporteAlegadoCi());
          alegacionRequerimientoExistente.setImporteReintegrado(alegacionRequerimiento.getImporteReintegrado());
          alegacionRequerimientoExistente.setImporteReintegradoCd(alegacionRequerimiento.getImporteReintegradoCd());
          alegacionRequerimientoExistente.setImporteReintegradoCi(alegacionRequerimiento.getImporteReintegradoCi());
          alegacionRequerimientoExistente.setInteresesReintegrados(alegacionRequerimiento.getInteresesReintegrados());
          alegacionRequerimientoExistente.setJustificanteReintegro(alegacionRequerimiento.getJustificanteReintegro());
          alegacionRequerimientoExistente.setObservaciones(alegacionRequerimiento.getObservaciones());

          // Actualizamos la entidad
          AlegacionRequerimiento returnValue = repository
              .save(alegacionRequerimientoExistente);

          log.debug(
              "update(AlegacionRequerimiento alegacionRequerimiento) - end");
          return returnValue;
        }).orElseThrow(() -> new AlegacionRequerimientoNotFoundException(
            alegacionRequerimiento.getId()));
  }
}
