import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IProyectoSocioPeriodoJustificacionDocumento } from '@core/models/csp/proyecto-socio-periodo-justificacion-documento';
import { IProyectoSocioPeriodoJustificacionDocumentoResponse } from '@core/services/csp/proyecto-socio-periodo-justificacion-documento/proyecto-socio-periodo-justificacion-documento-response';
import { PROYECTO_SOCIO_PERIODO_JUSTIFICACION_DOCUMENTO_RESPONSE_CONVERTER } from '@core/services/csp/proyecto-socio-periodo-justificacion-documento/proyecto-socio-periodo-justificacion-documento-response.converter';
import { environment } from '@env';
import { CreateCtor, mixinCreate, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

const _ProyectoSocioPeriodoJustificacionDocumentoServiceMixinBase:
  CreateCtor<IProyectoSocioPeriodoJustificacionDocumento, IProyectoSocioPeriodoJustificacionDocumento, IProyectoSocioPeriodoJustificacionDocumentoResponse, IProyectoSocioPeriodoJustificacionDocumentoResponse> &
  UpdateCtor<number, IProyectoSocioPeriodoJustificacionDocumento, IProyectoSocioPeriodoJustificacionDocumento, IProyectoSocioPeriodoJustificacionDocumentoResponse, IProyectoSocioPeriodoJustificacionDocumentoResponse> &
  typeof SgiRestBaseService =
  mixinUpdate(
    mixinCreate(
      SgiRestBaseService,
      PROYECTO_SOCIO_PERIODO_JUSTIFICACION_DOCUMENTO_RESPONSE_CONVERTER,
      PROYECTO_SOCIO_PERIODO_JUSTIFICACION_DOCUMENTO_RESPONSE_CONVERTER
    ),
    PROYECTO_SOCIO_PERIODO_JUSTIFICACION_DOCUMENTO_RESPONSE_CONVERTER,
    PROYECTO_SOCIO_PERIODO_JUSTIFICACION_DOCUMENTO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ProyectoSocioPeriodoJustificacionDocumentoService extends _ProyectoSocioPeriodoJustificacionDocumentoServiceMixinBase {
  private static readonly MAPPING = '/proyectosocioperiodojustificaciondocumentos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ProyectoSocioPeriodoJustificacionDocumentoService.MAPPING}`,
      http
    );
  }

  updateList(proyectoSocioPeriodoJustificacionId: number, entities: IProyectoSocioPeriodoJustificacionDocumento[]):
    Observable<IProyectoSocioPeriodoJustificacionDocumento[]> {
    return this.http.patch<IProyectoSocioPeriodoJustificacionDocumentoResponse[]>(
      `${this.endpointUrl}/${proyectoSocioPeriodoJustificacionId}`,
      PROYECTO_SOCIO_PERIODO_JUSTIFICACION_DOCUMENTO_RESPONSE_CONVERTER.fromTargetArray(entities)
    ).pipe(
      map(response => PROYECTO_SOCIO_PERIODO_JUSTIFICACION_DOCUMENTO_RESPONSE_CONVERTER.toTargetArray(response))
    );
  }
}
