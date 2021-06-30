package org.crue.hercules.sgi.csp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "proyecto_periodo_seguimiento_documento")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoPeriodoSeguimientoDocumento extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proyecto_periodo_seguimiento_documento_seq")
  @SequenceGenerator(name = "proyecto_periodo_seguimiento_documento_seq", sequenceName = "proyecto_periodo_seguimiento_documento_seq", allocationSize = 1)
  private Long id;

  /** ProyectoPeriodoSeguimiento Id */
  @Column(name = "proyecto_periodo_seguimiento_id", nullable = false)
  @NotNull
  private Long proyectoPeriodoSeguimientoId;

  /** Comentarios */
  @Column(name = "comentario", length = 2000, nullable = true)
  @Size(max = 2000)
  private String comentario;

  /** DocumentoRef */
  @Column(name = "documento_ref", length = 50, nullable = false)
  @Size(max = 50)
  @NotNull
  private String documentoRef;

  /** Nombre documento */
  @Column(name = "nombre", length = 50, nullable = false)
  @Size(max = 50)
  @NotNull
  private String nombre;

  /** Tipo documento */
  @ManyToOne
  @JoinColumn(name = "tipo_documento_id", nullable = true, foreignKey = @ForeignKey(name = "FK_PROYECTOPERIODOSEGUIMIENTODOCUMENTO_TIPODOCUMENTO"))
  private TipoDocumento tipoDocumento;

  /** Visibilidad */
  @Column(name = "visible", nullable = true)
  private Boolean visible;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "proyecto_periodo_seguimiento_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PROYECTOPERIODOSEGUIMIENTODOCUMENTO_PROYECTOPERIODOSEGUIMIENTO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento = null;
}