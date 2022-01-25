import { FormGroup } from '@angular/forms';
import { IProyectoPeriodoAmortizacion } from '@core/models/csp/proyecto-periodo-amortizacion';
import { IProyectoProyectoSge } from '@core/models/csp/proyecto-proyecto-sge';
import { Fragment } from '@core/services/action-service';
import { ProyectoAnualidadService } from '@core/services/csp/proyecto-anualidad/proyecto-anualidad.service';
import { ProyectoEntidadFinanciadoraService } from '@core/services/csp/proyecto-entidad-financiadora.service';
import { ProyectoPeriodoAmortizacionService } from '@core/services/csp/proyecto-periodo-amortizacion/proyecto-periodo-amortizacion.service';
import { PeriodoAmortizacionService } from '@core/services/sge/periodo-amortizacion/periodo-amortizacion.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { SgiRestListResult } from '@sgi/framework/http';
import { BehaviorSubject, EMPTY, from, merge, Observable, of } from 'rxjs';
import { catchError, map, mergeMap, switchMap, takeLast, tap, toArray } from 'rxjs/operators';
import { IEntidadFinanciadora } from '../proyecto-entidades-financiadoras/proyecto-entidades-financiadoras.fragment';

export interface IProyectoPeriodoAmortizacionListado extends IProyectoPeriodoAmortizacion {
  periodo: number;
}

export class ProyectoAmortizacionFondosFragment extends Fragment {

  entidadesFinanciadoras$ = new BehaviorSubject<StatusWrapper<IEntidadFinanciadora>[]>([]);
  periodosAmortizacion$ = new BehaviorSubject<StatusWrapper<IProyectoPeriodoAmortizacionListado>[]>([]);
  proyectosSGE$ = new BehaviorSubject<IProyectoProyectoSge[]>([]);
  private periodosAmortizacionEliminados: StatusWrapper<IProyectoPeriodoAmortizacionListado>[] = [];
  hasProyectoSGE = false;

  constructor(
    key: number,
    public readonly solicitudId: number,
    private proyectoPeriodoAmortizacionService: ProyectoPeriodoAmortizacionService,
    private proyectoEntidadFinanciadoraService: ProyectoEntidadFinanciadoraService,
    private empresaService: EmpresaService,
    private proyectoAnualidadService: ProyectoAnualidadService,
    private periodoAmortizacionService: PeriodoAmortizacionService
  ) {
    super(key);
    this.setComplete(true);
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup({
    });
  }

  protected onInitialize(): void {
    const key = this.getKey() as number;
    if (key) {
      const subscription =
        this.proyectosSGE$.pipe(
          map((response) => {
            return response.map(item => item.proyectoSge.id);
          }),
          switchMap(listadoProyectosSGE => {
            if (listadoProyectosSGE.length < 1) {
              this.hasProyectoSGE = false;
              return of(void 0);
            }
            this.hasProyectoSGE = true;
            if (this.isInitialized() && this.periodosAmortizacion$.value.length > 0) {
              return of(void 0);
            }
            return this.proyectoPeriodoAmortizacionService.findByproyectoSGERef(listadoProyectosSGE).pipe(
              map((response: SgiRestListResult<IProyectoPeriodoAmortizacionListado>) => {
                return response.items.map(periodo => new StatusWrapper<IProyectoPeriodoAmortizacionListado>(periodo));
              }),
              tap((value) => {
                this.periodosAmortizacion$.next(value);
              }),
              mergeMap(periodosAmortizacion =>
                from(periodosAmortizacion).pipe(
                  mergeMap(periodoAmortizacion => {
                    return this.proyectoEntidadFinanciadoraService.findById(periodoAmortizacion.value.proyectoEntidadFinanciadora.id).pipe(
                      map((proyectoEntidadFinanciadora) => {
                        periodoAmortizacion.value.proyectoEntidadFinanciadora = proyectoEntidadFinanciadora;
                        return periodoAmortizacion;
                      }),
                      switchMap(() =>
                        this.empresaService.findById(periodoAmortizacion.value.proyectoEntidadFinanciadora.empresa.id).pipe(
                          map((empresa) => {
                            periodoAmortizacion.value.proyectoEntidadFinanciadora.empresa = empresa;
                            return periodoAmortizacion;
                          }))
                      )
                    );
                  }),
                  mergeMap(periodoAmortizacion => {
                    return this.proyectoAnualidadService.findById(periodoAmortizacion.value.proyectoAnualidad.id).pipe(
                      map((proyectoAnualidad) => {
                        periodoAmortizacion.value.proyectoAnualidad = proyectoAnualidad;
                        return periodoAmortizacion;
                      })
                    );
                  })
                )
              ),
              toArray(),
              catchError((error) => EMPTY)
            )
          }),
        ).subscribe(() => {
          this.recalcularNumPeriodos();
        });
      this.subscriptions.push(subscription);
    }
  }

  public deletePeriodoAmortizacion(wrapper: StatusWrapper<IProyectoPeriodoAmortizacion>) {
    const current = this.periodosAmortizacion$.value;
    const index = current.findIndex(value => value.value.id === wrapper.value.id);
    if (index >= 0) {
      if (!wrapper.created) {
        this.periodosAmortizacionEliminados.push(current[index]);
      }
      current.splice(index, 1);
      this.periodosAmortizacion$.next(current);
      this.setChanges(true);
    }
  }

