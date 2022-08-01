package org.crue.hercules.sgi.pii.model;

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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = PaisValidado.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaisValidado extends BaseEntity {

  protected static final String TABLE_NAME = "pais_validado";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  public static final int REF_LENGTH = 50;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
  @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** Solicitud de Protección Id */
  @Column(name = "solicitud_proteccion_id", nullable = false)
  private Long solicitudProteccionId;

  /** Código de la Invención */
  @Column(name = "codigo_invencion", nullable = false, length = REF_LENGTH)
  private String codigoInvencion;

  /** País referenciado */
  @Column(name = "pais_ref", nullable = false, length = REF_LENGTH)
  private String paisRef;

  /** Fecha Validación. */
  @Column(name = "fecha_validacion", nullable = false)
  private Instant fechaValidacion;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "solicitud_proteccion_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PAISVALIDADO_SOLICITUDPROTECCION"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final SolicitudProteccion solicitudProteccion = null;

}
