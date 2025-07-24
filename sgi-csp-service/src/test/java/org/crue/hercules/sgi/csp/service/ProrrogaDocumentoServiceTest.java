package org.crue.hercules.sgi.csp.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ProrrogaDocumentoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoProrrogaNotFoundException;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucionNombre;
import org.crue.hercules.sgi.csp.model.ModeloTipoDocumento;
import org.crue.hercules.sgi.csp.model.ProrrogaDocumento;
import org.crue.hercules.sgi.csp.model.ProrrogaDocumentoComentario;
import org.crue.hercules.sgi.csp.model.ProrrogaDocumentoNombre;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.model.TipoDocumentoNombre;
import org.crue.hercules.sgi.csp.repository.ModeloTipoDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.ProrrogaDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoProrrogaRepository;
import org.crue.hercules.sgi.csp.service.impl.ProrrogaDocumentoServiceImpl;
import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;

/**
 * ProrrogaDocumentoServiceTest
 */
class ProrrogaDocumentoServiceTest extends BaseServiceTest {

  @Mock
  private ProrrogaDocumentoRepository repository;
  @Mock
  private ProyectoProrrogaRepository proyectoProrrogaRepository;
  @Mock
  private ModeloTipoDocumentoRepository modeloTipoDocumentoRepository;

  private ProrrogaDocumentoService service;

  @BeforeEach
  void setUp() throws Exception {
    service = new ProrrogaDocumentoServiceImpl(repository, proyectoProrrogaRepository, modeloTipoDocumentoRepository);
  }

