import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import { environment } from '@env';
import { FindByIdCtor, mixinFindById, SgiRestBaseService } from '@sgi/framework/http/';

// tslint:disable-next-line: variable-name
const _UnidadGestionMixinBase:
  FindByIdCtor<number, IUnidadGestion, IUnidadGestion> &
  typeof SgiRestBaseService = mixinFindById(
    SgiRestBaseService
  );

@Injectable({
  providedIn: 'root'
})
export class UnidadGestionPublicService extends _UnidadGestionMixinBase {

  private static readonly MAPPING = '/unidades';
  private static readonly PUBLIC_PREFIX = '/public';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.usr}${UnidadGestionPublicService.PUBLIC_PREFIX}${UnidadGestionPublicService.MAPPING}`,
      http
    );
  }


}
