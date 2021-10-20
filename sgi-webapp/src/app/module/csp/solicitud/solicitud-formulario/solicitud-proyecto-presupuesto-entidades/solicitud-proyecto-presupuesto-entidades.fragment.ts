import { FormControl, FormGroup, Validators } from '@angular/forms';
import { IEntidadFinanciadora } from '@core/models/csp/entidad-financiadora';
import { ISolicitudProyecto, TipoPresupuesto } from '@core/models/csp/solicitud-proyecto';
import { ISolicitudProyectoEntidad } from '@core/models/csp/solicitud-proyecto-entidad';
import { FormFragment } from '@core/services/action-service';
import { SolicitudProyectoEntidadFinanciadoraAjenaService } from '@core/services/csp/solicitud-proyecto-entidad-financiadora-ajena.service';
import { SolicitudProyectoService } from '@core/services/csp/solicitud-proyecto.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { BehaviorSubject, EMPTY, from, merge, Observable, of, Subject } from 'rxjs';
import { catchError, map, mergeAll, switchMap, takeLast, tap } from 'rxjs/operators';
import { SolicitudProyectoEntidadFinanciadoraAjenaData } from '../solicitud-proyecto-entidades-financiadoras/solicitud-proyecto-entidades-financiadoras.fragment';

export interface EntidadFinanciadoraDesglosePresupuesto {
  solicitudProyectoEntidadId: number;
  entidadFinanciadora: IEntidadFinanciadora;
  fuenteFinanciacion: string;
  ajena: boolean;
}

export class SolicitudProyectoPresupuestoEntidadesFragment extends FormFragment<ISolicitudProyecto> {

  entidadesFinanciadorasEdited$ = new BehaviorSubject<SolicitudProyectoEntidadFinanciadoraAjenaData[]>([]);
  entidadesFinanciadoras$ = new BehaviorSubject<EntidadFinanciadoraDesglosePresupuesto[]>([]);
  private readonly solicitudId: number;
  tipoPresupuestoMixto: boolean;
  tipoPresupuesto$: Subject<TipoPresupuesto> = new BehaviorSubject<TipoPresupuesto>(null);

  private solicitudProyecto: ISolicitudProyecto;

