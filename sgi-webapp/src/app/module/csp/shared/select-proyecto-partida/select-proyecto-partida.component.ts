import { Component, Input, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceComponent } from '@core/component/select-service/select-service.component';
import { TipoPartida } from '@core/enums/tipo-partida';
import { IProyectoPartida } from '@core/models/csp/proyecto-partida';
import { IPartidaPresupuestariaSge } from '@core/models/sge/partida-presupuestaria-sge';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { PartidaPresupuestariaGastoSgeService } from '@core/services/sge/partida-presupuestaria-sge/partida-presupuestaria-gasto-sge.service';
import { PartidaPresupuestariaIngresoSgeService } from '@core/services/sge/partida-presupuestaria-sge/partida-presupuestaria-ingreso-sge.service';
import { SgiRestListResult } from '@sgi/framework/http';
import { Observable, from, of } from 'rxjs';
import { map, mergeMap, switchMap, toArray } from 'rxjs/operators';

@Component({
  selector: 'sgi-select-proyecto-partida[proyectoId][tipoPartida]',
  templateUrl: '../../../../core/component/select-common/select-common.component.html',
  styleUrls: ['../../../../core/component/select-common/select-common.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectProyectoPartidaComponent
    }
  ]
})
export class SelectProyectoPartidaComponent extends SelectServiceComponent<IProyectoPartida> {

  @Input()
  get proyectoId(): number {
    return this._proyectoId;
  }
  set proyectoId(value: number) {
    const changes = this._proyectoId !== value;
    this._proyectoId = value;
    if (this.ready && changes) {
      this.loadData();
    }
    this.stateChanges.next();
  }
  // tslint:disable-next-line: variable-name
  private _proyectoId: number;

  @Input()
  get tipoPartida(): TipoPartida {
    return this._tipoPartida;
  }
  set tipoPartida(value: TipoPartida) {
    const changes = this._tipoPartida !== value;
    this._tipoPartida = value;
    if (this.ready && changes) {
      this.loadData();
    }
    this.stateChanges.next();
  }
  // tslint:disable-next-line: variable-name
  private _tipoPartida: TipoPartida;

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl,
    private service: ProyectoService,
    private partidaPresupuestariaGastoSgeService: PartidaPresupuestariaGastoSgeService,
    private partidaPresupuestariaIngresoSgeService: PartidaPresupuestariaIngresoSgeService
  ) {
    super(defaultErrorStateMatcher, ngControl);
    // Override default displayWith
    this.displayWith = (option) => option?.codigo ?? option?.partidaSge?.codigo ?? '';
  }

  protected loadServiceOptions(): Observable<IProyectoPartida[]> {
    if (!this.proyectoId || !this.tipoPartida) {
      return of([]);
    }

    let proyectoPartidas$: Observable<SgiRestListResult<IProyectoPartida>>;
    if (this.tipoPartida === TipoPartida.GASTO) {
      proyectoPartidas$ = this.service.findAllProyectoPartidasGastos(this.proyectoId)
    } else {
      proyectoPartidas$ = this.service.findAllProyectoPartidasIngresos(this.proyectoId)
    }

    return proyectoPartidas$
      .pipe(
        map(response => response.items),
        switchMap(proyectoPartidas =>
          from(proyectoPartidas).pipe(
            mergeMap(proyectoPartida =>
              this.getPartidaPresupuestariaSge(proyectoPartida.partidaSge?.id, this.tipoPartida).pipe(
                map(partidaSge => {
                  proyectoPartida.partidaSge = partidaSge;
                  return proyectoPartida;
                })
              )
            ),
            toArray(),
            map(() => {
              return proyectoPartidas;
            })
          )
        )
      );
  }

  private getPartidaPresupuestariaSge(
    partidaSgeId: string,
    tipoPartida: TipoPartida
  ): Observable<IPartidaPresupuestariaSge> {
    if (!partidaSgeId || !tipoPartida) {
      return of(null);
    }

    let partidaPresupuestariaSge$: Observable<IPartidaPresupuestariaSge>;
    if (tipoPartida === TipoPartida.GASTO) {
      partidaPresupuestariaSge$ = this.partidaPresupuestariaGastoSgeService.findById(partidaSgeId);
    } else {
      partidaPresupuestariaSge$ = this.partidaPresupuestariaIngresoSgeService.findById(partidaSgeId);
    }

    return partidaPresupuestariaSge$;
  }

}
