import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ACTA_WITH_NUM_EVALUACIONES_CONVERTER } from '@core/converters/eti/acta-with-num-evaluaciones.converter';
import { ACTA_CONVERTER } from '@core/converters/eti/acta.converter';
import { COMENTARIO_CONVERTER } from '@core/converters/eti/comentario.converter';
import { DOCUMENTO_CONVERTER } from '@core/converters/sgdoc/documento.converter';
import { IActa } from '@core/models/eti/acta';
import { IActaWithNumEvaluaciones } from '@core/models/eti/acta-with-num-evaluaciones';
import { IActaBackend } from '@core/models/eti/backend/acta-backend';
import { IActaWithNumEvaluacionesBackend } from '@core/models/eti/backend/acta-with-num-evaluaciones-backend';
import { IComentarioBackend } from '@core/models/eti/backend/comentario-backend';
import { IComentario } from '@core/models/eti/comentario';
import { IDocumentoBackend } from '@core/models/sgdoc/backend/documento-backend';
import { IDocumento } from '@core/models/sgdoc/documento';
import { environment } from '@env';
import { SgiMutableRestService, SgiRestFindOptions } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ActaService extends SgiMutableRestService<number, IActaBackend, IActa> {
  private static readonly MAPPING = '/actas';

  constructor(protected http: HttpClient) {
    super(
      ActaService.name,
      `${environment.serviceServers.eti}${ActaService.MAPPING}`,
      http,
      ACTA_CONVERTER
    );
  }

  /**
   * Recupera el listado de actas activas con el número de evaluaciones iniciales, en revisión y las totales de ambas.
   * @param options opciones de búsqueda.
   * @returns listado de actas.
   */
  findActivasWithEvaluaciones(options?: SgiRestFindOptions) {
    return this.find<IActaWithNumEvaluacionesBackend, IActaWithNumEvaluaciones>(
      `${this.endpointUrl}`,
      options,
      ACTA_WITH_NUM_EVALUACIONES_CONVERTER
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
    return this.http.get<IDocumentoBackend>(
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
    return this.http.get<IComentarioBackend[]>(`${this.endpointUrl}/${id}/comentarios-evaluador/${personaRef}/persona`)
      .pipe(
        map(r => {
          if (r == null) {
            return [];
          }
          return COMENTARIO_CONVERTER.toTargetArray(r);
        })
      );
  }

}
