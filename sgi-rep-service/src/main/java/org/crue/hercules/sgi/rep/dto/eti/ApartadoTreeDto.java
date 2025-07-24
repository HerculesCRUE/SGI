package org.crue.hercules.sgi.rep.dto.eti;

import java.util.List;

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
public class ApartadoTreeDto extends BaseRestDto {

  private Long bloqueId;
  private Long padreId;
  private Integer orden;
  private List<ApartadoDefinicionDto> definicion;
  private List<ApartadoTreeDto> hijos;
}