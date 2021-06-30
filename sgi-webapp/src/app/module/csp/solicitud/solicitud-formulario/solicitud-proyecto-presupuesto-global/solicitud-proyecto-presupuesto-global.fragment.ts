import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ISolicitudProyecto } from '@core/models/csp/solicitud-proyecto';
import { ISolicitudProyectoPresupuesto } from '@core/models/csp/solicitud-proyecto-presupuesto';
import { FormFragment } from '@core/services/action-service';
import { SolicitudProyectoPresupuestoService } from '@core/services/csp/solicitud-proyecto-presupuesto.service';
import { SolicitudProyectoService } from '@core/services/csp/solicitud-proyecto.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, EMPTY, from, merge, Observable, of } from 'rxjs';
import { catchError, map, mergeAll, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export class SolicitudProyectoPresupuestoGlobalFragment extends FormFragment<ISolicitudProyecto> {
  partidasGastos$ = new BehaviorSubject<StatusWrapper<ISolicitudProyectoPresupuesto>[]>([]);
  private partidasGastosEliminadas: StatusWrapper<ISolicitudProyectoPresupuesto>[] = [];

  private solicitudProyecto: ISolicitudProyecto;

  constructor(
    key: number,
    private solicitudService: SolicitudService,
    private solicitudProyectoPresupuestoService: SolicitudProyectoPresupuestoService,
    private empresaService: EmpresaService,
    private solicitudProyectoService: SolicitudProyectoService,
    public readonly: boolean
  ) {
    super(key, true);
    this.setComplete(true);
  }

  protected buildFormGroup(): FormGroup {
    const form = new FormGroup({
      importePresupuestado: new FormControl('', [
        Validators.min(0),
        Validators.max(2_147_483_647)
      ]),
      importeSolicitado: new FormControl('', [
        Validators.min(0),
        Validators.max(2_147_483_647)
      ]),
      importePresupuestadoSocios: new FormControl('', [
        Validators.min(0),
        Validators.max(2_147_483_647)
      ]),
      importeSolicitadoSocios: new FormControl('', [
        Validators.min(0),
        Validators.max(2_147_483_647)
      ]),
      totalImportePresupuestado: new FormControl('', [
        Validators.min(0),
        Validators.max(2_147_483_647)
      ]),
      totalImporteSolicitado: new FormControl('', [
        Validators.min(0),
        Validators.max(2_147_483_647)
      ])
    });

    if (this.readonly) {
      form.disable();
    }

    return form;
  }

  protected buildPatch(value: ISolicitudProyecto): { [key: string]: any; } {
    this.solicitudProyecto = value;
    const result = {
      importePresupuestado: value.importePresupuestado,
      importePresupuestadoSocios: value.importePresupuestadoSocios,
      importeSolicitado: value.importeSolicitado,
      importeSolicitadoSocios: value.importeSolicitadoSocios,
      totalImportePresupuestado: value.totalImportePresupuestado,
      totalImporteSolicitado: value.totalImporteSolicitado
    } as ISolicitudProyecto;
    return result;
  }

  protected initializer(key: number): Observable<ISolicitudProyecto> {
    const solicitudId = this.getKey() as number;
    if (solicitudId) {
      const subscription = this.solicitudService.findAllSolicitudProyectoPresupuesto(solicitudId).pipe(
        switchMap((solicitudProyectoPresupuestos) =>
          from(solicitudProyectoPresupuestos.items)
            .pipe(
              map((solicitudProyectoPresupuesto) => {
                if (solicitudProyectoPresupuesto.empresa.id) {
                  return this.empresaService.findById(solicitudProyectoPresupuesto.empresa.id)
                    .pipe(
                      tap(empresa => solicitudProyectoPresupuesto.empresa = empresa),
                      catchError(() => of(null))
                    );
                } else {
                  return of(solicitudProyectoPresupuesto);
                }
              }),
              mergeAll(),
              map(() => {
                return solicitudProyectoPresupuestos.items
                  .map(element => new StatusWrapper<ISolicitudProyectoPresupuesto>(element));
              })
            )
        ),
        takeLast(1)
      ).subscribe(
        (solicitudProyectoPresupuestos) => {
          this.partidasGastos$.next(solicitudProyectoPresupuestos);
        }
      );
      this.subscriptions.push(subscription);
    }

    return this.solicitudService.findSolicitudProyecto(key).pipe(
      map(response => {
        return this.solicitudProyecto = response;
      }),
      catchError(() => {
        return EMPTY;
      })
    );
  }

  getValue(): ISolicitudProyecto {
    if (this.solicitudProyecto === null) {
      this.solicitudProyecto = {} as ISolicitudProyecto;
    }
    const form = this.getFormGroup().value;
    this.solicitudProyecto.importePresupuestado = form.importePresupuestado;
    this.solicitudProyecto.importePresupuestadoSocios = form.importePresupuestadoSocios;
    this.solicitudProyecto.importeSolicitado = form.importeSolicitado;
    this.solicitudProyecto.importeSolicitadoSocios = form.importeSolicitadoSocios;
    this.solicitudProyecto.totalImportePresupuestado = form.totalImportePresupuestado;
    this.solicitudProyecto.totalImporteSolicitado = form.totalImporteSolicitado;
    return this.solicitudProyecto;
  }

  saveOrUpdate(): Observable<void> {
    return merge(
      this.deleteSolicitudProyectoPresupuestos(),
      this.updateSolicitudProyectoPresupuestos(),
      this.createSolicitudProyectoPresupuestos(),
      this.updateSolicitudProyecto()
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
  public deletePartidaGasto(wrapper: StatusWrapper<ISolicitudProyectoPresupuesto>): void {
    const current = this.partidasGastos$.value;
    const index = current.findIndex((value) => value === wrapper);
    if (index >= 0) {
      if (!wrapper.created) {
        this.partidasGastosEliminadas.push(current[index]);
      }
      current.splice(index, 1);
      this.partidasGastos$.next(current);
      this.setChanges(true);
    }
  }

  public addPartidaGasto(partidaGasto: ISolicitudProyectoPresupuesto) {
    const wrapped = new StatusWrapper<ISolicitudProyectoPresupuesto>(partidaGasto);
    wrapped.setCreated();
    const current = this.partidasGastos$.value;
    current.push(wrapped);
    this.partidasGastos$.next(current);
    this.setChanges(true);
  }

  public updatePartidaGasto(wrapper: StatusWrapper<ISolicitudProyectoPresupuesto>) {
    const current = this.partidasGastos$.value;
    const index = current.findIndex(value => value.value.id === wrapper.value.id);
    if (index >= 0) {
      wrapper.setEdited();
      this.partidasGastos$.value[index] = wrapper;
      this.setChanges(true);
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
   * Actualiza las SolicitudProyecto modificadas.
   */
  private updateSolicitudProyecto(): Observable<void> {
    this.getValue();
    if (!this.solicitudProyecto) {
      return of(void 0);
    }

    return this.solicitudProyectoService.update(this.solicitudProyecto.id, this.solicitudProyecto).pipe(
      catchError(() => {
        return of(void 0);
      })
    );
  }

  /**
   * Actualiza las SolicitudProyectoPresupuesto modificadas.
   */
  private updateSolicitudProyectoPresupuestos(): Observable<void> {
    const updatedSolicitudProyectoPresupuestos = this.partidasGastos$.value.filter((wrapper) => wrapper.edited);
    if (updatedSolicitudProyectoPresupuestos.length === 0) {
      return of(void 0);
    }

    return from(updatedSolicitudProyectoPresupuestos).pipe(
      mergeMap((wrapped) => {
        const solicitudProyectoPresupuesto = wrapped.value;
        return this.solicitudProyectoPresupuestoService.update(solicitudProyectoPresupuesto.id, solicitudProyectoPresupuesto).pipe(
          map((updated) => {
            const index = this.partidasGastos$.value.findIndex((current) => current === wrapped);
            this.partidasGastos$.value[index] = new StatusWrapper<ISolicitudProyectoPresupuesto>(updated);
          })
        );
      })
    );
  }

  private createSolicitudProyectoPresupuestos(): Observable<void> {
    const createdSolicitudProyectoPresupuestos = this.partidasGastos$.value.filter((solicitudProyectoPresupuesto) =>
      solicitudProyectoPresupuesto.created);
    if (createdSolicitudProyectoPresupuestos.length === 0) {
      return of(void 0);
    }

    return from(createdSolicitudProyectoPresupuestos).pipe(
      mergeMap((wrapped) => {
        const solicitudProyectoPresupuesto = wrapped.value;
        solicitudProyectoPresupuesto.solicitudProyectoId = this.getKey() as number;
        return this.solicitudProyectoPresupuestoService.create(solicitudProyectoPresupuesto).pipe(
          map((updated) => {
            const index = this.partidasGastos$.value.findIndex((current) => current === wrapped);
            this.partidasGastos$.value[index] = new StatusWrapper<ISolicitudProyectoPresupuesto>(updated);
          })
        );
      }),
      takeLast(1)
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.partidasGastos$.value.some((wrapper) => wrapper.touched);
    return (this.partidasGastosEliminadas.length > 0 || touched);
  }

}
