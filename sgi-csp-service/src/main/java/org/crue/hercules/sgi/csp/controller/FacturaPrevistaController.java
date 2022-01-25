package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.dto.FacturaEmitidaOutput;
import org.crue.hercules.sgi.csp.model.ProyectoFacturacion;
import org.crue.hercules.sgi.csp.service.ProyectoFacturacionService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping(path = "/facturas-previstas")
public class FacturaPrevistaController {

  public static final String MAPPING = "/facturas-previstas";

  private final ModelMapper modelMapper;
  private final ProyectoFacturacionService proyectoFacturacionService;

  public FacturaPrevistaController(final ModelMapper modelMapper,
      final ProyectoFacturacionService proyectoFacturacionService) {

    this.modelMapper = modelMapper;
    this.proyectoFacturacionService = proyectoFacturacionService;

    this.modelMapper.typeMap(ProyectoFacturacion.class, FacturaEmitidaOutput.class)
        .addMappings(mapper -> mapper.map(ProyectoFacturacion::getProyectoId, FacturaEmitidaOutput::setProyectoIdSGI));
  }

  @GetMapping
  public ResponseEntity<Page<FacturaEmitidaOutput>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {

    Page<ProyectoFacturacion> page = this.proyectoFacturacionService.findFacturasPrevistas(query, paging);
    return page.isEmpty() ? ResponseEntity.noContent().build()
        : ResponseEntity.ok(convertToFacturaEmitidaOutputPage(page));
  }

  private FacturaEmitidaOutput convert(ProyectoFacturacion entity) {
    return this.modelMapper.map(entity, FacturaEmitidaOutput.class);
  }

  private Page<FacturaEmitidaOutput> convertToFacturaEmitidaOutputPage(Page<ProyectoFacturacion> page) {

    return new PageImpl<>(page.getContent().stream().map(this::convert).collect(Collectors.toList()),
        page.getPageable(), page.getTotalElements());

  }
}
