package org.crue.hercules.sgi.eti.service;

import java.util.ArrayList;
import java.util.List;

import java.time.Instant;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.ConflictoInteresNotFoundException;
import org.crue.hercules.sgi.eti.model.CargoComite;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ConflictoInteres;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Comite.Genero;
import org.crue.hercules.sgi.eti.repository.ConflictoInteresRepository;
import org.crue.hercules.sgi.eti.repository.EvaluadorRepository;
import org.crue.hercules.sgi.eti.service.impl.ConflictoInteresServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.PageImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;

/**
 * ConflictoInteresServiceTest
 */
public class ConflictoInteresServiceTest extends BaseServiceTest {

  @Mock
  private ConflictoInteresRepository conflictoInteresRepository;

  @Mock
  private EvaluadorRepository evaluadorRepository;

  private ConflictoInteresService conflictoInteresService;

  @BeforeEach
  public void setUp() throws Exception {
    conflictoInteresService = new ConflictoInteresServiceImpl(conflictoInteresRepository, evaluadorRepository);
  }

  @Test
  public void find_WithId_ReturnsConflictoInteres() {
    BDDMockito.given(conflictoInteresRepository.findById(1L))
        .willReturn(Optional.of(generarMockConflictoInteres(1L, null)));

    ConflictoInteres conflictoInteres = conflictoInteresService.findById(1L);

    Assertions.assertThat(conflictoInteres.getId()).isEqualTo(1L);

    Assertions.assertThat(conflictoInteres.getPersonaConflictoRef()).isEqualTo("user-001");

  }

  @Test
  public void findAll_Unlimited_ReturnsFullConflictoInteresList() {
    List<ConflictoInteres> conflictoInteres = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      conflictoInteres.add(generarMockConflictoInteres(Long.valueOf(i), "user-123"));
    }

