import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IActa } from '@core/models/eti/acta';
import { IAsistente } from '@core/models/eti/asistente';
import { IConvocatoriaReunion } from '@core/models/eti/convocatoria-reunion';
import { IConvocatoriaReunionDatosGenerales } from '@core/models/eti/convocatoria-reunion-datos-generales';
import { IDocumentacionConvocatoriaReunion } from '@core/models/eti/documentacion-convocatoria-reunion';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { IConvocatoriaReunionDatosGeneralesResponse } from '@core/services/eti/convocatoria-reunion/convocatoria-reunion-datos-generales-response';
import { CONVOCATORIA_REUNION_DATOS_GENERALES_RESPONSE_CONVERTER } from '@core/services/eti/convocatoria-reunion/convocatoria-reunion-datos-generales-response.converter';
import { IConvocatoriaReunionResponse } from '@core/services/eti/convocatoria-reunion/convocatoria-reunion-response';
import { CONVOCATORIA_REUNION_RESPONSE_CONVERTER } from '@core/services/eti/convocatoria-reunion/convocatoria-reunion-response.converter';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate, mixinFindAll, mixinFindById, mixinUpdate, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IActaResponse } from './acta/acta-response';
import { ACTA_RESPONSE_CONVERTER } from './acta/acta-response.converter';
import { IAsistenteResponse } from './asistente/asistente-response';
import { ASISTENTE_RESPONSE_CONVERTER } from './asistente/asistente-response.converter';
import { IDocumentacionConvocatoriaReunionResponse } from './documentacion-convocatoria-reunion/documentacion-convocatoria-reunion-response';
import { DOCUMENTACION_CONVOCATORIA_REUNION_RESPONSE_CONVERTER } from './documentacion-convocatoria-reunion/documentacion-convocatoria-reunion-response.converter';
import { IEvaluacionResponse } from './evaluacion/evaluacion-response';
import { EVALUACION_RESPONSE_CONVERTER } from './evaluacion/evaluacion-response.converter';

