package org.crue.hercules.sgi.csp.service;

import org.apache.commons.lang3.StringUtils;
import org.crue.hercules.sgi.csp.exceptions.ProyectoFacturacionNotFoundException;
import org.crue.hercules.sgi.csp.model.EstadoValidacionIP;
import org.crue.hercules.sgi.csp.model.EstadoValidacionIP.TipoEstadoValidacion;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoFacturacion;
import org.crue.hercules.sgi.csp.repository.EstadoValidacionIPRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoFacturacionRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.predicate.ProyectoFacturacionPredicateResolver;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoFacturacionSpecifications;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.security.core.context.SgiSecurityContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ProyectoFacturacionService {

  private static final String USER_NOT_ALLOWED_MESSAGE = "La Unidad de Gestión no es gestionable por el usuario";
  private static final String ALLOWED_ROLE = "CSP-PRO-E";

  private final ProyectoFacturacionRepository proyectoFacturacionRepository;
  private final EstadoValidacionIPRepository estadoValidacionIPRepository;
  private final ProyectoRepository proyectoRepository;

  /**
   * Busca todos los objetos de tipo {@link ProyectoFacturacion} cuyo proyectoId
   * sea igual al que se recibe por parámetro
   * 
   * @param proyectoId id del {@link Proyecto} asociado
   * @param paging     Información de paginación
   * @return pagina de {@link ProyectoFacturacion}
   */
  public Page<ProyectoFacturacion> findByProyectoId(Long proyectoId, Pageable paging) {

    return this.proyectoFacturacionRepository.findByProyectoId(proyectoId, paging);
  }

  /**
   * Crea y persiste un objeto de tipo {@link ProyectoFacturacion}
   * 
   * @param toCreate objeto {@link ProyectoFacturacion} a persistir
   * @return objeto de tipo {@link ProyectoFacturacion} persistido
   */
  @Transactional
  public ProyectoFacturacion create(ProyectoFacturacion toCreate) {

    Assert.isNull(toCreate.getId(), "ProyectoFacturacion id tiene que ser null para crear un item de facturación");

    Assert.isTrue(
        SgiSecurityContextHolder.hasAuthorityForUO(ALLOWED_ROLE, getUnidadGestionRef(toCreate.getProyectoId())),
        USER_NOT_ALLOWED_MESSAGE);

    ProyectoFacturacion proyectoFacturacionAfterCreate = this.proyectoFacturacionRepository.save(toCreate);

    proyectoFacturacionAfterCreate.setEstadoValidacionIP(
        this.estadoValidacionIPRepository.save(EstadoValidacionIP.builder().estado(TipoEstadoValidacion.PENDIENTE)
            .proyectoFacturacionId(proyectoFacturacionAfterCreate.getId()).build()));

    return this.proyectoFacturacionRepository.save(proyectoFacturacionAfterCreate);
  }

  /**
   * Actualiza un objeto de tipo {@link ProyectoFacturacion}
   * 
   * @param toUpdate objeto de tipo {@link ProyectoFacturacion} con la información
   *                 a actualizar
   * @return objeto de tipo {@link ProyectoFacturacion}
   */
  @Transactional
  public ProyectoFacturacion update(ProyectoFacturacion toUpdate) {

    Assert.isTrue(
        SgiSecurityContextHolder.hasAuthorityForUO(ALLOWED_ROLE, getUnidadGestionRef(toUpdate.getProyectoId())),
        USER_NOT_ALLOWED_MESSAGE);

    ProyectoFacturacion beforeUpdate = this.proyectoFacturacionRepository.findById(toUpdate.getId())
        .orElseThrow(() -> new ProyectoFacturacionNotFoundException(toUpdate.getId()));

    beforeUpdate.setComentario(toUpdate.getComentario());
    beforeUpdate.setImporteBase(toUpdate.getImporteBase());
    beforeUpdate.setPorcentajeIVA(toUpdate.getPorcentajeIVA());
    beforeUpdate.setTipoFacturacion(toUpdate.getTipoFacturacion());
    beforeUpdate.setFechaEmision(toUpdate.getFechaEmision());
    beforeUpdate.setFechaConformidad(toUpdate.getFechaConformidad());

    if (toUpdate.getEstadoValidacionIP().getId() == null) {
      beforeUpdate.setEstadoValidacionIP(persistEstadoValidacionIP(toUpdate.getEstadoValidacionIP(), toUpdate.getId()));
    }

    return this.proyectoFacturacionRepository.save(beforeUpdate);
  }

  private EstadoValidacionIP persistEstadoValidacionIP(EstadoValidacionIP fromEstado, Long proyectoFacturacionId) {
    return this.estadoValidacionIPRepository.save(EstadoValidacionIP.builder().comentario(fromEstado.getComentario())
        .estado(fromEstado.getEstado()).proyectoFacturacionId(proyectoFacturacionId).build());
  }

  /**
   * Elimina de la base de datos un objeto de tipo {@link ProyectoFacturacion}
   * cuyo id coincide con el que se recibe por parámetro
   * 
   * @param toDeleteId id {@link Long} del {@link ProyectoFacturacion} a eliminar
   */
  @Transactional
  public void delete(Long toDeleteId) {

    ProyectoFacturacion proyectoToDelete = this.proyectoFacturacionRepository.findById(toDeleteId)
        .orElseThrow(() -> new ProyectoFacturacionNotFoundException(toDeleteId));

    Assert.isTrue(
        SgiSecurityContextHolder.hasAuthorityForUO(ALLOWED_ROLE, getUnidadGestionRef(proyectoToDelete.getProyectoId())),
        USER_NOT_ALLOWED_MESSAGE);

    proyectoToDelete.setEstadoValidacionIP(null);

    this.proyectoFacturacionRepository.save(proyectoToDelete);

    this.estadoValidacionIPRepository.deleteByProyectoFacturacionId(toDeleteId);

    this.proyectoFacturacionRepository.deleteById(toDeleteId);
  }

  private String getUnidadGestionRef(Long proyectoId) {

    return proyectoRepository.findById(proyectoId).map(proyecto -> proyecto.getUnidadGestionRef())
        .orElse(StringUtils.EMPTY);

  }

  /**
   * Obtiene las {@link ProyectoFacturacion} para una {@link Proyecto}.
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link ProyectoFacturacion} de la
   *         {@link Proyecto} paginadas.
   */
  public Page<ProyectoFacturacion> findFacturasPrevistas(String query, Pageable pageable) {

    Specification<ProyectoFacturacion> specs = ProyectoFacturacionSpecifications.validado()
        .and(ProyectoFacturacionSpecifications.byFechaConformidadNotNull())
        .and(SgiRSQLJPASupport.toSpecification(query, ProyectoFacturacionPredicateResolver.getInstance()));

    return proyectoFacturacionRepository.findAll(specs, pageable);

  }

}
