package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ConceptoGastoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaConceptoGastoNotFoundException;
import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto;
import org.crue.hercules.sgi.csp.repository.ConceptoGastoRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaConceptoGastoCodigoEcRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaConceptoGastoRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.service.impl.ConvocatoriaConceptoGastoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * ConvocatoriaConceptoGastoServiceTest
 */
public class ConvocatoriaConceptoGastoServiceTest extends BaseServiceTest {

  @Mock
  private ConvocatoriaConceptoGastoRepository repository;
  @Mock
  private ConvocatoriaRepository convocatoriaRepository;
  @Mock
  private ConceptoGastoRepository conceptoGastoRepository;
  @Mock
  private ConvocatoriaConceptoGastoService service;
  @Mock
  private ConvocatoriaConceptoGastoCodigoEcRepository convocatoriaConceptoGastoCodigoEcRepository;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ConvocatoriaConceptoGastoServiceImpl(repository, convocatoriaRepository, conceptoGastoRepository,
        convocatoriaConceptoGastoCodigoEcRepository);
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo ConvocatoriaConceptoGasto que ya tiene id
    ConvocatoriaConceptoGasto newConvocatoriaConceptoGasto = generarMockConvocatoriaConceptoGasto(1L);

    // when: Creamos el ConvocatoriaConceptoGasto
    // then: Lanza una excepcion porque el ConvocatoriaConceptoGasto ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(newConvocatoriaConceptoGasto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id tiene que ser null para crear ConvocatoriaConceptoGasto");
  }

  @Test
  public void create_WithoutConvocatoria_ThrowsIllegalArgumentException() {
    // given: Un nuevo ConvocatoriaConceptoGasto sin convocatoria
    ConvocatoriaConceptoGasto convocatoriaConceptoGasto = generarMockConvocatoriaConceptoGasto(null);
    convocatoriaConceptoGasto.setConvocatoriaId(null);

    // when: Creamos el ConvocatoriaConceptoGasto
    // then: Lanza una excepcion porque la convocatoria es null
    Assertions.assertThatThrownBy(() -> service.create(convocatoriaConceptoGasto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id Convocatoria no puede ser null para crear ConvocatoriaConceptoGasto");
  }

  @Test
  public void create_WithConceptoGastoInactivo_ThrowsIllegalArgumentException() {
    // given: Un nuevo ConvocatoriaConceptoGasto con el ConceptoGasto inactivo
    ConvocatoriaConceptoGasto convocatoriaConceptoGasto = generarMockConvocatoriaConceptoGasto(null);
    convocatoriaConceptoGasto.getConceptoGasto().setActivo(false);

    BDDMockito.given(conceptoGastoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(convocatoriaConceptoGasto.getConceptoGasto()));

    // when: Creamos el ConvocatoriaEnlace
    // then: Lanza una excepcion porque el enlace está inactivo
    Assertions.assertThatThrownBy(() -> service.create(convocatoriaConceptoGasto))
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El ConceptoGasto debe estar activo");
  }

  @Test
  public void update_WithConceptoGastoIdNoExists_ThrowsConceptoGastoNotFoundException() {
    // given: Un nuevo ConvocatoriaConceptoGasto sin tipo de enlace
    ConvocatoriaConceptoGasto convocatoriaConceptoGastoActualizar = generarMockConvocatoriaConceptoGasto(1L);

    BDDMockito.given(conceptoGastoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());
    // when: Creamos el ConvocatoriaConceptoGasto
    // then: Lanza una excepcion porque el concepto gasto es null
    Assertions.assertThatThrownBy(() -> service.update(convocatoriaConceptoGastoActualizar))
        .isInstanceOf(ConceptoGastoNotFoundException.class).hasMessage("ConceptoGasto 1 does not exist.");
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

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: NotFoundException is thrown
        .isInstanceOf(ConvocatoriaConceptoGastoNotFoundException.class);
  }

  @Test
  public void findById_ReturnsConvocatoriaConceptoGasto() {
    // given: Un ConvocatoriaConceptoGasto con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado))
        .willReturn(Optional.of(generarMockConvocatoriaConceptoGasto(idBuscado)));

    // when: Buscamos el ConvocatoriaConceptoGasto por su id
    ConvocatoriaConceptoGasto convocatoriaConceptoGasto = service.findById(idBuscado);

    // then: el ConvocatoriaConceptoGasto
    Assertions.assertThat(convocatoriaConceptoGasto).as("isNotNull()").isNotNull();
    Assertions.assertThat(convocatoriaConceptoGasto.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(convocatoriaConceptoGasto.getObservaciones()).as("getObservaciones()").isEqualTo("Obs-1");
    Assertions.assertThat(convocatoriaConceptoGasto.getConvocatoriaId()).as("getConvocatoria()").isEqualTo(1L);

  }

  @Test
  public void findById_WithIdNotExist_ThrowsConvocatoriaConceptoGastoNotFoundException() throws Exception {
    // given: Ningun ConvocatoriaConceptoGasto con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el ConvocatoriaConceptoGasto por su id
    // then: lanza un ConvocatoriaConceptoGastoNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(ConvocatoriaConceptoGastoNotFoundException.class);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred ConvocatoriaConceptoGasto
    List<ConvocatoriaConceptoGasto> conceptosGasto = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      conceptosGasto.add(generarMockConvocatoriaConceptoGasto(Long.valueOf(i)));
    }

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ConvocatoriaConceptoGasto>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<ConvocatoriaConceptoGasto>>() {
          @Override
          public Page<ConvocatoriaConceptoGasto> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<ConvocatoriaConceptoGasto> content = conceptosGasto.subList(fromIndex, toIndex);
            Page<ConvocatoriaConceptoGasto> page = new PageImpl<>(content, pageable, conceptosGasto.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ConvocatoriaConceptoGasto> page = service.findAll(null, paging);

    // then: A Page with ten ConvocatoriaConceptoGasto are returned
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      ConvocatoriaConceptoGasto item = page.getContent().get(i);
      Assertions.assertThat(item.getId()).isEqualTo(j);
    }
  }

  /**
   * Función que devuelve un objeto ConvocatoriaConceptoGasto
   * 
   * @param id     id del ConvocatoriaConceptoGasto
   * @param nombre nombre del ConvocatoriaConceptoGasto
   * @return el objeto ConvocatoriaConceptoGasto
   */
  private ConvocatoriaConceptoGasto generarMockConvocatoriaConceptoGasto(Long id) {
    ConceptoGasto conceptoGasto = new ConceptoGasto();
    conceptoGasto.setId(id == null ? 1 : id);

    conceptoGasto.setActivo(true);

    ConvocatoriaConceptoGasto convocatoriaConceptoGasto = new ConvocatoriaConceptoGasto();
    convocatoriaConceptoGasto.setId(id);
    convocatoriaConceptoGasto.setConvocatoriaId(id == null ? 1 : id);
    convocatoriaConceptoGasto.setObservaciones("Obs-" + id);
    convocatoriaConceptoGasto.setConceptoGasto(conceptoGasto);
    convocatoriaConceptoGasto.setImporteMaximo(400.0);
    convocatoriaConceptoGasto.setMesInicial(4);
    convocatoriaConceptoGasto.setMesFinal(5);
    convocatoriaConceptoGasto.setPermitido(true);
    convocatoriaConceptoGasto.setPorcentajeCosteIndirecto(3);

    return convocatoriaConceptoGasto;
  }

}
