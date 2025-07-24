import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IConvocatoriaRequisitoEquipo } from '@core/models/csp/convocatoria-requisito-equipo';
import { IRequisitoEquipoCategoriaProfesional } from '@core/models/csp/requisito-equipo-categoria-profesional';
import { IRequisitoEquipoNivelAcademico } from '@core/models/csp/requisito-equipo-nivel-academico';
import { IConvocatoriaRequisitoEquipoResponse } from '@core/services/csp/convocatoria-requisito-equipo/convocatoria-requisito-equipo-response';
import { environment } from '@env';
import { CreateCtor, FindByIdCtor, mixinCreate, mixinFindById, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { REQUISITO_EQUIPO_CATEGORIA_PROFESIONAL_REQUEST_CONVERTER } from '../requisito-equipo-categoria-profesional/requisito-equipo-categoria-profesional-request.converter';
import { IRequisitoEquipoCategoriaProfesionalResponse } from '../requisito-equipo-categoria-profesional/requisito-equipo-categoria-profesional-response';
import { REQUISITO_EQUIPO_CATEGORIA_PROFESIONAL_RESPONSE_CONVERTER } from '../requisito-equipo-categoria-profesional/requisito-equipo-categoria-profesional-response.converter';
import { REQUISITO_EQUIPO_NIVELACADEMICO_REQUEST_CONVERTER } from '../requisito-equipo-nivel-academico/requisito-equipo-nivel-academico-request.converter';
import { IRequisitoEquipoNivelAcademicoResponse } from '../requisito-equipo-nivel-academico/requisito-equipo-nivel-academico-response';
import { REQUISITO_EQUIPO_NIVELACADEMICO_RESPONSE_CONVERTER } from '../requisito-equipo-nivel-academico/requisito-equipo-nivel-academico-response.converter';
import { CONVOCATORIA_REQUISITO_EQUIPO_RESPONSE_CONVERTER } from './convocatoria-requisito-equipo-response.converter';

const _ConvocatoriaRequisitoEquipoServiceMixinBase:
  CreateCtor<IConvocatoriaRequisitoEquipo, IConvocatoriaRequisitoEquipo, IConvocatoriaRequisitoEquipoResponse, IConvocatoriaRequisitoEquipoResponse> &
  UpdateCtor<number, IConvocatoriaRequisitoEquipo, IConvocatoriaRequisitoEquipo, IConvocatoriaRequisitoEquipoResponse, IConvocatoriaRequisitoEquipoResponse> &
  FindByIdCtor<number, IConvocatoriaRequisitoEquipo, IConvocatoriaRequisitoEquipoResponse> &
  typeof SgiRestBaseService =
  mixinFindById(
    mixinUpdate(
      mixinCreate(
        SgiRestBaseService,
        CONVOCATORIA_REQUISITO_EQUIPO_RESPONSE_CONVERTER,
        CONVOCATORIA_REQUISITO_EQUIPO_RESPONSE_CONVERTER
      ),
      CONVOCATORIA_REQUISITO_EQUIPO_RESPONSE_CONVERTER,
      CONVOCATORIA_REQUISITO_EQUIPO_RESPONSE_CONVERTER
    ),
    CONVOCATORIA_REQUISITO_EQUIPO_RESPONSE_CONVERTER,
  );

@Injectable({
  providedIn: 'root'
})
export class ConvocatoriaRequisitoEquipoService extends _ConvocatoriaRequisitoEquipoServiceMixinBase {
  private static readonly MAPPING = '/convocatoria-requisitoequipos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ConvocatoriaRequisitoEquipoService.MAPPING}`,
      http
    );
  }

  /**
   * Recupera los niveles académicos asociados al RequisitoEquipo con el id indicado
   * @param id Identificador del RequisitoEquipo
   */
  findNivelesAcademicos(id: number): Observable<IRequisitoEquipoNivelAcademico[]> {
    const endpointUrl = `${this.endpointUrl}/${id}/niveles`;
    const params = new HttpParams().set('id', id.toString());
    return this.http.get<IRequisitoEquipoNivelAcademicoResponse[]>(endpointUrl, { params })
      .pipe(
        map(r => {
          return REQUISITO_EQUIPO_NIVELACADEMICO_RESPONSE_CONVERTER.toTargetArray(r);
        })
      );
  }

  /**
   * Actualiza los niveles académicos asociados al RequisitoEquipo con el id indicado
   * @param id Identificador del RequisitoEquipo
   * @param nivelesAcademicos niveles academicos a actualizar
   */
  updateNivelesAcademicos(id: number, nivelesAcademicos: IRequisitoEquipoNivelAcademico[]): Observable<IRequisitoEquipoNivelAcademico[]> {
    return this.http.patch<IRequisitoEquipoNivelAcademicoResponse[]>(`${this.endpointUrl}/${id}/niveles`,
      REQUISITO_EQUIPO_NIVELACADEMICO_REQUEST_CONVERTER.fromTargetArray(nivelesAcademicos)
    ).pipe(
      map((response => REQUISITO_EQUIPO_NIVELACADEMICO_RESPONSE_CONVERTER.toTargetArray(response)))
    );
  }

  /**
   * Recupera las categorías profesionales asociados al RequisitoEquipo con el id indicado
   * @param id Identificador del RequisitoEquipo
   */
  findCategoriaProfesional(id: number): Observable<IRequisitoEquipoCategoriaProfesional[]> {
    const endpointUrl = `${this.endpointUrl}/${id}/categoriasprofesionalesrequisitosequipo`;
    const params = new HttpParams().set('id', id.toString());
    return this.http.get<IRequisitoEquipoCategoriaProfesionalResponse[]>(endpointUrl, { params })
      .pipe(
        map(response => {
          return REQUISITO_EQUIPO_CATEGORIA_PROFESIONAL_RESPONSE_CONVERTER.toTargetArray(response);
        })
      );
  }

  /**
   * Actualiza las categorías académicas asociados al RequisitoEquipo con el id indicado
   * @param id Identificador del RequisitoEquipo
   * @param nivelesAcademicos niveles academicos a actualizar
   */
  updateCategoriasProfesionales(id: number, nivelesAcademicos: IRequisitoEquipoCategoriaProfesional[]):
    Observable<IRequisitoEquipoCategoriaProfesional[]> {
    return this.http.patch<IRequisitoEquipoCategoriaProfesionalResponse[]>(`${this.endpointUrl}/${id}/categoriasprofesionalesrequisitosequipo`,
      REQUISITO_EQUIPO_CATEGORIA_PROFESIONAL_REQUEST_CONVERTER.fromTargetArray(nivelesAcademicos)
    ).pipe(
      map((response => REQUISITO_EQUIPO_CATEGORIA_PROFESIONAL_RESPONSE_CONVERTER.toTargetArray(response)))
    );
  }

  /**
   * Recupera el Requisito del equipo asociado a la convocatoria.
   *
   * @param id Id de la convocatoria
   */
  findByConvocatoriaId(id: number): Observable<IConvocatoriaRequisitoEquipo> {
    return this.http.get<IConvocatoriaRequisitoEquipoResponse>(`${this.endpointUrl}/${id}`).pipe(
      map(response => CONVOCATORIA_REQUISITO_EQUIPO_RESPONSE_CONVERTER.toTarget(response))
    );

  }
}
