package org.crue.hercules.sgi.com.dto;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailParam implements Serializable {
  /** Serial version */
  private static final long serialVersionUID = 1L;

  /** Name */
  private String name;

  /** Value */
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private String value;

  public String getValue() {
    return value;
  }

  public void setValue(final String value) {
    this.value = value;
  }

  @JsonProperty(value = "value")
  public void setValueRaw(JsonNode jsonNode) throws IOException {
    // this leads to non-standard json:

    if (jsonNode.isNull()) {
      setValue(null);
    } else {
      if (jsonNode.getNodeType() == JsonNodeType.STRING) {
        setValue(jsonNode.asText());
      } else {
        StringWriter stringWriter = new StringWriter();
        ObjectMapper objectMapper = new ObjectMapper();
        try (JsonGenerator generator = new JsonFactory(objectMapper).createGenerator(stringWriter)) {
          generator.writeTree(jsonNode);
          setValue(stringWriter.toString());
        }
      }
    }
  }
}
