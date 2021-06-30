package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.exceptions.CargoComiteNotFoundException;
import org.crue.hercules.sgi.eti.model.CargoComite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link CargoComite}.
 */
public interface CargoComiteService {
  /**
   * Guardar {@link CargoComite}.
   *
   * @param cargoComite la entidad {@link CargoComite} a guardar.
   * @return la entidad {@link CargoComite} persistida.
   */
  CargoComite create(CargoComite cargoComite);

  /**
   * Actualizar {@link CargoComite}.
   *
   * @param cargoComite la entidad {@link CargoComite} a actualizar.
   * @return la entidad {@link CargoComite} persistida.
   */
  CargoComite update(CargoComite cargoComite);

  /**
   * Obtener todas las entidades {@link CargoComite} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link CargoComite} paginadas y/o filtradas.
   */
  Page<CargoComite> findAll(String query, Pageable pageable);

  /**
   * Obtiene {@link CargoComite} por id.
   *
   * @param id el id de la entidad {@link CargoComite}.
   * @return la entidad {@link CargoComite}.
   */
  CargoComite findById(Long id);

  /**
   * Elimina el {@link CargoComite} por id.
   *
   * @param id el id de la entidad {@link CargoComite}.
   */
  void delete(Long id) throws CargoComiteNotFoundException;

  /**
   * Elimina todos los {@link CargoComite}.
   */
  void deleteAll();

}