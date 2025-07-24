package org.crue.hercules.sgi.rep.report.data.objects;

import org.crue.hercules.sgi.rep.dto.sgemp.EmpresaDto;

import lombok.Getter;

@Getter
public class EmpresaObject {

  private String id;
  private String nombre;
  private String razonSocial;
  private String numeroIdentificacion;

  public EmpresaObject(EmpresaDto dto) {
    this.id = dto.getId();
    this.nombre = dto.getNombre();
    this.razonSocial = dto.getRazonSocial();
    this.numeroIdentificacion = dto.getNumeroIdentificacion();
  }

}
