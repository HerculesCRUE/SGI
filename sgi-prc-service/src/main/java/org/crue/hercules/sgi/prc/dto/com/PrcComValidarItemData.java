package org.crue.hercules.sgi.prc.dto.com;

import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;

import org.crue.hercules.sgi.framework.i18n.I18nFieldValue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrcComValidarItemData implements Serializable {
  private Collection<? extends I18nFieldValue> nombreEpigrafe;
  private String tituloItem;
  private Instant fechaItem;
}
