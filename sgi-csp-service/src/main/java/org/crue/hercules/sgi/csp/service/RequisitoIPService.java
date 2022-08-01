package org.crue.hercules.sgi.csp.service;

import java.util.Optional;

import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.RequisitoIPNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.RequisitoIP;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.RequisitoIPRepository;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service para gestion {@link RequisitoIP}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class RequisitoIPService {

  private final RequisitoIPRepository repository;
  private final ConvocatoriaRepository convocatoriaRepository;

  public RequisitoIPService(RequisitoIPRepository repository, ConvocatoriaRepository convocatoriaRepository) {
    this.repository = repository;
    this.convocatoriaRepository = convocatoriaRepository;
  }

  /**
   * Guarda la entidad {@link RequisitoIP}.
   * 
   * @param requisitoIP la entidad {@link RequisitoIP} a guardar.
   * @return RequisitoIP la entidad {@link RequisitoIP} persistida.
   */
  @Transactional
  public RequisitoIP create(RequisitoIP requisitoIP) {
    log.debug("create(RequisitoIP requisitoIP) - start");

    AssertHelper.idNotNull(requisitoIP.getId(), RequisitoIP.class);

    Assert.isTrue(!repository.existsById(requisitoIP.getId()),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key("org.crue.hercules.sgi.csp.exceptions.RelatedEntityAlreadyExists.message")
            .parameter(AssertHelper.PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(RequisitoIP.class))
            .parameter("related", ApplicationContextSupport.getMessage(Convocatoria.class)).build());

    if (!convocatoriaRepository.existsById(requisitoIP.getId())) {
      throw new ConvocatoriaNotFoundException(requisitoIP.getId());
    }

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
  @Transactional
  public RequisitoIP update(RequisitoIP requisitoIPActualizar, Long idConvocatoria) {
    log.debug("update(RequisitoIP requisitoIPActualizar) - start");

    AssertHelper.idNotNull(idConvocatoria, RequisitoIP.class);

    return repository.findById(idConvocatoria).map(requisitoIP -> {
      requisitoIP.setFechaMinimaNivelAcademico(requisitoIPActualizar.getFechaMinimaNivelAcademico());
      requisitoIP.setFechaMaximaNivelAcademico(requisitoIPActualizar.getFechaMaximaNivelAcademico());
      requisitoIP.setFechaMinimaCategoriaProfesional(requisitoIPActualizar.getFechaMinimaCategoriaProfesional());
      requisitoIP.setFechaMaximaCategoriaProfesional(requisitoIPActualizar.getFechaMaximaCategoriaProfesional());
      requisitoIP.setEdadMaxima(requisitoIPActualizar.getEdadMaxima());
      requisitoIP.setNumMaximoCompetitivosActivos(requisitoIPActualizar.getNumMaximoCompetitivosActivos());
      requisitoIP.setNumMaximoIP(requisitoIPActualizar.getNumMaximoIP());
      requisitoIP.setNumMaximoNoCompetitivosActivos(requisitoIPActualizar.getNumMaximoNoCompetitivosActivos());
      requisitoIP.setNumMinimoCompetitivos(requisitoIPActualizar.getNumMinimoCompetitivos());
      requisitoIP.setNumMinimoNoCompetitivos(requisitoIPActualizar.getNumMinimoNoCompetitivos());
      requisitoIP.setOtrosRequisitos(requisitoIPActualizar.getOtrosRequisitos());
      requisitoIP.setSexoRef(requisitoIPActualizar.getSexoRef());
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
  public RequisitoIP findByConvocatoria(Long id) {
    log.debug("findByConvocatoria(Long id) - start");

    if (convocatoriaRepository.existsById(id)) {
      final Optional<RequisitoIP> returnValue = repository.findById(id);
      log.debug("findByConvocatoriaId(Long id) - end");
      return (returnValue.isPresent()) ? returnValue.get() : null;
    } else {
      throw new ConvocatoriaNotFoundException(id);
    }
  }

}
