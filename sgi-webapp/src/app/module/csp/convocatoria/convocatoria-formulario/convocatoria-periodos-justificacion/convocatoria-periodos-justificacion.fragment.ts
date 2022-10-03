import { IConvocatoriaPeriodoJustificacion } from '@core/models/csp/convocatoria-periodo-justificacion';
import { Fragment } from '@core/services/action-service';
import { ConvocatoriaPeriodoJustificacionService } from '@core/services/csp/convocatoria-periodo-justificacion.service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, Observable } from 'rxjs';
import { map, takeLast, tap } from 'rxjs/operators';

export class ConvocatoriaPeriodosJustificacionFragment extends Fragment {
  periodosJustificacion$: BehaviorSubject<StatusWrapper<IConvocatoriaPeriodoJustificacion>[]>;
  periodosJustificacionEliminados: StatusWrapper<IConvocatoriaPeriodoJustificacion>[] = [];

  constructor(
    key: number,
    private convocatoriaService: ConvocatoriaService,
    private convocatoriaPeriodoJustificacionService: ConvocatoriaPeriodoJustificacionService,
    public readonly canEdit: boolean
  ) {
    super(key);
    this.setComplete(true);
    this.periodosJustificacion$ = new BehaviorSubject<StatusWrapper<IConvocatoriaPeriodoJustificacion>[]>([]);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      this.convocatoriaService.getPeriodosJustificacion(this.getKey() as number).pipe(
        map((response) => response.items)
      ).subscribe((periodosJustificacion) => {
        this.periodosJustificacion$.next(periodosJustificacion.map(
          periodoJustificacion => new StatusWrapper<IConvocatoriaPeriodoJustificacion>(periodoJustificacion))
        );
      });
    }
  }

  /**
   * Insertamos periodo justificacion
   *
   * @param periodoJustificacion Periodo de justificación
   */
  public addPeriodoJustificacion(periodoJustificacion: IConvocatoriaPeriodoJustificacion): void {
    const wrapped = new StatusWrapper<IConvocatoriaPeriodoJustificacion>(periodoJustificacion);
    wrapped.setCreated();
    const current = this.periodosJustificacion$.value;
    current.push(wrapped);
    this.periodosJustificacion$.next(current);
    this.setChanges(true);
    this.setErrors(false);
  }

  /**
   * Elimina el periodo justificacion de la tabla y se añade a la lista de eliminados
   *
   * @param periodoJustificacion Periodo de justificación
   */
  public deletePeriodoJustificacion(periodoJustificacion: StatusWrapper<IConvocatoriaPeriodoJustificacion>): void {
    const current = this.periodosJustificacion$.value;
    const indexPeriodoJustificacion = current.findIndex(
      (value: StatusWrapper<IConvocatoriaPeriodoJustificacion>) => value === periodoJustificacion
    );

    if (indexPeriodoJustificacion === -1) {
      // Periodo justificacion no encontrado
      return;
    }

    if (!periodoJustificacion.created) {
      this.periodosJustificacionEliminados.push(current[indexPeriodoJustificacion]);
      this.setChanges(true);
    }

    current.splice(indexPeriodoJustificacion, 1);
    this.periodosJustificacion$.next(current);
  }

  saveOrUpdate(): Observable<void> {
    const periodosJustificacion = this.periodosJustificacion$.value.map(wrapper => wrapper.value);
    // TODO: Eliminar si el backend deja de validar que convocatoriaId no pueda ser null
    periodosJustificacion.forEach(periodo => {
      periodo.convocatoriaId = this.getKey() as number;
    });

    return this.convocatoriaPeriodoJustificacionService
      .updateConvocatoriaPeriodoJustificacionesConvocatoria(this.getKey() as number, periodosJustificacion).pipe(
        takeLast(1),
        map((peridosJustificacionActualizados) => {
          this.periodosJustificacionEliminados = [];
          this.periodosJustificacion$.next(
            peridosJustificacionActualizados
              .map(periodoJustificacion => new StatusWrapper<IConvocatoriaPeriodoJustificacion>(periodoJustificacion)));
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
    const hasTouched = this.periodosJustificacion$.value.some((wrapper) => wrapper.touched);
    const hasNoDeleted = this.periodosJustificacionEliminados.length > 0;

    return !hasTouched && !hasNoDeleted;
  }


  public checkFirstPeriodoStartsAtOne() {
    if (this.periodosJustificacion$.value.length > 0
      && this.periodosJustificacion$.value[0].value.mesInicial !== 1) {
      this.setErrors(true);
    } else {
      this.setErrors(false);
    }
  }
}
