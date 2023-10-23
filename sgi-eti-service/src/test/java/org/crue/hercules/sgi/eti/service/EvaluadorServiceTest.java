package org.crue.hercules.sgi.eti.service;

import java.time.Instant;
import java.time.Period;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.config.SgiConfigProperties;
import org.crue.hercules.sgi.eti.exceptions.EvaluadorNotFoundException;
import org.crue.hercules.sgi.eti.model.CargoComite;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Comite.Genero;
import org.crue.hercules.sgi.eti.repository.EvaluadorRepository;
import org.crue.hercules.sgi.eti.service.impl.EvaluadorServiceImpl;
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
 * EvaluadorServiceTest
 */
public class EvaluadorServiceTest extends BaseServiceTest {

  @Mock
  private EvaluadorRepository evaluadorRepository;

  private SgiConfigProperties sgiConfigProperties;
  private EvaluadorService evaluadorService;

  @BeforeEach
  public void setUp() throws Exception {
    evaluadorService = new EvaluadorServiceImpl(evaluadorRepository, sgiConfigProperties);
  }

  @Test
  public void find_WithId_ReturnsEvaluador() {
    BDDMockito.given(evaluadorRepository.findById(1L)).willReturn(Optional.of(generarMockEvaluador(1L, "Evaluador1")));

    Evaluador evaluador = evaluadorService.findById(1L);

    Assertions.assertThat(evaluador.getId()).isEqualTo(1L);

    Assertions.assertThat(evaluador.getResumen()).isEqualTo("Evaluador1");

  }

  @Test
  public void find_NotFound_ThrowsEvaluadorNotFoundException() throws Exception {
    BDDMockito.given(evaluadorRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> evaluadorService.findById(1L)).isInstanceOf(EvaluadorNotFoundException.class);
  }

