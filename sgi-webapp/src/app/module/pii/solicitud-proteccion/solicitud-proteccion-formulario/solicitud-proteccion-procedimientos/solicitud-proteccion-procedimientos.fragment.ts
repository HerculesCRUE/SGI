import { IProcedimiento } from '@core/models/pii/procedimiento';
import { IProcedimientoDocumento } from '@core/models/pii/procedimiento-documento';
import { ISolicitudProteccion } from '@core/models/pii/solicitud-proteccion';
import { Fragment } from '@core/services/action-service';
import { SolicitudProteccionProcedimientoDocumentoService } from '@core/services/pii/solicitud-proteccion/solicitud-proteccion-procedimiento-documento/solicitud-proteccion-procedimiento-documento.service';
import { SolicitudProteccionProcedimientoService } from '@core/services/pii/solicitud-proteccion/solicitud-proteccion-procedimiento/solicitud-proteccion-procedimiento.service';
import { SolicitudProteccionService } from '@core/services/pii/solicitud-proteccion/solicitud-proteccion.service';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, forkJoin, from, merge, Observable, of } from 'rxjs';
import { catchError, map, mergeMap, shareReplay, switchMap, takeLast, tap } from 'rxjs/operators';

export class SolicitudProteccionProcedimientosFragment extends Fragment {

  private procedimientoDocumentos = new BehaviorSubject<StatusWrapper<IProcedimientoDocumento>[]>([]);
  private procedimientoDocumentosShared = this.procedimientoDocumentos.pipe(shareReplay(1));
  private procedimientoDocumentosToDelete: StatusWrapper<IProcedimientoDocumento>[] = [];

  private procedimientos = new BehaviorSubject<StatusWrapper<IProcedimiento>[]>([]);
  private procedimientosShared = this.procedimientos.pipe(shareReplay(1));
  private procedimientoSelected = new BehaviorSubject<StatusWrapper<IProcedimiento>>(null);
  private procedimientosToDelete: StatusWrapper<IProcedimiento>[] = [];

  get procedimientos$(): Observable<StatusWrapper<IProcedimiento>[]> {
    return this.procedimientosShared;
  }

  get procedimientoSelected$(): Observable<StatusWrapper<IProcedimiento>> {
    return this.procedimientoSelected.asObservable();
  }

  get procedimientoDocumentos$(): Observable<StatusWrapper<IProcedimientoDocumento>[]> {
    return this.procedimientoDocumentosShared;
  }

