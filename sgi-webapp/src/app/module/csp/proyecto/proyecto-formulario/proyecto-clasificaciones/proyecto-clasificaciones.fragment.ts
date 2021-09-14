import { IProyectoClasificacion } from '@core/models/csp/proyecto-clasificacion';
import { IClasificacion } from '@core/models/sgo/clasificacion';
import { Fragment } from '@core/services/action-service';
import { ProyectoClasificacionService } from '@core/services/csp/proyecto-clasificacion.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { ClasificacionService } from '@core/services/sgo/clasificacion.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export interface ProyectoClasificacionListado {
  id: number;
  proyectoId: number;
  clasificacion: IClasificacion;
  niveles: IClasificacion[];
  nivelesTexto: string;
  nivelSeleccionado: IClasificacion;
}

export class ProyectoClasificacionesFragment extends Fragment {
  clasificaciones$ = new BehaviorSubject<StatusWrapper<ProyectoClasificacionListado>[]>([]);
  private clasificacionesEliminadas: StatusWrapper<ProyectoClasificacionListado>[] = [];

  constructor(
    key: number,
    private service: ProyectoClasificacionService,
    private proyectoService: ProyectoService,
    private clasificacionService: ClasificacionService,
    public readonly: boolean,
    public isVisor: boolean
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const subscription = this.proyectoService.findAllClasificacionesProyecto(this.getKey() as number).pipe(
        map(response => response.items.map(proyectoClasificacion => {
          const clasificacionListado: ProyectoClasificacionListado = {
            id: proyectoClasificacion.id,
            proyectoId: proyectoClasificacion.proyectoId,
            clasificacion: undefined,
            nivelSeleccionado: proyectoClasificacion.clasificacion,
            niveles: undefined,
            nivelesTexto: ''
          };
          return clasificacionListado;
        })),
        switchMap((result) => {
          return from(result).pipe(
            mergeMap((proyectoClasificacion) => {

              return this.clasificacionService.findById(proyectoClasificacion.nivelSeleccionado.id).pipe(
                map((clasificacion) => {
                  proyectoClasificacion.nivelSeleccionado = clasificacion;
                  proyectoClasificacion.niveles = [clasificacion];
                }),
                switchMap(() => {
                  return this.getNiveles(proyectoClasificacion);
                })
              );
            })
          );
        })
      ).subscribe((proyectoClasificacion) => {
        proyectoClasificacion.clasificacion =
          proyectoClasificacion.niveles[proyectoClasificacion.niveles.length - 1];

        proyectoClasificacion.nivelesTexto = proyectoClasificacion.niveles
          .slice(1, proyectoClasificacion.niveles.length - 1)
          .reverse()
          .map(clasificacion => clasificacion.nombre).join(' - ');
        this.clasificaciones$.value.push(new StatusWrapper<ProyectoClasificacionListado>(proyectoClasificacion));
        this.clasificaciones$.next(this.clasificaciones$.value);
      });

      this.subscriptions.push(subscription);
    }
  }

  public addClasificaciones(clasificaciones: ProyectoClasificacionListado[]) {
    if (clasificaciones && clasificaciones.length > 0) {
      clasificaciones.forEach((clasificacion) => {
        clasificacion.proyectoId = this.getKey() as number;
        const wrapped = new StatusWrapper<ProyectoClasificacionListado>(clasificacion);
        wrapped.setCreated();
        const current = this.clasificaciones$.value;
        current.push(wrapped);
        this.clasificaciones$.next(current);
      });

      this.setChanges(true);
    }
  }

  public deleteClasificacion(wrapper: StatusWrapper<ProyectoClasificacionListado>) {
    const current = this.clasificaciones$.value;
    const index = current.findIndex(
      (value) => value === wrapper
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
        const proyectoClasificacion: IProyectoClasificacion = {
          id: undefined,
          proyectoId: wrappedClasificacion.value.proyectoId,
          clasificacion: wrappedClasificacion.value.nivelSeleccionado,
        };
        return this.service.create(proyectoClasificacion).pipe(
          map((createdClasificacion) => {
            const index = this.clasificaciones$.value.findIndex((currentClasificaciones) =>
              currentClasificaciones === wrappedClasificacion);
            const clasificacionListado = wrappedClasificacion.value;
            clasificacionListado.id = createdClasificacion.id;
            this.clasificaciones$.value[index] = new StatusWrapper<ProyectoClasificacionListado>(clasificacionListado);
          })
        );
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.clasificaciones$.value.some((wrapper) => wrapper.touched);
    return (this.clasificacionesEliminadas.length > 0 || touched);
  }

  private getNiveles(proyectoClasificacion: ProyectoClasificacionListado):
    Observable<ProyectoClasificacionListado> {
    const lastLevel = proyectoClasificacion.niveles[proyectoClasificacion.niveles.length - 1];
    if (!lastLevel.padreId) {
      return of(proyectoClasificacion);
    }

    return this.clasificacionService.findById(lastLevel.padreId).pipe(
      switchMap(clasificacion => {
        proyectoClasificacion.niveles.push(clasificacion);
        return this.getNiveles(proyectoClasificacion);
      })
    );
  }
}
