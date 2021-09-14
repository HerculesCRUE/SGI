package org.crue.hercules.sgi.csp.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.RequisitoIPNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.RequisitoIP;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.RequisitoIPRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;

/**
 * RequisitoIPServiceTest
 */
public class RequisitoIPServiceTest extends BaseServiceTest {

  @Mock
  private RequisitoIPRepository repository;
  @Mock
  private ConvocatoriaRepository convocatoriaRepository;
  @Mock
  private ConvocatoriaService convocatoriaService;

  private RequisitoIPService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new RequisitoIPService(repository, convocatoriaRepository, convocatoriaService);
  }

  @Test
  public void create_ReturnsRequisitoIP() {
    // given: Un nuevo RequisitoIP
    Long convocatoriaId = 1L;
    Convocatoria convocatoria = generarMockConvocatoria(convocatoriaId);
    RequisitoIP requisitoIP = generarMockRequisitoIP(convocatoriaId);

    BDDMockito.given(convocatoriaRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(convocatoria));

    BDDMockito.given(repository.save(requisitoIP)).will((InvocationOnMock invocation) -> {
      RequisitoIP requisitoIPCreado = invocation.getArgument(0);
      requisitoIPCreado.setId(1L);
      return requisitoIPCreado;
    });

    // when: Creamos el RequisitoIP
    RequisitoIP requisitoIPCreado = service.create(requisitoIP);

    // then: El RequisitoIP se crea correctamente
    Assertions.assertThat(requisitoIPCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(requisitoIPCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(requisitoIPCreado.getSexoRef()).as("getSexoRef()").isEqualTo(requisitoIP.getSexoRef());
  }

  @Test
  public void create_WithoutId_ThrowsIllegalArgumentException() {
    // given: Un nuevo RequisitoIP sin convocatoria
    RequisitoIP requisitoIP = generarMockRequisitoIP(null);

    // when: Creamos el RequisitoIP
    // then: Lanza una excepcion porque la convocatoria es null
    Assertions.assertThatThrownBy(() -> service.create(requisitoIP)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("The Identifier from Principal Researcher Requirement can't be null");
  }

  @Test
  public void create_WithDuplicatedConvocatoria_ThrowsIllegalArgumentException() {
    // given: Un nuevo RequisitoIP con convocatoria ya asignada
    Long convocatoriaId = 1L;
    RequisitoIP requisitoIPExistente = generarMockRequisitoIP(convocatoriaId);

    BDDMockito.given(repository.existsById(ArgumentMatchers.<Long>any())).willReturn(true);

    // when: Creamos el RequisitoIP
    // then: Lanza una excepcion porque la convocatoria ya tiene un RequisitoIP
    Assertions.assertThatThrownBy(() -> service.create(requisitoIPExistente))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("There is already a Principal Researcher Requirement related with the Call");
  }

  @Test
  public void update_ReturnsRequisitoIP() {
    // given: Un nuevo RequisitoIP con el sexo actualizado
    Long convocatoriaId = 1L;
    RequisitoIP requisitoIP = generarMockRequisitoIP(convocatoriaId);
    RequisitoIP requisitoIPActualizado = generarMockRequisitoIP(convocatoriaId);
    requisitoIPActualizado.setSexoRef("Mujer");

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(requisitoIP));
    BDDMockito.given(repository.save(ArgumentMatchers.<RequisitoIP>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el RequisitoIP
    requisitoIPActualizado = service.update(requisitoIPActualizado, 1L);

    // then: El RequisitoIP se actualiza correctamente.
    Assertions.assertThat(requisitoIPActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(requisitoIPActualizado.getId()).as("getId()").isEqualTo(requisitoIP.getId());
    Assertions.assertThat(requisitoIPActualizado.getSexoRef()).as("getSexoRef()").isEqualTo(requisitoIP.getSexoRef());
  }

  @Test
  public void update_WithIdNotExist_ThrowsRequisitoIPNotFoundException() {
    // given: Un RequisitoIP actualizado con un id que no existe
    Long convocatoriaId = 1L;
    RequisitoIP requisitoIP = generarMockRequisitoIP(convocatoriaId);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(requisitoIP));

    // when: Actualizamos el RequisitoIP
    // then: Lanza una excepcion porque el RequisitoIP no existe
    Assertions.assertThatThrownBy(() -> service.update(requisitoIP, 1L))
        .isInstanceOf(RequisitoIPNotFoundException.class);
  }

  @Test
  public void findByConvocatoriaId_ReturnsRequisitoIP() {
    // given: Un RequisitoIP con el id buscado
    Long idBuscado = 1L;

    BDDMockito.given(convocatoriaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.of(generarMockRequisitoIP(idBuscado)));

    // when: Buscamos el RequisitoIP por su id
    RequisitoIP requisitoIP = service.findByConvocatoria(idBuscado);

    // then: el RequisitoIP
    Assertions.assertThat(requisitoIP).as("isNotNull()").isNotNull();
    Assertions.assertThat(requisitoIP.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(requisitoIP.getSexoRef()).as("getSexoRef()").isEqualTo("Hombre");

  }

  @Test
  public void findByConvocatoriaId_WithIdNotExist_ThrowsConvocatoriaNotFoundExceptionException() throws Exception {
    // given: Ninguna Convocatoria con el id buscado
    Long idBuscado = 1L;

    BDDMockito.given(convocatoriaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    // when: Buscamos el RequisitoIP por id Convocatoria
    // then: lanza un ConvocatoriaNotFoundException
    Assertions.assertThatThrownBy(() -> service.findByConvocatoria(idBuscado))
        .isInstanceOf(ConvocatoriaNotFoundException.class);
  }

  private Convocatoria generarMockConvocatoria(Long convocatoriaId) {
    return Convocatoria.builder().id(convocatoriaId).activo(Boolean.TRUE).codigo("codigo" + convocatoriaId).build();
  }

  /**
   * Función que devuelve un objeto RequisitoIP
   * 
   * @param id id del RequisitoIP
   * @return el objeto RequisitoIP
   */
  private RequisitoIP generarMockRequisitoIP(Long id) {
    RequisitoIP requisitoIP = new RequisitoIP();
    requisitoIP.setId(id);
    requisitoIP.setSexoRef("Hombre");
    return requisitoIP;
  }

}
