package org.crue.hercules.sgi.pii.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.crue.hercules.sgi.framework.validation.ActivableIsActivo;
import org.crue.hercules.sgi.pii.model.TipoProcedimiento.OnActivar;
import org.crue.hercules.sgi.pii.model.TipoProcedimiento.OnActualizar;
import org.crue.hercules.sgi.pii.model.TipoProcedimiento.OnCrear;
import org.crue.hercules.sgi.pii.validation.UniqueNombreTipoProcedimiento;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = TipoProcedimiento.TABLE_NAME)
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@ActivableIsActivo(entityClass = TipoProcedimiento.class, groups = { OnActualizar.class })
@UniqueNombreTipoProcedimiento(groups = { OnActualizar.class, OnActivar.class, OnCrear.class })
public class TipoProcedimiento extends BaseActivableEntity {

  protected static final String TABLE_NAME = "tipo_procedimiento";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";
  public static final int NOMBRE_LENGTH = 50;
  public static final int DESCRIPCION_LENGTH = 250;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
  @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  @Column(name = "nombre", length = TipoProcedimiento.NOMBRE_LENGTH, nullable = false)
  private String nombre;

  @Column(name = "descripcion", length = TipoProcedimiento.DESCRIPCION_LENGTH, nullable = true)
  private String descripcion;

  /**
   * Interfaz para marcar validaciones en la creación de la entidad.
   */
  public interface OnCrear {
  }

  /**
   * Interfaz para marcar validaciones en la actualizacion de la entidad.
   */
  public interface OnActualizar {
  }

  /**
   * Interfaz para marcar validaciones en las activaciones de la entidad.
   */
  public interface OnActivar {
  }
}
