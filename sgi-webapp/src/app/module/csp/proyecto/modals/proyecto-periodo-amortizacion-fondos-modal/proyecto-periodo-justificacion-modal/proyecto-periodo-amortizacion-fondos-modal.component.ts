import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { SelectValue } from '@core/component/select-common/select-common.component';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoAnualidad } from '@core/models/csp/proyecto-anualidad';
import { IProyectoPeriodoAmortizacion } from '@core/models/csp/proyecto-periodo-amortizacion';
import { IProyectoProyectoSge } from '@core/models/csp/proyecto-proyecto-sge';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { SgiRestListResult } from '@sgi/framework/http';
import { BehaviorSubject, merge } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';
import { IEntidadFinanciadora } from '../../../proyecto-formulario/proyecto-entidades-financiadoras/proyecto-entidades-financiadoras.fragment';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const TITLE_NEW_ENTITY = marker('title.new.entity');
const PROYECTO_PERIODO_AMORTIZACION_FONDOS_KEY = marker('csp.proyecto-amortizacion-fondos.periodo-amortizacion');
const PROYECTO_SGE_KEY = marker('csp.proyecto-amortizacion-fondos.periodo-amortizacion.identificador-sge');
const ENTIDAD_FINANCIADORA_KEY = marker('csp.proyecto-amortizacion-fondos.entidad-financiadora');
const ANUALIDAD_KEY = marker('csp.proyecto-amortizacion-fondos.periodo-amortizacion.anualidad');
const IMPORTE_KEY = marker('csp.proyecto-amortizacion-fondos.periodo-amortizacion.importe');
const FECHA_LIMITE_AMORTIZACION_KEY = marker('csp.proyecto-amortizacion-fondos.periodo-amortizacion.fecha-limite-amortizacion');
const TIPO_FINANCIACION_NO_INFORMADO = marker('csp.proyecto-amortizacion-fondos.periodo-amortizacion.tipo-financiacion-no-informado');
const FUENTE_FINANCIACION_NO_INFORMADA = marker('csp.proyecto-amortizacion-fondos.periodo-amortizacion.fuente-financiacion-no-informada');
export interface IProyectoPeriodoAmortizacionModalData {
  proyectoId: number;
  title: string;
  periodoAmortizacion: IProyectoPeriodoAmortizacion;
  entidadesFinanciadoras: IEntidadFinanciadora[];
  proyectosSGE: IProyectoProyectoSge[];
  anualidadGenerica: boolean;
}

