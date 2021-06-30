package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ProyectoPeriodoSeguimientoDocumentoNotFoundException;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimientoDocumento;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.repository.ProyectoPeriodoSeguimientoDocumentoRepository;
import org.crue.hercules.sgi.csp.service.impl.ProyectoPeriodoSeguimientoDocumentoServiceImpl;
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
public class ProyectoPeriodoSeguimientoDocumentoServiceTest extends BaseServiceTest {

  @Mock
  private ProyectoPeriodoSeguimientoDocumentoRepository proyectoPeriodoSeguimientoDocumentoRepository;

  private ProyectoPeriodoSeguimientoDocumentoService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ProyectoPeriodoSeguimientoDocumentoServiceImpl(proyectoPeriodoSeguimientoDocumentoRepository);
  }

  @Test
  public void create_ReturnsProyectoPeriodoSeguimientoDocumento() {
    // given: new ProyectoPeriodoSeguimientoDocumento
    ProyectoPeriodoSeguimientoDocumento newProyectoPeriodoSeguimientoDocumento = generarMockProyectoPeriodoSeguimientoDocumento(
        null);

    BDDMockito
        .given(proyectoPeriodoSeguimientoDocumentoRepository
            .save(ArgumentMatchers.<ProyectoPeriodoSeguimientoDocumento>any()))
        .willAnswer(new Answer<ProyectoPeriodoSeguimientoDocumento>() {
          @Override
          public ProyectoPeriodoSeguimientoDocumento answer(InvocationOnMock invocation) throws Throwable {
            ProyectoPeriodoSeguimientoDocumento givenData = invocation.getArgument(0,
                ProyectoPeriodoSeguimientoDocumento.class);
            ProyectoPeriodoSeguimientoDocumento newData = new ProyectoPeriodoSeguimientoDocumento();
            BeanUtils.copyProperties(givenData, newData);
            newData.setId(1L);
            return newData;
          }
        });

    // when: create ProyectoPeriodoSeguimientoDocumento
    ProyectoPeriodoSeguimientoDocumento createdProyectoPeriodoSeguimientoDocumento = service
        .create(newProyectoPeriodoSeguimientoDocumento);

    // then: new ProyectoPeriodoSeguimientoDocumento is created
    Assertions.assertThat(createdProyectoPeriodoSeguimientoDocumento).isNotNull();
    Assertions.assertThat(createdProyectoPeriodoSeguimientoDocumento.getId()).isNotNull();
    Assertions.assertThat(createdProyectoPeriodoSeguimientoDocumento.getProyectoPeriodoSeguimientoId())
        .isEqualTo(newProyectoPeriodoSeguimientoDocumento.getProyectoPeriodoSeguimientoId());
    Assertions.assertThat(createdProyectoPeriodoSeguimientoDocumento.getTipoDocumento().getId())
        .isEqualTo(newProyectoPeriodoSeguimientoDocumento.getTipoDocumento().getId());
    Assertions.assertThat(createdProyectoPeriodoSeguimientoDocumento.getComentario())
        .isEqualTo(newProyectoPeriodoSeguimientoDocumento.getComentario());
    Assertions.assertThat(createdProyectoPeriodoSeguimientoDocumento.getDocumentoRef())
        .isEqualTo(newProyectoPeriodoSeguimientoDocumento.getDocumentoRef());
    Assertions.assertThat(createdProyectoPeriodoSeguimientoDocumento.getNombre())
        .isEqualTo(newProyectoPeriodoSeguimientoDocumento.getNombre());

  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: a ProyectoPeriodoSeguimientoDocumento with id filled
    ProyectoPeriodoSeguimientoDocumento newProyectoPeriodoSeguimientoDocumento = generarMockProyectoPeriodoSeguimientoDocumento(
        1L);

    Assertions.assertThatThrownBy(
        // when: create ProyectoPeriodoSeguimientoDocumento
        () -> service.create(newProyectoPeriodoSeguimientoDocumento))
        // then: throw exception as id can't be provided
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void create_WithoutProyectoPeriodoSeguimiento_ThrowsNotFoundException() throws Exception {
    // given: solicitud id null
    ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimientoDocumento = generarMockProyectoPeriodoSeguimientoDocumento(
        1L);
    proyectoPeriodoSeguimientoDocumento.setProyectoPeriodoSeguimientoId(null);

    Assertions.assertThatThrownBy(
        // when: update non existing ProyectoPeriodoSeguimientoDocumento
        () -> service.create(proyectoPeriodoSeguimientoDocumento))
        // then: NotFoundException is thrown
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void create_WithoutNombreDocumento_ThrowsIllegalArgumentException() {
    // given: a ProyectoPeriodoSeguimientoDocumento with nombre documento null
    ProyectoPeriodoSeguimientoDocumento newProyectoPeriodoSeguimientoDocumento = generarMockProyectoPeriodoSeguimientoDocumento(
        null);
    newProyectoPeriodoSeguimientoDocumento.setNombre(null);

    Assertions.assertThatThrownBy(
        // when: create ProyectoPeriodoSeguimientoDocumento
        () -> service.create(newProyectoPeriodoSeguimientoDocumento))
        // then: throw exception as id can't be provided
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void create_WithoutDocumentoRef_ThrowsIllegalArgumentException() {
    // given: a ProyectoPeriodoSeguimientoDocumento with documento ref null
    ProyectoPeriodoSeguimientoDocumento newProyectoPeriodoSeguimientoDocumento = generarMockProyectoPeriodoSeguimientoDocumento(
        null);
    newProyectoPeriodoSeguimientoDocumento.setDocumentoRef(null);

    Assertions.assertThatThrownBy(
        // when: create ProyectoPeriodoSeguimientoDocumento
        () -> service.create(newProyectoPeriodoSeguimientoDocumento))
        // then: throw exception as id can't be provided
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_WithExistingId_ReturnsProyectoPeriodoSeguimientoDocumento() {
    // given: existing ProyectoPeriodoSeguimientoDocumento
    ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimientoDocumento = generarMockProyectoPeriodoSeguimientoDocumento(
        1L);

    BDDMockito.given(proyectoPeriodoSeguimientoDocumentoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoPeriodoSeguimientoDocumento));

    BDDMockito
        .given(proyectoPeriodoSeguimientoDocumentoRepository
            .save(ArgumentMatchers.<ProyectoPeriodoSeguimientoDocumento>any()))
        .willAnswer(new Answer<ProyectoPeriodoSeguimientoDocumento>() {
          @Override
          public ProyectoPeriodoSeguimientoDocumento answer(InvocationOnMock invocation) throws Throwable {
            ProyectoPeriodoSeguimientoDocumento givenData = invocation.getArgument(0,
                ProyectoPeriodoSeguimientoDocumento.class);
            givenData.setComentario("comentarios-modificado");
            return givenData;
          }
        });

    // when: update ProyectoPeriodoSeguimientoDocumento
    ProyectoPeriodoSeguimientoDocumento updated = service.update(proyectoPeriodoSeguimientoDocumento);

    // then: ProyectoPeriodoSeguimientoDocumento is updated
    Assertions.assertThat(updated).isNotNull();
    Assertions.assertThat(updated.getId()).isNotNull();
    Assertions.assertThat(updated.getId()).isEqualTo(proyectoPeriodoSeguimientoDocumento.getId());
    Assertions.assertThat(updated.getProyectoPeriodoSeguimientoId())
        .isEqualTo(proyectoPeriodoSeguimientoDocumento.getProyectoPeriodoSeguimientoId());
    Assertions.assertThat(updated.getTipoDocumento().getId())
        .isEqualTo(proyectoPeriodoSeguimientoDocumento.getTipoDocumento().getId());
    Assertions.assertThat(updated.getComentario()).isEqualTo("comentarios-modificado");
    Assertions.assertThat(updated.getDocumentoRef()).isEqualTo(proyectoPeriodoSeguimientoDocumento.getDocumentoRef());
    Assertions.assertThat(updated.getNombre()).isEqualTo(proyectoPeriodoSeguimientoDocumento.getNombre());
  }

  @Test
  public void update_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimientoDocumento = generarMockProyectoPeriodoSeguimientoDocumento(
        1L);

    BDDMockito.given(proyectoPeriodoSeguimientoDocumentoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update non existing ProyectoPeriodoSeguimientoDocumento
        () -> service.update(proyectoPeriodoSeguimientoDocumento))
        // then: NotFoundException is thrown
        .isInstanceOf(ProyectoPeriodoSeguimientoDocumentoNotFoundException.class);
  }

  @Test
  public void update_WithoutProyectoPeriodoSeguimiento_ThrowsNotFoundException() throws Exception {
    // given: solicitud id null
    ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimientoDocumento = generarMockProyectoPeriodoSeguimientoDocumento(
        1L);
    proyectoPeriodoSeguimientoDocumento.setProyectoPeriodoSeguimientoId(null);

    Assertions.assertThatThrownBy(
        // when: update non existing ProyectoPeriodoSeguimientoDocumento
        () -> service.update(proyectoPeriodoSeguimientoDocumento))
        // then: NotFoundException is thrown
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_WithoutNombreDocumento_ThrowsNotFoundException() throws Exception {
    // given: nombre documento null
    ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimientoDocumento = generarMockProyectoPeriodoSeguimientoDocumento(
        1L);
    proyectoPeriodoSeguimientoDocumento.setNombre(null);

    Assertions.assertThatThrownBy(
        // when: update non existing ProyectoPeriodoSeguimientoDocumento
        () -> service.update(proyectoPeriodoSeguimientoDocumento))
        // then: NotFoundException is thrown
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_WithoutDocumentoRef_ThrowsNotFoundException() throws Exception {
    // given: nombre documento null
    ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimientoDocumento = generarMockProyectoPeriodoSeguimientoDocumento(
        1L);
    proyectoPeriodoSeguimientoDocumento.setDocumentoRef(null);

    Assertions.assertThatThrownBy(
        // when: update non existing ProyectoPeriodoSeguimientoDocumento
        () -> service.update(proyectoPeriodoSeguimientoDocumento))
        // then: NotFoundException is thrown
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {
    // given: a ProyectoPeriodoSeguimientoDocumento without id filled
    ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimientoDocumento = generarMockProyectoPeriodoSeguimientoDocumento(
        null);

    Assertions.assertThatThrownBy(
        // when: update ProyectoPeriodoSeguimientoDocumento
        () -> service.update(proyectoPeriodoSeguimientoDocumento))
        // then: throw exception as id must be provided
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithExistingId_ReturnsProyectoPeriodoSeguimientoDocumento() {
    // given: existing ProyectoPeriodoSeguimientoDocumento
    Long id = 1L;

    BDDMockito.given(proyectoPeriodoSeguimientoDocumentoRepository.existsById(ArgumentMatchers.anyLong()))
        .willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(proyectoPeriodoSeguimientoDocumentoRepository).deleteById(ArgumentMatchers.anyLong());

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

    BDDMockito.given(proyectoPeriodoSeguimientoDocumentoRepository.existsById(ArgumentMatchers.anyLong()))
        .willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: NotFoundException is thrown
        .isInstanceOf(ProyectoPeriodoSeguimientoDocumentoNotFoundException.class);
  }

  @Test
  public void findById_WithExistingId_ReturnsProyectoPeriodoSeguimientoDocumento() throws Exception {
    // given: existing ProyectoPeriodoSeguimientoDocumento
    ProyectoPeriodoSeguimientoDocumento givenProyectoPeriodoSeguimientoDocumento = generarMockProyectoPeriodoSeguimientoDocumento(
        1L);

    BDDMockito.given(proyectoPeriodoSeguimientoDocumentoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(givenProyectoPeriodoSeguimientoDocumento));

    // when: find by id ProyectoPeriodoSeguimientoDocumento
    ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimientoDocumento = service
        .findById(givenProyectoPeriodoSeguimientoDocumento.getId());

    // then: returns ProyectoPeriodoSeguimientoDocumento
    Assertions.assertThat(proyectoPeriodoSeguimientoDocumento).isNotNull();
    Assertions.assertThat(proyectoPeriodoSeguimientoDocumento.getId()).isNotNull();
    Assertions.assertThat(proyectoPeriodoSeguimientoDocumento.getId())
        .isEqualTo(proyectoPeriodoSeguimientoDocumento.getId());
    Assertions.assertThat(proyectoPeriodoSeguimientoDocumento.getProyectoPeriodoSeguimientoId())
        .isEqualTo(proyectoPeriodoSeguimientoDocumento.getProyectoPeriodoSeguimientoId());
    Assertions.assertThat(proyectoPeriodoSeguimientoDocumento.getTipoDocumento().getId())
        .isEqualTo(proyectoPeriodoSeguimientoDocumento.getTipoDocumento().getId());
    Assertions.assertThat(proyectoPeriodoSeguimientoDocumento.getComentario())
        .isEqualTo(proyectoPeriodoSeguimientoDocumento.getComentario());
    Assertions.assertThat(proyectoPeriodoSeguimientoDocumento.getDocumentoRef())
        .isEqualTo(proyectoPeriodoSeguimientoDocumento.getDocumentoRef());
    Assertions.assertThat(proyectoPeriodoSeguimientoDocumento.getNombre())
        .isEqualTo(proyectoPeriodoSeguimientoDocumento.getNombre());
  }

  @Test
  public void findById_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    Long id = 1L;
    BDDMockito.given(proyectoPeriodoSeguimientoDocumentoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: find by non existing id
        () -> service.findById(id))
        // then: NotFoundException is thrown
        .isInstanceOf(ProyectoPeriodoSeguimientoDocumentoNotFoundException.class);
  }

  @Test
  public void findAll_ReturnsPage() {
    // given: Una lista con 37 ProyectoPeriodoSeguimientoDocumento
    Long solicitudId = 1L;
    List<ProyectoPeriodoSeguimientoDocumento> proyectoPeriodoSeguimientoDocumentos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectoPeriodoSeguimientoDocumentos.add(generarMockProyectoPeriodoSeguimientoDocumento(i));
    }

    BDDMockito.given(proyectoPeriodoSeguimientoDocumentoRepository.findAll(
        ArgumentMatchers.<Specification<ProyectoPeriodoSeguimientoDocumento>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ProyectoPeriodoSeguimientoDocumento>>() {
          @Override
          public Page<ProyectoPeriodoSeguimientoDocumento> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > proyectoPeriodoSeguimientoDocumentos.size()
                ? proyectoPeriodoSeguimientoDocumentos.size()
                : toIndex;
            List<ProyectoPeriodoSeguimientoDocumento> content = proyectoPeriodoSeguimientoDocumentos.subList(fromIndex,
                toIndex);
            Page<ProyectoPeriodoSeguimientoDocumento> page = new PageImpl<>(content, pageable,
                proyectoPeriodoSeguimientoDocumentos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ProyectoPeriodoSeguimientoDocumento> page = service.findAllByProyectoPeriodoSeguimiento(solicitudId, null,
        paging);

    // then: Devuelve la pagina 3 con los Programa del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimientoDocumento = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(proyectoPeriodoSeguimientoDocumento.getId()).isEqualTo(i);
    }
  }

  /**
   * Función que devuelve un objeto ProyectoPeriodoSeguimientoDocumento
   * 
   * @param id id del ProyectoPeriodoSeguimientoDocumento
   * @return el objeto ProyectoPeriodoSeguimientoDocumento
   */
  private ProyectoPeriodoSeguimientoDocumento generarMockProyectoPeriodoSeguimientoDocumento(Long id) {

    TipoDocumento tipoDocumento = new TipoDocumento();
    tipoDocumento.setId((id != null ? id : 1));
    tipoDocumento.setNombre("TipoDocumento" + (id != null ? id : 1));
    tipoDocumento.setDescripcion("descripcion-" + (id != null ? id : 1));
    tipoDocumento.setActivo(Boolean.TRUE);

    ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimientoDocumento = new ProyectoPeriodoSeguimientoDocumento();
    proyectoPeriodoSeguimientoDocumento.setId(id);
    proyectoPeriodoSeguimientoDocumento.setProyectoPeriodoSeguimientoId(id == null ? 1 : id);
    proyectoPeriodoSeguimientoDocumento.setNombre("Nombre-" + String.format("%03d", (id != null ? id : 1)));
    proyectoPeriodoSeguimientoDocumento.setDocumentoRef("Doc-" + String.format("%03d", (id != null ? id : 1)));
    proyectoPeriodoSeguimientoDocumento.setComentario("comentario-" + String.format("%03d", (id != null ? id : 1)));
    proyectoPeriodoSeguimientoDocumento.setTipoDocumento(tipoDocumento);
    proyectoPeriodoSeguimientoDocumento.setVisible(Boolean.TRUE);

    return proyectoPeriodoSeguimientoDocumento;
  }
}
