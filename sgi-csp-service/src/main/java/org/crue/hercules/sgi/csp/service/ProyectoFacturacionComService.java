package org.crue.hercules.sgi.csp.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.crue.hercules.sgi.csp.dto.com.CspComCalendarioFacturacionNotificarData;
import org.crue.hercules.sgi.csp.dto.com.CspComCalendarioFacturacionNotificarData.CspComCalendarioFacturacionNotificarDataBuilder;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class ProyectoFacturacionComService {
  private static final String CONFIG_CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_DESTINATARIOS = "csp-com-cal-fact-validarip-destinatarios-";
  private static final String SIN_REQUISITO = "Sin requisito";

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

  public void enviarComunicado(ProyectoFacturacion proyectoFacturacion) throws Exception {

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

  private void enviarComunicadoValidarIpValidada(ProyectoFacturacion proyectoFacturacion) throws Exception {

    Proyecto proyecto = proyectoRepository.findById(proyectoFacturacion.getProyectoId())
        .orElseThrow(() -> new ProyectoNotFoundException(proyectoFacturacion.getProyectoId()));

    CspComCalendarioFacturacionValidarIPData data = buildCspComCalendarioFacturacionValidarIpData(proyectoFacturacion,
        proyecto);
    EmailOutput output = this.emailService.createComunicadoCalendarioFacturacionValidarIpValidada(data,
        this.getRecipientsUG(proyecto.getUnidadGestionRef()));
    this.emailService.sendEmail(output.getId());

  }

  private void enviarComunicadoValidarIpRechazada(ProyectoFacturacion proyectoFacturacion) throws Exception {

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

    CspComCalendarioFacturacionNotificarDataBuilder dataBuilder = fillBasicCspComCalendarioFacturacionNotificarData(
        proyectoFacturacion, proyecto);

    personas.forEach(persona -> {
      try {
        dataBuilder.apellidosDestinatario(persona.getApellidos());
        this.buildAndSendMailNotificar(proyectoFacturacion, dataBuilder.build(),
            getRecipientFromPersona(persona));
      } catch (Exception ex) {
        log.error(ex.getMessage(), ex);
      }
    });
  }

  private void buildAndSendMailNotificar(ProyectoFacturacion proyectoFacturacion,
      CspComCalendarioFacturacionNotificarData data,
      Recipient recipient) throws JsonProcessingException {

    this.buildAndSendIfFacturaIsUniqueAndNotInsideProrroga(proyectoFacturacion, data, recipient);
    this.buildAndSendIfFacturaIsFirstAndNotInsideProrrogaAndNotLast(proyectoFacturacion, data, recipient);
    this.buildAndSendIfFacturaIsNotFirstOrInsideProrrogaAndIsLast(proyectoFacturacion, data, recipient);
    this.buildAndSendIfFacturaIsNotFirstOrInsideProrrogaAndNotIsLast(proyectoFacturacion, data, recipient);
  }

  private void buildAndSendIfFacturaIsUniqueAndNotInsideProrroga(ProyectoFacturacion proyectoFacturacion,
      CspComCalendarioFacturacionNotificarData data, Recipient recipient) throws JsonProcessingException {
    if (proyectoFacturacion.getNumeroPrevision() == 1 && !isInsideProrroga(proyectoFacturacion)
        && isTheLastFactura(proyectoFacturacion)) {
      EmailOutput output = null;
      if (proyectoFacturacion.getTipoFacturacion() == null
          || StringUtils.isEmpty(proyectoFacturacion.getTipoFacturacion()
              .getNombre())
          || proyectoFacturacion.getTipoFacturacion().getNombre().equals(SIN_REQUISITO)) {
        output = this.emailService.createComunicadoCalendarioFacturacionNotificarFacturaUnicaNoProrrogaNoRequisito(data,
            Collections.singletonList(recipient));
      } else {
        output = this.emailService.createComunicadoCalendarioFacturacionNotificarFacturaUnicaNoProrroga(data,
            Collections.singletonList(recipient));
      }
      this.emailService.sendEmail(output.getId());

    }
  }

  private void buildAndSendIfFacturaIsFirstAndNotInsideProrrogaAndNotLast(ProyectoFacturacion proyectoFacturacion,
      CspComCalendarioFacturacionNotificarData data, Recipient recipient) throws JsonProcessingException {
    if (proyectoFacturacion.getNumeroPrevision() == 1 && !isInsideProrroga(proyectoFacturacion)
        && !isTheLastFactura(proyectoFacturacion)) {
      EmailOutput output = this.emailService.createComunicadoCalendarioFacturacionNotificarFacturaFirstNoProrrogaNoLast(
          data,
          Collections.singletonList(recipient));
      this.emailService.sendEmail(output.getId());
    }
  }

  private void buildAndSendIfFacturaIsNotFirstOrInsideProrrogaAndIsLast(ProyectoFacturacion proyectoFacturacion,
      CspComCalendarioFacturacionNotificarData data, Recipient recipient) throws JsonProcessingException {

    data.setProrroga(isInsideProrroga(proyectoFacturacion));

    if ((proyectoFacturacion.getNumeroPrevision() > 1 || data.isProrroga())
        && isTheLastFactura(proyectoFacturacion)) {

      EmailOutput output = null;
      if (proyectoFacturacion.getTipoFacturacion() == null
          || StringUtils.isEmpty(proyectoFacturacion.getTipoFacturacion()
              .getNombre())
          || SIN_REQUISITO.equals(proyectoFacturacion.getTipoFacturacion().getNombre())) {
        output = this.emailService
            .createComunicadoCalendarioFacturacionNotificarFacturaNotFirstOrInProrrogaAndIsLastNoRequisitos(data,
                Collections.singletonList(recipient));
      } else {
        output = this.emailService.createComunicadoCalendarioFacturacionNotificarFacturaNotFirstOrInProrrogaAndIsLast(
            data,
            Collections.singletonList(recipient));
      }
      this.emailService.sendEmail(output.getId());

    }
  }

  private void buildAndSendIfFacturaIsNotFirstOrInsideProrrogaAndNotIsLast(ProyectoFacturacion proyectoFacturacion,
      CspComCalendarioFacturacionNotificarData data, Recipient recipient) throws JsonProcessingException {
    if ((proyectoFacturacion.getNumeroPrevision() > 1 || isInsideProrroga(proyectoFacturacion))
        && !isTheLastFactura(proyectoFacturacion)) {
      EmailOutput output = null;
      if (proyectoFacturacion.getTipoFacturacion() == null
          || StringUtils.isEmpty(proyectoFacturacion.getTipoFacturacion()
              .getNombre())
          || SIN_REQUISITO.equals(proyectoFacturacion.getTipoFacturacion().getNombre())) {

        output = this.emailService
            .createComunicadoCalendarioFacturacionNotificarFacturaNotFirstOrInProrrogaAndIsNotLastNoRequisito(data,
                Collections.singletonList(recipient));
      } else {
        output = this.emailService
            .createComunicadoCalendarioFacturacionNotificarFacturaNotFirstOrInProrrogaAndIsNotLast(data,
                Collections.singletonList(recipient));
      }
      this.emailService.sendEmail(output.getId());
    }
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

  private CspComCalendarioFacturacionNotificarDataBuilder fillBasicCspComCalendarioFacturacionNotificarData(
      ProyectoFacturacion proyectoFacturacion,
      Proyecto proyecto) {

    return CspComCalendarioFacturacionNotificarData
        .builder()
        .codigosSge(this.getCodigosSge(proyectoFacturacion.getProyectoId()))
        .tituloProyecto(proyecto.getTitulo())
        .numPrevision(proyectoFacturacion.getNumeroPrevision())
        .tipoFacturacion(proyectoFacturacion.getTipoFacturacion() == null
            || StringUtils.isEmpty(proyectoFacturacion.getTipoFacturacion().getNombre()) ? "Sin especificar"
                : proyectoFacturacion.getTipoFacturacion().getNombre())
        .entidadesFinanciadoras(getNombresEntidadesFinanciadorasByProyectoId(
            proyectoFacturacion.getProyectoId()));
  }

  private List<String> getNombresEntidadesFinanciadorasByProyectoId(Long proyectoId) {
    return this.proyectoEntidadFinanciadoraRepository
        .findByProyectoId(proyectoId).stream()
        .map(entidad -> sgiApiSgempService.findById(entidad.getEntidadRef()).getNombre())
        .collect(Collectors.toList());
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

    List<String> members = this.proyectoEquipoRepository
        .findByProyectoIdAndRolProyectoRolPrincipalTrue(proyectoId).stream()
        .map(ProyectoEquipo::getPersonaRef).collect(Collectors.toList());

    members.addAll(
        this.proyectoResponsableEconomicoRepository.findByProyectoId(proyectoId).stream()
            .map(ProyectoResponsableEconomico::getPersonaRef).collect(Collectors.toList()));

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
            : "")
        .nombreApellidosValidador(persona.getNombre() + " " + persona.getApellidos())
        .build();
  }

  private List<String> getCodigosSge(Long proyectoId) {
    return this.proyectoProyectoSgeRepository.findByProyectoId(proyectoId).stream()
        .map(ProyectoProyectoSge::getProyectoSgeRef).collect(Collectors.toList());
  }

  private List<Recipient> getRecipientsUG(String unidadGestionRef) throws JsonProcessingException {
    return configService
        .findStringListByName(
            CONFIG_CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_DESTINATARIOS + unidadGestionRef)
        .stream()
        .map(destinatario -> Recipient.builder().name(destinatario).address(destinatario).build())
        .collect(Collectors.toList());
  }

}
