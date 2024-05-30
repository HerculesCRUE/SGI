import { FormControl, FormGroup } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IConvocatoriaConceptoGasto } from '@core/models/csp/convocatoria-concepto-gasto';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoConceptoGasto } from '@core/models/csp/proyecto-concepto-gasto';
import { Fragment } from '@core/services/action-service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { ProyectoConceptoGastoService } from '@core/services/csp/proyecto-concepto-gasto.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { DateTime } from 'luxon';
import { BehaviorSubject, Observable, Subject, from, merge, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';
import { compareConceptoGasto, getFechaFinConceptoGasto, getFechaInicioConceptoGasto } from '../../../proyecto-concepto-gasto/proyecto-concepto-gasto.utils';

const PROYECTO_CONCEPTO_GASTO_NO_COINCIDE_KEY = marker('info.csp.proyecto-concepto-gasto.no-coincide-convocatoria');
const PROYECTO_CONCEPTO_GASTO_CODIGOS_ECONONOMICOS_NO_COINCIDE_KEY = marker('info.csp.proyecto-concepto-gasto.codigo-economicos-no-coinciden-convocatoria');
const PROYECTO_CONCEPTO_GASTO_NO_CONVOCATORIA_KEY = marker('info.csp.proyecto-concepto-gasto.no-existe-en-convocatoria');
const PROYECTO_CONCEPTO_GASTO_NO_PROYECTO_KEY = marker('info.csp.proyecto-concepto-gasto.no-existe-en-proyecto');

export enum HelpIconClass {
  WARNING = 'warning',
  DANGER = 'danger',
}

interface HelpIcon {
  class: HelpIconClass;
  tooltip: string;
}

export interface ConceptoGastoListado {
  proyectoConceptoGasto: StatusWrapper<IProyectoConceptoGasto>;
  convocatoriaConceptoGasto: IConvocatoriaConceptoGasto;
  help: HelpIcon;
  conceptoGasto: string;
  descripcion: string;
  costesIndirectos: boolean;
  importeMaximo: number;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  observaciones: string;
}

export class ProyectoConceptosGastoFragment extends Fragment {

  proyecto$ = new Subject<IProyecto>();
  proyectoConceptosGastoPermitidos$ = new BehaviorSubject<ConceptoGastoListado[]>([]);
  proyectoConceptosGastoNoPermitidos$ = new BehaviorSubject<ConceptoGastoListado[]>([]);
  private proyectoConceptosGastoEliminados: StatusWrapper<IProyectoConceptoGasto>[] = [];

  constructor(
    key: number,
    private proyecto: IProyecto,
    private proyectoService: ProyectoService,
    private proyectoConceptoGastoService: ProyectoConceptoGastoService,
    private convocatoriaService: ConvocatoriaService,
    public readonly: boolean,
    public isVisor: boolean,
  ) {
    super(key);
    this.setComplete(true);
  }

  protected buildFormGroup(): FormGroup {
    const form = new FormGroup({
      costeIndirecto: new FormControl(null, [
        IsEntityValidator.isValid()
      ])
    });

    if (this.readonly) {
      form.disable();
    }

    return form;
  }

  public reloadData(): void {
    this.loadTablesData();
  }

  protected onInitialize(): void {
    this.loadTablesData();

    this.subscriptions.push(
      this.proyecto$.subscribe(proyecto => this.proyecto = proyecto)
    );
  }

  private loadTablesData(): void {
    const key = this.getKey() as number;
    if (key) {

      this.subscriptions.push(
        this.proyecto$.pipe(
          tap(proyecto => this.proyecto = proyecto),
          switchMap(() => this.proyectoService.findAllProyectoConceptosGastoNoPermitidos(key as number)),
          map((response) => response.items.map(item => {
            const conceptoGastoListado = {
              proyectoConceptoGasto: new StatusWrapper<IProyectoConceptoGasto>(item),
            } as ConceptoGastoListado;
            return conceptoGastoListado;
          })),
          switchMap(conceptosGastoListado => {
            let requestConvocatoriaConceptosGasto: Observable<ConceptoGastoListado[]>;

            if (this.proyecto.convocatoriaId) {
              requestConvocatoriaConceptosGasto = this.convocatoriaService
                .findAllConvocatoriaConceptoGastosNoPermitidos(this.proyecto.convocatoriaId)
                .pipe(
                  map((response) => response.items),
                  map(convocatoriaConceptosGasto => {
                    conceptosGastoListado.forEach(conceptoGastoListado => {
                      if (conceptoGastoListado.proyectoConceptoGasto.value.convocatoriaConceptoGastoId) {
                        const index = convocatoriaConceptosGasto.findIndex(convocatoriaConceptoGasto =>
                          convocatoriaConceptoGasto.id === conceptoGastoListado.proyectoConceptoGasto.value.convocatoriaConceptoGastoId
                        );
                        if (index >= 0) {
                          conceptoGastoListado.convocatoriaConceptoGasto = convocatoriaConceptosGasto[index];
                          convocatoriaConceptosGasto.splice(index, 1);
                        }
                      }
                    });

                    if (convocatoriaConceptosGasto.length > 0) {
                      conceptosGastoListado.push(...convocatoriaConceptosGasto.map(convocatoriaConceptoGasto => {
                        const conceptoGastoListado = {
                          convocatoriaConceptoGasto
                        } as ConceptoGastoListado;
                        return conceptoGastoListado;
                      }));
                    }

                    return conceptosGastoListado;
                  })
                );
            } else {
              requestConvocatoriaConceptosGasto = of(conceptosGastoListado);
            }
            return requestConvocatoriaConceptosGasto;
          }),
        ).subscribe((response) => {
          response.forEach(element => this.fillListadoFields(element));
          this.proyectoConceptosGastoNoPermitidos$.next(response);
        }));

      this.subscriptions.push(
        this.proyecto$.pipe(
          tap(proyecto => this.proyecto = proyecto),
          switchMap(() => this.proyectoService.findAllProyectoConceptosGastoPermitidos(key as number)),
          map((response) => response.items.map(item => {
            const conceptoGastoListado = {
              proyectoConceptoGasto: new StatusWrapper<IProyectoConceptoGasto>(item),
            } as ConceptoGastoListado;
            return conceptoGastoListado;
          })),
          switchMap(conceptosGastoListado => {
            let requestConvocatoriaConceptosGasto: Observable<ConceptoGastoListado[]>;

            if (this.proyecto.convocatoriaId) {
              requestConvocatoriaConceptosGasto = this.convocatoriaService
                .findAllConvocatoriaConceptoGastosPermitidos(this.proyecto.convocatoriaId)
                .pipe(
                  map((response) => response.items),
                  map(convocatoriaConceptosGasto => {
                    conceptosGastoListado.forEach(conceptoGastoListado => {
                      if (conceptoGastoListado.proyectoConceptoGasto.value.convocatoriaConceptoGastoId) {
                        const index = convocatoriaConceptosGasto.findIndex(convocatoriaConceptoGasto =>
                          convocatoriaConceptoGasto.id === conceptoGastoListado.proyectoConceptoGasto.value.convocatoriaConceptoGastoId
                        );
                        if (index >= 0) {
                          conceptoGastoListado.convocatoriaConceptoGasto = convocatoriaConceptosGasto[index];
                          convocatoriaConceptosGasto.splice(index, 1);
                        }
                      }
                    });

                    if (convocatoriaConceptosGasto.length > 0) {
                      conceptosGastoListado.push(...convocatoriaConceptosGasto.map(convocatoriaConceptoGasto => {
                        const conceptoGastoListado = {
                          convocatoriaConceptoGasto
                        } as ConceptoGastoListado;
                        return conceptoGastoListado;
                      }));
                    }

                    return conceptosGastoListado;
                  })
                );
            } else {
              requestConvocatoriaConceptosGasto = of(conceptosGastoListado);
            }
            return requestConvocatoriaConceptosGasto;
          })).subscribe((response) => {
            response.forEach(element => this.fillListadoFields(element));
            this.proyectoConceptosGastoPermitidos$.next(response);
          }));
    }
  }

  getValue(): ConceptoGastoListado[] {
    throw new Error('Method not implemented');
  }

  deleteProyectoConceptoGasto(wrapper: StatusWrapper<IProyectoConceptoGasto>) {
    const permitido = wrapper.value.permitido;
    if (permitido) {
      const current = this.proyectoConceptosGastoPermitidos$.value;
      const index = current.findIndex(
        (value) => value.proyectoConceptoGasto === wrapper
      );
      if (index >= 0) {
        this.proyectoConceptosGastoEliminados.push(current[index].proyectoConceptoGasto);
        if (wrapper.value.convocatoriaConceptoGastoId) {
          current[index].proyectoConceptoGasto = undefined;
          this.fillListadoFields(current[index]);
        } else {
          current.splice(index, 1);
        }
        this.proyectoConceptosGastoPermitidos$.next(current);
        this.setChanges(true);
      }
    } else {
      const current = this.proyectoConceptosGastoNoPermitidos$.value;
      const index = current.findIndex(
        (value) => value.proyectoConceptoGasto === wrapper
      );
      if (index >= 0) {
        this.proyectoConceptosGastoEliminados.push(current[index].proyectoConceptoGasto);
        if (wrapper.value.convocatoriaConceptoGastoId) {
          current[index].proyectoConceptoGasto = undefined;
          this.fillListadoFields(current[index]);
        } else {
          current.splice(index, 1);
        }
        this.proyectoConceptosGastoNoPermitidos$.next(current);
        this.setChanges(true);
      }
    }
  }

  saveOrUpdate(): Observable<void> {
    return merge(
      this.deleteProyectoConceptosGasto(),
      this.updateProyectoConceptosGasto()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private deleteProyectoConceptosGasto(): Observable<void> {
    if (this.proyectoConceptosGastoEliminados.length === 0) {
      return of(void 0);
    }
    return from(this.proyectoConceptosGastoEliminados).pipe(
      mergeMap((wrapped) => {
        return this.proyectoConceptoGastoService.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.proyectoConceptosGastoEliminados = this.proyectoConceptosGastoEliminados.filter(
                deletedConvocatoriaConceptoGasto =>
                  deletedConvocatoriaConceptoGasto.value.id !== wrapped.value.id);
            })
          );
      })
    );
  }

  private updateProyectoConceptosGasto(): Observable<void> {
    const updateProyectoConceptosGasto = this.proyectoConceptosGastoPermitidos$.value
      .filter(
        (proyectoConceptoGastoPermitido) => proyectoConceptoGastoPermitido.proyectoConceptoGasto?.edited)
      .concat(
        this.proyectoConceptosGastoNoPermitidos$.value
          .filter(
            (proyectoConceptoGastoNoPermitido) => proyectoConceptoGastoNoPermitido.proyectoConceptoGasto?.edited)
      )
      .map(proyectoConceptoGasto => proyectoConceptoGasto.proyectoConceptoGasto);



    if (updateProyectoConceptosGasto.length === 0) {
      return of(void 0);
    }
    return from(updateProyectoConceptosGasto).pipe(
      mergeMap((wrappedProyectoConceptoGasto) => {
        return this.proyectoConceptoGastoService.update(
          wrappedProyectoConceptoGasto.value.id, wrappedProyectoConceptoGasto.value).pipe(
            map((updatedProyectoConceptoGasto) => {
              const indexPermitido = this.proyectoConceptosGastoPermitidos$.value.findIndex(
                (currentProyectoConceptoGasto) => currentProyectoConceptoGasto.proyectoConceptoGasto === wrappedProyectoConceptoGasto);
              if (indexPermitido >= 0) {
                this.proyectoConceptosGastoPermitidos$.value[indexPermitido].proyectoConceptoGasto =
                  new StatusWrapper<IProyectoConceptoGasto>(updatedProyectoConceptoGasto);
              }

              const indexNoPermitido = this.proyectoConceptosGastoNoPermitidos$.value.findIndex(
                (currentProyectoConceptoGasto) => currentProyectoConceptoGasto.proyectoConceptoGasto === wrappedProyectoConceptoGasto);
              if (indexNoPermitido >= 0) {
                this.proyectoConceptosGastoNoPermitidos$.value[indexNoPermitido].proyectoConceptoGasto =
                  new StatusWrapper<IProyectoConceptoGasto>(updatedProyectoConceptoGasto);
              }
            })
          );
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.proyectoConceptosGastoPermitidos$.value.some((gasto) =>
      gasto.proyectoConceptoGasto && gasto.proyectoConceptoGasto.touched);
    return !(this.proyectoConceptosGastoEliminados.length > 0 || touched);
  }

  private fillListadoFields(conceptoGasto: ConceptoGastoListado): void {
    if (conceptoGasto.proyectoConceptoGasto) {
      conceptoGasto.conceptoGasto = conceptoGasto.proyectoConceptoGasto.value.conceptoGasto?.nombre;
      conceptoGasto.descripcion = conceptoGasto.proyectoConceptoGasto.value.conceptoGasto?.descripcion;
      conceptoGasto.importeMaximo = conceptoGasto.proyectoConceptoGasto.value.importeMaximo;
      conceptoGasto.fechaInicio = conceptoGasto.proyectoConceptoGasto.value.fechaInicio;
      conceptoGasto.fechaFin = conceptoGasto.proyectoConceptoGasto.value.fechaFin;
      conceptoGasto.observaciones = conceptoGasto.proyectoConceptoGasto.value.observaciones;
      conceptoGasto.costesIndirectos = conceptoGasto.proyectoConceptoGasto.value.conceptoGasto.costesIndirectos;

      if (conceptoGasto.convocatoriaConceptoGasto) {
        if (compareConceptoGasto(conceptoGasto.convocatoriaConceptoGasto, conceptoGasto.proyectoConceptoGasto.value,
          this.proyecto.fechaInicio, this.proyecto.fechaFin)) {

          conceptoGasto.help = {
            class: HelpIconClass.WARNING,
            tooltip: PROYECTO_CONCEPTO_GASTO_NO_COINCIDE_KEY
          };

        } else {
          this.proyectoConceptoGastoService.hasDifferencesCodigosEcConvocatoria(conceptoGasto.proyectoConceptoGasto.value.id)
            .subscribe((hasDifferences) => {
              if (hasDifferences) {
                conceptoGasto.help = {
                  class: HelpIconClass.WARNING,
                  tooltip: PROYECTO_CONCEPTO_GASTO_CODIGOS_ECONONOMICOS_NO_COINCIDE_KEY
                };
              }
            });
        }
      } else if (this.proyecto.convocatoriaId) {
        conceptoGasto.help = {
          class: HelpIconClass.WARNING,
          tooltip: PROYECTO_CONCEPTO_GASTO_NO_CONVOCATORIA_KEY
        };
      }
    } else {
      conceptoGasto.conceptoGasto = conceptoGasto.convocatoriaConceptoGasto.conceptoGasto?.nombre;
      conceptoGasto.descripcion = conceptoGasto.convocatoriaConceptoGasto.conceptoGasto?.descripcion;
      conceptoGasto.importeMaximo = conceptoGasto.convocatoriaConceptoGasto.importeMaximo;
      conceptoGasto.observaciones = conceptoGasto.convocatoriaConceptoGasto.observaciones;

      if (conceptoGasto.convocatoriaConceptoGasto.mesInicial) {
        conceptoGasto.fechaInicio = getFechaInicioConceptoGasto(this.proyecto.fechaInicio,
          conceptoGasto.convocatoriaConceptoGasto.mesInicial);
      }

      if (conceptoGasto.convocatoriaConceptoGasto.mesFinal) {
        conceptoGasto.fechaFin = getFechaFinConceptoGasto(this.proyecto.fechaInicio, this.proyecto.fechaFin,
          conceptoGasto.convocatoriaConceptoGasto.mesFinal, conceptoGasto.fechaInicio);
      }

      conceptoGasto.help = {
        class: HelpIconClass.DANGER,
        tooltip: PROYECTO_CONCEPTO_GASTO_NO_PROYECTO_KEY
      };
    }
  }

}
