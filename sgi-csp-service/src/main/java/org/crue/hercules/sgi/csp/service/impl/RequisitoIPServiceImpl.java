package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.exceptions.RequisitoIPNotFoundException;

import java.util.Optional;

import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.RequisitoIP;
import org.crue.hercules.sgi.csp.repository.RequisitoIPRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.service.ConvocatoriaService;
import org.crue.hercules.sgi.csp.service.RequisitoIPService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link RequisitoIP}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class RequisitoIPServiceImpl implements RequisitoIPService {

  private final RequisitoIPRepository repository;
  private final ConvocatoriaRepository convocatoriaRepository;
  private final ConvocatoriaService convocatoriaService;

  public RequisitoIPServiceImpl(RequisitoIPRepository repository, ConvocatoriaRepository convocatoriaRepository,
      ConvocatoriaService convocatoriaService) {
    this.repository = repository;
    this.convocatoriaRepository = convocatoriaRepository;
    this.convocatoriaService = convocatoriaService;
  }

  /**
   * Guarda la entidad {@link RequisitoIP}.
   * 
   * @param requisitoIP la entidad {@link RequisitoIP} a guardar.
   * @return RequisitoIP la entidad {@link RequisitoIP} persistida.
   */
  @Override
  @Transactional
  public RequisitoIP create(RequisitoIP requisitoIP) {
    log.debug("create(RequisitoIP requisitoIP) - start");

    Assert.isNull(requisitoIP.getId(), "Id tiene que ser null para crear RequisitoIP");

    Assert.isTrue(requisitoIP.getConvocatoriaId() != null, "Convocatoria no puede ser null para crear RequisitoIP");

    Assert.isTrue(!repository.findByConvocatoriaId(requisitoIP.getConvocatoriaId()).isPresent(),
        "Ya existe RequisitoIP para la convocatoria " + requisitoIP.getConvocatoriaId());

    convocatoriaRepository.findById(requisitoIP.getConvocatoriaId())
        .orElseThrow(() -> new ConvocatoriaNotFoundException(requisitoIP.getConvocatoriaId()));

    RequisitoIP returnValue = repository.save(requisitoIP);

    log.debug("create(RequisitoIP requisitoIP) - end");
    return returnValue;
  }

  /**
   * Actualiza la entidad {@link RequisitoIP}.
   * 
   * @param requisitoIPActualizar la entidad {@link RequisitoIP} a guardar.
   * @param idConvocatoria        identificado de la {@link Convocatoria} a
   *                              guardar.
   * @return RequisitoIP la entidad {@link RequisitoIP} persistida.
   */
  @Override
  @Transactional
  public RequisitoIP update(RequisitoIP requisitoIPActualizar, Long idConvocatoria) {
    log.debug("update(RequisitoIP requisitoIPActualizar) - start");

    Assert.notNull(idConvocatoria, "Id Convocatoria no puede ser null para actualizar RequisitoIP");

    return repository.findByConvocatoriaId(idConvocatoria).map(requisitoIP -> {

      // comprobar si convocatoria es modificable
      Assert.isTrue(
          convocatoriaService.modificable(requisitoIP.getConvocatoriaId(), null, new String[] { "CSP-CON-E" }),
          "No se puede modificar RequisitoIP. No tiene los permisos necesarios o la convocatoria está registrada y cuenta con solicitudes o proyectos asociados");

      requisitoIP.setAniosNivelAcademico(requisitoIPActualizar.getAniosNivelAcademico());
      requisitoIP.setAniosVinculacion(requisitoIPActualizar.getAniosVinculacion());
      requisitoIP.setEdadMaxima(requisitoIPActualizar.getEdadMaxima());
      requisitoIP.setModalidadContratoRef(requisitoIPActualizar.getModalidadContratoRef());
      requisitoIP.setNivelAcademicoRef(requisitoIPActualizar.getNivelAcademicoRef());
      requisitoIP.setNumMaximoCompetitivosActivos(requisitoIPActualizar.getNumMaximoCompetitivosActivos());
      requisitoIP.setNumMaximoIP(requisitoIPActualizar.getNumMaximoIP());
      requisitoIP.setNumMaximoNoCompetitivosActivos(requisitoIPActualizar.getNumMaximoNoCompetitivosActivos());
      requisitoIP.setNumMinimoCompetitivos(requisitoIPActualizar.getNumMinimoCompetitivos());
      requisitoIP.setNumMinimoNoCompetitivos(requisitoIPActualizar.getNumMinimoNoCompetitivos());
      requisitoIP.setOtrosRequisitos(requisitoIPActualizar.getOtrosRequisitos());
      requisitoIP.setSexo(requisitoIPActualizar.getSexo());
      requisitoIP.setVinculacionUniversidad(requisitoIPActualizar.getVinculacionUniversidad());

      RequisitoIP returnValue = repository.save(requisitoIP);
      log.debug("update(RequisitoIP requisitoIPActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new RequisitoIPNotFoundException(requisitoIPActualizar.getId()));

  }

  /**
   * Obtiene el {@link RequisitoIP} para una {@link Convocatoria}.
   *
   * @param id el id de la {@link Convocatoria}.
   * @return la entidad {@link RequisitoIP} de la {@link Convocatoria}
   */
  @Override
  public RequisitoIP findByConvocatoria(Long id) {
    log.debug("findByConvocatoria(Long id) - start");

    if (convocatoriaRepository.existsById(id)) {
      final Optional<RequisitoIP> returnValue = repository.findByConvocatoriaId(id);
      log.debug("findByConvocatoriaId(Long id) - end");
      return (returnValue.isPresent()) ? returnValue.get() : null;
    } else {
      throw new ConvocatoriaNotFoundException(id);
    }
  }

}
