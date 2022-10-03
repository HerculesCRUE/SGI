import { IGrupoLineaClasificacion } from '@core/models/csp/grupo-linea-clasificacion';
import { IGrupoLineaInvestigacion } from '@core/models/csp/grupo-linea-investigacion';
import { IClasificacion } from '@core/models/sgo/clasificacion';
import { Fragment } from '@core/services/action-service';
import { GrupoLineaClasificacionService } from '@core/services/csp/grupo-linea-clasificacion/grupo-linea-clasificacion.service';
import { GrupoLineaInvestigacionService } from '@core/services/csp/grupo-linea-investigacion/grupo-linea-investigacion.service';
import { ClasificacionService } from '@core/services/sgo/clasificacion.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export interface GrupoLineaClasificacionListado {
  id: number;
  grupoLineaInvestigacionId: number;
  clasificacion: IClasificacion;
  niveles: IClasificacion[];
  nivelesTexto: string;
  nivelSeleccionado: IClasificacion;
  grupoLineaInvestigacion?: IGrupoLineaInvestigacion;
}

export class GrupoLineaClasificacionesFragment extends Fragment {
  clasificaciones$ = new BehaviorSubject<StatusWrapper<GrupoLineaClasificacionListado>[]>([]);
  private clasificacionesEliminadas: StatusWrapper<GrupoLineaClasificacionListado>[] = [];

  constructor(
    key: number,
    private service: GrupoLineaClasificacionService,
    private grupoLineaInvestigacionService: GrupoLineaInvestigacionService,
    private clasificacionService: ClasificacionService,
    public readonly readonly: boolean
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const subscription = this.grupoLineaInvestigacionService.findClasificaciones(this.getKey() as number).pipe(
        map(response => response.items.map(grupoLineaClasificacion => {
          const clasificacionListado: GrupoLineaClasificacionListado = {
            id: grupoLineaClasificacion.id,
            grupoLineaInvestigacionId: undefined,
            clasificacion: undefined,
            nivelSeleccionado: grupoLineaClasificacion.clasificacion,
            niveles: undefined,
            nivelesTexto: ''
          };
          return clasificacionListado;
        })),
        switchMap((result) => {
          return from(result).pipe(
            mergeMap((grupoLineaClasificacion) => {

              return this.clasificacionService.findById(grupoLineaClasificacion.nivelSeleccionado.id).pipe(
                map((clasificacion) => {
                  grupoLineaClasificacion.nivelSeleccionado = clasificacion;
                  grupoLineaClasificacion.niveles = [clasificacion];
                }),
                switchMap(() => {
                  return this.getNiveles(grupoLineaClasificacion);
                })
              );
            })
          );
        })
      ).subscribe((grupoLineaClasificacion) => {
        grupoLineaClasificacion.clasificacion =
          grupoLineaClasificacion.niveles[grupoLineaClasificacion.niveles.length - 1];

        grupoLineaClasificacion.nivelesTexto = grupoLineaClasificacion.niveles
          .slice(1, grupoLineaClasificacion.niveles.length - 1)
          .reverse()
          .map(clasificacion => clasificacion.nombre).join(' - ');
        this.clasificaciones$.value.push(new StatusWrapper<GrupoLineaClasificacionListado>(grupoLineaClasificacion));
        this.clasificaciones$.next(this.clasificaciones$.value);
      });

      this.subscriptions.push(subscription);
    }
  }

  public addClasificaciones(clasificaciones: GrupoLineaClasificacionListado[]) {
    if (clasificaciones && clasificaciones.length > 0) {
      clasificaciones.forEach((clasificacion) => {
        clasificacion.grupoLineaInvestigacionId = this.getKey() as number;
        const wrapped = new StatusWrapper<GrupoLineaClasificacionListado>(clasificacion);
        wrapped.setCreated();
        const current = this.clasificaciones$.value;
        current.push(wrapped);
        this.clasificaciones$.next(current);
      });

      this.setChanges(true);
    }
  }

  public deleteClasificacion(wrapper: StatusWrapper<GrupoLineaClasificacionListado>) {
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
        const grupoLineaClasificacion: IGrupoLineaClasificacion = {
          id: undefined,
          grupoLineaInvestigacionId: this.getKey() as number,
          clasificacion: wrappedClasificacion.value.nivelSeleccionado,
        };
        return this.service.create(grupoLineaClasificacion).pipe(
          map((createdClasificacion) => {
            const index = this.clasificaciones$.value.findIndex((currentClasificaciones) =>
              currentClasificaciones === wrappedClasificacion);
            const clasificacionListado = wrappedClasificacion.value;
            clasificacionListado.id = createdClasificacion.id;
            this.clasificaciones$.value[index] = new StatusWrapper<GrupoLineaClasificacionListado>(clasificacionListado);
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

  private getNiveles(grupoLineaClasificacion: GrupoLineaClasificacionListado): Observable<GrupoLineaClasificacionListado> {
    const lastLevel = grupoLineaClasificacion.niveles[grupoLineaClasificacion.niveles.length - 1];
    if (!lastLevel.padreId) {
      return of(grupoLineaClasificacion);
    }

    return this.clasificacionService.findById(lastLevel.padreId).pipe(
      switchMap(clasificacion => {
        grupoLineaClasificacion.niveles.push(clasificacion);
        return this.getNiveles(grupoLineaClasificacion);
      })
    );
  }

}
