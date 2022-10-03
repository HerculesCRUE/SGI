import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CONVOCATORIA_REQUISITO_IP_CONVERTER } from '@core/converters/csp/convocatoria-requisito-ip.converter';
import { IConvocatoriaRequisitoIPBackend } from '@core/models/csp/backend/convocatoria-requisito-ip-backend';
import { IConvocatoriaRequisitoIP } from '@core/models/csp/convocatoria-requisito-ip';
import { IRequisitoIPCategoriaProfesional } from '@core/models/csp/requisito-ip-categoria-profesional';
import { IRequisitoIPNivelAcademico } from '@core/models/csp/requisito-ip-nivel-academico';
import { environment } from '@env';
import { FindByIdCtor, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IRequisitoIPCategoriaProfesionalResponse } from './requisito-ip-categoria-profesional/requisito-ip-categoria-profesional-response';
import { REQUISITOIP_CATEGORIA_PROFESIONAL_RESPONSE_CONVERTER } from './requisito-ip-categoria-profesional/requisito-ip-categoria-profesional-response.converter';
import { IRequisitoIPNivelAcademicoResponse } from './requisito-ip-nivel-academico/requisito-ip-nivel-academico-response';
import { REQUISITOIP_NIVELACADEMICO_RESPONSE_CONVERTER } from './requisito-ip-nivel-academico/requisito-ip-nivel-academico-response.converter';

// tslint:disable-next-line: variable-name
const _ConvocatoriaRequisitoIPServiceMixinBase:
  FindByIdCtor<number, IConvocatoriaRequisitoIP, IConvocatoriaRequisitoIPBackend> &
  typeof SgiRestBaseService = mixinFindById(
    SgiRestBaseService,
    CONVOCATORIA_REQUISITO_IP_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ConvocatoriaRequisitoIPPublicService extends _ConvocatoriaRequisitoIPServiceMixinBase {
  private static readonly MAPPING = '/convocatoria-requisitoips';
  private static readonly PUBLIC_PREFIX = '/public';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ConvocatoriaRequisitoIPPublicService.PUBLIC_PREFIX}${ConvocatoriaRequisitoIPPublicService.MAPPING}`,
      http,
    );
  }

  /**
   * Recupera el requisito ip de la convocatoria
   * @param id convocatoria
   */
  getRequisitoIPConvocatoria(id: number): Observable<IConvocatoriaRequisitoIP> {
    return this.findById(id);
  }

  /**
   * Recupera los niveles académicos asociados al RequisitoIP con el id indicado
   * @param id Identificador del RequisitoIP
   */
  findNivelesAcademicos(id: number): Observable<IRequisitoIPNivelAcademico[]> {
    const endpointUrl = `${this.endpointUrl}/${id}/niveles`;
    const params = new HttpParams().set('id', id.toString());
    return this.http.get<IRequisitoIPNivelAcademicoResponse[]>(endpointUrl, { params })
      .pipe(
        map(r => {
          return REQUISITOIP_NIVELACADEMICO_RESPONSE_CONVERTER.toTargetArray(r);
        })
      );
  }

  /**
   * Recupera las categorías profesionales asociados al RequisitoIP con el id indicado
   * @param id Identificador del RequisitoIP
   */
  findCategoriaProfesional(id: number): Observable<IRequisitoIPCategoriaProfesional[]> {
    const endpointUrl = `${this.endpointUrl}/${id}/categoriasprofesionales`;
    const params = new HttpParams().set('id', id.toString());
    return this.http.get<IRequisitoIPCategoriaProfesionalResponse[]>(endpointUrl, { params })
      .pipe(
        map(response => {
          return REQUISITOIP_CATEGORIA_PROFESIONAL_RESPONSE_CONVERTER.toTargetArray(response);
        })
      );
  }

}
