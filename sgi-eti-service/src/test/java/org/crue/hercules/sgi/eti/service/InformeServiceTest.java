package org.crue.hercules.sgi.eti.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.InformeNotFoundException;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Informe;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion.TipoValorSocial;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.crue.hercules.sgi.eti.model.Comite.Genero;
import org.crue.hercules.sgi.eti.repository.InformeRepository;
import org.crue.hercules.sgi.eti.service.impl.InformeServiceImpl;
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

    Assertions.assertThat(informe.getDocumentoRef()).isEqualTo("DocumentoFormulario1");

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
    Assertions.assertThat(informeCreado.getDocumentoRef()).isEqualTo("DocumentoFormularioNew");
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
    Assertions.assertThat(informeActualizado.getDocumentoRef()).isEqualTo("DocumentoFormulario1 actualizado");

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
      informes.add(generarMockInforme(Long.valueOf(i), "DocumentoFormulario" + String.format("%03d", i)));
    }

    BDDMockito
        .given(
            informeRepository.findAll(ArgumentMatchers.<Specification<Informe>>any(), ArgumentMatchers.<Pageable>any()))
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
      Assertions.assertThat(informe.getDocumentoRef()).isEqualTo("DocumentoFormulario" + String.format("%03d", j));
    }
  }

  @Test
  public void findAllByMemoriaId_ReturnsInformes() {

    Long informeId = 1L;
    // given: 10 Informes
    List<Informe> informes = new ArrayList<>();
    for (int i = 1; i <= 10; i++) {
      informes.add(generarMockInforme(Long.valueOf(i), "Informe" + String.format("%03d", i)));
    }

    BDDMockito.given(informeRepository.findByMemoriaId(ArgumentMatchers.anyLong(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(informes));

    // when: Se buscan todos las datos
    Page<Informe> result = informeService.findByMemoria(informeId, Pageable.unpaged());

    // then: Se recuperan todos los datos
    Assertions.assertThat(result.getContent()).isEqualTo(informes);
    Assertions.assertThat(result.getSize()).isEqualTo(10);
    Assertions.assertThat(result.getSize()).isEqualTo(informes.size());
    Assertions.assertThat(result.getTotalElements()).isEqualTo(informes.size());
  }

  @Test
  public void deleteInformeMemoria() {

    BDDMockito.given(informeRepository.findFirstByMemoriaIdOrderByVersionDesc(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockInforme(1L, "DocumentoFormulario1")));

    BDDMockito.doNothing().when(informeRepository).delete(ArgumentMatchers.<Informe>any());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> informeService.deleteInformeMemoria(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();

  }

  @Test
  public void deleteInformeMemoria_informeNull() {

    BDDMockito.given(informeRepository.findFirstByMemoriaIdOrderByVersionDesc(ArgumentMatchers.anyLong()))
        .willReturn(Optional.empty());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> informeService.deleteInformeMemoria(1L))
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

  public Informe generarMockInforme(Long id, String documentoRef) {
    TipoActividad tipoActividad = new TipoActividad();
    tipoActividad.setId(1L);
    tipoActividad.setNombre("TipoActividad1");
    tipoActividad.setActivo(Boolean.TRUE);

    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(id);
    peticionEvaluacion.setCodigo("Codigo1");
    peticionEvaluacion.setDisMetodologico("DiseñoMetodologico1");
    peticionEvaluacion.setExterno(Boolean.FALSE);
    peticionEvaluacion.setFechaFin(Instant.now());
    peticionEvaluacion.setFechaInicio(Instant.now());
    peticionEvaluacion.setExisteFinanciacion(false);
    peticionEvaluacion.setObjetivos("Objetivos1");
    peticionEvaluacion.setResumen("Resumen");
    peticionEvaluacion.setSolicitudConvocatoriaRef("Referencia solicitud convocatoria");
    peticionEvaluacion.setTieneFondosPropios(Boolean.FALSE);
    peticionEvaluacion.setTipoActividad(tipoActividad);
    peticionEvaluacion.setTitulo("PeticionEvaluacion1");
    peticionEvaluacion.setPersonaRef("user-001");
    peticionEvaluacion.setValorSocial(TipoValorSocial.ENSENIANZA_SUPERIOR);
    peticionEvaluacion.setActivo(Boolean.TRUE);

    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(1L, "Comite1", "nombreSecretario", "nombreInvestigacion", Genero.M, "nombreDecreto",
        "articulo", formulario, Boolean.TRUE);

    TipoMemoria tipoMemoria = new TipoMemoria();
    tipoMemoria.setId(id);
    tipoMemoria.setNombre("TipoMemoria1");
    tipoMemoria.setActivo(Boolean.TRUE);

    Memoria memoria = new Memoria(1L, "numRef-001", peticionEvaluacion, comite, "Memoria1", "user-001", tipoMemoria,
        new TipoEstadoMemoria(1L, "En elaboración", Boolean.TRUE), Instant.now(), Boolean.FALSE,
        new Retrospectiva(id, new EstadoRetrospectiva(1L, "Pendiente", Boolean.TRUE), Instant.now()), 3,
        "codOrganoCompetente", Boolean.TRUE, null);

    TipoEvaluacion tipoEvaluacion = new TipoEvaluacion();
    tipoEvaluacion.setId(1L);
    tipoEvaluacion.setActivo(true);
    tipoEvaluacion.setNombre("Memoria");

    Informe informe = new Informe();
    informe.setId(id);
    informe.setDocumentoRef(documentoRef);
    informe.setMemoria(memoria);
    informe.setVersion(3);
    informe.setTipoEvaluacion(tipoEvaluacion);

    return informe;
  }
}