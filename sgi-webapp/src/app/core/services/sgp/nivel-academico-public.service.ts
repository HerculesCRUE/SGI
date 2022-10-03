import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { INivelAcademico } from '@core/models/sgp/nivel-academico';
import { environment } from '@env';
import { FindByIdCtor, mixinFindById, SgiRestBaseService, SgiRestService } from '@sgi/framework/http';

// tslint:disable-next-line: variable-name
const _NivelAcademicoMixinBase:
  FindByIdCtor<string, INivelAcademico, INivelAcademico> &
  typeof SgiRestBaseService = mixinFindById(
    SgiRestBaseService
  );

@Injectable({
  providedIn: 'root'
})
export class NivelAcademicoPublicService extends _NivelAcademicoMixinBase {
  private static readonly MAPPING = '/niveles-academicos';
  private static readonly PUBLIC_PREFIX = '/public';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sgp}${NivelAcademicoPublicService.PUBLIC_PREFIX}${NivelAcademicoPublicService.MAPPING}`,
      http
    );
  }
}
