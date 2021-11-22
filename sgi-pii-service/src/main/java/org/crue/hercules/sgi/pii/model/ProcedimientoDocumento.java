package org.crue.hercules.sgi.pii.model;

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
@Table(name = ProcedimientoDocumento.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcedimientoDocumento extends BaseEntity {

  public static final String TABLE_NAME = "procedimiento_documento";
  private static final String SEQ_SUFFIX = "_seq";

  public static final int NOMBRE_LENGTH = 250;
  public static final int FICHERO_LENGTH = 250;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TABLE_NAME + SEQ_SUFFIX)
  @SequenceGenerator(name = TABLE_NAME + SEQ_SUFFIX, sequenceName = TABLE_NAME + SEQ_SUFFIX, allocationSize = 1)
  private Long id;

  @Column(name = "nombre", nullable = false, length = NOMBRE_LENGTH)
  private String nombre;

  @Column(name = "documento_ref", nullable = false, length = FICHERO_LENGTH)
  private String documentoRef;

  @Column(name = "procedimiento_id", nullable = false)
  private Long procedimientoId;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "procedimiento_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PROCEDIMIENTODOCUMENTO_PROCEDIMIENTO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Procedimiento procedimiento = null;

}
