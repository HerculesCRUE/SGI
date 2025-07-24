package org.crue.hercules.sgi.rep.report.data.objects;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import org.crue.hercules.sgi.rep.dto.sgp.DatosContactoDto;
import org.crue.hercules.sgi.rep.dto.sgp.DatosContactoDto.ComunidadAutonomaDto;
import org.crue.hercules.sgi.rep.dto.sgp.DatosContactoDto.PaisDto;
import org.crue.hercules.sgi.rep.dto.sgp.DatosContactoDto.ProvinciaDto;
import org.crue.hercules.sgi.rep.dto.sgp.EmailDto;

import lombok.Getter;

@Getter
public class DatosContactoObject implements Serializable {
  private PaisObject paisContacto;
  private ComunidadAutonomaObject comAutonomaContacto;
  private ProvinciaObject provinciaContacto;
  private String ciudadContacto;
  private String codigoPostalContacto;
  private String direccionContacto;
  private String email;
  private String telefono;
  private String movil;

  public DatosContactoObject(DatosContactoDto dto) {
    if (dto.getPaisContacto() != null) {
      this.paisContacto = new PaisObject(dto.getPaisContacto());
    }
    if (dto.getComAutonomaContacto() != null) {
      this.comAutonomaContacto = new ComunidadAutonomaObject(dto.getComAutonomaContacto());
    }
    if (dto.getProvinciaContacto() != null) {
      this.provinciaContacto = new ProvinciaObject(dto.getProvinciaContacto());
    }
    this.ciudadContacto = dto.getCiudadContacto();
    this.codigoPostalContacto = dto.getCodigoPostalContacto();
    this.direccionContacto = dto.getDireccionContacto();
    this.email = this.getEmail(dto.getEmails());
    this.telefono = this.getTelefonoOrMovil(dto.getTelefonos());
    this.movil = this.getTelefonoOrMovil(dto.getMoviles());
  }

  @Getter
  public static class PaisObject implements Serializable {
    private String nombre;

    public PaisObject(PaisDto dto) {
      this.nombre = dto.getNombre();
    }
  }

  @Getter
  public static class ComunidadAutonomaObject implements Serializable {
    private String nombre;

    public ComunidadAutonomaObject(ComunidadAutonomaDto dto) {
      this.nombre = dto.getNombre();
    }
  }

  @Getter
  public static class ProvinciaObject implements Serializable {
    private String nombre;

    public ProvinciaObject(ProvinciaDto dto) {
      this.nombre = dto.getNombre();
    }
  }

  private String getEmail(List<EmailDto> emails) {
    if (null != emails && !emails.isEmpty()) {
      return emails.stream()
          .filter(e -> null != e.getPrincipal() && e.getPrincipal().equals(Boolean.TRUE)).findFirst()
          .orElse(new EmailDto())
          .getEmail();
    } else {
      return null;
    }
  }

  private String getTelefonoOrMovil(List<String> values) {
    if (values == null || values.isEmpty()) {
      return null;
    }

    return values.stream()
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(null);

  }
}