  constructor(
    key: number,
    public readonly convocatoriaId: number,
    private solicitudService: SolicitudService,
    private empresaService: EmpresaService,
    private solicitudProyectoService: SolicitudProyectoService,
    private solicitudProyectoEntidadFinanciadoraAjenaService: SolicitudProyectoEntidadFinanciadoraAjenaService,
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

    this.entidadesFinanciadorasEdited$.pipe(
      switchMap(entidadesFinanciadorasEdited => {
        const entidadesFinanciadorasAdded = entidadesFinanciadorasEdited
          .filter(entidad => !entidad.id || 0 > this.entidadesFinanciadoras$.value.findIndex(e => entidad.id === e.entidadFinanciadora.id))
          .map(entidad => {
            const entidadDesglose: EntidadFinanciadoraDesglosePresupuesto = {
              solicitudProyectoEntidadId: undefined,
              entidadFinanciadora: entidad,
              fuenteFinanciacion: entidad.fuenteFinanciacion?.nombre,
              ajena: true
            };
            return entidadDesglose;
          });

        let entidadesFinanciadorasUpdated = this.entidadesFinanciadoras$.value.filter(entidadFinanciadora => !entidadFinanciadora.ajena);
        entidadesFinanciadorasUpdated = entidadesFinanciadorasUpdated.concat(entidadesFinanciadorasAdded);

        this.entidadesFinanciadoras$.value.forEach(entidadFinanciadora => {
          const edited = entidadesFinanciadorasEdited.find(a => entidadFinanciadora.entidadFinanciadora.id
            && entidadFinanciadora.entidadFinanciadora.id === a.id && entidadFinanciadora.ajena);
          if (edited) {
            entidadFinanciadora.entidadFinanciadora = edited;
            entidadesFinanciadorasUpdated.push(entidadFinanciadora);
          }
        });

        return of(entidadesFinanciadorasUpdated);
      }),
      switchMap(response => {
        const requestsSolicitudProyectoEntidad: Observable<EntidadFinanciadoraDesglosePresupuesto>[] = [];
        response.forEach(entidad => {
          if (!entidad.solicitudProyectoEntidadId && entidad.entidadFinanciadora.id) {
            requestsSolicitudProyectoEntidad.push(
              this.solicitudProyectoEntidadFinanciadoraAjenaService.getSolicitudProyectoEntidad(entidad.entidadFinanciadora.id).pipe(
                map(solicitudProyectoEntidad => {
                  entidad.solicitudProyectoEntidadId = solicitudProyectoEntidad.id;
                  return entidad;
                })
              ));
          } else {
            requestsSolicitudProyectoEntidad.push(of(entidad));
          }
        });
        return of(response).pipe(
          tap(() => merge(...requestsSolicitudProyectoEntidad).subscribe())
        );
      }),
    ).subscribe(entidadesFinanciadoras => {
      this.entidadesFinanciadoras$.next(entidadesFinanciadoras);
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
      importePresupuestadoUniversidad: new FormControl(null, [
        Validators.min(0),
        Validators.max(2_147_483_647)
      ]),
      importePresupuestadoUniversidadCostesIndirectos: new FormControl(null, [
        Validators.min(0),
        Validators.max(2_147_483_647)
      ]),
      totalImportePresupuestadoUniversidad: new FormControl({ value: null, disabled: true }, [
        Validators.min(0),
        Validators.max(2_147_483_647)
      ]),
      importeSolicitadoUniversidad: new FormControl(null, [
        Validators.min(0),
        Validators.max(2_147_483_647)
      ]),
      importeSolicitadoUniversidadCostesIndirectos: new FormControl(null, [
        Validators.min(0),
        Validators.max(2_147_483_647)
      ]),
      totalImporteSolicitadoUniversidad: new FormControl({ value: null, disabled: true }, [
        Validators.min(0),
        Validators.max(2_147_483_647)
      ]),
      importePresupuestadoSocios: new FormControl(null, [
        Validators.min(0),
        Validators.max(2_147_483_647)
      ]),
      importeSolicitadoSocios: new FormControl(null, [
        Validators.min(0),
        Validators.max(2_147_483_647)
      ]),
      totalImportePresupuestado: new FormControl(null, [
        Validators.min(0),
        Validators.max(2_147_483_647)
      ]),
      totalImporteSolicitado: new FormControl(null, [
        Validators.min(0),
        Validators.max(2_147_483_647)
      ])
    });

    if (this.readonly) {
      form.disable();
    }
    this.subscriptions.push(
      form.controls.importePresupuestadoUniversidad.valueChanges.subscribe(
        (value) => {
          form.controls.totalImportePresupuestadoUniversidad
            .patchValue((value + form.controls.importePresupuestadoUniversidadCostesIndirectos.value) !== 0 ?
              (value + form.controls.importePresupuestadoUniversidadCostesIndirectos.value) : null, { emitEvent: false });
        }),
      form.controls.importePresupuestadoUniversidadCostesIndirectos.valueChanges.subscribe(
        (value) => {
          form.controls.totalImportePresupuestadoUniversidad
            .patchValue((form.controls.importePresupuestadoUniversidad.value + value) !== 0 ?
              (form.controls.importePresupuestadoUniversidad.value + value) : null, { emitEvent: false });
        }),
      form.controls.importeSolicitadoUniversidad.valueChanges.subscribe(
        (value) => {
          form.controls.totalImporteSolicitadoUniversidad
            .patchValue((value + form.controls.importeSolicitadoUniversidadCostesIndirectos.value) !== 0 ?
              (value + form.controls.importeSolicitadoUniversidadCostesIndirectos.value) : null, { emitEvent: false });
        }),
      form.controls.importeSolicitadoUniversidadCostesIndirectos.valueChanges.subscribe(
        (value) => {
          form.controls.totalImporteSolicitadoUniversidad
            .patchValue((form.controls.importeSolicitadoUniversidad.value + value) !== 0 ?
              (form.controls.importeSolicitadoUniversidad.value + value) : null, { emitEvent: false });
        })
    );

    return form;
  }

