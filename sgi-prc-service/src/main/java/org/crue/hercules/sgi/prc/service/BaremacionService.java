package org.crue.hercules.sgi.prc.service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.stream.IntStream;

import org.crue.hercules.sgi.prc.config.SgiConfigProperties;
import org.crue.hercules.sgi.prc.dto.BaremacionInput;
import org.crue.hercules.sgi.prc.exceptions.ConvocatoriaBaremacionNotFoundException;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica.EpigrafeCVN;
import org.crue.hercules.sgi.prc.repository.ConvocatoriaBaremacionRepository;
import org.crue.hercules.sgi.prc.repository.ProduccionCientificaRepository;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para la baremación
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
public class BaremacionService {

  private final ProduccionCientificaRepository produccionCientificaRepository;
  private final ConvocatoriaBaremacionRepository convocatoriaBaremacionRepository;
  private final BaremacionPublicacionService baremacionPublicacionesService;
  private final BaremacionComiteEditorialService baremacionComitesEditorialesService;
  private final BaremacionCongresoService baremacionCongresosService;
  private final BaremacionDireccionTesisService baremacionDireccionTesisService;
  private final BaremacionObraArtisticaService baremacionObraArtisticaService;
  private final BaremacionOrganizacionActividadService baremacionOrganizacionActividadService;

  private final SgiConfigProperties sgiConfigProperties;
  private final ProduccionCientificaCloneService produccionCientificaCloneService;

  @Transactional
  public void baremacion(Long convocatoriaBaremacionId) {
    log.debug("baremacion(convocatoriaBaremacionId) - start");

    ConvocatoriaBaremacion convocatoriaBaremacion = convocatoriaBaremacionRepository.findById(convocatoriaBaremacionId)
        .orElseThrow(() -> new ConvocatoriaBaremacionNotFoundException(convocatoriaBaremacionId));

    produccionCientificaRepository.findByConvocatoriaBaremacionId(convocatoriaBaremacionId)
        .forEach(produccionCientificaCloneService::deleteProduccionCientifica);

    IntStream.range(convocatoriaBaremacion.getUltimoAnio() - convocatoriaBaremacion.getAniosBaremables() + 1,
        convocatoriaBaremacion.getUltimoAnio() + 1).forEach(anio -> {
          BaremacionInput baremacionInput = createBaremacionInput(anio, convocatoriaBaremacionId);

          // Publicaciones
          baremacionInput.setEpigrafeCVN(EpigrafeCVN.E060_010_010_000);
          baremacionPublicacionesService.evaluateProduccionCientificaByTypeAndAnio(baremacionInput);

          // Cómites editoriales
          baremacionInput.setEpigrafeCVN(EpigrafeCVN.E060_030_030_000);
          baremacionComitesEditorialesService.evaluateProduccionCientificaByTypeAndAnio(baremacionInput);

          // Congresos
          baremacionInput.setEpigrafeCVN(EpigrafeCVN.E060_010_020_000);
          baremacionCongresosService.evaluateProduccionCientificaByTypeAndAnio(baremacionInput);

          // Direccion tesis
          baremacionInput.setEpigrafeCVN(EpigrafeCVN.E030_040_000_000);
          baremacionDireccionTesisService.evaluateProduccionCientificaByTypeAndAnio(baremacionInput);

          // Obra artística
          baremacionInput.setEpigrafeCVN(EpigrafeCVN.E050_020_030_000);
          baremacionObraArtisticaService.evaluateProduccionCientificaByTypeAndAnio(baremacionInput);

          // Organización de actividades
          baremacionInput.setEpigrafeCVN(EpigrafeCVN.E060_020_030_000);
          baremacionOrganizacionActividadService.evaluateProduccionCientificaByTypeAndAnio(baremacionInput);

          // TODO evaluar todas las demás
        });

    log.debug("baremacion(convocatoriaBaremacionId) - end");
  }

  private BaremacionInput createBaremacionInput(int anio, Long convocatoriaBaremacionId) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        .withZone(sgiConfigProperties.getTimeZone().toZoneId()).withLocale(LocaleContextHolder.getLocale());

    Instant fechaInicio = Instant.now().atZone(sgiConfigProperties.getTimeZone().toZoneId())
        .withYear(anio).withMonth(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).toInstant();

    Instant fechaFin = Instant.now().atZone(sgiConfigProperties.getTimeZone().toZoneId())
        .withYear(anio).withMonth(12).withDayOfMonth(31).withHour(23).withMinute(59).withSecond(59).toInstant();
    return BaremacionInput.builder()
        .anio(anio)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .fechaInicio(formatter.format(fechaInicio))
        .fechaFin(formatter.format(fechaFin))
        .build();
  }

}