  public updatePeriodoAmortizacion(wrapper: StatusWrapper<IProyectoPeriodoAmortizacionListado>) {
    const current = this.periodosAmortizacion$.value;
    const index = current.findIndex(value => value.value.id === wrapper.value.id);
    if (index >= 0) {
      wrapper.setEdited();
      this.periodosAmortizacion$.value[index] = wrapper;
      this.setChanges(true);
    }
  }

  public addPeriodoAmortizacion(periodoAmortizacion: IProyectoPeriodoAmortizacionListado) {
    const wrapped = new StatusWrapper<IProyectoPeriodoAmortizacionListado>(periodoAmortizacion);
    wrapped.setCreated();
    const current = this.periodosAmortizacion$.value;
    current.push(wrapped);
    this.periodosAmortizacion$.next(current);
    this.setChanges(true);
  }

  saveOrUpdate(): Observable<void> {
    return merge(
      this.deleteProyectoPeriodosAmortizacion(),
      this.updateProyectoPeriodosAmortizacion(this.periodosAmortizacion$),
      this.createProyectoPeriodosAmortizacion(this.periodosAmortizacion$),
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private deleteProyectoPeriodosAmortizacion(): Observable<void> {
    const deleted = this.periodosAmortizacionEliminados.filter((value) => value.value.id);
    if (deleted.length === 0) {
      return of(void 0);
    }
    return from(deleted).pipe(
      mergeMap((wrapped) => {
        return this.proyectoPeriodoAmortizacionService.deleteById(wrapped.value.id)
          .pipe(
            switchMap(() =>
              this.periodoAmortizacionService.deleteById(wrapped.value.id).pipe(
                switchMap(() => of(void 0))
              )
            ),
            tap(() => {
              this.periodosAmortizacionEliminados = deleted.filter(periodo => periodo.value.id !== wrapped.value.id);
            })
          );
      })
    );
  }

  private updateProyectoPeriodosAmortizacion(target$: BehaviorSubject<StatusWrapper<IProyectoPeriodoAmortizacion>[]>): Observable<void> {
    const edited = target$.value.filter((value) => value.edited);
    if (edited.length === 0) {
      return of(void 0);
    }
    return from(edited).pipe(
      mergeMap((wrapped) => {
        return this.proyectoPeriodoAmortizacionService.update(wrapped.value.id, wrapped.value).pipe(
          switchMap(() =>
            this.periodoAmortizacionService.update(wrapped.value.id, wrapped.value).pipe(
              switchMap(() => of(void 0))
            )
          )
        );
      })
    );
  }

  private createProyectoPeriodosAmortizacion(target$: BehaviorSubject<StatusWrapper<IProyectoPeriodoAmortizacionListado>[]>): Observable<void> {
    const created = target$.value.filter((value) => value.created);
    if (created.length === 0) {
      return of(void 0);
    }

    return from(created).pipe(
      mergeMap((wrapped) => {
        return this.proyectoPeriodoAmortizacionService.create(wrapped.value).pipe(
          map((createdproyectoPeriodoAmortizacion: IProyectoPeriodoAmortizacion) => {
            const index = target$.value.findIndex((current) => current === wrapped);
            const proyectoPeriodoAmortizacionListado = wrapped.value;
            proyectoPeriodoAmortizacionListado.id = createdproyectoPeriodoAmortizacion.id;
            target$.value[index] = new StatusWrapper<IProyectoPeriodoAmortizacionListado>(proyectoPeriodoAmortizacionListado);
            target$.next(target$.value);
            return proyectoPeriodoAmortizacionListado;
          }),
          switchMap((proyectoPeriodoAmortizacionListado) =>
            this.periodoAmortizacionService.create(proyectoPeriodoAmortizacionListado).pipe(
              switchMap(() => of(void 0))
            )
          )
        );
      }));
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched = this.periodosAmortizacion$.value.some((wrapper) => wrapper.touched);
    return !(this.periodosAmortizacionEliminados.length > 0 || touched);
  }

  /**
 * Recalcula los numeros de los periodos de todos los periodos de amortizacion de la tabla en funcion de su anualidad y entidad.
 */
  public recalcularNumPeriodos(): void {
    let numPeriodo = 1;
    let nombreActual: string;
    this.periodosAmortizacion$.value
      .sort((a, b) => {
        return a.value.proyectoEntidadFinanciadora.empresa.nombre.localeCompare(b.value.proyectoEntidadFinanciadora.empresa.nombre)
          || a.value.proyectoAnualidad.anio.toString().localeCompare(b.value.proyectoAnualidad.anio.toString())
      });

    this.periodosAmortizacion$.value.forEach(c => {
      if (c.value.proyectoEntidadFinanciadora.empresa.nombre !== nombreActual) {
        numPeriodo = 1;
        nombreActual = c.value.proyectoEntidadFinanciadora.empresa.nombre;
      }
      c.value.periodo = numPeriodo++;
    });

    this.periodosAmortizacion$.next(this.periodosAmortizacion$.value);
  }

}
