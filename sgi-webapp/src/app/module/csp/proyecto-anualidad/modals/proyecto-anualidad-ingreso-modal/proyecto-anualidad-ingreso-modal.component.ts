import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { SelectValue } from '@core/component/select-common/select-common.component';
import { TipoPartida } from '@core/enums/tipo-partida';
import { MSG_PARAMS } from '@core/i18n';
import { IAnualidadIngreso } from '@core/models/csp/anualidad-ingreso';
import { IProyectoPartida } from '@core/models/csp/proyecto-partida';
import { IProyectoProyectoSge } from '@core/models/csp/proyecto-proyecto-sge';
import { ICodigoEconomicoGasto } from '@core/models/sge/codigo-economico-gasto';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { CodigoEconomicoGastoService } from '@core/services/sge/codigo-economico-gasto.service';
import { SelectValidator } from '@core/validators/select-validator';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const PROYECTO_PARTIDA_INGRESO_KEY = marker('csp.proyecto-anualidad.anualidad-ingreso');
const IDENTIFICADOR_SGE_KEY = marker('csp.proyecto-anualidad.partida-ingreso.identificador-sge');
const PARTIDA_PRESUPUESTARIA_KEY = marker('csp.proyecto-anualidad.partida.partida-presupuestaria');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface ProyectoAnualidadIngresoModalData {
  anualidadIngreso: IAnualidadIngreso;
  proyectoId: number;
  isEdit: boolean;
  readonly: boolean;
}

@Component({
  templateUrl: './proyecto-anualidad-ingreso-modal.component.html',
  styleUrls: ['./proyecto-anualidad-ingreso-modal.component.scss']
})
export class ProyectoAnualidadIngresoModalComponent extends DialogFormComponent<ProyectoAnualidadIngresoModalData> implements OnInit {

  textSaveOrUpdate: string;

  msgParamIdentificadorSge = {};
  msgParamFechaPrevistaEntity = {};
  msgParamPartidaPresupuestaria = {};
  title: string;

