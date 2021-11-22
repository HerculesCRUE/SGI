package org.crue.hercules.sgi.rep.dto.eti;

import java.time.Instant;

import org.crue.hercules.sgi.rep.dto.BaseRestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RetrospectivaDto extends BaseRestDto {

  private Long id;
  private EstadoRetrospectivaDto estadoRetrospectiva;
  private Instant fechaRetrospectiva;

}
