import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CONVOCATORIA_REQUISITO_EQUIPO_CONVERTER } from '@core/converters/csp/convocatoria-requisito-equipo.converter';
import { IConvocatoriaRequisitoEquipoBackend } from '@core/models/csp/backend/convocatoria-requisito-equipo-backend';
import { IConvocatoriaRequisitoEquipo } from '@core/models/csp/convocatoria-requisito-equipo';
import { IRequisitoEquipoCategoriaProfesional } from '@core/models/csp/requisito-equipo-categoria-profesional';
import { IRequisitoEquipoNivelAcademico } from '@core/models/csp/requisito-equipo-nivel-academico';
import { environment } from '@env';
import { FindByIdCtor, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IRequisitoEquipoCategoriaProfesionalResponse } from './requisito-equipo-categoria-profesional/requisito-equipo-categoria-profesional-response';
import { REQUISITO_EQUIPO_CATEGORIA_PROFESIONAL_RESPONSE_CONVERTER } from './requisito-equipo-categoria-profesional/requisito-equipo-categoria-profesional-response.converter';
import { IRequisitoEquipoNivelAcademicoResponse } from './requisito-equipo-nivel-academico/requisito-equipo-nivel-academico-response';
import { REQUISITO_EQUIPO_NIVELACADEMICO_RESPONSE_CONVERTER } from './requisito-equipo-nivel-academico/requisito-equipo-nivel-academico-response.converter';

// tslint:disable-next-line: variable-name
const _ConvocatoriaRequisitoEquipoMixinBase:
  FindByIdCtor<number, IConvocatoriaRequisitoEquipo, IConvocatoriaRequisitoEquipoBackend> &
  typeof SgiRestBaseService = mixinFindById(
    SgiRestBaseService,
    CONVOCATORIA_REQUISITO_EQUIPO_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ConvocatoriaRequisitoEquipoPublicService extends _ConvocatoriaRequisitoEquipoMixinBase {
  private static readonly MAPPING = '/convocatoria-requisitoequipos';
  private static readonly PUBLIC_PREFIX = '/public';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ConvocatoriaRequisitoEquipoPublicService.PUBLIC_PREFIX}${ConvocatoriaRequisitoEquipoPublicService.MAPPING}`,
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
   * Recupera el Requisito del equipo asociado a la convocatoria.
   *
   * @param id Id de la convocatoria
   */
  findByConvocatoriaId(id: number): Observable<IConvocatoriaRequisitoEquipo> {
    return this.http.get<IConvocatoriaRequisitoEquipoBackend>(`${this.endpointUrl}/${id}`).pipe(
      map(response => CONVOCATORIA_REQUISITO_EQUIPO_CONVERTER.toTarget(response))
    );
  }

}
