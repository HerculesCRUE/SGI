package org.crue.hercules.sgi.pii.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.crue.hercules.sgi.pii.dto.ViaProteccionInput;
import org.crue.hercules.sgi.pii.dto.ViaProteccionOutput;
import org.crue.hercules.sgi.pii.model.ViaProteccion;
import org.crue.hercules.sgi.pii.service.ViaProteccionService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("viasproteccion")
public class ViaProteccionController {

  private final ViaProteccionService viaProteccionService;
  private final ModelMapper modelMapper;

  /**
   * Devuelve una lista paginada y filtrada {@link TipoProcedimiento}.
   * 
   * @param query  Filtro de búsqueda.
   * @param paging Información de Paginado.
   * @return Lista de entidades {@link TipoProcedimiento}.
   */
  @GetMapping("/todos")
  @PreAuthorize("hasAnyAuthority('PII-VPR-V', 'PII-VPR-C', 'PII-VPR-E', 'PII-VPR-B', 'PII-VPR-R', 'PII-INV-E', 'PII-INV-V')")
  ResponseEntity<Page<ViaProteccionOutput>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {

    Page<ViaProteccion> page = this.viaProteccionService.findAll(query, paging);

    return page.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok().body(convert(page));
  }

  /**
   * Crea un nuevo {@link ViaProteccion}.
   * 
   * @param viaProteccion {@link ViaProteccion} que se quiere crear.
   * @return Nuevo {@link ViaProteccion} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthority('PII-VPR-C')")
  ResponseEntity<ViaProteccionOutput> create(@Valid @RequestBody ViaProteccionInput viaProteccionInput) {

    return new ResponseEntity<>(convert(this.viaProteccionService.create(convert(null, viaProteccionInput))),
        HttpStatus.CREATED);
  }

  /**
   * Activa el {@link ViaProteccion} con id indicado.
   * 
   * @param id Identificador de {@link ViaProteccion}.
   * @return {@link ViaProteccion} actualizado.
   */
  @PatchMapping("/{id}/activar")
  @PreAuthorize("hasAuthority('PII-VPR-R')")
  ViaProteccionOutput activar(@PathVariable Long id) {

    return convert(this.viaProteccionService.activar(id));
  }

  /**
   * Desactiva el {@link ViaProteccion} con id indicado.
   * 
   * @param id Identificador de {@link ViaProteccion}.
   */
  @PatchMapping("/{id}/desactivar")
  @PreAuthorize("hasAuthority('PII-VPR-B')")
  ViaProteccionOutput desactivar(@PathVariable Long id) {

    return convert(this.viaProteccionService.desactivar(id));
  }

  /**
   * Actualiza el {@link ViaProteccion} con el id indicado.
   * 
   * @param viaProteccion {@link ViaProteccionInput} a actualizar.
   * @param id            id del objeto {@link ViaProteccion} a actualizar.
   * @return {@link ViaProteccionOutput} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('PII-VPR-E')")
  ResponseEntity<ViaProteccionOutput> update(@Valid @RequestBody ViaProteccionInput viaProteccion,
      @PathVariable Long id) {
    return ResponseEntity.ok(convert(this.viaProteccionService.update(convert(id, viaProteccion))));

  }

  /****************/
  /* CONVERTERS */
  /****************/

  private ViaProteccionOutput convert(ViaProteccion viaProteccion) {
    return this.modelMapper.map(viaProteccion, ViaProteccionOutput.class);
  }

  private ViaProteccion convert(Long id, ViaProteccionInput viaProteccionInput) {
    ViaProteccion viaProteccion = modelMapper.map(viaProteccionInput, ViaProteccion.class);
    viaProteccion.setId(id);
    return viaProteccion;
  }

  private Page<ViaProteccionOutput> convert(Page<ViaProteccion> page) {
    List<ViaProteccionOutput> content = page.getContent().stream().map((viaProteccion) -> convert(viaProteccion))
        .collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }
}
