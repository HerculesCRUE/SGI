import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatAutocompleteTrigger } from '@angular/material/autocomplete';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IMiembroEquipoProyecto } from '@core/models/csp/miembro-equipo-proyecto';
import { IRolProyecto } from '@core/models/csp/rol-proyecto';
import { RolProyectoService } from '@core/services/csp/rol-proyecto.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { DateValidator } from '@core/validators/date-validator';
import { IRange } from '@core/validators/range-validator';
import { TranslateService } from '@ngx-translate/core';
import { DateTime } from 'luxon';
import { merge, Observable, of } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const MIEMBRO_EQUIPO_PROYECTO_FECHA_FIN_KEY = marker('csp.miembro-equipo-proyecto.fecha-fin');
const MIEMBRO_EQUIPO_PROYECTO_FECHA_INICIO_KEY = marker('csp.miembro-equipo-proyecto.fecha-inicio');
const MIEMBRO_EQUIPO_PROYECTO_EQUIPO_MIEMBRO_KEY = marker('csp.miembro-equipo-proyecto.miembro');
const MIEMBRO_EQUIPO_PROYECTO_EQUIPO_ROL_PARTICIPACION_KEY = marker('csp.miembro-equipo-proyecto.rol-participacion');
const MIEMBRO_EQUIPO_PROYECTO_HORA_KEY = marker('csp.miembro-equipo-proyecto.hora');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface MiembroEquipoProyectoModalData {
  titleEntity: string;
  selectedEntidades: IMiembroEquipoProyecto[];
  entidad: IMiembroEquipoProyecto;
  fechaInicioMin: DateTime;
  fechaFinMax: DateTime;
  isEdit: boolean;
  readonly: boolean;
}

@Component({
  templateUrl: './miembro-equipo-proyecto-modal.component.html',
  styleUrls: ['./miembro-equipo-proyecto-modal.component.scss']
})
export class MiembroEquipoProyectoModalComponent extends DialogFormComponent<MiembroEquipoProyectoModalData> implements OnInit {

  @ViewChild(MatAutocompleteTrigger) autocomplete: MatAutocompleteTrigger;

  textSaveOrUpdate: string;

  rolesProyecto$: Observable<IRolProyecto[]>;
  colectivosIdRolParticipacion: string[];

  msgParamFechaFinEntity = {};
  msgParamFechaInicioEntity = {};
  msgParamMiembroEntity = {};
  msgParamRolParticipacionEntity = {};
  msgParamHoraEntity = {};
  title: string;

  constructor(
    matDialogRef: MatDialogRef<MiembroEquipoProyectoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: MiembroEquipoProyectoModalData,
    private personaService: PersonaService,
    private rolProyectoService: RolProyectoService,
    private readonly translate: TranslateService) {
    super(matDialogRef, !!data?.entidad?.rolProyecto);

    this.rolesProyecto$ = this.rolProyectoService.findAll().pipe(
      map(result => result.items)
    );
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.setupI18N();

    this.textSaveOrUpdate = this.data?.entidad?.rolProyecto ? MSG_ACEPTAR : MSG_ANADIR;

    this.loadColectivosRolProyecto(this.data.entidad?.rolProyecto?.id);

    this.subscriptions.push(
      this.formGroup.get('rolParticipacion').valueChanges
        .subscribe((rolProyecto) => {
          this.checkSelectedRol(rolProyecto);
        })
    );

    this.subscriptions.push(
      merge(
        this.formGroup.get('miembro').valueChanges,
        this.formGroup.get('fechaInicio').valueChanges,
        this.formGroup.get('fechaFin').valueChanges
      ).subscribe(() => this.checkRangesDates())
    );
  }

  private loadColectivosRolProyecto(rolProyectoId: number): void {
    this.subscriptions.push(
      this.getColectivosRolProyecto(rolProyectoId).subscribe()
    );
  }

  private getColectivosRolProyecto(rolProyectoId: number): Observable<string[]> {
    this.colectivosIdRolParticipacion = [];
    if (!rolProyectoId) {
      return of([]);
    }

    return this.rolProyectoService.findAllColectivos(rolProyectoId).pipe(
      map(res => res.items),
      tap(colectivos => this.colectivosIdRolParticipacion = colectivos)
    );
  }

  private setupI18N(): void {
    this.translate.get(
      MIEMBRO_EQUIPO_PROYECTO_EQUIPO_ROL_PARTICIPACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamRolParticipacionEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      MIEMBRO_EQUIPO_PROYECTO_EQUIPO_MIEMBRO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamMiembroEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      MIEMBRO_EQUIPO_PROYECTO_FECHA_FIN_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaFinEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE });

