package org.crue.hercules.sgi.eti.dto;

import java.io.Serializable;

import org.crue.hercules.sgi.eti.model.EquipoTrabajo;
import org.crue.hercules.sgi.eti.model.FormacionEspecifica;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.TipoTarea;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TareaWithIsEliminable implements Serializable {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  private Long id;
  private EquipoTrabajo equipoTrabajo;
  private Memoria memoria;
  private String tarea;
  private String formacion;
  private FormacionEspecifica formacionEspecifica;
  private TipoTarea tipoTarea;
  private String organismo;
  private Integer anio;
  private boolean eliminable;

  public TareaWithIsEliminable(Long id, EquipoTrabajo equipoTrabajo, Memoria memoria, String tarea, String formacion,
      Long idFormacionEspecifica, String nombreFormacionEspecifica, Boolean activoFormacionEspecifica, Long idTipoTarea,
      String nombreTipoTarea, Boolean activoTipoTarea, String organismo, Integer anio, Boolean eliminable) {
    this.id = id;
    this.equipoTrabajo = equipoTrabajo;
    this.memoria = memoria;
    this.tarea = tarea;
    this.formacion = formacion;
    if (idFormacionEspecifica != null) {
      this.formacionEspecifica = new FormacionEspecifica();
      this.formacionEspecifica.setId(idFormacionEspecifica);
      this.formacionEspecifica.setNombre(nombreFormacionEspecifica);
      this.formacionEspecifica.setActivo(activoFormacionEspecifica);
    }
    if (idTipoTarea != null) {
      this.tipoTarea = new TipoTarea();
      this.tipoTarea.setId(idTipoTarea);
      this.tipoTarea.setNombre(nombreTipoTarea);
      this.tipoTarea.setActivo(activoTipoTarea);
    }
    this.organismo = organismo;
    this.anio = anio;
    this.eliminable = eliminable;
  }

}
