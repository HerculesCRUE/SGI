package org.crue.hercules.sgi.eti.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.RespuestaNotFoundException;
import org.crue.hercules.sgi.eti.model.Apartado;
import org.crue.hercules.sgi.eti.model.Bloque;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.Respuesta;
import org.crue.hercules.sgi.eti.repository.ApartadoRepository;
import org.crue.hercules.sgi.eti.repository.BloqueRepository;
import org.crue.hercules.sgi.eti.repository.RespuestaRepository;
import org.crue.hercules.sgi.eti.service.impl.RespuestaServiceImpl;
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
 * RespuestaServiceTest
 */
public class RespuestaServiceTest extends BaseServiceTest {

  @Mock
  private RespuestaRepository respuestaRepository;
  @Mock
  private BloqueRepository bloqueRepository;
  @Mock
  private MemoriaService memoriaService;
  @Mock
  private RetrospectivaService retrospectivaService;
  @Mock
  private ApartadoRepository apartadoRepository;

  private RespuestaService respuestaService;

  @BeforeEach
  public void setUp() throws Exception {
    respuestaService = new RespuestaServiceImpl(respuestaRepository, bloqueRepository, memoriaService,
        retrospectivaService, apartadoRepository);
  }

  @Test
  public void find_WithId_ReturnsRespuesta() {
    BDDMockito.given(respuestaRepository.findById(1L)).willReturn(Optional.of(generarMockRespuesta(1L)));

    Respuesta respuesta = respuestaService.findById(1L);

    Assertions.assertThat(respuesta.getId()).isEqualTo(1L);
    Assertions.assertThat(respuesta.getMemoria().getId()).isEqualTo(1L);
    Assertions.assertThat(respuesta.getApartado().getEsquema()).isEqualTo("{\"nombre\":\"EsquemaApartado01\"}");
    Assertions.assertThat(respuesta.getValor()).isEqualTo("{\"valor\":\"Valor1\"}");

  }

  @Test
  public void find_NotFound_ThrowsRespuestaNotFoundException() throws Exception {
    BDDMockito.given(respuestaRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> respuestaService.findById(1L)).isInstanceOf(RespuestaNotFoundException.class);
  }

