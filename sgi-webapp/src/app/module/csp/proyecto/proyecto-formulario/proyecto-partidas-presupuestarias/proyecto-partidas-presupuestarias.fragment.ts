import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { TipoPartida } from '@core/enums/tipo-partida';
import { IConvocatoriaPartidaPresupuestaria } from '@core/models/csp/convocatoria-partida-presupuestaria';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoPartida } from '@core/models/csp/proyecto-partida';
import { Fragment } from '@core/services/action-service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { ProyectoPartidaService } from '@core/services/csp/proyecto-partida.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, concat, from, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';
import { comparePartidaPresupuestaria } from './proyecto-partida-presupuestaria.utils';

const PARTIDA_PRESUPUESTARIA_NO_COINCIDE_KEY = marker('info.csp.partida-presupuestaria.no-coincide-convocatoria');
const PARTIDA_PRESUPUESTARIA_NO_CONVOCATORIA_KEY = marker('info.csp.partida-presupuestaria.no-existe-en-convocatoria');
const PARTIDA_PRESUPUESTARIA_NO_PROYECTO_KEY = marker('info.csp.partida-presupuestaria.no-existe-en-proyecto');

export enum HelpIconClass {
  WARNING = 'warning',
  DANGER = 'danger',
}

interface HelpIcon {
  class: HelpIconClass;
  tooltip: string;
}

export interface IPartidaPresupuestariaListado {
  partidaPresupuestaria: StatusWrapper<IProyectoPartida>;
  convocatoriaPartidaPresupuestaria: IConvocatoriaPartidaPresupuestaria;
  help: HelpIcon;
  codigo: string;
  descripcion: string;
  tipoPartida: TipoPartida;
  canEdit: boolean;
}

export class ProyectoPartidasPresupuestariasFragment extends Fragment {
  partidasPresupuestarias$ = new BehaviorSubject<IPartidaPresupuestariaListado[]>([]);
  private partidasPresupuestariasEliminadasIds: number[] = [];

  mapModificable: Map<number, boolean> = new Map();

