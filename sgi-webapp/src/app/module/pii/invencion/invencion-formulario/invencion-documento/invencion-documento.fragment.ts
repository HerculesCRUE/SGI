import { IInvencionDocumento } from '@core/models/pii/invencion-documento';
import { Fragment } from '@core/services/action-service';
import { InvencionDocumentoService } from '@core/services/pii/invencion/invencion-documento/invencion-documento.service';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { SgiRestListResult } from '@sgi/framework/http';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export class InvencionDocumentoFragment extends Fragment {

  public invencionDocumentos$ = new BehaviorSubject<StatusWrapper<IInvencionDocumento>[]>([]);
  private deletedInvencionDocumentos: StatusWrapper<IInvencionDocumento>[] = [];
  private needSaveActionDocumentos: number[] = [];

  constructor(
    key: number,
    private invencionService: InvencionService,
    protected invencionDocumentoService: InvencionDocumentoService,
    private documentoService: DocumentoService) {

    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    const id = this.getKey() as number;

    if (id) {
      this.subscriptions.push(
        this.invencionService.findAllInvencionDocumentos(id)
          .pipe(
            map((invencionDocumentos: SgiRestListResult<IInvencionDocumento>) => invencionDocumentos.items.map((invencionDocumento) => {
              this.resolveDocumentoInfo(invencionDocumento);
              return new StatusWrapper<IInvencionDocumento>(invencionDocumento);
            })))
          .subscribe(result => this.invencionDocumentos$.next(result)));
    }
  }

  private resolveDocumentoInfo(invencionDocumento: IInvencionDocumento): void {
    if (invencionDocumento.documento?.documentoRef) {
      this.subscriptions.push(
        this.documentoService.getInfoFichero(invencionDocumento.documento.documentoRef).pipe(
          map(docInfo => invencionDocumento.documento = docInfo)
        ).subscribe());
    }
  }

  saveOrUpdate(): Observable<void> {

    return merge(
      this.deleteInvencionDocumentos(),
      this.createOrUpdateDocumento()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.deletedInvencionDocumentos.length === 0) {
          this.setChanges(false);
        }
      })
    );
  }

  public deleteInvencionDocumento(wrapper: StatusWrapper<IInvencionDocumento>, row: number): void {

    const current = this.invencionDocumentos$.value;
    const index = current.findIndex((value) => value === wrapper);

    if (index >= 0) {
      if (!wrapper.created) {
        this.deletedInvencionDocumentos.push(current[index]);
        this.removeDeletedInvencionDocumentoFromArray(index, current);
        this.checkIfNeededSaveChanges(wrapper, row);
      } else {
        this.documentoService.eliminarFichero(wrapper.value.documento.documentoRef).subscribe(() => {
          this.removeDeletedInvencionDocumentoFromArray(index, current);
          this.checkIfNeededSaveChanges(wrapper, row);
        });
      }
    }
  }

  private removeDeletedInvencionDocumentoFromArray(index: number, currentInvencionDocumentos: StatusWrapper<IInvencionDocumento>[]): void {
    currentInvencionDocumentos.splice(index, 1);
    this.invencionDocumentos$.next(currentInvencionDocumentos);
    this.setChanges(true);
  }

  private checkIfNeededSaveChanges(enclosingDocument: StatusWrapper<IInvencionDocumento>, row: number): void {
    if (enclosingDocument.created && row > 0) {
      const foundedIndex = this.needSaveActionDocumentos.findIndex(element => element === row);
      if (foundedIndex >= 0) {
        this.needSaveActionDocumentos.splice(foundedIndex, 1);
      }
      const needUpdateDoc = this.invencionDocumentos$.value.find(documento => documento.created || documento.edited);
      if (this.needSaveActionDocumentos.length === 0 && needUpdateDoc === undefined) {
        this.setChanges(false);
      }
    }
  }

  private deleteInvencionDocumentos(): Observable<void> {
    if (this.deletedInvencionDocumentos.length === 0) {
      return of(void 0);
    }

    return from(this.deletedInvencionDocumentos).pipe(
      mergeMap((wrapped) => {
        return this.invencionDocumentoService.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.deletedInvencionDocumentos = this.deletedInvencionDocumentos.filter(deletedInvencionDocumento =>
                deletedInvencionDocumento.value.id !== wrapped.value.id);
            }),
            switchMap(() => this.documentoService.eliminarFichero(wrapped.value.documento.documentoRef)),
            takeLast(1)
          );
      }));
  }

  private createOrUpdateDocumento(): Observable<void> {
    return from(this.invencionDocumentos$.value).pipe(
      mergeMap(wrappedDoc => {
        return this.executeCreateOrUpdateService(wrappedDoc).pipe(
          map(savedDocumento => {
            wrappedDoc = new StatusWrapper<IInvencionDocumento>(savedDocumento);
          })
        );
      }));

  }

  private executeCreateOrUpdateService(wrappedDoc: StatusWrapper<IInvencionDocumento>): Observable<IInvencionDocumento> {
    if (!wrappedDoc.created && !wrappedDoc.edited) {
      return of(void 0);
    }
    return wrappedDoc.edited ? this.invencionDocumentoService.update(wrappedDoc.value.id, wrappedDoc.value)
      : this.invencionDocumentoService.create(wrappedDoc.value);
  }

  public updateTable(savedDocumento: IInvencionDocumento, invencionDocumento: IInvencionDocumento, row?: number): void {

    const current = this.invencionDocumentos$.value;
    if (!invencionDocumento) {
      this.subscriptions.push(
        this.documentoService.getInfoFichero(savedDocumento.documento.documentoRef).pipe(
          map(docInfo => savedDocumento.documento = docInfo)
        ).subscribe()
      );
      const created = new StatusWrapper(savedDocumento);
      created.setCreated();
      current.push(created);
      this.invencionDocumentos$.next(current);
      // Cuando un documento se crea nuevo, se informa en un array por si se elimina antes de guardarlo
      // así si es el caso, el botón de guardar se volverá a deshabilitar cuando se elimine el documento
      this.needSaveActionDocumentos.push(current.length - 1);
    } else if (!savedDocumento.id && row > 0) {
      const documentoWrapper: StatusWrapper<IInvencionDocumento> = this.invencionDocumentos$.value[row];
      documentoWrapper.value.nombre = savedDocumento.nombre;
    } else {
      const updatedDocuments = current.map(doc => {
        if (doc.value.id === invencionDocumento?.id) {
          doc.value.nombre = savedDocumento.nombre;
          doc.setEdited();
        }
        return doc;
      });
      this.invencionDocumentos$.next(updatedDocuments);
    }
  }
}
