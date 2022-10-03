import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { DOCUMENTO_CONVERTER } from '@core/converters/sgdoc/documento.converter';
import { IDocumentoBackend } from '@core/models/sgdoc/backend/documento-backend';
import { IDocumento } from '@core/models/sgdoc/documento';
import { environment } from '@env';
import { FindByIdCtor, mixinFindById, SgiRestBaseService } from '@sgi/framework/http/';
import { Observable } from 'rxjs';

export function triggerDownloadToUser(file: Blob, fileName: string) {
  const downloadLink = document.createElement('a');
  const href = window.URL.createObjectURL(file);
  downloadLink.href = href;
  downloadLink.download = fileName;
  document.body.appendChild(downloadLink);
  downloadLink.click();
  document.body.removeChild(downloadLink);
  window.URL.revokeObjectURL(href);
}

// tslint:disable-next-line: variable-name
const _DocumentoMixinBase:
  FindByIdCtor<string, IDocumento, IDocumentoBackend> &
  typeof SgiRestBaseService = mixinFindById(
    SgiRestBaseService,
    DOCUMENTO_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class DocumentoPublicService extends _DocumentoMixinBase {
  private static readonly MAPPING = '/documentos';
  private static readonly PUBLIC_PREFIX = '/public';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sgdoc}${DocumentoPublicService.PUBLIC_PREFIX}${DocumentoPublicService.MAPPING}`,
      http
    );
  }

  /**
   * Recupera la información del documento.
   * @param documentoRef referencia del documento.
   */
  getInfoFichero(documentoRef: string): Observable<IDocumento> {
    return this.findById(documentoRef);
  }

  /**
   * Descarga el fichero.
   * @param documentoRef referencia del documento.
   */
  downloadFichero(documentoRef: string): Observable<Blob> {
    return this.http.get(`${this.endpointUrl}/${documentoRef}/archivo`, {
      headers: new HttpHeaders().set('Accept', 'application/octet-stream'), responseType: 'blob'
    });
  }

}
