import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { TipoPartida } from '@core/enums/tipo-partida';
import { IConvocatoriaPartidaPresupuestaria } from '@core/models/csp/convocatoria-partida-presupuestaria';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoPartida } from '@core/models/csp/proyecto-partida';
import { IPartidaPresupuestariaSge } from '@core/models/sge/partida-presupuestaria-sge';
import { Fragment } from '@core/services/action-service';
import { ConfigService } from '@core/services/csp/configuracion/config.service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { ProyectoPartidaPresupuestariaService } from '@core/services/csp/proyecto-partida-presupuestaria/proyecto-partida-presupuestaria.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { PartidaPresupuestariaGastoSgeService } from '@core/services/sge/partida-presupuestaria-sge/partida-presupuestaria-gasto-sge.service';
import { PartidaPresupuestariaIngresoSgeService } from '@core/services/sge/partida-presupuestaria-sge/partida-presupuestaria-ingreso-sge.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, concat, forkJoin, from, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap, toArray } from 'rxjs/operators';
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
  partidaSge: IPartidaPresupuestariaSge;
  descripcion: string;
  tipoPartida: TipoPartida;
  canEdit: boolean;
}

export class ProyectoPartidasPresupuestariasFragment extends Fragment {
  partidasPresupuestarias$ = new BehaviorSubject<IPartidaPresupuestariaListado[]>([]);
  private partidasPresupuestariasEliminadasIds: number[] = [];

  mapModificable: Map<number, boolean> = new Map();

  _partidasPresupuestariasSgeEnabled: boolean;
  get partidasPresupuestariasSgeEnabled(): boolean {
    return this._partidasPresupuestariasSgeEnabled;
  }

