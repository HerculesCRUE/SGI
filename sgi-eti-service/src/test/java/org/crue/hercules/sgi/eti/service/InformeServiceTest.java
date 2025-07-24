package org.crue.hercules.sgi.eti.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.InformeNotFoundException;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Informe;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.MemoriaTitulo;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion.TipoValorSocial;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionDisMetodologico;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionObjetivos;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionResumen;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionTitulo;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.eti.repository.InformeRepository;
import org.crue.hercules.sgi.eti.service.impl.InformeServiceImpl;
import org.crue.hercules.sgi.framework.i18n.Language;
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
 * InformeServiceTest
 */
public class InformeServiceTest extends BaseServiceTest {

  @Mock
  private InformeRepository informeRepository;

  private InformeService informeService;

  @BeforeEach
  public void setUp() throws Exception {
    informeService = new InformeServiceImpl(informeRepository);
  }

  @Test
  public void find_WithId_ReturnsInforme() {
    BDDMockito.given(informeRepository.findById(1L))
        .willReturn(Optional.of(generarMockInforme(1L, "DocumentoFormulario1")));

    Informe informe = informeService.findById(1L);

    Assertions.assertThat(informe.getId()).isEqualTo(1L);

  }

  @Test
  public void find_NotFound_ThrowsInformeNotFoundException() throws Exception {
    BDDMockito.given(informeRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> informeService.findById(1L)).isInstanceOf(InformeNotFoundException.class);
  }

  @Test
  public void create_ReturnsInforme() {
    // given: Un nuevo Informe
    Informe informeNew = generarMockInforme(null, "DocumentoFormularioNew");

    Informe informe = generarMockInforme(1L, "DocumentoFormularioNew");

    BDDMockito.given(informeRepository.save(informeNew)).willReturn(informe);

    // when: Creamos el Informe
    Informe informeCreado = informeService.create(informeNew);

    // then: El Informe se crea correctamente
    Assertions.assertThat(informeCreado).isNotNull();
    Assertions.assertThat(informeCreado.getId()).isEqualTo(1L);
  }

