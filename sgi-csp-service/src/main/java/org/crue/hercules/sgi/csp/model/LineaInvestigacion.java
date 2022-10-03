package org.crue.hercules.sgi.csp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.crue.hercules.sgi.csp.validation.UniqueNombreLineaInvestigacionActiva;
import org.crue.hercules.sgi.framework.validation.ActivableIsActivo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "linea_investigacion")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@SuperBuilder
@UniqueNombreLineaInvestigacionActiva(groups = { BaseEntity.Update.class, BaseEntity.Create.class,
    BaseActivableEntity.OnActivar.class })
@ActivableIsActivo(entityClass = LineaInvestigacion.class, groups = { BaseEntity.Update.class })
public class LineaInvestigacion extends BaseActivableEntity {
  public static final int NOMBRE_LENGTH = 1000;
  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "linea_investigacion_seq")
  @SequenceGenerator(name = "linea_investigacion_seq", sequenceName = "linea_investigacion_seq", allocationSize = 1)
  private Long id;

  /** Nombre */
  @Column(name = "nombre", length = NOMBRE_LENGTH, nullable = false)
  private String nombre;

}