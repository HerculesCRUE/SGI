package org.crue.hercules.sgi.eti.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.ComiteNotFoundException;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.repository.ComiteRepository;
import org.crue.hercules.sgi.eti.service.impl.ComiteServiceImpl;
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
 * ComiteServiceTest
 */
public class ComiteServiceTest extends BaseServiceTest {
  @Mock
  private ComiteRepository comiteRepository;

  @Mock
  private ComiteService comiteService;

  @BeforeEach
  public void setUp() throws Exception {
    comiteService = new ComiteServiceImpl(comiteRepository);
  }

  @Test
  public void findAll_ReturnsComiteList() {

    // given: dos comites
    Comite comite = new Comite();
    comite.setId(1L);
    comite.setCodigo("Comite1");
    comite.setActivo(Boolean.TRUE);
    Comite comite2 = new Comite();
    comite2.setId(2L);
    comite2.setCodigo("Comite2");
    comite2.setActivo(Boolean.TRUE);

    List<Comite> comiteResponseList = new ArrayList<Comite>();
    comiteResponseList.add(comite);
    comiteResponseList.add(comite2);

    BDDMockito
        .given(
            comiteRepository.findAll(ArgumentMatchers.<Specification<Comite>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(comiteResponseList));

    // when: busqueda de todos los elementos
    Page<Comite> page = comiteService.findAll(null, Pageable.unpaged());

    // then: recuperamos los comités
    Assertions.assertThat(page.getContent().size()).isEqualTo(2);
    Assertions.assertThat(page.getNumber()).isZero();
    Assertions.assertThat(page.getSize()).isEqualTo(2);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(2);
  }

  @Test
  public void findAll_ReturnsEmptyList() throws Exception {

    List<Comite> comiteResponseList = new ArrayList<Comite>();

    // given: Una lista vacía
    BDDMockito.given(comiteService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(comiteResponseList));

    // when: Se realiza la búsqueda de comites
    Page<Comite> comiteList = comiteService.findAll(ArgumentMatchers.<String>any(), ArgumentMatchers.<Pageable>any());

    // then: Recuperamos la lista vacía
    Assertions.assertThat(comiteList);

  }

  @Test
  public void findById_WithId_ReturnsComite() throws ComiteNotFoundException {
    // given: El id de un comité

    Comite comiteMock = new Comite();
    comiteMock.setId(1L);
    comiteMock.setCodigo("Comite1");
    comiteMock.setActivo(Boolean.TRUE);

    BDDMockito.given(comiteRepository.findById(1L))
        .willReturn(Optional.of(comiteMock));

    // when: Buscamos por id
    Comite comite = comiteService.findById(1L);

    // then: Recuperamos el comité
    Assertions.assertThat(comite.getId()).isEqualTo(1L);
    Assertions.assertThat(comite.getCodigo()).isEqualTo("Comite1");

  }

  @Test
  public void findById_WithNoExistingId_ThrowsNotFoundException() throws Exception {

    // given: No existe el id
    BDDMockito.given(comiteRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: Búsqueda por un id que no existe
        () -> comiteService.findById(1L))
        // then: Excepción ComiteNotFoundException
        .isInstanceOf(ComiteNotFoundException.class);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {

    // given: Cien Comite
    List<Comite> comiteList = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      Comite comite = new Comite();
      comite.setId(Long.valueOf(i));
      comite.setCodigo("Comite" + String.format("%03d", i));
      comite.setActivo(Boolean.TRUE);
      comiteList.add(comite);
    }

    BDDMockito
        .given(
            comiteRepository.findAll(ArgumentMatchers.<Specification<Comite>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Comite>>() {
          @Override
          public Page<Comite> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Comite> content = comiteList.subList(fromIndex, toIndex);
            Page<Comite> page = new PageImpl<>(content, pageable, comiteList.size());
            return page;
          }
        });

    // when: Se obtiene page=3 con pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Comite> page = comiteService.findAll(null, paging);

    // then: Se retorna una Page con diez Comite que contienen
    // Comite='Comite031' to
    // 'Comite040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Comite comite = page.getContent().get(i);
      Assertions.assertThat(comite.getCodigo()).isEqualTo("Comite" + String.format("%03d", j));
    }
  }

}