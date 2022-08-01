package org.crue.hercules.sgi.csp.service;

import java.util.Optional;

import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.RequisitoEquipoNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.RequisitoEquipo;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.RequisitoEquipoRepository;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service para gestion {@link RequisitoEquipo}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class RequisitoEquipoService {

  private final RequisitoEquipoRepository repository;
  private final ConvocatoriaRepository convocatoriaRepository;

  public RequisitoEquipoService(RequisitoEquipoRepository repository, ConvocatoriaRepository convocatoriaRepository) {
    this.repository = repository;
    this.convocatoriaRepository = convocatoriaRepository;
  }

  /**
   * Guarda la entidad {@link RequisitoEquipo}.
   * 
   * @param requisitoEquipo la entidad {@link RequisitoEquipo} a guardar.
   * @return RequisitoEquipo la entidad {@link RequisitoEquipo} persistida.
   */
  @Transactional
  public RequisitoEquipo create(RequisitoEquipo requisitoEquipo) {
    log.debug("create(RequisitoEquipo requisitoEquipo) - start");

    AssertHelper.idNotNull(requisitoEquipo.getId(), RequisitoEquipo.class);

    Assert.isTrue(!repository.existsById(requisitoEquipo.getId()),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key("org.crue.hercules.sgi.csp.exceptions.RelatedEntityAlreadyExists.message")
            .parameter(AssertHelper.PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(RequisitoEquipo.class))
            .parameter("related", ApplicationContextSupport.getMessage(Convocatoria.class)).build());

    if (!convocatoriaRepository.existsById(requisitoEquipo.getId())) {
      throw new ConvocatoriaNotFoundException(requisitoEquipo.getId());
    }

    RequisitoEquipo returnValue = repository.save(requisitoEquipo);

    log.debug("create(RequisitoEquipo requisitoEquipo) - end");
    return returnValue;

  }

  /**
   * Actualiza la entidad {@link RequisitoEquipo} por {@link Convocatoria}
   * 
   * @param requisitoEquipoActualizar la entidad {@link RequisitoEquipo} a
   *                                  guardar.
   * @param convocatoriaId            Identificador de la {@link Convocatoria}
   * @return RequisitoEquipo la entidad {@link RequisitoEquipo} persistida.
   */
  @Transactional
  public RequisitoEquipo update(RequisitoEquipo requisitoEquipoActualizar, Long convocatoriaId) {
    log.debug("update(RequisitoEquipo requisitoEquipoActualizar, Long convocatoriaId) - start");

    AssertHelper.idNotNull(convocatoriaId, RequisitoEquipo.class);

    // Comprobación de existencia de Convocatoria
    if (!convocatoriaRepository.existsById(convocatoriaId)) {
      throw new ConvocatoriaNotFoundException(convocatoriaId);
    }

    return repository.findById(convocatoriaId).map(requisitoEquipo -> {
      requisitoEquipo.setFechaMinimaNivelAcademico(requisitoEquipoActualizar.getFechaMinimaNivelAcademico());
      requisitoEquipo.setFechaMaximaNivelAcademico(requisitoEquipoActualizar.getFechaMaximaNivelAcademico());
      requisitoEquipo
          .setFechaMinimaCategoriaProfesional(requisitoEquipoActualizar.getFechaMinimaCategoriaProfesional());
      requisitoEquipo
          .setFechaMaximaCategoriaProfesional(requisitoEquipoActualizar.getFechaMaximaCategoriaProfesional());
      requisitoEquipo.setEdadMaxima(requisitoEquipoActualizar.getEdadMaxima());
      requisitoEquipo.setRatioSexo(requisitoEquipoActualizar.getRatioSexo());
      requisitoEquipo.setNumMaximoCompetitivosActivos(requisitoEquipoActualizar.getNumMaximoCompetitivosActivos());
      requisitoEquipo.setNumMaximoNoCompetitivosActivos(requisitoEquipoActualizar.getNumMaximoNoCompetitivosActivos());
      requisitoEquipo.setNumMinimoCompetitivos(requisitoEquipoActualizar.getNumMinimoCompetitivos());
      requisitoEquipo.setNumMinimoNoCompetitivos(requisitoEquipoActualizar.getNumMinimoNoCompetitivos());
      requisitoEquipo.setOtrosRequisitos(requisitoEquipoActualizar.getOtrosRequisitos());
      requisitoEquipo.setSexoRef(requisitoEquipoActualizar.getSexoRef());
      requisitoEquipo.setVinculacionUniversidad(requisitoEquipoActualizar.getVinculacionUniversidad());

      RequisitoEquipo returnValue = repository.save(requisitoEquipo);
      log.debug("update(RequisitoEquipo requisitoEquipoActualizar, Long convocatoriaId) - end");
      return returnValue;
    }).orElseThrow(() -> new RequisitoEquipoNotFoundException(requisitoEquipoActualizar.getId()));

  }

  /**
   * Obtiene el {@link RequisitoEquipo} para una {@link Convocatoria}.
   *
   * @param id el id de la {@link Convocatoria}.
   * @return la entidad {@link RequisitoEquipo} de la {@link Convocatoria}
   */
  public RequisitoEquipo findByConvocatoriaId(Long id) {
    log.debug("findByConvocatoriaId(Long id) - start");

    if (convocatoriaRepository.existsById(id)) {
      final Optional<RequisitoEquipo> returnValue = repository.findById(id);
      log.debug("findByConvocatoriaId(Long id) - end");
      return (returnValue.isPresent()) ? returnValue.get() : null;
    } else {
      throw new ConvocatoriaNotFoundException(id);
    }
  }

}
