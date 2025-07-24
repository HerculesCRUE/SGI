package org.crue.hercules.sgi.csp.model;

import java.util.Set;
import java.util.HashSet;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

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
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name="tipo_ambito_geografico_nombre", joinColumns= @JoinColumn(name="tipo_ambito_geografico_id"))
  @NotEmpty
  @Valid
  private Set<TipoAmbitoGeograficoNombre> nombre = new HashSet<>();

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