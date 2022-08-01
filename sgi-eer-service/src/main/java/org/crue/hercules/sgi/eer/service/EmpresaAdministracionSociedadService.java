package org.crue.hercules.sgi.eer.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.eer.exceptions.EmpresaAdministracionSociedadMiembroException;
import org.crue.hercules.sgi.eer.exceptions.EmpresaAdministracionSociedadNotFoundException;
import org.crue.hercules.sgi.eer.exceptions.EmpresaNotFoundException;
import org.crue.hercules.sgi.eer.model.BaseEntity;
import org.crue.hercules.sgi.eer.model.Empresa;
import org.crue.hercules.sgi.eer.model.EmpresaAdministracionSociedad;
import org.crue.hercules.sgi.eer.model.EmpresaAdministracionSociedad.TipoAdministracion;
import org.crue.hercules.sgi.eer.repository.EmpresaAdministracionSociedadRepository;
import org.crue.hercules.sgi.eer.repository.EmpresaRepository;
import org.crue.hercules.sgi.eer.repository.specification.EmpresaAdministracionSociedadSpecifications;
import org.crue.hercules.sgi.eer.util.AssertHelper;
import org.crue.hercules.sgi.eer.util.EmpresaAuthorityHelper;
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
 * Service para la gestión de {@link EmpresaAdministracionSociedad}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
public class EmpresaAdministracionSociedadService {

  private final EmpresaAdministracionSociedadRepository repository;
  private final EmpresaRepository empresaRepository;
  private final EmpresaAuthorityHelper authorityHelper;

  /**
   * Obtiene una entidad {@link EmpresaAdministracionSociedad} por id.
   * 
   * @param id Identificador de la entidad {@link EmpresaAdministracionSociedad}.
   * @return la entidad {@link EmpresaAdministracionSociedad}.
   */
  public EmpresaAdministracionSociedad findById(Long id) {
    log.debug("findById(Long id) - start");

    AssertHelper.idNotNull(id, EmpresaAdministracionSociedad.class);
    final EmpresaAdministracionSociedad returnValue = repository.findById(id)
        .orElseThrow(() -> new EmpresaAdministracionSociedadNotFoundException(id));

    authorityHelper.checkUserHasAuthorityViewEmpresa(returnValue.getEmpresaId());

    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link EmpresaAdministracionSociedad} paginadas
   * y/o
   * filtradas del
   * {@link Empresa}.
   *
   * @param empresaId Identificador de la entidad {@link Empresa}.
   * @param paging    la información de la paginación.
   * @param query     la información del filtro.
   * @return la lista de entidades {@link EmpresaAdministracionSociedad} paginadas
   *         y/o
   *         filtradas.
   */
  public Page<EmpresaAdministracionSociedad> findAllByEmpresa(Long empresaId, String query, Pageable paging) {
    log.debug("findAll(Long empresaId, String query, Pageable paging) - start");
    AssertHelper.idNotNull(empresaId, Empresa.class);
    authorityHelper.checkUserHasAuthorityViewEmpresa(empresaId);

    Specification<EmpresaAdministracionSociedad> specs = EmpresaAdministracionSociedadSpecifications
        .byEmpresaId(empresaId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<EmpresaAdministracionSociedad> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(Long empresaId, String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Actualiza el listado de {@link EmpresaAdministracionSociedad} de la
   * {@link Empresa} con el
   * listado empresaAdministracionSociedades añadiendo, editando o eliminando los
   * elementos segun
   * proceda.
   *
   * @param empresaId                       Id de la {@link Empresa}.
   * @param empresaAdministracionSociedades lista con los nuevos
   *                                        {@link EmpresaAdministracionSociedad}
   *                                        a
   *                                        guardar.
   * @return la entidad {@link EmpresaAdministracionSociedad} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public List<EmpresaAdministracionSociedad> update(Long empresaId,
      @Valid List<EmpresaAdministracionSociedad> empresaAdministracionSociedades) {
    log.debug("update(Long empresaId, List<EmpresaAdministracionSociedad> empresaAdministracionSociedades) - start");

    AssertHelper.idNotNull(empresaId, Empresa.class);
    authorityHelper.checkUserHasAuthorityViewEmpresa(empresaId);

    if (!empresaRepository.existsById(empresaId)) {
      throw new EmpresaNotFoundException(empresaId);
    }

    List<EmpresaAdministracionSociedad> empresaAdministracionSociedadesBD = repository.findAllByEmpresaId(empresaId);

    // Empresa composicion sociedades eliminados
    List<EmpresaAdministracionSociedad> empresaAdministracionSociedadesEliminar = empresaAdministracionSociedadesBD
        .stream()
        .filter(
            empresaAdministracionSociedad -> empresaAdministracionSociedades.stream()
                .map(EmpresaAdministracionSociedad::getId)
                .noneMatch(id -> Objects.equals(id, empresaAdministracionSociedad.getId())))
        .collect(Collectors.toList());

    if (!empresaAdministracionSociedadesEliminar.isEmpty()) {
      repository.deleteAll(empresaAdministracionSociedadesEliminar);
    }

    this.validateEmpresaAdministracionSociedad(empresaAdministracionSociedades);

    List<EmpresaAdministracionSociedad> returnValue = repository.saveAll(empresaAdministracionSociedades);
    log.debug("update(Long empresaId, List<EmpresaAdministracionSociedad> empresaAdministracionSociedades) - END");

    return returnValue;
  }

  private void validateEmpresaAdministracionSociedad(
      List<EmpresaAdministracionSociedad> empresaAdministracionSociedades) {
    if (!empresaAdministracionSociedades.isEmpty()) {
      Integer countAdminUnico = empresaAdministracionSociedades.stream()
          .filter(e -> e.getTipoAdministracion().equals(TipoAdministracion.ADMINISTRADOR_UNICO))
          .collect(Collectors.toList()).size();

      Integer countAdminMancomunado = empresaAdministracionSociedades.stream()
          .filter(e -> e.getTipoAdministracion().equals(TipoAdministracion.ADMINISTRADOR_MANCOMUNADO))
          .collect(Collectors.toList()).size();

      Integer countAdminSolidario = empresaAdministracionSociedades.stream()
          .filter(e -> e.getTipoAdministracion().equals(TipoAdministracion.ADMINISTRADOR_SOLIDARIO))
          .collect(Collectors.toList()).size();

      Integer countConsejoAdmin = empresaAdministracionSociedades.stream()
          .filter(e -> e.getTipoAdministracion().equals(TipoAdministracion.CONSEJO_ADMINISTRACION))
          .collect(Collectors.toList()).size();

      boolean error = true;
      if (countAdminUnico == 1 || countAdminSolidario >= 2 || countAdminMancomunado >= 2 || countConsejoAdmin >= 3) {
        error = false;
      }

      if (error) {
        throw new EmpresaAdministracionSociedadMiembroException();
      }

    } else {
      throw new EmpresaAdministracionSociedadMiembroException();
    }
  }

}
