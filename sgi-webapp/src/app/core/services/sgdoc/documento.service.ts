import { HttpClient, HttpEvent, HttpEventType, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { DOCUMENTO_CONVERTER } from '@core/converters/sgdoc/documento.converter';
import { IDocumentoBackend } from '@core/models/sgdoc/backend/documento-backend';
import { IDocumento } from '@core/models/sgdoc/documento';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http/';
import { Observable, throwError } from 'rxjs';
import { catchError, map, takeLast } from 'rxjs/operators';

export interface FileModel {
  file: File;
  progress: number;
  status: 'attached' | 'uploading' | 'complete' | 'error';
  document?: IDocumento;
}

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

@Injectable({
  providedIn: 'root'
})
export class DocumentoService extends SgiMutableRestService<string, IDocumentoBackend, IDocumento>{
  private static readonly MAPPING = '/documentos';

  constructor(protected http: HttpClient) {
    super(
      DocumentoService.name,
      `${environment.serviceServers.sgdoc}${DocumentoService.MAPPING}`,
      http,
      DOCUMENTO_CONVERTER
    );
  }

  /**
   * Crea el fichero en sgdoc.
   * @param fichero Fichero a crear.
   */
  uploadFichero(fileModel: FileModel): Observable<IDocumento> {
    return this.uploadFicheroWithStatus(fileModel.file).pipe(
      map((event: HttpEvent<any>) => {
        switch (event.type) {
          case HttpEventType.Sent:
            fileModel.status = 'uploading';
            break;
          case HttpEventType.UploadProgress:
            fileModel.progress = Math.round(100 * event.loaded / event.total);
            break;
          case HttpEventType.Response:
            fileModel.status = 'complete';
            return DOCUMENTO_CONVERTER.toTarget(event.body);
        }
      }),
      takeLast(1),
      catchError((err) => {
        fileModel.status = 'error';
        return throwError(err);
      })
    );
  }

  uploadFicheroWithStatus(file: File): Observable<HttpEvent<any>> {
    const formData = new FormData();
    formData.append('archivo', file);

    return this.http.post(`${this.endpointUrl}`, formData, { observe: 'events', reportProgress: true });
  }

  /**
   * Recupera la información del documento.
   * @param documentoRef referencia del documento.
   */
  getInfoFichero(documentoRef: string): Observable<IDocumento> {
    return this.http.get<IDocumentoBackend>(
      `${this.endpointUrl}/${documentoRef}`
    ).pipe(
      map(response => DOCUMENTO_CONVERTER.toTarget(response))
    );
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


  /**
   * Elimina el documento.
   * @param documentoRef referencia del documento.
   */
  eliminarFichero(documentoRef: string): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${documentoRef}`);
  }
}
