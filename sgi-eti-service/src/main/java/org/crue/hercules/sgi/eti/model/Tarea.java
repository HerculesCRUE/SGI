package org.crue.hercules.sgi.eti.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tarea")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Tarea extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;
  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tarea_seq")
  @SequenceGenerator(name = "tarea_seq", sequenceName = "tarea_seq", allocationSize = 1)
  private Long id;

  /** Equipo trabajo */
  @ManyToOne
  @JoinColumn(name = "equipo_trabajo_id", nullable = false, foreignKey = @ForeignKey(name = "FK_TAREA_EQUIPOTRABAJO"))
  private EquipoTrabajo equipoTrabajo;

  /** Memoria */
  @ManyToOne
  @JoinColumn(name = "memoria_id", nullable = false, foreignKey = @ForeignKey(name = "FK_TAREA_MEMORIA"))
  @NotNull
  private Memoria memoria;

  /** Nombre */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "tarea_nombre", joinColumns = @JoinColumn(name = "tarea_id"))
  @Valid
  private Set<TareaNombre> nombre = new HashSet<>();

  /** Formacion */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "tarea_formacion", joinColumns = @JoinColumn(name = "tarea_id"))
  @Valid
  private Set<TareaFormacion> formacion = new HashSet<>();

  /** Formacion especifica */
  @ManyToOne
  @JoinColumn(name = "formacion_especifica_id", nullable = true, foreignKey = @ForeignKey(name = "FK_TAREA_FORMACIONESPECIFICA"))
  private FormacionEspecifica formacionEspecifica;

  /** Organismo */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "tarea_organismo", joinColumns = @JoinColumn(name = "tarea_id"))
  @Valid
  private Set<TareaOrganismo> organismo = new HashSet<>();

  /** Anio */
  @Column(name = "anio", nullable = true)
  private Integer anio;

  /** Tipo tarea */
  @ManyToOne
  @JoinColumn(name = "tipo_tarea_id", nullable = true, foreignKey = @ForeignKey(name = "FK_TAREA_TIPOTAREA"))
  private TipoTarea tipoTarea;

}