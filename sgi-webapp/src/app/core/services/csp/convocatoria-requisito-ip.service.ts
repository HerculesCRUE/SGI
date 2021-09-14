import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CONVOCATORIA_REQUISITO_IP_CONVERTER } from '@core/converters/csp/convocatoria-requisito-ip.converter';
import { IConvocatoriaRequisitoIPBackend } from '@core/models/csp/backend/convocatoria-requisito-ip-backend';
import { IConvocatoriaRequisitoIP } from '@core/models/csp/convocatoria-requisito-ip';
import { IRequisitoIPCategoriaProfesional } from '@core/models/csp/requisito-ip-categoria-profesional';
import { IRequisitoIPNivelAcademico } from '@core/models/csp/requisito-ip-nivel-academico';
import { environment } from '@env';
import { CreateCtor, mixinCreate, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { REQUISITOIP_CATEGORIA_PROFESIONAL_REQUEST_CONVERTER } from './requisito-ip-categoria-profesional/requisito-ip-categoria-profesional-request.converter';
import { IRequisitoIPCategoriaProfesionalResponse } from './requisito-ip-categoria-profesional/requisito-ip-categoria-profesional-response';
import { REQUISITOIP_CATEGORIA_PROFESIONAL_RESPONSE_CONVERTER } from './requisito-ip-categoria-profesional/requisito-ip-categoria-profesional-response.converter';
import { REQUISITOIP_NIVELACADEMICO_REQUEST_CONVERTER }  from './requisito-ip-nivel-academico/requisito-ip-nivel-academico-request.converter';
import { IRequisitoIPNivelAcademicoResponse } from './requisito-ip-nivel-academico/requisito-ip-nivel-academico-response';
import { REQUISITOIP_NIVELACADEMICO_RESPONSE_CONVERTER } from './requisito-ip-nivel-academico/requisito-ip-nivel-academico-response.converter';

// tslint:disable-next-line: variable-name
const _ConvocatoriaRequisitoIPServiceMixinBase:
  CreateCtor<IConvocatoriaRequisitoIP, IConvocatoriaRequisitoIP, IConvocatoriaRequisitoIPBackend, IConvocatoriaRequisitoIPBackend> &
  UpdateCtor<number, IConvocatoriaRequisitoIP, IConvocatoriaRequisitoIP, IConvocatoriaRequisitoIPBackend, IConvocatoriaRequisitoIPBackend> &
  typeof SgiRestBaseService =
  mixinUpdate(
    mixinCreate(
      SgiRestBaseService,
      CONVOCATORIA_REQUISITO_IP_CONVERTER,
      CONVOCATORIA_REQUISITO_IP_CONVERTER
    ),
    CONVOCATORIA_REQUISITO_IP_CONVERTER,
    CONVOCATORIA_REQUISITO_IP_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ConvocatoriaRequisitoIPService extends _ConvocatoriaRequisitoIPServiceMixinBase {
  private static readonly MAPPING = '/convocatoria-requisitoips';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ConvocatoriaRequisitoIPService.MAPPING}`,
      http,
    );
  }

  /**
   * Recupera el requisito ip de la convocatoria
   * @param id convocatoria
   */
  getRequisitoIPConvocatoria(id: number): Observable<IConvocatoriaRequisitoIP> {
    const endpointUrl = `${this.endpointUrl}/${id}`;
    return this.http.get<IConvocatoriaRequisitoIPBackend>(endpointUrl).pipe(
      map(response => CONVOCATORIA_REQUISITO_IP_CONVERTER.toTarget(response)),
      catchError((err) => {
        return of({} as IConvocatoriaRequisitoIP);
      })
    );
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
   * Actualiza los niveles académicos asociados al RequisitoIP con el id indicado
   * @param id Identificador del RequisitoIP
   * @param nivelesAcademicos niveles academicos a actualizar
   */
  updateNivelesAcademicos(id: number, nivelesAcademicos: IRequisitoIPNivelAcademico[]): Observable<IRequisitoIPNivelAcademico[]> {
    return this.http.patch<IRequisitoIPNivelAcademicoResponse[]>(`${this.endpointUrl}/${id}/niveles`,
      REQUISITOIP_NIVELACADEMICO_REQUEST_CONVERTER.fromTargetArray(nivelesAcademicos)
    ).pipe(
      map((response => REQUISITOIP_NIVELACADEMICO_RESPONSE_CONVERTER.toTargetArray(response)))
    );
  }

  /**
   * Recupera las categorías profesionales asociados al RequisitoIP con el id indicado
   * @param id Identificador del RequisitoIP
   */
  findCategoriaProfesional(id: number): Observable<IRequisitoIPCategoriaProfesional[]> {
    const endpointUrl = `${this.endpointUrl}/${id}/categorias`;
    const params = new HttpParams().set('id', id.toString());
    return this.http.get<IRequisitoIPCategoriaProfesionalResponse[]>(endpointUrl, { params })
      .pipe(
        map(response => {
          return REQUISITOIP_CATEGORIA_PROFESIONAL_RESPONSE_CONVERTER.toTargetArray(response);
        })
      );
  }

  /**
   * Actualiza las categorías académicas asociados al RequisitoIP con el id indicado
   * @param id Identificador del RequisitoIP
   * @param nivelesAcademicos niveles academicos a actualizar
   */
  updateCategoriasProfesionales(id: number, nivelesAcademicos: IRequisitoIPCategoriaProfesional[]):
    Observable<IRequisitoIPCategoriaProfesional[]> {
    return this.http.patch<IRequisitoIPCategoriaProfesionalResponse[]>(`${this.endpointUrl}/${id}/categorias`,
      REQUISITOIP_CATEGORIA_PROFESIONAL_REQUEST_CONVERTER.fromTargetArray(nivelesAcademicos)
    ).pipe(
      map((response => REQUISITOIP_CATEGORIA_PROFESIONAL_RESPONSE_CONVERTER.toTargetArray(response)))
    );
  }
}
