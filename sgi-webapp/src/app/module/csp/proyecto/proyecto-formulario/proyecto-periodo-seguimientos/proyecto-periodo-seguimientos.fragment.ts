import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { TipoSeguimiento } from '@core/enums/tipo-seguimiento';
import { IConvocatoriaPeriodoSeguimientoCientifico } from '@core/models/csp/convocatoria-periodo-seguimiento-cientifico';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoPeriodoSeguimiento } from '@core/models/csp/proyecto-periodo-seguimiento';
import { Fragment } from '@core/services/action-service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { ProyectoPeriodoSeguimientoService } from '@core/services/csp/proyecto-periodo-seguimiento.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { DateTime } from 'luxon';
import { BehaviorSubject, from, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';
import { comparePeriodoSeguimiento, getFechaFinPeriodoSeguimiento, getFechaInicioPeriodoSeguimiento } from '../../../proyecto-periodo-seguimiento/proyecto-periodo-seguimiento.utils';

const PROYECTO_PERIODO_SEGUIMIENTO_NO_COINCIDE_KEY = marker('info.csp.proyecto-periodo-seguimiento.no-coincide-convocatoria');
const PROYECTO_PERIODO_SEGUIMIENTO_NO_CONVOCATORIA_KEY = marker('info.csp.proyecto-periodo-seguimiento.no-existe-en-convocatoria');
const PROYECTO_PERIODO_SEGUIMIENTO_GASTO_NO_PROYECTO_KEY = marker('info.csp.proyecto-periodo-seguimiento.no-existe-en-proyecto');

export enum HelpIconClass {
  WARNING = 'warning',
  DANGER = 'danger',
}

interface HelpIcon {
  class: HelpIconClass;
  tooltip: string;
}

export interface IPeriodoSeguimientoListado {
  proyectoPeriodoSeguimiento: StatusWrapper<IProyectoPeriodoSeguimiento>;
  convocatoriaPeriodoSeguimiento: IConvocatoriaPeriodoSeguimientoCientifico;
  help: HelpIcon;
  numPeriodo: number;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  fechaInicioPresentacion: DateTime;
  fechaFinPresentacion: DateTime;
  tipoSeguimiento: TipoSeguimiento;
  observaciones: string;
}

export class ProyectoPeriodoSeguimientosFragment extends Fragment {
  periodoSeguimientos$ = new BehaviorSubject<IPeriodoSeguimientoListado[]>([]);
  private periodoSeguimientosEliminados: StatusWrapper<IProyectoPeriodoSeguimiento>[] = [];

  constructor(
    key: number,
    private proyecto: IProyecto,
    private proyectoService: ProyectoService,
    private proyectoPeriodoSeguimientoService: ProyectoPeriodoSeguimientoService,
    private convocatoriaService: ConvocatoriaService,
    private documentoService: DocumentoService
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      this.proyectoService.findAllProyectoPeriodoSeguimientoProyecto(this.getKey() as number).pipe(
        map((response) => response.items.map(item => {
          const periodoSeguimientoListado = {
            proyectoPeriodoSeguimiento: new StatusWrapper<IProyectoPeriodoSeguimiento>(item),
          } as IPeriodoSeguimientoListado;
          return periodoSeguimientoListado;
        })),
        switchMap(periodosSeguimientoListado => {
          let requestConvocatoriaPeriodoSeguimiento: Observable<IPeriodoSeguimientoListado[]>;

          if (this.proyecto.convocatoriaId) {
            requestConvocatoriaPeriodoSeguimiento = this.convocatoriaService
              .findSeguimientosCientificos(this.proyecto.convocatoriaId)
              .pipe(
                map((response) => response.items),
                map(periodoSeguimientoCientificoConvocatoria => {
                  periodosSeguimientoListado.forEach(periodoSeguimientoListado => {
                    if (periodoSeguimientoListado.proyectoPeriodoSeguimiento.value.convocatoriaPeriodoSeguimientoId) {
                      const index = periodoSeguimientoCientificoConvocatoria.findIndex(periodoSeguimientoConvocatoria =>
                        periodoSeguimientoConvocatoria.id ===
                        periodoSeguimientoListado.proyectoPeriodoSeguimiento.value.convocatoriaPeriodoSeguimientoId
                      );
                      if (index >= 0) {
                        periodoSeguimientoListado.convocatoriaPeriodoSeguimiento = periodoSeguimientoCientificoConvocatoria[index];
                        periodoSeguimientoCientificoConvocatoria.splice(index, 1);
                      }
                    }
                  });

                  if (periodoSeguimientoCientificoConvocatoria.length > 0) {
                    periodosSeguimientoListado.push(...periodoSeguimientoCientificoConvocatoria.map(convocatoriaPeriodoSeguimiento => {
                      const periodoSeguimientoListado = {
                        convocatoriaPeriodoSeguimiento
                      } as IPeriodoSeguimientoListado;
                      return periodoSeguimientoListado;
                    }));
                  }

                  return periodosSeguimientoListado;
                })
              );
          } else {
            requestConvocatoriaPeriodoSeguimiento = of(periodosSeguimientoListado);
          }
          return requestConvocatoriaPeriodoSeguimiento;
        }),
      ).subscribe((periodoSeguimientoListado) => {
        periodoSeguimientoListado.forEach(element => this.fillListadoFields(element));
        this.periodoSeguimientos$.next(periodoSeguimientoListado);
        this.recalcularNumPeriodos();
      });
    }
  }

  public deletePeriodoSeguimiento(wrapper: StatusWrapper<IProyectoPeriodoSeguimiento>) {
    const current = this.periodoSeguimientos$.value;
    const index = current.findIndex(
      (value) => value.proyectoPeriodoSeguimiento === wrapper
    );
    if (index >= 0) {
      if (!wrapper.created) {
        this.periodoSeguimientosEliminados.push(current[index].proyectoPeriodoSeguimiento);
      }
      if (wrapper.value.convocatoriaPeriodoSeguimientoId) {
        current[index].proyectoPeriodoSeguimiento = undefined;
        this.fillListadoFields(current[index]);
      } else {
        current.splice(index, 1);
      }
      this.periodoSeguimientos$.next(current);
      this.setChanges(true);
      this.recalcularNumPeriodos();
    }
  }

  saveOrUpdate(): Observable<void> {
    return this.deletePeriodoSeguimientos().pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private deletePeriodoSeguimientos(): Observable<void> {
    if (this.periodoSeguimientosEliminados.length === 0) {
      return of(void 0);
    }
    return from(this.periodoSeguimientosEliminados).pipe(
      mergeMap((wrapped) => {
        return this.proyectoPeriodoSeguimientoService.findDocumentos(wrapped.value.id).pipe(
          switchMap((documentos) => {
            return this.proyectoPeriodoSeguimientoService.deleteById(wrapped.value.id).pipe(
              tap(() => {
                this.periodoSeguimientosEliminados = this.periodoSeguimientosEliminados.filter(deletedPeriodoSeguimiento =>
                  deletedPeriodoSeguimiento.value.id !== wrapped.value.id),
                  map(() => {
                    return from(documentos.items).pipe(
                      mergeMap(documento => {
                        return this.documentoService.eliminarFichero(documento.documentoRef);
                      })
                    );
                  });
              }),
              takeLast(1)
            );
          })
        );
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    return this.periodoSeguimientosEliminados.length > 0;
  }

  /**
   * Recalcula los numeros de los periodos de todos los periodos de seguimiento de la tabla en funcion de su fecha de inicio.
   */
  private recalcularNumPeriodos(): void {
    let numPeriodo = 1;
    this.periodoSeguimientos$.value
      .sort((a, b) => ((a.proyectoPeriodoSeguimiento ? a.proyectoPeriodoSeguimiento.value.fechaInicio : getFechaInicioPeriodoSeguimiento(this.proyecto.fechaInicio,
        a.convocatoriaPeriodoSeguimiento.mesInicial)) > (b.proyectoPeriodoSeguimiento ?
          b.proyectoPeriodoSeguimiento.value.fechaInicio : getFechaInicioPeriodoSeguimiento(this.proyecto.fechaInicio,
            b.convocatoriaPeriodoSeguimiento.mesInicial))) ? 1 :
        (((b.proyectoPeriodoSeguimiento ? b.proyectoPeriodoSeguimiento.value.fechaInicio : getFechaInicioPeriodoSeguimiento(this.proyecto.fechaInicio,
          b.convocatoriaPeriodoSeguimiento.mesInicial)) > (a.proyectoPeriodoSeguimiento ? a.proyectoPeriodoSeguimiento.value.fechaInicio : getFechaInicioPeriodoSeguimiento(this.proyecto.fechaInicio,
            a.convocatoriaPeriodoSeguimiento.mesInicial))) ? -1 : 0));

    this.periodoSeguimientos$.value.forEach(c => {
      if (c.proyectoPeriodoSeguimiento) {
        c.numPeriodo = numPeriodo++;
      } else {
        c.numPeriodo = numPeriodo++;
      }
    });

    this.periodoSeguimientos$.next(this.periodoSeguimientos$.value);
  }

  private fillListadoFields(periodoSeguimientoListado: IPeriodoSeguimientoListado): void {
    if (periodoSeguimientoListado.proyectoPeriodoSeguimiento) {
      periodoSeguimientoListado.numPeriodo = periodoSeguimientoListado.proyectoPeriodoSeguimiento.value.numPeriodo;
      periodoSeguimientoListado.tipoSeguimiento = periodoSeguimientoListado.proyectoPeriodoSeguimiento.value.tipoSeguimiento;
      periodoSeguimientoListado.fechaInicio = periodoSeguimientoListado.proyectoPeriodoSeguimiento.value.fechaInicio;
      periodoSeguimientoListado.fechaFin = periodoSeguimientoListado.proyectoPeriodoSeguimiento.value.fechaFin;
      periodoSeguimientoListado.fechaInicioPresentacion =
        periodoSeguimientoListado.proyectoPeriodoSeguimiento.value.fechaInicioPresentacion;
      periodoSeguimientoListado.fechaFinPresentacion = periodoSeguimientoListado.proyectoPeriodoSeguimiento.value.fechaFinPresentacion;
      periodoSeguimientoListado.observaciones = periodoSeguimientoListado.proyectoPeriodoSeguimiento.value.observaciones;

      if (periodoSeguimientoListado.convocatoriaPeriodoSeguimiento) {
        if (comparePeriodoSeguimiento(periodoSeguimientoListado.convocatoriaPeriodoSeguimiento,
          periodoSeguimientoListado.proyectoPeriodoSeguimiento.value,
          this.proyecto.fechaInicio, this.proyecto.fechaFin)) {

          periodoSeguimientoListado.help = {
            class: HelpIconClass.WARNING,
            tooltip: PROYECTO_PERIODO_SEGUIMIENTO_NO_COINCIDE_KEY
          };
        }

      } else {
        periodoSeguimientoListado.help = {
          class: HelpIconClass.WARNING,
          tooltip: PROYECTO_PERIODO_SEGUIMIENTO_NO_CONVOCATORIA_KEY
        };
      }
    } else {
      periodoSeguimientoListado.numPeriodo = periodoSeguimientoListado.convocatoriaPeriodoSeguimiento?.numPeriodo;
      periodoSeguimientoListado.tipoSeguimiento = periodoSeguimientoListado.convocatoriaPeriodoSeguimiento?.tipoSeguimiento;
      periodoSeguimientoListado.fechaInicioPresentacion =
        periodoSeguimientoListado.convocatoriaPeriodoSeguimiento?.fechaInicioPresentacion;
      periodoSeguimientoListado.fechaFinPresentacion = periodoSeguimientoListado.convocatoriaPeriodoSeguimiento?.fechaFinPresentacion;
      periodoSeguimientoListado.observaciones = periodoSeguimientoListado.convocatoriaPeriodoSeguimiento?.observaciones;

      if (periodoSeguimientoListado.convocatoriaPeriodoSeguimiento?.mesInicial) {
        periodoSeguimientoListado.fechaInicio = getFechaInicioPeriodoSeguimiento(this.proyecto.fechaInicio,
          periodoSeguimientoListado.convocatoriaPeriodoSeguimiento.mesInicial);
      }

      if (periodoSeguimientoListado.convocatoriaPeriodoSeguimiento?.mesFinal) {
        periodoSeguimientoListado.fechaFin = getFechaFinPeriodoSeguimiento(this.proyecto.fechaInicio, this.proyecto.fechaFin,
          periodoSeguimientoListado.convocatoriaPeriodoSeguimiento.mesFinal);
      }

      periodoSeguimientoListado.
        help = {
        class: HelpIconClass.DANGER,
        tooltip: PROYECTO_PERIODO_SEGUIMIENTO_GASTO_NO_PROYECTO_KEY
      };
    }
  }

}
