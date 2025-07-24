package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.exceptions.MemoriaNotFoundException;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link Formulario}.
 */
public interface FormularioService {
  /**
   * Obtener todas las entidades {@link Formulario} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link Formulario} paginadas y/o filtradas.
   */
  Page<Formulario> findAll(String query, Pageable pageable);

  /**
   * Obtiene {@link Formulario} por id.
   *
   * @param id el id de la entidad {@link Formulario}.
   * @return la entidad {@link Formulario}.
   */
  Formulario findById(Long id);

  /**
   * Obtiene el report asociado al {@link Formulario} en el idioma especificado
   * 
   * @param id       el id de la entidad {@link Formulario}.
   * @param language Idioma
   * @return Report asociado
   */
  byte[] getReport(Long id, Language language);

  /**
   * Comprueba si existe un report asociado al {@link Formulario} en el idioma
   * especificado
   * 
   * @param id       el id de la entidad {@link Formulario}.
   * @param language Idioma
   * @return <code>true</code> si existe un report asociado
   */
  boolean existReport(Long id, Language language);

  /**
   * Actualiza el estado de la memoria o de la retrospectiva al estado final
   * correspondiente al tipo de formulario completado.
   *
   * @param memoriaId      Identificador de la {@link Memoria}.
   * @param formularioTipo Tipo de formulario {@link Formulario.Tipo}
   * @throws MemoriaNotFoundException Si no existe ningún {@link Memoria} con ese
   *                                  id.
   */
  void completado(Long memoriaId, Formulario.Tipo formularioTipo) throws MemoriaNotFoundException;

  /**
   * Actualiza o crea el report asociado al {@link Formulario} en el idioma
   * especificado
   * 
   * @param id          el id de la entidad {@link Formulario}.
   * @param language    Idioma
   * @param reportValue report
   * @return Formulario del report asociado
   */
  Formulario updateReport(Long id, Language language, byte[] reportValue);

}