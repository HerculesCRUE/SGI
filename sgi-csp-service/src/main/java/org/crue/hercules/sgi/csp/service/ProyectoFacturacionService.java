package org.crue.hercules.sgi.csp.service;

import java.time.Instant;

import org.crue.hercules.sgi.csp.config.SgiConfigProperties;
import org.crue.hercules.sgi.csp.exceptions.ProyectoFacturacionNotFoundException;
import org.crue.hercules.sgi.csp.model.EstadoValidacionIP;
import org.crue.hercules.sgi.csp.model.EstadoValidacionIP.TipoEstadoValidacion;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoFacturacion;
import org.crue.hercules.sgi.csp.repository.EstadoValidacionIPRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoFacturacionRepository;
import org.crue.hercules.sgi.csp.repository.TipoFacturacionRepository;
import org.crue.hercules.sgi.csp.repository.predicate.ProyectoFacturacionPredicateResolver;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoFacturacionSpecifications;
import org.crue.hercules.sgi.csp.util.ProyectoFacturacionAuthorityHelper;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ProyectoFacturacionService {

  private final ProyectoFacturacionRepository proyectoFacturacionRepository;
  private final EstadoValidacionIPRepository estadoValidacionIPRepository;
  private final TipoFacturacionRepository tipoFacturacionRepository;
  private final ProyectoFacturacionAuthorityHelper authorityHelper;
  private final ProyectoFacturacionComService proyectoFacturacionComService;
  private final SgiConfigProperties sgiConfigProperties;

  /**
   * Busca todos los objetos de tipo {@link ProyectoFacturacion} cuyo proyectoId
   * sea igual al que se recibe por parámetro
   * 
   * @param proyectoId id del {@link Proyecto} asociado
   * @param paging     Información de paginación
   * @return pagina de {@link ProyectoFacturacion}
   */
  public Page<ProyectoFacturacion> findByProyectoId(Long proyectoId, Pageable paging) {
    authorityHelper.checkCanAccessProyecto(proyectoId);
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

    authorityHelper.checkUserHasAuthorityModifyProyecto(toCreate.getProyectoId());

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

    authorityHelper.checkUserHasAuthorityModifyProyecto(toUpdate.getProyectoId());

    ProyectoFacturacion beforeUpdate = this.proyectoFacturacionRepository.findById(toUpdate.getId())
        .orElseThrow(() -> new ProyectoFacturacionNotFoundException(toUpdate.getId()));

    beforeUpdate.setComentario(toUpdate.getComentario());
    beforeUpdate.setImporteBase(toUpdate.getImporteBase());
    beforeUpdate.setPorcentajeIVA(toUpdate.getPorcentajeIVA());
    beforeUpdate.setTipoFacturacion(toUpdate.getTipoFacturacion());
    beforeUpdate.setFechaEmision(toUpdate.getFechaEmision());
    beforeUpdate.setFechaConformidad(toUpdate.getFechaConformidad());
    beforeUpdate.setProyectoProrrogaId(toUpdate.getProyectoProrrogaId());
    beforeUpdate.setProyectoSgeRef(toUpdate.getProyectoSgeRef());

    if (toUpdate.getEstadoValidacionIP().getEstado() != beforeUpdate.getEstadoValidacionIP().getEstado()) {
      beforeUpdate.setEstadoValidacionIP(persistEstadoValidacionIP(toUpdate.getEstadoValidacionIP(), toUpdate.getId()));

      if (toUpdate.getEstadoValidacionIP().getEstado().equals(TipoEstadoValidacion.VALIDADA)) {
        Instant fechaActual = Instant.now().atZone(sgiConfigProperties.getTimeZone().toZoneId()).toInstant();
        beforeUpdate.setFechaConformidad(fechaActual);
      }

      if (this.checkIfCanSendComunicado(beforeUpdate)) {
        try {
          beforeUpdate.setTipoFacturacion(beforeUpdate.getTipoFacturacion() == null ? null
              : this.tipoFacturacionRepository.findById(beforeUpdate.getTipoFacturacion().getId()).orElse(null));
          this.proyectoFacturacionComService.enviarComunicado(beforeUpdate);
        } catch (Exception e) {
          log.error(e.getMessage(), e);
        }
      }
    }

    return this.proyectoFacturacionRepository.save(beforeUpdate);
  }

  /**
   * Actualiza un objeto de tipo {@link ProyectoFacturacion} con la informacion de
   * validacion
   * 
   * @param toUpdate objeto de tipo {@link ProyectoFacturacion} con la información
   *                 a actualizar
   * @return objeto de tipo {@link ProyectoFacturacion}
   */
  @Transactional
  public ProyectoFacturacion updateValidacionIP(ProyectoFacturacion toUpdate) {

    authorityHelper.checkUserHasAuthorityValidateIPProyectoFacturacion(toUpdate.getProyectoId());

    ProyectoFacturacion beforeUpdate = this.proyectoFacturacionRepository.findById(toUpdate.getId())
        .orElseThrow(() -> new ProyectoFacturacionNotFoundException(toUpdate.getId()));

    if (toUpdate.getEstadoValidacionIP().getEstado() != beforeUpdate.getEstadoValidacionIP().getEstado()) {
      beforeUpdate.setEstadoValidacionIP(persistEstadoValidacionIP(toUpdate.getEstadoValidacionIP(), toUpdate.getId()));

      if (toUpdate.getEstadoValidacionIP().getEstado().equals(TipoEstadoValidacion.VALIDADA)) {
        Instant fechaActual = Instant.now().atZone(sgiConfigProperties.getTimeZone().toZoneId()).toInstant();
        beforeUpdate.setFechaConformidad(fechaActual);
      }

      if (this.checkIfCanSendComunicado(beforeUpdate)) {
        try {
          this.proyectoFacturacionComService.enviarComunicado(beforeUpdate);
        } catch (Exception e) {
          log.error(e.getMessage(), e);
        }
      }
    }

    return this.proyectoFacturacionRepository.save(beforeUpdate);
  }

  private boolean checkIfCanSendComunicado(ProyectoFacturacion proyectoFacturacion) {
    boolean res = false;
    switch (proyectoFacturacion.getEstadoValidacionIP().getEstado()) {
      case RECHAZADA:
      case VALIDADA:
        res = !((!TipoEstadoValidacion.VALIDADA.equals(proyectoFacturacion.getEstadoValidacionIP().getEstado())
            && !TipoEstadoValidacion.RECHAZADA.equals(proyectoFacturacion.getEstadoValidacionIP().getEstado()))
            || ((!authorityHelper.checkIfUserIsInvestigadorPrincipal(proyectoFacturacion.getProyectoId()))
                && !authorityHelper.checkUserIsResponsableEconomico(proyectoFacturacion.getProyectoId())));
        break;
      case NOTIFICADA:
        res = true;
        break;
      default:
        break;
    }
    return res;
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

    authorityHelper.checkUserHasAuthorityModifyProyecto(proyectoToDelete.getProyectoId());

    proyectoToDelete.setEstadoValidacionIP(null);

    this.proyectoFacturacionRepository.save(proyectoToDelete);

    this.estadoValidacionIPRepository.deleteByProyectoFacturacionId(toDeleteId);

    this.proyectoFacturacionRepository.deleteById(toDeleteId);
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