  @Test
  public void create_InformeWithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo InformeFromulario que ya tiene id
    Informe informeNew = generarMockInforme(1L, "DocumentoFormularioNew");
    // when: Creamos el informe
    // then: Lanza una excepcion porque el informe ya tiene id
    Assertions.assertThatThrownBy(() -> informeService.create(informeNew)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_ReturnsInforme() {
    // given: Un nuevo informe con el servicio actualizado
    Informe informeServicioActualizado = generarMockInforme(1L, "DocumentoFormulario1 actualizado");

    Informe informe = generarMockInforme(1L, "DocumentoFormulario1");

    BDDMockito.given(informeRepository.findById(1L)).willReturn(Optional.of(informe));
    BDDMockito.given(informeRepository.save(informe)).willReturn(informeServicioActualizado);

    // when: Actualizamos el informe
    Informe informeActualizado = informeService.update(informe);

    // then: El informe se actualiza correctamente.
    Assertions.assertThat(informeActualizado.getId()).isEqualTo(1L);

  }

  @Test
  public void update_ThrowsInformeNotFoundException() {
    // given: Un nuevo informe a actualizar
    Informe informe = generarMockInforme(1L, "DocumentoFormulario");

    // then: Lanza una excepcion porque el informe no existe
    Assertions.assertThatThrownBy(() -> informeService.update(informe)).isInstanceOf(InformeNotFoundException.class);

  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {

    // given: Un Informe que venga sin id
    Informe informe = generarMockInforme(null, "DocumentoFormulario");

    Assertions.assertThatThrownBy(
        // when: update Informe
        () -> informeService.update(informe))
        // then: Lanza una excepción, el id es necesario
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> informeService.delete(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsInformeNotFoundException() {
    // given: Id no existe
    BDDMockito.given(informeRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> informeService.delete(1L))
        // then: Lanza InformeNotFoundException
        .isInstanceOf(InformeNotFoundException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesInforme() {
    // given: Id existente
    BDDMockito.given(informeRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(informeRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> informeService.delete(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void deleteAll_DeleteAllInforme() {
    // given: One hundred Informe
    List<Informe> informes = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      informes.add(generarMockInforme(Long.valueOf(i), "DocumentoFormulario" + String.format("%03d", i)));
    }

    BDDMockito.doNothing().when(informeRepository).deleteAll();

    Assertions.assertThatCode(
        // when: Delete all
        () -> informeService.deleteAll())
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void findAll_Unlimited_ReturnsFullInformeList() {
    // given: One hundred Informe
    List<Informe> informes = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      informes.add(generarMockInforme(Long.valueOf(i), "DocumentoFormulario" + String.format("%03d", i)));
    }

    BDDMockito
        .given(
            informeRepository.findAll(ArgumentMatchers.<Specification<Informe>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(informes));

    // when: find unlimited
    Page<Informe> page = informeService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred Informes
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isZero();
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred Informes
    List<Informe> informes = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      informes.add(generarMockInforme(Long.valueOf(i), "DocumentoFormulario" +
          String.format("%03d", i)));
    }

    BDDMockito.given(
        informeRepository.findAll(ArgumentMatchers.<Specification<Informe>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Informe>>() {
          @Override
          public Page<Informe> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Informe> content = informes.subList(fromIndex, toIndex);
            Page<Informe> page = new PageImpl<>(content, pageable, informes.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Informe> page = informeService.findAll(null, paging);

    // then: A Page with ten Informes are returned containing
    // descripcion='Informe031' to 'Informe040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Informe informe = page.getContent().get(i);
      Assertions.assertThat(informe.getId()).isEqualTo(j);
    }
  }

  @Test
  public void findAllByMemoriaId_ReturnsInformes() {

    Long informeId = 1L;
    // given: 10 Informes
    List<Informe> informes = new ArrayList<>();
    for (int i = 1; i <= 10; i++) {
      informes.add(generarMockInforme(Long.valueOf(i), "Informe" +
          String.format("%03d", i)));
    }

    BDDMockito
        .given(informeRepository.findByMemoriaId(ArgumentMatchers.anyLong(),
            ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(informes));

    // when: Se buscan todos las datos
    Page<Informe> result = informeService.findByMemoria(informeId,
        Pageable.unpaged());

    // then: Se recuperan todos los datos
    Assertions.assertThat(result.getContent()).isEqualTo(informes);
    Assertions.assertThat(result.getSize()).isEqualTo(10);
    Assertions.assertThat(result.getSize()).isEqualTo(informes.size());
    Assertions.assertThat(result.getTotalElements()).isEqualTo(informes.size());
  }

  @Test
  public void deleteInformeMemoria() {

    BDDMockito
        .given(informeRepository.findFirstByMemoriaIdOrderByVersionDesc(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockInforme(1L, "DocumentoFormulario1")));

    BDDMockito.doNothing().when(informeRepository).delete(ArgumentMatchers.<Informe>any());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> informeService.deleteLastInformeMemoria(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();

  }

  @Test
  public void deleteInformeMemoria_informeNull() {

    BDDMockito
        .given(informeRepository.findFirstByMemoriaIdOrderByVersionDesc(ArgumentMatchers.anyLong()))
        .willReturn(Optional.empty());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> informeService.deleteLastInformeMemoria(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();

  }

  /**
   * Función que devuelve un objeto Informe
   * 
   * @param id           id del Informe
   * @param documentoRef la referencia del documento
   * @return el objeto Informe
   */

  private Informe generarMockInforme(Long id, String documentoRef) {
    TipoActividad tipoActividad = new TipoActividad();
    tipoActividad.setId(1L);
    tipoActividad.setNombre("TipoActividad1");
    tipoActividad.setActivo(Boolean.TRUE);

    Set<PeticionEvaluacionTitulo> peTitulo = new HashSet<>();
    peTitulo.add(new PeticionEvaluacionTitulo(Language.ES, "PeticionEvaluacion1"));
    Set<PeticionEvaluacionResumen> resumen = new HashSet<>();
    resumen.add(new PeticionEvaluacionResumen(Language.ES, "Resumen"));
    Set<PeticionEvaluacionObjetivos> objetivos = new HashSet<>();
    objetivos.add(new PeticionEvaluacionObjetivos(Language.ES, "Objetivos1"));
    Set<PeticionEvaluacionDisMetodologico> disMetodologico = new HashSet<>();
    disMetodologico.add(new PeticionEvaluacionDisMetodologico(Language.ES, "DiseñoMetodologico1"));
    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(id);
    peticionEvaluacion.setCodigo("Codigo1");
    peticionEvaluacion.setDisMetodologico(disMetodologico);
    peticionEvaluacion.setFechaFin(Instant.now());
    peticionEvaluacion.setFechaInicio(Instant.now());
    peticionEvaluacion.setExisteFinanciacion(false);
    peticionEvaluacion.setObjetivos(objetivos);
    peticionEvaluacion.setResumen(resumen);
    peticionEvaluacion.setSolicitudConvocatoriaRef("Referencia solicitud convocatoria");
    peticionEvaluacion.setTieneFondosPropios(Boolean.FALSE);
    peticionEvaluacion.setTipoActividad(tipoActividad);
    peticionEvaluacion.setTitulo(peTitulo);
    peticionEvaluacion.setPersonaRef("user-001");
    peticionEvaluacion.setValorSocial(TipoValorSocial.ENSENIANZA_SUPERIOR);
    peticionEvaluacion.setActivo(Boolean.TRUE);

    Comite comite = new Comite();
    comite.setId(1L);
    comite.setCodigo("Comite1");
    comite.setActivo(Boolean.TRUE);

    TipoEstadoMemoria tipoEstadoMemoria = new TipoEstadoMemoria();
    tipoEstadoMemoria.setId(1L);
    tipoEstadoMemoria.setNombre("En elaboración");
    tipoEstadoMemoria.setActivo(Boolean.TRUE);

    EstadoRetrospectiva estadoRetrospectiva = new EstadoRetrospectiva();
    estadoRetrospectiva.setId(1L);
    estadoRetrospectiva.setNombre("Pendiente");
    estadoRetrospectiva.setActivo(Boolean.TRUE);

    Retrospectiva retrospectiva = new Retrospectiva();
    retrospectiva.setId(id);
    retrospectiva.setEstadoRetrospectiva(estadoRetrospectiva);
    retrospectiva.setFechaRetrospectiva(Instant.now());

    Set<MemoriaTitulo> mTitulo = new HashSet<>();
    mTitulo.add(new MemoriaTitulo(Language.ES, "Memoria1"));
    Memoria memoria = new Memoria();
    memoria.setId(1L);
    memoria.setNumReferencia("numRef-001");
    memoria.setPeticionEvaluacion(peticionEvaluacion);
    memoria.setComite(comite);
    memoria.setTitulo(mTitulo);
    memoria.setPersonaRef("user-001");
    memoria.setTipo(Memoria.Tipo.NUEVA);
    memoria.setEstadoActual(tipoEstadoMemoria);
    memoria.setFechaEnvioSecretaria(Instant.now());
    memoria.setRequiereRetrospectiva(Boolean.FALSE);
    memoria.setRetrospectiva(retrospectiva);
    memoria.setVersion(3);
    memoria.setActivo(Boolean.TRUE);

    TipoEvaluacion tipoEvaluacion = new TipoEvaluacion();
    tipoEvaluacion.setId(1L);
    tipoEvaluacion.setActivo(true);
    tipoEvaluacion.setNombre("Memoria");

    Informe informe = new Informe();
    informe.setId(id);
    informe.setMemoria(memoria);
    informe.setVersion(3);
    informe.setTipoEvaluacion(tipoEvaluacion);

    return informe;
  }
}