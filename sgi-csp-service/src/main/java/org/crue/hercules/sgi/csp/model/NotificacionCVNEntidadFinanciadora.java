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
@Table(name = NotificacionCVNEntidadFinanciadora.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacionCVNEntidadFinanciadora extends BaseEntity {

  protected static final String TABLE_NAME = "notificacion_cvn_entidad_financiadora";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";
  public static final int MAX_LENGTH = 250;

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;
  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = NotificacionCVNEntidadFinanciadora.SEQUENCE_NAME)
  @SequenceGenerator(name = NotificacionCVNEntidadFinanciadora.SEQUENCE_NAME, sequenceName = NotificacionCVNEntidadFinanciadora.SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** Datos Entidad Fianciadora */
  @Column(name = "datos_entidad_financiadora", length = NotificacionCVNEntidadFinanciadora.MAX_LENGTH, nullable = true)
  @Size(max = NotificacionCVNEntidadFinanciadora.MAX_LENGTH)
  private String datosEntidadFinanciadora;

  /** Entidad Financiadora Ref */
  @Column(name = "entidad_financiadora_ref", nullable = true)
  private String entidadFinanciadoraRef;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "autorizacion_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_ESTADOAUTORIZACION_NOTIFICACIONPROYECTOEXTERNOCVN"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private static final NotificacionProyectoExternoCVN notificacionProyecto = null;
}
