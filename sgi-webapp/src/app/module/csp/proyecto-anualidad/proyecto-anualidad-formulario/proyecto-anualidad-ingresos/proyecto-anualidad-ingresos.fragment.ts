import { IAnualidadIngreso } from '@core/models/csp/anualidad-ingreso';
import { Fragment } from '@core/services/action-service';
import { AnualidadIngresoService } from '@core/services/csp/anualidad-ingreso/anualidad-ingreso.service';
import { ProyectoAnualidadService } from '@core/services/csp/proyecto-anualidad/proyecto-anualidad.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, Observable } from 'rxjs';
import { map, takeLast, tap } from 'rxjs/operators';

export class ProyectoAnualidadIngresosFragment extends Fragment {

  anualidadIngresos$ = new BehaviorSubject<StatusWrapper<IAnualidadIngreso>[]>([]);

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    readonly proyectoId: number,
    private proyectoAnualidadService: ProyectoAnualidadService,
    private anualidadIngresoService: AnualidadIngresoService,
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const id = this.getKey() as number;
      this.subscriptions.push(
        this.proyectoAnualidadService.findAllAnualidadIngreso(id).subscribe(
          result => {
            this.anualidadIngresos$.next(
              result.items.map(anualidadIngreso =>
                new StatusWrapper<IAnualidadIngreso>(anualidadIngreso)
              )
            );
          },
          error => {
            this.logger.error(error);
          }
        )
      );
    }
  }

  addAnualidadIngreso(element: IAnualidadIngreso) {
    const wrapped = new StatusWrapper<IAnualidadIngreso>(element);
    wrapped.setCreated();
    const current = this.anualidadIngresos$.value;
    current.push(wrapped);
    this.anualidadIngresos$.next(current);
    this.setChanges(true);
  }

  updateAnualidadIngreso(wrapper: StatusWrapper<IAnualidadIngreso>): void {
    const current = this.anualidadIngresos$.value;
    const index = current.findIndex(value => value.value.id === wrapper.value.id);
    if (index >= 0) {
      wrapper.setEdited();
      this.anualidadIngresos$.value[index] = wrapper;
      this.setChanges(true);
    }
  }

  deleteAnualidadIngreso(wrapper: StatusWrapper<IAnualidadIngreso>) {
    const current = this.anualidadIngresos$.value;
    const index = current.findIndex((value) => value === wrapper);
    if (index >= 0) {
      current.splice(index, 1);
      this.anualidadIngresos$.next(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<void> {
    const values = this.anualidadIngresos$.value.map(wrapper => wrapper.value);
    const id = this.getKey() as number;
    return this.anualidadIngresoService.updateList(id, values)
      .pipe(
        takeLast(1),
        map((results) => {
          this.anualidadIngresos$.next(
            results.map(value => new StatusWrapper<IAnualidadIngreso>(value)));
        }),
        tap(() => {
          if (this.isSaveOrUpdateComplete()) {
            this.setChanges(false);
          }
        })
      );
  }

  private isSaveOrUpdateComplete(): boolean {
    const hasTouched = this.anualidadIngresos$.value.some((wrapper) => wrapper.touched);
    return !hasTouched;
  }
}
