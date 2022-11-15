import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IGrupoResumen } from '@core/models/csp/grupo-resumen';
import { environment } from '@env';
import { FindByIdCtor, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';

// tslint:disable-next-line: variable-name
const _GrupoResumenServiceMixinBase:
  FindByIdCtor<number, IGrupoResumen, IGrupoResumen> &
  typeof SgiRestBaseService = mixinFindById(
    SgiRestBaseService
  );

@Injectable({
  providedIn: 'root'
})
export class GrupoResumenService extends _GrupoResumenServiceMixinBase {
  private static readonly MAPPING = '/grupos-resumen';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${GrupoResumenService.MAPPING}`,
      http,
    );
  }
}
