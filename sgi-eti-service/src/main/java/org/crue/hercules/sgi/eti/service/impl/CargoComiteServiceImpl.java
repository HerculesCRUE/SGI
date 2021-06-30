package org.crue.hercules.sgi.eti.service.impl;

import org.crue.hercules.sgi.eti.exceptions.CargoComiteNotFoundException;
import org.crue.hercules.sgi.eti.model.CargoComite;
import org.crue.hercules.sgi.eti.repository.CargoComiteRepository;
import org.crue.hercules.sgi.eti.repository.specification.CargoComiteSpecifications;
import org.crue.hercules.sgi.eti.service.CargoComiteService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link CargoComite}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class CargoComiteServiceImpl implements CargoComiteService {
  private final CargoComiteRepository cargoComiteRepository;

  public CargoComiteServiceImpl(CargoComiteRepository cargoComiteRepository) {
    this.cargoComiteRepository = cargoComiteRepository;
  }

  /**
   * Guarda la entidad {@link CargoComite}.
   *
   * @param cargoComite la entidad {@link CargoComite} a guardar.
   * @return la entidad {@link CargoComite} persistida.
   */
  @Transactional
  public CargoComite create(CargoComite cargoComite) {
    log.debug("Petición a create CargoComite : {} - start", cargoComite);
    Assert.notNull(cargoComite.getId(), "CargoComite id no puede ser null para crear un nuevo cargoComite");

    return cargoComiteRepository.save(cargoComite);
  }

  /**
   * Obtiene todas las entidades {@link CargoComite} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link CargoComite} paginadas y filtradas.
   */
  public Page<CargoComite> findAll(String query, Pageable paging) {
    log.debug("findAllCargoComite(String query,Pageable paging) - start");
    Specification<CargoComite> specs = CargoComiteSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<CargoComite> returnValue = cargoComiteRepository.findAll(specs, paging);

    log.debug("findAllCargoComite(String query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link CargoComite} por id.
   *
   * @param id el id de la entidad {@link CargoComite}.
   * @return la entidad {@link CargoComite}.
   * @throws CargoComiteNotFoundException Si no existe ningún {@link CargoComite}e
   *                                      con ese id.
   */
  public CargoComite findById(final Long id) throws CargoComiteNotFoundException {
    log.debug("Petición a get CargoComite : {}  - start", id);
    final CargoComite CargoComite = cargoComiteRepository.findById(id)
        .orElseThrow(() -> new CargoComiteNotFoundException(id));
    log.debug("Petición a get CargoComite : {}  - end", id);
    return CargoComite;

  }

  /**
   * Elimina una entidad {@link CargoComite} por id.
   *
   * @param id el id de la entidad {@link CargoComite}.
   */
  @Transactional
  public void delete(Long id) throws CargoComiteNotFoundException {
    log.debug("Petición a delete CargoComite : {}  - start", id);
    Assert.notNull(id, "El id de CargoComite no puede ser null.");
    if (!cargoComiteRepository.existsById(id)) {
      throw new CargoComiteNotFoundException(id);
    }
    cargoComiteRepository.deleteById(id);
    log.debug("Petición a delete CargoComite : {}  - end", id);
  }

  /**
   * Elimina todos los registros {@link CargoComite}.
   */
  @Transactional
  public void deleteAll() {
    log.debug("Petición a deleteAll de CargoComite: {} - start");
    cargoComiteRepository.deleteAll();
    log.debug("Petición a deleteAll de CargoComite: {} - end");

  }

  /**
   * Actualiza los datos del {@link CargoComite}.
   * 
   * @param cargoComiteActualizar {@link CargoComite} con los datos actualizados.
   * @return El {@link CargoComite} actualizado.
   * @throws CargoComiteNotFoundException Si no existe ningún {@link CargoComite}
   *                                      con ese id.
   * @throws IllegalArgumentException     Si el {@link CargoComite} no tiene id.
   */

  @Transactional
  public CargoComite update(final CargoComite cargoComiteActualizar) {
    log.debug("update(CargoComite CargoComiteActualizar) - start");

    Assert.notNull(cargoComiteActualizar.getId(), "CargoComite id no puede ser null para actualizar un cargo comité");

    return cargoComiteRepository.findById(cargoComiteActualizar.getId()).map(cargoComite -> {
      cargoComite.setNombre(cargoComiteActualizar.getNombre());
      cargoComite.setActivo(cargoComiteActualizar.getActivo());

      CargoComite returnValue = cargoComiteRepository.save(cargoComite);
      log.debug("update(CargoComite cargoComiteActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new CargoComiteNotFoundException(cargoComiteActualizar.getId()));
  }

}
