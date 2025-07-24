package org.crue.hercules.sgi.rep.report.data.objects;

import lombok.Getter;

/*
 * Se mantiene como retrocompatible con formularios antiguos
 */
@Getter
public class TipoMemoriaObject {
  private Long id;
  private String nombre;
  private Boolean activo = Boolean.TRUE;

  public TipoMemoriaObject(Long id, String nombre) {
    this.id = id;
    this.nombre = nombre;
  }
}
