package org.crue.hercules.sgi.csp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.crue.hercules.sgi.csp.validation.UniqueNombreTipoFacturacionActivo;
import org.crue.hercules.sgi.framework.validation.ActivableIsActivo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = TipoFacturacion.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@UniqueNombreTipoFacturacionActivo(groups = { TipoFacturacion.OnActualizar.class, BaseActivableEntity.OnActivar.class,
    TipoFacturacion.OnCrear.class })
@ActivableIsActivo(entityClass = TipoFacturacion.class, groups = { TipoFacturacion.OnActualizar.class })
public class TipoFacturacion extends BaseActivableEntity {

  protected static final String TABLE_NAME = "tipo_facturacion";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  public static final int NOMBRE_MAX_LENGTH = 45;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
  @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  @Column(name = "nombre", length = NOMBRE_MAX_LENGTH, nullable = false)
  private String nombre;

  @Column(name = "incluir_en_comunicado", columnDefinition = "boolean default false", nullable = false)
  private boolean incluirEnComunicado;

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

}
