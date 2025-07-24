import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { DOCUMENTO_CONVERTER } from '@core/converters/sgdoc/documento.converter';
import { IActa } from '@core/models/eti/acta';
import { IActaWithNumEvaluaciones } from '@core/models/eti/acta-with-num-evaluaciones';
import { IComentario } from '@core/models/eti/comentario';
import { IDocumentoBackend } from '@core/models/sgdoc/backend/documento-backend';
import { IDocumento } from '@core/models/sgdoc/documento';
import { IActaResponse } from '@core/services/eti/acta/acta-response';
import { ACTA_RESPONSE_CONVERTER } from '@core/services/eti/acta/acta-response.converter';
import { IActaWithNumEvaluacionesResponse } from '@core/services/eti/acta/acta-with-num-evaluaciones-response';
import { ACTA_WITH_NUM_EVALUACIONES_RESPONSE_CONVERTER } from '@core/services/eti/acta/acta-with-num-evaluaciones-response.converter';
import { IComentarioResponse } from '@core/services/eti/comentario/comentario-response';
import { COMENTARIO_RESPONSE_CONVERTER } from '@core/services/eti/comentario/comentario-response.converter';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate, mixinFindAll, mixinFindById, mixinUpdate, SgiRestBaseService, SgiRestFindOptions, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

const _ActaServiceMixinBase:
  CreateCtor<IActa, IActa, IActaResponse, IActaResponse> &
  UpdateCtor<number, IActa, IActa, IActaResponse, IActaResponse> &
  FindByIdCtor<number, IActa, IActaResponse> &
  FindAllCtor<IActa, IActaResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          ACTA_RESPONSE_CONVERTER,
          ACTA_RESPONSE_CONVERTER
        ),
        ACTA_RESPONSE_CONVERTER,
        ACTA_RESPONSE_CONVERTER
      ),
      ACTA_RESPONSE_CONVERTER
    ),
    ACTA_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ActaService extends _ActaServiceMixinBase {
  private static readonly MAPPING = '/actas';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.eti}${ActaService.MAPPING}`,
      http
    );
  }

  /**
   * Recupera el listado de actas activas con el número de evaluaciones iniciales, en revisión y las totales de ambas.
   * @param options opciones de búsqueda.
   * @returns listado de actas.
   */
  findActivasWithEvaluaciones(options?: SgiRestFindOptions) {
    return this.find<IActaWithNumEvaluacionesResponse, IActaWithNumEvaluaciones>(
      `${this.endpointUrl}`,
      options,
      ACTA_WITH_NUM_EVALUACIONES_RESPONSE_CONVERTER
    );
  }

  /**
   * Finaliza el acta recibido por parámetro.
   * @param actaId id de acta.
   */
  finishActa(actaId: number): Observable<void> {
    return this.http.put<void>(`${this.endpointUrl}/${actaId}/finalizar`, null);
  }

  /**
   * Obtiene el documento del acta
   * @param idActa identificador del acta
   */
  getDocumentoActa(idActa: number): Observable<IDocumento> {
    return this.get<IDocumentoBackend>(
      `${this.endpointUrl}/${idActa}/documento`
    ).pipe(
      map(response => DOCUMENTO_CONVERTER.toTarget(response))
    );
  }

  /**
   * Comprueba si el usuario es miembro activo del comité del acta
   * @param id Id del Acta
   */
  isMiembroActivoComite(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/miembro-comite`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(x => x.status === 200)
    );
  }

  /**
   * Comprueba si el registro blockchain ha sido confirmado correctamente o ha sido alterado
   * @param id Id del Acta
   */
  isRegistroBlockchainConfirmado(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/confirmar-registro-blockchain`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(x => x.status === 200)
    );
  }

  /**
   * Comprueba si los comentarios del acta están en estado cerrado
   *
   */
  isComentariosEnviados(idEvaluacion: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${idEvaluacion}/comentarios-enviados`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
  * Comprueba si existen comentarios de otro usuario abiertos
  *
  */
  isPosibleEnviarComentarios(idEvaluacion: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${idEvaluacion}/posible-enviar-comentarios`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
* Permite enviar comentarios del acta
* 
* * @param idActa id Acta
*/
  enviarComentarios(idActa: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${idActa}/enviar-comentarios`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
 * Devuelve los comentarios de tipo EVALUADOR de una evaluación
 *
 * @param id Id del Acta
 * @param options Opciones de paginación
 */
  getComentariosPersonaEvaluador(id: number, personaRef: string): Observable<IComentario[]> {
    return this.http.get<IComentarioResponse[]>(`${this.endpointUrl}/${id}/comentarios-evaluador/${personaRef}/persona`)
      .pipe(
        map(r => {
          if (r == null) {
            return [];
          }
          return COMENTARIO_RESPONSE_CONVERTER.toTargetArray(r);
        })
      );
  }

}
