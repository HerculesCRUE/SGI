package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.crue.hercules.sgi.csp.config.SgiConfigProperties;
import org.crue.hercules.sgi.csp.dto.com.CspComCalendarioFacturacionNotificarData;
import org.crue.hercules.sgi.csp.dto.com.CspComCalendarioFacturacionValidarIPData;
import org.crue.hercules.sgi.csp.dto.com.EmailOutput;
import org.crue.hercules.sgi.csp.dto.com.Recipient;
import org.crue.hercules.sgi.csp.dto.sgp.PersonaOutput;
import org.crue.hercules.sgi.csp.dto.sgp.PersonaOutput.Email;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.model.EstadoValidacionIP.TipoEstadoValidacion;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.model.ProyectoFacturacion;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge;
import org.crue.hercules.sgi.csp.model.ProyectoResponsableEconomico;
import org.crue.hercules.sgi.csp.model.TipoFacturacion;
import org.crue.hercules.sgi.csp.repository.ProyectoEntidadFinanciadoraRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoFacturacionRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoProrrogaRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoProyectoSgeRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoResponsableEconomicoRepository;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiCnfService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiComService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiSgempService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiSgpService;
import org.crue.hercules.sgi.framework.i18n.I18nConfig;
import org.crue.hercules.sgi.framework.i18n.I18nFieldValue;
import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class ProyectoFacturacionComService {
  private static final String CONFIG_CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_DESTINATARIOS = "csp-com-cal-fact-validarip-destinatarios-";
  private static final String MSG_SIN_ESPEFICAR = "sinEspeficar";

  private final ProyectoRepository proyectoRepository;
  private final ProyectoProyectoSgeRepository proyectoProyectoSgeRepository;
  private final ProyectoEquipoRepository proyectoEquipoRepository;
  private final ProyectoResponsableEconomicoRepository proyectoResponsableEconomicoRepository;
  private final ProyectoEntidadFinanciadoraRepository proyectoEntidadFinanciadoraRepository;
  private final ProyectoProrrogaRepository proyectoProrrogaRepository;
  private final ProyectoFacturacionRepository proyectoFacturacionRepository;
  private final SgiApiCnfService configService;
  private final SgiApiComService emailService;
  private final SgiApiSgpService sgiApiSgpService;
  private final SgiApiSgempService sgiApiSgempService;
  private final MessageSource messageSource;
  private final I18nConfig i18nConfig;
  private final SgiConfigProperties sgiConfigProperties;

  public void enviarComunicado(ProyectoFacturacion proyectoFacturacion) throws JsonProcessingException {

    switch (proyectoFacturacion.getEstadoValidacionIP().getEstado()) {
      case VALIDADA:
        this.enviarComunicadoValidarIpValidada(proyectoFacturacion);
        break;
      case RECHAZADA:
        this.enviarComunicadoValidarIpRechazada(proyectoFacturacion);
        break;
      case NOTIFICADA:
        this.enviarComunicadoNotificar(proyectoFacturacion);
        break;
      default:
        break;
    }
  }

  private void enviarComunicadoValidarIpValidada(ProyectoFacturacion proyectoFacturacion)
      throws JsonProcessingException {

    Proyecto proyecto = proyectoRepository.findById(proyectoFacturacion.getProyectoId())
        .orElseThrow(() -> new ProyectoNotFoundException(proyectoFacturacion.getProyectoId()));

    CspComCalendarioFacturacionValidarIPData data = buildCspComCalendarioFacturacionValidarIpData(proyectoFacturacion,
        proyecto);
    EmailOutput output = this.emailService.createComunicadoCalendarioFacturacionValidarIpValidada(data,
        this.getRecipientsUG(proyecto.getUnidadGestionRef()));
    this.emailService.sendEmail(output.getId());

  }

  private void enviarComunicadoValidarIpRechazada(ProyectoFacturacion proyectoFacturacion)
      throws JsonProcessingException {

    Proyecto proyecto = proyectoRepository.findById(proyectoFacturacion.getProyectoId())
        .orElseThrow(() -> new ProyectoNotFoundException(proyectoFacturacion.getProyectoId()));

    CspComCalendarioFacturacionValidarIPData data = buildCspComCalendarioFacturacionValidarIpData(proyectoFacturacion,
        proyecto);

    EmailOutput output = this.emailService.createComunicadoCalendarioFacturacionValidarIpRechazada(data,
        this.getRecipientsUG(proyecto.getUnidadGestionRef()));
    this.emailService.sendEmail(output.getId());
  }

  private void enviarComunicadoNotificar(ProyectoFacturacion proyectoFacturacion) {

    Proyecto proyecto = proyectoRepository.findById(proyectoFacturacion.getProyectoId())
        .orElseThrow(() -> new ProyectoNotFoundException(proyectoFacturacion.getProyectoId()));

    List<PersonaOutput> personas = getMiembrosEquiposAndResponsablesEconomicos(proyectoFacturacion.getProyectoId());

    personas.forEach(persona -> {
      try {
        CspComCalendarioFacturacionNotificarData data = fillBasicCspComCalendarioFacturacionNotificarData(
            proyectoFacturacion, proyecto, persona);
        this.buildAndSendMailNotificar(proyectoFacturacion, data, getRecipientFromPersona(persona));
      } catch (Exception ex) {
        log.error(ex.getMessage(), ex);
      }
    });
  }

  private void buildAndSendMailNotificar(ProyectoFacturacion proyectoFacturacion,
      CspComCalendarioFacturacionNotificarData data,
      Recipient recipient) throws JsonProcessingException {

    boolean isIncluirEnComunicado = proyectoFacturacion.getTipoFacturacion() != null
        && proyectoFacturacion.getTipoFacturacion().isIncluirEnComunicado();

    data.setProrroga(isInsideProrroga(proyectoFacturacion));

    if (proyectoFacturacion.getNumeroPrevision() == 1 && !data.isProrroga()) {
      if (isTheLastFactura(proyectoFacturacion)) {
        // Es la primera factura Y NO se trata de una prórroga Y ES la factura final
        this.buildAndSendIfFacturaIsUniqueAndNotInsideProrroga(data, recipient, isIncluirEnComunicado);
      } else {
        // Es la primera factura Y NO se trata de una prórroga Y NO ES la factura final
        this.buildAndSendIfFacturaIsFirstAndNotInsideProrrogaAndNotLast(data, recipient);
      }
    }

    if ((proyectoFacturacion.getNumeroPrevision() > 1 || data.isProrroga())) {
      if (isTheLastFactura(proyectoFacturacion)) {
        // (NO es la primera factura O se trata de una prórroga) Y ES la factura final
        this.buildAndSendIfFacturaIsNotFirstOrInsideProrrogaAndIsLast(data, recipient, isIncluirEnComunicado);
      } else {
        // (NO es la primera factura O se trata de una prórroga) Y NO ES la factura
        // final
        this.buildAndSendIfFacturaIsNotFirstOrInsideProrrogaAndNotIsLast(data, recipient, isIncluirEnComunicado);
      }
    }

  }

  /**
   * Envia el comunicado si {@code isIncluirEnComunicado} es
   * {@code true}
   * <code>CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA</code>,
   * si es {@code false} se envia
   * <code>CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA_NO_REQUISITO</code>
   * 
   * @param data                  datos para el comunicado
   * @param recipient             destinatarios
   * @param isIncluirEnComunicado indica si el tipo {@link TipoFacturacion} se
   *                              incluye en el comunicado
   * @throws JsonProcessingException
   */
  private void buildAndSendIfFacturaIsUniqueAndNotInsideProrroga(
      CspComCalendarioFacturacionNotificarData data,
      Recipient recipient,
      boolean isIncluirEnComunicado) throws JsonProcessingException {
    EmailOutput output = null;
    if (isIncluirEnComunicado) {
      output = this.emailService.createComunicadoCalendarioFacturacionNotificarFacturaUnicaNoProrroga(data,
          Collections.singletonList(recipient));
    } else {
      output = this.emailService.createComunicadoCalendarioFacturacionNotificarFacturaUnicaNoProrrogaNoRequisito(data,
          Collections.singletonList(recipient));
    }

    this.emailService.sendEmail(output.getId());
  }

  /**
   * Envia el comunicado
   * <code>CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_FIRST_NO_PRORROGA_NO_LAST</code>
   * 
   * @param data      datos para el comunicado
   * @param recipient destinatarios
   * @throws JsonProcessingException
   */
  private void buildAndSendIfFacturaIsFirstAndNotInsideProrrogaAndNotLast(
      CspComCalendarioFacturacionNotificarData data,
      Recipient recipient) throws JsonProcessingException {
    EmailOutput output = this.emailService.createComunicadoCalendarioFacturacionNotificarFacturaFirstNoProrrogaNoLast(
        data,
        Collections.singletonList(recipient));
    this.emailService.sendEmail(output.getId());
  }

  /**
   * Envia el comunicado si {@code isIncluirEnComunicado} es
   * {@code true}
   * <code>CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST</code>,
   * si es {@code false} se envia
   * <code>CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST_NO_REQUISITO</code>
   * 
   * @param data                  datos para el comunicado
   * 
   * @param recipient             destinatarios
   * @param isIncluirEnComunicado indica si el tipo {@link TipoFacturacion} se
   *                              incluye en el comunicado
   * @throws JsonProcessingException
   */
  private void buildAndSendIfFacturaIsNotFirstOrInsideProrrogaAndIsLast(
      CspComCalendarioFacturacionNotificarData data,
      Recipient recipient,
      boolean isIncluirEnComunicado) throws JsonProcessingException {
    EmailOutput output = null;
    if (isIncluirEnComunicado) {
      output = this.emailService.createComunicadoCalendarioFacturacionNotificarFacturaNotFirstOrInProrrogaAndIsLast(
          data,
          Collections.singletonList(recipient));
    } else {
      output = this.emailService
          .createComunicadoCalendarioFacturacionNotificarFacturaNotFirstOrInProrrogaAndIsLastNoRequisitos(data,
              Collections.singletonList(recipient));
    }

    this.emailService.sendEmail(output.getId());
  }

  /**
   * Envia el comunicado si {@code isIncluirEnComunicado} es
   * {@code true}
   * <code>CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST</code>,
   * si es {@code false} se envia
   * <code>CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST_NO_REQUISITO</code>
   * 
   * @param data                  datos para el comunicado
   * @param recipient             destinatarios
   * @param isIncluirEnComunicado indica si el tipo {@link TipoFacturacion} se
   *                              incluye en el comunicado
   * @throws JsonProcessingException
   */
  private void buildAndSendIfFacturaIsNotFirstOrInsideProrrogaAndNotIsLast(
      CspComCalendarioFacturacionNotificarData data,
      Recipient recipient,
      boolean isIncluirEnComunicado) throws JsonProcessingException {

    EmailOutput output = null;
    if (isIncluirEnComunicado) {
      output = this.emailService
          .createComunicadoCalendarioFacturacionNotificarFacturaNotFirstOrInProrrogaAndIsNotLast(data,
              Collections.singletonList(recipient));
    } else {
      output = this.emailService
          .createComunicadoCalendarioFacturacionNotificarFacturaNotFirstOrInProrrogaAndIsNotLastNoRequisito(data,
              Collections.singletonList(recipient));
    }

    this.emailService.sendEmail(output.getId());
  }

  private boolean isTheLastFactura(ProyectoFacturacion proyectoFacturacion) {
    return this.proyectoFacturacionRepository
        .findFirstByProyectoIdOrderByNumeroPrevisionDesc(proyectoFacturacion.getProyectoId())
        .map(ProyectoFacturacion::getNumeroPrevision).orElse(-1)
        .longValue() == proyectoFacturacion.getNumeroPrevision().longValue();
  }

  private boolean isInsideProrroga(ProyectoFacturacion proyectoFacturacion) {
    return proyectoFacturacion.getFechaEmision() != null && this.proyectoProrrogaRepository
        .existsByProyectoIdAndFechaConcesionLessThanEqualAndFechaFinGreaterThanEqual(
            proyectoFacturacion.getProyectoId(), proyectoFacturacion.getFechaEmision(),
            proyectoFacturacion.getFechaEmision());
  }

  private CspComCalendarioFacturacionNotificarData fillBasicCspComCalendarioFacturacionNotificarData(
      ProyectoFacturacion proyectoFacturacion,
      Proyecto proyecto, PersonaOutput persona) {

    List<I18nFieldValueDto> i18nSinEspecificar = new ArrayList<>();
    for (Language language : i18nConfig.getEnabledLanguages()) {
      i18nSinEspecificar.add(new I18nFieldValueDto(language,
          messageSource.getMessage(MSG_SIN_ESPEFICAR, null, Locale.forLanguageTag(language.getCode()))));
    }

    return CspComCalendarioFacturacionNotificarData
        .builder()
        .codigosSge(this.getCodigosSge(proyectoFacturacion.getProyectoId()))
        .tituloProyecto(convertToI18nFieldValueDto(proyecto.getTitulo()))
        .numPrevision(proyectoFacturacion.getNumeroPrevision())
        .tipoFacturacion(proyectoFacturacion.getTipoFacturacion() == null
            ? i18nSinEspecificar
            : convertToI18nFieldValueDto(proyectoFacturacion.getTipoFacturacion().getNombre()))
        .entidadesFinanciadoras(getNombresEntidadesFinanciadorasByProyectoId(
            proyectoFacturacion.getProyectoId()))
        .apellidosDestinatario(persona.getApellidos())
        .enlaceAplicacion(sgiConfigProperties.getWebUrl())
        .build();
  }

  private List<String> getNombresEntidadesFinanciadorasByProyectoId(Long proyectoId) {
    return this.proyectoEntidadFinanciadoraRepository
        .findByProyectoId(proyectoId).stream()
        .map(entidad -> sgiApiSgempService.findById(entidad.getEntidadRef()).getNombre())
        .toList();
  }

  private Recipient getRecipientFromPersona(PersonaOutput persona) {
    Recipient.RecipientBuilder builder = Recipient.builder();
    builder.name(persona.getNombre() + " " + persona.getApellidos());
    if (CollectionUtils.isEmpty(persona.getEmails())) {
      return null;
    }
    String address = persona.getEmails().stream().filter(Email::getPrincipal).findFirst()
        .map(Email::getEmail)
        .orElse(null);
    builder.address(address);

    return StringUtils.isEmpty(address) ? null : builder.build();
  }

  private List<PersonaOutput> getMiembrosEquiposAndResponsablesEconomicos(Long proyectoId) {
    List<String> members = Stream.concat(
        this.proyectoEquipoRepository
            .findByProyectoIdAndRolProyectoRolPrincipalTrue(proyectoId)
            .stream()
            .map(ProyectoEquipo::getPersonaRef),
        this.proyectoResponsableEconomicoRepository
            .findByProyectoId(proyectoId)
            .stream()
            .map(ProyectoResponsableEconomico::getPersonaRef))
        .distinct()
        .toList();

    return this.sgiApiSgpService.findAllByIdIn(members);
  }

  private CspComCalendarioFacturacionValidarIPData buildCspComCalendarioFacturacionValidarIpData(
      ProyectoFacturacion proyectoFacturacion, Proyecto proyecto) {

    String personaRef = SecurityContextHolder.getContext().getAuthentication().getName();

    PersonaOutput persona = this.sgiApiSgpService.findById(personaRef);

    List<String> codigosSge = getCodigosSge(proyecto.getId());

    return CspComCalendarioFacturacionValidarIPData.builder()
        .tituloProyecto(proyecto.getTitulo())
        .numPrevision(proyectoFacturacion.getNumeroPrevision())
        .codigosSge(codigosSge)
        .motivoRechazo(proyectoFacturacion.getEstadoValidacionIP().getEstado() == TipoEstadoValidacion.RECHAZADA
            ? proyectoFacturacion.getEstadoValidacionIP().getComentario()
            : Collections.emptyList())
        .nombreApellidosValidador(persona.getNombre() + " " + persona.getApellidos())
        .build();
  }

  private List<String> getCodigosSge(Long proyectoId) {
    return this.proyectoProyectoSgeRepository.findByProyectoId(proyectoId).stream()
        .map(ProyectoProyectoSge::getProyectoSgeRef).toList();
  }

  private List<Recipient> getRecipientsUG(String unidadGestionRef) throws JsonProcessingException {
    return configService
        .findStringListByName(
            CONFIG_CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_DESTINATARIOS + unidadGestionRef)
        .stream()
        .map(destinatario -> Recipient.builder().name(destinatario).address(destinatario).build())
        .toList();
  }

  private Set<I18nFieldValueDto> convertToI18nFieldValueDto(Set<? extends I18nFieldValue> values) {
    return values.stream().map(t -> new I18nFieldValueDto(t.getLang(), t.getValue()))
        .collect(Collectors.toSet());
  }

}
