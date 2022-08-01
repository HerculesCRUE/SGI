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
@Table(name = SolicitudRrhhRequisitoNivelAcademico.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudRrhhRequisitoNivelAcademico extends BaseEntity {

  protected static final String TABLE_NAME = "solicitud_rrhh_requisito_nivel_academico";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  public static final int ENTIDAD_REF_LENGTH = 50;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SolicitudRrhhRequisitoNivelAcademico.SEQUENCE_NAME)
  @SequenceGenerator(name = SolicitudRrhhRequisitoNivelAcademico.SEQUENCE_NAME, sequenceName = SolicitudRrhhRequisitoNivelAcademico.SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** SolicitudRRHH Id */
  @Column(name = "solicitud_rrhh_id", nullable = false)
  @NotNull
  private Long solicitudRrhhId;

  /** RequisitoIPNivelAcademico Id */
  @Column(name = "requisitoip_nivelacademico_id", nullable = false)
  @NotNull
  private Long requisitoIpNivelAcademicoId;

  /** Documento Ref */
  @Column(name = "documento_ref", length = SolicitudRrhhRequisitoCategoria.ENTIDAD_REF_LENGTH, nullable = true)
  @Size(max = SolicitudRrhhRequisitoCategoria.ENTIDAD_REF_LENGTH)
  private String documentoRef;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "solicitud_rrhh_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_SOLICITUDRRHHREQUISITONIVELACADEMICO_SOLICITUDRRHH"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final SolicitudRrhh solicitudrRrhh = null;

  @ManyToOne
  @JoinColumn(name = "requisitoip_nivelacademico_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_SOLICITUDRRHHREQUISITONIVELACADEMICO_REQUISITOIPNIVELACADEMICO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final RequisitoIPNivelAcademico requisitoIPNivelAcademico = null;

}
