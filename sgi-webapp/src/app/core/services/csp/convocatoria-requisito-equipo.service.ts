import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CONVOCATORIA_REQUISITO_EQUIPO_CONVERTER } from '@core/converters/csp/convocatoria-requisito-equipo.converter';
import { IConvocatoriaRequisitoEquipoBackend } from '@core/models/csp/backend/convocatoria-requisito-equipo-backend';
import { IConvocatoriaRequisitoEquipo } from '@core/models/csp/convocatoria-requisito-equipo';
import { IRequisitoEquipoCategoriaProfesional } from '@core/models/csp/requisito-equipo-categoria-profesional';
import { IRequisitoEquipoNivelAcademico } from '@core/models/csp/requisito-equipo-nivel-academico';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { REQUISITO_EQUIPO_CATEGORIA_PROFESIONAL_REQUEST_CONVERTER } from './requisito-equipo-categoria-profesional/requisito-equipo-categoria-profesional-request.converter';
import { IRequisitoEquipoCategoriaProfesionalResponse } from './requisito-equipo-categoria-profesional/requisito-equipo-categoria-profesional-response';
import { REQUISITO_EQUIPO_CATEGORIA_PROFESIONAL_RESPONSE_CONVERTER } from './requisito-equipo-categoria-profesional/requisito-equipo-categoria-profesional-response.converter';
import { REQUISITO_EQUIPO_NIVELACADEMICO_REQUEST_CONVERTER } from './requisito-equipo-nivel-academico/requisito-equipo-nivel-academico-request.converter';
import { IRequisitoEquipoNivelAcademicoResponse } from './requisito-equipo-nivel-academico/requisito-equipo-nivel-academico-response';
import { REQUISITO_EQUIPO_NIVELACADEMICO_RESPONSE_CONVERTER } from './requisito-equipo-nivel-academico/requisito-equipo-nivel-academico-response.converter';

@Injectable({
  providedIn: 'root'
})
export class ConvocatoriaRequisitoEquipoService
  extends SgiMutableRestService<number, IConvocatoriaRequisitoEquipoBackend, IConvocatoriaRequisitoEquipo> {
  private static readonly MAPPING = '/convocatoria-requisitoequipos';

  constructor(protected http: HttpClient) {
    super(
      ConvocatoriaRequisitoEquipoService.name,
      `${environment.serviceServers.csp}${ConvocatoriaRequisitoEquipoService.MAPPING}`,
      http,
      CONVOCATORIA_REQUISITO_EQUIPO_CONVERTER
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
    const endpointUrl = `${this.endpointUrl}/${id}/categorias`;
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
    return this.http.patch<IRequisitoEquipoCategoriaProfesionalResponse[]>(`${this.endpointUrl}/${id}/categorias`,
      REQUISITO_EQUIPO_CATEGORIA_PROFESIONAL_REQUEST_CONVERTER.fromTargetArray(nivelesAcademicos)
    ).pipe(
      map((response => REQUISITO_EQUIPO_CATEGORIA_PROFESIONAL_RESPONSE_CONVERTER.toTargetArray(response)))
    );
  }
}
