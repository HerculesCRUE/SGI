import { IInvencion } from '@core/models/pii/invencion';
import { IInvencionInventor } from '@core/models/pii/invencion-inventor';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { IPersona } from '@core/models/sgp/persona';
import { Fragment } from '@core/services/action-service';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, combineLatest, EMPTY, from, Observable, of, Subscription } from 'rxjs';
import { catchError, filter, map, mergeMap, switchMap, takeLast, tap, toArray } from 'rxjs/operators';


export class InvencionInventorFragment extends Fragment {

  // tslint:disable-next-line: variable-name
  private _participacionCompleta$ = new BehaviorSubject<boolean>(true);
  // tslint:disable-next-line: variable-name
  private _verificarParticipacion$ = new BehaviorSubject<any>(null);
  private _invencionInventores$: BehaviorSubject<StatusWrapper<IInvencionInventor>[]> = new BehaviorSubject<StatusWrapper<IInvencionInventor>[]>([]);

  get invencionInventores$(): BehaviorSubject<StatusWrapper<IInvencionInventor>[]> {
    return this._invencionInventores$;
  }

  get participacionCompleta$(): Observable<boolean> {
    return this._participacionCompleta$.asObservable();
  }

  constructor(
    key: number,
    private readonly logger: NGXLogger,
    private readonly invencionService: InvencionService,
    private readonly personaService: PersonaService,
    private readonly empresaService: EmpresaService,
    private readonly isEditPerm: boolean
  ) {
    super(key);
    this.setComplete(false);
  }


  protected onInitialize(): void | Observable<any> {
    combineLatest([this._invencionInventores$, this._verificarParticipacion$])
      .pipe(
        switchMap(([invencionInventores, verificarParticipacion]) =>
          of(invencionInventores.filter(el => !el.deleted).reduce((acc, curr) => acc + curr.value.participacion, 0))
        )
      ).subscribe(total => {
        const participacionCompleta = total === 100 ? true : false;
        setTimeout(() => {
          this.setErrors(!participacionCompleta);
          this.setComplete(participacionCompleta);
        });
        this._participacionCompleta$.next(participacionCompleta);
      });

    return this.initializer(this.getKey() as number);
  }

  protected initializer = (key: number): Observable<IInvencionInventor> =>
    of(key)
      .pipe(
        filter(elem => !!elem),
        tap(() => this.subscriptions.push(this.loadInvencionInventores(key))),
        mergeMap(ev => of(null)),
        catchError((err) => {
          this.setComplete(false);
          this.setErrors(true);
          this.logger.error(err);
          return EMPTY;
        })
      )

  hasEditPerm = () => this.isEditPerm;

  saveOrUpdate(): Observable<string | number | void> {
    const invencionId = this.getKey() as number;
    this._invencionInventores$.value.forEach(el => el.value.invencion = { id: invencionId } as IInvencion);

    return this._persistChangesInvencionInventor().pipe(
      takeLast(1),
      tap(() => {
        this.setChanges(false);
      })
    );
  }

  /**
   * Funcion que debe ser llamada cuando los cambios no impliquen una emisión
   * del arreglo de {@link IInvencionInventor}
   */
  invencionChanged() {
    this.setChanges(true);
    this.forceCheckParticipacion();
  }

  /**
   * Marca como eliminado el elemento pasado por parámetros y lo agrega al
   * listado de elementos a eliminar.
   *
   * @param invencionInventor Elemento a ser eliminado.
   */
  deleteInvencionInventor(invencionInventor: StatusWrapper<IInvencionInventor>) {
    const current = this._invencionInventores$.value;
    const index = current.findIndex((value) => value === invencionInventor);
    if (index >= 0) {
      current.splice(index, 1);
      this._invencionInventores$.next(current);
      this.setChanges(true);
    }
  }

  /**
   * Agrega al listado de {@link InvencionInventor} el elemento pasado
   * por parámetros marcándolo como un nuevo elemento creado. En caso que se
   * identifique que existe un elemento que ya hace referencia a la persona asociada se generará un error.
   * @param invencionInventorCreated Entidad siendo creada
   */
  addInvencionInventor(invencionInventorCreated: StatusWrapper<IInvencionInventor>) {
    invencionInventorCreated.setEdited();
    invencionInventorCreated.value.invencion = { id: this.getKey() } as IInvencion;
    invencionInventorCreated.value.activo = true;
    const current = this._invencionInventores$.value;
    current.push(invencionInventorCreated);
    this._invencionInventores$.next(current);
    this.setChanges(true);
  }