  constructor(
    key: number,
    private solicitudProteccionService: SolicitudProteccionService,
    private solicitudProteccionProcedimientoService: SolicitudProteccionProcedimientoService,
    private procedimientoDocumentoService: SolicitudProteccionProcedimientoDocumentoService,
    private documentoService: DocumentoService,
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void | Observable<any> {
    this.subscriptions.push(
      this.procedimientoSelected$.pipe(
        switchMap(elem => {
          if (!elem?.value?.id) {
            return of([] as StatusWrapper<IProcedimientoDocumento>[]);
          }
          return this.solicitudProteccionProcedimientoService
            .findProcedimientoDocumentosBySolicitudId(elem.value.id).pipe(
              map(listaProcedimientoDocumentos =>
                listaProcedimientoDocumentos
                  .items
                  .filter(
                    documento => !this.procedimientoDocumentosToDelete.some((docToDelete) => docToDelete.value.id === documento.id)
                  )
                  .map(item => {
                    this.resolveDocumentoInfo(item);
                    return new StatusWrapper<IProcedimientoDocumento>(item);
                  })),
              catchError((error) => {
                console.log(error);
                return of([]);
              }),
            );
        }),
      ).subscribe(
        (procedimientoDocumentos) => {
          this.procedimientoDocumentos.next(procedimientoDocumentos);
        }
      )
    );

    this.subscriptions.push(
      this.procedimientos$
        .subscribe(
          elems => {
            if (!elems.includes(this.procedimientoSelected.value)) {
              this.procedimientoSelected.next(elems.length ? elems[0] : null);
            }
          }
        )
    );

    this.subscriptions.push(
      this.solicitudProteccionService.findProcedimientosBySolicitudId(this.getKey() as number).subscribe(
        procedimientos => this.procedimientos.next(procedimientos.items.map(elem => new StatusWrapper(elem)))
      )
    );
  }

  saveOrUpdate(): Observable<string | number | void> {
    return merge(
      this.deleteDocumentos(),
      this.updateDocumentos(),
      this.addDocumentos(),
      this.deleteProcedimientos(),
      this.updateProcedimientos(),
      this.addProcedimientos()
    ).pipe(
      takeLast(1),
      tap(() => {
        this.checkChanges();
      })
    );
  }

  public canAddDocumento(): boolean {
    return this.procedimientoSelected.value && !this.procedimientoSelected.value.created;
  }

  public setSelectedProcedimiento(elem: StatusWrapper<IProcedimiento>) {
    if (elem.created) {
      this.procedimientoSelected.next(null);
      return;
    }
    this.procedimientoSelected.next(elem);
  }

  public addProcedimientoDocumento(addedElem: StatusWrapper<IProcedimientoDocumento>): void {
    if (!this.canAddDocumento()) {
      return;
    }
    addedElem.setCreated();
    addedElem.value.procedimiento = this.procedimientoSelected.value.value;
    const current = this.procedimientoDocumentos.getValue();
    if (current.indexOf(addedElem) === -1) {
      current.push(addedElem);
      this.procedimientoDocumentos.next(current);
    }
    this.checkChanges();
  }

  public deleteProcedimientoDocumento(elemToDelete: StatusWrapper<IProcedimientoDocumento>): void {
    const current = this.procedimientoDocumentos.value;
    const index = current.findIndex((value) => value === elemToDelete);
    if (index >= 0) {
      current.splice(index, 1);
      this.procedimientoDocumentos.next(current);
      elemToDelete.setDeleted();
    }
    this.procedimientoDocumentosToDelete.push(elemToDelete);
    this.checkChanges();
  }

  public editProcedimientoDocumento(elemToEdit: StatusWrapper<IProcedimientoDocumento>): void {
    elemToEdit.setEdited();
    this.checkChanges();
  }

  public addProcedimiento(addedElem: StatusWrapper<IProcedimiento>): void {
    addedElem.setCreated();
    addedElem.value.solicitudProteccion = { id: this.getKey() as number } as ISolicitudProteccion;
    const current = this.procedimientos.getValue();
    if (current.indexOf(addedElem) === -1) {
      current.push(addedElem);
      this.procedimientos.next(current);
    }
    this.checkChanges();
  }

  public editProcedimiento(procedimientoToEdit: StatusWrapper<IProcedimiento>): void {
    this.checkChanges();
  }

  public deleteProcedimiento(procedimientoToDelete: StatusWrapper<IProcedimiento>): void {
    const current = this.procedimientos.value;
    const index = current.findIndex((value) => value === procedimientoToDelete);
    if (index >= 0) {
      current.splice(index, 1);
      this.procedimientos.next(current);
      if (procedimientoToDelete.value?.id) {
        procedimientoToDelete.setDeleted();
        this.procedimientosToDelete.push(procedimientoToDelete);
      }
      this.checkChanges();
    }
  }

  public createEmptyProcedimiento(): StatusWrapper<IProcedimiento> {
    const emptyElem = new StatusWrapper<IProcedimiento>({} as IProcedimiento);
    emptyElem.setCreated();

    return emptyElem;
  }

  public createEmptyProcedimientoDocumento(): StatusWrapper<IProcedimientoDocumento> {
    const emptyElem = new StatusWrapper<IProcedimientoDocumento>({} as IProcedimientoDocumento);
    emptyElem.value.procedimiento = this.procedimientoSelected.value.value;
    emptyElem.setCreated();

    return emptyElem;
  }

  private checkChanges() {
    this.setChanges(
      this.procedimientosToDelete.length > 0 ||
      this.procedimientos.value.some(elem => elem.touched) ||
      this.procedimientoDocumentosToDelete.length > 0 ||
      this.procedimientoDocumentos.value.some(elem => elem.touched)
    );
  }

  private deleteDocumentos() {
    if (this.procedimientoDocumentosToDelete.length === 0) {
      return of(void 0);
    }
    return from(this.procedimientoDocumentosToDelete).pipe(
      mergeMap((wrapped) => {
        return forkJoin([
          wrapped.value?.id ? this.procedimientoDocumentoService.deleteById(wrapped.value?.id as number) : of(void 0),
          wrapped.value?.documento?.documentoRef ? this.documentoService.eliminarFichero(wrapped.value?.documento.documentoRef) : of(void 0)
        ]).pipe(
          catchError(() => of(void 0))
        );
      }),
      switchMap(() => {
        this.procedimientoDocumentosToDelete = [];
        return of(void 0);
      })
    );
  }

  private deleteProcedimientos(): Observable<void> {
    if (this.procedimientosToDelete.length === 0) {
      return of(void 0);
    }
    return from(this.procedimientosToDelete).pipe(
      mergeMap((wrapped) => {
        return this.solicitudProteccionProcedimientoService.deleteById(wrapped.value?.id);
      }),
      switchMap(() => {
        this.procedimientosToDelete = [];
        return of(void 0);
      })
    );
  }

  private updateDocumentos(): Observable<void> {
    return from(this.procedimientoDocumentos.value.filter(elem => elem.touched && !elem.created)).pipe(
      mergeMap((wrapped) => {
        return forkJoin([of(wrapped), this.procedimientoDocumentoService.update(wrapped.value?.id, wrapped.value)]);
      }),
      tap(([wrapped, result]) => {
        const newValue = new StatusWrapper<IProcedimientoDocumento>(result);
        newValue.value.documento = wrapped.value.documento;
        this.procedimientoDocumentos.next(
          this.updateExistingValue(wrapped, newValue, this.procedimientoDocumentos.value)
        );
      }),
      switchMap(() => {
        return of(void 0);
      })
    );
  }

  private updateProcedimientos(): Observable<void> {
    return from(this.procedimientos.value.filter(elem => elem.value?.id && elem.touched && !elem.created)).pipe(
      mergeMap((wrapped) => {
        return forkJoin([of(wrapped), this.solicitudProteccionProcedimientoService.update(wrapped.value?.id, wrapped.value)]);
      }),
      tap(([wrapped, result]) => {
        this.procedimientos.next(this.updateExistingValue(wrapped, new StatusWrapper<IProcedimiento>(result), this.procedimientos.value));
      }),
      catchError(() => {
        return of(void 0);
      }),
      switchMap(() => {
        return of(void 0);
      })
    );
  }

  private addDocumentos(): Observable<void> {
    return from(this.procedimientoDocumentos.value.filter(elem => elem.created)).pipe(
      mergeMap((wrapped) => {
        return forkJoin([of(wrapped), this.procedimientoDocumentoService.create(wrapped.value)]);
      }),
      tap(([wrapped, result]) => {
        const newValue = new StatusWrapper<IProcedimientoDocumento>(result);
        newValue.value.documento = wrapped.value.documento;
        this.procedimientoDocumentos.next(
          this.updateExistingValue(wrapped, newValue, this.procedimientoDocumentos.value)
        );
      }),
      switchMap(() => {
        return of(void 0);
      })
    );
  }

  private addProcedimientos(): Observable<void> {
    return from(this.procedimientos.value.filter(elem => elem.created || (elem.edited && !elem.value?.id))).pipe(
      mergeMap((wrapped) => {
        return forkJoin([of(wrapped), this.solicitudProteccionProcedimientoService.create(wrapped.value)]);
      }),
      tap(([wrapped, result]) => {
        this.procedimientos.next(this.updateExistingValue(wrapped, new StatusWrapper<IProcedimiento>(result), this.procedimientos.value));
      }),
      switchMap(() => {
        return of(void 0);
      })
    );
  }

  private updateExistingValue(
    valueToUpdate: any,
    updatedValue: any,
    existingValues: any[]): any[] {
    existingValues[existingValues.findIndex(elem => elem === valueToUpdate)] = updatedValue;
    return existingValues;
  }

  private resolveDocumentoInfo(procedimientoDocumento: IProcedimientoDocumento): void {
    if (procedimientoDocumento.documento?.documentoRef) {
      this.subscriptions.push(
        this.documentoService.getInfoFichero(procedimientoDocumento.documento.documentoRef).pipe(
          map(docInfo => procedimientoDocumento.documento = docInfo)
        ).subscribe());
    }
  }

}