  @Test
  void create_ReturnsProrrogaDocumento() {
    // given: new ProrrogaDocumento
    ProrrogaDocumento newProrrogaDocumento = generarMockProrrogaDocumento(1L, 1L, 1L);
    newProrrogaDocumento.setId(null);
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(newProrrogaDocumento);
    Proyecto proyecto = generarMockProyecto();

    BDDMockito.given(proyectoProrrogaRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoProrrogaRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));
    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloTipoDocumento));

    BDDMockito.given(repository.save(newProrrogaDocumento)).will((InvocationOnMock invocation) -> {
      ProrrogaDocumento prorrogaDocumentoCreado = invocation.getArgument(0);
      prorrogaDocumentoCreado.setId(1L);
      return prorrogaDocumentoCreado;
    });

    // when: create ProrrogaDocumento
    ProrrogaDocumento created = service.create(newProrrogaDocumento);

    // then: new ProrrogaDocumento is created
    Assertions.assertThat(created).as("isNotNull()").isNotNull();
    Assertions.assertThat(created.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(created.getProyectoProrrogaId()).as("getProyectoProrrogaId()")
        .isEqualTo(newProrrogaDocumento.getProyectoProrrogaId());
    Assertions.assertThat(created.getNombre()).as("getNombre()").isEqualTo(newProrrogaDocumento.getNombre());
    Assertions.assertThat(created.getDocumentoRef()).as("getDocumentoRef()")
        .isEqualTo(newProrrogaDocumento.getDocumentoRef());
    Assertions.assertThat(newProrrogaDocumento.getTipoDocumento().getId()).as("getTipoDocumento()")
        .isEqualTo(newProrrogaDocumento.getTipoDocumento().getId());
    Assertions.assertThat(created.getVisible()).as("getVisible()").isEqualTo(newProrrogaDocumento.getVisible());
    Assertions.assertThat(created.getComentario()).as("getComentario()")
        .isEqualTo(newProrrogaDocumento.getComentario());
  }

  @Test
  void create_WithId_ThrowsIllegalArgumentException() {
    // given: new ProrrogaDocumento with Id
    ProrrogaDocumento newProrrogaDocumento = generarMockProrrogaDocumento(1L, 1L, 1L);

    Assertions.assertThatThrownBy(
        // when: Create ProrrogaDocumento
        () -> service.create(newProrrogaDocumento))
        // then: throw exception as id can't be provided
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Identificador de Prórroga Documento debe ser nulo");
  }

  @Test
  void create_WithoutProyectoProrroga_ThrowsIllegalArgumentException() {
    // given: new ProrrogaDocumento without ProyectoProrroga
    ProrrogaDocumento newProrrogaDocumento = generarMockProrrogaDocumento(1L, 1L, 1L);
    newProrrogaDocumento.setId(null);
    newProrrogaDocumento.setProyectoProrrogaId(null);

    Assertions.assertThatThrownBy(
        // when: Create ProrrogaDocumento
        () -> service.create(newProrrogaDocumento))
        // then: throw exception as ProyectoProrroga is not provided
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Identificador de Proyecto Prórroga no puede ser nulo");
  }

  @Test
  void create_WithoutNombre_ThrowsIllegalArgumentException() {
    // given: new ProrrogaDocumento without Nombre
    ProrrogaDocumento newProrrogaDocumento = generarMockProrrogaDocumento(1L, 1L, 1L);
    newProrrogaDocumento.setId(null);
    newProrrogaDocumento.setNombre(null);

    Assertions.assertThatThrownBy(
        // when: Create ProrrogaDocumento
        () -> service.create(newProrrogaDocumento))
        // then: throw exception as Nombre is not provided
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Nombre de Prórroga Documento no puede ser nulo");
  }

  @Test
  void create_WithoutDocumentoRef_ThrowsIllegalArgumentException() {
    // given: new ProrrogaDocumento without DocumentoRef
    ProrrogaDocumento newProrrogaDocumento = generarMockProrrogaDocumento(1L, 1L, 1L);
    newProrrogaDocumento.setId(null);
    newProrrogaDocumento.setDocumentoRef(null);

    Assertions.assertThatThrownBy(
        // when: Create ProrrogaDocumento
        () -> service.create(newProrrogaDocumento))
        // then: throw exception as DocumentoRef is not provided
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Referencia documento de Prórroga Documento no puede ser nulo");
  }

  @Test
  void create_WithoutVisible_ThrowsIllegalArgumentException() {
    // given: new ProrrogaDocumento without Visible
    ProrrogaDocumento newProrrogaDocumento = generarMockProrrogaDocumento(1L, 1L, 1L);
    newProrrogaDocumento.setId(null);
    newProrrogaDocumento.setVisible(null);

    Assertions.assertThatThrownBy(
        // when: Create ProrrogaDocumento
        () -> service.create(newProrrogaDocumento))
        // then: throw exception as Visible is not provided
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Visible de Prórroga Documento no puede ser nulo");
  }

  @Test
  void create_WithNoExistingProyectoProrroga_ThrowsNotFoundException() {
    // given: new ProrrogaDocumento with no existing ProyectoProrroga
    ProrrogaDocumento newProrrogaDocumento = generarMockProrrogaDocumento(1L, 1L, 1L);
    newProrrogaDocumento.setId(null);

    BDDMockito.given(proyectoProrrogaRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Create ProrrogaDocumento
        () -> service.create(newProrrogaDocumento))
        // then: throw exception as Prorroga is not found
        .isInstanceOf(ProyectoProrrogaNotFoundException.class);
  }

  @Test
  void create_WithNoExistingModeloEjecucion_ThrowsIllegalArgumentException() {
    // given: new ProrrogaDocumento with no existing ModeloEjecucion
    ProrrogaDocumento newProrrogaDocumento = generarMockProrrogaDocumento(1L, 1L, 1L);
    newProrrogaDocumento.setId(null);

    BDDMockito.given(proyectoProrrogaRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoProrrogaRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: Create ProrrogaDocumento
        () -> service.create(newProrrogaDocumento))
        // then: throw exception as Prorroga is not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Modelo Ejecución de Proyecto Prórroga no existe");
  }

  @Test
  void create_WithDisabledModeloEjecucion_ThrowsIllegalArgumentException() {
    // given: new ProrrogaDocumento with disabled ModeloEjecucion
    ProrrogaDocumento newProrrogaDocumento = generarMockProrrogaDocumento(1L, 1L, 1L);
    newProrrogaDocumento.setId(null);
    Proyecto proyecto = generarMockProyecto();
    proyecto.getModeloEjecucion().setActivo(Boolean.FALSE);

    BDDMockito.given(proyectoProrrogaRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoProrrogaRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));

    Assertions.assertThatThrownBy(
        // when: Create ProrrogaDocumento
        () -> service.create(newProrrogaDocumento))
        // then: throw exception as ModeloEjecucion is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("%s de Modelo Ejecución no está activo", proyecto.getModeloEjecucion().getNombre());
  }

  @Test
  void create_WithoutModeloTipoDocumento_ThrowsIllegalArgumentException() {
    // given: new ProrrogaDocumento without ModeloTipoDocumento
    ProrrogaDocumento newProrrogaDocumento = generarMockProrrogaDocumento(1L, 1L, 1L);
    newProrrogaDocumento.setId(null);
    Proyecto proyecto = generarMockProyecto();

    BDDMockito.given(proyectoProrrogaRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoProrrogaRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));
    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: Create ProrrogaDocumento
        () -> service.create(newProrrogaDocumento))
        // then: throw exception as ModeloTipoDocumento is not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Tipo Documento de Modelo Ejecución no existe");
  }

  @Test
  void create_WithTipoDocumentoDisabled_ThrowsIllegalArgumentException() {
    // given: new ProrrogaDocumento with TipoDocumento disabled
    ProrrogaDocumento newProrrogaDocumento = generarMockProrrogaDocumento(1L, 1L, 1L);
    newProrrogaDocumento.setId(null);
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(newProrrogaDocumento);
    modeloTipoDocumento.getTipoDocumento().setActivo(Boolean.FALSE);
    Proyecto proyecto = generarMockProyecto();

    BDDMockito.given(proyectoProrrogaRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoProrrogaRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));
    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloTipoDocumento));

    Assertions.assertThatThrownBy(
        // when: Create ProrrogaDocumento
        () -> service.create(newProrrogaDocumento))
        // then: throw exception as TipoDocumento is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("%s de Tipo Documento no está activo", newProrrogaDocumento.getTipoDocumento().getNombre());
  }

  @Test
  void create_WithModeloTipoDocumentoDisabled_ThrowsIllegalArgumentException() {
    // given: new ProrrogaDocumento with ModeloTipoDocumento disabled
    ProrrogaDocumento newProrrogaDocumento = generarMockProrrogaDocumento(1L, 1L, 1L);
    newProrrogaDocumento.setId(null);
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(newProrrogaDocumento);
    modeloTipoDocumento.setActivo(Boolean.FALSE);
    Proyecto proyecto = generarMockProyecto();

    BDDMockito.given(proyectoProrrogaRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoProrrogaRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));
    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloTipoDocumento));

    Assertions.assertThatThrownBy(
        // when: Create ProrrogaDocumento
        () -> service.create(newProrrogaDocumento))
        // then: throw exception as ModeloTipoDocumento is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("%s de Modelo Tipo Documento no está activo",
            newProrrogaDocumento.getTipoDocumento().getNombre());
  }

  @Test
  void update_ReturnsProrrogaDocumento() {
    // given: updated ProrrogaDocumento
    ProrrogaDocumento originalProrrogaDocumento = generarMockProrrogaDocumento(1L, 1L, 1L);
    ProrrogaDocumento updatedProrrogaDocumento = generarMockProrrogaDocumento(1L, 1L, 1L);
    Set<ProrrogaDocumentoComentario> prorrogaDocumentoComentario = new HashSet<>();
    prorrogaDocumentoComentario.add(new ProrrogaDocumentoComentario(Language.ES, "comentario-modificado"));
    updatedProrrogaDocumento.setComentario(prorrogaDocumentoComentario);
    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(originalProrrogaDocumento);
    Proyecto proyecto = generarMockProyecto();

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(originalProrrogaDocumento));

    BDDMockito.given(proyectoProrrogaRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoProrrogaRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));
    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloTipoDocumento));

    BDDMockito.given(repository.save(ArgumentMatchers.<ProrrogaDocumento>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Update
    ProrrogaDocumento updated = service.update(updatedProrrogaDocumento);

    // then: ProrrogaDocumento is updated
    Assertions.assertThat(updated).as("isNotNull()").isNotNull();
    Assertions.assertThat(updated.getId()).as("getId()").isEqualTo(originalProrrogaDocumento.getId());
    Assertions.assertThat(updated.getProyectoProrrogaId()).as("getProyectoProrrogaId()")
        .isEqualTo(originalProrrogaDocumento.getProyectoProrrogaId());
    Assertions.assertThat(updated.getNombre()).as("getNombre()").isEqualTo(updatedProrrogaDocumento.getNombre());
    Assertions.assertThat(updated.getDocumentoRef()).as("getDocumentoRef()")
        .isEqualTo(updatedProrrogaDocumento.getDocumentoRef());
    Assertions.assertThat(originalProrrogaDocumento.getTipoDocumento().getId()).as("getTipoDocumento()")
        .isEqualTo(originalProrrogaDocumento.getTipoDocumento().getId());
    Assertions.assertThat(updated.getVisible()).as("getPublico()").isEqualTo(originalProrrogaDocumento.getVisible());
    Assertions.assertThat(updated.getComentario()).as("getComentario()")
        .isEqualTo(updatedProrrogaDocumento.getComentario());
  }

  @Test
  void update_WithoutId_ThrowsIllegalArgumentException() {
    // given: a updated ProrrogaDocumento with id filled
    ProrrogaDocumento updatedProrrogaDocumento = generarMockProrrogaDocumento(1L, 1L, 1L);
    updatedProrrogaDocumento.setId(null);

    Assertions.assertThatThrownBy(
        // when: update ProrrogaDocumento
        () -> service.update(updatedProrrogaDocumento))
        // then: throw exception as id can't be provided
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Identificador de Prórroga Documento no puede ser nulo");
  }

  @Test
  void update_WithoutProyectoProrroga_ThrowsIllegalArgumentException() {
    // given: a updated ProrrogaDocumento without ProyectoProrroga
    ProrrogaDocumento updatedProrrogaDocumento = generarMockProrrogaDocumento(1L, 1L, 1L);
    updatedProrrogaDocumento.setProyectoProrrogaId(null);

    Assertions.assertThatThrownBy(
        // when: update ProrrogaDocumento
        () -> service.update(updatedProrrogaDocumento))
        // then: throw exception as ProyectoProrroga is not provided
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Identificador de Proyecto Prórroga no puede ser nulo");
  }

  @Test
  void update_WithoutNombre_ThrowsIllegalArgumentException() {
    // given: a updated ProrrogaDocumento without Nombre
    ProrrogaDocumento updatedProrrogaDocumento = generarMockProrrogaDocumento(1L, 1L, 1L);
    updatedProrrogaDocumento.setNombre(null);

    Assertions.assertThatThrownBy(
        // when: update ProrrogaDocumento
        () -> service.update(updatedProrrogaDocumento))
        // then: throw exception as Publico is not provided
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Nombre de Prórroga Documento no puede ser nulo");
  }

  @Test
  void update_WithoutDocumentoRef_ThrowsIllegalArgumentException() {
    // given: a updated ProrrogaDocumento without DocumentoRef
    ProrrogaDocumento updatedProrrogaDocumento = generarMockProrrogaDocumento(1L, 1L, 1L);
    updatedProrrogaDocumento.setDocumentoRef(null);

    Assertions.assertThatThrownBy(
        // when: update ProrrogaDocumento
        () -> service.update(updatedProrrogaDocumento))
        // then: throw exception as DocumentoRef is not provided
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Referencia documento de Prórroga Documento no puede ser nulo");
  }

  @Test
  void update_WithoutVisible_ThrowsIllegalArgumentException() {
    // given: a updated ProrrogaDocumento without Visible
    ProrrogaDocumento updatedProrrogaDocumento = generarMockProrrogaDocumento(1L, 1L, 1L);
    updatedProrrogaDocumento.setVisible(null);

    Assertions.assertThatThrownBy(
        // when: update ProrrogaDocumento
        () -> service.update(updatedProrrogaDocumento))
        // then: throw exception as Visible is not provided
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Visible de Prórroga Documento no puede ser nulo");
  }

  @Test
  void update_WithNoExistingId_ThrowsNotFoundException() {
    // given: a updated ProrrogaDocumento with no existing Id
    ProrrogaDocumento updatedProrrogaDocumento = generarMockProrrogaDocumento(1L, 1L, 1L);

    Assertions.assertThatThrownBy(
        // when: update ProrrogaDocumento
        () -> service.update(updatedProrrogaDocumento))
        // then: throw exception as ID is not found
        .isInstanceOf(ProrrogaDocumentoNotFoundException.class);
  }

  @Test
  void update_WithNoExistingProyectoProrroga_ThrowsNotFoundException() {
    // given: a updated ProrrogaDocumento with no existing Prorroga
    ProrrogaDocumento originalProrrogaDocumento = generarMockProrrogaDocumento(1L, 1L, 1L);
    ProrrogaDocumento updatedProrrogaDocumento = generarMockProrrogaDocumento(1L, 1L, 1L);
    Set<ProrrogaDocumentoComentario> prorrogaDocumentoComentario = new HashSet<>();
    prorrogaDocumentoComentario.add(new ProrrogaDocumentoComentario(Language.ES, "comentario-modificado"));
    updatedProrrogaDocumento.setComentario(prorrogaDocumentoComentario);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(originalProrrogaDocumento));
    BDDMockito.given(proyectoProrrogaRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: update ProrrogaDocumento
        () -> service.update(updatedProrrogaDocumento))
        // then: throw exception as ID is not found
        .isInstanceOf(ProyectoProrrogaNotFoundException.class);
  }

  @Test
  void update_WithNoExistingModeloEjecucion_ThrowsIllegalArgumentException() {
    // given: ProrrogaDocumento with no existing ModeloEjecucion
    ProrrogaDocumento originalProrrogaDocumento = generarMockProrrogaDocumento(1L, 1L, 1L);
    ProrrogaDocumento updatedProrrogaDocumento = generarMockProrrogaDocumento(1L, 1L, 1L);
    Set<ProrrogaDocumentoComentario> prorrogaDocumentoComentario = new HashSet<>();
    prorrogaDocumentoComentario.add(new ProrrogaDocumentoComentario(Language.ES, "comentario-modificado"));
    updatedProrrogaDocumento.setComentario(prorrogaDocumentoComentario);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(originalProrrogaDocumento));

    BDDMockito.given(proyectoProrrogaRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoProrrogaRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update ProrrogaDocumento
        () -> service.update(updatedProrrogaDocumento))
        // then: throw exception as Prorroga is not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Modelo Ejecución de Proyecto Prórroga no existe");
  }

  @Test
  void update_WithDisabledModeloEjecucion_ThrowsIllegalArgumentException() {
    // given: ProrrogaDocumento with disabled ModeloEjecucion
    ProrrogaDocumento originalProrrogaDocumento = generarMockProrrogaDocumento(1L, 1L, 1L);
    ProrrogaDocumento updatedProrrogaDocumento = generarMockProrrogaDocumento(1L, 1L, 1L);
    Set<ProrrogaDocumentoComentario> prorrogaDocumentoComentario = new HashSet<>();
    prorrogaDocumentoComentario.add(new ProrrogaDocumentoComentario(Language.ES, "comentario-modificado"));
    updatedProrrogaDocumento.setComentario(prorrogaDocumentoComentario);
    Proyecto proyecto = generarMockProyecto();

    proyecto.getModeloEjecucion().setActivo(Boolean.FALSE);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(originalProrrogaDocumento));

    BDDMockito.given(proyectoProrrogaRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoProrrogaRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));

    Assertions.assertThatThrownBy(
        // when: update ProrrogaDocumento
        () -> service.update(updatedProrrogaDocumento))
        // then: throw exception as ModeloEjecucion is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("%s de Modelo Ejecución no está activo", proyecto.getModeloEjecucion().getNombre());
  }

  @Test
  void update_WithoutModeloTipoDocumento_ThrowsIllegalArgumentException() {
    // given: ProrrogaDocumento without ModeloTipoDocumento
    ProrrogaDocumento originalProrrogaDocumento = generarMockProrrogaDocumento(1L, 1L, 1L);
    ProrrogaDocumento updatedProrrogaDocumento = generarMockProrrogaDocumento(1L, 1L, 1L);
    Set<ProrrogaDocumentoComentario> prorrogaDocumentoComentario = new HashSet<>();
    prorrogaDocumentoComentario.add(new ProrrogaDocumentoComentario(Language.ES, "comentario-modificado"));
    updatedProrrogaDocumento.setComentario(prorrogaDocumentoComentario);
    Proyecto proyecto = generarMockProyecto();

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(originalProrrogaDocumento));

    BDDMockito.given(proyectoProrrogaRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoProrrogaRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));
    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update ProrrogaDocumento
        () -> service.update(updatedProrrogaDocumento))
        // then: throw exception as ModeloTipoDocumento is not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Tipo Documento de Modelo Ejecución no existe");
  }

  @Test
  void update_WithTipoDocumentoDisabled_ThrowsIllegalArgumentException() {
    // given: ProrrogaDocumento with TipoDocumento disabled
    ProrrogaDocumento originalProrrogaDocumento = generarMockProrrogaDocumento(1L, 1L, 1L);
    ProrrogaDocumento updatedProrrogaDocumento = generarMockProrrogaDocumento(1L, 1L, 1L);
    Set<ProrrogaDocumentoComentario> prorrogaDocumentoComentario = new HashSet<>();
    prorrogaDocumentoComentario.add(new ProrrogaDocumentoComentario(Language.ES, "comentario-modificado"));
    updatedProrrogaDocumento.setComentario(prorrogaDocumentoComentario);

    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(originalProrrogaDocumento);
    modeloTipoDocumento.getTipoDocumento().setActivo(Boolean.FALSE);

    Proyecto proyecto = generarMockProyecto();

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(originalProrrogaDocumento));

    BDDMockito.given(proyectoProrrogaRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoProrrogaRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));
    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloTipoDocumento));

    Assertions.assertThatThrownBy(
        // when: update ProrrogaDocumento
        () -> service.update(updatedProrrogaDocumento))
        // then: throw exception as TipoDocumento is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("%s de Tipo Documento no está activo", updatedProrrogaDocumento.getTipoDocumento().getNombre());
  }

  @Test
  void update_WithModeloTipoDocumentoDisabled_ThrowsIllegalArgumentException() {
    // given: ProrrogaDocumento with ModeloTipoDocumento disabled
    ProrrogaDocumento originalProrrogaDocumento = generarMockProrrogaDocumento(1L, 1L, 1L);
    ProrrogaDocumento updatedProrrogaDocumento = generarMockProrrogaDocumento(1L, 1L, 1L);
    Set<ProrrogaDocumentoComentario> prorrogaDocumentoComentario = new HashSet<>();
    prorrogaDocumentoComentario.add(new ProrrogaDocumentoComentario(Language.ES, "comentario-modificado"));
    updatedProrrogaDocumento.setComentario(prorrogaDocumentoComentario);

    ModeloTipoDocumento modeloTipoDocumento = generarMockModeloTipoDocumento(originalProrrogaDocumento);
    modeloTipoDocumento.setActivo(Boolean.FALSE);

    Proyecto proyecto = generarMockProyecto();

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(originalProrrogaDocumento));

    BDDMockito.given(proyectoProrrogaRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoProrrogaRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto.getModeloEjecucion()));
    BDDMockito
        .given(modeloTipoDocumentoRepository.findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(
            ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any(), ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(modeloTipoDocumento));

    Assertions.assertThatThrownBy(
        // when: update ProrrogaDocumento
        () -> service.update(updatedProrrogaDocumento))
        // then: throw exception as ModeloTipoDocumento is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("%s de Modelo Tipo Documento no está activo",
            updatedProrrogaDocumento.getTipoDocumento().getNombre());
  }

  @Test
  void delete_WithExistingId_DoesNotThrowAnyException() {
    // given: existing prorrogaDocumento
    Long id = 1L;

    BDDMockito.given(repository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(repository).deleteById(ArgumentMatchers.<Long>any());

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
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void delete_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    Long id = 1L;

    BDDMockito.given(repository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: NotFoundException is thrown
        .isInstanceOf(ProrrogaDocumentoNotFoundException.class);
  }

  @Test
  void findById_ReturnsProrrogaDocumento() {
    // given: existing ProrrogaDocumento
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado))
        .willReturn(Optional.of(generarMockProrrogaDocumento(idBuscado, 1L, 1L)));

    // when: find ProrrogaDocumento by id
    ProrrogaDocumento prorrogaDocumento = service.findById(idBuscado);

    // then: ProrrogaDocumento is found
    Assertions.assertThat(prorrogaDocumento).as("isNotNull()").isNotNull();
    Assertions.assertThat(prorrogaDocumento.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(prorrogaDocumento.getProyectoProrrogaId()).as("getProyectoProrrogaId()")
        .isEqualTo(prorrogaDocumento.getProyectoProrrogaId());
    Assertions.assertThat(prorrogaDocumento.getDocumentoRef()).as("getDocumentoRef()")
        .isEqualTo("documentoRef-" + String.format("%03d", idBuscado));
    Assertions.assertThat(I18nHelper.getValueForLanguage(prorrogaDocumento.getNombre(), Language.ES)).as("getNombre()")
        .isEqualTo("prorroga-documento-" + String.format("%03d", idBuscado));
    Assertions.assertThat(prorrogaDocumento.getTipoDocumento().getId()).as("getTipoDocumento().getId()").isEqualTo(1L);
    Assertions.assertThat(I18nHelper.getValueForLanguage(prorrogaDocumento.getComentario(), Language.ES))
        .as("getComentario()")
        .isEqualTo("comentario-prorroga-documento-" + String.format("%03d", idBuscado));
    Assertions.assertThat(prorrogaDocumento.getVisible()).as("getVisible()").isEqualTo(Boolean.TRUE);
  }

  @Test
  void findById_WithIdNotExist_ThrowsProrrogaDocumentoNotFoundException() throws Exception {
    // given: Ningun ProrrogaDocumento con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el ProrrogaDocumento por su id
    // then: lanza un ProyectoProrrogaNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(ProrrogaDocumentoNotFoundException.class);
  }

  private Proyecto generarMockProyecto() {
    Set<ModeloEjecucionNombre> nombreModeloEjecucion = new HashSet<>();
    nombreModeloEjecucion.add(new ModeloEjecucionNombre(Language.ES, "nombreModeloEjecucion"));
    // @formatter:off
    return Proyecto.builder()
        .id(1L)
        .modeloEjecucion(ModeloEjecucion.builder()
            .id(1L)
            .nombre(nombreModeloEjecucion)
            .activo(Boolean.TRUE)
            .build())
        .activo(Boolean.TRUE)
        .build();
    // @formatter:on
  }

  /**
   * Función que devuelve un objeto ProrrogaDocumento
   * 
   * @param id                 id del ProrrogaDocumento
   * @param proyectoProrrogaId id del ProyectoProrroga
   * @return el objeto ProrrogaDocumento
   */
  private ProrrogaDocumento generarMockProrrogaDocumento(Long id, Long proyectoProrrogaId, Long tipoDocumentoId) {
    Set<TipoDocumentoNombre> nombreTipoDocumento = new HashSet<>();
    nombreTipoDocumento.add(new TipoDocumentoNombre(Language.ES, "nombreTipoDocumento"));

    Set<ProrrogaDocumentoNombre> prorrogaDocumentoNombre = new HashSet<>();
    prorrogaDocumentoNombre.add(new ProrrogaDocumentoNombre(Language.ES,
        "prorroga-documento-" + (id == null ? "" : String.format("%03d", id))));

    Set<ProrrogaDocumentoComentario> prorrogaDocumentoComentario = new HashSet<>();
    prorrogaDocumentoComentario.add(new ProrrogaDocumentoComentario(Language.ES,
        "comentario-prorroga-documento-" + (id == null ? "" : String.format("%03d", id))));

    // @formatter:off
    return ProrrogaDocumento.builder()
        .id(id)
        .proyectoProrrogaId(proyectoProrrogaId)
        .nombre(prorrogaDocumentoNombre)
        .documentoRef("documentoRef-" + (id == null ? "" : String.format("%03d", id)))
        .tipoDocumento(TipoDocumento.builder()
            .id(tipoDocumentoId)
            .nombre(nombreTipoDocumento)
            .activo(Boolean.TRUE)
            .build())
        .comentario(prorrogaDocumentoComentario)
        .visible(Boolean.TRUE)
        .build();
    // @formatter:on
  }

  /**
   * Función que devuelve un objeto ModeloTipoDocumento a partir de un objeto
   * ProrrogaDocumento
   * 
   * @param prorrogaDocumento
   * @return el objeto ModeloTipoFase
   */
  private ModeloTipoDocumento generarMockModeloTipoDocumento(ProrrogaDocumento prorrogaDocumento) {

    // @formatter:off
    return ModeloTipoDocumento.builder()
        .id(prorrogaDocumento.getId() == null ? 1L : prorrogaDocumento.getId())
        .modeloEjecucion(generarMockProyecto().getModeloEjecucion())
        .modeloTipoFase(null)
        .tipoDocumento(prorrogaDocumento.getTipoDocumento())
        .activo(Boolean.TRUE)
        .build();
    // @formatter:on
  }

}
