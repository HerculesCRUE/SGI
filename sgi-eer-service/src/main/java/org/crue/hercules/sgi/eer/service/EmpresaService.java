package org.crue.hercules.sgi.eer.service;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.eer.exceptions.EmpresaNotFoundException;
import org.crue.hercules.sgi.eer.model.BaseEntity;
import org.crue.hercules.sgi.eer.model.Empresa;
import org.crue.hercules.sgi.eer.repository.EmpresaRepository;
import org.crue.hercules.sgi.eer.repository.predicate.EmpresaPredicateResolver;
import org.crue.hercules.sgi.eer.repository.specification.EmpresaSpecifications;
import org.crue.hercules.sgi.eer.util.AssertHelper;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service para la gestión de {@link Empresa}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
public class EmpresaService {

  private final EmpresaRepository repository;

  /**
   * Guarda la entidad {@link Empresa}.
   * 
   * @param empresa la entidad {@link Empresa} a guardar.
   * @return la entidad {@link Empresa} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public Empresa create(@Valid Empresa empresa) {
    log.debug("create(Empresa empresa) - start");

    AssertHelper.idIsNull(empresa.getId(), Empresa.class);

    Empresa newEmpresa = repository.save(empresa);

    log.debug("create(Empresa empresa) - end");
    return newEmpresa;
  }

  /**
   * Actualiza los datos del {@link Empresa}.
   *
   * @param empresaActualizar {@link Empresa} con los datos actualizados.
   * @return {@link Empresa} actualizado.
   */
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public Empresa update(@Valid Empresa empresaActualizar) {
    log.debug("update(Empresa empresaActualizar) - start");

    AssertHelper.idNotNull(empresaActualizar.getId(), Empresa.class);

    return repository.findById(empresaActualizar.getId()).map(data -> {
      data.setConocimientoTecnologia(empresaActualizar.getConocimientoTecnologia());
      data.setEntidadRef(empresaActualizar.getEntidadRef());
      data.setEstado(empresaActualizar.getEstado());
      data.setFechaAprobacionCG(empresaActualizar.getFechaAprobacionCG());
      data.setFechaCese(empresaActualizar.getFechaCese());
      data.setFechaConstitucion(empresaActualizar.getFechaConstitucion());
      data.setFechaDesvinculacion(empresaActualizar.getFechaDesvinculacion());
      data.setFechaIncorporacion(empresaActualizar.getFechaIncorporacion());
      data.setFechaSolicitud(empresaActualizar.getFechaSolicitud());
      data.setNombreRazonSocial(empresaActualizar.getNombreRazonSocial());
      data.setNotario(empresaActualizar.getNotario());
      data.setNumeroProtocolo(empresaActualizar.getNumeroProtocolo());
      data.setObjetoSocial(empresaActualizar.getObjetoSocial());
      data.setObservaciones(empresaActualizar.getObservaciones());
      data.setSolicitanteRef(empresaActualizar.getSolicitanteRef());
      data.setTipoEmpresa(empresaActualizar.getTipoEmpresa());

      Empresa returnValue = repository.save(data);

      log.debug("update(Empresa empresaActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new EmpresaNotFoundException(empresaActualizar.getId()));
  }

  /**
   * Obtiene una entidad {@link Empresa} por id.
   * 
   * @param id Identificador de la entidad {@link Empresa}.
   * @return la entidad {@link Empresa}.
   */
  public Empresa findById(Long id) {
    log.debug("findById(Long id) - start");

    AssertHelper.idNotNull(id, Empresa.class);
    final Empresa returnValue = repository.findById(id).orElseThrow(() -> new EmpresaNotFoundException(id));

    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Comprueba la existencia del {@link Empresa} por id.
   *
   * @param id el id de la entidad {@link Empresa}.
   * @return <code>true</code> si existe y <code>false</code> en caso contrario.
   */
  public boolean existsById(Long id) {
    log.debug("existsById(Long id)  - start");

    AssertHelper.idNotNull(id, Empresa.class);
    final boolean exists = repository.existsById(id);

    log.debug("existsById(Long id)  - end");
    return exists;
  }

  /**
   * Obtener todas las entidades {@link Empresa} activas paginadas y/o filtradas.
   *
   * @param paging la información de la paginación.
   * @param query  la información del filtro.
   * @return la lista de entidades {@link Empresa} activas paginadas y/o
   *         filtradas.
   */
  public Page<Empresa> findActivos(String query, Pageable paging) {
    log.debug("findActivos(String query, Pageable paging) - start");

    Specification<Empresa> specs = EmpresaSpecifications.distinct()
        .and(EmpresaSpecifications.activos())
        .and(SgiRSQLJPASupport.toSpecification(query));
    Page<Empresa> returnValue = repository.findAll(specs, paging);

    log.debug("findActivos(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link Empresa}.
   *
   * @param id Id del {@link Empresa}.
   * @return la entidad {@link Empresa} persistida.
   */
  @Transactional
  public Empresa desactivar(Long id) {
    log.debug("desactivar(Long id) - start");

    AssertHelper.idNotNull(id, Empresa.class);

    return repository.findById(id).map(empresa -> {
      if (Boolean.FALSE.equals(empresa.getActivo())) {
        // Si no esta activo no se hace nada
        return empresa;
      }

      empresa.setActivo(false);

      Empresa returnValue = repository.save(empresa);
      log.debug("desactivar(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new EmpresaNotFoundException(id));
  }

  /**
   * Obtiene los ids de {@link Empresa} modificados que cumplan las condiciones
   * indicadas en el filtro de búsqueda
   *
   * @param query información del filtro.
   * @return el listado de ids de {@link Empresa}.
   */
  public List<Long> findIdsEmpresaModificados(String query) {
    log.debug("findIdsEmpresaModificados(String query) - start");

    Specification<Empresa> specs = SgiRSQLJPASupport.toSpecification(query, EmpresaPredicateResolver.getInstance());

    List<Long> returnValue = repository.findIds(specs);

    log.debug("findIdsEmpresaModificados(String query) - end");

    return returnValue;
  }

}
