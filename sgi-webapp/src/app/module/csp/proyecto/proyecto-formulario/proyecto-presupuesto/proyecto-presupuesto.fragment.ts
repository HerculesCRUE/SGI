import { FormControl, FormGroup, Validators } from '@angular/forms';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoAnualidadResumen } from '@core/models/csp/proyecto-anualidad-resumen';
import { FormFragment } from '@core/services/action-service';
import { ProyectoAnualidadService } from '@core/services/csp/proyecto-anualidad/proyecto-anualidad.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, EMPTY, from, merge, Observable, of } from 'rxjs';
import { catchError, map, mergeMap, takeLast, tap } from 'rxjs/operators';

export class ProyectoPresupuestoFragment extends FormFragment<IProyecto>  {

  proyecto: IProyecto;
  proyectoAnualidades$ = new BehaviorSubject<StatusWrapper<IProyectoAnualidadResumen>[]>([]);
  private proyectoAnualidadesEliminadas: StatusWrapper<IProyectoAnualidadResumen>[] = [];

  disableAddAnualidad$ = new BehaviorSubject<boolean>(false);
  columnAnualidades$ = new BehaviorSubject<boolean>(false);
  showPresupuestoSolicitud$ = new BehaviorSubject<boolean>(false);

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private proyectoService: ProyectoService,
    private proyectoAnualidadService: ProyectoAnualidadService,
    private solicitudService: SolicitudService,
    public readonly: boolean,
    public isVisor: boolean
  ) {
    super(key, true);
    this.setComplete(true);
  }

  protected initializer(key: string | number): Observable<IProyecto> {
    return this.proyectoService.findById(key as number).pipe(
      map((proyecto) => {

        if (proyecto.solicitudId) {
          this.checkProyectoPresupuesto(proyecto.solicitudId);
        }
        return proyecto;
      }),
      tap(() => this.loadAnualidades(key as number)),
      catchError((error) => {
        this.logger.error(error);
        return EMPTY;
      })
    );
  }

  buildPatch(value: IProyecto): { [key: string]: any } {
    this.proyecto = value;
    const result = {
      anualidades: value.anualidades,
      importePresupuesto: value.importePresupuesto,
      importePresupuestoCostesIndirectos: value.importePresupuestoCostesIndirectos,
      importePresupuestoSocios: value.importePresupuestoSocios,
      importeConcedidoCostesIndirectos: value.importeConcedidoCostesIndirectos,
      importeConcedido: value.importeConcedido,
      importeConcedidoSocios: value.importeConcedidoSocios,
      totalImportePresupuesto: value.totalImportePresupuesto,
      totalImporteConcedido: value.totalImporteConcedido
    };

    const form = this.getFormGroup();
    form.controls.importePresupuestoUniversidad
      .setValue(value.importePresupuesto);
    form.controls.importePresupuestoUniversidadCostesIndirectos
      .setValue(value.importePresupuestoCostesIndirectos);
    form.controls.totalImportePresupuestoUniversidad
      .setValue((value.importePresupuesto + value.importePresupuestoCostesIndirectos) !== 0 ?
        (value.importePresupuesto + value.importePresupuestoCostesIndirectos) : null);

    form.controls.importeConcedidoUniversidad
      .setValue(value.importeConcedido);
    form.controls.importeConcedidoUniversidadCostesIndirectos
      .setValue(value.importeConcedidoCostesIndirectos);
    form.controls.totalImporteConcedidoUniversidad
      .setValue((value.importeConcedido + value.importeConcedidoCostesIndirectos) !== 0 ?
        (value.importeConcedido + value.importeConcedidoCostesIndirectos) : null);

    form.controls.totalImportePresupuesto
      .setValue(value.totalImportePresupuesto);
    form.controls.totalImporteConcedido
      .setValue(value.totalImporteConcedido);

    return result;
  }

  getValue(): IProyecto {
    if (this.proyecto === null) {
      this.proyecto = {} as IProyecto;
    }
    const form = this.getFormGroup().getRawValue();
    this.proyecto.anualidades = form.anualidades;
    this.proyecto.importePresupuesto = form.importePresupuestoUniversidad;
    this.proyecto.importePresupuestoCostesIndirectos = form.importePresupuestoUniversidadCostesIndirectos;
    this.proyecto.importeConcedidoCostesIndirectos = form.importeConcedidoUniversidadCostesIndirectos;
    this.proyecto.importePresupuestoSocios = form.importePresupuestoSocios;
    this.proyecto.importeConcedido = form.importeConcedidoUniversidad;
    this.proyecto.importeConcedidoSocios = form.importeConcedidoSocios;
    this.proyecto.totalImportePresupuesto = form.totalImportePresupuesto;
    this.proyecto.totalImporteConcedido = form.totalImporteConcedido;
    return this.proyecto;
  }
  protected buildFormGroup(): FormGroup {
    const form = new FormGroup({
      anualidades: new FormControl(null, Validators.required),
      importePresupuestoUniversidad: new FormControl(null, [
        Validators.min(0),
        Validators.max(2_147_483_647)
      ]),
      importePresupuestoUniversidadCostesIndirectos: new FormControl(null, [
        Validators.min(0),
        Validators.max(2_147_483_647)
      ]),
      totalImportePresupuestoUniversidad: new FormControl({ value: null, disabled: true }, [
        Validators.min(0),
        Validators.max(2_147_483_647)
      ]),
      importeConcedidoUniversidad: new FormControl(null, [
        Validators.min(0),
        Validators.max(2_147_483_647)
      ]),
      importeConcedidoUniversidadCostesIndirectos: new FormControl(null, [
        Validators.min(0),
        Validators.max(2_147_483_647)
      ]),
      totalImporteConcedidoUniversidad: new FormControl({ value: null, disabled: true }, [
        Validators.min(0),
        Validators.max(2_147_483_647)
      ]),
      importePresupuestoSocios: new FormControl(null, [
        Validators.min(0),
        Validators.max(2_147_483_647)
      ]),
      importeConcedidoSocios: new FormControl(null, [
        Validators.min(0),
        Validators.max(2_147_483_647)
      ]),
      totalImportePresupuesto: new FormControl(null, [
        Validators.min(0),
        Validators.max(2_147_483_647)
      ]),
      totalImporteConcedido: new FormControl(null, [
        Validators.min(0),
        Validators.max(2_147_483_647)
      ])
    });
    if (this.readonly || this.isVisor) {
      form.disable();
    }

    this.subscriptions.push(
      form.controls.importePresupuestoUniversidad.valueChanges.subscribe(
        (value) => {
          form.controls.importePresupuestoUniversidad
            .patchValue((value + form.controls.importePresupuestoUniversidadCostesIndirectos.value) !== 0 ?
              (value + form.controls.importePresupuestoUniversidadCostesIndirectos.value) : null, { emitEvent: false });
        }),
      form.controls.importePresupuestoUniversidadCostesIndirectos.valueChanges.subscribe(
        (value) => {
          form.controls.totalImportePresupuestoUniversidad
            .patchValue((form.controls.importePresupuestoUniversidad.value + value) !== 0 ?
              (form.controls.importePresupuestoUniversidad.value + value) : null, { emitEvent: false });
        }),
      form.controls.importeConcedidoUniversidad.valueChanges.subscribe(
        (value) => {
          form.controls.totalImporteConcedidoUniversidad
            .patchValue((value + form.controls.importeConcedidoUniversidadCostesIndirectos.value) !== 0 ?
              (value + form.controls.importeConcedidoUniversidadCostesIndirectos.value) : null, { emitEvent: false });
        }),
      form.controls.importeConcedidoUniversidadCostesIndirectos.valueChanges.subscribe(
        (value) => {
          form.controls.totalImporteConcedidoUniversidad
            .patchValue((form.controls.importeConcedidoUniversidad.value + value) !== 0 ?
              (form.controls.importeConcedidoUniversidad.value + value) : null, { emitEvent: false });
        })
    );
    return form;
  }

  saveOrUpdate(): Observable<void> {
    return merge(
      this.deleteAnualidades(),
      this.updateProyecto()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  deleteAnualidad(anualidad: StatusWrapper<IProyectoAnualidadResumen>) {
    const current = this.proyectoAnualidades$.value;
    const index = current.findIndex((value) => value === anualidad);
    if (index >= 0) {
      if (!anualidad.created) {
        this.proyectoAnualidadesEliminadas.push(current[index]);
      }
      current.splice(index, 1);
      this.proyectoAnualidades$.next(current);
      this.setChanges(true);
    }
  }

  /**
   * Elimina las anualidades añadidas a proyecto.
   */
  private deleteAnualidades(): Observable<void> {
    if (this.proyectoAnualidadesEliminadas.length === 0) {
      return of(void 0);
    }

    return from(this.proyectoAnualidadesEliminadas).pipe(
      mergeMap((wrapped) => {
        return this.proyectoAnualidadService.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.proyectoAnualidadesEliminadas = this.proyectoAnualidadesEliminadas
                .filter(deletedAnualidad => deletedAnualidad.value.id !== wrapped.value.id);
            })
          );
      })
    );
  }

  /**
   * Actualiza el proyecto
   */
  private updateProyecto(): Observable<void> {
    this.getValue();
    if (!this.proyecto) {
      return of(void 0);
    }

    return this.proyectoService.update(this.proyecto.id, this.proyecto).pipe(
      catchError(() => {
        return of(void 0);
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.proyectoAnualidades$.value.some((wrapper) => wrapper.touched);
    return (this.proyectoAnualidadesEliminadas.length > 0 || touched);
  }

  private loadAnualidades(idProyecto: number) {
    this.subscriptions.push(this.proyectoService.findAllProyectoAnualidades(idProyecto).pipe().subscribe(data => {
      this.proyectoAnualidades$.next(data.items.map(
        anualidad => new StatusWrapper<IProyectoAnualidadResumen>(anualidad)));
    }));

  }

  private checkProyectoPresupuesto(solicitudId: number) {
    this.subscriptions.push(this.solicitudService.existsSolictudProyectoPresupuesto(solicitudId).pipe().subscribe(value => {
      this.showPresupuestoSolicitud$.next(value);
    }));
  }
}
