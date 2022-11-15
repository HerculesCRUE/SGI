import { IDocumentacionMemoria } from '@core/models/eti/documentacion-memoria';
import { Fragment } from '@core/services/action-service';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { SgiRestListResult } from '@sgi/framework/http';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { endWith, map, mergeMap, switchMap, takeLast } from 'rxjs/operators';

export enum TIPO_DOCUMENTACION {
  INICIAL = 0,
  SEGUIMIENTO_ANUAL = 1,
  SEGUIMIENTO_FINAL = 2,
  RETROSPECTIVA = 4
}

export class MemoriaDocumentacionFragment extends Fragment {

  readonly documentacionesMemoria$ = new BehaviorSubject<StatusWrapper<IDocumentacionMemoria>[]>([]);
  readonly documentacionesSeguimientoAnual$ = new BehaviorSubject<StatusWrapper<IDocumentacionMemoria>[]>([]);
  readonly documentacionesSeguimientoFinal$ = new BehaviorSubject<StatusWrapper<IDocumentacionMemoria>[]>([]);
  readonly documentacionesRetrospectiva$ = new BehaviorSubject<StatusWrapper<IDocumentacionMemoria>[]>([]);

  private deletedDocumentacion = new Map<TIPO_DOCUMENTACION, IDocumentacionMemoria[]>();

  constructor(
    key: number,
    private service: MemoriaService,
    private documentoService: DocumentoService
  ) {
    super(key);
    const values = Object.values(TIPO_DOCUMENTACION);
    values.filter(value => typeof value !== 'string').forEach(value => this.deletedDocumentacion.set(value as TIPO_DOCUMENTACION, []));
  }

  onInitialize(): void {
    if (this.getKey()) {
      this.subscriptions.push(
        merge(
          this.loadDocumentos(TIPO_DOCUMENTACION.INICIAL),
          this.loadDocumentos(TIPO_DOCUMENTACION.SEGUIMIENTO_ANUAL),
          this.loadDocumentos(TIPO_DOCUMENTACION.SEGUIMIENTO_FINAL),
          this.loadDocumentos(TIPO_DOCUMENTACION.RETROSPECTIVA),
        ).subscribe()
      );
    }
  }

  private getLoadService(tipoDocumentacion: TIPO_DOCUMENTACION): Observable<SgiRestListResult<IDocumentacionMemoria>> {
    switch (tipoDocumentacion) {
      case TIPO_DOCUMENTACION.INICIAL:
        return this.service.findDocumentacionFormulario(this.getKey() as number);
      case TIPO_DOCUMENTACION.SEGUIMIENTO_ANUAL:
        return this.service.findDocumentacionSeguimientoAnual(this.getKey() as number);
      case TIPO_DOCUMENTACION.SEGUIMIENTO_FINAL:
        return this.service.findDocumentacionSeguimientoFinal(this.getKey() as number);
      case TIPO_DOCUMENTACION.RETROSPECTIVA:
        return this.service.findDocumentacionRetrospectiva(this.getKey() as number);
    }
  }

  private loadDocumentos(tipoDocumentacion: TIPO_DOCUMENTACION): Observable<void> {
    return this.getLoadService(tipoDocumentacion).pipe(
      map((response) => {
        const documentaciones = response.items.map(documentacion => new StatusWrapper<IDocumentacionMemoria>(documentacion));
        this.getListByTipoDocumentacion(tipoDocumentacion).next(documentaciones);
        return;
      })
    );
  }

  saveOrUpdate(): Observable<void> {
    this.setChanges(false);
    return merge(
      this.createDocumentacion(TIPO_DOCUMENTACION.INICIAL),
      this.deleteDocumentacion(TIPO_DOCUMENTACION.INICIAL),
      this.createDocumentacion(TIPO_DOCUMENTACION.SEGUIMIENTO_ANUAL),
      this.deleteDocumentacion(TIPO_DOCUMENTACION.SEGUIMIENTO_ANUAL),
      this.createDocumentacion(TIPO_DOCUMENTACION.SEGUIMIENTO_FINAL),
      this.deleteDocumentacion(TIPO_DOCUMENTACION.SEGUIMIENTO_FINAL),
      this.createDocumentacion(TIPO_DOCUMENTACION.RETROSPECTIVA),
      this.deleteDocumentacion(TIPO_DOCUMENTACION.RETROSPECTIVA)
    ).pipe(
      takeLast(1)
    );
  }

  private deleteDocumentacion(tipoDocumentacion: TIPO_DOCUMENTACION): Observable<void> {
    let docs = this.deletedDocumentacion.get(tipoDocumentacion);
    if (!!!docs?.length) {
      return of(void 0);
    }
    return from(docs).pipe(
      mergeMap(doc => {
        return this.getDeleteService(tipoDocumentacion, doc.memoria.id, doc.id)
          .pipe(
            switchMap(() => {
              docs = docs.filter(deleted => deleted.id !== doc.id);
              this.deletedDocumentacion.set(tipoDocumentacion, docs);
              return this.documentoService.eliminarFichero(doc.documento.documentoRef);
            })
          );
      }));
  }

