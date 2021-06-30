package org.crue.hercules.sgi.csp.service.impl;

import java.util.Optional;

import org.crue.hercules.sgi.csp.exceptions.SolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoNotFoundException;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoService;
import org.crue.hercules.sgi.csp.service.SolicitudService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link SolicitudProyecto}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class SolicitudProyectoServiceImpl implements SolicitudProyectoService {

  private final SolicitudProyectoRepository repository;

  private final SolicitudRepository solicitudRepository;

  private final SolicitudService solicitudService;

  public SolicitudProyectoServiceImpl(SolicitudProyectoRepository repository, SolicitudRepository solicitudRepository,
      SolicitudService solicitudService) {
    this.repository = repository;
    this.solicitudRepository = solicitudRepository;
    this.solicitudService = solicitudService;
  }

  /**
   * Guarda la entidad {@link SolicitudProyecto}.
   * 
   * @param solicitudProyecto la entidad {@link SolicitudProyecto} a guardar.
   * @return SolicitudProyecto la entidad {@link SolicitudProyecto} persistida.
   */
  @Override
  @Transactional
  public SolicitudProyecto create(SolicitudProyecto solicitudProyecto) {
    log.debug("create(SolicitudProyecto solicitudProyecto) - start");

    validateSolicitudProyecto(solicitudProyecto);

    SolicitudProyecto returnValue = repository.save(solicitudProyecto);

    log.debug("create(SolicitudProyecto solicitudProyecto) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link SolicitudProyecto}.
   * 
   * @param solicitudProyecto rolSocioActualizar {@link SolicitudProyecto} con los
   *                          datos actualizados.
   * @return {@link SolicitudProyecto} actualizado.
   */
  @Override
  @Transactional
  public SolicitudProyecto update(SolicitudProyecto solicitudProyecto) {
    log.debug("update(SolicitudProyecto solicitudProyecto) - start");

    validateSolicitudProyecto(solicitudProyecto);

    // TODO validación de SolicitudProyectoPresupuesto

    // comprobar si la solicitud es modificable
    Assert.isTrue(solicitudService.modificable(solicitudProyecto.getId()), "No se puede modificar SolicitudProyecto");

    return repository.findById(solicitudProyecto.getId()).map((solicitudProyectoExistente) -> {

      solicitudProyectoExistente.setTitulo(solicitudProyecto.getTitulo());
      solicitudProyectoExistente.setAcronimo(solicitudProyecto.getAcronimo());
      solicitudProyectoExistente.setCodExterno(solicitudProyecto.getCodExterno());
      solicitudProyectoExistente.setDuracion(solicitudProyecto.getDuracion());
      solicitudProyectoExistente.setColaborativo(solicitudProyecto.getColaborativo());
      solicitudProyectoExistente.setCoordinadorExterno(solicitudProyecto.getCoordinadorExterno());
      solicitudProyectoExistente.setObjetivos(solicitudProyecto.getObjetivos());
      solicitudProyectoExistente.setIntereses(solicitudProyecto.getIntereses());
      solicitudProyectoExistente.setResultadosPrevistos(solicitudProyecto.getResultadosPrevistos());
      solicitudProyectoExistente.setAreaTematica(solicitudProyecto.getAreaTematica());
      solicitudProyectoExistente.setChecklistRef(solicitudProyecto.getChecklistRef());
      solicitudProyectoExistente.setPeticionEvaluacionRef(solicitudProyecto.getPeticionEvaluacionRef());
      solicitudProyectoExistente.setTipoPresupuesto(solicitudProyecto.getTipoPresupuesto());
      solicitudProyectoExistente.setImporteSolicitado(solicitudProyecto.getImporteSolicitado());
      solicitudProyectoExistente.setImportePresupuestado(solicitudProyecto.getImportePresupuestado());
      solicitudProyectoExistente.setImporteSolicitadoSocios(solicitudProyecto.getImporteSolicitadoSocios());
      solicitudProyectoExistente.setImportePresupuestadoSocios(solicitudProyecto.getImportePresupuestadoSocios());
      solicitudProyectoExistente.setTotalImporteSolicitado(solicitudProyecto.getTotalImporteSolicitado());
      solicitudProyectoExistente.setTotalImportePresupuestado(solicitudProyecto.getTotalImportePresupuestado());
      SolicitudProyecto returnValue = repository.save(solicitudProyectoExistente);

      log.debug("update(SolicitudProyecto solicitudProyecto) - end");
      return returnValue;
    }).orElseThrow(() -> new SolicitudProyectoNotFoundException(solicitudProyecto.getId()));
  }

  /**
   * Obtiene una entidad {@link SolicitudProyecto} por id.
   * 
   * @param id Identificador de la entidad {@link SolicitudProyecto}.
   * @return SolicitudProyecto la entidad {@link SolicitudProyecto}.
   */
  @Override
  public SolicitudProyecto findById(Long id) {
    log.debug("findById(Long id) - start");
    final SolicitudProyecto returnValue = repository.findById(id)
        .orElseThrow(() -> new SolicitudProyectoNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina la {@link SolicitudProyecto}.
   *
   * @param id Id del {@link SolicitudProyecto}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id, "SolicitudProyecto id no puede ser null para eliminar un SolicitudProyecto");
    if (!repository.existsById(id)) {
      throw new SolicitudProyectoNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");

  }

  /**
   * Obtiene la {@link SolicitudProyecto} para una {@link Solicitud}.
   *
   * @param solicitudId el id de la {@link Solicitud}.
   * @return la lista de entidades {@link SolicitudProyecto} de la
   *         {@link Solicitud} paginadas.
   */
  @Override
  public SolicitudProyecto findBySolicitud(Long solicitudId) {
    log.debug("findBySolicitud(Long solicitudId) - start");

    if (solicitudRepository.existsById(solicitudId)) {
      final Optional<SolicitudProyecto> returnValue = repository.findById(solicitudId);
      log.debug("findBySolicitud(Long solicitudId) - end");
      return (returnValue.isPresent()) ? returnValue.get() : null;
    } else {
      throw new SolicitudNotFoundException(solicitudId);
    }

  }

  /**
   * Comprueba si existe una solicitud de proyecto
   * 
   * @param id Identificador de la {@link Solicitud}
   * @return Indicador de si existe o no solicitud de proyecto.
   */
  @Override
  public boolean existsBySolicitudId(Long id) {

    return repository.existsById(id);
  }

  /**
   * Realiza las validaciones comunes para las operaciones de creación y
   * acutalización de solicitud de proyecto.
   * 
   * @param solicitudProyecto {@link SolicitudNotFoundException}
   */
  private void validateSolicitudProyecto(SolicitudProyecto solicitudProyecto) {
    log.debug("validateSolicitudProyecto(SolicitudProyecto solicitudProyecto) - start");

    Assert.notNull(solicitudProyecto.getId(),
        "El id no puede ser null para realizar la acción sobre SolicitudProyecto");

    Assert.notNull(solicitudProyecto.getTitulo(),
        "El título no puede ser null para realizar la acción sobre SolicitudProyecto");

    Assert.notNull(solicitudProyecto.getColaborativo(),
        "Colaborativo no puede ser null para realizar la acción sobre SolicitudProyecto");

    Assert.notNull(solicitudProyecto.getTipoPresupuesto(),
        "Tipo presupuesto no puede ser null para realizar la acción sobre SolicitudProyecto");

    if (!solicitudRepository.existsById(solicitudProyecto.getId())) {
      throw new SolicitudNotFoundException(solicitudProyecto.getId());
    }

    log.debug("validateSolicitudProyecto(SolicitudProyecto solicitudProyecto) - end");
  }

}
