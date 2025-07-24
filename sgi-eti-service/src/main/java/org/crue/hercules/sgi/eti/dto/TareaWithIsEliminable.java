package org.crue.hercules.sgi.eti.dto;

import java.io.Serializable;
import java.util.Set;

import org.crue.hercules.sgi.eti.model.EquipoTrabajo;
import org.crue.hercules.sgi.eti.model.FormacionEspecifica;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.Tarea;
import org.crue.hercules.sgi.eti.model.TareaFormacion;
import org.crue.hercules.sgi.eti.model.TareaNombre;
import org.crue.hercules.sgi.eti.model.TareaOrganismo;
import org.crue.hercules.sgi.eti.model.TipoTarea;
import org.crue.hercules.sgi.eti.repository.custom.CustomTareaRepository;

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
  private Set<TareaNombre> nombre;
  private Set<TareaFormacion> formacion;
  private FormacionEspecifica formacionEspecifica;
  private TipoTarea tipoTarea;
  private Set<TareaOrganismo> organismo;
  private Integer anio;
  private boolean eliminable;

  /**
   * Constructor especifico para
   * {@link CustomTareaRepository#findAllByPeticionEvaluacionId(Long)}
   * 
   * @param tarea
   * @param eliminable
   */
  public TareaWithIsEliminable(Tarea tarea, Boolean eliminable) {
    this.id = tarea.getId();
    this.equipoTrabajo = tarea.getEquipoTrabajo();
    this.memoria = tarea.getMemoria();
    this.nombre = tarea.getNombre();
    this.formacion = tarea.getFormacion();
    this.formacionEspecifica = tarea.getFormacionEspecifica();
    this.tipoTarea = tarea.getTipoTarea();
    this.organismo = tarea.getOrganismo();
    this.anio = tarea.getAnio();
    this.eliminable = eliminable;
  }

}
