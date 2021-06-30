package org.crue.hercules.sgi.csp.model;

import java.time.Instant;

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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "proyecto_hito")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoHito extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proyecto_hito_seq")
  @SequenceGenerator(name = "proyecto_hito_seq", sequenceName = "proyecto_hito_seq", allocationSize = 1)
  private Long id;

  /** Proyecto Id */
  @Column(name = "proyecto_id", nullable = false)
  @NotNull
  private Long proyectoId;

  /** Tipo hito. */
  @ManyToOne
  @JoinColumn(name = "tipo_hito_id", nullable = false, foreignKey = @ForeignKey(name = "FK_PROYECTOHITO_TIPOHITO"))
  @NotNull
  private TipoHito tipoHito;

  /** Fecha. */
  @Column(name = "fecha", nullable = false)
  @NotNull
  private Instant fecha;

  /** Comentario. */
  @Column(name = "comentario", length = 2000)
  private String comentario;

  /** Genera Aviso */
  @Column(name = "genera_aviso", nullable = false)
  @NotNull
  private Boolean generaAviso;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "proyecto_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PROYECTOHITO_PROYECTO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Proyecto proyecto = null;
}