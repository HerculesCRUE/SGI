package org.crue.hercules.sgi.eti.model;

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

import org.crue.hercules.sgi.framework.validation.ExistsById;
import org.crue.hercules.sgi.framework.validation.FieldValueEquals;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * La entidad Checklist representa las respuestas al formulario preliminar para
 * la solicitud de una PeticionEvaluacion.
 */
@Entity
@Table(name = "checklist")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Checklist extends BaseEntity {
  public static final int PERSONA_REF_LENGTH = 250;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "checklist_seq")
  @SequenceGenerator(name = "checklist_seq", sequenceName = "checklist_seq", allocationSize = 1)
  private Long id;

  /** Referencia persona */
  @Column(name = "persona_ref", length = PERSONA_REF_LENGTH, nullable = false)
  private String personaRef;

  /** Formly del Checklist */
  @ManyToOne
  @JoinColumn(name = "formly_id", nullable = false, foreignKey = @ForeignKey(name = "FK_CHECKLIST_FORMLY"))
  @ExistsById(entityClass = Formly.class)
  @FieldValueEquals(entityClass = Formly.class, fieldName = "nombre", value = "CHECKLIST")
  private Formly formly;

  /** Respuestas del formulario definido en el Formly */
  @Column(name = "respuesta", nullable = false, columnDefinition = "clob")
  private String respuesta;

}