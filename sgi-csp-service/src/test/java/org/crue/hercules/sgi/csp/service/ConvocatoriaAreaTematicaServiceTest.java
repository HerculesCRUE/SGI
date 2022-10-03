package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.AreaTematicaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaAreaTematicaNotFoundException;
import org.crue.hercules.sgi.csp.model.AreaTematica;
import org.crue.hercules.sgi.csp.model.ConvocatoriaAreaTematica;
import org.crue.hercules.sgi.csp.repository.AreaTematicaRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaAreaTematicaRepository;
import org.crue.hercules.sgi.csp.service.impl.ConvocatoriaAreaTematicaServiceImpl;
import org.crue.hercules.sgi.csp.util.ConvocatoriaAuthorityHelper;
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

class ConvocatoriaAreaTematicaServiceTest extends BaseServiceTest {

  @Mock
  private ConvocatoriaAreaTematicaRepository convocatoriaAreaTematicaRepository;

  @Mock
  private AreaTematicaRepository areaTematicaRepository;
  @Mock
  private ConvocatoriaService convocatoriaService;
  @Mock
  private ConvocatoriaAuthorityHelper authorityHelper;

  private ConvocatoriaAreaTematicaService service;

  @BeforeEach
  void setUp() throws Exception {
    service = new ConvocatoriaAreaTematicaServiceImpl(convocatoriaAreaTematicaRepository, areaTematicaRepository,
        convocatoriaService, authorityHelper);
  }

  @Test
  void create_ReturnsConvocatoriaAreaTematica() {
    // given: new ConvocatoriaAreaTematica
    Long convocatoriaId = 1L;
    ConvocatoriaAreaTematica newConvocatoriaAreaTematica = generarConvocatoriaAreaTematica(null, convocatoriaId, 1L);

    BDDMockito.given(convocatoriaService.isRegistradaConSolicitudesOProyectos(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<String[]>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(areaTematicaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(newConvocatoriaAreaTematica.getAreaTematica()));
    BDDMockito.given(convocatoriaAreaTematicaRepository
        .findByConvocatoriaIdAndAreaTematicaId(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong()))
        .willReturn(Optional.empty());

    BDDMockito.given(convocatoriaAreaTematicaRepository.save(ArgumentMatchers.<ConvocatoriaAreaTematica>any()))
        .willAnswer(new Answer<ConvocatoriaAreaTematica>() {
          @Override
          public ConvocatoriaAreaTematica answer(InvocationOnMock invocation) throws Throwable {
            ConvocatoriaAreaTematica givenData = invocation.getArgument(0, ConvocatoriaAreaTematica.class);
            ConvocatoriaAreaTematica newData = new ConvocatoriaAreaTematica();
            BeanUtils.copyProperties(givenData, newData);
            newData.setId(1L);
            return newData;
          }
        });

    // when: create ConvocatoriaAreaTematica
    ConvocatoriaAreaTematica createdConvocatoriaAreaTematica = service.create(newConvocatoriaAreaTematica);

    // then: new ConvocatoriaAreaTematica is created
    Assertions.assertThat(createdConvocatoriaAreaTematica).isNotNull();
    Assertions.assertThat(createdConvocatoriaAreaTematica.getId()).isNotNull();
    Assertions.assertThat(createdConvocatoriaAreaTematica.getConvocatoriaId())
        .isEqualTo(newConvocatoriaAreaTematica.getConvocatoriaId());
    Assertions.assertThat(createdConvocatoriaAreaTematica.getAreaTematica().getId())
        .isEqualTo(newConvocatoriaAreaTematica.getAreaTematica().getId());
    Assertions.assertThat(createdConvocatoriaAreaTematica.getObservaciones())
        .isEqualTo(newConvocatoriaAreaTematica.getObservaciones());
  }

  @Test
  void create_WithId_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaAreaTematica with id filled
    ConvocatoriaAreaTematica newConvocatoriaAreaTematica = generarConvocatoriaAreaTematica(1L, 1L, 1L);

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaAreaTematica
        () -> service.create(newConvocatoriaAreaTematica))
        // then: throw exception as id can't be provided
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id tiene que ser null para crear ConvocatoriaAreaTematica");
  }

