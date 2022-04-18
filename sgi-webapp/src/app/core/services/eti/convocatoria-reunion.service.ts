import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ACTA_CONVERTER } from '@core/converters/eti/acta.converter';
import { ASISTENTE_CONVERTER } from '@core/converters/eti/asistente.converter';
import { CONVOCATORIA_REUNION_DATOS_GENERALES_CONVERTER } from '@core/converters/eti/convocatoria-reunion-datos-generales.converter';
import { CONVOCATORIA_REUNION_CONVERTER } from '@core/converters/eti/convocatoria-reunion.converter';
import { EVALUACION_CONVERTER } from '@core/converters/eti/evaluacion.converter';
import { IActa } from '@core/models/eti/acta';
import { IAsistente } from '@core/models/eti/asistente';
import { IActaBackend } from '@core/models/eti/backend/acta-backend';
import { IAsistenteBackend } from '@core/models/eti/backend/asistente-backend';
import { IConvocatoriaReunionBackend } from '@core/models/eti/backend/convocatoria-reunion-backend';
import { IConvocatoriaReunionDatosGeneralesBackend } from '@core/models/eti/backend/convocatoria-reunion-datos-generales-backend';
import { IEvaluacionBackend } from '@core/models/eti/backend/evaluacion-backend';
import { IConvocatoriaReunion } from '@core/models/eti/convocatoria-reunion';
import { IConvocatoriaReunionDatosGenerales } from '@core/models/eti/convocatoria-reunion-datos-generales';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { environment } from '@env';
import { SgiMutableRestService, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class ConvocatoriaReunionService extends SgiMutableRestService<number, IConvocatoriaReunionBackend, IConvocatoriaReunion> {
  private static readonly MAPPING = '/convocatoriareuniones';

  constructor(protected http: HttpClient) {
    super(
      ConvocatoriaReunionService.name,
      `${environment.serviceServers.eti}${ConvocatoriaReunionService.MAPPING}`,
      http,
      CONVOCATORIA_REUNION_CONVERTER
    );
  }

  /**
   * Devuelve todos los asistentes por convocatoria id.
   * @param idConvocatoria id convocatoria.
   */
  findAsistentes(idConvocatoria: number): Observable<SgiRestListResult<IAsistente>> {
    return this.find<IAsistenteBackend, IAsistente>(
      `${this.endpointUrl}/${idConvocatoria}/asistentes`,
      null,
      ASISTENTE_CONVERTER
    );
  }

  /**
   * Devuelve todos las evaluaciones por convocatoria id.
   * @param idConvocatoria id convocatoria.
   */
  findEvaluacionesActivas(idConvocatoria: number): Observable<SgiRestListResult<IEvaluacion>> {
    return this.find<IEvaluacionBackend, IEvaluacion>(
      `${this.endpointUrl}/${idConvocatoria}/evaluaciones-activas`,
      null,
      EVALUACION_CONVERTER
    );
  }

  /**
   * Devuelve la convocatoria por id.con el número de evaluaciones activas que no son revisión mínima
   * @param idConvocatoria id convocatoria.
   */
  public findByIdWithDatosGenerales(idConvocatoria: number): Observable<IConvocatoriaReunionDatosGenerales> {
    return this.http.get<IConvocatoriaReunionDatosGeneralesBackend>(
      `${this.endpointUrl}/${idConvocatoria}/datos-generales`
    ).pipe(
      map((convocatoriaReunion) => {
        return CONVOCATORIA_REUNION_DATOS_GENERALES_CONVERTER.toTarget(convocatoriaReunion);
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
    return this.find<IConvocatoriaReunionBackend, IConvocatoriaReunion>(
      `${this.endpointUrl}/acta-no-asignada`,
      null,
      CONVOCATORIA_REUNION_CONVERTER
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
    return this.http.get<IActaBackend>(
      `${this.endpointUrl}/${idConvocatoria}/acta`
    ).pipe(
      map((acta) => {
        return ACTA_CONVERTER.toTarget(acta);
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
}