  protected buildPatch(value: ISolicitudProyecto): { [key: string]: any; } {
    this.solicitudProyecto = value ?? {} as ISolicitudProyecto;
    const result = {
      importePresupuestado: value.importePresupuestado,
      importePresupuestadoSocios: value.importePresupuestadoSocios,
      importeSolicitado: value.importeSolicitado,
      importeSolicitadoSocios: value.importeSolicitadoSocios,
      totalImportePresupuestado: value.totalImportePresupuestado,
      totalImporteSolicitado: value.totalImporteSolicitado
    } as ISolicitudProyecto;

    const form = this.getFormGroup();
    /*Presupuestado*/
    form.controls.importePresupuestadoUniversidad
      .setValue(value.importePresupuestado);
    form.controls.importePresupuestadoUniversidadCostesIndirectos
      .setValue(value.importePresupuestadoCostesIndirectos);
    form.controls.totalImportePresupuestadoUniversidad
      .setValue((value.importePresupuestado + value.importePresupuestadoCostesIndirectos) !== 0 ?
        (value.importePresupuestado + value.importePresupuestadoCostesIndirectos) : null);
    /*Solicitado*/
    form.controls.importeSolicitadoUniversidad
      .setValue(value.importeSolicitado);
    form.controls.importeSolicitadoUniversidadCostesIndirectos
      .setValue(value.importeSolicitadoCostesIndirectos);
    form.controls.totalImporteSolicitadoUniversidad
      .setValue((value.importeSolicitado + value.importeSolicitadoCostesIndirectos) !== 0 ?
        (value.importeSolicitado + value.importeSolicitadoCostesIndirectos) : null);
    /*Totales*/
    form.controls.totalImportePresupuestado
      .setValue(value.totalImportePresupuestado);
    form.controls.totalImporteSolicitado
      .setValue(value.totalImporteSolicitado);

    return result;
  }

  getValue(): ISolicitudProyecto {
    if (this.solicitudProyecto === null) {
      this.solicitudProyecto = {} as ISolicitudProyecto;
    }
    const form = this.getFormGroup().value;
    this.solicitudProyecto.importePresupuestado = form.importePresupuestadoUniversidad;
    this.solicitudProyecto.importePresupuestadoCostesIndirectos = form.importePresupuestadoUniversidadCostesIndirectos;
    this.solicitudProyecto.importeSolicitadoCostesIndirectos = form.importeSolicitadoUniversidadCostesIndirectos;
    this.solicitudProyecto.importePresupuestadoSocios = form.importePresupuestadoSocios;
    this.solicitudProyecto.importeSolicitado = form.importeSolicitadoUniversidad;
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

  public setEntidadesFinanciadorasEdited(entidadesFinanciadorasEdited: SolicitudProyectoEntidadFinanciadoraAjenaData[]) {
    this.entidadesFinanciadorasEdited$.next(entidadesFinanciadorasEdited);
  }

  private loadEntidadesFinanciadoras(tipoPresupuesto: TipoPresupuesto): void {

    let entidades$: Observable<ISolicitudProyectoEntidad[]>;
    switch (tipoPresupuesto) {
      case TipoPresupuesto.POR_ENTIDAD:
        entidades$ = this.solicitudService.findAllSolicitudProyectoEntidadTipoPresupuestoPorEntidades(this.solicitudId)
          .pipe(
            map(result => result.items)
          );

        break;
      case TipoPresupuesto.MIXTO:
        entidades$ = this.solicitudService.findAllSolicitudProyectoEntidadTipoPresupuestoMixto(this.solicitudId)
          .pipe(
            map(result => result.items)
          );

        break;
      default:
        entidades$ = of([]);
        break;
    }

    const subscription = entidades$
      .pipe(
        map(solicitudProyectoEntidades => solicitudProyectoEntidades.map(solicitudProyectoEntidad => {
          const entidadFinanciadora: IEntidadFinanciadora = solicitudProyectoEntidad.solicitudProyectoEntidadFinanciadoraAjena
            ?? solicitudProyectoEntidad.convocatoriaEntidadFinanciadora
            ?? {
              id: solicitudProyectoEntidad.convocatoriaEntidadGestora.id,
              empresa: solicitudProyectoEntidad.convocatoriaEntidadGestora.empresa
            } as IEntidadFinanciadora;

          const entidadFinanciadoraDesglosePresupuesto: EntidadFinanciadoraDesglosePresupuesto = {
            solicitudProyectoEntidadId: solicitudProyectoEntidad.id,
            entidadFinanciadora,
            fuenteFinanciacion: entidadFinanciadora.fuenteFinanciacion?.nombre,
            ajena: !!solicitudProyectoEntidad.solicitudProyectoEntidadFinanciadoraAjena
          };

          return entidadFinanciadoraDesglosePresupuesto;
        })),
        switchMap((solicitudProyectoEntidades) => {
          if (solicitudProyectoEntidades.length === 0) {
            return of([] as EntidadFinanciadoraDesglosePresupuesto[]);
          }

          return from(solicitudProyectoEntidades)
            .pipe(
              map((solicitudProyectoEntidad) => {
                return this.empresaService.findById(solicitudProyectoEntidad.entidadFinanciadora.empresa.id)
                  .pipe(
                    map(empresa => {
                      solicitudProyectoEntidad.entidadFinanciadora.empresa = empresa;
                      return solicitudProyectoEntidad;
                    }),
                  );

              }),
              mergeAll(),
              map(() => solicitudProyectoEntidades)
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