  @Test
  public void create_ReturnsEvaluador() {
    // given: Un nuevo Evaluador
    Evaluador evaluadorNew = generarMockEvaluador(null, "EvaluadorNew");

    Evaluador evaluador = generarMockEvaluador(1L, "EvaluadorNew");

    BDDMockito.given(evaluadorRepository.save(evaluadorNew)).willReturn(evaluador);

    // when: Creamos el Evaluador
    Evaluador evaluadorCreado = evaluadorService.create(evaluadorNew);

    // then: El Evaluador se crea correctamente
    Assertions.assertThat(evaluadorCreado).isNotNull();
    Assertions.assertThat(evaluadorCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(evaluadorCreado.getResumen()).isEqualTo("EvaluadorNew");
  }

  @Test
  public void create_EvaluadorWithCargoPresidenteAndFechaBajaInformada_ReturnsEvaluador() {
    // given: Un nuevo Evaluador con cargo presidente
    String cargoComite = "presidente/a";
    Instant fecha = Instant.from(Instant.now().atZone(ZoneOffset.UTC).plus(Period.ofYears(1)));

    Evaluador evaluadorNew = generarMockEvaluadorWithCargoComiteAndFechaBaja(null, "EvaluadorNew", 1L, cargoComite,
        fecha);

    Evaluador evaluador = generarMockEvaluadorWithCargoComiteAndFechaBaja(1L, "EvaluadorNew", 1L, cargoComite, fecha);

    BDDMockito.given(evaluadorRepository.save(evaluadorNew)).willReturn(evaluador);

    Evaluador evaluadorCreado = evaluadorService.create(evaluadorNew);

    // then: El evaluador tiene cargo presidente
    Assertions.assertThat(evaluadorCreado).isNotNull();
    Assertions.assertThat(evaluadorCreado.getCargoComite().getNombre().toLowerCase()).isEqualTo("presidente/a");
  }

  @Test
  public void create_EvaluadorWithCargoPresidenteAndFechaBajaNull_ReturnsEvaluador() {
    // given: Un nuevo Evaluador con cargo presidente
    String cargoComite = "presidente/a";
    Instant fecha = null;

    Evaluador evaluadorNew = generarMockEvaluadorWithCargoComiteAndFechaBaja(null, "EvaluadorNew", 1L, cargoComite,
        fecha);

    Evaluador evaluador = generarMockEvaluadorWithCargoComiteAndFechaBaja(1L, "EvaluadorNew", 1L, cargoComite, fecha);

    BDDMockito.given(evaluadorRepository.save(evaluadorNew)).willReturn(evaluador);

    Evaluador evaluadorCreado = evaluadorService.create(evaluadorNew);

    // then: El evaluador tiene cargo presidente
    Assertions.assertThat(evaluadorCreado).isNotNull();
    Assertions.assertThat(evaluadorCreado.getCargoComite().getNombre().toLowerCase()).isEqualTo("presidente/a");
  }

  @Test
  public void create_EvaluadorWithCargoVocalAndFechaBajaInformada_ReturnsEvaluador() {
    // given: Un nuevo Evaluador con cargo vocal
    String cargoComite = "vocal";
    Instant fecha = Instant.from(Instant.now().atZone(ZoneOffset.UTC).plus(Period.ofYears(1)));

    Evaluador evaluadorNew = generarMockEvaluadorWithCargoComiteAndFechaBaja(null, "EvaluadorNew", 2L, cargoComite,
        fecha);

    Evaluador evaluador = generarMockEvaluadorWithCargoComiteAndFechaBaja(1L, "EvaluadorNew", 2L, cargoComite, fecha);

    BDDMockito.given(evaluadorRepository.save(evaluadorNew)).willReturn(evaluador);

    Evaluador evaluadorCreado = evaluadorService.create(evaluadorNew);

    // then: El evaluador tiene cargo vocal
    Assertions.assertThat(evaluadorCreado).isNotNull();
    Assertions.assertThat(evaluadorCreado.getCargoComite().getNombre().toLowerCase()).isEqualTo("vocal");
  }

  @Test
  public void create_EvaluadorWithCargoVocalAndFechaBajaNull_ReturnsEvaluador() {
    // given: Un nuevo Evaluador con cargo vocal
    String cargoComite = "vocal";
    Instant fecha = null;

    Evaluador evaluadorNew = generarMockEvaluadorWithCargoComiteAndFechaBaja(null, "EvaluadorNew", 2L, cargoComite,
        fecha);

    Evaluador evaluador = generarMockEvaluadorWithCargoComiteAndFechaBaja(1L, "EvaluadorNew", 2L, cargoComite, fecha);

    BDDMockito.given(evaluadorRepository.save(evaluadorNew)).willReturn(evaluador);

    Evaluador evaluadorCreado = evaluadorService.create(evaluadorNew);

    // then: El evaluador tiene cargo vocal
    Assertions.assertThat(evaluadorCreado).isNotNull();
    Assertions.assertThat(evaluadorCreado.getCargoComite().getNombre().toLowerCase()).isEqualTo("vocal");
  }

  @Test
  public void create_EvaluadorWithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo evaluador que ya tiene id
    Evaluador evaluadorNew = generarMockEvaluador(1L, "EvaluadorNew");
    // when: Creamos el evaluador
    // then: Lanza una excepcion porque el evaluador ya tiene id
    Assertions.assertThatThrownBy(() -> evaluadorService.create(evaluadorNew))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_ReturnsEvaluador() {
    // given: Un nuevo evaluador con el servicio actualizado
    Evaluador evaluadorServicioActualizado = generarMockEvaluador(1L, "Evaluador1 actualizada");

    Evaluador evaluador = generarMockEvaluador(1L, "Evaluador1");

    BDDMockito.given(evaluadorRepository.findById(1L)).willReturn(Optional.of(evaluador));
    BDDMockito.given(evaluadorRepository.save(evaluador)).willReturn(evaluadorServicioActualizado);

    // when: Actualizamos el evaluador
    Evaluador evaluadorActualizado = evaluadorService.update(evaluador);

    // then: El evaluador se actualiza correctamente.
    Assertions.assertThat(evaluadorActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(evaluadorActualizado.getResumen()).isEqualTo("Evaluador1 actualizada");

  }

  @Test
  public void update_EvaluadorWithCargoPresidenteAndFechaBajaInformada_ReturnsEvaluador() {
    // given: Un nuevo Evaluador con cargo presidente
    String cargoComite = "presidente/a";
    Instant fecha = Instant.from(Instant.now().atZone(ZoneOffset.UTC).plus(Period.ofYears(1)));

    Evaluador evaluadorServicioActualizado = generarMockEvaluadorWithCargoComiteAndFechaBaja(1L,
        "Evaluador1 actualizada", 1L, cargoComite, fecha);

    Evaluador evaluador = generarMockEvaluadorWithCargoComiteAndFechaBaja(1L, "Evaluador1", 1L, cargoComite, fecha);

    BDDMockito.given(evaluadorRepository.findById(1L)).willReturn(Optional.of(evaluador));
    BDDMockito.given(evaluadorRepository.save(evaluador)).willReturn(evaluadorServicioActualizado);

    // when: Actualizamos el evaluador
    Evaluador evaluadorActualizado = evaluadorService.update(evaluador);

    // then: El evaluador se actualiza correctamente.
    Assertions.assertThat(evaluadorActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(evaluadorActualizado.getResumen()).isEqualTo("Evaluador1 actualizada");

  }

  @Test
  public void update_EvaluadorWithCargoPresidenteAndFechaBajaNull_ReturnsEvaluador() {
    // given: Un nuevo Evaluador con cargo presidente
    String cargoComite = "presidente/a";
    Instant fecha = null;

    Evaluador evaluadorServicioActualizado = generarMockEvaluadorWithCargoComiteAndFechaBaja(1L,
        "Evaluador1 actualizada", 1L, cargoComite, fecha);

    Evaluador evaluador = generarMockEvaluadorWithCargoComiteAndFechaBaja(1L, "Evaluador1", 1L, cargoComite, fecha);

    BDDMockito.given(evaluadorRepository.findById(1L)).willReturn(Optional.of(evaluador));
    BDDMockito.given(evaluadorRepository.save(evaluador)).willReturn(evaluadorServicioActualizado);

    // when: Actualizamos el evaluador
    Evaluador evaluadorActualizado = evaluadorService.update(evaluador);

    // then: El evaluador se actualiza correctamente.
    Assertions.assertThat(evaluadorActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(evaluadorActualizado.getResumen()).isEqualTo("Evaluador1 actualizada");

  }

  @Test
  public void update_EvaluadorWithCargoVocalAndFechaBajaInformada_ReturnsEvaluador() {
    // given: Un nuevo Evaluador con cargo vocal
    String cargoComite = "vocal";
    Instant fecha = Instant.from(Instant.now().atZone(ZoneOffset.UTC).plus(Period.ofYears(1)));

    Evaluador evaluadorServicioActualizado = generarMockEvaluadorWithCargoComiteAndFechaBaja(1L,
        "Evaluador1 actualizada", 2L, cargoComite, fecha);

    Evaluador evaluador = generarMockEvaluadorWithCargoComiteAndFechaBaja(1L, "Evaluador1", 2L, cargoComite, fecha);

    BDDMockito.given(evaluadorRepository.findById(1L)).willReturn(Optional.of(evaluador));
    BDDMockito.given(evaluadorRepository.save(evaluador)).willReturn(evaluadorServicioActualizado);

    // when: Actualizamos el evaluador
    Evaluador evaluadorActualizado = evaluadorService.update(evaluador);

    // then: El evaluador se actualiza correctamente.
    Assertions.assertThat(evaluadorActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(evaluadorActualizado.getResumen()).isEqualTo("Evaluador1 actualizada");

  }

  @Test
  public void update_EvaluadorWithCargoVocalAndFechaBajaNull_ReturnsEvaluador() {
    // given: Un nuevo Evaluador con cargo vocal
    String cargoComite = "vocal";
    Instant fecha = null;

    Evaluador evaluadorServicioActualizado = generarMockEvaluadorWithCargoComiteAndFechaBaja(1L,
        "Evaluador1 actualizada", 2L, cargoComite, fecha);

    Evaluador evaluador = generarMockEvaluadorWithCargoComiteAndFechaBaja(1L, "Evaluador1", 2L, cargoComite, fecha);

    BDDMockito.given(evaluadorRepository.findById(1L)).willReturn(Optional.of(evaluador));
    BDDMockito.given(evaluadorRepository.save(evaluador)).willReturn(evaluadorServicioActualizado);

    // when: Actualizamos el evaluador
    Evaluador evaluadorActualizado = evaluadorService.update(evaluador);

    // then: El evaluador se actualiza correctamente.
    Assertions.assertThat(evaluadorActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(evaluadorActualizado.getResumen()).isEqualTo("Evaluador1 actualizada");

  }

  @Test
  public void update_ThrowsEvaluadorNotFoundException() {
    // given: Un nuevo evaluador a actualizar
    Evaluador evaluador = generarMockEvaluador(1L, "Evaluador");

    // then: Lanza una excepcion porque el evaluador no existe
    Assertions.assertThatThrownBy(() -> evaluadorService.update(evaluador))
        .isInstanceOf(EvaluadorNotFoundException.class);

  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {

    // given: Un Evaluador que venga sin id
    Evaluador evaluador = generarMockEvaluador(null, "Evaluador");

    Assertions.assertThatThrownBy(
        // when: update Evaluador
        () -> evaluadorService.update(evaluador))
        // then: Lanza una excepción, el id es necesario
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> evaluadorService.delete(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsEvaluadorNotFoundException() {
    // given: Id no existe
    BDDMockito.given(evaluadorRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> evaluadorService.delete(1L))
        // then: Lanza EvaluadorNotFoundException
        .isInstanceOf(EvaluadorNotFoundException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesEvaluador() {
    // given: Id existente
    BDDMockito.given(evaluadorRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(evaluadorRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> evaluadorService.delete(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void findAll_Unlimited_ReturnsFullEvaluadorList() {
    // given: One hundred Evaluador
    List<Evaluador> evaluadores = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      evaluadores.add(generarMockEvaluador(Long.valueOf(i), "Evaluador" + String.format("%03d", i)));
    }

    BDDMockito.given(
        evaluadorRepository.findAll(ArgumentMatchers.<Specification<Evaluador>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(evaluadores));

    // when: find unlimited
    Page<Evaluador> page = evaluadorService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred Evaluadores
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isZero();
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred Evaluadores
    List<Evaluador> evaluadores = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      evaluadores.add(generarMockEvaluador(Long.valueOf(i), "Evaluador" + String.format("%03d", i)));
    }

    BDDMockito.given(
        evaluadorRepository.findAll(ArgumentMatchers.<Specification<Evaluador>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Evaluador>>() {
          @Override
          public Page<Evaluador> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Evaluador> content = evaluadores.subList(fromIndex, toIndex);
            Page<Evaluador> page = new PageImpl<>(content, pageable, evaluadores.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Evaluador> page = evaluadorService.findAll(null, paging);

    // then: A Page with ten Evaluadores are returned containing
    // resumen='Evaluador031' to 'Evaluador040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Evaluador evaluador = page.getContent().get(i);
      Assertions.assertThat(evaluador.getResumen()).isEqualTo("Evaluador" + String.format("%03d", j));
    }
  }

  @Test
  public void findAllByComiteSinconflictoInteresesMemoria_Unlimited_ReturnsFullEvaluadorList() {
    // given: idComite, idMemoria, One hundred Evaluador
    Long idComite = 1L;
    Long idMemoria = 1L;
    List<Evaluador> evaluadores = new ArrayList<>();
    for (int i = 1; i <= 10; i++) {
      evaluadores.add(generarMockEvaluador(Long.valueOf(i), "Evaluador" + String.format("%03d", i)));
    }

    BDDMockito.given(evaluadorRepository.findAllByComiteSinconflictoInteresesMemoria(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong(), ArgumentMatchers.any(Instant.class))).willReturn(evaluadores);

    // when: find unlimited
    List<Evaluador> result = evaluadorService.findAllByComiteSinconflictoInteresesMemoria(idComite, idMemoria,
        Instant.now());

    // then: Get a page with one hundred Evaluadores
    Assertions.assertThat(result.size()).isEqualTo(10);
  }

  @Test
  public void findAllByComiteSinconflictoInteresesMemoria_WithPaging_ReturnsPage() {
    // given: idComite, idMemoria, One hundred Evaluadores
    Long idComite = 1L;
    Long idMemoria = 1L;
    List<Evaluador> evaluadores = new ArrayList<>();
    for (int i = 1; i <= 10; i++) {
      evaluadores.add(generarMockEvaluador(Long.valueOf(i), "Evaluador" + String.format("%03d", i)));
    }

    BDDMockito.given(evaluadorRepository.findAllByComiteSinconflictoInteresesMemoria(ArgumentMatchers.anyLong(),
        ArgumentMatchers.anyLong(), ArgumentMatchers.any(Instant.class))).willReturn(evaluadores);

    // when: Get page=3 with pagesize=10
    List<Evaluador> result = evaluadorService.findAllByComiteSinconflictoInteresesMemoria(idComite, idMemoria,
        Instant.now());

    // then: A List with ten Evaluadores are returned containing
    Assertions.assertThat(result.size()).isEqualTo(10);
    for (int i = 0, j = 1; i < 10; i++, j++) {
      Evaluador evaluador = result.get(i);
      Assertions.assertThat(evaluador.getResumen()).isEqualTo("Evaluador" + String.format("%03d", j));
    }
  }

  /**
   * Función que devuelve un objeto Evaluador
   * 
   * @param id      id del Evaluador
   * @param resumen el resumen de Evaluador
   * @return el objeto Evaluador
   */

  public Evaluador generarMockEvaluador(Long id, String resumen) {
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
    evaluador.setPersonaRef("user-00" + id);
    evaluador.setActivo(Boolean.TRUE);

    return evaluador;
  }

  public Evaluador generarMockEvaluadorWithCargoComiteAndFechaBaja(Long id, String resumen, Long cargoComiteId,
      String cargoComiteNombre, Instant fecha) {
    CargoComite cargoComite = new CargoComite(cargoComiteId, cargoComiteNombre, Boolean.TRUE);

    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(1L, "Comite1", "nombreInvestigacion", Genero.M, formulario, Boolean.TRUE);

    Evaluador evaluador = new Evaluador();
    evaluador.setId(id);
    evaluador.setCargoComite(cargoComite);
    evaluador.setComite(comite);
    evaluador.setFechaAlta(Instant.now());
    evaluador.setFechaBaja(fecha);
    evaluador.setResumen(resumen);
    evaluador.setPersonaRef("user-00" + id);
    evaluador.setActivo(Boolean.TRUE);

    return evaluador;
  }
}