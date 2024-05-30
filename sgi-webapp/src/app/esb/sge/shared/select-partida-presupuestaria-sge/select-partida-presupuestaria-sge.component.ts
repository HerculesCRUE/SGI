import { Component, Input, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceComponent } from '@core/component/select-service/select-service.component';
import { TipoPartida } from '@core/enums/tipo-partida';
import { IPartidaPresupuestariaSge } from '@core/models/sge/partida-presupuestaria-sge';
import { PartidaPresupuestariaGastoSgeService } from '@core/services/sge/partida-presupuestaria-sge/partida-presupuestaria-gasto-sge.service';
import { PartidaPresupuestariaIngresoSgeService } from '@core/services/sge/partida-presupuestaria-sge/partida-presupuestaria-ingreso-sge.service';
import { SgiRestListResult } from '@sgi/framework/http';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'sgi-select-partida-presupuestaria-sge[tipo]',
  templateUrl: '../../../../core/component/select-common/select-common.component.html',
  styleUrls: ['../../../../core/component/select-common/select-common.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectPartidaPresupuestariaSgeComponent
    }
  ]
})
export class SelectPartidaPresupuestariaSgeComponent extends SelectServiceComponent<IPartidaPresupuestariaSge> {

  @Input()
  get tipo(): TipoPartida {
    return this._tipo;
  }
  set tipo(value: TipoPartida) {
    const newValue = value;
    const changes = this._tipo !== newValue;
    this._tipo = newValue;
    if (this.ready && changes) {
      this.loadData();
    }
    this.stateChanges.next();
  }
  // tslint:disable-next-line: variable-name
  private _tipo: TipoPartida;

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    private partidaPresupuestariaGastoSgeServiceice: PartidaPresupuestariaGastoSgeService,
    private partidaPresupuestariaIngresoSgeServiceice: PartidaPresupuestariaIngresoSgeService,
    @Self() @Optional() ngControl: NgControl) {
    super(defaultErrorStateMatcher, ngControl);
    this.displayWith = (option) => option?.codigo ?? '';
  }

  protected loadServiceOptions(): Observable<IPartidaPresupuestariaSge[]> {
    if (!this.tipo) {
      return of([]);
    }

    let partidasPresupuestariasSge$: Observable<SgiRestListResult<IPartidaPresupuestariaSge>>;
    if (this.tipo === TipoPartida.GASTO) {
      partidasPresupuestariasSge$ = this.partidaPresupuestariaGastoSgeServiceice.findAll();
    } else {
      partidasPresupuestariasSge$ = this.partidaPresupuestariaIngresoSgeServiceice.findAll();
    }

    return partidasPresupuestariasSge$.pipe(
      map(response => response.items)
    )
  }

}
