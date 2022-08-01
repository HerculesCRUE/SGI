import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoLineaInvestigador } from '@core/models/csp/grupo-linea-investigador';
import { IPersona } from '@core/models/sgp/persona';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { DateValidator } from '@core/validators/date-validator';
import { TranslateService } from '@ngx-translate/core';
import { DateTime, Interval } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from } from 'rxjs';
import { map, mergeMap, switchMap } from 'rxjs/operators';
import { IGrupoEquipoListado } from '../../../grupo/grupo-formulario/grupo-equipo-investigacion/grupo-equipo-investigacion.fragment';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const GRUPO_LINEA_INVESTIGADOR_FECHA_FIN_KEY = marker('csp.grupo-linea-investigador.fecha-fin');
const GRUPO_LINEA_INVESTIGADOR_FECHA_INICIO_KEY = marker('csp.grupo-linea-investigador.fecha-inicio');
const GRUPO_LINEA_INVESTIGADOR_LINEA_INVESTIGADOR_KEY = marker('csp.grupo-linea-investigador');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface GrupoLineaInvestigadorModalData {
  titleEntity: string;
  idGrupo: number;
  selectedEntidades: IGrupoLineaInvestigador[];
  entidad: IGrupoLineaInvestigador;
  fechaInicioMin: DateTime;
  fechaFinMax: DateTime;
  isEdit: boolean;
}

@Component({
  templateUrl: './grupo-linea-investigador-modal.component.html',
  styleUrls: ['./grupo-linea-investigador-modal.component.scss']
})
export class GrupoLineaInvestigadorModalComponent extends DialogFormComponent<GrupoLineaInvestigadorModalData> implements OnInit {

  textSaveOrUpdate: string;

  miembrosEquipo$ = new BehaviorSubject<StatusWrapper<IPersona>[]>([]);

