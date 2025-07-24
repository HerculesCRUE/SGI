import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IRespuesta } from '@core/models/eti/respuesta';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate, mixinFindAll, mixinFindById, mixinUpdate, RSQLSgiRestFilter, SgiRestBaseService, SgiRestFilterOperator, SgiRestFindOptions, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IRespuestaResponse } from './respuesta/respuesta-response';
import { RESPUESTA_RESPONSE_CONVERTER } from './respuesta/respuesta-response.converter';

// tslint:disable-next-line: variable-name
const _RespuestaServiceMixinBase:
  CreateCtor<IRespuesta, IRespuesta, IRespuestaResponse, IRespuestaResponse> &
  UpdateCtor<number, IRespuesta, IRespuesta, IRespuestaResponse, IRespuestaResponse> &
  FindByIdCtor<number, IRespuesta, IRespuestaResponse> &
  FindAllCtor<IRespuesta, IRespuestaResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          RESPUESTA_RESPONSE_CONVERTER,
          RESPUESTA_RESPONSE_CONVERTER
        ),
        RESPUESTA_RESPONSE_CONVERTER,
        RESPUESTA_RESPONSE_CONVERTER
      ),
      RESPUESTA_RESPONSE_CONVERTER
    ),
    RESPUESTA_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class RespuestaService extends _RespuestaServiceMixinBase {
  private static readonly MAPPING = '/respuestas';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.eti}${RespuestaService.MAPPING}`,
      http
    );
  }

  findByMemoriaIdAndApartadoId(memoriaId: number, apartadoId: number): Observable<IRespuesta> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('memoria.id', SgiRestFilterOperator.EQUALS, memoriaId.toString())
        .and('apartado.id', SgiRestFilterOperator.EQUALS, apartadoId.toString())
    };
    return this.find<IRespuestaResponse, IRespuesta>(
      `${this.endpointUrl}`,
      options,
      RESPUESTA_RESPONSE_CONVERTER
    ).pipe(
      map((response) => {
        if (response.items.length > 0) {
          return response.items[0];
        }
        else {
          return;
        }
      })
    );
  }

  findLastByMemoriaId(memoriaId: number): Observable<IRespuesta> {
    return this.get<IRespuestaResponse>(
      `${this.endpointUrl}/${memoriaId}/last`,
    ).pipe(
      map((response) => {
        return RESPUESTA_RESPONSE_CONVERTER.toTarget(response);
      })
    );
  }

  /**
   * Actualiza los datos de la restrospectiva en la memoria con los valores de la
   * respuesta si el formulario es de tipo M20.
   *
   * @param id identificador de la respuesta
   */
  updateDatosRetrospectiva(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/update-datos-retrospectiva`, undefined);
  }

}