  /**
   * Marca como editado el  {@link InvencionInventor} pasado por parámetros
   *
   * @param invencionInventorEdited Entidad siendo editada
   */
  editInvencionInventor(invencionInventorEdited: StatusWrapper<IInvencionInventor>) {
    invencionInventorEdited.value.activo = true;
    this.invencionChanged();
  }

  private loadInvencionInventores = (invencionId: number): Subscription =>
    this.invencionService
      .findInventores(invencionId)
      .pipe(
        filter(invencionInventores => !!invencionInventores),
        mergeMap(invencionInventores => from(invencionInventores)),
        mergeMap(invencionInventor =>
          this.resolveInventorAndEntidad(invencionInventor)
        ),
        map(invencionInventor => new StatusWrapper<IInvencionInventor>(invencionInventor)),
        toArray()
      ).subscribe(
        (invencionInventores) => {
          this._invencionInventores$.next(invencionInventores);
        }
      )

  private forceCheckParticipacion = () =>
    this._verificarParticipacion$.next(null)

  private resolveInventorAndEntidad = (invencionInventor: IInvencionInventor) =>
    this.personaService
      .findById(invencionInventor.inventor?.id).pipe(
        map(persona => {
          invencionInventor.inventor = persona;
          return invencionInventor;
        }),
        catchError(err => {
          this.logger.error(err);
          invencionInventor.inventor = {} as IPersona;
          return of(invencionInventor);
        }),
        mergeMap(invencionInventorEntidad => {
          if (!invencionInventorEntidad.inventor?.entidad?.id) {
            invencionInventorEntidad.inventor.entidad = {} as IEmpresa;
            return of(invencionInventorEntidad);
          }
          return this.empresaService
            .findById(invencionInventorEntidad.inventor?.entidad?.id).pipe(
              map(entidad => {
                invencionInventorEntidad.inventor.entidad = entidad;
                return invencionInventorEntidad;
              }),
              catchError(err => {
                this.logger.error(err);
                invencionInventorEntidad.inventor.entidad = {} as IEmpresa;
                return of(invencionInventorEntidad);
              })
            );
        }),
        mergeMap(invencionInventorEntidad => {
          if (!invencionInventorEntidad.inventor?.entidadPropia?.id) {
            invencionInventorEntidad.inventor.entidadPropia = {} as IEmpresa;
            return of(invencionInventorEntidad);
          }
          return this.empresaService
            .findById(invencionInventorEntidad.inventor?.entidadPropia?.id).pipe(
              map(entidad => {
                invencionInventorEntidad.inventor.entidadPropia = entidad;
                return invencionInventorEntidad;
              }),
              catchError(err => {
                this.logger.error(err);
                invencionInventorEntidad.inventor.entidadPropia = {} as IEmpresa;
                return of(invencionInventorEntidad);
              })
            );
        },
        )
      )

  private _persistChangesInvencionInventor(): Observable<void> {
    const invencionInventoresToPersist = this._invencionInventores$.value;
    return this.invencionService.
      bulkSaveOrUpdateInvencionInventores(this.getKey() as number, invencionInventoresToPersist.map(elem => elem.value)).pipe(
        tap(invencionInventoresPersisted => this.refreshInvencionInventoresData(invencionInventoresPersisted)),
        switchMap(e => of(void 0))
      );
  }

  private refreshInvencionInventoresData(invencionInventoresPersisted: IInvencionInventor[]): void {
    const currentInvencionInventores = this._invencionInventores$.value.map((wrapper) => wrapper.value);
    const invencionInventoresRefreshed = invencionInventoresPersisted.map(invencionInventorPersisted => {
      this.copyInvencionInventorRelatedAttributes(this.findInvencionInventorSource(currentInvencionInventores, invencionInventorPersisted), invencionInventorPersisted);
      return new StatusWrapper(invencionInventorPersisted);
    });
    this._invencionInventores$.next(invencionInventoresRefreshed);
  }

  private findInvencionInventorSource(currentInvencionInventores: IInvencionInventor[], invencionInventorPersisted: IInvencionInventor): IInvencionInventor | undefined {
    return currentInvencionInventores.find(currentInventor => currentInventor.inventor.id === invencionInventorPersisted.inventor.id);
  }

  private copyInvencionInventorRelatedAttributes(source: IInvencionInventor | undefined, target: IInvencionInventor): void {
    if (source) {
      target.invencion = source.invencion;
      target.inventor = source.inventor;
    }
  }

  private _isValidIndex(array: StatusWrapper<IInvencionInventor>[], index: number): boolean {
    if (index < 0 || index >= array.length) {
      return false;
    }
    return true;
  }

}
