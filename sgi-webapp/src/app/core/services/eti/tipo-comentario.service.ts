import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TipoComentario } from '@core/models/eti/tipo-comentario';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class TipoComentarioService extends SgiRestService<number, TipoComentario> {
  private static readonly MAPPING = '/tipocomentarios';

  constructor(protected http: HttpClient) {
    super(
      TipoComentarioService.name,
      `${environment.serviceServers.eti}${TipoComentarioService.MAPPING}`,
      http
    );
  }
}
