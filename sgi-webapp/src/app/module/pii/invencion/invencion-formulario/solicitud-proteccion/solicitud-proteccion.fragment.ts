import { TipoPropiedad } from '@core/enums/tipo-propiedad';
import { ISolicitudProteccion } from '@core/models/pii/solicitud-proteccion';
import { Fragment } from '@core/services/action-service';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { SolicitudProteccionService } from '@core/services/pii/invencion/solicitud-proteccion/solicitud-proteccion.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { SgiRestListResult } from '@sgi/framework/http';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export class SolicitudProteccionFragment extends Fragment {

  public solicitudesProteccion$ = new BehaviorSubject<StatusWrapper<ISolicitudProteccion>[]>([]);
  public deactivateSolicitudesProteccion: number[] = [];
  public activateSolicitudesProteccion: number[] = [];
  private needSaveSolicitudesProteccion: number[] = [];

  constructor(
    key: number,
    private invencionService: InvencionService,
    protected solicitudProteccionService: SolicitudProteccionService,
    public invencionTipoPropiedad: TipoPropiedad) {

    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    const id = this.getKey() as number;

    if (id) {
      this.subscriptions.push(
        this.invencionService.findAllSolicitudesProteccion(id).pipe(
          map((solicitudesProteccion: SgiRestListResult<ISolicitudProteccion>) =>
            solicitudesProteccion.items.map((solicitudProteccion) => new StatusWrapper<ISolicitudProteccion>(solicitudProteccion)))
        ).subscribe(result => this.solicitudesProteccion$.next(result))
      );
    }
  }

  saveOrUpdate(): Observable<void> {
    return merge(
      this.checkAndActivateSolicitudesProteccion(),
      this.checkAndDeactivateSolicitudesProteccion()
    ).pipe(
      takeLast(1)
    );
  }

  private checkAndActivateSolicitudesProteccion(): Observable<void> {
    if (this.activateSolicitudesProteccion.length === 0) {
      return of(void 0);
    }
    return from(this.activateSolicitudesProteccion).pipe(
      mergeMap((solicitudProteccionId: number) => {
        return this.activateSolicitudProteccion(solicitudProteccionId).pipe(
          tap(() => {
            this.activateSolicitudesProteccion.splice(
              this.activateSolicitudesProteccion.findIndex(currentSolicitudProtId =>
                currentSolicitudProtId === solicitudProteccionId), 1);
          }),
          switchMap(() => this.checkIfSaveChangesIsNeeded()),
          takeLast(1)
        );
      })
    );
  }

  private checkAndDeactivateSolicitudesProteccion(): Observable<void> {
    if (this.deactivateSolicitudesProteccion.length === 0) {
      return of(void 0);
    }
    return from(this.deactivateSolicitudesProteccion).pipe(
      mergeMap((solicitudProteccionId: number) => {
        return this.deactivateSolicitudProteccion(solicitudProteccionId).pipe(
          tap(() => {
            this.deactivateSolicitudesProteccion.splice(
              this.deactivateSolicitudesProteccion.findIndex(currentSolicitudProtId =>
                currentSolicitudProtId === solicitudProteccionId), 1);
          }),
          switchMap(() => this.checkIfSaveChangesIsNeeded()),
          takeLast(1)
        );
      })
    );
  }

  /**
   * Desactivar Solicitud de Proteccion.
   * @param solicitudProteccionId: number
   */
  private deactivateSolicitudProteccion(solicitudProteccionId: number): Observable<void> {
    return this.solicitudProteccionService.deactivate(solicitudProteccionId);
  }

  /**
   * Activar un registro de Solicitud de Proteccion
   * @param solicitudProteccionId: number
   */
  private activateSolicitudProteccion(solicitudProteccionId: number): Observable<void> {
    return this.solicitudProteccionService.activate(solicitudProteccionId);
  }

  public checkIfSaveChangesIsNeeded(): Observable<void> {
    if (this.activateSolicitudesProteccion.length === 0 && this.deactivateSolicitudesProteccion.length === 0) {
      this.setChanges(false);
    }
    return of(void 0);
  }
}
