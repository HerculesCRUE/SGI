import { ISolicitudProyectoEntidad } from '@core/models/csp/solicitud-proyecto-entidad';
import { ISolicitudProyectoPresupuesto } from '@core/models/csp/solicitud-proyecto-presupuesto';
import { ISolicitudProyectoPresupuestoTotalConceptoGasto } from '@core/models/csp/solicitud-proyecto-presupuesto-total-concepto-gasto';
import { Fragment } from '@core/services/action-service';
import { SolicitudProyectoEntidadService } from '@core/services/csp/solicitud-proyecto-entidad/solicitud-proyecto-entidad.service';
import { SolicitudProyectoPresupuestoService } from '@core/services/csp/solicitud-proyecto-presupuesto.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export interface SolicitudProyectoPresupuestoListado {
  partidaGasto: StatusWrapper<ISolicitudProyectoPresupuesto>;
  importeSolicitadoPrevio: number;
  importeTotalSolicitadoConceptoGasto: number;
  importePresupuestadoPrevio: number;
  importeTotalPresupuestadoConceptoGasto: number;
  index: number;
}

export class SolicitudProyectoPresupuestoPartidasGastoFragment extends Fragment {

  partidasGastos$ = new BehaviorSubject<SolicitudProyectoPresupuestoListado[]>([]);
  private partidasGastosEliminadas: StatusWrapper<ISolicitudProyectoPresupuesto>[] = [];
  private solicitudProyectoPresupuestoTotalesConceptoGasto: ISolicitudProyectoPresupuestoTotalConceptoGasto[];
  public convocatoriaId: number;

  constructor(
    solicitudId: number,
    private solicitudProyectoEntidadId: number,
    private solicitudService: SolicitudService,
    private solicitudProyectoPresupuestoService: SolicitudProyectoPresupuestoService,
    private solicitudProyectoEntidadService: SolicitudProyectoEntidadService,
    public readonly: boolean
  ) {
    super(solicitudId);
  }

  protected onInitialize(): void {
    const key = this.getKey() as number;
    const subscription = this.solicitudService.findById(key).pipe(
      map(solicitud => {
        this.convocatoriaId = solicitud?.convocatoriaId;
      }),
      switchMap(() => {
        return this.solicitudService.findAllSolicitudProyectoPresupuestoTotalesConceptoGasto(key).pipe(
          map((result) => result.items),
          switchMap((solicitudProyectoPresupuestoTotalesConceptoGasto) => {
            this.solicitudProyectoPresupuestoTotalesConceptoGasto = solicitudProyectoPresupuestoTotalesConceptoGasto;

            return this.solicitudProyectoEntidadService
              .findAllSolicitudProyectoPresupuestoEntidadConvocatoria(this.solicitudProyectoEntidadId)
              .pipe(
                map((result) => result.items),
                switchMap((solicitudProyectoPresupuestos) =>
                  from(solicitudProyectoPresupuestos)
                    .pipe(
                      map(() => {
                        return solicitudProyectoPresupuestos
                          .map((element, index) => {
                            return {
                              partidaGasto: new StatusWrapper<ISolicitudProyectoPresupuesto>(element),
                              importeSolicitadoPrevio: element.importeSolicitado,
                              importeTotalSolicitadoConceptoGasto: solicitudProyectoPresupuestoTotalesConceptoGasto
                                .find(concepto => concepto.conceptoGasto.id === element.conceptoGasto.id)?.importeTotalSolicitado,
                              importeTotalPresupuestadoConceptoGasto: solicitudProyectoPresupuestoTotalesConceptoGasto
                                .find(concepto => concepto.conceptoGasto.id === element.conceptoGasto.id)?.importeTotalPresupuestado,
                              importePresupuestadoPrevio: element.importePresupuestado,
                              index
                            } as SolicitudProyectoPresupuestoListado;
                          });
                      })
                    )
                ),
                takeLast(1)
              );
          })
        );
      })
    ).subscribe(
      (solicitudProyectoPresupuestos) => {
        this.partidasGastos$.next(solicitudProyectoPresupuestos);
      }
    );
    this.subscriptions.push(subscription);
  }

