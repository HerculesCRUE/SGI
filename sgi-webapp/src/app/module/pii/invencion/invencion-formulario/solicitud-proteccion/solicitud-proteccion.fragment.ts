import { TipoPropiedad } from '@core/enums/tipo-propiedad';
import { ISolicitudProteccion } from '@core/models/pii/solicitud-proteccion';
import { IPais } from '@core/models/sgo/pais';
import { Fragment } from '@core/services/action-service';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { SolicitudProteccionService } from '@core/services/pii/solicitud-proteccion/solicitud-proteccion.service';
import { PaisService } from '@core/services/sgo/pais/pais.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { catchError, map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export class SolicitudProteccionFragment extends Fragment {

  public solicitudesProteccion$ = new BehaviorSubject<StatusWrapper<ISolicitudProteccion>[]>([]);
  private solicitudesProteccionEliminadas: StatusWrapper<ISolicitudProteccion>[] = [];

  public paises$ = new BehaviorSubject<IPais[]>([]);

  constructor(
    key: number,
    private invencionService: InvencionService,
    protected solicitudProteccionService: SolicitudProteccionService,
    public invencionTipoPropiedad: TipoPropiedad,
    private paisService: PaisService,
    private logger: NGXLogger) {

    super(key);
    this.setComplete(true);
    this.loadPaises$().subscribe(paises => {
      this.paises$.next(paises);
    });

  }

  protected onInitialize(): void {
    const id = this.getKey() as number;

    if (id) {
      this.loadTable();
    }
  }

  saveOrUpdate(): Observable<void> {
    return this.deleteSolicitudesProteccion()
      .pipe(
        takeLast(1),
        tap(() => {
          if (this.isSaveOrUpdateComplete()) {
            this.setChanges(false);
          }
        })
      );
  }

  public deleteSolicitudProteccion(solicitudProteccion: ISolicitudProteccion) {
    const current = this.solicitudesProteccion$.value;
    const index = current.findIndex(value => value.value.id === solicitudProteccion.id);
    if (index >= 0) {
      this.solicitudesProteccionEliminadas.push(current[index]);
      current.splice(index, 1);
      this.solicitudesProteccion$.next(current);
      this.setChanges(true);
    }
  }

  private deleteSolicitudesProteccion(): Observable<void> {
    const deleted = this.solicitudesProteccionEliminadas.filter((value) => value.value.id);
    if (deleted.length === 0) {
      return of(void 0);
    }
    return from(deleted).pipe(
      mergeMap((wrapped) => {
        return this.solicitudProteccionService.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.solicitudesProteccionEliminadas = deleted.filter(solicitudProteccion => solicitudProteccion.value.id !== wrapped.value.id);
            })
          );
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    return this.solicitudesProteccionEliminadas.length === 0;
  }

  private loadPaises$(): Observable<IPais[]> {
    return this.paisService.findAll().pipe(
      map(response => response.items),
      catchError(error => {
        this.logger.error(error);
        return of([]);
      })
    );
  }

  public loadTable(options?: SgiRestFindOptions) {
    this.solicitudesProteccion$.next([]);

    this.subscriptions.push(this.invencionService.findAllSolicitudesProteccion(this.getKey() as number, options).pipe(
      map((solicitudesProteccion: SgiRestListResult<ISolicitudProteccion>) =>
        solicitudesProteccion.items.map((solicitudProteccion) => new StatusWrapper<ISolicitudProteccion>(solicitudProteccion)))
    ).subscribe(result => this.solicitudesProteccion$.next(result)));
  }

  getSolicitudesProteccion$(): Observable<ISolicitudProteccion[]> {
    return this.solicitudesProteccion$.asObservable().pipe(
      map(solicitudesProteccion => solicitudesProteccion.map(wrapper => wrapper.value)
      )
    );
  }

}