// tslint:disable-next-line: variable-name
const _ConvocatoriaReunionServiceMixinBase:
  CreateCtor<IConvocatoriaReunion, IConvocatoriaReunion, IConvocatoriaReunionResponse, IConvocatoriaReunionResponse> &
  UpdateCtor<number, IConvocatoriaReunion, IConvocatoriaReunion, IConvocatoriaReunionResponse, IConvocatoriaReunionResponse> &
  FindByIdCtor<number, IConvocatoriaReunion, IConvocatoriaReunionResponse> &
  FindAllCtor<IConvocatoriaReunion, IConvocatoriaReunionResponse> &
  typeof SgiRestBaseService =
  mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          CONVOCATORIA_REUNION_RESPONSE_CONVERTER,
          CONVOCATORIA_REUNION_RESPONSE_CONVERTER
        ),
        CONVOCATORIA_REUNION_RESPONSE_CONVERTER,
        CONVOCATORIA_REUNION_RESPONSE_CONVERTER
      ),
      CONVOCATORIA_REUNION_RESPONSE_CONVERTER
    ), CONVOCATORIA_REUNION_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root',
})
export class ConvocatoriaReunionService extends _ConvocatoriaReunionServiceMixinBase {
  private static readonly MAPPING = '/convocatoriareuniones';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.eti}${ConvocatoriaReunionService.MAPPING}`,
      http
    );
  }

  deleteById(idConvocatoria: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${idConvocatoria}`);
  }

  /**
   * Devuelve todos los asistentes por convocatoria id.
   * @param idConvocatoria id convocatoria.
   */
  findAsistentes(idConvocatoria: number): Observable<SgiRestListResult<IAsistente>> {
    return this.find<IAsistenteResponse, IAsistente>(
      `${this.endpointUrl}/${idConvocatoria}/asistentes`,
      null,
      ASISTENTE_RESPONSE_CONVERTER
    );
  }

  /**
   * Devuelve todos las evaluaciones por convocatoria id.
   * @param idConvocatoria id convocatoria.
   */
  findEvaluacionesActivas(idConvocatoria: number): Observable<SgiRestListResult<IEvaluacion>> {
    return this.find<IEvaluacionResponse, IEvaluacion>(
      `${this.endpointUrl}/${idConvocatoria}/evaluaciones-activas`,
      null,
      EVALUACION_RESPONSE_CONVERTER
    );
  }

  /**
   * Devuelve la convocatoria por id.con el número de evaluaciones activas que no son revisión mínima
   * @param idConvocatoria id convocatoria.
   */
  public findByIdWithDatosGenerales(idConvocatoria: number): Observable<IConvocatoriaReunionDatosGenerales> {
    return this.http.get<IConvocatoriaReunionDatosGeneralesResponse>(
      `${this.endpointUrl}/${idConvocatoria}/datos-generales`
    ).pipe(
      map((convocatoriaReunion) => {
        return CONVOCATORIA_REUNION_DATOS_GENERALES_RESPONSE_CONVERTER.toTarget(convocatoriaReunion);
      })
    );
  }

  /** Elimina las memorias asignadas a la convocatoria de reunión
   * @param idConvocatoria el identificador de la convocatoria
   * @param idEvaluacion el identificador de la evaluación
   */
  deleteEvaluacion(idConvocatoria: number, idEvaluacion: number) {
    return this.http.delete<void>(`${this.endpointUrl}/${idConvocatoria}/evaluacion/${idEvaluacion}`);
  }

  /**
   * Devuelve todos las convocatorias que no estén asociadas a un acta.
   */
  findConvocatoriasSinActa(): Observable<SgiRestListResult<IConvocatoriaReunion>> {
    return this.find<IConvocatoriaReunionResponse, IConvocatoriaReunion>(
      `${this.endpointUrl}/acta-no-asignada`,
      null,
      CONVOCATORIA_REUNION_RESPONSE_CONVERTER
    );
  }

  /**
   * Comprueba si cumple los requisitos para poder eliminar la convocatoria de reunión
   *
   * @param id Id de la convocatoria de reunión
   */
  eliminable(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/eliminable`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Comprueba si tiene permisos de edición de la convocatoria de reunión
   *
   * @param id Id de la convocatoria de reunión
   */
  modificable(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/modificable`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Devuelve el acta asociada ala convocatoria de reunión
   * @param idConvocatoria id convocatoria.
   */
  public findActaInConvocatoriaReunion(idConvocatoria: number): Observable<IActa> {
    return this.http.get<IActaResponse>(
      `${this.endpointUrl}/${idConvocatoria}/acta`
    ).pipe(
      map((acta) => {
        return ACTA_RESPONSE_CONVERTER.toTarget(acta);
      })
    );
  }

  /**
   * Permite enviar el comunicado de la convocatoria de reunión
   * @param idConvocatoria id convocatoria
   */
  enviarComunicado(idConvocatoria: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${idConvocatoria}/comunicado`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
 * Devuelve todos las evaluaciones por convocatoria id.
 * @param idConvocatoria id convocatoria.
 */
  findEvaluacionesTodas(idConvocatoria: number): Observable<SgiRestListResult<IEvaluacion>> {
    return this.find<IEvaluacionResponse, IEvaluacion>(
      `${this.endpointUrl}/${idConvocatoria}/evaluaciones/todas`,
      null,
      EVALUACION_RESPONSE_CONVERTER
    );
  }

  /**
 * Devuelve toda la documentación asociada a una convocatoria de reunión
 *
 * @param id id de la convocatoria de reunión.
 */
  getDocumentaciones(id: number, options?: SgiRestFindOptions)
    : Observable<SgiRestListResult<IDocumentacionConvocatoriaReunion>> {
    return this.find<IDocumentacionConvocatoriaReunionResponse, IDocumentacionConvocatoriaReunion>(
      `${this.endpointUrl}/${id}/documentaciones`,
      options,
      DOCUMENTACION_CONVOCATORIA_REUNION_RESPONSE_CONVERTER
    );
  }

  /**
  * Crea el documento de una convocatoria de reunión
  *
  * @param id Identifiacdor de la convocatoria de reunión.
  * @param documentacionConvocatoriaReunion La documentación de convocatoria de reunión.
  *
  * @returns observable para eliminar documentación.
  */
  createDocumentacion(id: number, documentacionConvocatoriaReunion: IDocumentacionConvocatoriaReunion): Observable<IDocumentacionConvocatoriaReunion> {
    return this.http.post<IDocumentacionConvocatoriaReunionResponse>(
      `${this.endpointUrl}/${id}/documentacion`,
      DOCUMENTACION_CONVOCATORIA_REUNION_RESPONSE_CONVERTER.fromTarget(documentacionConvocatoriaReunion)
    ).pipe(
      map(response => DOCUMENTACION_CONVOCATORIA_REUNION_RESPONSE_CONVERTER.toTarget(response))
    );
  }

  /**
  * Actualiza el documento de una convocatoria de reunión
  *
  * @param id Identifiacdor de la convocatoria de reunión.
  * @param documentacionConvocatoriaReunion La documentación de convocatoria de reunión.
  *
  * @returns observable para eliminar documentación.
  */
  updateDocumentacion(id: number, documentacionConvocatoriaReunion: IDocumentacionConvocatoriaReunion): Observable<IDocumentacionConvocatoriaReunion> {
    return this.http.put<IDocumentacionConvocatoriaReunionResponse>(
      `${this.endpointUrl}/${id}/documentacion/${documentacionConvocatoriaReunion.id}`,
      DOCUMENTACION_CONVOCATORIA_REUNION_RESPONSE_CONVERTER.fromTarget(documentacionConvocatoriaReunion)
    ).pipe(
      map(response => DOCUMENTACION_CONVOCATORIA_REUNION_RESPONSE_CONVERTER.toTarget(response))
    );
  }

  /**
 * Elimina el documento de una convocatoria de reunión.
 *
 * @param id Identifiacdor de la convocatoria de reunión.
 * @param idDocumentacionConvocatoriaReunion Identificador del documento de convocatoria de reunión.
 *
 * @returns observable para eliminar documentación.
 */
  deleteDocumentacion(id: number, idDocumentacionConvocatoriaReunion: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}/documentacion/${idDocumentacionConvocatoriaReunion}`);
  }
}