  saveOrUpdate(): Observable<void> {
    return merge(
      this.deleteSolicitudProyectoPresupuestos(),
      this.updateSolicitudProyectoPresupuestos(),
      this.createSolicitudProyectoPresupuestos()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  /**
   * Elimina la partida de gasto y la marca como eliminada si ya existia previamente.
   *
   * @param wrapper ISolicitudProyectoPresupuesto
   */
  public deletePartidaGasto(wrapper: SolicitudProyectoPresupuestoListado): void {
    const current = this.partidasGastos$.value;
    const index = current.findIndex((value) => value.index === wrapper.index);
    if (index >= 0) {
      if (!wrapper.partidaGasto.created) {
        this.partidasGastosEliminadas.push(current[index].partidaGasto);
      }

      const solicitudProyectoPresupuestoTotalesConceptoGasto = this.solicitudProyectoPresupuestoTotalesConceptoGasto
        .find(concepto => concepto.conceptoGasto.id === wrapper.partidaGasto.value.conceptoGasto.id);
      solicitudProyectoPresupuestoTotalesConceptoGasto.importeTotalSolicitado -= wrapper.partidaGasto.value.importeSolicitado;
      solicitudProyectoPresupuestoTotalesConceptoGasto.importeTotalPresupuestado -= wrapper.partidaGasto.value.importePresupuestado;

      current
        .filter(value => value.partidaGasto.value.conceptoGasto.id === wrapper.partidaGasto.value.conceptoGasto.id)
        .map(value => {
          value.importeTotalSolicitadoConceptoGasto = solicitudProyectoPresupuestoTotalesConceptoGasto?.importeTotalSolicitado;
          value.importeTotalPresupuestadoConceptoGasto = solicitudProyectoPresupuestoTotalesConceptoGasto?.importeTotalPresupuestado;
        });

      current.splice(index, 1);
      this.partidasGastos$.next(current);
      this.setChanges(true);
    }

  }

  public addPartidaGasto(partidaGasto: ISolicitudProyectoPresupuesto) {
    partidaGasto.solicitudProyectoEntidad = { id: this.solicitudProyectoEntidadId } as ISolicitudProyectoEntidad;

    const wrapped = new StatusWrapper<ISolicitudProyectoPresupuesto>(partidaGasto);
    wrapped.setCreated();

    let solicitudProyectoPresupuestoTotalesConceptoGasto = this.solicitudProyectoPresupuestoTotalesConceptoGasto
      .find(concepto => concepto.conceptoGasto.id === partidaGasto.conceptoGasto.id);

    if (!solicitudProyectoPresupuestoTotalesConceptoGasto) {
      solicitudProyectoPresupuestoTotalesConceptoGasto = {
        conceptoGasto: partidaGasto.conceptoGasto,
        importeTotalSolicitado: 0,
        importeTotalPresupuestado: 0
      };

      this.solicitudProyectoPresupuestoTotalesConceptoGasto.push(solicitudProyectoPresupuestoTotalesConceptoGasto);
    }

    const importeTotalSolicitadoConceptoGasto =
      solicitudProyectoPresupuestoTotalesConceptoGasto?.importeTotalSolicitado + partidaGasto.importeSolicitado;
    const importeTotalPresupuestadoConceptoGasto =
      solicitudProyectoPresupuestoTotalesConceptoGasto?.importeTotalPresupuestado + partidaGasto.importePresupuestado;

    const current = this.partidasGastos$.value;
    const solicitudProyectoPresupuestoListado = {
      partidaGasto: wrapped,
      importeSolicitadoPrevio: partidaGasto.importeSolicitado,
      importeTotalSolicitadoConceptoGasto,
      importePresupuestadoPrevio: partidaGasto.importePresupuestado,
      importeTotalPresupuestadoConceptoGasto,
      index: current.reduce((prev, acu) => ((prev.index > acu.index) ? prev : acu), { index: 0 }).index + 1
    } as SolicitudProyectoPresupuestoListado;

    current.push(solicitudProyectoPresupuestoListado);
    this.partidasGastos$.next(current);
    solicitudProyectoPresupuestoTotalesConceptoGasto.importeTotalSolicitado = importeTotalSolicitadoConceptoGasto;
    solicitudProyectoPresupuestoTotalesConceptoGasto.importeTotalPresupuestado = importeTotalPresupuestadoConceptoGasto;
    this.setChanges(true);

    current
      .filter(value => value.partidaGasto.value.conceptoGasto.id === partidaGasto.conceptoGasto.id)
      .map(value => {
        value.importeTotalSolicitadoConceptoGasto = solicitudProyectoPresupuestoTotalesConceptoGasto?.importeTotalSolicitado;
        value.importeTotalPresupuestadoConceptoGasto = solicitudProyectoPresupuestoTotalesConceptoGasto?.importeTotalPresupuestado;
      });
  }

  public updateImporteTotalConceptoGasto(solicitudProyectoPresupuestoListado: SolicitudProyectoPresupuestoListado) {
    const current = this.partidasGastos$.value;
    const index = current.findIndex(value => value.index === solicitudProyectoPresupuestoListado.index);
    if (index >= 0) {
      const solicitudProyectoPresupuestoTotalesConceptoGasto = this.solicitudProyectoPresupuestoTotalesConceptoGasto
        .find(concepto => concepto.conceptoGasto.id === solicitudProyectoPresupuestoListado.partidaGasto.value.conceptoGasto.id);

      if (!solicitudProyectoPresupuestoTotalesConceptoGasto) {
        this.solicitudProyectoPresupuestoTotalesConceptoGasto.push({
          conceptoGasto: solicitudProyectoPresupuestoListado.partidaGasto.value.conceptoGasto,
          importeTotalSolicitado: 0,
          importeTotalPresupuestado: 0
        });
      }

      const importeTotalSolicitadoConceptoGasto = solicitudProyectoPresupuestoListado.partidaGasto.value.importeSolicitado
        + solicitudProyectoPresupuestoTotalesConceptoGasto?.importeTotalSolicitado
        - this.partidasGastos$.value[index].importeSolicitadoPrevio;

      const importeTotalPresupuestadoConceptoGasto = solicitudProyectoPresupuestoListado.partidaGasto.value.importePresupuestado
        + solicitudProyectoPresupuestoTotalesConceptoGasto?.importeTotalPresupuestado
        - this.partidasGastos$.value[index].importePresupuestadoPrevio;

      this.partidasGastos$.value[index].partidaGasto = solicitudProyectoPresupuestoListado.partidaGasto;
      this.partidasGastos$.value[index].importeSolicitadoPrevio = solicitudProyectoPresupuestoListado.partidaGasto.value.importeSolicitado;
      solicitudProyectoPresupuestoTotalesConceptoGasto.importeTotalSolicitado = importeTotalSolicitadoConceptoGasto;
      this.partidasGastos$.value[index].importePresupuestadoPrevio =
        solicitudProyectoPresupuestoListado.partidaGasto.value.importePresupuestado;
      solicitudProyectoPresupuestoTotalesConceptoGasto.importeTotalPresupuestado = importeTotalPresupuestadoConceptoGasto;
      this.setChanges(true);

      // Actualiza el importe total de todos las partidas de gasto con el mismo concepto de gasto
      current
        .filter(value =>
          value.partidaGasto.value.conceptoGasto.id === solicitudProyectoPresupuestoListado.partidaGasto.value.conceptoGasto.id
        )
        .map(value => {
          value.importeTotalSolicitadoConceptoGasto = importeTotalSolicitadoConceptoGasto;
          value.importeTotalPresupuestadoConceptoGasto = importeTotalPresupuestadoConceptoGasto;
        });
    }
  }

  public updatePartidaGasto(wrapper: StatusWrapper<ISolicitudProyectoPresupuesto>) {
    const current = this.partidasGastos$.value;
    const index = current.findIndex(value => value.partidaGasto.value.id === wrapper.value.id);
    if (index >= 0) {
      const solicitudProyectoPresupuestoTotalesConceptoGasto = this.solicitudProyectoPresupuestoTotalesConceptoGasto
        .find(concepto => concepto.conceptoGasto.id === wrapper.value.conceptoGasto.id);

      if (!solicitudProyectoPresupuestoTotalesConceptoGasto) {
        this.solicitudProyectoPresupuestoTotalesConceptoGasto.push({
          conceptoGasto: wrapper.value.conceptoGasto,
          importeTotalSolicitado: 0,
          importeTotalPresupuestado: 0
        });
      }

      const importeTotalSolicitadoConceptoGasto = wrapper.value.importeSolicitado
        + solicitudProyectoPresupuestoTotalesConceptoGasto?.importeTotalSolicitado
        - this.partidasGastos$.value[index].importeSolicitadoPrevio;

      const importeTotalPresupuestadoConceptoGasto = wrapper.value.importePresupuestado
        + solicitudProyectoPresupuestoTotalesConceptoGasto?.importeTotalPresupuestado
        - this.partidasGastos$.value[index].importePresupuestadoPrevio;

      this.partidasGastos$.value[index].partidaGasto = wrapper;
      this.partidasGastos$.value[index].importeSolicitadoPrevio = wrapper.value.importeSolicitado;
      solicitudProyectoPresupuestoTotalesConceptoGasto.importeTotalSolicitado = importeTotalSolicitadoConceptoGasto;
      this.partidasGastos$.value[index].importePresupuestadoPrevio = wrapper.value.importePresupuestado;
      solicitudProyectoPresupuestoTotalesConceptoGasto.importeTotalPresupuestado = importeTotalPresupuestadoConceptoGasto;

      wrapper.setEdited();
      this.setChanges(true);

      // Actualiza el importe total de todos las partidas de gasto con el mismo concepto de gasto
      current
        .filter(value => value.partidaGasto.value.conceptoGasto.id === wrapper.value.conceptoGasto.id)
        .map(value => {
          value.importeTotalSolicitadoConceptoGasto = importeTotalSolicitadoConceptoGasto;
          value.importeTotalPresupuestadoConceptoGasto = importeTotalPresupuestadoConceptoGasto;
        });
    }
  }

  /**
   * Elimina las partidas de gasto añadidas a partidasGastosEliminadas.
   */
  private deleteSolicitudProyectoPresupuestos(): Observable<void> {

    if (this.partidasGastosEliminadas.length === 0) {
      return of(void 0);
    }

    return from(this.partidasGastosEliminadas).pipe(
      mergeMap((wrapped) => {
        return this.solicitudProyectoPresupuestoService.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.partidasGastosEliminadas = this.partidasGastosEliminadas
                .filter(deletedEnlace => deletedEnlace.value.id !== wrapped.value.id);
            })
          );
      })
    );
  }

  /**
   * Actualiza las SolicitudProyectoPresupuesto modificadas.
   */
  private updateSolicitudProyectoPresupuestos(): Observable<void> {

    const updatedSolicitudProyectoPresupuestos = this.partidasGastos$.value.filter((wrapper) => wrapper.partidaGasto.edited);
    if (updatedSolicitudProyectoPresupuestos.length === 0) {
      return of(void 0);
    }

    return from(updatedSolicitudProyectoPresupuestos).pipe(
      mergeMap((wrapped) => {
        const solicitudProyectoPresupuesto = wrapped.partidaGasto.value;
        return this.solicitudProyectoPresupuestoService.update(solicitudProyectoPresupuesto.id, solicitudProyectoPresupuesto).pipe(
          map((updated) => {
            const index = this.partidasGastos$.value.findIndex((current) => current === wrapped);
            this.partidasGastos$.value[index].partidaGasto = new StatusWrapper<ISolicitudProyectoPresupuesto>(updated);
          })
        );
      })
    );
  }

  private createSolicitudProyectoPresupuestos(): Observable<void> {
    const createdSolicitudProyectoPresupuestos = this.partidasGastos$.value.filter(
      (solicitudProyectoPresupuesto) => solicitudProyectoPresupuesto.partidaGasto.created
    );
    if (createdSolicitudProyectoPresupuestos.length === 0) {
      return of(void 0);
    }

    return from(createdSolicitudProyectoPresupuestos).pipe(
      mergeMap((wrapped) => {
        const solicitudProyectoPresupuesto = wrapped.partidaGasto.value;
        solicitudProyectoPresupuesto.solicitudProyectoId = this.getKey() as number;
        return this.solicitudProyectoPresupuestoService.create(solicitudProyectoPresupuesto).pipe(
          map((updated) => {
            const index = this.partidasGastos$.value.findIndex((current) => current === wrapped);
            this.partidasGastos$.value[index].partidaGasto = new StatusWrapper<ISolicitudProyectoPresupuesto>(updated);
          })
        );
      }),
      takeLast(1)
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.partidasGastos$.value.some((wrapper) => wrapper.partidaGasto.touched);
    return !(this.partidasGastosEliminadas.length > 0 || touched);
  }

}
