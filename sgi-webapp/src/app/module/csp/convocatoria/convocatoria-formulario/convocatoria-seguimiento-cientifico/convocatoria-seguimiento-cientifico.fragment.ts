import { IConvocatoriaPeriodoSeguimientoCientifico } from '@core/models/csp/convocatoria-periodo-seguimiento-cientifico';
import { Fragment } from '@core/services/action-service';
import { ConvocatoriaSeguimientoCientificoService } from '@core/services/csp/convocatoria-seguimiento-cientifico.service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, Observable } from 'rxjs';
import { map, takeLast, tap } from 'rxjs/operators';

export class ConvocatoriaSeguimientoCientificoFragment extends Fragment {
  seguimientosCientificos$ = new BehaviorSubject<StatusWrapper<IConvocatoriaPeriodoSeguimientoCientifico>[]>([]);
  seguimientosCientificosEliminados: StatusWrapper<IConvocatoriaPeriodoSeguimientoCientifico>[] = [];

  constructor(
    key: number,
    private convocatoriaService: ConvocatoriaService,
    private convocatoriaSeguimientoCientificoService: ConvocatoriaSeguimientoCientificoService,
    public readonly canEdit: boolean
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      this.convocatoriaService.findSeguimientosCientificos(this.getKey() as number).pipe(
        map((response) => response.items)
      ).subscribe((seguimientosCientificos) => {
        this.seguimientosCientificos$.next(seguimientosCientificos.map(
          seguimientoCientifico => new StatusWrapper<IConvocatoriaPeriodoSeguimientoCientifico>(seguimientoCientifico))
        );
      });
    }
  }

  /**
   * Insertamos seguimiento cientifico
   *
   * @param seguimientoCientifico seguimiento cientifico
   */
  public addSeguimientoCientifico(seguimientoCientifico: IConvocatoriaPeriodoSeguimientoCientifico): void {
    const wrapped = new StatusWrapper<IConvocatoriaPeriodoSeguimientoCientifico>(seguimientoCientifico);
    wrapped.setCreated();
    const current = this.seguimientosCientificos$.value;
    current.push(wrapped);
    this.seguimientosCientificos$.next(current);
    this.setChanges(true);
    this.setErrors(false);
  }

  /**
   * Elimina el seguimiento cientifico de la tabla y se añade a la lista de eliminados
   *
   * @param seguimientoCientifico seguimiento cientifico
   */
  public deleteSeguimientoCientifico(seguimientoCientifico: StatusWrapper<IConvocatoriaPeriodoSeguimientoCientifico>): void {
    const current = this.seguimientosCientificos$.value;
    const indexseguimientoCientifico = current.findIndex(
      (value: StatusWrapper<IConvocatoriaPeriodoSeguimientoCientifico>) => value === seguimientoCientifico
    );

    if (!seguimientoCientifico.created) {
      this.seguimientosCientificosEliminados.push(current[indexseguimientoCientifico]);
      this.setChanges(true);
    }

    current.splice(indexseguimientoCientifico, 1);
    this.seguimientosCientificos$.next(current);
  }

  saveOrUpdate(): Observable<void> {
    const seguimientosCientificos = this.seguimientosCientificos$.value.map(wrapper => wrapper.value);
    // TODO: Eliminar si el backend deja de validar que convocatoriaId no pueda ser null
    seguimientosCientificos.forEach(periodo => {
      periodo.convocatoriaId = this.getKey() as number;
    });

    return this.convocatoriaSeguimientoCientificoService
      .updateConvocatoriaSeguimientoCientificoConvocatoria(this.getKey() as number, seguimientosCientificos).pipe(
        takeLast(1),
        map((seguimientosCientificosActualizados) => {
          this.seguimientosCientificosEliminados = [];
          this.seguimientosCientificos$.next(
            seguimientosCientificosActualizados
              .map(seguimientoCientifico => new StatusWrapper<IConvocatoriaPeriodoSeguimientoCientifico>(seguimientoCientifico)));
        }),
        tap(() => {
          if (this.isSaveOrUpdateComplete()) {
            this.setChanges(false);
          }
        })
      );
  }

  /**
   * Comprueba si se ejecutaron correctamente todos borrados, actualizaciones y creaciones.
   *
   * @returns true si no queda ningun cambio pendiente.
   */
  private isSaveOrUpdateComplete(): boolean {
    const hasTouched = this.seguimientosCientificos$.value.some((wrapper) => wrapper.touched);
    const hasNoDeleted = this.seguimientosCientificosEliminados.length > 0;
    return !hasTouched && !hasNoDeleted;
  }

  public checkFirstPeriodoStartsAtOne() {
    if (this.seguimientosCientificos$.value.length > 0
      && this.seguimientosCientificos$.value[0].value.mesInicial !== 1) {
      this.setErrors(true);
    } else {
      this.setErrors(false);
    }
  }

}
