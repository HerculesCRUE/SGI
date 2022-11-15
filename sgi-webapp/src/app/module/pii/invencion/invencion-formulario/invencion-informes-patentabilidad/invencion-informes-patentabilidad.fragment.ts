import { IInformePatentabilidad } from '@core/models/pii/informe-patentabilidad';
import { IInvencion } from '@core/models/pii/invencion';
import { Fragment } from '@core/services/action-service';
import { InformePatentabilidadService } from '@core/services/pii/informe-patentabilidad/informe-patentabilidad.service';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { catchError, map, mergeMap, takeLast, tap } from 'rxjs/operators';

export class InvencionInformesPatentabilidadFragment extends Fragment {

  public informesPatentabilidad$ = new BehaviorSubject<StatusWrapper<IInformePatentabilidad>[]>([]);
  private informesPatentabilidadToDelete: StatusWrapper<IInformePatentabilidad>[] = [];
  private documentosRefUnrelated: string[] = [];

  constructor(
    key: number,
    private readonly isEditPerm: boolean,
    private readonly invencionService: InvencionService,
    private readonly informePatentabilidadService: InformePatentabilidadService,
    private readonly documentoService: DocumentoService,
    private readonly empresaService: EmpresaService,
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void | Observable<any> {
    const id = this.getKey() as number;

    if (id) {
      return this.invencionService.findInformesPatentabilidad(id)
        .pipe(
          map((informesPatentabilidad: IInformePatentabilidad[]) => informesPatentabilidad.map((informePatentabilidad) => {
            return new StatusWrapper<IInformePatentabilidad>(informePatentabilidad);
          })),
          tap(informesPatentabilidadWrapped => this.informesPatentabilidad$.next(informesPatentabilidadWrapped)),
          mergeMap(informesPatentabilidadWrapped => this.fillAdditionalData$(informesPatentabilidadWrapped))
        );
    }
  }

  private fillAdditionalData$(wrapperList: StatusWrapper<IInformePatentabilidad>[]): Observable<StatusWrapper<IInformePatentabilidad>> {
    return from(wrapperList).pipe(
      mergeMap(wrapper => this.fillDocumentoInfo$(wrapper)),
      mergeMap(wrapper => this.fillEntidadCreadoraInfo$(wrapper))
    );
  }

  private fillDocumentoInfo$(wrapper: StatusWrapper<IInformePatentabilidad>): Observable<StatusWrapper<IInformePatentabilidad>> {
    return this.documentoService.getInfoFichero(wrapper.value.documento.documentoRef).pipe(
      map((documento) => {
        wrapper.value.documento = documento;
        return wrapper;
      }),
      catchError(() => of(wrapper))
    );
  }

  private fillEntidadCreadoraInfo$(wrapper: StatusWrapper<IInformePatentabilidad>): Observable<StatusWrapper<IInformePatentabilidad>> {
    return this.empresaService.findById(wrapper.value.entidadCreadora.id).pipe(
      map((empresa) => {
        wrapper.value.entidadCreadora = empresa;
        return wrapper;
      }),
      catchError(() => of(wrapper))
    );
  }

  getInformesPatentabilidad$(): Observable<StatusWrapper<IInformePatentabilidad>[]> {
    return this.informesPatentabilidad$.asObservable();
  }

  addInformePatentabilidad(informePatentabilidad: IInformePatentabilidad): void {
    informePatentabilidad.invencion = { id: +this.getKey() } as IInvencion;
    const wrapped = new StatusWrapper<IInformePatentabilidad>(informePatentabilidad);
    wrapped.setCreated();
    const current = this.informesPatentabilidad$.value;
    current.push(wrapped);
    this.informesPatentabilidad$.next(current);
    this.setChanges(true);
  }

  /**
   * Actualiza el informe de patentabilidad y si se modifica el documento asociado lo anade a la lista de documentos a eliminar
   * y si no esta persistido aun se elimina directamente.
   */
  updateInformePatentabilidad(updatedInformePatentabilidad: IInformePatentabilidad, previousDocumentoRef: string, index: number) {
    if (index >= 0) {
      const current = this.informesPatentabilidad$.value;
      const wrapper = current[index];

      if (!wrapper.created) {
        wrapper.setEdited();
      }

      if (!!previousDocumentoRef && updatedInformePatentabilidad.documento.documentoRef !== previousDocumentoRef) {
        if (wrapper.created) {
          this.documentoService.eliminarFichero(wrapper.value.documento.documentoRef).subscribe();
        } else {
          this.documentosRefUnrelated.push(previousDocumentoRef);
        }
      }

      this.informesPatentabilidad$.next(current);
      this.setChanges(true);
    }
  }

  /**
   * Si el informe de patentabilidad ya esta creado lo anade a la lista de elementos a eliminar
   * y si no esta persistido aun se elimina directamente el documento asociado.
   */
  deleteInformePatentabilidad(wrapper: StatusWrapper<IInformePatentabilidad>): void {
    let deleteDocumento$ = of(void 0);
    const current = this.informesPatentabilidad$.value;
    const index = current.findIndex(value => value === wrapper);

    if (index >= 0) {
      if (wrapper.created) {
        deleteDocumento$ = this.documentoService.eliminarFichero(wrapper.value.documento.documentoRef);
      } else {
        this.informesPatentabilidadToDelete.push(current[index]);
      }

      deleteDocumento$.subscribe(() =>
        this.removeDeletedInformePatentabilidadFromArray(index, current)
      );
    }
  }

  private removeDeletedInformePatentabilidadFromArray(
    index: number,
    currentInformesPatentabilidad: StatusWrapper<IInformePatentabilidad>[]
  ): void {
    currentInformesPatentabilidad.splice(index, 1);
    this.informesPatentabilidad$.next(currentInformesPatentabilidad);
    this.setChanges(this.hasFragmentChangesPending());
  }

  private hasFragmentChangesPending() {
    return this.informesPatentabilidadToDelete.length > 0
      || this.informesPatentabilidad$.value.some((value) => value.created || value.edited);
  }

  hasEditPerm(): boolean {
    return this.isEditPerm;
  }

  saveOrUpdate(): Observable<string | number | void> {
    return merge(
      this.deleteInformesPatentabilidad(),
      this.updateInformesPatentabilidad(),
      this.createInformesPatentabilidad(),
      this.deleteDocumentosUnrelated()
    ).pipe(
      takeLast(1),
      tap(() => {
        this.setChanges(this.hasFragmentChangesPending());
      })
    );
  }

  private deleteInformesPatentabilidad(): Observable<void> {
    if (this.informesPatentabilidadToDelete.length === 0) {
      return of(void 0);
    }

    return from(this.informesPatentabilidadToDelete).pipe(
      mergeMap(wrapped =>
        merge(
          this.deleteInformePatentabilidadById(wrapped),
          this.deleteInformePatentabilidadRelatedDocumento(wrapped.value.documento.documentoRef)
        )
      ),
      takeLast(1)
    );
  }

  private deleteDocumentosUnrelated(): Observable<void> {
    if (this.documentosRefUnrelated.length === 0) {
      return of(void 0);
    }

    return from(this.documentosRefUnrelated).pipe(
      mergeMap(documentoRef =>
        this.documentoService.eliminarFichero(documentoRef)
          .pipe(
            tap(() =>
              this.documentosRefUnrelated = this.documentosRefUnrelated
                .filter(documentoRefEliminado => documentoRefEliminado !== documentoRef)
            )
          )
      ),
      takeLast(1)
    );
  }

  private deleteInformePatentabilidadById(wrapped: StatusWrapper<IInformePatentabilidad>): Observable<void> {
    return this.informePatentabilidadService.deleteById(wrapped.value).pipe(
      tap(() =>
        this.informesPatentabilidadToDelete = this.informesPatentabilidadToDelete.filter(entidadEliminada =>
          entidadEliminada.value.id !== wrapped.value.id
        ),
        catchError(() => of(void 0))
      )
    );
  }

  private deleteInformePatentabilidadRelatedDocumento(documentoRef: string): Observable<void> {
    return this.documentoService.eliminarFichero(documentoRef).pipe(
      catchError(() => of(void 0))
    );
  }

  private updateInformesPatentabilidad(): Observable<void> {
    const current = this.informesPatentabilidad$.value;
    return from(current.filter(wrapper => wrapper.edited)).pipe(
      mergeMap(wrapper => {
        return this.informePatentabilidadService.update(wrapper.value.id, wrapper.value).pipe(
          map((informePatentabilidadResponse) => this.refreshInformesPatentabilidadData(informePatentabilidadResponse, wrapper, current)),
          catchError(() => of(void 0))
        );
      })
    );
  }

  private createInformesPatentabilidad(): Observable<void> {
    const current = this.informesPatentabilidad$.value;
    return from(current.filter(wrapper => wrapper.created)).pipe(
      mergeMap((wrapper => {
        return this.informePatentabilidadService.create(wrapper.value).pipe(
          map((informePatentabilidadResponse) => this.refreshInformesPatentabilidadData(informePatentabilidadResponse, wrapper, current)),
          catchError(() => of(void 0))
        );
      }))
    );
  }

  private refreshInformesPatentabilidadData(
    informePatentabilidadResponse: IInformePatentabilidad,
    wrapper: StatusWrapper<IInformePatentabilidad>,
    current: StatusWrapper<IInformePatentabilidad>[]
  ): void {
    this.copyRelatedAttributes(wrapper.value, informePatentabilidadResponse);
    current[current.findIndex(c => c === wrapper)] = new StatusWrapper<IInformePatentabilidad>(informePatentabilidadResponse);
    this.informesPatentabilidad$.next(current);
  }

  private copyRelatedAttributes(
    source: IInformePatentabilidad,
    target: IInformePatentabilidad
  ): void {
    target.invencion = source.invencion;
    target.resultadoInformePatentabilidad = source.resultadoInformePatentabilidad;
    target.documento = source.documento;
    target.entidadCreadora = source.entidadCreadora;
  }

}
