import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IProyectoResumen } from '@core/models/csp/proyecto-resumen';
import { environment } from '@env';
import { FindByIdCtor, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';
import { IProyectoResumenResponse } from './proyecto-resumen-response';
import { PROYECTO_RESUMEN_RESPONSE_CONVERTER } from './proyecto-resumen-response.converter';

// tslint:disable-next-line: variable-name
const _ProyectoResumenServiceMixinBase:
  FindByIdCtor<number, IProyectoResumen, IProyectoResumenResponse> &
  typeof SgiRestBaseService = mixinFindById(
    SgiRestBaseService,
    PROYECTO_RESUMEN_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ProyectoResumenService extends _ProyectoResumenServiceMixinBase {

  private static readonly MAPPING = '/proyectos-resumen';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ProyectoResumenService.MAPPING}`,
      http,
    );
  }
}
