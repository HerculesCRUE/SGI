package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.SolicitudDocumentoNotFoundException;
import org.crue.hercules.sgi.csp.model.SolicitudDocumento;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.repository.SolicitudDocumentoRepository;
import org.crue.hercules.sgi.csp.service.impl.SolicitudDocumentoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class SolicitudDocumentoServiceTest extends BaseServiceTest {

  @Mock
  private SolicitudDocumentoRepository solicitudDocumentoRepository;

  @Mock
  private SolicitudService solicitudService;

  private SolicitudDocumentoService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new SolicitudDocumentoServiceImpl(solicitudDocumentoRepository, solicitudService);
  }

  @Test
  public void create_ReturnsSolicitudDocumento() {
    // given: new SolicitudDocumento
    SolicitudDocumento newSolicitudDocumento = generarSolicitudDocumento(null, 1L, 1L);

    BDDMockito.given(solicitudDocumentoRepository.save(ArgumentMatchers.<SolicitudDocumento>any()))
        .willAnswer(new Answer<SolicitudDocumento>() {
          @Override
          public SolicitudDocumento answer(InvocationOnMock invocation) throws Throwable {
            SolicitudDocumento givenData = invocation.getArgument(0, SolicitudDocumento.class);
            SolicitudDocumento newData = new SolicitudDocumento();
            BeanUtils.copyProperties(givenData, newData);
            newData.setId(1L);
            return newData;
          }
        });

    // when: create SolicitudDocumento
    SolicitudDocumento createdSolicitudDocumento = service.create(newSolicitudDocumento);

    // then: new SolicitudDocumento is created
    Assertions.assertThat(createdSolicitudDocumento).isNotNull();
    Assertions.assertThat(createdSolicitudDocumento.getId()).isNotNull();
    Assertions.assertThat(createdSolicitudDocumento.getSolicitudId()).isEqualTo(newSolicitudDocumento.getSolicitudId());
    Assertions.assertThat(createdSolicitudDocumento.getTipoDocumento().getId())
        .isEqualTo(newSolicitudDocumento.getTipoDocumento().getId());
    Assertions.assertThat(createdSolicitudDocumento.getComentario()).isEqualTo(newSolicitudDocumento.getComentario());
    Assertions.assertThat(createdSolicitudDocumento.getDocumentoRef())
        .isEqualTo(newSolicitudDocumento.getDocumentoRef());
    Assertions.assertThat(createdSolicitudDocumento.getNombre()).isEqualTo(newSolicitudDocumento.getNombre());

  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: a SolicitudDocumento with id filled
    SolicitudDocumento newSolicitudDocumento = generarSolicitudDocumento(1L, 1L, 1L);

    Assertions.assertThatThrownBy(
        // when: create SolicitudDocumento
        () -> service.create(newSolicitudDocumento))
        // then: throw exception as id can't be provided
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void create_WithoutSolicitud_ThrowsNotFoundException() throws Exception {
    // given: solicitud id null
    SolicitudDocumento solicitudDocumento = generarSolicitudDocumento(1L, 1L, 1L);
    solicitudDocumento.setSolicitudId(null);

    Assertions.assertThatThrownBy(
        // when: update non existing SolicitudDocumento
        () -> service.create(solicitudDocumento))
        // then: NotFoundException is thrown
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void create_WithoutNombreDocumento_ThrowsIllegalArgumentException() {
    // given: a SolicitudDocumento with nombre documento null
    SolicitudDocumento newSolicitudDocumento = generarSolicitudDocumento(null, 1L, 1L);
    newSolicitudDocumento.setNombre(null);

    Assertions.assertThatThrownBy(
        // when: create SolicitudDocumento
        () -> service.create(newSolicitudDocumento))
        // then: throw exception as id can't be provided
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void create_WithoutDocumentoRef_ThrowsIllegalArgumentException() {
    // given: a SolicitudDocumento with documento ref null
    SolicitudDocumento newSolicitudDocumento = generarSolicitudDocumento(null, 1L, 1L);
    newSolicitudDocumento.setDocumentoRef(null);

    Assertions.assertThatThrownBy(
        // when: create SolicitudDocumento
        () -> service.create(newSolicitudDocumento))
        // then: throw exception as id can't be provided
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_WithExistingId_ReturnsSolicitudDocumento() {
    // given: existing SolicitudDocumento
    SolicitudDocumento solicitudDocumento = generarSolicitudDocumento(1L, 1L, 1L);

    BDDMockito.given(solicitudDocumentoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudDocumento));

    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    BDDMockito.given(solicitudDocumentoRepository.save(ArgumentMatchers.<SolicitudDocumento>any()))
        .willAnswer(new Answer<SolicitudDocumento>() {
          @Override
          public SolicitudDocumento answer(InvocationOnMock invocation) throws Throwable {
            SolicitudDocumento givenData = invocation.getArgument(0, SolicitudDocumento.class);
            givenData.setComentario("comentarios-modificado");
            return givenData;
          }
        });

    // when: update SolicitudDocumento
    SolicitudDocumento updated = service.update(solicitudDocumento);

    // then: SolicitudDocumento is updated
    Assertions.assertThat(updated).isNotNull();
    Assertions.assertThat(updated.getId()).isNotNull();
    Assertions.assertThat(updated.getId()).isEqualTo(solicitudDocumento.getId());
    Assertions.assertThat(updated.getSolicitudId()).isEqualTo(solicitudDocumento.getSolicitudId());
    Assertions.assertThat(updated.getTipoDocumento().getId()).isEqualTo(solicitudDocumento.getTipoDocumento().getId());
    Assertions.assertThat(updated.getComentario()).isEqualTo("comentarios-modificado");
    Assertions.assertThat(updated.getDocumentoRef()).isEqualTo(solicitudDocumento.getDocumentoRef());
    Assertions.assertThat(updated.getNombre()).isEqualTo(solicitudDocumento.getNombre());
  }

  @Test
  public void update_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    SolicitudDocumento solicitudDocumento = generarSolicitudDocumento(1L, 1L, 1L);

    BDDMockito.given(solicitudDocumentoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    Assertions.assertThatThrownBy(
        // when: update non existing SolicitudDocumento
        () -> service.update(solicitudDocumento))
        // then: NotFoundException is thrown
        .isInstanceOf(SolicitudDocumentoNotFoundException.class);
  }

  @Test
  public void update_WithoutSolicitud_ThrowsNotFoundException() throws Exception {
    // given: solicitud id null
    SolicitudDocumento solicitudDocumento = generarSolicitudDocumento(1L, 1L, 1L);
    solicitudDocumento.setSolicitudId(null);

    Assertions.assertThatThrownBy(
        // when: update non existing SolicitudDocumento
        () -> service.update(solicitudDocumento))
        // then: NotFoundException is thrown
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_WithoutNombreDocumento_ThrowsNotFoundException() throws Exception {
    // given: nombre documento null
    SolicitudDocumento solicitudDocumento = generarSolicitudDocumento(1L, 1L, 1L);
    solicitudDocumento.setNombre(null);

    Assertions.assertThatThrownBy(
        // when: update non existing SolicitudDocumento
        () -> service.update(solicitudDocumento))
        // then: NotFoundException is thrown
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_WithoutDocumentoRef_ThrowsNotFoundException() throws Exception {
    // given: nombre documento null
    SolicitudDocumento solicitudDocumento = generarSolicitudDocumento(1L, 1L, 1L);
    solicitudDocumento.setDocumentoRef(null);

    Assertions.assertThatThrownBy(
        // when: update non existing SolicitudDocumento
        () -> service.update(solicitudDocumento))
        // then: NotFoundException is thrown
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {
    // given: a SolicitudDocumento without id filled
    SolicitudDocumento solicitudDocumento = generarSolicitudDocumento(null, 1L, 1L);

    Assertions.assertThatThrownBy(
        // when: update SolicitudDocumento
        () -> service.update(solicitudDocumento))
        // then: throw exception as id must be provided
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithExistingId_ReturnsSolicitudDocumento() {
    // given: existing SolicitudDocumento
    Long id = 1L;

    BDDMockito.given(solicitudDocumentoRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(solicitudDocumentoRepository).deleteById(ArgumentMatchers.anyLong());

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

    BDDMockito.given(solicitudDocumentoRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: NotFoundException is thrown
        .isInstanceOf(SolicitudDocumentoNotFoundException.class);
  }

  @Test
  public void findById_WithExistingId_ReturnsSolicitudDocumento() throws Exception {
    // given: existing SolicitudDocumento
    SolicitudDocumento givenSolicitudDocumento = generarSolicitudDocumento(1L, 1L, 1L);

    BDDMockito.given(solicitudDocumentoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(givenSolicitudDocumento));

    // when: find by id SolicitudDocumento
    SolicitudDocumento solicitudDocumento = service.findById(givenSolicitudDocumento.getId());

    // then: returns SolicitudDocumento
    Assertions.assertThat(solicitudDocumento).isNotNull();
    Assertions.assertThat(solicitudDocumento.getId()).isNotNull();
    Assertions.assertThat(solicitudDocumento.getId()).isEqualTo(solicitudDocumento.getId());
    Assertions.assertThat(solicitudDocumento.getSolicitudId()).isEqualTo(solicitudDocumento.getSolicitudId());
    Assertions.assertThat(solicitudDocumento.getTipoDocumento().getId())
        .isEqualTo(solicitudDocumento.getTipoDocumento().getId());
    Assertions.assertThat(solicitudDocumento.getComentario()).isEqualTo(solicitudDocumento.getComentario());
    Assertions.assertThat(solicitudDocumento.getDocumentoRef()).isEqualTo(solicitudDocumento.getDocumentoRef());
    Assertions.assertThat(solicitudDocumento.getNombre()).isEqualTo(solicitudDocumento.getNombre());
  }

  @Test
  public void findById_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    Long id = 1L;
    BDDMockito.given(solicitudDocumentoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: find by non existing id
        () -> service.findById(id))
        // then: NotFoundException is thrown
        .isInstanceOf(SolicitudDocumentoNotFoundException.class);
  }

  @Test
  public void findAll_ReturnsPage() {
    // given: Una lista con 37 SolicitudDocumento
    Long solicitudId = 1L;
    List<SolicitudDocumento> solicitudDocumentos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      solicitudDocumentos.add(generarSolicitudDocumento(i, i, i));
    }

    BDDMockito.given(solicitudDocumentoRepository.findAll(ArgumentMatchers.<Specification<SolicitudDocumento>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<SolicitudDocumento>>() {
          @Override
          public Page<SolicitudDocumento> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > solicitudDocumentos.size() ? solicitudDocumentos.size() : toIndex;
            List<SolicitudDocumento> content = solicitudDocumentos.subList(fromIndex, toIndex);
            Page<SolicitudDocumento> page = new PageImpl<>(content, pageable, solicitudDocumentos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<SolicitudDocumento> page = service.findAllBySolicitud(solicitudId, null, paging);

    // then: Devuelve la pagina 3 con los Programa del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      SolicitudDocumento solicitudDocumento = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(solicitudDocumento.getId()).isEqualTo(i);
    }
  }

  /**
   * Función que devuelve un objeto SolicitudDocumento
   * 
   * @param solicitudDocumentoId
   * @param convocatoriaId
   * @param areaTematicaId
   * @return el objeto SolicitudDocumento
   */
  private SolicitudDocumento generarSolicitudDocumento(Long solicitudDocumentoId, Long solicitudId,
      Long tipoDocumentoId) {

    SolicitudDocumento solicitudDocumento = SolicitudDocumento.builder().id(solicitudDocumentoId)
        .solicitudId(solicitudId).comentario("comentarios-" + solicitudDocumentoId)
        .documentoRef("documentoRef-" + solicitudDocumentoId).nombre("nombreDocumento-" + solicitudDocumentoId)
        .tipoDocumento(TipoDocumento.builder().id(tipoDocumentoId).build()).build();

    return solicitudDocumento;
  }
}
