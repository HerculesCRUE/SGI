package org.crue.hercules.sgi.csp.service;

import java.util.Optional;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.exceptions.SolicitudRrhhNotFoundException;
import org.crue.hercules.sgi.csp.model.BaseEntity;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudRrhh;
import org.crue.hercules.sgi.csp.repository.SolicitudRrhhRepository;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.csp.util.SolicitudAuthorityHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service para la gestión de {@link SolicitudRrhh}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
public class SolicitudRrhhService {

  private final SolicitudRrhhRepository repository;
  private final SolicitudAuthorityHelper authorityHelper;

  /**
   * Guarda la entidad {@link SolicitudRrhh}.
   * 
   * @param solicitudRrhh la entidad {@link SolicitudRrhh} a guardar.
   * @return la entidad {@link SolicitudRrhh} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public SolicitudRrhh create(@Valid SolicitudRrhh solicitudRrhh) {
    log.debug("create(SolicitudRrhh solicitudRrhh) - start");

    AssertHelper.idNotNull(solicitudRrhh.getId(), SolicitudRrhh.class);
    authorityHelper.checkUserHasAuthorityModifySolicitud(solicitudRrhh.getId());

    SolicitudRrhh returnValue = repository.save(solicitudRrhh);

    log.debug("create(SolicitudRrhh solicitudRrhh) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos de la {@link SolicitudRrhh}.
   *
   * @param solicitudRrhhActualizar {@link SolicitudRrhh} con los datos
   *                                actualizados.
   * @return {@link SolicitudRrhh} actualizado.
   */
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public SolicitudRrhh update(@Valid SolicitudRrhh solicitudRrhhActualizar) {
    log.debug("update(SolicitudRrhh solicitudRrhhActualizar) - start");

    AssertHelper.idNotNull(solicitudRrhhActualizar.getId(), SolicitudRrhh.class);
    authorityHelper.checkUserHasAuthorityModifySolicitud(solicitudRrhhActualizar.getId());

    return repository.findById(solicitudRrhhActualizar.getId()).map(data -> {
      data.setAreaAnepRef(solicitudRrhhActualizar.getAreaAnepRef());
      data.setUniversidad(solicitudRrhhActualizar.getUniversidad());
      data.setUniversidadRef(solicitudRrhhActualizar.getUniversidadRef());

      SolicitudRrhh returnValue = repository.save(data);

      log.debug("update(SolicitudRrhh solicitudRrhhActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new SolicitudRrhhNotFoundException(solicitudRrhhActualizar.getId()));
  }

  /**
   * Actualiza el tutor de la {@link SolicitudRrhh}.
   *
   * @param solicitudRrhhActualizar {@link SolicitudRrhh} con los datos del tutor
   *                                actualizados.
   * @return {@link SolicitudRrhh} actualizado.
   */
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public SolicitudRrhh updateTutor(@Valid SolicitudRrhh solicitudRrhhActualizar) {
    log.debug("updateTutor(SolicitudRrhh solicitudRrhhActualizar) - start");

    AssertHelper.idNotNull(solicitudRrhhActualizar.getId(), SolicitudRrhh.class);
    authorityHelper.checkUserHasAuthorityModifySolicitud(solicitudRrhhActualizar.getId());

    return repository.findById(solicitudRrhhActualizar.getId()).map(data -> {
      data.setTutorRef(solicitudRrhhActualizar.getTutorRef());

      SolicitudRrhh returnValue = repository.save(data);

      log.debug("updateTutor(SolicitudRrhh solicitudRrhhActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new SolicitudRrhhNotFoundException(solicitudRrhhActualizar.getId()));
  }

  /**
   * Actualiza la memoria de la {@link SolicitudRrhh}.
   *
   * @param solicitudRrhhActualizar {@link SolicitudRrhh} con los datos de la
   *                                memoria actualizados.
   * @return {@link SolicitudRrhh} actualizado.
   */
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public SolicitudRrhh updateMemoria(@Valid SolicitudRrhh solicitudRrhhActualizar) {
    log.debug("updateMemoria(SolicitudRrhh solicitudRrhhActualizar) - start");

    AssertHelper.idNotNull(solicitudRrhhActualizar.getId(), SolicitudRrhh.class);
    authorityHelper.checkUserHasAuthorityModifySolicitud(solicitudRrhhActualizar.getId());

    return repository.findById(solicitudRrhhActualizar.getId()).map(data -> {
      data.setTituloTrabajo(solicitudRrhhActualizar.getTituloTrabajo());
      data.setResumen(solicitudRrhhActualizar.getResumen());
      data.setObservaciones(solicitudRrhhActualizar.getObservaciones());

      SolicitudRrhh returnValue = repository.save(data);

      log.debug("updateMemoria(SolicitudRrhh solicitudRrhhActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new SolicitudRrhhNotFoundException(solicitudRrhhActualizar.getId()));
  }

  /**
   * Obtiene una entidad {@link SolicitudRrhh} por id.
   * 
   * @param id Identificador de la entidad {@link SolicitudRrhh}.
   * @return la entidad {@link SolicitudRrhh}.
   */
  public SolicitudRrhh findById(Long id) {
    log.debug("findById(Long id) - start");

    AssertHelper.idNotNull(id, SolicitudRrhh.class);
    authorityHelper.checkUserHasAuthorityViewSolicitud(id);

    final SolicitudRrhh returnValue = repository.findById(id).orElseThrow(() -> new SolicitudRrhhNotFoundException(id));

    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene la {@link SolicitudRrhh} para una {@link Solicitud}.
   *
   * @param solicitudId el id de la {@link Solicitud}.
   * @return la {@link SolicitudRrhh} para una {@link Solicitud}.
   */
  public SolicitudRrhh findBySolicitud(Long solicitudId) {
    log.debug("findBySolicitud(Long solicitudId) - start");
    AssertHelper.idNotNull(solicitudId, SolicitudRrhh.class);
    authorityHelper.checkUserHasAuthorityViewSolicitud(solicitudId);
    Optional<SolicitudRrhh> returnValue = repository.findById(solicitudId);
    log.debug("findBySolicitud(Long solicitudId) - end");
    return (returnValue.isPresent()) ? returnValue.get() : null;
  }

}