  msgParamFechaFinEntity = {};
  msgParamFechaInicioEntity = {};
  msgParamMiembroEntity = {};
  msgParamRolParticipacionEntity = {};
  msgParamHoraEntity = {};
  msgParamDedicacionEntity = {};
  msgParamParticipacionEntity = {};
  title: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    private readonly logger: NGXLogger,
    matDialogRef: MatDialogRef<GrupoLineaInvestigadorModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: GrupoLineaInvestigadorModalData,
    private readonly translate: TranslateService,
    private grupoService: GrupoService,
    private personaService: PersonaService,
  ) {
    super(matDialogRef, !!data?.isEdit);

    this.textSaveOrUpdate = this.data?.isEdit ? MSG_ACEPTAR : MSG_ANADIR;

    this.data.selectedEntidades?.sort((a, b) => {
      if (!!!a.fechaInicio) {
        return -1;
      }
      return a.fechaInicio.toMillis() - b.fechaInicio.toMillis();
    });

    this.subscriptions.push(
      this.grupoService.findMiembrosEquipo(this.data.idGrupo).pipe(
        switchMap(result => {
          return from(result.items).pipe(
            mergeMap(element => {
              return this.personaService.findById(element.persona.id).pipe(
                map(persona => {
                  element.persona = persona;
                  return element as IGrupoEquipoListado;
                })
              );
            }),
            map(() => result)
          );
        }),
        map(miembrosEquipo => {
          return miembrosEquipo.items.map(miembroEquipo => {
            miembroEquipo.grupo = { id: this.data.idGrupo } as IGrupo;
            return new StatusWrapper<IGrupoEquipoListado>(miembroEquipo as IGrupoEquipoListado);
          });
        }),
      ).subscribe(
        result => {
          this.miembrosEquipo$.next(result.map(
            equipo => new StatusWrapper<IPersona>(equipo.value.persona)));
        },
        error => {
          this.logger.error(error);
        }
      )
    );
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {

    this.translate.get(
      GRUPO_LINEA_INVESTIGADOR_LINEA_INVESTIGADOR_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamMiembroEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      GRUPO_LINEA_INVESTIGADOR_FECHA_FIN_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaFinEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE });

    this.translate.get(
      GRUPO_LINEA_INVESTIGADOR_FECHA_INICIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaInicioEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE });


    if (this.data?.isEdit) {
      this.translate.get(
        this.data.titleEntity,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        this.data.titleEntity,
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

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup(
      {
        miembro: new FormControl(this.data?.entidad?.persona, [Validators.required]),
        fechaInicio: new FormControl(this.data?.entidad?.fechaInicio ?? this.data.fechaInicioMin),
        fechaFin: new FormControl(this.data?.entidad?.fechaFin),
      }
    );

    this.subscriptions.push(formGroup.controls.fechaInicio.statusChanges.subscribe(
      () => {
        if (formGroup.controls.fechaFin.value) {
          formGroup.controls.fechaFin.markAsTouched();
          formGroup.controls.fechaFin.updateValueAndValidity();
        } else {
          this.validateFechaFinNull();
        }
      }
    ));

    this.subscriptions.push(formGroup.controls.miembro.valueChanges.subscribe(
      (value) => {
        this.setupValidators(formGroup, value.value);
        formGroup.controls.fechaInicio.markAsTouched();
        formGroup.controls.fechaInicio.updateValueAndValidity();
        this.validateFechaFinNull(value.value);
      }
    ));

    this.subscriptions.push(formGroup.controls.fechaFin.valueChanges.subscribe(
      (value) => {
        if (value === null) {
          this.validateFechaFinNull();
        }
      }
    ));

    if (this.data?.entidad?.persona) {
      this.setupValidators(formGroup, this.data?.entidad?.persona);
    }

    return formGroup;
  }

  private setupValidators(formGroup: FormGroup, persona?: IPersona): void {

    const miembroBuscado = this.data.selectedEntidades?.find(miembro => miembro.persona.id === persona?.id);

    const intervals: Interval[] = this.data.selectedEntidades?.filter(
      miembro => miembroBuscado?.persona?.id === miembro.persona?.id)
      .map(miembro => {
        return Interval.fromDateTimes(
          miembro.fechaInicio ? miembro.fechaInicio : this.data.fechaInicioMin,
          miembro.fechaFin ? miembro.fechaFin : this.data.fechaFinMax
        );
      });

    formGroup.controls.fechaInicio.setValidators([
      Validators.required,
      DateValidator.isBetween(this.data.fechaInicioMin, this.data.fechaFinMax),
      DateValidator.notOverlaps(intervals),
      DateValidator.notOverlapsDependentForStart(intervals, formGroup.controls.fechaFin)
    ]);

    formGroup.controls.fechaFin.setValidators([
      DateValidator.isAfterOther(formGroup.controls.fechaInicio),
      DateValidator.isBetween(this.data.fechaInicioMin, this.data.fechaFinMax),
      DateValidator.notOverlaps(intervals),
      DateValidator.notOverlapsDependentForEnd(intervals, formGroup.controls.fechaInicio)
    ]);
  }

  protected getValue(): GrupoLineaInvestigadorModalData {
    this.data.entidad.persona = this.formGroup.get('miembro').value;
    if (!this.data.entidad.persona.nombre) {
      this.data.entidad.persona = this.formGroup.get('miembro').value.value;
    }
    this.data.entidad.fechaInicio = this.formGroup.get('fechaInicio').value;
    this.data.entidad.fechaFin = this.formGroup.get('fechaFin').value;
    return this.data;
  }

  private validateFechaFinNull(persona?: IPersona) {
    const fechaFinForm = this.formGroup.get('fechaFin');
    if (!fechaFinForm.value) {
      const miembroBuscado = this.data.selectedEntidades?.find(
        miembro => miembro.persona.id === (persona?.id ?? this.formGroup.get('miembro').value?.id));
      const fechaInicio: DateTime = this.formGroup.get('fechaInicio').value;
      if (miembroBuscado && miembroBuscado.fechaFin !== null && fechaInicio.toMillis() <= miembroBuscado.fechaFin.toMillis()) {
        fechaFinForm.setErrors({ overlaps: true });
        fechaFinForm.markAsTouched({ onlySelf: true });
      } else if (fechaFinForm.errors) {
        delete fechaFinForm.errors.overlaps;
        fechaFinForm.updateValueAndValidity({ onlySelf: true });
      }
    }
  }

  displayerMiembroEquipo(persona: StatusWrapper<IPersona> | IPersona): string {
    const p = persona as IPersona;
    if (p?.nombre) {
      return p?.nombre ? (p?.nombre + ' ' + p?.apellidos) : '';
    } else {
      const wrapperPersona = persona as StatusWrapper<IPersona>;
      return wrapperPersona?.value?.nombre ? (wrapperPersona?.value?.nombre + ' ' + wrapperPersona?.value?.apellidos) : '';
    }
  }

}
