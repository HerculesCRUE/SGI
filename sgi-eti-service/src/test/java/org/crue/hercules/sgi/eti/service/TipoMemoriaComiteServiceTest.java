package org.crue.hercules.sgi.eti.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.ComiteNotFoundException;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.crue.hercules.sgi.eti.model.TipoMemoriaComite;
import org.crue.hercules.sgi.eti.model.Comite.Genero;
import org.crue.hercules.sgi.eti.repository.ComiteRepository;
import org.crue.hercules.sgi.eti.repository.TipoMemoriaComiteRepository;
import org.crue.hercules.sgi.eti.service.impl.TipoMemoriaComiteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 * TipoMemoriaComiteServiceTest
 */
public class TipoMemoriaComiteServiceTest extends BaseServiceTest {

  @Mock
  private TipoMemoriaComiteRepository tipoMemoriaComiteRepository;

  @Mock
  private ComiteRepository comiteRepository;

  private TipoMemoriaComiteService tipoMemoriaComiteService;

  @BeforeEach
  public void setUp() throws Exception {
    tipoMemoriaComiteService = new TipoMemoriaComiteServiceImpl(tipoMemoriaComiteRepository, comiteRepository);
  }

  @Test
  public void findByComite_WithId_ReturnsMemoria() {

    BDDMockito.given(comiteRepository.findByIdAndActivoTrue(1L)).willReturn(Optional.of(new Comite()));

    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(1L, "Comite1", "nombreInvestigacion", Genero.M, formulario, Boolean.TRUE);
    TipoMemoria tipoMemoria = new TipoMemoria(1L, "TipoMemoria1", Boolean.TRUE);

    List<TipoMemoriaComite> tipoMemoriasComite = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoMemoriasComite.add(generarMockTipoMemoriaComite(Long.valueOf(i), comite, tipoMemoria));
    }

    BDDMockito.given(tipoMemoriaComiteRepository.findByComiteIdAndComiteActivoTrue(1L, Pageable.unpaged()))
        .willReturn(new PageImpl<>(tipoMemoriasComite));

    // when: find unlimited
    Page<TipoMemoria> page = tipoMemoriaComiteService.findByComite(1L, Pageable.unpaged());
    // then: Get a page with one hundred TipoMemorias
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isZero();
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);

  }

  @Test
  public void findByComite_NotFound_ThrowsComiteNotFoundException() throws Exception {
    BDDMockito.given(comiteRepository.findByIdAndActivoTrue(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> tipoMemoriaComiteService.findByComite(1L, null))
        .isInstanceOf(ComiteNotFoundException.class);
  }

  @Test
  public void findByComite_ComiteIdNull() throws Exception {

    try {
      // when: Creamos la memoria
      tipoMemoriaComiteService.findByComite(null, null);
      Assertions.fail("El identificador del comité no puede ser null para recuperar sus tipos de memoria asociados.");
      // then: se debe lanzar una excepción
    } catch (final IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("El identificador del comité no puede ser null para recuperar sus tipos de memoria asociados.");
    }
  }

  /**
   * Función que devuelve un objeto TipoMemoriaComite
   * 
   * @param id          id del TipoMemoriaComite
   * @param comite      el Comite de TipoMemoriaComite
   * @param tipoMemoria el TipoMemoria de TipoMemoriaComite
   * @return el objeto TipoMemoriaComite
   */

  private TipoMemoriaComite generarMockTipoMemoriaComite(Long id, Comite comite, TipoMemoria tipoMemoria) {

    TipoMemoriaComite tipoMemoriaComite = new TipoMemoriaComite();
    tipoMemoriaComite.setId(id);
    tipoMemoriaComite.setComite(comite);
    tipoMemoriaComite.setTipoMemoria(tipoMemoria);

    return tipoMemoriaComite;
  }
}