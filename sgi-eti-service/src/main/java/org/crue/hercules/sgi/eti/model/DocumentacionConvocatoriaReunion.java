package org.crue.hercules.sgi.eti.model;

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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DocumentacionConvocatoriaReunion
 */
@Entity
@Table(name = "documentacion_convocatoria_reunion")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class DocumentacionConvocatoriaReunion extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "documentacion_convocatoria_reunion_seq")
  @SequenceGenerator(name = "documentacion_convocatoria_reunion_seq", sequenceName = "documentacion_convocatoria_reunion_seq", allocationSize = 1)
  private Long id;

  /** ConvocatoriaReunion */
  @Column(name = "convocatoria_reunion_id", nullable = false)
  private Long convocatoriaReunionId;

  /** Referencia documento */
  @Column(name = "documento_ref", length = 250, nullable = false)
  @NotNull
  private String documentoRef;

  /** Nombre */
  @Column(name = "nombre", nullable = false, length = 250)
  private String nombre;

  @ManyToOne
  @JoinColumn(name = "convocatoria_reunion_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_DOCUMENTACIONCONVOCATORIAREUNION_CONVOCATORIAREUNION"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final ConvocatoriaReunion convocatoriaReunion = null;
}