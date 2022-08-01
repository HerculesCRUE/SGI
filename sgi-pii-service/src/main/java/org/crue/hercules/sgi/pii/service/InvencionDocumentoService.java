package org.crue.hercules.sgi.pii.service;

import java.time.Instant;
import java.time.LocalTime;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.config.SgiConfigProperties;
import org.crue.hercules.sgi.pii.exceptions.InvencionDocumentoNotFoundException;
import org.crue.hercules.sgi.pii.model.InvencionDocumento;
import org.crue.hercules.sgi.pii.repository.InvencionDocumentoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class InvencionDocumentoService {

  private final InvencionDocumentoRepository invencionDocumentoRepository;
  private final SgiConfigProperties sgiConfigProperties;

  /**
   * Devuelve una página de objetos {@link InvencionDocumento}
   * 
   * @param invencionId id de la invención
   * @param pageable    objeto descriptor de la paginación
   * @return pagina de entidades {@link InvencionDocumento}
   */
  public Page<InvencionDocumento> findByInvencionId(Long invencionId, Pageable pageable) {
    return this.invencionDocumentoRepository.findByInvencionId(invencionId, pageable);
  }

  /**
   * Crea un nuevo {@link InvencionDocumento}.
   *
   * @param invencionDocumento la entidad {@link InvencionDocumento} a guardar.
   * @return la entidad {@link InvencionDocumento} persistida.
   */
  public InvencionDocumento create(InvencionDocumento invencionDocumento) {

    Assert.isNull(invencionDocumento.getId(),
        () -> ProblemMessage.builder().key(Assert.class, "isNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(InvencionDocumento.class)).build());

    Instant fechaAnadido = Instant.now().atZone(this.sgiConfigProperties.getTimeZone().toZoneId())
        .with(LocalTime.MAX).withNano(0).toInstant();

    invencionDocumento.setFechaAnadido(fechaAnadido);

    return this.invencionDocumentoRepository.save(invencionDocumento);
  }

  /**
   * Actualiza un objeto de tipo {@link InvencionDocumento}
   * 
   * @param invencionDocumento objeto de tipo {@link InvencionDocumento} a
   *                           actualizar
   * @return {@link InvencionDocumento} actualizado.
   */
  public InvencionDocumento update(InvencionDocumento invencionDocumento) {
    Assert.notNull(invencionDocumento.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(InvencionDocumento.class)).build());

    return this.invencionDocumentoRepository.findById(invencionDocumento.getId()).map(toSave -> {
      toSave.setNombre(invencionDocumento.getNombre());
      return this.invencionDocumentoRepository.save(toSave);
    }).orElseThrow(() -> new InvencionDocumentoNotFoundException(invencionDocumento.getId()));

  }

  /**
   * Elinina un objeto de tipo {@link InvencionDocumento} permanentemente de la
   * base de datos
   * 
   * @param id Identificador del objeto {@link InvencionDocumento}
   */
  public void delete(Long id) {
    this.invencionDocumentoRepository.deleteById(id);
  }
}