    this.translate.get(
      MIEMBRO_EQUIPO_PROYECTO_FECHA_INICIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaInicioEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE });

    this.translate.get(
      MIEMBRO_EQUIPO_PROYECTO_HORA_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamHoraEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE });


    if (this.data?.entidad?.rolProyecto) {
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
        rolParticipacion: new FormControl(this.data?.entidad?.rolProyecto, Validators.required),
        miembro: new FormControl({
          value: this.data?.entidad?.persona,
          disabled: !this.data.entidad.rolProyecto
        }, [
          Validators.required
        ]),
        fechaInicio: new FormControl(this.data?.entidad?.fechaInicio, [
          DateValidator.isBetween(this.data.fechaInicioMin, this.data.fechaFinMax)
        ]),
        fechaFin: new FormControl(this.data?.entidad?.fechaFin, [
          DateValidator.isBetween(this.data.fechaInicioMin, this.data.fechaFinMax)
        ])
      },
      {
        validators: [
          DateValidator.isAfter('fechaInicio', 'fechaFin', false)
        ]
      }
    );

    if (this.data.readonly) {
      formGroup.disable();
    }

    return formGroup;
  }

  protected getValue(): MiembroEquipoProyectoModalData {
    this.data.entidad.rolProyecto = this.formGroup.get('rolParticipacion').value;
    this.data.entidad.persona = this.formGroup.get('miembro').value;
    this.data.entidad.fechaInicio = this.formGroup.get('fechaInicio').value;
    this.data.entidad.fechaFin = this.formGroup.get('fechaFin').value;
    return this.data;
  }

  private checkSelectedRol(rolProyecto: IRolProyecto): void {
    if (rolProyecto && this.formGroup.controls.miembro.value) {
      this.subscriptions.push(
        this.getColectivosRolProyecto(rolProyecto?.id).pipe(
          switchMap(colectivos => {
            this.colectivosIdRolParticipacion = colectivos;
            return this.personaService.isPersonaInColectivo(this.formGroup.controls.miembro.value.id, this.colectivosIdRolParticipacion);
          })
        ).subscribe(result => {
          if (!result) {
            this.formGroup.controls.miembro.setValue(undefined);
          }
        })
      );
    } else if (rolProyecto) {
      this.subscriptions.push(
        this.getColectivosRolProyecto(rolProyecto.id).subscribe(() => {
          if (this.formGroup.controls.miembro.disabled) {
            this.formGroup.controls.miembro.enable();
          }
        })
      );
    } else if (!rolProyecto) {
      this.formGroup.controls.miembro.disable();
      this.formGroup.controls.miembro.setValue(undefined);
    }
  }

  private checkRangesDates(): void {
    const miembroForm = this.formGroup.get('miembro');
    const fechaInicioForm = this.formGroup.get('fechaInicio');
    const fechaFinForm = this.formGroup.get('fechaFin');

    const fechaInicio = fechaInicioForm.value ? fechaInicioForm.value.toMillis() : Number.MIN_VALUE;
    const fechaFin = fechaFinForm.value ? fechaFinForm.value.toMillis() : Number.MAX_VALUE;
    const ranges = this.data.selectedEntidades.filter(element => element.persona.id === miembroForm.value?.id)
      .map(value => {
        const range: IRange = {
          inicio: value.fechaInicio ? value.fechaInicio.toMillis() : Number.MIN_VALUE,
          fin: value.fechaFin ? value.fechaFin.toMillis() : Number.MAX_VALUE,
        };
        return range;
      });

    if (ranges.some(range => (fechaInicio <= range.fin && range.inicio <= fechaFin))) {
      if (fechaInicioForm.value) {
        this.addError(fechaInicioForm, 'range');
      }
      if (fechaFinForm.value) {
        this.addError(fechaFinForm, 'range');
      }
      if (!fechaInicioForm.value && !fechaFinForm.value) {
        this.addError(miembroForm, 'contains');
      } else if (miembroForm.errors) {
        this.deleteError(miembroForm, 'contains');
      }
    } else {
      this.deleteError(fechaInicioForm, 'range');
      this.deleteError(fechaFinForm, 'range');
      this.deleteError(miembroForm, 'contains');
    }
  }

  private deleteError(formControl: AbstractControl, errorName: string): void {
    if (formControl.errors) {
      delete formControl.errors[errorName];
      if (Object.keys(formControl.errors).length === 0) {
        formControl.setErrors(null);
      }
    }
  }

  private addError(formControl: AbstractControl, errorName: string): void {
    if (!formControl.errors) {
      formControl.setErrors({});
    }
    formControl.errors[errorName] = true;
    formControl.markAsTouched({ onlySelf: true });
  }

}