  @Test
  public void create_ReturnsRespuesta() {
    // given: Un nuevo Respuesta
    Respuesta respuestaNew = generarMockRespuesta(null);

    Respuesta respuesta = generarMockRespuesta(1L);
    respuesta.setValor("{\"valor\":\"ValorNew\"}");

    Memoria memoria = generarMockMemoria(1L);

    BDDMockito.given(respuestaRepository.save(respuestaNew)).willReturn(respuesta);
    BDDMockito.given(memoriaService.findById(1L)).willReturn(memoria);
    BDDMockito.given(apartadoRepository.findById(1L)).willReturn(Optional.of(getMockApartado(1L, 1L, 1L)));
    BDDMockito.given(bloqueRepository.findFirstByFormularioIdOrderByOrdenDesc(1L))
        .willReturn(generarMockBloque(1L, memoria.getComite().getFormulario()));
    List<Apartado> ultimosApartados = new ArrayList<Apartado>();
    ultimosApartados.add(getMockApartado(1L, 1L, 1L));
    BDDMockito.given(apartadoRepository.findFirst2ByBloqueIdOrderByOrdenDesc(1L)).willReturn(ultimosApartados);

    // when: Creamos el Respuesta
    Respuesta respuestaCreado = respuestaService.create(respuestaNew);

    // then: El Respuesta se crea correctamente
    Assertions.assertThat(respuestaCreado).isNotNull();
    Assertions.assertThat(respuestaCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(respuestaCreado.getValor()).isEqualTo("{\"valor\":\"ValorNew\"}");
  }

  @Test
  public void create_RespuestaWithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo Respuesta que ya tiene id
    Respuesta respuestaNew = generarMockRespuesta(1L);
    // when: Creamos el Respuesta
    // then: Lanza una excepcion porque el Respuesta ya tiene id
    Assertions.assertThatThrownBy(() -> respuestaService.create(respuestaNew))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_ReturnsRespuesta() {
    // given: Un nuevo Respuesta con el servicio actualizado
    Respuesta respuestaServicioActualizado = generarMockRespuesta(1L);
    respuestaServicioActualizado.setValor("{\"valor\":\"Valor actualizado\"}");

    Respuesta respuesta = generarMockRespuesta(1L);

    Memoria memoria = generarMockMemoria(1L);

    BDDMockito.given(respuestaRepository.findById(1L)).willReturn(Optional.of(respuesta));
    BDDMockito.given(respuestaRepository.save(respuesta)).willReturn(respuestaServicioActualizado);
    BDDMockito.given(memoriaService.findById(1L)).willReturn(memoria);
    BDDMockito.given(apartadoRepository.findById(1L)).willReturn(Optional.of(getMockApartado(1L, 1L, 1L)));
    BDDMockito.given(bloqueRepository.findFirstByFormularioIdOrderByOrdenDesc(1L))
        .willReturn(generarMockBloque(1L, memoria.getComite().getFormulario()));
    List<Apartado> ultimosApartados = new ArrayList<Apartado>();
    ultimosApartados.add(getMockApartado(1L, 1L, 1L));
    BDDMockito.given(apartadoRepository.findFirst2ByBloqueIdOrderByOrdenDesc(1L)).willReturn(ultimosApartados);

    // when: Actualizamos el Respuesta
    Respuesta RespuestaActualizado = respuestaService.update(respuesta);

    // then: El Respuesta se actualiza correctamente.
    Assertions.assertThat(RespuestaActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(RespuestaActualizado.getValor()).isEqualTo("{\"valor\":\"Valor actualizado\"}");

  }

  @Test
  public void update_ThrowsRespuestaNotFoundException() {
    // given: Un nuevo Respuesta a actualizar
    Respuesta respuesta = generarMockRespuesta(1L);

    // then: Lanza una excepcion porque el Respuesta no existe
    Assertions.assertThatThrownBy(() -> respuestaService.update(respuesta))
        .isInstanceOf(RespuestaNotFoundException.class);

  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {

    // given: Un Respuesta que venga sin id
    Respuesta respuesta = generarMockRespuesta(null);

    Assertions.assertThatThrownBy(
        // when: update Respuesta
        () -> respuestaService.update(respuesta))
        // then: Lanza una excepción, el id es necesario
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> respuestaService.delete(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsRespuestaNotFoundException() {
    // given: Id no existe
    BDDMockito.given(respuestaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> respuestaService.delete(1L))
        // then: Lanza RespuestaNotFoundException
        .isInstanceOf(RespuestaNotFoundException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesRespuesta() {
    // given: Id existente
    BDDMockito.given(respuestaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(respuestaRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> respuestaService.delete(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void findAll_Unlimited_ReturnsFullRespuestaList() {
    // given: One hundred Respuesta
    List<Respuesta> respuestas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      respuestas.add(generarMockRespuesta(Long.valueOf(i)));
    }

    BDDMockito.given(
        respuestaRepository.findAll(ArgumentMatchers.<Specification<Respuesta>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(respuestas));

    // when: find unlimited
    Page<Respuesta> page = respuestaService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred Respuestas
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred Respuestas
    List<Respuesta> respuestas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      respuestas.add(generarMockRespuesta(Long.valueOf(i)));
    }

    BDDMockito.given(
        respuestaRepository.findAll(ArgumentMatchers.<Specification<Respuesta>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Respuesta>>() {
          @Override
          public Page<Respuesta> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Respuesta> content = respuestas.subList(fromIndex, toIndex);
            Page<Respuesta> page = new PageImpl<>(content, pageable, respuestas.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Respuesta> page = respuestaService.findAll(null, paging);

    // then: A Page with ten Respuestas are returned containing
    // descripcion='Respuesta031' to 'Respuesta040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Respuesta Respuesta = page.getContent().get(i);
      Assertions.assertThat(Respuesta.getId()).isEqualTo(j);
    }
  }

  @Test
  public void deleteAll_DeleteAllRespuesta() {
    // given: One hundred Respuestas
    List<Respuesta> respuestas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      respuestas.add(generarMockRespuesta(Long.valueOf(i)));
    }

    BDDMockito.doNothing().when(respuestaRepository).deleteAll();

    Assertions.assertThatCode(
        // when: Delete all
        () -> respuestaService.deleteAll())
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  /**
   * Función que devuelve un objeto Respuesta
   * 
   * @param id id del Respuesta
   * @return el objeto Respuesta
   */

  public Respuesta generarMockRespuesta(Long id) {
    Memoria memoria = new Memoria();
    memoria.setId(id);

    Formulario formulario = new Formulario();
    formulario.setId(id);

    Apartado apartado = getMockApartado(id == null ? 1L : id, 1L, null);

    Respuesta respuesta = new Respuesta();
    respuesta.setId(id);
    respuesta.setMemoria(memoria);
    respuesta.setApartado(apartado);
    respuesta.setValor("{\"valor\":\"Valor" + id + "\"}");

    return respuesta;
  }

  /**
   * Genera un objeto {@link Apartado}
   * 
   * @param id
   * @param bloqueId
   * @param componenteFormularioId
   * @param padreId
   * @return Apartado
   */
  private Apartado getMockApartado(Long id, Long bloqueId, Long padreId) {

    Formulario formulario = new Formulario(1L, "M10", "Descripcion1");
    Bloque Bloque = new Bloque(bloqueId, formulario, "Bloque " + bloqueId, bloqueId.intValue());

    Apartado padre = (padreId != null) ? getMockApartado(padreId, bloqueId, null) : null;

    String txt = (id % 2 == 0) ? String.valueOf(id) : "0" + String.valueOf(id);

    final Apartado data = new Apartado();
    data.setId(id);
    data.setBloque(Bloque);
    data.setNombre("Apartado" + txt);
    data.setPadre(padre);
    data.setOrden(id.intValue());
    data.setEsquema("{\"nombre\":\"EsquemaApartado" + txt + "\"}");

    return data;
  }

  /**
   * Función que devuelve un objeto Memoria
   * 
   * @param id id de la Memoria
   * @return el objeto Memoria
   */

  public Memoria generarMockMemoria(Long id) {
    Memoria memoria = new Memoria();
    memoria.setId(id);

    Formulario formulario = new Formulario();
    formulario.setId(id);
    formulario.setNombre("M10");

    Comite comite = new Comite();
    comite.setId(id);
    comite.setFormulario(formulario);

    memoria.setComite(comite);

    return memoria;
  }

  /**
   * Función que devuelve un objeto Bloque
   * 
   * @param id         id del bloque
   * @param formulario el formulario del bloque
   * @return el objeto Bloque
   */
  public Bloque generarMockBloque(Long id, Formulario formulario) {
    return new Bloque(id, formulario, "Bloque " + id, id.intValue());
  }
}