  private getDeleteService(tipoDocumentacion: TIPO_DOCUMENTACION, memoriaId: number, documentacionMemoriaId: number): Observable<void> {
    switch (tipoDocumentacion) {
      case TIPO_DOCUMENTACION.INICIAL:
        return this.service.deleteDocumentacionInicial(memoriaId, documentacionMemoriaId);
      case TIPO_DOCUMENTACION.SEGUIMIENTO_ANUAL:
        return this.service.deleteDocumentacionSeguimientoAnual(memoriaId, documentacionMemoriaId);
      case TIPO_DOCUMENTACION.SEGUIMIENTO_FINAL:
        return this.service.deleteDocumentacionSeguimientoFinal(memoriaId, documentacionMemoriaId);
      case TIPO_DOCUMENTACION.RETROSPECTIVA:
        return this.service.deleteDocumentacionRetrospectiva(memoriaId, documentacionMemoriaId);
    }
  }

  private getCreateService(tipoDocumentacion: TIPO_DOCUMENTACION, documentacionMemoria: IDocumentacionMemoria)
    : Observable<IDocumentacionMemoria> {
    switch (tipoDocumentacion) {
      case TIPO_DOCUMENTACION.INICIAL:
        return this.service.createDocumentacionInicial(this.getKey() as number, documentacionMemoria);
      case TIPO_DOCUMENTACION.SEGUIMIENTO_ANUAL:
        return this.service.createDocumentacionSeguimientoAnual(this.getKey() as number, documentacionMemoria);
      case TIPO_DOCUMENTACION.SEGUIMIENTO_FINAL:
        return this.service.createDocumentacionSeguimientoFinal(this.getKey() as number, documentacionMemoria);
      case TIPO_DOCUMENTACION.RETROSPECTIVA:
        return this.service.createDocumentacionRetrospectiva(this.getKey() as number, documentacionMemoria);
    }
  }

  private createDocumentacion(tipoDocumentacion: TIPO_DOCUMENTACION): Observable<void> {
    const data$ = this.getListByTipoDocumentacion(tipoDocumentacion);
    const documentacionCreada = data$.value.filter((documentacion) => documentacion.created);
    if (documentacionCreada.length === 0) {
      return of(void 0);
    }
    return from(documentacionCreada).pipe(
      mergeMap((wrappedDocumentacion) => {
        return this.getCreateService(tipoDocumentacion, wrappedDocumentacion.value).pipe(
          map((savedDocumentacion) => {
            const index = data$.value.findIndex((currentDocumentacion) => currentDocumentacion === wrappedDocumentacion);
            data$.value.forEach((currentDocumentacion) => {
              if (currentDocumentacion === wrappedDocumentacion) {
                currentDocumentacion.setEdited();
                currentDocumentacion.value.id = savedDocumentacion.id;
              }
            });
            wrappedDocumentacion.value.id = savedDocumentacion.id;
            this.documentacionesMemoria$.value[index] = new StatusWrapper<IDocumentacionMemoria>(wrappedDocumentacion.value);
            this.documentacionesMemoria$.next(this.documentacionesMemoria$.value);
          })
        );
      }),
      endWith()
    );
  }

  private getListByTipoDocumentacion(tipoDocumentacion: TIPO_DOCUMENTACION): BehaviorSubject<StatusWrapper<IDocumentacionMemoria>[]> {
    switch (tipoDocumentacion) {
      case TIPO_DOCUMENTACION.INICIAL:
        return this.documentacionesMemoria$;
      case TIPO_DOCUMENTACION.SEGUIMIENTO_ANUAL:
        return this.documentacionesSeguimientoAnual$;
      case TIPO_DOCUMENTACION.SEGUIMIENTO_FINAL:
        return this.documentacionesSeguimientoFinal$;
      case TIPO_DOCUMENTACION.RETROSPECTIVA:
        return this.documentacionesRetrospectiva$;
    }
  }

  addDocumento(tipoDocumentacion: TIPO_DOCUMENTACION, documentacion: IDocumentacionMemoria,) {
    const data$ = this.getListByTipoDocumentacion(tipoDocumentacion);

    const wrapped = new StatusWrapper<IDocumentacionMemoria>(documentacion);
    wrapped.setCreated();

    const current = data$.value;
    current.push(wrapped);
    data$.next(current);

    this.setChanges(true);
    this.setErrors(false);
  }

  deleteDocumento(tipoDocumentacion: TIPO_DOCUMENTACION, wrapperDocumentacion: StatusWrapper<IDocumentacionMemoria>): void {
    const data$ = this.getListByTipoDocumentacion(tipoDocumentacion);
    const current = data$.value;
    const index = current.findIndex((value) => value === wrapperDocumentacion);
    if (index >= 0) {
      if (!wrapperDocumentacion.created) {
        current[index].setDeleted();
        this.deletedDocumentacion.get(tipoDocumentacion).push(current[index].value);
      }
      current.splice(index, 1);
      data$.next(current);
      this.setChanges(true);
    }
  }
}
