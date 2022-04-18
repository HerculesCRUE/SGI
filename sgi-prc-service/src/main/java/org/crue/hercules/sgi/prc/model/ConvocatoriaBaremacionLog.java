package org.crue.hercules.sgi.prc.model;

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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = ConvocatoriaBaremacionLog.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConvocatoriaBaremacionLog extends BaseEntity {

  protected static final String TABLE_NAME = "convocatoria_baremacion_log";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
  @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  @Column(name = "trace", nullable = false, columnDefinition = "clob")
  private String trace;

  @Column(name = "convocatoria_baremacion_id", nullable = true)
  private Long convocatoriaBaremacionId;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "convocatoria_baremacion_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_CONVOCATORIABAREMACIONLOG_CONVOCATORIABAREMACION"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final ConvocatoriaBaremacion convocatoriaBaremacion = null;

}
