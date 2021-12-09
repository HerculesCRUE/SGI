import { ISolicitudProyectoClasificacion } from '@core/models/csp/solicitud-proyecto-clasificacion';
import { IClasificacion } from '@core/models/sgo/clasificacion';
import { Fragment } from '@core/services/action-service';
import { SolicitudProyectoClasificacionService } from '@core/services/csp/solicitud-proyecto-clasificacion.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { ClasificacionService } from '@core/services/sgo/clasificacion.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export interface SolicitudProyectoClasificacionListado {
  id: number;
  solicitudProyectoId: number;
  clasificacion: IClasificacion;
  niveles: IClasificacion[];
  nivelesTexto: string;
  nivelSeleccionado: IClasificacion;
}

export class SolicitudProyectoClasificacionesFragment extends Fragment {
  clasificaciones$ = new BehaviorSubject<StatusWrapper<SolicitudProyectoClasificacionListado>[]>([]);
  private clasificacionesEliminadas: StatusWrapper<SolicitudProyectoClasificacionListado>[] = [];

  constructor(
    key: number,
    private service: SolicitudProyectoClasificacionService,
    private solicitudService: SolicitudService,
    private clasificacionService: ClasificacionService,
    public readonly: boolean
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const subscription = this.solicitudService.findAllClasificacionesSolicitud(this.getKey() as number).pipe(
        map(response => response.items.map(solicitudProyectoClasificacion => {
          const clasificacionListado: SolicitudProyectoClasificacionListado = {
            id: solicitudProyectoClasificacion.id,
            solicitudProyectoId: solicitudProyectoClasificacion.solicitudProyectoId,
            clasificacion: undefined,
            nivelSeleccionado: solicitudProyectoClasificacion.clasificacion,
            niveles: undefined,
            nivelesTexto: ''
          };
          return clasificacionListado;
        })),
        switchMap((result) => {
          return from(result).pipe(
            mergeMap((solicitudProyectoClasificacion) => {

              return this.clasificacionService.findById(solicitudProyectoClasificacion.nivelSeleccionado.id).pipe(
                map((clasificacion) => {
                  solicitudProyectoClasificacion.nivelSeleccionado = clasificacion;
                  solicitudProyectoClasificacion.niveles = [clasificacion];
                }),
                switchMap(() => {
                  return this.getNiveles(solicitudProyectoClasificacion);
                })
              );
            })
          );
        })
      ).subscribe((solicitudProyectoClasificacion) => {
        solicitudProyectoClasificacion.clasificacion =
          solicitudProyectoClasificacion.niveles[solicitudProyectoClasificacion.niveles.length - 1];

        solicitudProyectoClasificacion.nivelesTexto = solicitudProyectoClasificacion.niveles
          .slice(1, solicitudProyectoClasificacion.niveles.length - 1)
          .reverse()
          .map(clasificacion => clasificacion.nombre).join(' - ');
        this.clasificaciones$.value.push(new StatusWrapper<SolicitudProyectoClasificacionListado>(solicitudProyectoClasificacion));
        this.clasificaciones$.next(this.clasificaciones$.value);
      });

      this.subscriptions.push(subscription);
    }
  }

  public addClasificaciones(clasificaciones: SolicitudProyectoClasificacionListado[]) {
    if (clasificaciones && clasificaciones.length > 0) {
      clasificaciones.forEach((clasificacion) => {
        clasificacion.solicitudProyectoId = this.getKey() as number;
        const wrapped = new StatusWrapper<SolicitudProyectoClasificacionListado>(clasificacion);
        wrapped.setCreated();
        const current = this.clasificaciones$.value;
        current.push(wrapped);
        this.clasificaciones$.next(current);
      });

      this.setChanges(true);
    }
  }

  public deleteClasificacion(wrapper: StatusWrapper<SolicitudProyectoClasificacionListado>) {
    const current = this.clasificaciones$.value;
    const index = current.findIndex(
      (value) => value.value === wrapper.value
    );
    if (index >= 0) {
      if (!wrapper.created) {
        this.clasificacionesEliminadas.push(current[index]);
      }
      current.splice(index, 1);
      this.clasificaciones$.next(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<void> {
    return merge(
      this.deleteClasificaciones(),
      this.createClasificaciones()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private deleteClasificaciones(): Observable<void> {
    if (this.clasificacionesEliminadas.length === 0) {
      return of(void 0);
    }
    return from(this.clasificacionesEliminadas).pipe(
      mergeMap((wrapped) => {
        return this.service.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.clasificacionesEliminadas = this.clasificacionesEliminadas.filter(deletedClasificacion =>
                deletedClasificacion.value.id !== wrapped.value.id);
            })
          );
      })
    );
  }

  private createClasificaciones(): Observable<void> {
    const createdClasificaciones = this.clasificaciones$.value.filter((clasificacion) => clasificacion.created);
    if (createdClasificaciones.length === 0) {
      return of(void 0);
    }

    return from(createdClasificaciones).pipe(
      mergeMap((wrappedClasificacion) => {
        const solicitudProyectoClasificacion: ISolicitudProyectoClasificacion = {
          id: undefined,
          solicitudProyectoId: wrappedClasificacion.value.solicitudProyectoId,
          clasificacion: wrappedClasificacion.value.nivelSeleccionado,
        };
        return this.service.create(solicitudProyectoClasificacion).pipe(
          map((createdClasificacion) => {
            const index = this.clasificaciones$.value.findIndex((currentClasificaciones) =>
              currentClasificaciones === wrappedClasificacion);
            const clasificacionListado = wrappedClasificacion.value;
            clasificacionListado.id = createdClasificacion.id;
            this.clasificaciones$.value[index] = new StatusWrapper<SolicitudProyectoClasificacionListado>(clasificacionListado);
            this.clasificaciones$.next(this.clasificaciones$.value);
          })
        );
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.clasificaciones$.value.some((wrapper) => wrapper.touched);
    return !(this.clasificacionesEliminadas.length > 0 || touched);
  }

  private getNiveles(solicitudProyectoClasificacion: SolicitudProyectoClasificacionListado):
    Observable<SolicitudProyectoClasificacionListado> {
    const lastLevel = solicitudProyectoClasificacion.niveles[solicitudProyectoClasificacion.niveles.length - 1];
    if (!lastLevel.padreId) {
      return of(solicitudProyectoClasificacion);
    }

    return this.clasificacionService.findById(lastLevel.padreId).pipe(
      switchMap(clasificacion => {
        solicitudProyectoClasificacion.niveles.push(clasificacion);
        return this.getNiveles(solicitudProyectoClasificacion);
      })
    );
  }
}
