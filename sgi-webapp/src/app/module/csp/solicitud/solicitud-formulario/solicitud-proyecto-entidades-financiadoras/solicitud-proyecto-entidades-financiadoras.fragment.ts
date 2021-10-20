import { ISolicitudProyectoEntidadFinanciadoraAjena } from '@core/models/csp/solicitud-proyecto-entidad-financiadora-ajena';
import { Fragment } from '@core/services/action-service';
import { SolicitudProyectoEntidadFinanciadoraAjenaService } from '@core/services/csp/solicitud-proyecto-entidad-financiadora-ajena.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { map, mergeAll, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export interface SolicitudProyectoEntidadFinanciadoraAjenaData extends ISolicitudProyectoEntidadFinanciadoraAjena {
  hasDesglosePresupuesto: boolean;
}

export class SolicitudProyectoEntidadesFinanciadorasFragment extends Fragment {
  entidadesFinanciadoras$ = new BehaviorSubject<StatusWrapper<SolicitudProyectoEntidadFinanciadoraAjenaData>[]>([]);
  private entidadesFinanciadorasEliminadas: StatusWrapper<SolicitudProyectoEntidadFinanciadoraAjenaData>[] = [];

  constructor(
    key: number,
    private solicitudService: SolicitudService,
    private solicitudProyectoEntidadFinanciadoraService: SolicitudProyectoEntidadFinanciadoraAjenaService,
    private empresaService: EmpresaService,
    private solicitudProyectoEntidadFinanciadoraAjenaService: SolicitudProyectoEntidadFinanciadoraAjenaService,
    public readonly: boolean
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    const id = this.getKey() as number;

    if (id) {
      const subscription = this.solicitudService.findAllSolicitudProyectoEntidadFinanciadora(id)
        .pipe(
          map(result => {
            return result.items.map((entidadFinanciadora) => {
              return new StatusWrapper<SolicitudProyectoEntidadFinanciadoraAjenaData>(
                entidadFinanciadora as SolicitudProyectoEntidadFinanciadoraAjenaData
              );
            });
          }),
          switchMap((entidadesFinanciadoras) => {
            return from(entidadesFinanciadoras)
              .pipe(
                map((entidadesFinanciadora) => {
                  return this.empresaService.findById(entidadesFinanciadora.value.empresa.id)
                    .pipe(
                      map(empresa => {
                        entidadesFinanciadora.value.empresa = empresa;
                        return entidadesFinanciadora;
                      }),
                    );

                }),
                mergeAll(),
                map(() => {
                  return entidadesFinanciadoras;
                })
              );
          }),
          switchMap((response) => {
            const requestsHasDesglosePresupuesto: Observable<StatusWrapper<SolicitudProyectoEntidadFinanciadoraAjenaData>>[] = [];
            response.forEach(solicitudProyectoEntidadFinanciadora => {
              requestsHasDesglosePresupuesto.push(
                this.solicitudProyectoEntidadFinanciadoraAjenaService
                  .hasSolicitudProyectoPresupuestoEntidad(solicitudProyectoEntidadFinanciadora.value.id)
                  .pipe(
                    map(hasDesglosePresupuesto => {
                      solicitudProyectoEntidadFinanciadora.value.hasDesglosePresupuesto = hasDesglosePresupuesto;
                      return solicitudProyectoEntidadFinanciadora;
                    })
                  ));
            });
            return of(response).pipe(
              tap(() => merge(...requestsHasDesglosePresupuesto).subscribe())
            );
          }),
          takeLast(1)
        ).subscribe(
          (result) => {
            this.entidadesFinanciadoras$.next(result);
          }
        );

      this.subscriptions.push(subscription);
    }
  }

