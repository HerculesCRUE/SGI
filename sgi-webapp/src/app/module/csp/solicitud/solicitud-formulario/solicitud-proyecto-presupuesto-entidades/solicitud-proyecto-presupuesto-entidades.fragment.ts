import { FormControl, FormGroup, Validators } from '@angular/forms';
import { IEntidad } from '@core/models/csp/entidad';
import { ISolicitudProyecto, TipoPresupuesto } from '@core/models/csp/solicitud-proyecto';
import { FormFragment } from '@core/services/action-service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { SolicitudProyectoService } from '@core/services/csp/solicitud-proyecto.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { BehaviorSubject, EMPTY, from, merge, Observable, of, Subject } from 'rxjs';
import { catchError, map, mergeAll, switchMap, takeLast, tap } from 'rxjs/operators';

export interface EntidadFinanciadoraDesglosePresupuesto {
  entidadFinanciadora: IEntidad;
  ajena: boolean;
}

export class SolicitudProyectoPresupuestoEntidadesFragment extends FormFragment<ISolicitudProyecto> {

  entidadesFinanciadoras$ = new BehaviorSubject<EntidadFinanciadoraDesglosePresupuesto[]>([]);
  private readonly solicitudId: number;
  tipoPresupuestoMixto: boolean;
  tipoPresupuesto$: Subject<TipoPresupuesto> = new BehaviorSubject<TipoPresupuesto>(null);

  private solicitudProyecto: ISolicitudProyecto;

  constructor(
    key: number,
    public readonly convocatoriaId: number,
    private convocatoriaService: ConvocatoriaService,
    private solicitudService: SolicitudService,
    private empresaService: EmpresaService,
    private solicitudProyectoService: SolicitudProyectoService,
    public readonly readonly: boolean
  ) {
    super(key, true);
    this.setComplete(true);
    this.solicitudId = key;
  }

  protected initializer(key: number): Observable<ISolicitudProyecto> {
    this.tipoPresupuesto$.subscribe(tipoPresupuesto => {
      this.tipoPresupuestoMixto = tipoPresupuesto === TipoPresupuesto.MIXTO;
      this.loadEntidadesFinanciadoras(tipoPresupuesto);
    });

    return this.solicitudService.findSolicitudProyecto(key).pipe(
      map(response => {
        return this.solicitudProyecto = response;
      }),
      catchError(() => {
        return EMPTY;
      })
    );
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
      this.updateSolicitudProyecto()
    ).pipe(
      takeLast(1),
      tap(() => {
        this.setChanges(false);
      })
    );
  }

  private loadEntidadesFinanciadoras(tipoPresupuesto: TipoPresupuesto): void {
    let entidades: EntidadFinanciadoraDesglosePresupuesto[] = [];
    let entidades$: Observable<EntidadFinanciadoraDesglosePresupuesto[]>;

    switch (tipoPresupuesto) {
      case TipoPresupuesto.INDIVIDUAL:
        entidades$ = merge(
          this.loadEntidadFinanciadoraConvocatoria(this.convocatoriaId),
          this.loadEntidadFinanciadoraSolicitud(this.solicitudId)
        );
        break;
      case TipoPresupuesto.MIXTO:
        entidades$ = merge(
          this.loadEntidadGestoraConvocatoria(this.convocatoriaId),
          this.loadEntidadFinanciadoraSolicitud(this.solicitudId)
        );
        break;
      default:
        entidades$ = of([]);
        break;
    }

    const subscription = entidades$.subscribe(
      (result) => {
        entidades = entidades.concat(result);
        this.entidadesFinanciadoras$.next(entidades);
      }
    );

    this.subscriptions.push(subscription);
  }

  private loadEntidadFinanciadoraSolicitud(solicitudId: number): Observable<EntidadFinanciadoraDesglosePresupuesto[]> {
    return this.solicitudService.findAllSolicitudProyectoEntidadFinanciadora(solicitudId)
      .pipe(
        map(result => {
          return result.items;
        }),
        switchMap((entidadesFinanciadoras) => {
          if (entidadesFinanciadoras.length === 0) {
            return of([] as EntidadFinanciadoraDesglosePresupuesto[]);
          }

          return from(entidadesFinanciadoras)
            .pipe(
              map((entidadesFinanciadora) => {
                return this.empresaService.findById(entidadesFinanciadora.empresa.id)
                  .pipe(
                    map(empresa => {
                      entidadesFinanciadora.empresa = empresa;
                      return entidadesFinanciadora;
                    }),
                  );

              }),
              mergeAll(),
              map(() => {
                return entidadesFinanciadoras.map((entidadFinanciadora) => {
                  const entidadFinanciadoraDesglosePresupuesto: EntidadFinanciadoraDesglosePresupuesto = {
                    entidadFinanciadora,
                    ajena: true
                  };

                  return entidadFinanciadoraDesglosePresupuesto;
                });
              })
            );
        }),
        takeLast(1)
      );
  }

  private loadEntidadFinanciadoraConvocatoria(convocatoriaId: number): Observable<EntidadFinanciadoraDesglosePresupuesto[]> {
    if (!convocatoriaId) {
      return of([] as EntidadFinanciadoraDesglosePresupuesto[]);
    }

    return this.convocatoriaService.findEntidadesFinanciadoras(convocatoriaId)
      .pipe(
        map(result => result.items),
        switchMap((entidadesFinanciadoras) => {
          if (entidadesFinanciadoras.length === 0) {
            return of([] as EntidadFinanciadoraDesglosePresupuesto[]);
          }

          return from(entidadesFinanciadoras)
            .pipe(
              map((entidadesFinanciadora) => {
                return this.empresaService.findById(entidadesFinanciadora.empresa.id)
                  .pipe(
                    map(empresa => {
                      entidadesFinanciadora.empresa = empresa;
                      return entidadesFinanciadora;
                    }),
                  );
              }),
              mergeAll(),
              map(() => {
                return entidadesFinanciadoras.map((entidadFinanciadora) => {
                  const entidadFinanciadoraDesglosePresupuesto: EntidadFinanciadoraDesglosePresupuesto = {
                    entidadFinanciadora,
                    ajena: false
                  };

                  return entidadFinanciadoraDesglosePresupuesto;
                });
              })
            );
        }),
        takeLast(1)
      );
  }

  private loadEntidadGestoraConvocatoria(convocatoriaId: number): Observable<EntidadFinanciadoraDesglosePresupuesto[]> {
    if (!convocatoriaId) {
      return of([] as EntidadFinanciadoraDesglosePresupuesto[]);
    }

    return this.convocatoriaService.findAllConvocatoriaEntidadGestora(convocatoriaId)
      .pipe(
        map(result => result.items),
        switchMap((entidadesGestoras) => {
          if (entidadesGestoras.length === 0) {
            return of([] as EntidadFinanciadoraDesglosePresupuesto[]);
          }

          return from(entidadesGestoras)
            .pipe(
              map((entidadGestora) => {
                return this.empresaService.findById(entidadGestora.empresa.id)
                  .pipe(
                    map(empresa => {
                      entidadGestora.empresa = empresa;
                      return entidadGestora;
                    }),
                  );
              }),
              mergeAll(),
              map(() => {
                return entidadesGestoras.map((entidadGestora) => {
                  const entidadFinanciadoraDesglosePresupuesto: EntidadFinanciadoraDesglosePresupuesto = {
                    entidadFinanciadora: entidadGestora,
                    ajena: false
                  };

                  return entidadFinanciadoraDesglosePresupuesto;
                });
              })
            );
        }),
        takeLast(1)
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

}
