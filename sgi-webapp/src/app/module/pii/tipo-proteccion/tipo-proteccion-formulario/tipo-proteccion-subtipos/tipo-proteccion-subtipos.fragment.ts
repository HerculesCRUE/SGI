import { MSG_PARAMS } from '@core/i18n';
import { ITipoProteccion } from '@core/models/pii/tipo-proteccion';
import { Fragment } from '@core/services/action-service';
import { TipoProteccionService } from '@core/services/pii/tipo-proteccion/tipo-proteccion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { EMPTY } from 'rxjs';
import { BehaviorSubject, concat, forkJoin, from, Observable, of } from 'rxjs';
import { catchError, concatMap, filter, map, switchMap, takeLast, tap } from 'rxjs/operators';

class TipoProteccionDuplicatedError extends Error { }

export class TipoProteccionSubtiposFragment extends Fragment {

  tipoProteccionPadre: ITipoProteccion;
  subtiposProteccion$ = new BehaviorSubject<StatusWrapper<ITipoProteccion>[]>([]);
  subtiposProteccionToDeactivate$: StatusWrapper<ITipoProteccion>[] = [];
  subtiposProteccionToActivate$: StatusWrapper<ITipoProteccion>[] = [];

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private tipoProteccionService: TipoProteccionService,
    protected readonly snackBarService: SnackBarService
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const cargarPadre$ = this.tipoProteccionService.findById(this.getKey() as number);
      const cargarSubtipos$ = this.tipoProteccionService
        .findAllSubtipos(this.getKey() as number).pipe(
          switchMap((sgiResultSubtipos) => {
            return of(sgiResultSubtipos.items.map(item => new StatusWrapper<ITipoProteccion>(item)));
          }));

      this.subscriptions.push(
        cargarPadre$.subscribe((tipoProteccionPadre) => { this.tipoProteccionPadre = tipoProteccionPadre; }),
        cargarSubtipos$.subscribe(subtipos => this.subtiposProteccion$.next(subtipos)));
    }
  }

  saveOrUpdate(): Observable<void> {
    return concat(
      this.agregarSubtiposProteccion(),
      this.activateTiposProteccion(),
      this.deactivateTiposProteccion()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      }),
      catchError((error) => {
        this.logger.error(error);
        return EMPTY;
      }),
      switchMap(() => EMPTY)
    );
  }

  public agregarSubtipoProteccion(subtipoProteccion: StatusWrapper<ITipoProteccion>, errorNombreDuplicatedMsg: string, errorMsg: string) {
    this.subscriptions.push(
      forkJoin([of(subtipoProteccion), of(this.subtiposProteccion$.value)])
        .pipe(
          filter(([subtipoProteccionAgregar, listaSubtiposProteccion]) =>
            listaSubtiposProteccion.indexOf(subtipoProteccionAgregar) === -1),
          switchMap(([subtipoProteccionAgregar, listaSubtiposProteccion]) => {
            if (listaSubtiposProteccion.some(elem => elem.value.nombre === subtipoProteccionAgregar.value.nombre)) {
              throw new TipoProteccionDuplicatedError();
            }
            return this.tipoProteccionService
              .finTiposProteccionByNombre(this.getKey() as number, subtipoProteccionAgregar.value.nombre).pipe(
                tap((elem) => {
                  if (elem.items.length > 0) {
                    throw new TipoProteccionDuplicatedError();
                  }
                }),
                map(elem => [subtipoProteccionAgregar, listaSubtiposProteccion])
              );
          }),
          map(([subtipoProteccionAgregar, listaSubtiposProteccion]) => {
            subtipoProteccionAgregar = (subtipoProteccionAgregar as StatusWrapper<ITipoProteccion>);
            subtipoProteccionAgregar.setCreated();
            subtipoProteccionAgregar.value.activo = true;
            subtipoProteccionAgregar.value.tipoPropiedad = this.tipoProteccionPadre.tipoPropiedad;
            subtipoProteccionAgregar.value.padre = this.tipoProteccionPadre;
            (listaSubtiposProteccion as StatusWrapper<ITipoProteccion>[]).push((subtipoProteccion as StatusWrapper<ITipoProteccion>));

            return listaSubtiposProteccion as StatusWrapper<ITipoProteccion>[];
          }),
        ).subscribe((listaSubtiposProteccion) => {
          this.subtiposProteccion$.next(listaSubtiposProteccion);
          this.setChanges(true);
        }, (error) => {
          if (error instanceof TipoProteccionDuplicatedError) {
            this.snackBarService.showError(errorNombreDuplicatedMsg);
          } else {
            this.snackBarService.showError(errorMsg);
          }
        })
    );
  }

  public deactivateTipoProteccion(subtipoProteccion: StatusWrapper<ITipoProteccion>) {
    subtipoProteccion.value.activo = false;
    if (this.subtiposProteccionToActivate$.indexOf(subtipoProteccion) !== -1) {
      const index = this.subtiposProteccionToActivate$.indexOf(subtipoProteccion);
      this.subtiposProteccionToActivate$.splice(index, 1);
      return;
    }
    if (this.subtiposProteccionToDeactivate$.indexOf(subtipoProteccion) === -1) {
      this.setChanges(true);
      this.subtiposProteccionToDeactivate$.push(subtipoProteccion);
    }
  }

  public activateTipoProteccion(subtipoProteccion: StatusWrapper<ITipoProteccion>) {
    subtipoProteccion.value.activo = true;
    if (this.subtiposProteccionToDeactivate$.indexOf(subtipoProteccion) !== -1) {
      const index = this.subtiposProteccionToDeactivate$.indexOf(subtipoProteccion);
      this.subtiposProteccionToDeactivate$.splice(index, 1);
      return;
    }
    if (this.subtiposProteccionToActivate$.indexOf(subtipoProteccion) === -1) {
      this.setChanges(true);
      this.subtiposProteccionToActivate$.push(subtipoProteccion);
    }
  }

  public createEmptySubtipoProteccion = () => {
    const emptySubtipoProteccion = new StatusWrapper<ITipoProteccion>({ activo: true } as ITipoProteccion);
    emptySubtipoProteccion.setCreated();
    return emptySubtipoProteccion;
  }

  private agregarSubtiposProteccion() {
    return from(this.subtiposProteccion$.value).pipe(
      filter(elem => elem.created),
      concatMap((subtipoToCreate) =>
        this.tipoProteccionService.create(subtipoToCreate.value).pipe(switchMap((createdSubtipo) => {
          const index = this.subtiposProteccion$.value.indexOf(subtipoToCreate);
          this.subtiposProteccion$.value[index] = new StatusWrapper<ITipoProteccion>(createdSubtipo);
          return of(subtipoToCreate);
        })))
    );
  }

  private deactivateTiposProteccion() {
    return from(this.subtiposProteccionToDeactivate$).pipe(
      filter(elem => !!elem.value.id),
      concatMap((subtipoToDeactivate) =>
        this.tipoProteccionService.deactivate(subtipoToDeactivate.value.id)
      )
    );
  }

  private activateTiposProteccion() {
    return from(this.subtiposProteccionToActivate$).pipe(
      filter(elem => !!elem.value.id),
      concatMap((subtipoToactivate) =>
        this.tipoProteccionService.activate(subtipoToactivate.value.id))
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.subtiposProteccion$.value.some((subtipoWrapper) =>
      subtipoWrapper && subtipoWrapper.touched);
    return !touched;
  }

}
