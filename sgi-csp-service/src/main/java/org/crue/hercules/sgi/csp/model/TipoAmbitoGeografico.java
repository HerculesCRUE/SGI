package org.crue.hercules.sgi.csp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.validation.UniqueNombreTipoAmbitoGeograficoActivo;
import org.crue.hercules.sgi.framework.validation.ActivableIsActivo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tipo_ambito_geografico")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@SuperBuilder
@UniqueNombreTipoAmbitoGeograficoActivo(groups = { TipoAmbitoGeografico.OnActualizar.class,
    BaseActivableEntity.OnActivar.class,
    TipoAmbitoGeografico.OnCrear.class })
@ActivableIsActivo(entityClass = TipoAmbitoGeografico.class, groups = { TipoAmbitoGeografico.OnActualizar.class })
public class TipoAmbitoGeografico extends BaseActivableEntity {

  public static final int NOMBRE_LENGTH = 50;

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tipo_ambito_geografico_seq")
  @SequenceGenerator(name = "tipo_ambito_geografico_seq", sequenceName = "tipo_ambito_geografico_seq", allocationSize = 1)
  private Long id;

  /** Nombre */
  @Column(name = "nombre", length = NOMBRE_LENGTH, nullable = false)
  @NotEmpty
  @Size(max = 50)
  private String nombre;

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