  constructor(
    key: number,
    private proyecto: IProyecto,
    private readonly configService: ConfigService,
    private readonly convocatoriaService: ConvocatoriaService,
    private readonly partidaPresupuestariaGastoSgeService: PartidaPresupuestariaGastoSgeService,
    private readonly partidaPresupuestariaIngresoSgeService: PartidaPresupuestariaIngresoSgeService,
    private readonly proyectoPartidaPresupuestariaService: ProyectoPartidaPresupuestariaService,
    private readonly proyectoService: ProyectoService,
    public readonly: boolean
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (!this.getKey()) {
      this.subscriptions.push(
        this.configService.isPartidasPresupuestariasSgeEnabled().subscribe(partidasPresupuestariasSgeEnabled => {
          this._partidasPresupuestariasSgeEnabled = partidasPresupuestariasSgeEnabled;
        })
      );
      return;
    }

    this.subscriptions.push(
      forkJoin({
        partidasPresupuestariasListado: this.proyectoService.findAllProyectoPartidas(this.getKey() as number).pipe(
          map((response) => response.items.map(item => {
            const partidaPresupuestariaListado = {
              partidaPresupuestaria: new StatusWrapper<IProyectoPartida>(item),
            } as IPartidaPresupuestariaListado;
            return partidaPresupuestariaListado;
          }))
        ),
        partidasPresupuestariasSgeEnabled: this.configService.isPartidasPresupuestariasSgeEnabled()
      }).pipe(
        tap(({ partidasPresupuestariasSgeEnabled }) => {
          this._partidasPresupuestariasSgeEnabled = partidasPresupuestariasSgeEnabled;
        }),
        switchMap(({ partidasPresupuestariasListado }) => {
          if (!this.proyecto.convocatoriaId) {
            return of(partidasPresupuestariasListado);
          }

          return this.convocatoriaService
            .findPartidasPresupuestarias(this.proyecto.convocatoriaId)
            .pipe(
              map((response) => response.items),
              map(convocatoriaPartidaPresupuestarias => {
                partidasPresupuestariasListado.forEach(partidaPresupuestariaListado => {
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
                  partidasPresupuestariasListado.push(...convocatoriaPartidaPresupuestarias.map(convocatoriaPartidaPresupuestaria => {
                    const partidaPresupuestoListado = {
                      convocatoriaPartidaPresupuestaria
                    } as IPartidaPresupuestariaListado;
                    return partidaPresupuestoListado;
                  }));
                }

                return partidasPresupuestariasListado;
              })
            );
        }),
        switchMap((partidasPresupuestariasListado) =>
          from(partidasPresupuestariasListado).pipe(
            mergeMap(partidaPresupuestariaListado => {

              const partidaSgeId = !!partidaPresupuestariaListado.partidaPresupuestaria
                ? partidaPresupuestariaListado.partidaPresupuestaria.value.partidaSge?.id
                : partidaPresupuestariaListado.convocatoriaPartidaPresupuestaria.partidaSge?.id;

              const tipoPartida = !!partidaPresupuestariaListado.partidaPresupuestaria
                ? partidaPresupuestariaListado.partidaPresupuestaria.value.tipoPartida
                : partidaPresupuestariaListado.convocatoriaPartidaPresupuestaria.tipoPartida;

              return forkJoin({
                modificable: this.isModificable(partidaPresupuestariaListado.partidaPresupuestaria?.value?.id),
                partidaPresupuestariaSge: this.getPartidaPresupuestariaSge(partidaSgeId, tipoPartida)
              }).pipe(
                map(({ modificable, partidaPresupuestariaSge }) => {
                  if (!!partidaPresupuestariaListado.partidaPresupuestaria) {
                    this.mapModificable.set(partidaPresupuestariaListado.partidaPresupuestaria.value.id, modificable);
                    partidaPresupuestariaListado.partidaPresupuestaria.value.partidaSge = partidaPresupuestariaSge;
                  } else {
                    partidaPresupuestariaListado.convocatoriaPartidaPresupuestaria.partidaSge = partidaPresupuestariaSge;
                  }
                  return partidaPresupuestariaListado;
                })
              )
            }),
            toArray(),
            map(() => {
              return partidasPresupuestariasListado;
            })
          )
        )
      ).subscribe((response) => {
        response.forEach(partida => {
          this.fillListadoFields(partida)
          this.checkCanEditPartida(partida);
        });
        this.partidasPresupuestarias$.next(response);
      }));

  }

  addPartidaPresupuestaria(partidaPresupuestaria: IProyectoPartida, convocatoriaPartida?: IConvocatoriaPartidaPresupuestaria) {
    const wrapped = new StatusWrapper<IProyectoPartida>(partidaPresupuestaria);
    wrapped.setCreated();
    const current = this.partidasPresupuestarias$.value;
    partidaPresupuestaria.convocatoriaPartidaId = convocatoriaPartida?.id;
    const partidaPresupuestariaListado: IPartidaPresupuestariaListado = {
      partidaPresupuestaria: wrapped,
      convocatoriaPartidaPresupuestaria: convocatoriaPartida,
      codigo: partidaPresupuestaria.codigo,
      partidaSge: partidaPresupuestaria.partidaSge,
      descripcion: partidaPresupuestaria.descripcion,
      tipoPartida: partidaPresupuestaria.tipoPartida,
      canEdit: true,
      help: null
    };

    if (convocatoriaPartida) {
      const index = current.findIndex(value => value.convocatoriaPartidaPresupuestaria?.id === convocatoriaPartida.id);
      this.fillListadoFields(partidaPresupuestariaListado);
      current[index] = partidaPresupuestariaListado;
    } else {
      if (this.proyecto.convocatoriaId) {
        partidaPresupuestariaListado.help = {
          class: HelpIconClass.WARNING,
          tooltip: PARTIDA_PRESUPUESTARIA_NO_CONVOCATORIA_KEY
        };
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
      partidaPresupuestariaListado.partidaSge = wrapper.value.partidaSge;
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
      } else if (this.proyecto.convocatoriaId) {
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
      (value) => value.partidaPresupuestaria.value === wrapper.value
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
        return this.proyectoPartidaPresupuestariaService.deleteById(partidaPresupuestariaId)
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
        return this.proyectoPartidaPresupuestariaService.create(partidaPresupuestariaListado.partidaPresupuestaria.value).pipe(
          map((createdPartidaPresupuestaria) => {
            const index = this.partidasPresupuestarias$.value
              .findIndex((currentPartidaPresupuestariaListado) => currentPartidaPresupuestariaListado.partidaPresupuestaria?.value.id
                === partidaPresupuestariaListado.partidaPresupuestaria.value.id);
            const partidasPresupuestariaListado = partidaPresupuestariaListado.partidaPresupuestaria.value;
            partidasPresupuestariaListado.id = createdPartidaPresupuestaria.id;

            this.partidasPresupuestarias$.value[index] = {
              partidaPresupuestaria: new StatusWrapper<IProyectoPartida>(partidasPresupuestariaListado),
              convocatoriaPartidaPresupuestaria: partidaPresupuestariaListado.convocatoriaPartidaPresupuestaria,
              codigo: createdPartidaPresupuestaria.codigo,
              partidaSge: partidaPresupuestariaListado.partidaSge,
              descripcion: createdPartidaPresupuestaria.descripcion,
              tipoPartida: createdPartidaPresupuestaria.tipoPartida,
              canEdit: true,
              help: partidaPresupuestariaListado.help
            };
            this.mapModificable.set(createdPartidaPresupuestaria.id, true);
            this.partidasPresupuestarias$.next(this.partidasPresupuestarias$.value);
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
        return this.proyectoPartidaPresupuestariaService.update(partidaPresupuestariaListado.partidaPresupuestaria.value.id,
          partidaPresupuestariaListado.partidaPresupuestaria.value).pipe(
            map((updatedPartidaPresupuestaria) => {
              const index = this.partidasPresupuestarias$.value
                .findIndex((currentPartidaPresupuestaria) => currentPartidaPresupuestaria === partidaPresupuestariaListado);
              this.partidasPresupuestarias$.value[index] = {
                partidaPresupuestaria: new StatusWrapper<IProyectoPartida>(updatedPartidaPresupuestaria),
                convocatoriaPartidaPresupuestaria: this.partidasPresupuestarias$.value[index].convocatoriaPartidaPresupuestaria,
                codigo: updatedPartidaPresupuestaria.codigo,
                partidaSge: partidaPresupuestariaListado.partidaSge,
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
      partidasPresupuestaria.partidaSge = partidasPresupuestaria.partidaPresupuestaria.value.partidaSge;
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
      } else if (this.proyecto.convocatoriaId) {
        partidasPresupuestaria.help = {
          class: HelpIconClass.WARNING,
          tooltip: PARTIDA_PRESUPUESTARIA_NO_CONVOCATORIA_KEY
        };
      }
    } else {
      partidasPresupuestaria.codigo = partidasPresupuestaria.convocatoriaPartidaPresupuestaria.codigo;
      partidasPresupuestaria.partidaSge = partidasPresupuestaria.convocatoriaPartidaPresupuestaria.partidaSge;
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
        this.proyectoPartidaPresupuestariaService.hasAnyAnualidadAssociated(partida.partidaPresupuestaria?.value.id)
          .subscribe((hasAnualidades: boolean) => partida.canEdit = !hasAnualidades)
      );
    } else {
      partida.canEdit = true;
    }
  }

  private getPartidaPresupuestariaSge(partidaSgeId: string, tipo: TipoPartida): Observable<IPartidaPresupuestariaSge> {
    if (!partidaSgeId || !tipo) {
      return of(null);
    }

    if (tipo === TipoPartida.GASTO) {
      return this.partidaPresupuestariaGastoSgeService.findById(partidaSgeId);
    } else {
      return this.partidaPresupuestariaIngresoSgeService.findById(partidaSgeId);
    }
  }

  private isModificable(partidaPresupuestariaId: number): Observable<boolean> {
    if (this.readonly || !partidaPresupuestariaId) {
      return of(false);
    }

    return this.proyectoPartidaPresupuestariaService.modificable(partidaPresupuestariaId);
  }

}
