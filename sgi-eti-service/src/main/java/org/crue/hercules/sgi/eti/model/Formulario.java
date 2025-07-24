package org.crue.hercules.sgi.eti.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Formulario
 */

@Entity
@Table(name = "formulario")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Formulario extends BaseEntity {

  public enum Tipo {
    /** Memoria */
    MEMORIA,
    /** Seguimiento Anual */
    SEGUIMIENTO_ANUAL,
    /** Seguimiento Final */
    SEGUIMIENTO_FINAL,
    /** Retrospectiva */
    RETROSPECTIVA;
  }

  /** Titulo de la documentación de Seguimiento Anual */
  public enum SeguimientoAnualDocumentacionTitle {
    /** Memoria */
    TITULO_1,
    /** Seguimiento Anual */
    TITULO_2,
    /** Seguimiento Final */
    TITULO_3;
  }

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id. */
  @Id
  @Column(name = "id", length = 28, nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "formulario_seq")
  @SequenceGenerator(name = "formulario_seq", sequenceName = "formulario_seq", allocationSize = 1)
  private Long id;

  /** Tipo */
  @Column(name = "tipo", nullable = false)
  @Enumerated(EnumType.STRING)
  private Tipo tipo;

  /** Codigo */
  @Column(name = "codigo", nullable = false, unique = true, length = 50)
  private String codigo;

  /** Titulo de la documentación del seguimiento anual */
  @Column(name = "seguimiento_anual_documentacion_title", nullable = false)
  @Enumerated(EnumType.STRING)
  private SeguimientoAnualDocumentacionTitle seguimientoAnualDocumentacionTitle;

}