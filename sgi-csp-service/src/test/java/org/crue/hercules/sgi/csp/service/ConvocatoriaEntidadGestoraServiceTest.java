package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaEntidadGestoraNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadGestora;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaEntidadGestoraRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.service.impl.ConvocatoriaEntidadGestoraServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public class ConvocatoriaEntidadGestoraServiceTest extends BaseServiceTest {

  @Mock
  private ConvocatoriaEntidadGestoraRepository convocatoriaEntidadGestoraRepository;
  @Mock
  private ConvocatoriaRepository convocatoriaRepository;

  private ConvocatoriaEntidadGestoraService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ConvocatoriaEntidadGestoraServiceImpl(convocatoriaEntidadGestoraRepository, convocatoriaRepository);
  }

  @Test
  public void create_ReturnsConvocatoriaEntidadGestora() {
    // given: new ConvocatoriaEntidadGestora
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    ConvocatoriaEntidadGestora newConvocatoriaEntidadGestora = generarConvocatoriaEntidadGestora(null, convocatoriaId,
        "entidad-001");

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(convocatoriaEntidadGestoraRepository.findByConvocatoriaIdAndEntidadRef(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyString())).willReturn(Optional.empty());

    BDDMockito.given(convocatoriaEntidadGestoraRepository.save(ArgumentMatchers.<ConvocatoriaEntidadGestora>any()))
        .willAnswer(new Answer<ConvocatoriaEntidadGestora>() {
          @Override
          public ConvocatoriaEntidadGestora answer(InvocationOnMock invocation) throws Throwable {
            ConvocatoriaEntidadGestora givenData = invocation.getArgument(0, ConvocatoriaEntidadGestora.class);
            ConvocatoriaEntidadGestora newData = new ConvocatoriaEntidadGestora();
            BeanUtils.copyProperties(givenData, newData);
            newData.setId(1L);
            return newData;
          }
        });

    // when: create ConvocatoriaEntidadGestora
    ConvocatoriaEntidadGestora createdConvocatoriaEntidadGestora = service.create(newConvocatoriaEntidadGestora);

    // then: new ConvocatoriaEntidadGestora is created
    Assertions.assertThat(createdConvocatoriaEntidadGestora).isNotNull();
    Assertions.assertThat(createdConvocatoriaEntidadGestora.getId()).isNotNull();
    Assertions.assertThat(createdConvocatoriaEntidadGestora.getConvocatoriaId())
        .isEqualTo(newConvocatoriaEntidadGestora.getConvocatoriaId());
    Assertions.assertThat(createdConvocatoriaEntidadGestora.getEntidadRef())
        .isEqualTo(newConvocatoriaEntidadGestora.getEntidadRef());
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaEntidadGestora with id filled
    ConvocatoriaEntidadGestora newConvocatoriaEntidadGestora = generarConvocatoriaEntidadGestora(1L, 1L, "entidad-001");

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaEntidadGestora
        () -> service.create(newConvocatoriaEntidadGestora))
        // then: throw exception as id can't be provided
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void create_WithoutConvocatoriaId_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaEntidadGestora without ConvocatoriaId
    ConvocatoriaEntidadGestora newConvocatoriaEntidadGestora = generarConvocatoriaEntidadGestora(null, null,
        "entidad-001");

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaEntidadGestora
        () -> service.create(newConvocatoriaEntidadGestora))
        // then: throw exception as ConvocatoriaId is null
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void create_WithoutEntidadRef_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaEntidadGestora without EntidadRef
    ConvocatoriaEntidadGestora newConvocatoriaEntidadGestora = generarConvocatoriaEntidadGestora(null, 1L, null);

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaEntidadGestora
        () -> service.create(newConvocatoriaEntidadGestora))
        // then: throw exception as EntidadRef is null
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void create_WithNoExistingConvocatoria_404() {
    // given: a ConvocatoriaEntidadGestora with non existing Convocatoria
    ConvocatoriaEntidadGestora newConvocatoriaEntidadGestora = generarConvocatoriaEntidadGestora(null, 1L,
        "entidad-001");

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaEntidadGestora
        () -> service.create(newConvocatoriaEntidadGestora))
        // then: throw exception as Convocatoria is not found
        .isInstanceOf(ConvocatoriaNotFoundException.class);
  }

  @Test
  public void create_WithDuplicatedConvocatoriaIdAndEntidadRef_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaEntidadGestora assigned with same
    // Convocatoria And EntidadRef
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    ConvocatoriaEntidadGestora newConvocatoriaEntidadGestora = generarConvocatoriaEntidadGestora(null, convocatoriaId,
        "entidad-001");
    ConvocatoriaEntidadGestora ConvocatoriaEntidadGestoraExistente = generarConvocatoriaEntidadGestora(1L,
        convocatoriaId, "entidad-001");

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));
    BDDMockito.given(convocatoriaEntidadGestoraRepository.findByConvocatoriaIdAndEntidadRef(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyString())).willReturn(Optional.of(ConvocatoriaEntidadGestoraExistente));

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaEntidadGestora
        () -> service.create(newConvocatoriaEntidadGestora))
        // then: throw exception as assigned with same Convocatoria And EntidadRef
        // exists
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithExistingId_ReturnsConvocatoriaEntidadGestora() {
    // given: existing ConvocatoriaEntidadGestora
    Long id = 1L;

    BDDMockito.given(convocatoriaEntidadGestoraRepository.existsById(ArgumentMatchers.anyLong()))
        .willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(convocatoriaEntidadGestoraRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: delete by existing id
        () -> service.delete(id))
        // then: no exception is thrown
        .doesNotThrowAnyException();
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() throws Exception {
    // given: no id
    Long id = null;

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: IllegalArgumentException is thrown
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    Long id = 1L;

    BDDMockito.given(convocatoriaEntidadGestoraRepository.existsById(ArgumentMatchers.anyLong()))
        .willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: NotFoundException is thrown
        .isInstanceOf(ConvocatoriaEntidadGestoraNotFoundException.class);
  }

  @Test
  public void findAllByConvocatoria_ReturnsPage() {
    // given: Una lista con 37 ConvocatoriaEntidadGestora para la Convocatoria
    Long convocatoriaId = 1L;
    List<ConvocatoriaEntidadGestora> convocatoriasEntidadesGestoras = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      convocatoriasEntidadesGestoras.add(generarConvocatoriaEntidadGestora(i, convocatoriaId, "entidad-" + i));
    }

    BDDMockito
        .given(convocatoriaEntidadGestoraRepository.findAll(
            ArgumentMatchers.<Specification<ConvocatoriaEntidadGestora>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ConvocatoriaEntidadGestora>>() {
          @Override
          public Page<ConvocatoriaEntidadGestora> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > convocatoriasEntidadesGestoras.size() ? convocatoriasEntidadesGestoras.size() : toIndex;
            List<ConvocatoriaEntidadGestora> content = convocatoriasEntidadesGestoras.subList(fromIndex, toIndex);
            Page<ConvocatoriaEntidadGestora> page = new PageImpl<>(content, pageable,
                convocatoriasEntidadesGestoras.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ConvocatoriaEntidadGestora> page = service.findAllByConvocatoria(convocatoriaId, null, paging);

    // then: Devuelve la pagina 3 con los ConvocatoriaEntidadGestora del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ConvocatoriaEntidadGestora convocatoriaEntidadGestora = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(convocatoriaEntidadGestora.getId()).isEqualTo(Long.valueOf(i));
      Assertions.assertThat(convocatoriaEntidadGestora.getConvocatoriaId()).isEqualTo(convocatoriaId);
      Assertions.assertThat(convocatoriaEntidadGestora.getEntidadRef()).isEqualTo("entidad-" + i);
    }
  }

  private Convocatoria generarMockConvocatoria(Long convocatoriaId) {
    return Convocatoria.builder().id(convocatoriaId).build();
  }

  /**
   * Función que devuelve un objeto ConvocatoriaEntidadGestora
   * 
   * @param convocatoriaEntidadGestoraId
   * @param convocatoriaId
   * @param entidadRef
   * @return el objeto ConvocatoriaEntidadGestora
   */
  private ConvocatoriaEntidadGestora generarConvocatoriaEntidadGestora(Long convocatoriaEntidadGestoraId,
      Long convocatoriaId, String entidadRef) {

    return ConvocatoriaEntidadGestora.builder().id(convocatoriaEntidadGestoraId).convocatoriaId(convocatoriaId)
        .entidadRef(entidadRef).build();

  }
}
