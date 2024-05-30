import { IDocumentacionConvocatoriaReunion } from '@core/models/eti/documentacion-convocatoria-reunion';
import { Fragment } from '@core/services/action-service';
import { ConvocatoriaReunionService } from '@core/services/eti/convocatoria-reunion.service';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, merge, Observable, of, throwError } from 'rxjs';
import { catchError, endWith, map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export class ConvocatoriaReunionDocumentacionFragment extends Fragment {

  readonly documentacionesConvocatoriaReunion$ = new BehaviorSubject<StatusWrapper<IDocumentacionConvocatoriaReunion>[]>([]);

  private deletedDocumentacion: StatusWrapper<IDocumentacionConvocatoriaReunion>[] = [];

  get isReadonly(): boolean {
    return this.readonly;
  }

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private service: ConvocatoriaReunionService,
    private documentoService: DocumentoService,
    private readonly: boolean
  ) {
    super(key);

    // Para que permita crear convocatorias sin documentación
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      this.service.getDocumentaciones(this.getKey() as number).pipe(
        map((response) =>
          response.items.map((documentacionConvocatoriaReunion => {
            this.resolveDocumentoInfo(documentacionConvocatoriaReunion);
            return documentacionConvocatoriaReunion;
          }))
        )
      ).subscribe((documentacion) => {
        this.documentacionesConvocatoriaReunion$.next(documentacion.map(
          documentaciones => new StatusWrapper<IDocumentacionConvocatoriaReunion>(documentaciones))
        );
      });
    }
  }

  private resolveDocumentoInfo(documentacionConvocatoriaReunion: IDocumentacionConvocatoriaReunion): void {
    if (documentacionConvocatoriaReunion.documento?.documentoRef) {
      this.subscriptions.push(
        this.documentoService.getInfoFichero(documentacionConvocatoriaReunion.documento.documentoRef).pipe(
          map(docInfo => documentacionConvocatoriaReunion.documento = docInfo)
        ).subscribe());
    }
  }

  saveOrUpdate(): Observable<void> {
    this.setChanges(false);
    return merge(
      this.createDocumentacion(),
      this.updateDocumentacion(),
      this.deleteDocumentacion(),
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      }),
      catchError(error => {
        this.setChanges(true);
        return throwError(error);
      })
    );
  }

  private deleteDocumentacion(): Observable<void> {
    if (!!!this.deletedDocumentacion?.length) {
      return of(void 0);
    }
    return from(this.deletedDocumentacion).pipe(
      mergeMap(doc => {
        return this.service.deleteDocumentacion(doc.value.convocatoriaReunion.id, doc.value.id)
          .pipe(
            switchMap(() => {
              this.deletedDocumentacion = this.deletedDocumentacion.filter(deleted => deleted.value.id !== doc.value.id);
              return this.documentoService.eliminarFichero(doc.value.documento.documentoRef);
            })
          );
      }));
  }

  private createDocumentacion(): Observable<void> {
    const documentacionCreada = this.documentacionesConvocatoriaReunion$.value.filter((documentacion) => documentacion.created);
    if (documentacionCreada.length === 0) {
      return of(void 0);
    }
    return from(documentacionCreada).pipe(
      mergeMap((wrappedDocumentacion) => {
        return this.service.createDocumentacion(this.getKey() as number, wrappedDocumentacion.value).pipe(
          map((savedDocumentacion) => {
            const index = this.documentacionesConvocatoriaReunion$.value.findIndex((currentDocumentacion) => currentDocumentacion === wrappedDocumentacion);
            this.documentacionesConvocatoriaReunion$.value.forEach((currentDocumentacion) => {
              if (currentDocumentacion === wrappedDocumentacion) {
                currentDocumentacion.setEdited();
                currentDocumentacion.value.id = savedDocumentacion.id;
              }
            });
            wrappedDocumentacion.value.id = savedDocumentacion.id;
            this.documentacionesConvocatoriaReunion$.value[index] = new StatusWrapper<IDocumentacionConvocatoriaReunion>(wrappedDocumentacion.value);
            this.documentacionesConvocatoriaReunion$.next(this.documentacionesConvocatoriaReunion$.value);
          })
        );
      }),
      endWith()
    );
  }

  private updateDocumentacion(): Observable<void> {
    const documentacionUpdated = this.documentacionesConvocatoriaReunion$.value.filter((documentacion) => documentacion.edited);
    if (documentacionUpdated.length === 0) {
      return of(void 0);
    }
    return from(documentacionUpdated).pipe(
      mergeMap((wrappedDocumentacion) => {
        return this.service.updateDocumentacion(this.getKey() as number, wrappedDocumentacion.value).pipe(
          map((savedDocumentacion) => {
            const index = this.documentacionesConvocatoriaReunion$.value.findIndex((currentDocumentacion) => currentDocumentacion === wrappedDocumentacion);
            this.documentacionesConvocatoriaReunion$.value.forEach((currentDocumentacion) => {
              if (currentDocumentacion === wrappedDocumentacion) {
                currentDocumentacion.setEdited();
                currentDocumentacion.value.id = savedDocumentacion.id;
              }
            });
            wrappedDocumentacion.value.id = savedDocumentacion.id;
            this.documentacionesConvocatoriaReunion$.value[index] = new StatusWrapper<IDocumentacionConvocatoriaReunion>(wrappedDocumentacion.value);
            this.documentacionesConvocatoriaReunion$.next(this.documentacionesConvocatoriaReunion$.value);
          })
        );
      }),
      endWith()
    );
  }

  addDocumento(documentacion: IDocumentacionConvocatoriaReunion) {
    const wrapped = new StatusWrapper<IDocumentacionConvocatoriaReunion>(documentacion);
    wrapped.setCreated();

    const current = this.documentacionesConvocatoriaReunion$.value;
    current.push(wrapped);
    this.documentacionesConvocatoriaReunion$.next(current);

    this.setChanges(true);
    this.setErrors(false);
  }

  updateDocumento(documentacion: IDocumentacionConvocatoriaReunion, wrapper?: StatusWrapper<IDocumentacionConvocatoriaReunion>) {
    const current = this.documentacionesConvocatoriaReunion$.value;
    let index = current.findIndex((value) => value === wrapper);
    if (documentacion?.id) {
      index = current.findIndex((value) => value.value.id === documentacion.id);
    }
    if (index >= 0) {
      if (!current[index].created) {
        current[index].setEdited();
      }
      current[index].value.nombre = documentacion.nombre;
      current[index].value.documento = documentacion.documento;
    }

    this.setChanges(true);
    this.setErrors(false);
  }

  deleteDocumento(wrapperDocumentacion: StatusWrapper<IDocumentacionConvocatoriaReunion>): void {
    const current = this.documentacionesConvocatoriaReunion$.value;
    const index = current.findIndex((value) => value === wrapperDocumentacion);
    if (index >= 0) {
      if (!wrapperDocumentacion.created) {
        current[index].setDeleted();
        this.deletedDocumentacion.push(current[index]);
      }
      current.splice(index, 1);
      this.documentacionesConvocatoriaReunion$.next(current);
      this.setChanges(true);
    }
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.documentacionesConvocatoriaReunion$.value.some((wrapper) => wrapper.touched);
    if (this.deletedDocumentacion.length > 0 || touched) {
      return false;
    }
    return true;
  }
}