  saveOrUpdate(): Observable<void> {
    return merge(
      this.deleteSolicitudProyectoEntidadFinanciadoras(),
      this.updateSolicitudProyectoEntidadFinanciadoras(),
      this.createSolicitudProyectoEntidadFinanciadoras()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
          this.entidadesFinanciadoras$.next(this.entidadesFinanciadoras$.value);
        }
      })
    );
  }

  public deleteSolicitudProyectoEntidadFinanciadora(wrapper: StatusWrapper<ISolicitudProyectoEntidadFinanciadoraAjena>) {
    const current = this.entidadesFinanciadoras$.value;
    const index = current.findIndex(
      (value) => value === wrapper
    );
    if (index >= 0) {
      if (!wrapper.created) {
        this.entidadesFinanciadorasEliminadas.push(current[index]);
      }
      current.splice(index, 1);
      this.entidadesFinanciadoras$.next(current);
      this.setChanges(true);
    }
  }

  public addSolicitudProyectoEntidadFinanciadora(entidadFinanciadora: SolicitudProyectoEntidadFinanciadoraAjenaData) {
    entidadFinanciadora.hasDesglosePresupuesto = false;
    const wrapped = new StatusWrapper<SolicitudProyectoEntidadFinanciadoraAjenaData>(entidadFinanciadora);
    wrapped.setCreated();
    const current = this.entidadesFinanciadoras$.value;
    current.push(wrapped);
    this.entidadesFinanciadoras$.next(current);
    this.setChanges(true);
  }

  public updateSolicitudProyectoEntidadFinanciadora(wrapper: StatusWrapper<SolicitudProyectoEntidadFinanciadoraAjenaData>) {
    const current = this.entidadesFinanciadoras$.value;
    if (wrapper.value.id) {
      const index = current.findIndex(value => value.value?.id === wrapper.value.id);
      if (index >= 0) {
        if (!wrapper.created) {
          wrapper.setEdited();
          this.setChanges(true);
        }

        this.entidadesFinanciadoras$.value[index] = wrapper;
      }
    }
    this.entidadesFinanciadoras$.next(current);
  }

  /**
   * Elimina las SolicitudProyectoEntidadFinanciadoraAjena de la lista de entidades a eliminar
   */
  private deleteSolicitudProyectoEntidadFinanciadoras(): Observable<void> {
    if (this.entidadesFinanciadorasEliminadas.length === 0) {
      return of(void 0);
    }

    return from(this.entidadesFinanciadorasEliminadas).pipe(
      mergeMap((wrapped) => {
        return this.solicitudProyectoEntidadFinanciadoraService.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.entidadesFinanciadorasEliminadas = this.entidadesFinanciadorasEliminadas.filter(deletedEntidadFinanciadora =>
                deletedEntidadFinanciadora.value.id !== wrapped.value.id);
            })
          );
      })
    );
  }

  /**
   * Actualiza las SolicitudProyectoEntidadFinanciadoraAjena modificadas.
   */
  private updateSolicitudProyectoEntidadFinanciadoras(): Observable<void> {
    const updatedEntidadesFinanciadoras = this.entidadesFinanciadoras$.value.filter((entidadFinanciadora) => entidadFinanciadora.edited);
    if (updatedEntidadesFinanciadoras.length === 0) {
      return of(void 0);
    }

    return from(updatedEntidadesFinanciadoras).pipe(
      mergeMap((wrapped) => {
        const entidadFinanciadora = wrapped.value;
        return this.solicitudProyectoEntidadFinanciadoraService.update(entidadFinanciadora.id, entidadFinanciadora).pipe(
          map((solicitudProyectoEntidadFinanciadora) => {
            const updated = solicitudProyectoEntidadFinanciadora as SolicitudProyectoEntidadFinanciadoraAjenaData;
            updated.empresa = wrapped.value.empresa;
            updated.hasDesglosePresupuesto = wrapped.value.hasDesglosePresupuesto;
            const index = this.entidadesFinanciadoras$.value.findIndex((current) => current === wrapped);
            this.entidadesFinanciadoras$.value[index] = new StatusWrapper<SolicitudProyectoEntidadFinanciadoraAjenaData>(
              updated as SolicitudProyectoEntidadFinanciadoraAjenaData
            );
          })
        );
      })
    );
  }

  private createSolicitudProyectoEntidadFinanciadoras(): Observable<void> {
    const createdEntidadesFinanciadoras = this.entidadesFinanciadoras$.value.filter((entidadFinanciadora) => entidadFinanciadora.created);
    if (createdEntidadesFinanciadoras.length === 0) {
      return of(void 0);
    }

    return from(createdEntidadesFinanciadoras).pipe(
      mergeMap((wrapped) => {
        const entidadFinanciadora = wrapped.value;
        entidadFinanciadora.solicitudProyectoId = this.getKey() as number;
        return this.solicitudProyectoEntidadFinanciadoraService.create(entidadFinanciadora).pipe(
          map((solicitudProyectoEntidadFinanciadora) => {
            const created = solicitudProyectoEntidadFinanciadora as SolicitudProyectoEntidadFinanciadoraAjenaData;
            created.empresa = wrapped.value.empresa;
            created.hasDesglosePresupuesto = wrapped.value.hasDesglosePresupuesto;
            const index = this.entidadesFinanciadoras$.value.findIndex((current) => current === wrapped);
            this.entidadesFinanciadoras$.value[index] = new StatusWrapper<SolicitudProyectoEntidadFinanciadoraAjenaData>(
              created as SolicitudProyectoEntidadFinanciadoraAjenaData
            );
            this.entidadesFinanciadoras$.next(this.entidadesFinanciadoras$.value);
          })
        );
      }),
      takeLast(1)
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.entidadesFinanciadoras$.value.some((wrapper) => wrapper.touched);
    return !(this.entidadesFinanciadorasEliminadas.length > 0 || touched);
  }

}