    BDDMockito.given(conflictoInteresRepository.findAll(ArgumentMatchers.<Specification<ConflictoInteres>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(conflictoInteres));

    Page<ConflictoInteres> page = conflictoInteresService.findAll(null, Pageable.unpaged());

    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getSize()).isEqualTo(conflictoInteres.size());
    Assertions.assertThat(page.getTotalElements()).isEqualTo(conflictoInteres.size());

  }

  @Test
  public void findAllByEvaluadorId() {
    Long evaluadorId = 1L;
    List<ConflictoInteres> conflictoInteres = new ArrayList<>();
    conflictoInteres.add(generarMockConflictoInteres(1L, "ConflictoInteres"));

    BDDMockito.given(conflictoInteresRepository.findAllByEvaluadorId(evaluadorId, Pageable.unpaged()))
        .willReturn(new PageImpl<>(conflictoInteres));

    Page<ConflictoInteres> page = conflictoInteresService.findAllByEvaluadorId(evaluadorId, Pageable.unpaged());

    Assertions.assertThat(page.getContent().size()).isEqualTo(1);
    Assertions.assertThat(page.getSize()).isEqualTo(conflictoInteres.size());
    Assertions.assertThat(page.getTotalElements()).isEqualTo(conflictoInteres.size());

  }

  @Test
  public void find_NotFound_ThrowsConflictoInteresNotFoundException() throws Exception {
    BDDMockito.given(conflictoInteresRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> conflictoInteresService.findById(1L))
        .isInstanceOf(ConflictoInteresNotFoundException.class);
  }

  @Test
  public void create_ReturnsConflictoInteres() {
    // given: Un nuevo ConflictoInteres
    ConflictoInteres conflictoInteresNew = generarMockConflictoInteres(null, null);
    conflictoInteresNew.getEvaluador().setId(1L);
    ConflictoInteres conflictoInteres = generarMockConflictoInteres(1L, null);

    BDDMockito.given(evaluadorRepository.findById(1L)).willReturn(Optional.of(generarMockEvaluador(1L, "Resumen1")));
    BDDMockito.given(conflictoInteresRepository.save(conflictoInteresNew)).willReturn(conflictoInteres);

    // when: Creamos el ConflictoInteres
    ConflictoInteres conflictoInteresCreado = conflictoInteresService.create(conflictoInteresNew);

    // then: El ConflictoInteres se crea correctamente
    Assertions.assertThat(conflictoInteresCreado).isNotNull();
    Assertions.assertThat(conflictoInteresCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(conflictoInteresCreado.getPersonaConflictoRef()).isEqualTo("user-001");
  }

  @Test
  public void create_ReturnsConflictoInteres_ThrowsConflictoInteresNotFoundException() {
    // given: Un nuevo ConflictoInteres
    ConflictoInteres conflictoInteresNew = generarMockConflictoInteres(null, null);
    conflictoInteresNew.getEvaluador().setId(1L);

    Assertions.assertThatThrownBy(() -> conflictoInteresService.create(conflictoInteresNew))
        .isInstanceOf(ConflictoInteresNotFoundException.class);
  }

  @Test
  public void create_ConflictoInteresWithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo conflictoInteres que no tiene id de evaluador
    ConflictoInteres conflictoInteresNew = generarMockConflictoInteres(1L, "ConflictoInteresNew");
    conflictoInteresNew.getEvaluador().setId(null);
    // when: Creamos el conflictoInteres
    // then: Lanza una excepcion porque el conflictoInteres ya tiene id
    Assertions.assertThatThrownBy(() -> conflictoInteresService.create(conflictoInteresNew))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_ReturnsConflictoInteres() {
    // given: Un nuevo conflictoInteres con el servicio actualizado
    ConflictoInteres conflictoInteresServicioActualizado = generarMockConflictoInteres(1L, "user-123");

    ConflictoInteres conflictoInteres = generarMockConflictoInteres(1L, null);

    BDDMockito.given(conflictoInteresRepository.findById(1L)).willReturn(Optional.of(conflictoInteres));
    BDDMockito.given(conflictoInteresRepository.save(conflictoInteres)).willReturn(conflictoInteresServicioActualizado);

    // when: Actualizamos el conflictoInteres
    ConflictoInteres conflictoInteresActualizado = conflictoInteresService.update(conflictoInteres);

    // then: El conflictoInteres se actualiza correctamente.
    Assertions.assertThat(conflictoInteresActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(conflictoInteresActualizado.getPersonaConflictoRef()).isEqualTo("user-123");

  }

  @Test
  public void update_ThrowsConflictoInteresNotFoundException() {
    // given: Un nuevo conflictoInteres a actualizar
    ConflictoInteres conflictoInteres = generarMockConflictoInteres(1L, "ConflictoInteres");

    // then: Lanza una excepcion porque el conflictoInteres no existe
    Assertions.assertThatThrownBy(() -> conflictoInteresService.update(conflictoInteres))
        .isInstanceOf(ConflictoInteresNotFoundException.class);

  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {

    // given: Un ConflictoInteres que venga sin id
    ConflictoInteres conflictoInteres = generarMockConflictoInteres(null, "ConflictoInteres");

    Assertions.assertThatThrownBy(
        // when: update ConflictoInteres
        () -> conflictoInteresService.update(conflictoInteres))
        // then: Lanza una excepción, el id es necesario
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> conflictoInteresService.delete(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsConflictoInteresNotFoundException() {
    // given: Id no existe
    BDDMockito.given(conflictoInteresRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> conflictoInteresService.delete(1L))
        // then: Lanza ConflictoInteresNotFoundException
        .isInstanceOf(ConflictoInteresNotFoundException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesConflictoInteres() {
    // given: Id existente
    BDDMockito.given(conflictoInteresRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(conflictoInteresRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> conflictoInteresService.delete(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
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
    Comite comite = new Comite(1L, "Comite1", "nombreSecretario", "nombreInvestigacion", Genero.M, "nombreDecreto",
        "articulo", formulario, Boolean.TRUE);

    Evaluador evaluador = new Evaluador();
    evaluador.setId(id);
    evaluador.setCargoComite(cargoComite);
    evaluador.setComite(comite);
    evaluador.setFechaAlta(Instant.now());
    evaluador.setFechaBaja(Instant.now());
    evaluador.setResumen(resumen);
    evaluador.setPersonaRef("user-00" + (id != null ? id : "1"));
    evaluador.setActivo(Boolean.TRUE);

    return evaluador;
  }

  /**
   * Función que devuelve un objeto ConflictoInteres
   * 
   * @param id                  id del ConflictoInteres
   * @param personaConflictoRef la persona del conflicto de interés
   * @return el objeto ConflictoInteres
   */
  public ConflictoInteres generarMockConflictoInteres(Long id, String personaConflictoInteres) {
    ConflictoInteres conflicto = new ConflictoInteres();
    conflicto.setId(id);
    conflicto.setEvaluador(generarMockEvaluador(id, "Resumen" + (id != null ? id : "1")));
    if (personaConflictoInteres != null) {
      conflicto.setPersonaConflictoRef(personaConflictoInteres);
    } else {
      conflicto.setPersonaConflictoRef("user-00" + (id != null ? id : "1"));
    }
    return conflicto;
  }

}