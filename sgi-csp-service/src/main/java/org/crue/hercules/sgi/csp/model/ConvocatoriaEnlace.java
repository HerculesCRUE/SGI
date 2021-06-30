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
@Table(name = "convocatoria_enlace")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ConvocatoriaEnlace extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "convocatoria_enlace_seq")
  @SequenceGenerator(name = "convocatoria_enlace_seq", sequenceName = "convocatoria_enlace_seq", allocationSize = 1)
  private Long id;

  /** Convocatoria Id */
  @Column(name = "convocatoria_id", nullable = false)
  @NotNull
  private Long convocatoriaId;

  /** Url */
  @Column(name = "url", length = 250, nullable = false)
  @Size(max = 250)
  private String url;

  /** Descripcion */
  @Column(name = "descripcion", length = 250, nullable = true)
  @Size(max = 250)
  private String descripcion;

  /** Tipo enlace. */
  @ManyToOne
  @JoinColumn(name = "tipo_enlace_id", foreignKey = @ForeignKey(name = "FK_CONVOCATORIAENLACE_TIPOENLACE"))
  private TipoEnlace tipoEnlace;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "convocatoria_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_CONVOCATORIAENLACE_CONVOCATORIA"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Convocatoria convocatoria = null;
}