import { IRequerimientoJustificacion } from '@core/models/csp/requerimiento-justificacion';
import { Fragment } from '@core/services/action-service';
import { ProyectoPeriodoJustificacionService } from '@core/services/csp/proyecto-periodo-justificacion/proyecto-periodo-justificacion.service';
import { ProyectoProyectoSgeService } from '@core/services/csp/proyecto-proyecto-sge.service';
import { ProyectoSeguimientoEjecucionEconomicaService } from '@core/services/csp/proyecto-seguimiento-ejecucion-economica/proyecto-seguimiento-ejecucion-economica.service';
import { RequerimientoJustificacionService } from '@core/services/csp/requerimiento-justificacion/requerimiento-justificacion.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { DateTime } from 'luxon';
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
        map(({ items }) => items.map(item => {
          // De existir el requerimientoPrevio debe estar dentro del array
          item.requerimientoPrevio = this.findRequerimientoPrevio(item, items);
          return new StatusWrapper(item);
        })),
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
    requerimientosJustificacionToFind: IRequerimientoJustificacion[]): IRequerimientoJustificacion | undefined {
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
      this.recalcularNumRequerimientos(current);
      this.requerimientosJustificacion$.next(current);
      this.setChanges(true);
    }
  }

  private recalcularNumRequerimientos(current: StatusWrapper<IRequerimientoJustificacion>[]) {
    let previousId: number;
    let numRequerimiento = 0;
    [...current].sort((a, b) =>
      a.value.proyectoProyectoSge.id - b.value.proyectoProyectoSge.id !== 0 ?
        a.value.proyectoProyectoSge.id - b.value.proyectoProyectoSge.id :
        this.getDateInMillis(a.value.fechaNotificacion) - this.getDateInMillis(b.value.fechaNotificacion)
    ).forEach((wrapper) => {
      if (wrapper.value.proyectoProyectoSge.id !== previousId) {
        numRequerimiento = 0;
      }
      previousId = wrapper.value.proyectoProyectoSge.id;
      numRequerimiento += 1;
      wrapper.value.numRequerimiento = numRequerimiento;
    });
  }

  private getDateInMillis(date: DateTime | undefined | null): number {
    return date?.toMillis() ?? 0;
  }

  saveOrUpdate(): Observable<void> {
    return this.deleteRequerimientos().pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private deleteRequerimientos(): Observable<void> {
    if (this.requerimientosJustificacionEliminados.length === 0) {
      return of(void 0);
    }
    return from(this.requerimientosJustificacionEliminados).pipe(
      concatMap((wrapper) => {
        return this.deleteRequerimientoById(wrapper);
      })
    );
  }

  private deleteRequerimientoById(wrapper: StatusWrapper<IRequerimientoJustificacion>): Observable<void> {
    return this.requerimientoJustificacionService.deleteById(wrapper.value.id)
      .pipe(
        tap(() => this.requerimientosJustificacionEliminados = this.requerimientosJustificacionEliminados.filter(
          entidadEliminada => entidadEliminada.value.id !== wrapper.value.id))
      );
  }

  private isSaveOrUpdateComplete(): boolean {
    return this.requerimientosJustificacionEliminados.length > 0;
  }
}
