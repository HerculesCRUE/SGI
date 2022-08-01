package org.crue.hercules.sgi.csp.converter;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.dto.GrupoPalabraClaveInput;
import org.crue.hercules.sgi.csp.dto.GrupoPalabraClaveOutput;
import org.crue.hercules.sgi.csp.model.GrupoPalabraClave;
import org.crue.hercules.sgi.csp.service.BaseServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

class GrupoPalabraClaveConverterTest extends BaseServiceTest {

  @Mock
  private ModelMapper modelMapper;

  private GrupoPalabraClaveConverter grupoPalabraClaveConverter;

  @BeforeEach
  void setup() {
    this.grupoPalabraClaveConverter = new GrupoPalabraClaveConverter(this.modelMapper);
  }

  @Test
  void convertGrupoPalabrasClaveInput_ReturnsGrupoPalabraClaveList() {
    final String palabraClaveRef = "testing-ref";
    final GrupoPalabraClaveInput input = this.buildMockGrupoPalabraClaveInput(palabraClaveRef);
    final GrupoPalabraClave entity = this.buildMockGrupoPalabraClave(1L, 1L, palabraClaveRef);

    final List<GrupoPalabraClaveInput> inputList = Arrays.asList(input);

    BDDMockito.given(this.modelMapper.map(input, GrupoPalabraClave.class)).willReturn(entity);

    List<GrupoPalabraClave> result = this.grupoPalabraClaveConverter.convertGrupoPalabrasClaveInput(inputList);

    Assertions.assertThat(result).isNotNull().isNotEmpty();
    Assertions.assertThat(result).hasSize(1);
    Assertions.assertThat(result.get(0).getId()).isNull();
  }

  @Test
  void convert_ReturnsGrupoPalabraClaveOutputPage() {
    final String palabraClaveRef = "testing-ref";
    final Long id = 1L;
    final GrupoPalabraClave entity = this.buildMockGrupoPalabraClave(id, 1L, palabraClaveRef);
    final GrupoPalabraClaveOutput output = this.buildMockGrupoPalabraClaveOutput(id, palabraClaveRef);

    final List<GrupoPalabraClave> entities = Arrays.asList(entity);
    final Page<GrupoPalabraClave> page = new PageImpl<>(entities);

    BDDMockito.given(modelMapper.map(entity, GrupoPalabraClaveOutput.class)).willReturn(output);

    Page<GrupoPalabraClaveOutput> result = this.grupoPalabraClaveConverter.convert(page);

    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.getContent()).isNotEmpty();
    Assertions.assertThat(result.getContent().get(0).getId()).isEqualTo(id);
    Assertions.assertThat(result.getContent().get(0).getPalabraClaveRef()).isEqualTo(palabraClaveRef);

  }

  private GrupoPalabraClaveInput buildMockGrupoPalabraClaveInput(String palabraClaveRef) {
    return GrupoPalabraClaveInput.builder()
        .palabraClaveRef(palabraClaveRef)
        .build();
  }

  private GrupoPalabraClave buildMockGrupoPalabraClave(Long id, Long grupoId, String palabraClaveRef) {
    return GrupoPalabraClave.builder()
        .id(id)
        .palabraClaveRef(palabraClaveRef)
        .grupoId(grupoId)
        .build();
  }

  private GrupoPalabraClaveOutput buildMockGrupoPalabraClaveOutput(Long id, String palabraClaveRef) {
    return GrupoPalabraClaveOutput.builder()
        .id(id)
        .palabraClaveRef(palabraClaveRef)
        .build();
  }

}