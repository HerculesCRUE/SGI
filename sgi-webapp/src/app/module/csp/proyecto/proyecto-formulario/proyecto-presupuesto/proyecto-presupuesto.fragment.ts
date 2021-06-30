import { FormControl, FormGroup, Validators } from '@angular/forms';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoAnualidadResumen } from '@core/models/csp/proyecto-anualidad-resumen';
import { FormFragment } from '@core/services/action-service';
import { ProyectoAnualidadService } from '@core/services/csp/proyecto-anualidad/proyecto-anualidad.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, EMPTY, from, merge, Observable, of } from 'rxjs';
import { catchError, mergeMap, takeLast, tap } from 'rxjs/operators';

export class ProyectoPresupuestoFragment extends FormFragment<IProyecto>  {

  private proyecto: IProyecto;
  proyectoAnualidades$ = new BehaviorSubject<StatusWrapper<IProyectoAnualidadResumen>[]>([]);
  private proyectoAnualidadesEliminadas: StatusWrapper<IProyectoAnualidadResumen>[] = [];

  disableAddAnualidad$ = new BehaviorSubject<boolean>(false);
  columnAnualidades$ = new BehaviorSubject<boolean>(false);

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private proyectoService: ProyectoService,
    private proyectoAnualidadService: ProyectoAnualidadService,
    public readonly: boolean
  ) {
    super(key, true);
    this.setComplete(true);
  }

  protected initializer(key: string | number): Observable<IProyecto> {
    return this.proyectoService.findById(key as number).pipe(
      tap(() => this.loadAnualidades(key as number)),
      catchError((error) => {
        this.logger.error(error);
        return EMPTY;
      })
    );
  }

  buildPatch(proyecto: IProyecto): { [key: string]: any } {
    this.proyecto = proyecto;
    const result = {
      anualidades: proyecto.anualidades,
      importePresupuesto: proyecto.importePresupuesto,
      importeConcedido: proyecto.importeConcedido,
      importePresupuestoSocios: proyecto.importePresupuestoSocios,
      importeConcedidoSocios: proyecto.importeConcedidoSocios,
      totalImportePresupuesto: proyecto.totalImportePresupuesto,
      totalImporteConcedido: proyecto.totalImporteConcedido
    };

    return result;
  }

  getValue(): IProyecto {
    if (this.proyecto === null) {
      this.proyecto = {} as IProyecto;
    }
    const form = this.getFormGroup().value;
    this.proyecto.anualidades = form.anualidades;
    this.proyecto.importePresupuesto = form.importePresupuesto;
    this.proyecto.importePresupuestoSocios = form.importePresupuestoSocios;
    this.proyecto.importeConcedido = form.importeConcedido;
    this.proyecto.importeConcedidoSocios = form.importeConcedidoSocios;
    this.proyecto.totalImportePresupuesto = form.totalImportePresupuesto;
    this.proyecto.totalImporteConcedido = form.totalImporteConcedido;
    return this.proyecto;
  }
  protected buildFormGroup(): FormGroup {
    const form = new FormGroup({
      anualidades: new FormControl(null, Validators.required),
      importePresupuesto: new FormControl('', [
        Validators.min(0),
        Validators.max(2_147_483_647)
      ]),
      importeConcedido: new FormControl('', [
        Validators.min(0),
        Validators.max(2_147_483_647)
      ]),
      importePresupuestoSocios: new FormControl('', [
        Validators.min(0),
        Validators.max(2_147_483_647)
      ]),
      importeConcedidoSocios: new FormControl('', [
        Validators.min(0),
        Validators.max(2_147_483_647)
      ]),
      totalImportePresupuesto: new FormControl('', [
        Validators.min(0),
        Validators.max(2_147_483_647)
      ]),
      totalImporteConcedido: new FormControl('', [
        Validators.min(0),
        Validators.max(2_147_483_647)
      ])
    });
    if (this.readonly) {
      form.disable();
    }
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
}
