package org.crue.hercules.sgi.eti.service.impl;

import org.crue.hercules.sgi.eti.exceptions.FormacionEspecificaNotFoundException;
import org.crue.hercules.sgi.eti.model.FormacionEspecifica;
import org.crue.hercules.sgi.eti.repository.FormacionEspecificaRepository;
import org.crue.hercules.sgi.eti.repository.specification.FormacionEspecificaSpecifications;
import org.crue.hercules.sgi.eti.service.FormacionEspecificaService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link FormacionEspecifica}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class FormacionEspecificaServiceImpl implements FormacionEspecificaService {
  private final FormacionEspecificaRepository formacionEspecificaRepository;

  public FormacionEspecificaServiceImpl(FormacionEspecificaRepository formacionEspecificaRepository) {
    this.formacionEspecificaRepository = formacionEspecificaRepository;
  }

  /**
   * Guarda la entidad {@link FormacionEspecifica}.
   *
   * @param formacionEspecifica la entidad {@link FormacionEspecifica} a guardar.
   * @return la entidad {@link FormacionEspecifica} persistida.
   */
  @Transactional
  public FormacionEspecifica create(FormacionEspecifica formacionEspecifica) {
    log.debug("Petición a create FormacionEspecifica : {} - start", formacionEspecifica);
    Assert.notNull(formacionEspecifica.getId(),
        "FormacionEspecifica id no puede ser null para crear un nuevo formacionEspecifica");

    return formacionEspecificaRepository.save(formacionEspecifica);
  }

  /**
   * Obtiene todas las entidades {@link FormacionEspecifica} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link FormacionEspecifica} paginadas y
   *         filtradas.
   */
  public Page<FormacionEspecifica> findAll(String query, Pageable paging) {
    log.debug("findAllFormacionEspecifica(String query,Pageable paging) - start");
    Specification<FormacionEspecifica> specs = FormacionEspecificaSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<FormacionEspecifica> returnValue = formacionEspecificaRepository.findAll(specs, paging);
    log.debug("findAllFormacionEspecifica(String query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link FormacionEspecifica} por id.
   *
   * @param id el id de la entidad {@link FormacionEspecifica}.
   * @return la entidad {@link FormacionEspecifica}.
   * @throws FormacionEspecificaNotFoundException Si no existe ninguna
   *                                              {@link FormacionEspecifica}e con
   *                                              ese id.
   */
  public FormacionEspecifica findById(final Long id) throws FormacionEspecificaNotFoundException {
    log.debug("Petición a get FormacionEspecifica : {}  - start", id);
    final FormacionEspecifica FormacionEspecifica = formacionEspecificaRepository.findById(id)
        .orElseThrow(() -> new FormacionEspecificaNotFoundException(id));
    log.debug("Petición a get FormacionEspecifica : {}  - end", id);
    return FormacionEspecifica;

  }

  /**
   * Elimina una entidad {@link FormacionEspecifica} por id.
   *
   * @param id el id de la entidad {@link FormacionEspecifica}.
   */
  @Transactional
  public void delete(Long id) throws FormacionEspecificaNotFoundException {
    log.debug("Petición a delete FormacionEspecifica : {}  - start", id);
    Assert.notNull(id, "El id de FormacionEspecifica no puede ser null.");
    if (!formacionEspecificaRepository.existsById(id)) {
      throw new FormacionEspecificaNotFoundException(id);
    }
    formacionEspecificaRepository.deleteById(id);
    log.debug("Petición a delete FormacionEspecifica : {}  - end", id);
  }

  /**
   * Elimina todos los registros {@link FormacionEspecifica}.
   */
  @Transactional
  public void deleteAll() {
    log.debug("Petición a deleteAll de FormacionEspecifica: {} - start");
    formacionEspecificaRepository.deleteAll();
    log.debug("Petición a deleteAll de FormacionEspecifica: {} - end");

  }

  /**
   * Actualiza los datos del {@link FormacionEspecifica}.
   * 
   * @param formacionEspecificaActualizar {@link FormacionEspecifica} con los
   *                                      datos actualizados.
   * @return El {@link FormacionEspecifica} actualizado.
   * @throws FormacionEspecificaNotFoundException Si no existe ninguna
   *                                              {@link FormacionEspecifica} con
   *                                              ese id.
   * @throws IllegalArgumentException             Si la
   *                                              {@link FormacionEspecifica} no
   *                                              tiene id.
   */

  @Transactional
  public FormacionEspecifica update(final FormacionEspecifica formacionEspecificaActualizar) {
    log.debug("update(FormacionEspecifica FormacionEspecificaActualizar) - start");

    Assert.notNull(formacionEspecificaActualizar.getId(),
        "FormacionEspecifica id no puede ser null para actualizar una formacion específica");

    return formacionEspecificaRepository.findById(formacionEspecificaActualizar.getId()).map(formacionEspecifica -> {
      formacionEspecifica.setNombre(formacionEspecificaActualizar.getNombre());
      formacionEspecifica.setActivo(formacionEspecificaActualizar.getActivo());

      FormacionEspecifica returnValue = formacionEspecificaRepository.save(formacionEspecifica);
      log.debug("update(FormacionEspecifica formacionEspecificaActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new FormacionEspecificaNotFoundException(formacionEspecificaActualizar.getId()));
  }

}
