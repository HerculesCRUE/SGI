package org.crue.hercules.sgi.eti.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.AsistentesNotFoundException;
import org.crue.hercules.sgi.eti.model.Asistentes;
import org.crue.hercules.sgi.eti.model.CargoComite;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Comite.Genero;
import org.crue.hercules.sgi.eti.repository.AsistentesRepository;
import org.crue.hercules.sgi.eti.service.impl.AsistentesServiceImpl;
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
 * AsistentesServiceTest
 */
public class AsistentesServiceTest extends BaseServiceTest {

  @Mock
  private AsistentesRepository asistentesRepository;

  private AsistentesService asistentesService;

  @BeforeEach
  public void setUp() throws Exception {
    asistentesService = new AsistentesServiceImpl(asistentesRepository);
  }

  @Test
  public void find_WithId_ReturnsAsistentes() {
    BDDMockito.given(asistentesRepository.findById(1L))
        .willReturn(Optional.of(generarMockAsistentes(1L, "Motivo 1", Boolean.TRUE)));

    Asistentes asistente = asistentesService.findById(1L);

    Assertions.assertThat(asistente.getId()).isEqualTo(1L);
    Assertions.assertThat(asistente).isNotNull();
    Assertions.assertThat(asistente.getId()).isEqualTo(1L);
    Assertions.assertThat(asistente.getConvocatoriaReunion().getId()).isEqualTo(1L);
    Assertions.assertThat(asistente.getEvaluador().getId()).isEqualTo(1L);
    Assertions.assertThat(asistente.getAsistencia()).isTrue();
    Assertions.assertThat(asistente.getMotivo()).isEqualTo("Motivo 1");

  }

  @Test
  public void find_NotFound_ThrowsAsistentesNotFoundException() throws Exception {
    BDDMockito.given(asistentesRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> asistentesService.findById(1L)).isInstanceOf(AsistentesNotFoundException.class);
  }

