import { ISolicitudProyectoPresupuesto } from '@core/models/csp/solicitud-proyecto-presupuesto';
import { ISolicitudProyectoPresupuestoTotalConceptoGasto } from '@core/models/csp/solicitud-proyecto-presupuesto-total-concepto-gasto';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { Fragment } from '@core/services/action-service';
import { SolicitudProyectoPresupuestoService } from '@core/services/csp/solicitud-proyecto-presupuesto.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export interface SolicitudProyectoPresupuestoListado {
  partidaGasto: StatusWrapper<ISolicitudProyectoPresupuesto>;
  importeSolicitadoPrevio: number;
  importeTotalConceptoGasto: number;
  index: number;
}

export class SolicitudProyectoPresupuestoPartidasGastoFragment extends Fragment {

  partidasGastos$ = new BehaviorSubject<SolicitudProyectoPresupuestoListado[]>([]);
  private partidasGastosEliminadas: StatusWrapper<ISolicitudProyectoPresupuesto>[] = [];
  private solicitudProyectoPresupuestoTotalesConceptoGasto: ISolicitudProyectoPresupuestoTotalConceptoGasto[];
  public convocatoriaId: number;

  constructor(
    solicitudId: number,
    private empresa: IEmpresa,
    private readonly ajena: boolean,
    private solicitudService: SolicitudService,
    private solicitudProyectoPresupuestoService: SolicitudProyectoPresupuestoService,
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

            const observable$ = !this.ajena ?
              this.solicitudService.findAllSolicitudProyectoPresupuestoEntidadConvocatoria(key, this.empresa.id) :
              this.solicitudService.findAllSolicitudProyectoPresupuestoEntidadAjena(key, this.empresa.id);
            return observable$
              .pipe(
                map((result) => result.items),
                switchMap((solicitudProyectoPresupuestos) =>
                  from(solicitudProyectoPresupuestos)
                    .pipe(
                      map(() => {
                        return solicitudProyectoPresupuestos
                          .map((element, index) => {
                            element.empresa = this.empresa;

                            return {
                              partidaGasto: new StatusWrapper<ISolicitudProyectoPresupuesto>(element),
                              importeSolicitadoPrevio: element.importeSolicitado,
                              importeTotalConceptoGasto: solicitudProyectoPresupuestoTotalesConceptoGasto
                                .find(concepto => concepto.conceptoGasto.id === element.conceptoGasto.id)?.importeTotal,
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
      solicitudProyectoPresupuestoTotalesConceptoGasto.importeTotal -= wrapper.partidaGasto.value.importeSolicitado;

      current
        .filter(value => value.partidaGasto.value.conceptoGasto.id === wrapper.partidaGasto.value.conceptoGasto.id)
        .map(value => {
          value.importeTotalConceptoGasto = solicitudProyectoPresupuestoTotalesConceptoGasto?.importeTotal;
        });

      current.splice(index, 1);
      this.partidasGastos$.next(current);
      this.setChanges(true);
    }

  }

  public addPartidaGasto(partidaGasto: ISolicitudProyectoPresupuesto) {
    partidaGasto.empresa = this.empresa;
    partidaGasto.financiacionAjena = this.ajena;

    const wrapped = new StatusWrapper<ISolicitudProyectoPresupuesto>(partidaGasto);
    wrapped.setCreated();

    let solicitudProyectoPresupuestoTotalesConceptoGasto = this.solicitudProyectoPresupuestoTotalesConceptoGasto
      .find(concepto => concepto.conceptoGasto.id === partidaGasto.conceptoGasto.id);

    if (!solicitudProyectoPresupuestoTotalesConceptoGasto) {
      solicitudProyectoPresupuestoTotalesConceptoGasto = {
        conceptoGasto: partidaGasto.conceptoGasto,
        importeTotal: 0
      };

      this.solicitudProyectoPresupuestoTotalesConceptoGasto.push(solicitudProyectoPresupuestoTotalesConceptoGasto);
    }

    const importeTotalconceptoGasto = solicitudProyectoPresupuestoTotalesConceptoGasto?.importeTotal + partidaGasto.importeSolicitado;

    const current = this.partidasGastos$.value;
    const solicitudProyectoPresupuestoListado = {
      partidaGasto: wrapped,
      importeSolicitadoPrevio: partidaGasto.importeSolicitado,
      importeTotalConceptoGasto: importeTotalconceptoGasto,
      index: current.reduce((prev, acu) => ((prev.index > acu.index) ? prev : acu), { index: 0 }).index + 1
    } as SolicitudProyectoPresupuestoListado;

    current.push(solicitudProyectoPresupuestoListado);
    this.partidasGastos$.next(current);
    solicitudProyectoPresupuestoTotalesConceptoGasto.importeTotal = importeTotalconceptoGasto;
    this.setChanges(true);

    current
      .filter(value => value.partidaGasto.value.conceptoGasto.id === partidaGasto.conceptoGasto.id)
      .map(value => {
        value.importeTotalConceptoGasto = solicitudProyectoPresupuestoTotalesConceptoGasto?.importeTotal;
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
          importeTotal: 0
        });
      }

      const importeTotalconceptoGasto = solicitudProyectoPresupuestoListado.partidaGasto.value.importeSolicitado
        + solicitudProyectoPresupuestoTotalesConceptoGasto?.importeTotal
        - this.partidasGastos$.value[index].importeSolicitadoPrevio;

      this.partidasGastos$.value[index].partidaGasto = solicitudProyectoPresupuestoListado.partidaGasto;
      this.partidasGastos$.value[index].importeSolicitadoPrevio = solicitudProyectoPresupuestoListado.partidaGasto.value.importeSolicitado;
      solicitudProyectoPresupuestoTotalesConceptoGasto.importeTotal = importeTotalconceptoGasto;
      this.setChanges(true);

      // Actualiza el importe total de todos las partidas de gasto con el mismo concepto de gasto
      current
        .filter(value =>
          value.partidaGasto.value.conceptoGasto.id === solicitudProyectoPresupuestoListado.partidaGasto.value.conceptoGasto.id
        )
        .map(value => {
          value.importeTotalConceptoGasto = importeTotalconceptoGasto;
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
          importeTotal: 0
        });
      }

      const importeTotalconceptoGasto = wrapper.value.importeSolicitado
        + solicitudProyectoPresupuestoTotalesConceptoGasto?.importeTotal
        - this.partidasGastos$.value[index].importeSolicitadoPrevio;

      this.partidasGastos$.value[index].partidaGasto = wrapper;
      this.partidasGastos$.value[index].importeSolicitadoPrevio = wrapper.value.importeSolicitado;
      solicitudProyectoPresupuestoTotalesConceptoGasto.importeTotal = importeTotalconceptoGasto;

      wrapper.setEdited();
      this.setChanges(true);

      // Actualiza el importe total de todos las partidas de gasto con el mismo concepto de gasto
      current
        .filter(value => value.partidaGasto.value.conceptoGasto.id === wrapper.value.conceptoGasto.id)
        .map(value => {
          value.importeTotalConceptoGasto = importeTotalconceptoGasto;
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
