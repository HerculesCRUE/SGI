package org.crue.hercules.sgi.eti.model;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.crue.hercules.sgi.framework.i18n.Language;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * La entidad Formly representa un formulario configurable.
 */
@Entity
@IdClass(FormlyDefinicionId.class)
@Table(name = "formly_definicion")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormlyDefinicion implements Serializable {
  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  public static final int NOMBRE_LENGTH = 50;

  /** Formly */
  @Id
  @NotNull
  @JsonIgnore()
  private Long formlyId;

  /** Language. */
  @Id
  @NotNull
  private Language lang;

  /** Esquema. */
  @Column(name = "esquema", nullable = false, columnDefinition = "clob")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private String esquema;

  @JsonRawValue
  public String getEsquema() {
    return esquema;
  }

  public void setEsquema(final String esquema) {
    this.esquema = esquema;
  }

  @JsonProperty(value = "esquema")
  public void setEsquemaRaw(JsonNode jsonNode) throws IOException {
    // this leads to non-standard json:

    if (jsonNode.isNull()) {
      setEsquema(null);
    } else {
      StringWriter stringWriter = new StringWriter();
      ObjectMapper objectMapper = new ObjectMapper();
      try (JsonGenerator generator = new JsonFactory(objectMapper).createGenerator(stringWriter)) {
        generator.writeTree(jsonNode);
        setEsquema(stringWriter.toString());
      }
    }
  }

  // Relation mappings for JPA metamodel generation only
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "formly_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_FORMLYDEFINICION_FORMLY"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  @EqualsAndHashCode.Exclude
  private final Formly formly = null;

}