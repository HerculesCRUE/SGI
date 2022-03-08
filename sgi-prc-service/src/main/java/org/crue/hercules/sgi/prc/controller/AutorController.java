package org.crue.hercules.sgi.prc.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.crue.hercules.sgi.prc.dto.AutorGrupoOutput;
import org.crue.hercules.sgi.prc.model.Autor;
import org.crue.hercules.sgi.prc.model.AutorGrupo;
import org.crue.hercules.sgi.prc.service.AutorGrupoService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * AutorController
 */
@RestController
@RequestMapping(AutorController.MAPPING)
@Slf4j
public class AutorController {
  public static final String MAPPING = "/autores";
  public static final String PATH_GRUPOS = "/{id}/grupos";

  private ModelMapper modelMapper;

  /** AutorGrupoService service */
  private final AutorGrupoService autorGrupoService;

  /**
   * Instancia un nuevo AutorController.
   * 
   * @param modelMapper       {@link ModelMapper}
   * @param autorGrupoService {@link AutorGrupoService}
   */
  public AutorController(
      ModelMapper modelMapper,
      AutorGrupoService autorGrupoService) {
    this.modelMapper = modelMapper;
    this.autorGrupoService = autorGrupoService;
  }

  /**
   * Obtiene todos los {@link AutorGrupo} del {@link Autor} con el id indicado
   * paginadas y/o filtradas.
   *
   * @param id     el id de {@link Autor}.
   * @param query  la información del filtro.
   * @param paging la información de la paginación.
   * @return listado de {@link AutorGrupo} paginadas y/o filtradas.
   */
  @GetMapping(PATH_GRUPOS)
  @PreAuthorize("hasAnyAuthority('PRC-VAL-V', 'PRC-VAL-E')")
  public ResponseEntity<Page<AutorGrupoOutput>> findGrupos(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findGrupos(Long id, String query, Pageable paging) - start");
    Page<AutorGrupo> page = autorGrupoService.findAllByAutorId(id, query, paging);
    if (page.isEmpty()) {
      log.debug("findGrupos(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findGrupos(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(convertGrupo(page), HttpStatus.OK);
  }

  private Page<AutorGrupoOutput> convertGrupo(Page<AutorGrupo> page) {
    List<AutorGrupoOutput> content = page.getContent().stream()
        .map((grupo) -> convert(grupo))
        .collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private AutorGrupoOutput convert(AutorGrupo grupo) {
    return modelMapper.map(grupo, AutorGrupoOutput.class);
  }
}
