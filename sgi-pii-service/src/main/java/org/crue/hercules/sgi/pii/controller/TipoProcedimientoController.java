package org.crue.hercules.sgi.pii.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.crue.hercules.sgi.pii.dto.TipoProcedimientoInput;
import org.crue.hercules.sgi.pii.dto.TipoProcedimientoOutput;
import org.crue.hercules.sgi.pii.model.TipoProcedimiento;
import org.crue.hercules.sgi.pii.service.TipoProcedimientoService;
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
@RequestMapping("tiposprocedimiento")
public class TipoProcedimientoController {

  private final TipoProcedimientoService tipoProcedimientoService;
  private final ModelMapper modelMapper;

  /**
   * Devuelve todos los tiposProcedimientos activos
   * 
   * @param query
   * @param paging
   * @return la lista de entidades {@link TipoProcedimiento} paginada con
   *         posibilidad de filtrado
   */
  @GetMapping
  @PreAuthorize("hasAnyAuthority('PII-TPR-V', 'PII-TPR-C', 'PII-TPR-E', 'PII-TPR-B', 'PII-TPR-R', 'PII-INV-V', 'PII-INV-C', 'PII-INV-E', 'PII-INV-B', 'PII-INV-R')")
  ResponseEntity<Page<TipoProcedimientoOutput>> findActivos(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {

    Page<TipoProcedimientoOutput> page = convert(tipoProcedimientoService.findActivos(query, paging));

    if (page.isEmpty()) {
      return ResponseEntity.noContent().build();
    }

    return ResponseEntity.ok(page);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TipoProcedimiento}.
   * 
   * @param query  Filtro de búsqueda.
   * @param paging Información de Paginado.
   * @return Lista de entidades {@link TipoProcedimiento}.
   */
  @GetMapping("/todos")
  @PreAuthorize("hasAnyAuthority('PII-TPR-V', 'PII-TPR-C', 'PII-TPR-E', 'PII-TPR-B', 'PII-TPR-R')")
  ResponseEntity<Page<TipoProcedimientoOutput>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {

    Page<TipoProcedimiento> page = this.tipoProcedimientoService.findAll(query, paging);

    return page.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok().body(convert(page));
  }

  /**
   * Devuelve el {@link TipoProcedimiento} con el id indicado.
   * 
   * @param id Identificador del {@link TipoProcedimiento}.
   * @return {@link TipoProcedimiento} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('PII-TPR-V', 'PII-TPR-C', 'PII-TPR-E', 'PII-TPR-B', 'PII-TPR-R')")
  TipoProcedimientoOutput findById(@PathVariable Long id) {

    return convert(this.tipoProcedimientoService.findById(id));
  }

  /**
   * Crea un nuevo {@link TipoProcedimiento}.
   * 
   * @param tipoProcedimientoInput {@link TipoProcedimiento} que se quiere crear.
   * @return Nuevo {@link TipoProteccion} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthority('PII-TPR-C')")
  ResponseEntity<TipoProcedimientoOutput> create(@Valid @RequestBody TipoProcedimientoInput tipoProcedimientoInput) {

    return new ResponseEntity<>(convert(this.tipoProcedimientoService.create(convert(null, tipoProcedimientoInput))),
        HttpStatus.CREATED);
  }

  /**
   * Activa el {@link TipoProcedimiento} con el id indicado.
   * 
   * @param id Identificador del {@link TipoProcedimiento}.
   * @return {@link TipoProcedimiento} actualizado.
   */
  @PatchMapping("/{id}/activar")
  @PreAuthorize("hasAuthority('PII-TPR-R')")
  TipoProcedimientoOutput activar(@PathVariable Long id) {

    return convert(this.tipoProcedimientoService.activar(id));
  }

  /**
   * Desactiva el {@link TipoProcedimiento} con el id indicado.
   * 
   * @param id Identificador del {@link TipoProcedimiento}.
   */
  @PatchMapping("/{id}/desactivar")
  @PreAuthorize("hasAuthority('PII-TPR-B')")
  TipoProcedimientoOutput desactivar(@PathVariable Long id) {

    return convert(this.tipoProcedimientoService.desactivar(id));
  }

  /**
   * Actualiza el {@link TipoProcedimiento} con el id indicado.
   * 
   * @param tipoProcedimiento {@link TipoProcedimiento} a actualizar.
   * @param id                id {@link TipoProcedimiento} a actualizar.
   * @return {@link TipoProcedimiento} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('PII-TPR-E')")
  TipoProcedimientoOutput update(@Valid @RequestBody TipoProcedimientoInput tipoProcedimiento, @PathVariable Long id) {

    return convert(this.tipoProcedimientoService.update(convert(id, tipoProcedimiento)));
  }

  /****************/
  /* CONVERTERS */
  /****************/

  private TipoProcedimientoOutput convert(TipoProcedimiento TipoProcedimiento) {
    return modelMapper.map(TipoProcedimiento, TipoProcedimientoOutput.class);
  }

  private TipoProcedimiento convert(Long id, TipoProcedimientoInput TipoProcedimientoInput) {
    TipoProcedimiento TipoProcedimiento = modelMapper.map(TipoProcedimientoInput, TipoProcedimiento.class);
    TipoProcedimiento.setId(id);
    return TipoProcedimiento;
  }

  private Page<TipoProcedimientoOutput> convert(Page<TipoProcedimiento> page) {
    List<TipoProcedimientoOutput> content = page.getContent().stream()
        .map((TipoProcedimiento) -> convert(TipoProcedimiento)).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

}
