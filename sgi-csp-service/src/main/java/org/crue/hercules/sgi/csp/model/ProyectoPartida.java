package org.crue.hercules.sgi.csp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.enums.TipoPartida;
import org.crue.hercules.sgi.csp.validation.UniqueCodigoTipoProyectoPartida;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "proyecto_partida", uniqueConstraints = { @UniqueConstraint(columnNames = { "proyecto_id", "codigo",
    "tipo_partida" }, name = "UK_PROYECTOPARTIDA_PROYECTO_CODIGO_TIPO") })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@UniqueCodigoTipoProyectoPartida(groups = { BaseEntity.Create.class, BaseEntity.Update.class })
public class ProyectoPartida extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proyecto_partida_seq")
  @SequenceGenerator(name = "proyecto_partida_seq", sequenceName = "proyecto_partida_seq", allocationSize = 1)
  private Long id;

  /** Proyecto Id */
  @Column(name = "proyecto_id", nullable = false)
  @NotNull
  private Long proyectoId;

  /** Convocatoria partida id. */
  @Column(name = "convocatoria_partida_id", nullable = true)
  private Long convocatoriaPartidaId;

  /** Convocatoria partida id. */
  @Column(name = "codigo", length = 50, nullable = false)
  @Size(max = 50)
  @NotNull
  private String codigo;

  /** Descripcion */
  @Column(name = "descripcion", length = 250, nullable = true)
  @Size(max = 250)
  private String descripcion;

  /** Tipo partida */
  @Column(name = "tipo_partida", length = 50, nullable = false)
  @Enumerated(EnumType.STRING)
  @NotNull
  private TipoPartida tipoPartida;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "proyecto_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PROYECTOPARTIDA_PROYECTO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Proyecto proyecto = null;

}