  @Test
  public void create_ReturnsAsistentes() {
    // given: Un nuevo asistente
    Asistentes asistenteNew = generarMockAsistentes(null, "MotivoNew", Boolean.TRUE);

    Asistentes asistente = generarMockAsistentes(1L, "MotivoNew", Boolean.TRUE);

    BDDMockito.given(asistentesRepository.save(asistenteNew)).willReturn(asistente);

    // when: Creamos el asistente
    Asistentes asistenteCreado = asistentesService.create(asistenteNew);

    // then: El asistente se crea correctamente
    Assertions.assertThat(asistenteCreado).isNotNull();
    Assertions.assertThat(asistenteCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(asistenteCreado.getConvocatoriaReunion().getId()).isEqualTo(1L);
    Assertions.assertThat(asistenteCreado.getEvaluador().getId()).isEqualTo(1L);
    Assertions.assertThat(asistenteCreado.getAsistencia()).isTrue();
  }

  @Test
  public void create_AsistentesWithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo asistente que ya tiene id
    Asistentes asistentesNew = generarMockAsistentes(1L, "MotivoNew", Boolean.TRUE);
    // when: Creamos el asistente
    // then: Lanza una excepcion porque el asistente ya tiene id
    Assertions.assertThatThrownBy(() -> asistentesService.create(asistentesNew))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_ReturnsAsistentes() {
    // given: Un nuevo asistente actualizado
    Asistentes asistenteActualizado = generarMockAsistentes(1L, "Motivo 1 actualizado", Boolean.FALSE);

    Asistentes asistente = generarMockAsistentes(1L, "Motivo 1", Boolean.TRUE);

    BDDMockito.given(asistentesRepository.findById(1L)).willReturn(Optional.of(asistente));
    BDDMockito.given(asistentesRepository.save(asistente)).willReturn(asistenteActualizado);

    // when: Actualizamos asistente
    Asistentes asistenteActualizadoBBDD = asistentesService.update(asistente);

    // then: El asistente se actualiza correctamente.
    Assertions.assertThat(asistenteActualizadoBBDD.getId()).isEqualTo(1L);
    Assertions.assertThat(asistenteActualizadoBBDD.getConvocatoriaReunion().getId()).isEqualTo(1L);
    Assertions.assertThat(asistenteActualizadoBBDD.getEvaluador().getId()).isEqualTo(1L);
    Assertions.assertThat(asistenteActualizadoBBDD.getAsistencia()).isFalse();
    Assertions.assertThat(asistenteActualizadoBBDD.getMotivo()).isEqualTo("Motivo 1 actualizado");

  }

  @Test
  public void update_ThrowsAsistentesNotFoundException() {
    // given: Un nuevo asistente a actualizar
    Asistentes asistente = generarMockAsistentes(1L, "Motivo 1", Boolean.TRUE);

    // then: Lanza una excepcion porque elasistente no existe
    Assertions.assertThatThrownBy(() -> asistentesService.update(asistente))
        .isInstanceOf(AsistentesNotFoundException.class);

  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {

    // given: Un asistente que venga sin id
    Asistentes asistente = generarMockAsistentes(null, "Motivo 1", Boolean.TRUE);

    Assertions.assertThatThrownBy(
        // when: update asistente
        () -> asistentesService.update(asistente))
        // then: Lanza una excepción, el id es necesario
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> asistentesService.delete(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsAsistentesNotFoundException() {
    // given: Id no existe
    BDDMockito.given(asistentesRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> asistentesService.delete(1L))
        // then: Lanza AsistentesNotFoundException
        .isInstanceOf(AsistentesNotFoundException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesAsistentes() {
    // given: Id existente
    BDDMockito.given(asistentesRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(asistentesRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> asistentesService.delete(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void findAll_Unlimited_ReturnsFullAsistentesList() {
    // given: One hundred asistentes
    List<Asistentes> asistentes = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      asistentes.add(generarMockAsistentes(Long.valueOf(i), "Motivo " + String.format("%03d", i), Boolean.TRUE));
    }

    BDDMockito.given(asistentesRepository.findAll(ArgumentMatchers.<Specification<Asistentes>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(asistentes));

    // when: find unlimited
    Page<Asistentes> page = asistentesService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred asistentes
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isZero();
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred Asistentes
    List<Asistentes> asistentes = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      asistentes.add(generarMockAsistentes(Long.valueOf(i), "Motivo" + String.format("%03d", i), Boolean.TRUE));
    }

    BDDMockito.given(asistentesRepository.findAll(ArgumentMatchers.<Specification<Asistentes>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<Asistentes>>() {
          @Override
          public Page<Asistentes> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Asistentes> content = asistentes.subList(fromIndex, toIndex);
            Page<Asistentes> page = new PageImpl<>(content, pageable, asistentes.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Asistentes> page = asistentesService.findAll(null, paging);

    // then: A Page with ten Asistentess are returned containing
    // motivo='Motivo031' to 'Motivo040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Asistentes asistente = page.getContent().get(i);
      Assertions.assertThat(asistente.getMotivo()).isEqualTo("Motivo" + String.format("%03d", j));
    }
  }

  @Test
  public void findAllByConvocatoriaReunionId_Unlimited_ReturnsFullAsistentesList() {

    // given: Datos existentes con convocatoriaReunionId = 1
    Long convocatoriaReunionId = 1L;
    List<Asistentes> response = new LinkedList<Asistentes>();
    response.add(generarMockAsistentes(1L, "Motivo1", Boolean.TRUE));
    response.add(generarMockAsistentes(2L, "Motivo2", Boolean.TRUE));

    BDDMockito.given(asistentesRepository.findAllByConvocatoriaReunionId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(response));

    // when: Se buscan todos las datos
    Page<Asistentes> result = asistentesService.findAllByConvocatoriaReunionId(convocatoriaReunionId,
        Pageable.unpaged());

    // then: Se recuperan todos los datos
    Assertions.assertThat(result.getContent()).isEqualTo(response);
    Assertions.assertThat(result.getNumber()).isZero();
    Assertions.assertThat(result.getSize()).isEqualTo(response.size());
    Assertions.assertThat(result.getTotalElements()).isEqualTo(response.size());
  }

  @Test
  public void findAllByConvocatoriaReunionId_Unlimited_ReturnEmptyPage() {

    // given: No hay datos con convocatoriaReunionId = 1
    Long convocatoriaReunionId = 1L;
    BDDMockito.given(asistentesRepository.findAllByConvocatoriaReunionId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(Collections.emptyList()));

    // when: Se buscan todos las datos
    Page<Asistentes> result = asistentesService.findAllByConvocatoriaReunionId(convocatoriaReunionId,
        Pageable.unpaged());

    // then: Se recupera lista vacía
    Assertions.assertThat(result.isEmpty());
  }

  @Test
  public void findAllByConvocatoriaReunionId_WithPaging_ReturnsPage() {

    // given: Datos existentes con convocatoriaReunionId = 1
    Long convocatoriaReunionId = 1L;
    List<Asistentes> response = new LinkedList<Asistentes>();
    response.add(generarMockAsistentes(1L, "Motivo1", Boolean.TRUE));
    response.add(generarMockAsistentes(2L, "Motivo2", Boolean.TRUE));
    response.add(generarMockAsistentes(3L, "Motivo3", Boolean.TRUE));

    // página 1 con 2 elementos por página
    Pageable pageable = PageRequest.of(1, 2);
    Page<Asistentes> pageResponse = new PageImpl<>(response.subList(2, 3), pageable, response.size());

    BDDMockito.given(asistentesRepository.findAllByConvocatoriaReunionId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<Pageable>any())).willReturn(pageResponse);

    // when: Se buscan los datos paginados
    Page<Asistentes> result = asistentesService.findAllByConvocatoriaReunionId(convocatoriaReunionId, pageable);

    // then: Se recuperan los datos correctamente según la paginación solicitada
    Assertions.assertThat(result).isEqualTo(pageResponse);
    Assertions.assertThat(result.getContent()).isEqualTo(response.subList(2, 3));
    Assertions.assertThat(result.getNumber()).isEqualTo(pageable.getPageNumber());
    Assertions.assertThat(result.getSize()).isEqualTo(pageable.getPageSize());
    Assertions.assertThat(result.getTotalElements()).isEqualTo(response.size());
  }

  @Test
  public void findAllByConvocatoriaReunionId_WithPaging_ReturnEmptyPage() {

    // given: No hay datos con convocatoriaReunionId = 1
    Long convocatoriaReunionId = 1L;

    List<Asistentes> response = new LinkedList<Asistentes>();
    Pageable pageable = PageRequest.of(1, 2);
    Page<Asistentes> pageResponse = new PageImpl<>(response, pageable, response.size());

    BDDMockito.given(asistentesRepository.findAllByConvocatoriaReunionId(ArgumentMatchers.anyLong(),
        ArgumentMatchers.<Pageable>any())).willReturn(pageResponse);

    // when: Se buscan los datos paginados
    Page<Asistentes> result = asistentesService.findAllByConvocatoriaReunionId(convocatoriaReunionId, pageable);

    // then: Se recupera lista de datos paginados vacía
    Assertions.assertThat(result).isEmpty();
  }

  /**
   * Función que devuelve un objeto Asistentes
   * 
   * @param id         id del asistentes
   * @param motivo     motivo
   * @param asistencia asistencia
   * @return el objeto Asistentes
   */

  private Asistentes generarMockAsistentes(Long id, String motivo, Boolean asistencia) {

    Asistentes asistentes = new Asistentes();
    asistentes.setId(id);
    asistentes.setEvaluador(generarMockEvaluador(id, "Resumen " + motivo));
    asistentes.setConvocatoriaReunion(getMockConvocatoriaReunion(id, id));
    asistentes.setMotivo(motivo);
    asistentes.setAsistencia(asistencia);

    return asistentes;
  }

  /**
   * Función que devuelve un objeto Evaluador
   * 
   * @param id      id del Evaluador
   * @param resumen el resumen de Evaluador
   * @return el objeto Evaluador
   */

  private Evaluador generarMockEvaluador(Long id, String resumen) {
    CargoComite cargoComite = new CargoComite();
    cargoComite.setId(1L);
    cargoComite.setNombre("CargoComite1");
    cargoComite.setActivo(Boolean.TRUE);

    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(1L, "Comite1", "nombreInvestigacion", Genero.M, formulario, Boolean.TRUE);

    Evaluador evaluador = new Evaluador();
    evaluador.setId(id);
    evaluador.setCargoComite(cargoComite);
    evaluador.setComite(comite);
    evaluador.setFechaAlta(Instant.now());
    evaluador.setFechaBaja(Instant.now());
    evaluador.setResumen(resumen);
    evaluador.setPersonaRef("user-" + String.format("%03d", id));
    evaluador.setActivo(Boolean.TRUE);

    return evaluador;
  }

  /**
   * Genera un objeto {@link ConvocatoriaReunion}
   * 
   * @param id       id de la convocatoria reunión
   * @param comiteId comite id
   * @return ConvocatoriaReunion
   */
  private ConvocatoriaReunion getMockConvocatoriaReunion(Long id, Long comiteId) {

    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(comiteId, "Comite" + comiteId, "nombreInvestigacion", Genero.M, formulario,
        Boolean.TRUE);

    TipoConvocatoriaReunion tipoConvocatoriaReunion = new TipoConvocatoriaReunion(1L, "Ordinaria", Boolean.TRUE);

    final ConvocatoriaReunion data = new ConvocatoriaReunion();
    data.setId(id);
    data.setComite(comite);
    data.setFechaEvaluacion(Instant.parse("2020-07-20T00:00:00Z"));
    data.setFechaLimite(Instant.parse("2020-08-20T23:59:59Z"));
    data.setLugar("Lugar " + String.format("%03d", id));
    data.setOrdenDia("Orden del día convocatoria reunión " + String.format("%03d", id));
    data.setAnio(2020);
    data.setNumeroActa(100L);
    data.setTipoConvocatoriaReunion(tipoConvocatoriaReunion);
    data.setHoraInicio(7);
    data.setMinutoInicio(30);
    data.setFechaEnvio(Instant.parse("2020-07-13T00:00:00Z"));
    data.setActivo(Boolean.TRUE);

    return data;
  }
}