  @Test
  void create_WithoutConvocatoriaId_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaAreaTematica without ConvocatoriaId
    ConvocatoriaAreaTematica newConvocatoriaAreaTematica = generarConvocatoriaAreaTematica(null, null, 1L);

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaAreaTematica
        () -> service.create(newConvocatoriaAreaTematica))
        // then: throw exception as ConvocatoriaId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id Convocatoria no puede ser null para crear ConvocatoriaAreaTematica");
  }

  @Test
  void create_WithoutAreaTematica_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaAreaTematica without AreaTematica
    ConvocatoriaAreaTematica newConvocatoriaAreaTematica = generarConvocatoriaAreaTematica(null, 1L, null);

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaAreaTematica
        () -> service.create(newConvocatoriaAreaTematica))
        // then: throw exception as AreaTematica is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id AreaTematica no puede ser null para crear ConvocatoriaAreaTematica");
  }

  @Test
  void create_WithNoExistingAreaTematica_404() {
    // given: a ConvocatoriaAreaTematica with non existing AreaTematica
    Long convocatoriaId = 1L;
    ConvocatoriaAreaTematica newConvocatoriaAreaTematica = generarConvocatoriaAreaTematica(null, convocatoriaId, 1L);

    BDDMockito.given(convocatoriaService.isRegistradaConSolicitudesOProyectos(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<String[]>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(areaTematicaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaAreaTematica
        () -> service.create(newConvocatoriaAreaTematica))
        // then: throw exception as AreaTematica is not found
        .isInstanceOf(AreaTematicaNotFoundException.class);
  }

  @Test
  void create_WithDuplicatedConvocatoriaIdAndAreaTematicaId_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaAreaTematica assigned with same
    // Convocatoria And EntidadRef
    Long convocatoriaId = 1L;
    ConvocatoriaAreaTematica newConvocatoriaAreaTematica = generarConvocatoriaAreaTematica(null, convocatoriaId, 1L);
    ConvocatoriaAreaTematica ConvocatoriaAreaTematicaExistente = generarConvocatoriaAreaTematica(1L, convocatoriaId,
        1L);

    BDDMockito.given(convocatoriaService.isRegistradaConSolicitudesOProyectos(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<String[]>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(areaTematicaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(newConvocatoriaAreaTematica.getAreaTematica()));
    BDDMockito.given(convocatoriaAreaTematicaRepository
        .findByConvocatoriaIdAndAreaTematicaId(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(ConvocatoriaAreaTematicaExistente));

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaAreaTematica
        () -> service.create(newConvocatoriaAreaTematica))
        // then: throw exception as assigned with same Convocatoria And EntidadRef
        // exists
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe una asociación activa para esa Convocatoria y AreaTematica");
  }

  @Test
  void create_WhenModificableReturnsFalse_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaAreaTematica when modificable returns False
    Long convocatoriaId = 1L;
    ConvocatoriaAreaTematica newConvocatoriaAreaTematica = generarConvocatoriaAreaTematica(null, convocatoriaId, 1L);

    BDDMockito.given(convocatoriaService.isRegistradaConSolicitudesOProyectos(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<String[]>any())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaAreaTematica
        () -> service.create(newConvocatoriaAreaTematica))
        // then: throw exception as Convocatoria is not modificable
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "No se puede crear ConvocatoriaAreaTematica. No tiene los permisos necesarios o la convocatoria está registrada y cuenta con solicitudes o proyectos asociados");
  }

  @Test
  void update_WithExistingId_ReturnsConvocatoriaAreaTematica() {
    // given: existing ConvocatoriaAreaTematica
    ConvocatoriaAreaTematica convocatoriaAreaTematica = generarConvocatoriaAreaTematica(1L, 1L, 1L);

    BDDMockito.given(convocatoriaAreaTematicaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaAreaTematica));
    BDDMockito.given(convocatoriaAreaTematicaRepository.save(ArgumentMatchers.<ConvocatoriaAreaTematica>any()))
        .willAnswer(new Answer<ConvocatoriaAreaTematica>() {
          @Override
          public ConvocatoriaAreaTematica answer(InvocationOnMock invocation) throws Throwable {
            ConvocatoriaAreaTematica givenData = invocation.getArgument(0, ConvocatoriaAreaTematica.class);
            givenData.setObservaciones("observaciones-modificadas");
            return givenData;
          }
        });

    // when: update ConvocatoriaAreaTematica
    ConvocatoriaAreaTematica updated = service.update(convocatoriaAreaTematica);

    // then: ConvocatoriaAreaTematica is updated
    Assertions.assertThat(updated).isNotNull();
    Assertions.assertThat(updated.getId()).isNotNull();
    Assertions.assertThat(updated.getId()).isEqualTo(convocatoriaAreaTematica.getId());
    Assertions.assertThat(updated.getConvocatoriaId()).isEqualTo(convocatoriaAreaTematica.getConvocatoriaId());
    Assertions.assertThat(updated.getAreaTematica().getId())
        .isEqualTo(convocatoriaAreaTematica.getAreaTematica().getId());
    Assertions.assertThat(updated.getObservaciones()).isEqualTo("observaciones-modificadas");
  }

  @Test
  void update_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    ConvocatoriaAreaTematica convocatoriaAreaTematica = generarConvocatoriaAreaTematica(1L, 1L, 1L);

    BDDMockito.given(convocatoriaAreaTematicaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update non existing ConvocatoriaAreaTematica
        () -> service.update(convocatoriaAreaTematica))
        // then: NotFoundException is thrown
        .isInstanceOf(ConvocatoriaAreaTematicaNotFoundException.class);
  }

  @Test
  void update_WithoutId_ThrowsIllegalArgumentException() {
    // given: a ConvocatoriaAreaTematica without id filled
    ConvocatoriaAreaTematica convocatoriaAreaTematica = generarConvocatoriaAreaTematica(null, 1L, 1L);

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaAreaTematica
        () -> service.update(convocatoriaAreaTematica))
        // then: throw exception as id must be provided
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ConvocatoriaAreaTematica id no puede ser null para actualizar un ConvocatoriaAreaTematica");
  }

  @Test
  void delete_WithExistingId_ReturnsConvocatoriaAreaTematica() {
    // given: existing ConvocatoriaAreaTematica
    Long id = 1L;

    BDDMockito.given(convocatoriaAreaTematicaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarConvocatoriaAreaTematica(1L, 1L, 1L)));
    BDDMockito.given(convocatoriaService.isRegistradaConSolicitudesOProyectos(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<String[]>any())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(convocatoriaAreaTematicaRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: delete by existing id
        () -> service.delete(id))
        // then: no exception is thrown
        .doesNotThrowAnyException();
  }

  @Test
  void delete_WithoutId_ThrowsIllegalArgumentException() throws Exception {
    // given: no id
    Long id = null;

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: IllegalArgumentException is thrown
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ConvocatoriaAreaTematica id no puede ser null para eliminar un ConvocatoriaAreaTematica");
  }

  @Test
  void delete_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    Long id = 1L;

    BDDMockito.given(convocatoriaAreaTematicaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: NotFoundException is thrown
        .isInstanceOf(ConvocatoriaAreaTematicaNotFoundException.class);
  }

  @Test
  void delete_WhenModificableReturnsFalse_ThrowsIllegalArgumentException() {
    // given: existing ConvocatoriaAreaTematica when modificable returns false
    Long id = 1L;

    BDDMockito.given(convocatoriaAreaTematicaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarConvocatoriaAreaTematica(1L, 1L, 1L)));
    BDDMockito.given(convocatoriaService.isRegistradaConSolicitudesOProyectos(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.<String>any(), ArgumentMatchers.<String[]>any())).willReturn(Boolean.FALSE);

    Assertions.assertThatCode(
        // when: delete by existing id
        () -> service.delete(id))
        // then: throw exception as Convocatoria is not modificable
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "No se puede eliminar ConvocatoriaAreaTematica. No tiene los permisos necesarios o la convocatoria está registrada y cuenta con solicitudes o proyectos asociados");
  }

  @Test
  void findById_WithExistingId_ReturnsConvocatoriaAreaTematica() throws Exception {
    // given: existing ConvocatoriaAreaTematica
    ConvocatoriaAreaTematica givenConvocatoriaAreaTematica = generarConvocatoriaAreaTematica(1L, 1L, 1L);

    BDDMockito.given(convocatoriaAreaTematicaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(givenConvocatoriaAreaTematica));

    // when: find by id ConvocatoriaAreaTematica
    ConvocatoriaAreaTematica convocatoriaAreaTematica = service.findById(givenConvocatoriaAreaTematica.getId());

    // then: returns ConvocatoriaAreaTematica
    Assertions.assertThat(convocatoriaAreaTematica).isNotNull();
    Assertions.assertThat(convocatoriaAreaTematica.getId()).isNotNull();
    Assertions.assertThat(convocatoriaAreaTematica.getId()).isEqualTo(convocatoriaAreaTematica.getId());
    Assertions.assertThat(convocatoriaAreaTematica.getConvocatoriaId())
        .isEqualTo(convocatoriaAreaTematica.getConvocatoriaId());
    Assertions.assertThat(convocatoriaAreaTematica.getAreaTematica().getId())
        .isEqualTo(convocatoriaAreaTematica.getAreaTematica().getId());
    Assertions.assertThat(convocatoriaAreaTematica.getObservaciones())
        .isEqualTo(convocatoriaAreaTematica.getObservaciones());
  }

  @Test
  void findById_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    Long id = 1L;
    BDDMockito.given(convocatoriaAreaTematicaRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: find by non existing id
        () -> service.findById(id))
        // then: NotFoundException is thrown
        .isInstanceOf(ConvocatoriaAreaTematicaNotFoundException.class);
  }

  @Test
  void findAllByConvocatoria_ReturnsPage() {
    // given: Una lista con 37 ConvocatoriaAreaTematica para la Convocatoria
    Long convocatoriaId = 1L;
    List<ConvocatoriaAreaTematica> convocatoriasAreasTematicas = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      convocatoriasAreasTematicas.add(generarConvocatoriaAreaTematica(i, convocatoriaId, i));
    }

    BDDMockito
        .given(convocatoriaAreaTematicaRepository
            .findAll(ArgumentMatchers.<Specification<ConvocatoriaAreaTematica>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ConvocatoriaAreaTematica>>() {
          @Override
          public Page<ConvocatoriaAreaTematica> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > convocatoriasAreasTematicas.size() ? convocatoriasAreasTematicas.size() : toIndex;
            List<ConvocatoriaAreaTematica> content = convocatoriasAreasTematicas.subList(fromIndex, toIndex);
            Page<ConvocatoriaAreaTematica> page = new PageImpl<>(content, pageable, convocatoriasAreasTematicas.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ConvocatoriaAreaTematica> page = service.findAllByConvocatoria(convocatoriaId, null, paging);

    // then: Devuelve la pagina 3 con los ConvocatoriaAreaTematica del 31 al 37
    Assertions.assertThat(page.getContent()).as("getContent().size()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ConvocatoriaAreaTematica ConvocatoriaAreaTematica = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(ConvocatoriaAreaTematica.getId()).isEqualTo(Long.valueOf(i));
      Assertions.assertThat(ConvocatoriaAreaTematica.getConvocatoriaId()).isEqualTo(convocatoriaId);
      Assertions.assertThat(ConvocatoriaAreaTematica.getAreaTematica().getId()).isEqualTo(i);
      Assertions.assertThat(ConvocatoriaAreaTematica.getObservaciones()).isEqualTo("observaciones-" + i);
    }
  }

  /**
   * Función que devuelve un objeto ConvocatoriaAreaTematica
   * 
   * @param convocatoriaAreaTematicaId
   * @param convocatoriaId
   * @param areaTematicaId
   * @return el objeto ConvocatoriaAreaTematica
   */
  private ConvocatoriaAreaTematica generarConvocatoriaAreaTematica(Long convocatoriaAreaTematicaId, Long convocatoriaId,
      Long areaTematicaId) {

    return ConvocatoriaAreaTematica.builder().id(convocatoriaAreaTematicaId).convocatoriaId(convocatoriaId)
        .areaTematica(AreaTematica.builder().id(areaTematicaId).build())
        .observaciones("observaciones-" + convocatoriaAreaTematicaId).build();
  }
}
