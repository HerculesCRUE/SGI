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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = IncidenciaDocumentacionRequerimiento.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidenciaDocumentacionRequerimiento extends BaseEntity {

  protected static final String TABLE_NAME = "incidencia_documentacion_requerimiento";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = IncidenciaDocumentacionRequerimiento.SEQUENCE_NAME)
  @SequenceGenerator(name = IncidenciaDocumentacionRequerimiento.SEQUENCE_NAME, sequenceName = IncidenciaDocumentacionRequerimiento.SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  @Column(name = "requerimiento_justificacion_id", nullable = false)
  private Long requerimientoJustificacionId;

  @Column(name = "nombre_documento", nullable = false, length = DEFAULT_TEXT_LENGTH)
  private String nombreDocumento;

  @Column(name = "incidencia", nullable = true, length = DEFAULT_LONG_TEXT_LENGTH)
  private String incidencia;

  @Column(name = "alegacion", nullable = true, length = DEFAULT_LONG_TEXT_LENGTH)
  private String alegacion;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "requerimiento_justificacion_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_INCIDENCIADOCUMENTACIONREQUERIMIENTO_REQUERIMIENTOJUSTIFICACION"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final RequerimientoJustificacion requerimientoJustificacion = null;
}
