package org.crue.hercules.sgi.eti.service.impl;

import org.crue.hercules.sgi.eti.exceptions.FormularioNotFoundException;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.repository.FormularioRepository;
import org.crue.hercules.sgi.eti.service.FormularioService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link Formulario}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class FormularioServiceImpl implements FormularioService {
  private final FormularioRepository formularioRepository;

  public FormularioServiceImpl(FormularioRepository formularioRepository) {
    this.formularioRepository = formularioRepository;
  }

  /**
   * Guarda la entidad {@link Formulario}.
   *
   * @param formulario la entidad {@link Formulario} a guardar.
   * @return la entidad {@link Formulario} persistida.
   */
  @Transactional
  public Formulario create(Formulario formulario) {
    log.debug("Petición a create Formulario : {} - start", formulario);
    Assert.notNull(formulario.getId(), "Formulario id no puede ser null para crear un nuevo formulario");

    return formularioRepository.save(formulario);
  }

  /**
   * Obtiene todas las entidades {@link Formulario} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link Formulario} paginadas y filtradas.
   */
  public Page<Formulario> findAll(String query, Pageable paging) {
    log.debug("findAllFormulario(String query,Pageable paging) - start");
    Specification<Formulario> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<Formulario> returnValue = formularioRepository.findAll(specs, paging);
    log.debug("findAllFormulario(String query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link Formulario} por id.
   *
   * @param id el id de la entidad {@link Formulario}.
   * @return la entidad {@link Formulario}.
   * @throws FormularioNotFoundException Si no existe ningún {@link Formulario}e
   *                                     con ese id.
   */
  public Formulario findById(final Long id) throws FormularioNotFoundException {
    log.debug("Petición a get Formulario : {}  - start", id);
    final Formulario formulario = formularioRepository.findById(id)
        .orElseThrow(() -> new FormularioNotFoundException(id));
    log.debug("Petición a get Formulario : {}  - end", id);
    return formulario;

  }

  /**
   * Elimina una entidad {@link Formulario} por id.
   *
   * @param id el id de la entidad {@link Formulario}.
   */
  @Transactional
  public void delete(Long id) throws FormularioNotFoundException {
    log.debug("Petición a delete Formulario : {}  - start", id);
    Assert.notNull(id, "El id de Formulario no puede ser null.");
    if (!formularioRepository.existsById(id)) {
      throw new FormularioNotFoundException(id);
    }
    formularioRepository.deleteById(id);
    log.debug("Petición a delete Formulario : {}  - end", id);
  }

  /**
   * Elimina todos los registros {@link Formulario}.
   */
  @Transactional
  public void deleteAll() {
    log.debug("Petición a deleteAll de Formulario: {} - start");
    formularioRepository.deleteAll();
    log.debug("Petición a deleteAll de Formulario: {} - end");

  }

  /**
   * Actualiza los datos del {@link Formulario}.
   * 
   * @param formularioActualizar {@link Formulario} con los datos actualizados.
   * @return El {@link Formulario} actualizado.
   * @throws FormularioNotFoundException Si no existe ningún {@link Formulario}
   *                                     con ese id.
   * @throws IllegalArgumentException    Si el {@link Formulario} no tiene id.
   */

  @Transactional
  public Formulario update(final Formulario formularioActualizar) {
    log.debug("update(Formulario FormularioActualizar) - start");

    Assert.notNull(formularioActualizar.getId(), "Formulario id no puede ser null para actualizar un formulario");

    return formularioRepository.findById(formularioActualizar.getId()).map(formulario -> {
      formulario.setNombre(formularioActualizar.getNombre());
      formulario.setDescripcion(formularioActualizar.getDescripcion());

      Formulario returnValue = formularioRepository.save(formulario);
      log.debug("update(Formulario FormularioActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new FormularioNotFoundException(formularioActualizar.getId()));
  }

}
