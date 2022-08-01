package org.crue.hercules.sgi.csp.service.impl;

import java.util.Optional;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoSocioNotFoundException;
import org.crue.hercules.sgi.csp.model.EstadoProyecto;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioEquipo;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacionDocumento;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoPago;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoSocioEquipoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoSocioPeriodoJustificacionDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoSocioPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoSocioPeriodoPagoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoSocioRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoSocioSpecifications;
import org.crue.hercules.sgi.csp.service.ProyectoSocioService;
import org.crue.hercules.sgi.csp.util.ProyectoHelper;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio implementación para la gestión de {@link ProyectoSocio}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ProyectoSocioServiceImpl implements ProyectoSocioService {

  private final ProyectoSocioRepository repository;
  private final ProyectoSocioEquipoRepository equipoRepository;
  private final ProyectoSocioPeriodoPagoRepository periodoPagoRepository;
  private final ProyectoSocioPeriodoJustificacionDocumentoRepository documentoRepository;
  private final ProyectoSocioPeriodoJustificacionRepository periodoJustificacionRepository;
  private final ProyectoRepository proyectoRepository;
  private final ProyectoHelper proyectoHelper;

  public ProyectoSocioServiceImpl(ProyectoSocioRepository repository, ProyectoSocioEquipoRepository equipoRepository,
      ProyectoSocioPeriodoPagoRepository periodoPagoRepository,
      ProyectoSocioPeriodoJustificacionDocumentoRepository documentoRepository,
      ProyectoSocioPeriodoJustificacionRepository periodoJustificacionRepository,
      ProyectoRepository proyectoRepository,
      ProyectoHelper proyectoHelper) {
    this.repository = repository;
    this.equipoRepository = equipoRepository;
    this.periodoPagoRepository = periodoPagoRepository;
    this.documentoRepository = documentoRepository;
    this.periodoJustificacionRepository = periodoJustificacionRepository;
    this.proyectoRepository = proyectoRepository;
    this.proyectoHelper = proyectoHelper;
  }

  /**
   * Guarda la entidad {@link ProyectoSocio}.
   * 
   * @param proyectoSocio la entidad {@link ProyectoSocioService} a guardar.
   * @return la entidad {@link ProyectoSocio} persistida.
   */
  @Override
  @Transactional
  public ProyectoSocio create(ProyectoSocio proyectoSocio) {
    log.debug("create(ProyectoSocio proyectoSocio) - start");

    Assert.isNull(proyectoSocio.getId(), "Id tiene que ser null para crear el ProyectoSocio");
    Assert.isTrue(!isRangoFechasSolapado(proyectoSocio), "El rango de fechas del socio se solapa");

    ProyectoSocio returnValue = repository.save(proyectoSocio);

    log.debug("create(ProyectoSocio proyectoSocio) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link ProyectoSocio}.
   * 
   * @param proyectoSocio {@link ProyectoSocio} con los datos actualizados.
   * @return {@link ProyectoSocio} actualizado.
   */
  @Override
  @Transactional
  public ProyectoSocio update(ProyectoSocio proyectoSocio) {
    log.debug("update(ProyectoSocio proyectoSocio) - start");

    Assert.notNull(proyectoSocio.getId(), "Id no puede ser null para actualizar ProyectoSocio");

    return repository.findById(proyectoSocio.getId()).map(proyectoSocioExistente -> {

      // Validaciones
      Assert.isTrue(!isRangoFechasSolapado(proyectoSocio), "El rango de fechas del socio se solapa");

      if (!proyectoSocio.getRolSocio().getCoordinador().booleanValue()
          && proyectoSocioExistente.getRolSocio().getCoordinador().booleanValue()) {

        Proyecto proyecto = proyectoRepository.findById(proyectoSocioExistente.getProyectoId())
            .orElseThrow(() -> new ProyectoNotFoundException(proyectoSocioExistente.getProyectoId()));
        if (proyecto.getEstado().getEstado().equals(EstadoProyecto.Estado.CONCEDIDO)
            && proyecto.getColaborativo().booleanValue()
            && proyecto.getCoordinadorExterno().booleanValue()) {

          Assert.isTrue(existsProyectoSocioCoordinador(proyectoSocioExistente.getProyectoId()),
              "Debe existir al menos un socio con TipoRolSocio que tenga el campo coordinador a true");
        }

      }

      proyectoSocioExistente.setRolSocio(proyectoSocio.getRolSocio());
      proyectoSocioExistente.setFechaInicio(proyectoSocio.getFechaInicio());
      proyectoSocioExistente.setFechaFin(proyectoSocio.getFechaFin());
      proyectoSocioExistente.setNumInvestigadores(proyectoSocio.getNumInvestigadores());
      proyectoSocioExistente.setImporteConcedido(proyectoSocio.getImporteConcedido());
      proyectoSocioExistente.setImportePresupuesto(proyectoSocio.getImportePresupuesto());

      ProyectoSocio returnValue = repository.save(proyectoSocioExistente);
      log.debug("update(ProyectoSocio proyectoSocio) - end");
      return returnValue;
    }).orElseThrow(() -> new ProyectoSocioNotFoundException(proyectoSocio.getId()));

  }

  /**
   * Elimina el {@link ProyectoSocio}.
   *
   * @param id Id del {@link ProyectoSocio}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id, "ProyectoSocio id no puede ser null para desactivar un ProyectoSocio");

    Optional<ProyectoSocio> socio = repository.findById(id);
    if (socio.isPresent()) {
      // Validaciones
      if (socio.get().getRolSocio().getCoordinador().booleanValue()) {

        Proyecto proyecto = proyectoRepository.findById(socio.get().getProyectoId())
            .orElseThrow(() -> new ProyectoNotFoundException(socio.get().getProyectoId()));
        if (proyecto.getEstado().getEstado().equals(EstadoProyecto.Estado.CONCEDIDO)
            && proyecto.getColaborativo().booleanValue()
            && proyecto.getCoordinadorExterno().booleanValue()) {

          Assert.isTrue(existsProyectoSocioCoordinador(socio.get().getProyectoId()),
              "Debe existir al menos un socio con TipoRolSocio que tenga el campo coordinador a true");
        }

      }
    } else {
      throw new ProyectoSocioNotFoundException(id);
    }

    equipoRepository.deleteByProyectoSocioId(id);
    periodoPagoRepository.deleteByProyectoSocioId(id);
    documentoRepository.deleteByProyectoSocioPeriodoJustificacionProyectoSocioId(id);
    periodoJustificacionRepository.deleteByProyectoSocioId(id);
    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Comprueba la existencia del {@link ProyectoSocio} por id.
   *
   * @param id el id de la entidad {@link ProyectoSocio}.
   * @return true si existe y false en caso contrario.
   */
  @Override
  public boolean existsById(final Long id) {
    log.debug("existsById(final Long id)  - start", id);
    final boolean existe = repository.existsById(id);
    log.debug("existsById(final Long id)  - end", id);
    return existe;
  }

  /**
   * Obtiene una entidad {@link ProyectoSocio} por id.
   * 
   * @param id Identificador de la entidad {@link ProyectoSocio}.
   * @return la entidad {@link ProyectoSocio}.
   */
  @Override
  public ProyectoSocio findById(Long id) {
    log.debug("findById(Long id) - start");
    final ProyectoSocio returnValue = repository.findById(id).orElseThrow(() -> new ProyectoSocioNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link ProyectoSocio} activas paginadas y
   * filtradas.
   *
   * @param proyectoId el id de la {@link Proyecto}.
   * @param query      información del filtro.
   * @param paging     información de paginación.
   * @return el listado de entidades {@link Proyecto} activas paginadas y
   *         filtradas.
   */
  @Override
  public Page<ProyectoSocio> findAllByProyecto(Long proyectoId, String query, Pageable paging) {
    log.debug("findAllByProyecto(Long proyectoId, String query, Pageable paging) - start");
    Specification<ProyectoSocio> specs = ProyectoSocioSpecifications.byProyectoId(proyectoId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<ProyectoSocio> returnValue = repository.findAll(specs, paging);
    log.debug("findAllByProyecto(Long proyectoId, String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Comprueba si existe algun {@link ProyectoSocio} que tenga un rol con el flag
   * coordinador a true para el proyecto.
   * 
   * @param proyectoId Identificador del {@link Proyecto}.
   * @return true si el proyecto tiene algun socio coordinador o false en caso
   *         contrario.
   */
  @Override
  public boolean existsProyectoSocioCoordinador(Long proyectoId) {
    log.debug("existsProyectoSocioCoordinador(Long proyectoId) - start");

    Specification<ProyectoSocio> specByProyecto = ProyectoSocioSpecifications.byProyectoId(proyectoId);
    Specification<ProyectoSocio> specCoordinadores = ProyectoSocioSpecifications.sociosCoordinadores();

    Specification<ProyectoSocio> specs = Specification.where(specByProyecto).and(specCoordinadores);
    boolean returnValue = repository.count(specs) > 0;
    log.debug("existsProyectoSocioCoordinador(Long proyectoId) - end");
    return returnValue;
  }

  /**
   * Comprueba si el rango de fechas del socio se solapa con alguno de los rangos
   * de ese mismo socio en el proyecto.
   * 
   * @param proyectoSocio un {@link ProyectoSocio}.
   * @return true si se solapa o false si no hay solapamiento.
   */
  private boolean isRangoFechasSolapado(ProyectoSocio proyectoSocio) {
    log.debug("isRangoFechasSolapado(ProyectoSocio proyectoSocio) - start");

    Specification<ProyectoSocio> specByIdNotEqual = ProyectoSocioSpecifications.byIdNotEqual(proyectoSocio.getId());
    Specification<ProyectoSocio> specByProyecto = ProyectoSocioSpecifications
        .byProyectoId(proyectoSocio.getProyectoId());
    Specification<ProyectoSocio> specByEmpresaRef = ProyectoSocioSpecifications
        .byEmpresaRef(proyectoSocio.getEmpresaRef());
    Specification<ProyectoSocio> specByRangoFechaSolapados = ProyectoSocioSpecifications
        .byRangoFechaSolapados(proyectoSocio.getFechaInicio(), proyectoSocio.getFechaFin());

    Specification<ProyectoSocio> specs = Specification.where(specByProyecto).and(specByEmpresaRef)
        .and(specByRangoFechaSolapados).and(specByIdNotEqual);
    boolean returnValue = repository.count(specs) > 0;
    log.debug("isRangoFechasSolapado(ProyectoSocio proyectoSocio) - end");
    return returnValue;
  }

  /**
   * Indica si {@link ProyectoSocio} tiene {@link ProyectoSocioEquipo},
   * {@link ProyectoSocioPeriodoPago},
   * {@link ProyectoSocioPeriodoJustificacionDocumento} y/o
   * {@link ProyectoSocioPeriodoJustificacion} relacionadas.
   *
   * @param id Id de la {@link Proyecto}.
   * @return True si tiene {@link ProyectoSocioEquipo},
   *         {@link ProyectoSocioPeriodoPago},
   *         {@link ProyectoSocioPeriodoJustificacionDocumento} y/o
   *         {@link ProyectoSocioPeriodoJustificacion} relacionadas. En caso
   *         contrario false
   */
  public Boolean vinculaciones(Long id) {
    log.debug("vinculaciones(Long id) - start");
    final Boolean returnValue = repository.vinculaciones(id);
    log.debug("vinculaciones(Long id) - start");
    return returnValue;
  }

  @Override
  public boolean hasAnyProyectoSocioWithRolCoordinador(Long proyectoId) {
    proyectoHelper.checkCanAccessProyecto(proyectoId);
    return repository.existsByProyectoIdAndRolSocioCoordinador(proyectoId, true);
  }

  @Override
  public boolean hasAnyProyectoSocioWithProyectoId(Long proyectoId) {
    proyectoHelper.checkCanAccessProyecto(proyectoId);
    return repository.existsByProyectoId(proyectoId);
  }

  @Override
  public boolean existsProyectoSocioPeriodoPagoByProyectoSocioId(Long proyectoId) {
    proyectoHelper.checkCanAccessProyecto(proyectoId);
    return !this.repository.findByProyectoId(proyectoId).stream()
        .filter(proyectoSocio -> this.periodoPagoRepository.existsByProyectoSocioId(proyectoSocio.getId()))
        .collect(Collectors.toList()).isEmpty();
  }

  @Override
  public boolean existsProyectoSocioPeriodoJustificacionByProyectoSocioId(Long proyectoId) {
    proyectoHelper.checkCanAccessProyecto(proyectoId);
    return !this.repository.findByProyectoId(proyectoId).stream()
        .filter(proyectoSocio -> this.periodoJustificacionRepository.existsByProyectoSocioId(proyectoSocio.getId()))
        .collect(Collectors.toList()).isEmpty();
  }

}