  constructor(
    key: number,
    private proyecto: IProyecto,
    private proyectoService: ProyectoService,
    private proyectoPartidaService: ProyectoPartidaService,
    private readonly convocatoriaService: ConvocatoriaService,
    public readonly: boolean
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const id = this.getKey() as number;
      this.subscriptions.push(
        this.proyectoService.findAllProyectoPartidas(id).pipe(
          map((response) => response.items.map(item => {
            const partidaPresupuestariaListado = {
              partidaPresupuestaria: new StatusWrapper<IProyectoPartida>(item),
            } as IPartidaPresupuestariaListado;
            return partidaPresupuestariaListado;
          })),
          switchMap(partidasPresupuestariaListado => {
            let requestConvocatoriaPartidaPresupuestaria: Observable<IPartidaPresupuestariaListado[]>;

            if (this.proyecto.convocatoriaId) {
              requestConvocatoriaPartidaPresupuestaria = this.convocatoriaService
                .findPartidasPresupuestarias(this.proyecto.convocatoriaId)
                .pipe(
                  map((response) => response.items),
                  map(convocatoriaPartidaPresupuestarias => {
                    partidasPresupuestariaListado.forEach(partidaPresupuestariaListado => {
                      if (partidaPresupuestariaListado.partidaPresupuestaria.value.convocatoriaPartidaId) {
                        const index = convocatoriaPartidaPresupuestarias.findIndex(convocatoriaPartidaPresupuestaria =>
                          convocatoriaPartidaPresupuestaria.id ===
                          partidaPresupuestariaListado.partidaPresupuestaria.value.convocatoriaPartidaId
                        );
                        if (index >= 0) {
                          partidaPresupuestariaListado.convocatoriaPartidaPresupuestaria = convocatoriaPartidaPresupuestarias[index];
                          convocatoriaPartidaPresupuestarias.splice(index, 1);
                        }
                      }
                    });

                    if (convocatoriaPartidaPresupuestarias.length > 0) {
                      partidasPresupuestariaListado.push(...convocatoriaPartidaPresupuestarias.map(convocatoriaPartidaPresupuestaria => {
                        const partidaPresupuestoListado = {
                          convocatoriaPartidaPresupuestaria
                        } as IPartidaPresupuestariaListado;
                        return partidaPresupuestoListado;
                      }));
                    }

                    return partidasPresupuestariaListado;
                  })
                );
            } else {
              requestConvocatoriaPartidaPresupuestaria = of(partidasPresupuestariaListado);
            }
            return requestConvocatoriaPartidaPresupuestaria;
          }),
          switchMap((partidasPresupuestarias) => {
            if (partidasPresupuestarias) {
              partidasPresupuestarias.forEach(partida => {
                if (!this.readonly) {
                  if (partida.partidaPresupuestaria) {
                    this.subscriptions.push(this.proyectoPartidaService.modificable(partida.partidaPresupuestaria.value.id).subscribe((value) => {
                      this.mapModificable.set(partida.partidaPresupuestaria.value.id, value);
                    }));
                  }
                } else {
                  this.mapModificable.set(partida.partidaPresupuestaria?.value.id, false);
                }
              });
            }
            return of(partidasPresupuestarias);
          })
        ).subscribe((response) => {
          response.forEach(element => this.fillListadoFields(element));
          response.map(partida => {
            this.checkCanEditPartida(partida);
          });
          this.partidasPresupuestarias$.next(response);
        }));
    }
  }

  addPartidaPresupuestaria(partidaPresupuestaria: IProyectoPartida, convocatoriaPartida?: IConvocatoriaPartidaPresupuestaria) {
    const wrapped = new StatusWrapper<IProyectoPartida>(partidaPresupuestaria);
    wrapped.setCreated();
    const current = this.partidasPresupuestarias$.value;
    partidaPresupuestaria.convocatoriaPartidaId = convocatoriaPartida?.id;
    const partidaPresupuestariaListado = {
      partidaPresupuestaria: wrapped,
      codigo: partidaPresupuestaria.codigo,
      descripcion: partidaPresupuestaria.descripcion,
      tipoPartida: partidaPresupuestaria.tipoPartida,
      canEdit: true
    } as IPartidaPresupuestariaListado;

    if (convocatoriaPartida) {
      const index = current.findIndex(value => value.convocatoriaPartidaPresupuestaria?.id === convocatoriaPartida.id);
      current[index] = partidaPresupuestariaListado;
    } else {
      partidaPresupuestariaListado.help = {
        class: HelpIconClass.WARNING,
        tooltip: PARTIDA_PRESUPUESTARIA_NO_CONVOCATORIA_KEY
      }
      current.push(partidaPresupuestariaListado);
    }

    this.partidasPresupuestarias$.next(current);
    this.setChanges(true);
  }

  updatePartidaPresupuestaria(wrapper: StatusWrapper<IProyectoPartida>, index: number): void {

    if (index >= 0) {
      if (wrapper.value.id) {
        wrapper.setEdited();
      } else {
        wrapper.setCreated();
      }

      const partidaPresupuestariaListado = this.partidasPresupuestarias$.value[index];
      partidaPresupuestariaListado.partidaPresupuestaria = wrapper;
      partidaPresupuestariaListado.codigo = wrapper.value.codigo;
      partidaPresupuestariaListado.descripcion = wrapper.value.descripcion;
      partidaPresupuestariaListado.tipoPartida = wrapper.value.tipoPartida;

      if (partidaPresupuestariaListado.convocatoriaPartidaPresupuestaria) {
        if (comparePartidaPresupuestaria(partidaPresupuestariaListado.convocatoriaPartidaPresupuestaria,
          partidaPresupuestariaListado.partidaPresupuestaria.value)) {

          partidaPresupuestariaListado.help = {
            class: HelpIconClass.WARNING,
            tooltip: PARTIDA_PRESUPUESTARIA_NO_COINCIDE_KEY
          };

        } else {
          partidaPresupuestariaListado.help = null;
        }
      } else {
        partidaPresupuestariaListado.help = {
          class: HelpIconClass.WARNING,
          tooltip: PARTIDA_PRESUPUESTARIA_NO_CONVOCATORIA_KEY
        };
      }

      this.partidasPresupuestarias$.value[index] = partidaPresupuestariaListado;
      this.setChanges(true);
    }
  }

  deletePartidaPresupuestaria(wrapper: StatusWrapper<IProyectoPartida>) {
    const current = this.partidasPresupuestarias$.value;
    const index = current.findIndex(
      (value) => value.partidaPresupuestaria === wrapper
    );
    if (index >= 0) {
      if (!wrapper.created) {
        this.partidasPresupuestariasEliminadasIds.push(current[index].partidaPresupuestaria.value.id);
      }
      if (wrapper.value.convocatoriaPartidaId) {
        current[index].partidaPresupuestaria = undefined;
        this.fillListadoFields(current[index]);
      } else {
        current.splice(index, 1);
      }
      this.partidasPresupuestarias$.next(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<void> {
    return concat(
      this.deletePartidasPresupuestarias(),
      this.updatePartidasPresupuestarias(),
      this.createPartidasPresupuestarias()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private deletePartidasPresupuestarias(): Observable<void> {
    if (this.partidasPresupuestariasEliminadasIds.length === 0) {
      return of(void 0);
    }
    return from(this.partidasPresupuestariasEliminadasIds).pipe(
      mergeMap((partidaPresupuestariaId) => {
        return this.proyectoPartidaService.deleteById(partidaPresupuestariaId)
          .pipe(
            tap(() => {
              this.partidasPresupuestariasEliminadasIds = this.partidasPresupuestariasEliminadasIds
                .filter(deletedPartidaPresupuestariaId => deletedPartidaPresupuestariaId !== partidaPresupuestariaId);
            })
          );
      })
    );
  }

  private createPartidasPresupuestarias(): Observable<void> {
    const createdPartidasPresupuestarias = this.partidasPresupuestarias$.value
      .filter((partidaPresupuestariaListado) => partidaPresupuestariaListado.partidaPresupuestaria?.created);
    if (createdPartidasPresupuestarias.length === 0) {
      return of(void 0);
    }
    createdPartidasPresupuestarias.forEach(
      (partidaPresupuestariaListado) => partidaPresupuestariaListado.partidaPresupuestaria.value.proyectoId = this.getKey() as number
    );
    return from(createdPartidasPresupuestarias).pipe(
      mergeMap((partidaPresupuestariaListado) => {
        return this.proyectoPartidaService.create(partidaPresupuestariaListado.partidaPresupuestaria.value).pipe(
          map((createdPartidaPresupuestaria) => {
            const index = this.partidasPresupuestarias$.value
              .findIndex((currentPartidaPresupuestariaListado) => currentPartidaPresupuestariaListado.partidaPresupuestaria?.value.id
                === partidaPresupuestariaListado.partidaPresupuestaria.value.id);
            this.partidasPresupuestarias$.value[index] = {
              partidaPresupuestaria: new StatusWrapper<IProyectoPartida>(createdPartidaPresupuestaria),
              codigo: createdPartidaPresupuestaria.codigo,
              descripcion: createdPartidaPresupuestaria.descripcion,
              tipoPartida: createdPartidaPresupuestaria.tipoPartida,
              help: {
                class: HelpIconClass.WARNING,
                tooltip: PARTIDA_PRESUPUESTARIA_NO_CONVOCATORIA_KEY
              }
            } as IPartidaPresupuestariaListado;
          })
        );
      })
    );
  }

  private updatePartidasPresupuestarias(): Observable<void> {
    const updatedPartidasPresupuestarias = this.partidasPresupuestarias$.value
      .filter((partidaPresupuestariaListado) => partidaPresupuestariaListado.partidaPresupuestaria?.edited);
    if (updatedPartidasPresupuestarias.length === 0) {
      return of(void 0);
    }
    return from(updatedPartidasPresupuestarias).pipe(
      mergeMap((partidaPresupuestariaListado) => {
        return this.proyectoPartidaService.update(partidaPresupuestariaListado.partidaPresupuestaria.value.id,
          partidaPresupuestariaListado.partidaPresupuestaria.value).pipe(
            map((updatedPartidaPresupuestaria) => {
              const index = this.partidasPresupuestarias$.value
                .findIndex((currentPartidaPresupuestaria) => currentPartidaPresupuestaria === partidaPresupuestariaListado);
              this.partidasPresupuestarias$.value[index] = {
                partidaPresupuestaria: new StatusWrapper<IProyectoPartida>(updatedPartidaPresupuestaria),
                convocatoriaPartidaPresupuestaria: this.partidasPresupuestarias$.value[index].convocatoriaPartidaPresupuestaria,
                codigo: updatedPartidaPresupuestaria.codigo,
                descripcion: updatedPartidaPresupuestaria.descripcion,
                tipoPartida: updatedPartidaPresupuestaria.tipoPartida,
                help: this.partidasPresupuestarias$.value[index].help
              } as IPartidaPresupuestariaListado;
            })
          );
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.partidasPresupuestarias$.value.some((partidaPresupuestariaListado) =>
      partidaPresupuestariaListado.partidaPresupuestaria?.touched);
    return !(this.partidasPresupuestariasEliminadasIds.length > 0 || touched);
  }


  private fillListadoFields(partidasPresupuestaria: IPartidaPresupuestariaListado): void {
    if (partidasPresupuestaria.partidaPresupuestaria) {
      partidasPresupuestaria.codigo = partidasPresupuestaria.partidaPresupuestaria.value.codigo;
      partidasPresupuestaria.descripcion = partidasPresupuestaria.partidaPresupuestaria.value.descripcion;
      partidasPresupuestaria.tipoPartida = partidasPresupuestaria.partidaPresupuestaria.value.tipoPartida;

      if (partidasPresupuestaria.convocatoriaPartidaPresupuestaria) {
        if (comparePartidaPresupuestaria(partidasPresupuestaria.convocatoriaPartidaPresupuestaria,
          partidasPresupuestaria.partidaPresupuestaria.value)) {

          partidasPresupuestaria.help = {
            class: HelpIconClass.WARNING,
            tooltip: PARTIDA_PRESUPUESTARIA_NO_COINCIDE_KEY
          };

        }
      } else {
        partidasPresupuestaria.help = {
          class: HelpIconClass.WARNING,
          tooltip: PARTIDA_PRESUPUESTARIA_NO_CONVOCATORIA_KEY
        };
      }
    } else {
      partidasPresupuestaria.codigo = partidasPresupuestaria.convocatoriaPartidaPresupuestaria.codigo;
      partidasPresupuestaria.descripcion = partidasPresupuestaria.convocatoriaPartidaPresupuestaria.descripcion;
      partidasPresupuestaria.tipoPartida = partidasPresupuestaria.convocatoriaPartidaPresupuestaria.tipoPartida;

      partidasPresupuestaria.help = {
        class: HelpIconClass.DANGER,
        tooltip: PARTIDA_PRESUPUESTARIA_NO_PROYECTO_KEY
      };
    }
  }

  private checkCanEditPartida(partida: IPartidaPresupuestariaListado): void {
    if (partida.partidaPresupuestaria?.value.id) {
      this.subscriptions.push(
        this.proyectoPartidaService.hasAnyAnualidadAssociated(partida.partidaPresupuestaria?.value.id)
          .subscribe((hasAnualidades: boolean) => partida.canEdit = !hasAnualidades)
      );
    } else {
      partida.canEdit = true;
    }
  }

}