  readonly proyectosSge$ = new BehaviorSubject<IProyectoProyectoSge[]>([]);
  readonly proyectoPartida$ = new BehaviorSubject<IProyectoPartida[]>([]);
  codigosEconomicos$: Observable<ICodigoEconomicoGasto[]>;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    matDialogRef: MatDialogRef<ProyectoAnualidadIngresoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ProyectoAnualidadIngresoModalData,
    private readonly translate: TranslateService,
    private readonly proyectoService: ProyectoService,
    codigoEconomicoGastoService: CodigoEconomicoGastoService,
  ) {
    super(matDialogRef, data.isEdit);

    this.textSaveOrUpdate = this.data.isEdit ? MSG_ACEPTAR : MSG_ANADIR;

    this.subscriptions.push(
      this.proyectoService.findAllProyectosSgeProyecto(data.proyectoId)
        .subscribe(response => this.proyectosSge$.next(response.items))
    );

    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('tipoPartida', SgiRestFilterOperator.EQUALS, TipoPartida.INGRESO)
    };
    this.subscriptions.push(
      this.proyectoService.findAllProyectoPartidas(data.proyectoId, options)
        .subscribe(response => this.proyectoPartida$.next(response.items))
    );

    this.codigosEconomicos$ = codigoEconomicoGastoService.findAll().pipe(
      map(response => response.items)
    );
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.subscriptions.push(this.codigosEconomicos$.subscribe(
      (codigosEconomicos) => this.formGroup.controls.codigoEconomico.setValidators(
        SelectValidator.isSelectOption(codigosEconomicos.map(cod => cod.id), true)
      )
    ));
  }

  private setupI18N(): void {

    if (this.data.isEdit) {
      this.translate.get(
        PROYECTO_PARTIDA_INGRESO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        PROYECTO_PARTIDA_INGRESO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).pipe(
        switchMap((value) => {
          return this.translate.get(
            TITLE_NEW_ENTITY,
            { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
          );
        })
      ).subscribe((value) => this.title = value);
    }

    this.translate.get(
      IDENTIFICADOR_SGE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamIdentificadorSge = {
      entity: value, ...MSG_PARAMS.GENDER.MALE,
      ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });

    this.translate.get(
      PARTIDA_PRESUPUESTARIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamPartidaPresupuestaria = {
      entity: value, ...MSG_PARAMS.GENDER.MALE,
      ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });
  }

  protected getValue(): ProyectoAnualidadIngresoModalData {
    this.data.anualidadIngreso.proyectoSgeRef = this.formGroup.controls.identificadorSge.value.proyectoSge.id;
    this.data.anualidadIngreso.codigoEconomico = this.formGroup.controls.codigoEconomico.value;
    this.data.anualidadIngreso.importeConcedido = this.formGroup.controls.importeConcedido.value;
    this.data.anualidadIngreso.proyectoPartida = this.formGroup.controls.proyectoPartida.value;
    return this.data;
  }

  protected buildFormGroup(): FormGroup {

    const identificadorSge = this.data.anualidadIngreso?.proyectoSgeRef
      ? {
        proyectoSge:
          {
            id: this.data.anualidadIngreso?.proyectoSgeRef
          } as IProyectoSge
      } as IProyectoProyectoSge
      : null;
    const proyectoPartida = this.data.anualidadIngreso?.proyectoPartida
      ? {
        id: this.data.anualidadIngreso?.proyectoPartida.id,
        codigo: this.data.anualidadIngreso?.proyectoPartida.codigo
      } as IProyectoPartida
      : null;

    const proyectoPartidaFormControl = new FormControl(proyectoPartida, Validators.required);

    // TODO: Sincronizar la carga
    this.proyectoService.findAllProyectoPartidas(this.data.proyectoId).subscribe(partidas => {
      if (proyectoPartida == null && partidas.items.filter(partida => partida.tipoPartida === TipoPartida.INGRESO).length === 1) {
        proyectoPartidaFormControl.setValue(partidas.items.filter(partida => partida.tipoPartida === TipoPartida.INGRESO)[0]);
      } else {
        proyectoPartidaFormControl.setValue(proyectoPartida);
      }
    });

    const formGroup = new FormGroup(
      {
        identificadorSge: new FormControl(identificadorSge, [Validators.required]),
        proyectoPartida: proyectoPartidaFormControl,
        codigoEconomico: new FormControl(
          this.data.anualidadIngreso?.codigoEconomico?.id
            ? this.data.anualidadIngreso?.codigoEconomico
            : null
        ),
        importeConcedido: new FormControl(this.data.anualidadIngreso.importeConcedido, [Validators.required]),
      }
    );

    if (!this.data.anualidadIngreso?.proyectoSgeRef) {
      this.subscriptions.push(
        this.proyectosSge$.subscribe((values) => {
          if (values.length === 1) {
            this.formGroup.controls.identificadorSge.setValue(values[0]);
          }
        })
      );
    }

    if (this.data.readonly) {
      formGroup.disable();
    }

    return formGroup;
  }

  displayerIdentificadorSge(proyectoSge: IProyectoProyectoSge): string {
    return proyectoSge?.proyectoSge?.id;
  }

  sorterIdentificadorSge(o1: SelectValue<IProyectoSge>, o2: SelectValue<IProyectoSge>): number {
    return o1?.displayText.toString().localeCompare(o2?.displayText.toString());
  }

  comparerIdentificadorSge(o1: IProyectoSge, o2: IProyectoSge): boolean {
    if (o1 && o2) {
      return o1?.id === o2?.id;
    }
    return o1 === o2;
  }

  displayerProyectoPartida(proyectoPartida: IProyectoPartida): string {
    return proyectoPartida?.codigo;
  }

  displayerCodigoEconomico(codigoEconomico: ICodigoEconomicoGasto): string {
    return `${codigoEconomico?.id} - ${codigoEconomico?.nombre ?? ''}` ?? '';
  }

  sorterCodigoEconomico(o1: SelectValue<ICodigoEconomicoGasto>, o2: SelectValue<ICodigoEconomicoGasto>): number {
    return o1?.displayText.toString().localeCompare(o2?.displayText.toString());
  }

  comparerCodigoEconomico(o1: ICodigoEconomicoGasto, o2: ICodigoEconomicoGasto): boolean {
    if (o1 && o2) {
      return o1?.id === o2?.id;
    }
    return o1 === o2;
  }

}
