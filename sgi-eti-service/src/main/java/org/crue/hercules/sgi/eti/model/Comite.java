package org.crue.hercules.sgi.eti.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comite")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Comite extends BaseEntity {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "id", length = 28, nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comite_seq")
  @SequenceGenerator(name = "comite_seq", sequenceName = "comite_seq", allocationSize = 1)
  private Long id;

  /** Código */
  @Column(name = "codigo", length = 50, nullable = false)
  private String codigo;

  /** Nombre */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "comite_nombre", joinColumns = @JoinColumn(name = "comite_id"))
  @NotEmpty
  @Valid
  private Set<ComiteNombre> nombre = new HashSet<>();

  /** Formulario Memoria */
  @Column(name = "formulario_memoria_id", nullable = false)
  @NotNull
  private Long formularioMemoriaId;

  /** Formulario Seguimiento Anual */
  @Column(name = "formulario_seguimiento_anual_id", nullable = false)
  @NotNull
  private Long formularioSeguimientoAnualId;

  /** Formulario Seguimiento Final */
  @Column(name = "formulario_seguimiento_final_id", nullable = false)
  @NotNull
  private Long formularioSeguimientoFinalId;

  /** Formulario Restrospectiva */
  @Column(name = "formulario_retrospectiva_id", nullable = true)
  private Long formularioRetrospectivaId;

  /** Requiere retrospectiva */
  @Column(name = "requiere_retrospectiva", nullable = false)
  @NotNull
  private Boolean requiereRetrospectiva;

  /** Prefijo de las memorias */
  @Column(name = "prefijo_referencia", length = 255, nullable = false)
  private String prefijoReferencia;

  /** Permitir memorias de tipo ratificación */
  @Column(name = "permitir_ratificacion", nullable = false)
  @NotNull
  private Boolean permitirRatificacion;

  /** Habilitar la creación de tareas con nombre libre */
  @Column(name = "tarea_nombre_libre", nullable = false)
  @NotNull
  private Boolean tareaNombreLibre;

  /** Habilitar la creación de tareas con experiencia libre */
  @Column(name = "tarea_experiencia_libre", nullable = false)
  @NotNull
  private Boolean tareaExperienciaLibre;

  /** Habilitar la creación de tareas con detelle de experiencia */
  @Column(name = "tarea_experiencia_detalle", nullable = false)
  @NotNull
  private Boolean tareaExperienciaDetalle;

  /** Habilitar la creación de memorias con título libre */
  @Column(name = "memoria_titulo_libre", nullable = false)
  @NotNull
  private Boolean memoriaTituloLibre;

  /** Activo */
  @Column(name = "activo", columnDefinition = "boolean default true", nullable = false)
  private Boolean activo;
}