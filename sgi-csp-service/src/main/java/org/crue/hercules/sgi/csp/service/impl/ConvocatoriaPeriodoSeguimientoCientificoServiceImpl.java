package org.crue.hercules.sgi.csp.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.enums.TipoSeguimiento;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaPeriodoSeguimientoCientificoNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoSeguimientoCientifico;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaPeriodoSeguimientoCientificoRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.specification.ConvocatoriaPeriodoSeguimientoCientificoSpecifications;
import org.crue.hercules.sgi.csp.service.ConvocatoriaPeriodoSeguimientoCientificoService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion
 * {@link ConvocatoriaPeriodoSeguimientoCientifico}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ConvocatoriaPeriodoSeguimientoCientificoServiceImpl
    implements ConvocatoriaPeriodoSeguimientoCientificoService {

  private final ConvocatoriaPeriodoSeguimientoCientificoRepository repository;
  private final ConvocatoriaRepository convocatoriaRepository;

  public ConvocatoriaPeriodoSeguimientoCientificoServiceImpl(
      ConvocatoriaPeriodoSeguimientoCientificoRepository repository, ConvocatoriaRepository convocatoriaRepository) {
    this.repository = repository;
    this.convocatoriaRepository = convocatoriaRepository;
  }

  /**
   * Actualiza el listado de {@link ConvocatoriaPeriodoSeguimientoCientifico} de
   * la {@link Convocatoria} con el listado
   * convocatoriaPeriodoSeguimientoCientificos añadiendo, editando o eliminando
   * los elementos segun proceda.
   *
   * @param convocatoriaId                            Id de la
   *                                                  {@link Convocatoria}.
   * @param convocatoriaPeriodoSeguimientoCientificos lista con los nuevos
   *                                                  {@link ConvocatoriaPeriodoSeguimientoCientifico}
   *                                                  a guardar.
   * @return la entidad {@link ConvocatoriaPeriodoSeguimientoCientifico}
   *         persistida.
   */
  @Override
  @Transactional
  public List<ConvocatoriaPeriodoSeguimientoCientifico> updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria(
      Long convocatoriaId, List<ConvocatoriaPeriodoSeguimientoCientifico> convocatoriaPeriodoSeguimientoCientificos) {
    log.debug(
        "updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria(Long convocatoriaId, List<ConvocatoriaPeriodoSeguimientoCientifico> convocatoriaPeriodoSeguimientoCientificos) - start");

    if (convocatoriaPeriodoSeguimientoCientificos.isEmpty()) {
      return new ArrayList<>();
    }

    Convocatoria convocatoria = convocatoriaRepository.findById(convocatoriaId)
        .orElseThrow(() -> new ConvocatoriaNotFoundException(convocatoriaId));

    List<ConvocatoriaPeriodoSeguimientoCientifico> convocatoriaPeriodoSeguimientoCientificoesBD = repository
        .findAllByConvocatoriaIdOrderByMesInicial(convocatoriaId);

    // Periodos eliminados
    List<ConvocatoriaPeriodoSeguimientoCientifico> periodoSeguimientoCientificoesEliminar = convocatoriaPeriodoSeguimientoCientificoesBD
        .stream()
        .filter(periodo -> !convocatoriaPeriodoSeguimientoCientificos.stream()
            .map(ConvocatoriaPeriodoSeguimientoCientifico::getId).anyMatch(id -> id == periodo.getId()))
        .collect(Collectors.toList());

    if (!periodoSeguimientoCientificoesEliminar.isEmpty()) {
      repository.deleteAll(periodoSeguimientoCientificoesEliminar);
    }

    // Ordena los periodos por mesInicial
    convocatoriaPeriodoSeguimientoCientificos
        .sort(Comparator.comparing(ConvocatoriaPeriodoSeguimientoCientifico::getMesInicial));

    AtomicInteger numPeriodo = new AtomicInteger(0);

    ConvocatoriaPeriodoSeguimientoCientifico periodoSeguimientoCientificoAnterior = null;
    for (int i = 0; i < convocatoriaPeriodoSeguimientoCientificos.size(); i++) {
      ConvocatoriaPeriodoSeguimientoCientifico periodoSeguimientoCientifico = (ConvocatoriaPeriodoSeguimientoCientifico) convocatoriaPeriodoSeguimientoCientificos
          .get(i);
      // Actualiza el numero de periodo
      periodoSeguimientoCientifico.setNumPeriodo(numPeriodo.incrementAndGet());

      // Si tiene id se valida que exista y que tenga la convocatoria de la que se
      // estan actualizando los periodos
      if (periodoSeguimientoCientifico.getId() != null) {
        ConvocatoriaPeriodoSeguimientoCientifico periodoSeguimientoCientificoBD = convocatoriaPeriodoSeguimientoCientificoesBD
            .stream().filter(periodo -> periodo.getId() == periodoSeguimientoCientifico.getId()).findFirst()
            .orElseThrow(() -> new ConvocatoriaPeriodoSeguimientoCientificoNotFoundException(
                periodoSeguimientoCientifico.getId()));

        Assert.isTrue(
            periodoSeguimientoCientificoBD.getConvocatoriaId() == periodoSeguimientoCientifico.getConvocatoriaId(),
            "No se puede modificar la convocatoria del ConvocatoriaPeriodoSeguimientoCientifico");
      }

      // Setea la convocatoria recuperada del convocatoriaId
      periodoSeguimientoCientifico.setConvocatoriaId(convocatoria.getId());

      // Validaciones
      Assert.isTrue(periodoSeguimientoCientifico.getMesInicial() < periodoSeguimientoCientifico.getMesFinal(),
          "El mes final tiene que ser posterior al mes inicial");

      if (periodoSeguimientoCientifico.getFechaInicioPresentacion() != null
          && periodoSeguimientoCientifico.getFechaFinPresentacion() != null) {
        Assert.isTrue(
            periodoSeguimientoCientifico.getFechaInicioPresentacion()
                .isBefore(periodoSeguimientoCientifico.getFechaFinPresentacion()),
            "La fecha de fin tiene que ser posterior a la fecha de inicio");
      }

      Assert.isTrue(
          convocatoria.getDuracion() == null
              || periodoSeguimientoCientifico.getMesFinal() <= convocatoria.getDuracion(),
          "El mes final no puede ser superior a la duración en meses indicada en la Convocatoria");

      Assert.isTrue(
          periodoSeguimientoCientificoAnterior == null || (periodoSeguimientoCientificoAnterior != null
              && periodoSeguimientoCientificoAnterior.getMesFinal() < periodoSeguimientoCientifico.getMesInicial()),
          "El periodo se solapa con otro existente");

      // Tipo seguimiento not null
      Assert.notNull(periodoSeguimientoCientifico.getTipoSeguimiento(),
          "El tipo de seguimiento científico no puede ser null");

      // Solo puede haber un tipo de seguimiento 'final' y ha de ser el último"
      if (periodoSeguimientoCientifico.getTipoSeguimiento().equals(TipoSeguimiento.FINAL)
          && i != convocatoriaPeriodoSeguimientoCientificos.size() - 1) {
        throw new IllegalArgumentException(
            "Solo puede existir un periodo de seguimiento de tipo 'final' y este debe ser el último periodo");
      }

      periodoSeguimientoCientificoAnterior = periodoSeguimientoCientifico;
    }

    List<ConvocatoriaPeriodoSeguimientoCientifico> returnValue = repository
        .saveAll(convocatoriaPeriodoSeguimientoCientificos);
    log.debug(
        "updateConvocatoriaPeriodoSeguimientoCientificosConvocatoria(Long convocatoriaId, List<ConvocatoriaPeriodoSeguimientoCientifico> convocatoriaPeriodoSeguimientoCientificos) - end");

    return returnValue;
  }

  /**
   * Obtiene una entidad {@link ConvocatoriaPeriodoSeguimientoCientifico} por id.
   * 
   * @param id Identificador de la entidad
   *           {@link ConvocatoriaPeriodoSeguimientoCientifico}.
   * @return ConvocatoriaPeriodoSeguimientoCientifico la entidad
   *         {@link ConvocatoriaPeriodoSeguimientoCientifico}.
   */
  @Override
  public ConvocatoriaPeriodoSeguimientoCientifico findById(Long id) {
    log.debug("findById(Long id) - start");
    final ConvocatoriaPeriodoSeguimientoCientifico returnValue = repository.findById(id)
        .orElseThrow(() -> new ConvocatoriaPeriodoSeguimientoCientificoNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link ConvocatoriaPeriodoSeguimientoCientifico} para una
   * {@link Convocatoria}.
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param query          la información del filtro.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades
   *         {@link ConvocatoriaPeriodoSeguimientoCientifico} de la
   *         {@link Convocatoria} paginadas.
   */
  public Page<ConvocatoriaPeriodoSeguimientoCientifico> findAllByConvocatoria(Long convocatoriaId, String query,
      Pageable pageable) {
    log.debug("findAllByConvocatoria(Long convocatoriaId, String query, Pageable pageable) - start");
    Specification<ConvocatoriaPeriodoSeguimientoCientifico> specs = ConvocatoriaPeriodoSeguimientoCientificoSpecifications
        .byConvocatoriaId(convocatoriaId).and(SgiRSQLJPASupport.toSpecification(query));

    Page<ConvocatoriaPeriodoSeguimientoCientifico> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByConvocatoria(Long convocatoriaId, String query, Pageable pageable) - end");
    return returnValue;
  }

}
