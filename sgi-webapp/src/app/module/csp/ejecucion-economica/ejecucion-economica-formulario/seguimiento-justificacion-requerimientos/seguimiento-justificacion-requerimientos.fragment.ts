import { IRequerimientoJustificacion } from '@core/models/csp/requerimiento-justificacion';
import { Fragment } from '@core/services/action-service';
import { ProyectoPeriodoJustificacionService } from '@core/services/csp/proyecto-periodo-justificacion/proyecto-periodo-justificacion.service';
import { ProyectoProyectoSgeService } from '@core/services/csp/proyecto-proyecto-sge.service';
import { ProyectoSeguimientoEjecucionEconomicaService } from '@core/services/csp/proyecto-seguimiento-ejecucion-economica/proyecto-seguimiento-ejecucion-economica.service';
import { RequerimientoJustificacionService } from '@core/services/csp/requerimiento-justificacion/requerimiento-justificacion.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, from, Observable, of } from 'rxjs';
import { concatMap, map, mergeMap, takeLast, tap, toArray } from 'rxjs/operators';

export class SeguimientoJustificacionRequerimientosFragment extends Fragment {
  private requerimientosJustificacion$ = new BehaviorSubject<StatusWrapper<IRequerimientoJustificacion>[]>([]);
  private requerimientosJustificacionEliminados: StatusWrapper<IRequerimientoJustificacion>[] = [];

  constructor(
    key: number,
    private readonly proyectoSeguimientoEjecucionEconomicaService: ProyectoSeguimientoEjecucionEconomicaService,
    private readonly requerimientoJustificacionService: RequerimientoJustificacionService,
    private readonly proyectoProyectoSgeService: ProyectoProyectoSgeService,
    private readonly proyectoPeriodoJustificacionService: ProyectoPeriodoJustificacionService
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    const proyectoSgeRef = this.getKey() as string;
    if (proyectoSgeRef) {
      this.proyectoSeguimientoEjecucionEconomicaService.findRequerimientosJustificacion(proyectoSgeRef).pipe(
        // De existir el requerimientoPrevio debe estar dentro del array
        map(({ items }) => items.map(item => ({
          ...item,
          requerimientoPrevio: this.findRequerimientoPrevio(item, items)
        }))),
        map((requerimientosJustificacion) => requerimientosJustificacion.map(item => new StatusWrapper(item))),
        mergeMap(requerimientosJustificacion =>
          from(requerimientosJustificacion).pipe(
            mergeMap(requerimientoJustificacion => this.fillProyectoProyectoSge(requerimientoJustificacion)),
            mergeMap(requerimientoJustificacion => this.fillPeriodoJustificacion(requerimientoJustificacion)),
            toArray()
          )
        )
      ).subscribe(requerimientosJustificacion => this.requerimientosJustificacion$.next(requerimientosJustificacion));
    }
  }

  private findRequerimientoPrevio(
    requerimientoJustificacion: IRequerimientoJustificacion,
    requerimientosJustificacionToFind: IRequerimientoJustificacion[]): IRequerimientoJustificacion {
    if (requerimientoJustificacion.requerimientoPrevio?.id) {
      return requerimientosJustificacionToFind.find(requerimientoJustificacionToFind =>
        requerimientoJustificacionToFind.id === requerimientoJustificacion.requerimientoPrevio.id);
    } else {
      return requerimientoJustificacion.requerimientoPrevio;
    }
  }

  private fillProyectoProyectoSge(requerimientoJustificacion: StatusWrapper<IRequerimientoJustificacion>):
    Observable<StatusWrapper<IRequerimientoJustificacion>> {
    if (requerimientoJustificacion.value.proyectoProyectoSge?.id) {
      return this.proyectoProyectoSgeService.findById(requerimientoJustificacion.value.proyectoProyectoSge.id).pipe(
        map(proyectoProyectoSge => {
          requerimientoJustificacion.value.proyectoProyectoSge = proyectoProyectoSge;
          return requerimientoJustificacion;
        })
      );
    } else {
      return of(requerimientoJustificacion);
    }
  }

  private fillPeriodoJustificacion(requerimientoJustificacion: StatusWrapper<IRequerimientoJustificacion>):
    Observable<StatusWrapper<IRequerimientoJustificacion>> {
    if (requerimientoJustificacion.value.proyectoPeriodoJustificacion?.id) {
      return this.proyectoPeriodoJustificacionService.findById(requerimientoJustificacion.value.proyectoPeriodoJustificacion.id).pipe(
        map(proyectoPeriodoJustificacion => {
          requerimientoJustificacion.value.proyectoPeriodoJustificacion = proyectoPeriodoJustificacion;
          return requerimientoJustificacion;
        })
      );
    } else {
      return of(requerimientoJustificacion);
    }
  }

  getRequerimientosJustificacion$(): Observable<StatusWrapper<IRequerimientoJustificacion>[]> {
    return this.requerimientosJustificacion$.asObservable();
  }

  public deleteRequerimiento(wrapper: StatusWrapper<IRequerimientoJustificacion>) {
    const current = this.requerimientosJustificacion$.value;
    const index = current.findIndex(
      (value) => value === wrapper
    );
    if (index >= 0) {
      if (!wrapper.created) {
        this.requerimientosJustificacionEliminados.push(current[index]);
      }
      current.splice(index, 1);
      this.requerimientosJustificacion$.next(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<void> {
    return this.deletePeriodoSeguimientos().pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private deletePeriodoSeguimientos(): Observable<void> {
    if (this.requerimientosJustificacionEliminados.length === 0) {
      return of(void 0);
    }
    return from(this.requerimientosJustificacionEliminados).pipe(
      tap(console.log),
      concatMap((wrapped) => {
        return this.requerimientoJustificacionService.deleteById(wrapped.value.id);
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    return this.requerimientosJustificacionEliminados.length > 0;
  }
}
