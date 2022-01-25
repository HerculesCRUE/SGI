package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.dto.ProyectoFacturacionInput;
import org.crue.hercules.sgi.csp.dto.ProyectoFacturacionOutput;
import org.crue.hercules.sgi.csp.model.ProyectoFacturacion;
import org.crue.hercules.sgi.csp.service.ProyectoFacturacionService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(path = ProyectoFacturacionController.MAPPING)
public class ProyectoFacturacionController {

  public static final String MAPPING = "/proyectosfacturacion";

  private final ModelMapper modelMapper;
  private final ProyectoFacturacionService proyectoFacturacionService;

  /**
   * persiste un objeto de tipo {@link ProyectoFacturacion} en la base de datos
   * @param input objeto de tipo {@link ProyectoFacturacionInput} con la información del objeto que se va a crear
   * @return objeto de tipo {@link ProyectoFacturacionOutput} con la información del objeto creado
   */
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  @PostMapping
  public ResponseEntity<ProyectoFacturacionOutput> create(@Valid @RequestBody ProyectoFacturacionInput input) {
    ProyectoFacturacion newItem = this.proyectoFacturacionService.create(this.convert(input));
    return new ResponseEntity<>(this.convert(newItem), HttpStatus.CREATED);
  }

  /**
   * Actualiza un objeto de tipo {@link ProyectoFacturacion}
   * @param id {@link Long} id del objeto a actualizar
   * @param toUpdate objeto de tipo {@link ProyectoFacturacionInput} con la información a actualizar
   * @return objeto de tipo {@link ProyectoFacturacionOutput} con la copia del objeto que se ha actualizado
   */
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  @PutMapping("/{id}")
  public ResponseEntity<ProyectoFacturacionOutput> update(@PathVariable(required = true) Long id,
      @Valid @RequestBody ProyectoFacturacionInput toUpdate) {
    toUpdate.setId(id);
    return ResponseEntity.ok(this.convert(this.proyectoFacturacionService.update(this.convert(toUpdate))));
  }

  /**
   * Elimina un objeto de tipo {@link ProyectoFacturacion} de la base de datos
   * @param id {@link Long} id del objeto a eliminar
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void delete(@PathVariable(required = true) Long id) {
    this.proyectoFacturacionService.delete(id);
  }

  private ProyectoFacturacion convert(ProyectoFacturacionInput input) {
    return this.modelMapper.map(input, ProyectoFacturacion.class);
  }

  private ProyectoFacturacionOutput convert(ProyectoFacturacion entity) {
    return this.modelMapper.map(entity, ProyectoFacturacionOutput.class);
  }
}
