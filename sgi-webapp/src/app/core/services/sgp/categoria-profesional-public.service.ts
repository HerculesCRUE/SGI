import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ICategoriaProfesional } from '@core/models/sgp/categoria-profesional';
import { environment } from '@env';
import { FindByIdCtor, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';

// tslint:disable-next-line: variable-name
const _CategoriaProfesionalMixinBase:
  FindByIdCtor<string, ICategoriaProfesional, ICategoriaProfesional> &
  typeof SgiRestBaseService = mixinFindById(
    SgiRestBaseService
  );

@Injectable({
  providedIn: 'root'
})
export class CategoriaProfesionalPublicService extends _CategoriaProfesionalMixinBase {
  private static readonly MAPPING = '/categorias-profesionales';
  private static readonly PUBLIC_PREFIX = '/public';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sgp}${CategoriaProfesionalPublicService.PUBLIC_PREFIX}${CategoriaProfesionalPublicService.MAPPING}`,
      http
    );
  }

}
