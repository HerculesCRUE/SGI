import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { TipoJustificacion } from '@core/enums/tipo-justificacion';
import { IConvocatoriaPeriodoJustificacion } from '@core/models/csp/convocatoria-periodo-justificacion';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoPeriodoJustificacion } from '@core/models/csp/proyecto-periodo-justificacion';
import { Fragment } from '@core/services/action-service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { ProyectoPeriodoJustificacionService } from '@core/services/csp/proyecto-periodo-justificacion/proyecto-periodo-justificacion.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { DateTime } from 'luxon';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';
import { comparePeriodoJustificacion, getFechaFinPeriodoSeguimiento, getFechaInicioPeriodoSeguimiento } from '../../../proyecto-periodo-seguimiento/proyecto-periodo-seguimiento.utils';

const PROYECTO_PERIODO_JUSTIFICACION_NO_COINCIDE_KEY = marker('info.csp.proyecto-periodo-justificacion.no-coincide-convocatoria');
const PROYECTO_PERIODO_JUSTIFICACION_NO_CONVOCATORIA_KEY = marker('info.csp.proyecto-periodo-justificacion.no-existe-en-convocatoria');
const PROYECTO_PERIODO_JUSTIFICACION_NO_PROYECTO_KEY = marker('info.csp.proyecto-periodo-justificacion.no-existe-en-proyecto');

export enum HelpIconClass {
  WARNING = 'warning',
  DANGER = 'danger',
}

interface HelpIcon {
  class: HelpIconClass;
  tooltip: string;
}

export interface IPeriodoJustificacionListado {
  proyectoPeriodoJustificacion: StatusWrapper<IProyectoPeriodoJustificacion>;
  convocatoriaPeriodoJustificacion: IConvocatoriaPeriodoJustificacion;
  help: HelpIcon;
  numPeriodo: number;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  fechaInicioPresentacion: DateTime;
  fechaFinPresentacion: DateTime;
  tipoJustificacion: TipoJustificacion;
  observaciones: string;
}

export class ProyectoCalendarioJustificacionFragment extends Fragment {
  periodoJustificaciones$: BehaviorSubject<IPeriodoJustificacionListado[]>;

  constructor(
    key: number,
    public proyecto: IProyecto,
    private proyectoService: ProyectoService,
    private proyectoPeriodoJustifiacionService: ProyectoPeriodoJustificacionService,
    private convocatoriaService: ConvocatoriaService
  ) {
    super(key);
    this.setComplete(true);
    this.periodoJustificaciones$ = new BehaviorSubject<IPeriodoJustificacionListado[]>([]);
  }


  protected onInitialize(): void {
    if (this.getKey()) {
      this.proyectoService.findAllPeriodoJustificacion(this.getKey() as number).pipe(
        map((response) => response.items.map(item => {
          const periodoJustificacionListado = {
            proyectoPeriodoJustificacion: new StatusWrapper<IProyectoPeriodoJustificacion>(item),
          } as IPeriodoJustificacionListado;
          return periodoJustificacionListado;
        })),
        switchMap(periodosJustificacionListado => {
          let requestConvocatoriaPeriodosJustificacion: Observable<IPeriodoJustificacionListado[]>;

          if (this.proyecto.convocatoriaId) {
            requestConvocatoriaPeriodosJustificacion = this.convocatoriaService
              .getPeriodosJustificacion(this.proyecto.convocatoriaId)
              .pipe(
                map((response) => response.items),
                map(periodoJustificacionConvocatoria => {
                  periodosJustificacionListado.forEach(periodoJustificacionListado => {
                    if (periodoJustificacionListado.proyectoPeriodoJustificacion.value.convocatoriaPeriodoJustificacionId) {
                      const index = periodoJustificacionConvocatoria.findIndex(justificacionConvocatoria =>
                        justificacionConvocatoria.id ===
                        periodoJustificacionListado.proyectoPeriodoJustificacion.value.convocatoriaPeriodoJustificacionId
                      );
                      if (index >= 0) {
                        periodoJustificacionListado.convocatoriaPeriodoJustificacion = periodoJustificacionConvocatoria[index];
                        periodoJustificacionConvocatoria.splice(index, 1);
                      }
                    }
                  });

                  if (periodoJustificacionConvocatoria.length > 0) {
                    periodosJustificacionListado.push(...periodoJustificacionConvocatoria.map(convocatoriaPeriodojustificacion => {
                      const periodoJustificacionListado = {
                        convocatoriaPeriodoJustificacion: convocatoriaPeriodojustificacion
                      } as IPeriodoJustificacionListado;
                      return periodoJustificacionListado;
                    }));
                  }

                  return periodosJustificacionListado;
                })
              );
          } else {
            requestConvocatoriaPeriodosJustificacion = of(periodosJustificacionListado);
          }
          return requestConvocatoriaPeriodosJustificacion;
        }),
      ).subscribe((periodoJustificacionListado) => {
        periodoJustificacionListado.forEach(element => this.fillListadoFields(element));
        this.periodoJustificaciones$?.next(periodoJustificacionListado);
        this.recalcularNumPeriodos();
      });
    }
  }

