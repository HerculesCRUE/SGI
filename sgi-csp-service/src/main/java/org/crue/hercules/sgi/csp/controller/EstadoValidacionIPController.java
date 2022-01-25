package org.crue.hercules.sgi.csp.controller;

import lombok.RequiredArgsConstructor;
import org.crue.hercules.sgi.csp.dto.EstadoValidacionIPOutput;
import org.crue.hercules.sgi.csp.model.EstadoValidacionIP;
import org.crue.hercules.sgi.csp.service.EstadoValidacionIPService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(path = EstadoValidacionIPController.MAPPING)
public class EstadoValidacionIPController {

  public static final String MAPPING = "/estadosvalidacionip";

  private final ModelMapper modelMapper;
  private final EstadoValidacionIPService estadoValidacionIPService;

  /**
   * Busca objetos de tipo {@link EstadoValidacionIPOutput} según la query de
   * entrada
   * 
   * @param query  {@link String} con los parámetros de búsqueda
   * @param paging {@link Pageable}
   * @return lista de objetos de tipo {@link EstadoValidacionIPOutput}
   */
  @GetMapping
  public ResponseEntity<Page<EstadoValidacionIPOutput>> findAll(@RequestParam(name = "q", required = true) String query,
      @RequestPageable(sort = "s") Pageable paging) {

    Page<EstadoValidacionIP> estados = this.estadoValidacionIPService.findAll(query, paging);

    return CollectionUtils.isEmpty(estados.getContent()) ? ResponseEntity.noContent().build()
        : ResponseEntity.ok(this.convertToEstadoValidacionIPOutputPage(estados));
  }

  EstadoValidacionIPOutput entityToOutput(EstadoValidacionIP entity) {
    return this.modelMapper.map(entity, EstadoValidacionIPOutput.class);
  }

  List<EstadoValidacionIPOutput> entitiesToOutputs(List<EstadoValidacionIP> entities) {
    return entities.stream().map(this::entityToOutput).collect(Collectors.toList());
  }

  private Page<EstadoValidacionIPOutput> convertToEstadoValidacionIPOutputPage(Page<EstadoValidacionIP> page) {

    return new PageImpl<>(page.getContent().stream().map(this::entityToOutput).collect(Collectors.toList()),
        page.getPageable(), page.getTotalElements());

  }

}
