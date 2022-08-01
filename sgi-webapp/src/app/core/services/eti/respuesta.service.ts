import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { RESPUESTA_CONVERTER } from '@core/converters/eti/respuesta.converter';
import { IRespuestaBackend } from '@core/models/eti/backend/respuesta-backend';
import { IRespuesta } from '@core/models/eti/respuesta';
import { environment } from '@env';
import { RSQLSgiRestFilter, SgiMutableRestService, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class RespuestaService extends SgiMutableRestService<number, IRespuestaBackend, IRespuesta>{
  private static readonly MAPPING = '/respuestas';

  constructor(protected http: HttpClient) {
    super(
      RespuestaService.name,
      `${environment.serviceServers.eti}${RespuestaService.MAPPING}`,
      http,
      RESPUESTA_CONVERTER
    );
  }

  findByMemoriaIdAndApartadoId(memoriaId: number, apartadoId: number): Observable<IRespuesta> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('memoria.id', SgiRestFilterOperator.EQUALS, memoriaId.toString())
        .and('apartado.id', SgiRestFilterOperator.EQUALS, apartadoId.toString())
    };
    return this.find<IRespuestaBackend, IRespuesta>(
      `${this.endpointUrl}`,
      options,
      RESPUESTA_CONVERTER
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
    return this.http.get<IRespuestaBackend>(
      `${this.endpointUrl}/${memoriaId}/last`,
    ).pipe(
      map((response) => {
        return RESPUESTA_CONVERTER.toTarget(response);
      })
    );
  }

}
