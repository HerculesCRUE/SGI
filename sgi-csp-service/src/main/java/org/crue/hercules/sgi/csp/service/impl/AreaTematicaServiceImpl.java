package org.crue.hercules.sgi.csp.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.crue.hercules.sgi.csp.exceptions.AreaTematicaNotFoundException;
import org.crue.hercules.sgi.csp.model.AreaTematica;
import org.crue.hercules.sgi.csp.repository.AreaTematicaRepository;
import org.crue.hercules.sgi.csp.repository.specification.AreaTematicaSpecifications;
import org.crue.hercules.sgi.csp.service.AreaTematicaService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link AreaTematica}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class AreaTematicaServiceImpl implements AreaTematicaService {

  private final AreaTematicaRepository repository;

  private final int BUSCAR_NOMBRE = 1;
  private final int BUSCAR_DESCRIPCION = 2;

  public AreaTematicaServiceImpl(AreaTematicaRepository areaTematicaRepository) {
    this.repository = areaTematicaRepository;
  }

  /**
   * Guardar un nuevo {@link AreaTematica}.
   *
   * @param areaTematica la entidad {@link AreaTematica} a guardar.
   * @return la entidad {@link AreaTematica} persistida.
   */
  @Override
  @Transactional
  public AreaTematica create(AreaTematica areaTematica) {
    log.debug("create(AreaTematica areaTematica) - start");

    Assert.isNull(areaTematica.getId(), "AreaTematica id tiene que ser null para crear un nuevo AreaTematica");

    if (areaTematica.getPadre() != null) {
      if (areaTematica.getPadre().getId() == null) {
        areaTematica.setPadre(null);
      } else {
        areaTematica.setPadre(repository.findById(areaTematica.getPadre().getId())
            .orElseThrow(() -> new AreaTematicaNotFoundException(areaTematica.getPadre().getId())));
      }
    }

    if (areaTematica.getPadre() == null) {
      Assert.isTrue(!existGrupoWithNombre(areaTematica.getNombre(), null), "Ya existe un grupo con el mismo nombre");
    } else {
      // nombre(back) ==> abreviatura(front)
      // descripcion(back) ==> nombre(front)
      Assert.isTrue(areaTematica.getPadre().getActivo(),
          "AreaTematica padre '" + areaTematica.getPadre().getNombre() + "' está desactivada");

      Assert.isTrue(areaTematica.getNombre().length() <= 5,
          "Se ha superado la longitud máxima permitida para la abreviatura de AreaTematica (5)");

      Assert.isTrue(StringUtils.isNotBlank(areaTematica.getDescripcion()),
          "El nombre de AreaTematica es un campo obligatorio");

      Assert.isTrue(areaTematica.getDescripcion().length() <= 50,
          "Se ha superado la longitud máxima permitida para el nombre de AreaTematica (50)");

      Assert.isTrue(!existAreaTematicaNombreDescripcion(areaTematica.getPadre().getId(), areaTematica.getNombre(), null,
          BUSCAR_NOMBRE), "Ya existe un AreaTematica con la misma abreviatura en el grupo");

      Assert.isTrue(!existAreaTematicaNombreDescripcion(areaTematica.getPadre().getId(), areaTematica.getDescripcion(),
          null, BUSCAR_DESCRIPCION), "Ya existe un AreaTematica con el mismo nombre en el grupo");
    }

    areaTematica.setActivo(true);

    AreaTematica returnValue = repository.save(areaTematica);

    log.debug("create(AreaTematica areaTematica) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link AreaTematica}.
   *
   * @param areaTematicaActualizar la entidad {@link AreaTematica} a actualizar.
   * @return la entidad {@link AreaTematica} persistida.
   */
  @Override
  @Transactional
  public AreaTematica update(AreaTematica areaTematicaActualizar) {
    log.debug("update(AreaTematica areaTematicaActualizar) - start");

    Assert.notNull(areaTematicaActualizar.getId(), "AreaTematica id no puede ser null para actualizar un AreaTematica");

    if (areaTematicaActualizar.getPadre() != null) {
      if (areaTematicaActualizar.getPadre().getId() == null) {
        areaTematicaActualizar.setPadre(null);
      } else {
        areaTematicaActualizar.setPadre(repository.findById(areaTematicaActualizar.getPadre().getId())
            .orElseThrow(() -> new AreaTematicaNotFoundException(areaTematicaActualizar.getPadre().getId())));
      }
    }

    return repository.findById(areaTematicaActualizar.getId()).map(areaTematica -> {
      if (areaTematica.getPadre() == null) {
        Assert.isTrue(!existGrupoWithNombre(areaTematicaActualizar.getNombre(), areaTematicaActualizar.getId()),
            "Ya existe un grupo con el mismo nombre");
      } else {
        // nombre(back) ==> abreviatura(front)
        // descripcion(back) ==> nombre(front)
        if (areaTematica.getPadre().getId() != areaTematicaActualizar.getPadre().getId()) {
          Assert.isTrue(areaTematicaActualizar.getPadre().getActivo(),
              "AreaTematica padre '" + areaTematicaActualizar.getPadre().getNombre() + "' está desactivada");
        }

        Assert.isTrue(areaTematicaActualizar.getNombre().length() <= 5,
            "Se ha superado la longitud máxima permitida para la abreviatura de AreaTematica (5)");

        Assert.isTrue(StringUtils.isNotBlank(areaTematicaActualizar.getDescripcion()),
            "El nombre de AreaTematica es un campo obligatorio");

        Assert.isTrue(areaTematicaActualizar.getDescripcion().length() <= 50,
            "Se ha superado la longitud máxima permitida para el nombre de AreaTematica (50)");

        Assert.isTrue(
            !existAreaTematicaNombreDescripcion(areaTematicaActualizar.getPadre().getId(),
                areaTematicaActualizar.getNombre(), areaTematicaActualizar.getId(), BUSCAR_NOMBRE),
            "Ya existe un AreaTematica con la misma abreviatura en el grupo");

        Assert.isTrue(
            !existAreaTematicaNombreDescripcion(areaTematicaActualizar.getPadre().getId(),
                areaTematicaActualizar.getDescripcion(), areaTematicaActualizar.getId(), BUSCAR_DESCRIPCION),
            "Ya existe un AreaTematica con el mismo nombre en el grupo");
      }

      areaTematica.setNombre(areaTematicaActualizar.getNombre());
      areaTematica.setDescripcion(areaTematicaActualizar.getDescripcion());
      areaTematica.setPadre(areaTematicaActualizar.getPadre());

      AreaTematica returnValue = repository.save(areaTematica);
      log.debug("update(AreaTematica areaTematicaActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new AreaTematicaNotFoundException(areaTematicaActualizar.getId()));
  }

  /**
   * Reactiva el {@link AreaTematica}.
   *
   * @param id Id del {@link AreaTematica}.
   * @return la entidad {@link AreaTematica} persistida.
   */
  @Override
  @Transactional
  public AreaTematica enable(Long id) {
    log.debug("enable(Long id) - start");

    Assert.notNull(id, "AreaTematica id no puede ser null para reactivar un AreaTematica");

    return repository.findById(id).map(areaTematica -> {
      if (areaTematica.getActivo()) {
        // Si esta activo no se hace nada
        return areaTematica;
      }

      Assert.isTrue(areaTematica.getPadre() == null, "Solo se puede reactivar si es un grupo (AreaTematica sin padre)");

      Assert.isTrue(!existGrupoWithNombre(areaTematica.getNombre(), areaTematica.getId()),
          "Ya existe un grupo con el mismo nombre");

      areaTematica.setActivo(true);

      AreaTematica returnValue = repository.save(areaTematica);
      log.debug("enable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new AreaTematicaNotFoundException(id));
  }

  /**
   * Desactiva el {@link AreaTematica}
   *
   * @param id Id del {@link AreaTematica}.
   * @return la entidad {@link AreaTematica} persistida.
   */
  @Override
  @Transactional
  public AreaTematica disable(Long id) {
    log.debug("disable(Long id) - start");

    Assert.notNull(id, "AreaTematica id no puede ser null para desactivar un AreaTematica");

    return repository.findById(id).map(areaTematica -> {
      if (!areaTematica.getActivo()) {
        // Si no esta activo no se hace nada
        return areaTematica;
      }

      areaTematica.setActivo(false);
      AreaTematica returnValue = repository.save(areaTematica);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new AreaTematicaNotFoundException(id));
  }

  /**
   * Obtiene {@link AreaTematica} por su id.
   *
   * @param id el id de la entidad {@link AreaTematica}.
   * @return la entidad {@link AreaTematica}.
   */
  @Override
  public AreaTematica findById(Long id) {
    log.debug("findById(Long id)  - start");
    final AreaTematica returnValue = repository.findById(id).orElseThrow(() -> new AreaTematicaNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Obtiene los {@link AreaTematica} activos.
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link AreaTematica} paginadas.
   */
  @Override
  public Page<AreaTematica> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<AreaTematica> specs = AreaTematicaSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<AreaTematica> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene los grupos activos (los {@link AreaTematica} con padre null).
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link AreaTematica} paginadas.
   */
  @Override
  public Page<AreaTematica> findAllGrupo(String query, Pageable pageable) {
    log.debug("findAllGrupo(String query, Pageable pageable) - start");
    Specification<AreaTematica> specs = AreaTematicaSpecifications.grupos().and(AreaTematicaSpecifications.activos())
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<AreaTematica> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllGrupo(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene los grupos (los {@link AreaTematica} con padre null).
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link AreaTematica} paginadas.
   */
  @Override
  public Page<AreaTematica> findAllTodosGrupo(String query, Pageable pageable) {
    log.debug("findAllTodosGrupo(String query, Pageable pageable) - start");
    Specification<AreaTematica> specs = AreaTematicaSpecifications.grupos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<AreaTematica> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllTodosGrupo(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene los {@link AreaTematica} hijos directos del {@link AreaTematica} con
   * el id indicado.
   *
   * @param areaTematicaId el id de la entidad {@link AreaTematica}.
   * @param query          la información del filtro.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link AreaTematica} paginadas.
   */
  @Override
  public Page<AreaTematica> findAllHijosAreaTematica(Long areaTematicaId, String query, Pageable pageable) {
    log.debug("findAllHijosAreaTematica(Long areaTematicaId, String query, Pageable pageable) - start");
    Specification<AreaTematica> specs = AreaTematicaSpecifications.hijos(areaTematicaId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<AreaTematica> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllHijosAreaTematica(Long areaTematicaId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Comprueba si existe algun grupo ({@link AreaTematica} con padre null) con el
   * nombre indicado.
   *
   * @param nombre                nombre del grupo.
   * @param areaTematicaIdExcluir Identificador del {@link AreaTematica} que se
   *                              excluye de la busqueda.
   * @return true si existe algun grupo con ese nombre.
   */

  private boolean existGrupoWithNombre(String nombre, Long areaTematicaIdExcluir) {
    log.debug("existGrupoWithNombre(String nombre, Long areaTematicaIdExcluir) - start");
    Specification<AreaTematica> specGruposByNombre = AreaTematicaSpecifications.gruposByNombre(nombre,
        areaTematicaIdExcluir);

    boolean returnValue = !repository.findAll(specGruposByNombre, Pageable.unpaged()).isEmpty();

    log.debug("existGrupoWithNombre(String nombre, Long areaTematicaIdExcluir) - end");
    return returnValue;
  }

  /**
   * Comprueba si existe {@link AreaTematica} con el nombre indicado en el arbol
   * del areaTematica indicado.
   *
   * @param areaTematicaId        Identificador del {@link AreaTematica}.
   * @param textoBuscar           nombre del areaTematica.
   * @param areaTematicaIdExcluir Identificador del {@link AreaTematica} que se
   *                              excluye de la busqueda.
   * @return true si existe algun {@link AreaTematica} con ese nombre.
   */

  private boolean existAreaTematicaNombreDescripcion(Long areaTematicaId, String textoBuscar,
      Long areaTematicaIdExcluir, int tipoBusqueda) {
    log.debug(
        "existAreaTematicaNombreDescripcion(Long areaTematicaId, String textoBuscar,Long areaTematicaIdExcluir, int tipoBusqueda) - start");

    // Busca el areaTematica raiz
    AreaTematica areaTematicaRaiz = repository.findById(areaTematicaId).map(areaTematica -> {
      return areaTematica;
    }).orElseThrow(() -> new AreaTematicaNotFoundException(areaTematicaId));

    while (areaTematicaRaiz.getPadre() != null) {
      areaTematicaRaiz = repository.findById(areaTematicaRaiz.getPadre().getId()).get();
    }

    // Busca el nombre desde el nodo raiz nivel a nivel
    boolean textoEncontrado = false;

    List<AreaTematica> areaTematicasHijos = repository
        .findByPadreIdInAndActivoIsTrue(Arrays.asList(areaTematicaRaiz.getId()));

    if (tipoBusqueda == BUSCAR_NOMBRE) {
      textoEncontrado = areaTematicasHijos.stream()
          .anyMatch(areaTematica -> areaTematica.getNombre().equals(textoBuscar)
              && areaTematica.getId() != areaTematicaIdExcluir);
    } else if (tipoBusqueda == BUSCAR_DESCRIPCION) {
      textoEncontrado = areaTematicasHijos.stream()
          .anyMatch(areaTematica -> areaTematica.getDescripcion().equals(textoBuscar)
              && areaTematica.getId() != areaTematicaIdExcluir);
    }

    while (!textoEncontrado && !areaTematicasHijos.isEmpty()) {
      areaTematicasHijos = repository.findByPadreIdInAndActivoIsTrue(
          areaTematicasHijos.stream().map(AreaTematica::getId).collect(Collectors.toList()));
      if (tipoBusqueda == BUSCAR_NOMBRE) {
        textoEncontrado = areaTematicasHijos.stream()
            .anyMatch(areaTematica -> areaTematica.getNombre().equals(textoBuscar)
                && areaTematica.getId() != areaTematicaIdExcluir);
      } else if (tipoBusqueda == BUSCAR_DESCRIPCION) {
        textoEncontrado = areaTematicasHijos.stream()
            .anyMatch(areaTematica -> areaTematica.getDescripcion().equals(textoBuscar)
                && areaTematica.getId() != areaTematicaIdExcluir);
      }
    }

    log.debug(
        "existAreaTematicaNombreDescripcion(Long areaTematicaId, String textoBuscar,Long areaTematicaIdExcluir, int tipoBusqueda) - end");
    return textoEncontrado;
  }

}