@Component({
  selector: 'sgi-proyecto-periodo-amortizacion-fondos-modal',
  templateUrl: './proyecto-periodo-amortizacion-fondos-modal.component.html',
  styleUrls: ['./proyecto-periodo-amortizacion-fondos-modal.component.scss']
})
export class ProyectoPeriodoAmortizacionModalComponent
  extends DialogFormComponent<IProyectoPeriodoAmortizacionModalData> implements OnInit {

  anualidades$ = new BehaviorSubject<IProyectoAnualidad[]>([]);
  anualidadGenerica: IProyectoAnualidad;

  msgParamPeriodoAmortizacionEntity = {};
  msgParamImporte = {};
  msgParamFechaLimiteAmortizacion = {};
  msgParamProyectoSgeEntity = {};
  msgParamEntidadFinanciadoraEntity = {};
  msgParamAnualidadEntity = {};

  textSaveOrUpdate: string;
  title: string;

  readonly displayerEntidadFinanciadora = (entidadFinanciadora: IEntidadFinanciadora): string => {
    let fuente = this.translate.instant(FUENTE_FINANCIACION_NO_INFORMADA);
    let tipo = this.translate.instant(TIPO_FINANCIACION_NO_INFORMADO);
    if (entidadFinanciadora?.fuenteFinanciacion?.nombre) {
      fuente = entidadFinanciadora?.fuenteFinanciacion?.nombre;
    }
    if (entidadFinanciadora?.tipoFinanciacion?.nombre) {
      tipo = entidadFinanciadora?.tipoFinanciacion?.nombre;
    }
    return entidadFinanciadora?.empresa?.nombre + ' - ' + fuente + ' - ' + tipo;
  }

  readonly comparerIdentificadorSge = (o1: StatusWrapper<IProyectoProyectoSge>, o2: StatusWrapper<IProyectoProyectoSge>): boolean => {
    if (o1 && o2) {
      return o1?.value?.proyectoSge?.id === o2?.value?.proyectoSge?.id;
    }
    return o1 === o2;
  }
  readonly displayerIdentificadorSge = (proyectoProyectoSGE: IProyectoProyectoSge): string => proyectoProyectoSGE.proyectoSge?.id ?? '';

  readonly sorterIdentificadorSge = (o1: SelectValue<IProyectoProyectoSge>, o2: SelectValue<IProyectoProyectoSge>): number =>
    o1?.displayText.localeCompare(o2?.displayText);

  readonly displayerAnualidad = (proyectoAnualidad: IProyectoAnualidad): string => proyectoAnualidad?.anio?.toString() ?? '';

  constructor(
    matDialogRef: MatDialogRef<ProyectoPeriodoAmortizacionModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: IProyectoPeriodoAmortizacionModalData,
    private proyectoService: ProyectoService,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data.periodoAmortizacion?.proyectoEntidadFinanciadora);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.textSaveOrUpdate = this.data.periodoAmortizacion?.proyectoEntidadFinanciadora ? MSG_ACEPTAR : MSG_ANADIR;

    this.subscriptions.push(this.formGroup.get('anualidad').valueChanges.subscribe(
      (value) => {
        this.formGroup.controls.fechaInicioAnualidad.setValue(value.fechaInicio);
        this.formGroup.controls.fechaFinAnualidad.setValue(value.fechaFin);
      }
    ));

    if (this.data.proyectosSGE.length === 1) {
      this.formGroup.controls.identificadorSge.setValue(this.data.proyectosSGE[0]);
    }

    const subscription =
      merge(
        this.proyectoService.findAllProyectoAnualidadesByProyectoId(this.data.proyectoId).pipe(
          map((response: SgiRestListResult<IProyectoAnualidad>) => {
            return response.items;
          }),
          tap((value) => {
            this.anualidades$.next(value);
            if (!!this.data.anualidadGenerica) {
              this.anualidadGenerica = value[0];
            }
          }),
        ),
      ).subscribe();
    this.subscriptions.push(subscription);
  }


  protected buildFormGroup(): FormGroup {
    const identificadorSge = this.data.periodoAmortizacion?.proyectoSGE
      ? {
        proyectoSge:
          {
            id: this.data.periodoAmortizacion?.proyectoSGE.id
          } as IProyectoSge
      } as IProyectoProyectoSge
      : null;
    const entidadFinanciadora = this.data.periodoAmortizacion?.proyectoEntidadFinanciadora ?? null;
    const anualidad = this.data.anualidadGenerica ? this.anualidadGenerica : (this.data.periodoAmortizacion?.proyectoAnualidad ?? null);
    const fechaInicioAnualidad = this.data.periodoAmortizacion?.proyectoAnualidad?.fechaInicio ?? null;
    const fechaFinAnualidad = this.data.periodoAmortizacion?.proyectoAnualidad?.fechaFin ?? null;
    const fechaLimiteAmortizacion = this.data.periodoAmortizacion?.fechaLimiteAmortizacion ?? null;
    const importe = this.data.periodoAmortizacion?.importe ?? null;

    return new FormGroup(
      {
        identificadorSge: new FormControl(identificadorSge, Validators.required),
        entidadFinanciadora: new FormControl(entidadFinanciadora, Validators.required),
        anualidad: new FormControl({ value: anualidad, disabled: this.data.anualidadGenerica }, Validators.required),
        fechaInicioAnualidad: new FormControl({ value: fechaInicioAnualidad, disabled: true }),
        fechaFinAnualidad: new FormControl({ value: fechaFinAnualidad, disabled: true }),
        fechaLimiteAmortizacion: new FormControl(fechaLimiteAmortizacion, Validators.required
        ),
        importe: new FormControl(importe,
          [
            Validators.min(0),
            Validators.max(2_147_483_647),
            Validators.required
          ]),
      }
    );
  }

  protected getValue(): IProyectoPeriodoAmortizacionModalData {
    this.data.periodoAmortizacion.proyectoSGE = this.formGroup.controls.identificadorSge.value?.proyectoSge;
    this.data.periodoAmortizacion.proyectoEntidadFinanciadora = this.formGroup.controls.entidadFinanciadora.value;
    this.data.periodoAmortizacion.proyectoAnualidad = this.anualidadGenerica ?? this.formGroup.controls.anualidad.value;
    this.data.periodoAmortizacion.importe = this.formGroup.controls.importe.value;
    this.data.periodoAmortizacion.fechaLimiteAmortizacion = this.formGroup.controls.fechaLimiteAmortizacion.value;

    return this.data;
  }

  private setupI18N(): void {

    this.translate.get(
      FECHA_LIMITE_AMORTIZACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamFechaLimiteAmortizacion = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_PERIODO_AMORTIZACION_FONDOS_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamPeriodoAmortizacionEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      IMPORTE_KEY
    ).subscribe((value) => this.msgParamImporte = {
      entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });

    this.translate.get(
      PROYECTO_SGE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamProyectoSgeEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      ENTIDAD_FINANCIADORA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamEntidadFinanciadoraEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      ANUALIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamAnualidadEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.data.periodoAmortizacion?.proyectoEntidadFinanciadora) {
      this.translate.get(
        PROYECTO_PERIODO_AMORTIZACION_FONDOS_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);

    } else {
      this.translate.get(
        PROYECTO_PERIODO_AMORTIZACION_FONDOS_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).pipe(
        switchMap((value) => {
          return this.translate.get(
            TITLE_NEW_ENTITY,
            { entity: value, ...MSG_PARAMS.GENDER.MALE }
          );
        })
      ).subscribe((value) => this.title = value);
    }

  }

}
