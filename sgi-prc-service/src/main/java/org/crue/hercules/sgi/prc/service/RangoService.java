package org.crue.hercules.sgi.prc.service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.crue.hercules.sgi.prc.exceptions.ConvocatoriaBaremacionNotFoundException;
import org.crue.hercules.sgi.prc.exceptions.RangoGapBetweenRangesRangeException;
import org.crue.hercules.sgi.prc.exceptions.RangoMultiplesNullDesdeException;
import org.crue.hercules.sgi.prc.exceptions.RangoMultiplesNullHastaException;
import org.crue.hercules.sgi.prc.exceptions.RangoMultiplesTemporalidadFinalException;
import org.crue.hercules.sgi.prc.exceptions.RangoMultiplesTemporalidadInicialException;
import org.crue.hercules.sgi.prc.exceptions.RangoNotFoundException;
import org.crue.hercules.sgi.prc.exceptions.RangoOverlapRangeException;
import org.crue.hercules.sgi.prc.exceptions.RangoPuntosNullOrLessThanZeroException;
import org.crue.hercules.sgi.prc.model.BaseEntity;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;
import org.crue.hercules.sgi.prc.model.Rango;
import org.crue.hercules.sgi.prc.model.Rango.TipoRango;
import org.crue.hercules.sgi.prc.model.Rango.TipoTemporalidad;
import org.crue.hercules.sgi.prc.repository.ConvocatoriaBaremacionRepository;
import org.crue.hercules.sgi.prc.repository.RangoRepository;
import org.crue.hercules.sgi.prc.repository.specification.RangoSpecifications;
import org.crue.hercules.sgi.prc.util.AssertHelper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service para la gestión de {@link Rango}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
public class RangoService {

  private final RangoRepository repository;
  private final ConvocatoriaBaremacionRepository convocatoriaBaremacionRepository;

  /**
   * Obtiene una entidad {@link Rango} por id.
   * 
   * @param id Identificador de la entidad {@link Rango}.
   * @return la entidad {@link Rango}.
   */
  public Rango findById(Long id) {
    log.debug("findById({}) - start", id);

    AssertHelper.idNotNull(id, Rango.class);
    final Rango returnValue = repository.findById(id).orElseThrow(() -> new RangoNotFoundException(id));

    log.debug("findById({}) - end", id);
    return returnValue;
  }

  /**
   * Actualiza el listado de {@link Rango} de la {@link ConvocatoriaBaremacion} y
   * del tipo de rango con el listado rangos añadiendo, editando o eliminando los
   * elementos segun proceda.
   *
   * @param convocatoriaBaremacionId Id de la {@link ConvocatoriaBaremacion}.
   * @param tipoRango                {@link TipoRango}.
   * @param rangos                   lista con los nuevos
   *                                 {@link Rango} a
   *                                 guardar.
   * @return la entidad {@link Rango} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public List<Rango> update(Long convocatoriaBaremacionId, TipoRango tipoRango, @Valid @NotEmpty List<Rango> rangos) {
    log.debug("update(Long convocatoriaBaremacionId, TipoRango tipoRango, List<Rango> rangos) - start");

    List<Rango> rangosBD = findByConvocatoriaBaremacionIdAndTipoRango(convocatoriaBaremacionId, tipoRango);

    List<Rango> rangosEliminar = rangosBD.stream()
        .filter(rango -> rangos.stream().map(Rango::getId)
            .noneMatch(id -> Objects.equals(id, rango.getId())))
        .collect(Collectors.toList());

    if (!rangosEliminar.isEmpty()) {
      repository.deleteAll(rangosEliminar);
    }

    this.validateRangos(rangos);

    List<Rango> returnValue = repository.saveAll(rangos);
    log.debug("update(Long convocatoriaBaremacionId, TipoRango tipoRango, List<Rango> rangos) - end");

    return returnValue;
  }

  public List<Rango> findByConvocatoriaBaremacionIdAndTipoRango(Long convocatoriaBaremacionId, TipoRango tipoRango) {
    log.debug("findByConvocatoriaBaremacionIdAndTipoRango({},{}) - start", convocatoriaBaremacionId, tipoRango);

    if (!convocatoriaBaremacionRepository.existsById(convocatoriaBaremacionId)) {
      throw new ConvocatoriaBaremacionNotFoundException(convocatoriaBaremacionId);
    }

    Specification<Rango> specs = RangoSpecifications.byConvocatoriaBaremacionId(convocatoriaBaremacionId)
        .and(RangoSpecifications.byTipoRango(tipoRango));

    log.debug("findByConvocatoriaBaremacionIdAndTipoRango({},{}) - end", convocatoriaBaremacionId, tipoRango);

    return repository.findAll(specs);
  }

  public void validateRangos(@Valid @NotEmpty List<Rango> rangos) {
    log.debug("validateRangos(rangos) - start");

    // Ordena los responsables por campo desde
    rangos.sort(Comparator.comparing(Rango::getDesde, Comparator.nullsFirst(Comparator.naturalOrder())));

    boolean hasMultiplesNullDesde = rangos.stream().filter(rango -> rango.getDesde() == null).count() > 1L;
    if (hasMultiplesNullDesde) {
      throw new RangoMultiplesNullDesdeException();
    }

    boolean hasMultiplesNullHasta = rangos.stream().filter(rango -> rango.getHasta() == null).count() > 1L;
    if (hasMultiplesNullHasta) {
      throw new RangoMultiplesNullHastaException();
    }

    boolean hasPuntosNullOrLessThanZero = rangos.stream().filter(rango -> rango.getPuntos() == null || rango
        .getPuntos().compareTo(new BigDecimal(0)) <= 0).count() > 0L;
    if (hasPuntosNullOrLessThanZero) {
      throw new RangoPuntosNullOrLessThanZeroException();
    }

    boolean hasMultiplesTemporalidadInicial = rangos.stream()
        .filter(rango -> rango.getTipoTemporalidad().equals(TipoTemporalidad.INICIAL)).count() > 1L;

    if (hasMultiplesTemporalidadInicial) {
      throw new RangoMultiplesTemporalidadInicialException();
    }

    boolean hasMultiplesTemporalidadFinal = rangos.stream()
        .filter(rango -> rango.getTipoTemporalidad().equals(TipoTemporalidad.FINAL)).count() > 1L;
    if (hasMultiplesTemporalidadFinal) {
      throw new RangoMultiplesTemporalidadFinalException();
    }

    boolean hasOverlappedRanges = rangos.stream()
        .filter(rango -> null != rango.getHasta() && null != rango.getDesde()
            && rango.getDesde().compareTo(rango.getHasta()) >= 0)
        .count() > 0L;
    if (hasOverlappedRanges) {
      throw new RangoOverlapRangeException();
    }

    boolean hasGapBetweenRanges = IntStream.range(0, rangos.size() - 1)
        .filter(i -> null != rangos.get(i).getHasta()
            && null != rangos.get(i + 1).getDesde()
            && rangos.get(i).getHasta().add(new BigDecimal(1)).compareTo(rangos.get(i + 1).getDesde()) != 0)
        .count() > 0L;
    if (hasGapBetweenRanges) {
      throw new RangoGapBetweenRangesRangeException();
    }
    log.debug("validateRangos(rangos) - end");
  }

}
