import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PROYECTO_SOCIO_PERIODO_JUSTIFICACION_DOCUMENTO_CONVERTER } from '@core/converters/csp/proyecto-socio-periodo-justificacion-documento.converter';
import { IProyectoSocioPeriodoJustificacionDocumentoBackend } from '@core/models/csp/backend/proyecto-socio-periodo-justificacion-documento-backend';
import { IProyectoSocioPeriodoJustificacionDocumento } from '@core/models/csp/proyecto-socio-periodo-justificacion-documento';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ProyectoSocioPeriodoJustificacionDocumentoService
  extends SgiMutableRestService<number, IProyectoSocioPeriodoJustificacionDocumentoBackend, IProyectoSocioPeriodoJustificacionDocumento> {
  private static readonly MAPPING = '/proyectosocioperiodojustificaciondocumentos';

  constructor(protected http: HttpClient) {
    super(
      ProyectoSocioPeriodoJustificacionDocumentoService.name,
      `${environment.serviceServers.csp}${ProyectoSocioPeriodoJustificacionDocumentoService.MAPPING}`,
      http,
      PROYECTO_SOCIO_PERIODO_JUSTIFICACION_DOCUMENTO_CONVERTER
    );
  }

  updateList(proyectoSocioPeriodoJustificacionId: number, entities: IProyectoSocioPeriodoJustificacionDocumento[]):
    Observable<IProyectoSocioPeriodoJustificacionDocumento[]> {
    return this.http.patch<IProyectoSocioPeriodoJustificacionDocumentoBackend[]>(
      `${this.endpointUrl}/${proyectoSocioPeriodoJustificacionId}`,
      this.converter.fromTargetArray(entities)
    ).pipe(
      map(response => this.converter.toTargetArray(response))
    );
  }
}