  public addPeriodoJustificacion(periodoJustificacion: IProyectoPeriodoJustificacion, periodoJustifiacionId?: number): void {
    const wrapped = new StatusWrapper<IProyectoPeriodoJustificacion>(periodoJustificacion);
    wrapped.setCreated();

    const periodoJustificacionListado: IPeriodoJustificacionListado = {
      proyectoPeriodoJustificacion: wrapped
    } as IPeriodoJustificacionListado;

    const current = this.periodoJustificaciones$.value;

    if (!periodoJustifiacionId) {
      this.fillListadoFields(periodoJustificacionListado);
      current.push(periodoJustificacionListado);
    } else {
      const index = current.findIndex((value) => value.proyectoPeriodoJustificacion?.value?.id === periodoJustifiacionId);
      if (index >= 0) {
        current[index].proyectoPeriodoJustificacion = new StatusWrapper<IProyectoPeriodoJustificacion>(periodoJustificacion);
        this.fillListadoFields(current[index]);
      }
    }

    this.periodoJustificaciones$.next(current);
    this.setChanges(true);
  }

  deletePeriodoJustificacion(index: number, wrapper: StatusWrapper<IProyectoPeriodoJustificacion>) {
    const current = this.periodoJustificaciones$.value;
    if (index >= 0) {
      if (wrapper.value.convocatoriaPeriodoJustificacionId) {
        current[index].proyectoPeriodoJustificacion = undefined;
        this.fillListadoFields(current[index]);
      } else {
        current.splice(index, 1);
      }
      this.periodoJustificaciones$.next(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<void> {
    const values = this.periodoJustificaciones$.value
      .filter(periodoJustificacion => periodoJustificacion.proyectoPeriodoJustificacion)
      .map(periodoJustificacion => periodoJustificacion.proyectoPeriodoJustificacion.value);
    const id = this.getKey() as number;
    values.forEach(value => {
      if (!Boolean(value.id)) {
        value.proyecto = { id: this.getKey() as number } as IProyecto;
        value.numPeriodo = value.numPeriodo;
      }
    });
    return this.proyectoPeriodoJustifiacionService.updateList(id, values).pipe(
      map((proyectoConceptosGasto) => {
        const current = this.periodoJustificaciones$.value;
        proyectoConceptosGasto.forEach(periodoJustificacion => {
          const index = current.findIndex(value =>
            (value.proyectoPeriodoJustificacion && value.proyectoPeriodoJustificacion.value.id === periodoJustificacion.id)
            || (value.proyectoPeriodoJustificacion
              && value.convocatoriaPeriodoJustificacion?.id === periodoJustificacion.convocatoriaPeriodoJustificacionId)
            || (value.proyectoPeriodoJustificacion
              && value.proyectoPeriodoJustificacion.value.fechaInicio?.toMillis() === periodoJustificacion.fechaInicio?.toMillis()
              && value.proyectoPeriodoJustificacion.value.fechaFin?.toMillis() === periodoJustificacion.fechaFin?.toMillis())
          );

          current[index].proyectoPeriodoJustificacion = new StatusWrapper<IProyectoPeriodoJustificacion>(periodoJustificacion);

          this.periodoJustificaciones$.next(current);
        });
      }),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.periodoJustificaciones$.value.some((periodoJusficacion) =>
      periodoJusficacion.proyectoPeriodoJustificacion && periodoJusficacion.proyectoPeriodoJustificacion.touched);
    return !touched;
  }
  /**
   * Recalcula los numeros de los periodos de todos los periodos de seguimiento de la tabla en funcion de su fecha de inicio.
   */
  public recalcularNumPeriodos(): void {
    let numPeriodo = 1;
    this.periodoJustificaciones$?.value
      .sort((a, b) => ((a.proyectoPeriodoJustificacion ?
        a.proyectoPeriodoJustificacion.value.fechaInicio : getFechaInicioPeriodoSeguimiento(this.proyecto.fechaInicio,
          a.convocatoriaPeriodoJustificacion.mesInicial)) > (b.proyectoPeriodoJustificacion ?
            b.proyectoPeriodoJustificacion.value.fechaInicio : getFechaInicioPeriodoSeguimiento(this.proyecto.fechaInicio,
              b.convocatoriaPeriodoJustificacion.mesInicial))) ? 1 :
        (((b.proyectoPeriodoJustificacion ?
          b.proyectoPeriodoJustificacion.value.fechaInicio : getFechaInicioPeriodoSeguimiento(this.proyecto.fechaInicio,
            b.convocatoriaPeriodoJustificacion.mesInicial)) > (a.proyectoPeriodoJustificacion ?
              a.proyectoPeriodoJustificacion.value.fechaInicio : getFechaInicioPeriodoSeguimiento(this.proyecto.fechaInicio,
                a.convocatoriaPeriodoJustificacion.mesInicial))) ? -1 : 0));

    this.periodoJustificaciones$?.value.forEach(c => {
      if (c?.proyectoPeriodoJustificacion) {
        c.numPeriodo = numPeriodo++;
      } else {
        c.numPeriodo = numPeriodo++;
      }
    });

    this.periodoJustificaciones$?.next(this.periodoJustificaciones$?.value);
  }
  private fillListadoFields(periodoJustificacionListado: IPeriodoJustificacionListado): void {
    if (periodoJustificacionListado.proyectoPeriodoJustificacion) {
      periodoJustificacionListado.numPeriodo = periodoJustificacionListado.proyectoPeriodoJustificacion.value.numPeriodo;
      periodoJustificacionListado.tipoJustificacion = periodoJustificacionListado.proyectoPeriodoJustificacion.value.tipoJustificacion;
      periodoJustificacionListado.fechaInicio = periodoJustificacionListado.proyectoPeriodoJustificacion.value.fechaInicio;
      periodoJustificacionListado.fechaFin = periodoJustificacionListado.proyectoPeriodoJustificacion.value.fechaFin;
      periodoJustificacionListado.fechaInicioPresentacion =
        periodoJustificacionListado.proyectoPeriodoJustificacion.value.fechaInicioPresentacion;
      periodoJustificacionListado.fechaFinPresentacion =
        periodoJustificacionListado.proyectoPeriodoJustificacion.value.fechaFinPresentacion;
      periodoJustificacionListado.observaciones = periodoJustificacionListado.proyectoPeriodoJustificacion.value.observaciones;

      if (periodoJustificacionListado.convocatoriaPeriodoJustificacion) {
        if (comparePeriodoJustificacion(
          periodoJustificacionListado.convocatoriaPeriodoJustificacion, periodoJustificacionListado.proyectoPeriodoJustificacion.value,
          this.proyecto.fechaInicio, this.proyecto.fechaFin)) {
          periodoJustificacionListado.help = {
            class: HelpIconClass.WARNING,
            tooltip: PROYECTO_PERIODO_JUSTIFICACION_NO_COINCIDE_KEY
          };
        } else {
          periodoJustificacionListado.help = undefined;
        }
      } else {
        periodoJustificacionListado.help = {
          class: HelpIconClass.WARNING,
          tooltip: PROYECTO_PERIODO_JUSTIFICACION_NO_CONVOCATORIA_KEY
        };
      }
    } else {
      periodoJustificacionListado.tipoJustificacion = periodoJustificacionListado.convocatoriaPeriodoJustificacion?.tipo;
      periodoJustificacionListado.fechaInicioPresentacion =
        periodoJustificacionListado.convocatoriaPeriodoJustificacion?.fechaInicioPresentacion;
      periodoJustificacionListado.fechaFinPresentacion = periodoJustificacionListado.convocatoriaPeriodoJustificacion?.fechaFinPresentacion;
      periodoJustificacionListado.observaciones = periodoJustificacionListado.convocatoriaPeriodoJustificacion?.observaciones;
      if (periodoJustificacionListado.convocatoriaPeriodoJustificacion?.mesInicial) {
        periodoJustificacionListado.fechaInicio = getFechaInicioPeriodoSeguimiento(this.proyecto.fechaInicio,
          periodoJustificacionListado.convocatoriaPeriodoJustificacion.mesInicial);
      }

      if (periodoJustificacionListado.convocatoriaPeriodoJustificacion?.mesFinal) {
        periodoJustificacionListado.fechaFin = getFechaFinPeriodoSeguimiento(this.proyecto.fechaInicio, this.proyecto.fechaFin,
          periodoJustificacionListado.convocatoriaPeriodoJustificacion.mesFinal);
      }
      periodoJustificacionListado.help = {
        class: HelpIconClass.DANGER,
        tooltip: PROYECTO_PERIODO_JUSTIFICACION_NO_PROYECTO_KEY
      };

    }
  }

  updatePeriodoJustificacion(wrapper: StatusWrapper<IProyectoPeriodoJustificacion>, index: number): void {
    if (index >= 0) {
      this.periodoJustificaciones$.value[index].proyectoPeriodoJustificacion = wrapper;
      this.fillListadoFields(this.periodoJustificaciones$.value[index]);
      if (wrapper.value.id) {
        this.periodoJustificaciones$.value[index].proyectoPeriodoJustificacion.setEdited();
      } else {
        this.periodoJustificaciones$.value[index].proyectoPeriodoJustificacion.setCreated();
      }

      this.setChanges(true);
    }

  }

}
