package org.crue.hercules.sgi.csp.controller;

import lombok.RequiredArgsConstructor;
import org.crue.hercules.sgi.csp.model.TipoFacturacion;
import org.crue.hercules.sgi.csp.service.TipoFacturacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(TipoFacturacionController.REQUEST_MAPPING)
public class TipoFacturacionController {
  protected static final String REQUEST_MAPPING = "/tiposfacturacion";

  private final TipoFacturacionService tipoFacturacionService;

  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-V', 'CSP-PRO-C', 'CSP-PRO-E', 'CSP-PRO-B', 'CSP-PRO-MOD-V')")
  public ResponseEntity<List<TipoFacturacion>> findAll() {
    List<TipoFacturacion> tipos = this.tipoFacturacionService.findAll();
    return CollectionUtils.isEmpty(tipos) ? ResponseEntity.noContent().build() : ResponseEntity.ok(tipos);
  }
}
