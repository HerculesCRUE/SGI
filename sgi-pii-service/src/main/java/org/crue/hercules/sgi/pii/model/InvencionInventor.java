package org.crue.hercules.sgi.pii.model;

import java.math.BigDecimal;

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
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = InvencionInventor.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class InvencionInventor {

  public static final String TABLE_NAME = "invencion_inventor";
  public static final String SEQ_SUFFIX = "_seq";
  public static final String SEQ_NAME = InvencionInventor.TABLE_NAME + InvencionInventor.SEQ_SUFFIX;
  public static final int REF_LENGTH = 50;
  public static final int PARTICIPACION_MIN = 1;
  public static final int PARTICIPACION_MAX = 100;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = InvencionInventor.SEQ_NAME)
  @SequenceGenerator(name = InvencionInventor.SEQ_NAME, sequenceName = InvencionInventor.SEQ_NAME, allocationSize = 1)
  private Long id;

  /** Invencion Id */
  @Column(name = "invencion_id", nullable = false)
  private Long invencionId;

  /** Referencia a una Persona */
  @Column(name = "inventor_ref", length = REF_LENGTH, nullable = false)
  private String inventorRef;

  /** Porcentaje de Participación en la Invención */
  @Column(name = "participacion", nullable = false)
  private BigDecimal participacion;

  /** Define si se realizará el Reparto por parte de la Universidad */
  @Column(name = "reparto_universidad", columnDefinition = "boolean default false", nullable = false)
  private Boolean repartoUniversidad;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "invencion_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_INVENCIONINVENTOR_INVENCION"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Invencion invencion = null;

  @JsonInclude()
  @Transient
  @Builder.Default
  private Boolean activo = true;

}
