package org.crue.hercules.sgi.eti.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comite")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Comite extends BaseEntity {

  /**
   * Comité
   */
  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "id", length = 28, nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comite_seq")
  @SequenceGenerator(name = "comite_seq", sequenceName = "comite_seq", allocationSize = 1)
  private Long id;

  /** Comité */
  @Column(name = "comite", length = 50, nullable = false)
  private String comite;

  /** Nombre secretario */
  @Column(name = "nombre_secretario", length = 255, nullable = false)
  private String nombreSecretario;

  /** Nombre investigación */
  @Column(name = "nombre_investigacion", length = 255, nullable = false)
  private String nombreInvestigacion;

  /** Género Nombre investigación */
  @Column(name = "genero", length = 1, nullable = false)
  @Enumerated(EnumType.STRING)
  private Genero genero;

  /** Nombre decreto */
  @Column(name = "nombre_decreto", length = 255, nullable = false)
  private String nombreDecreto;

  /** Artículo */
  @Column(name = "articulo", length = 255, nullable = false)
  private String articulo;

  /** Formulario */
  @OneToOne
  @JoinColumn(name = "formulario_id", nullable = false, foreignKey = @ForeignKey(name = "FK_COMITE_FORMULARIO"))
  @NotNull
  private Formulario formulario;

  /** Activo */
  @Column(name = "activo", columnDefinition = "boolean default true", nullable = false)
  private Boolean activo;

  /** Género de nombre de investigación */
  public enum Genero {
    /** Femenino */
    F,
    /** Masculino */
    M;
  }
}