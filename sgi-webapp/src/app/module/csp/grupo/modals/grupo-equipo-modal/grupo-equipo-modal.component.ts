import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatAutocompleteTrigger } from '@angular/material/autocomplete';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IGrupo } from '@core/models/csp/grupo';
import { Dedicacion, DEDICACION_MAP, IGrupoEquipo, PARTICIPACION_MAX } from '@core/models/csp/grupo-equipo';
import { IRolProyecto } from '@core/models/csp/rol-proyecto';
import { IPersona } from '@core/models/sgp/persona';
import { GrupoEquipoService } from '@core/services/csp/grupo-equipo/grupo-equipo.service';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { RolProyectoService } from '@core/services/csp/rol-proyecto/rol-proyecto.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { DateValidator } from '@core/validators/date-validator';
import { IRange } from '@core/validators/range-validator';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { DateTime } from 'luxon';
import { combineLatest, from, merge, Observable, of } from 'rxjs';
import { filter, map, mergeMap, startWith, switchMap, tap, toArray } from 'rxjs/operators';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const MIEMBRO_EQUIPO_PROYECTO_FECHA_FIN_KEY = marker('csp.miembro-equipo-proyecto.fecha-fin');
const MIEMBRO_EQUIPO_PROYECTO_FECHA_INICIO_KEY = marker('csp.miembro-equipo-proyecto.fecha-inicio');
const MIEMBRO_EQUIPO_PROYECTO_EQUIPO_MIEMBRO_KEY = marker('csp.miembro-equipo-proyecto.miembro');
const MIEMBRO_EQUIPO_PROYECTO_EQUIPO_ROL_PARTICIPACION_KEY = marker('csp.miembro-equipo-proyecto.rol-participacion');
const MIEMBRO_EQUIPO_PROYECTO_HORA_KEY = marker('csp.miembro-equipo-proyecto.hora');
const GRUPO_EQUIPO_DEDICACION_KEY = marker('csp.grupo-equipo.dedicacion');
const GRUPO_EQUIPO_PARTICIPACION_KEY = marker('csp.grupo-equipo.participacion');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface GrupoEquipoModalData {
  titleEntity: string;
  selectedEntidades: IGrupoEquipo[];
  entidad: IGrupoEquipo;
  fechaInicioMin: DateTime;
  fechaFinMax: DateTime;
  dedicacionMinimaGrupo: number;
  grupo: IGrupo;
  isGrupoEspecialInvestigacion: boolean;
}
@Component({
  templateUrl: './grupo-equipo-modal.component.html',
  styleUrls: ['./grupo-equipo-modal.component.scss']
})
export class GrupoEquipoModalComponent extends DialogFormComponent<GrupoEquipoModalData> implements OnInit {

  @ViewChild(MatAutocompleteTrigger) autocomplete: MatAutocompleteTrigger;

  textSaveOrUpdate: string;

  rolesGrupo$: Observable<IRolProyecto[]>;
  colectivosIdRolParticipacion: string[];

  msgParamFechaFinEntity = {};
  msgParamFechaInicioEntity = {};
  msgParamMiembroEntity = {};
  msgParamRolParticipacionEntity = {};
  msgParamHoraEntity = {};
  msgParamDedicacionEntity = {};
  msgParamParticipacionEntity = {};
  title: string;

  get DEDICACION_MAP() {
    return DEDICACION_MAP;
  }

  constructor(
    matDialogRef: MatDialogRef<GrupoEquipoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: GrupoEquipoModalData,
    private personaService: PersonaService,
    private rolProyectoService: RolProyectoService,
    private readonly translate: TranslateService,
    private readonly grupoEquipoService: GrupoEquipoService,
    private readonly grupoService: GrupoService
  ) {
    super(matDialogRef, !!data?.entidad?.rol);

    this.rolesGrupo$ = this.rolProyectoService.findAll().pipe(
      map(result => result.items)
    );
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.setupI18N();

    this.textSaveOrUpdate = this.data?.entidad?.rol ? MSG_ACEPTAR : MSG_ANADIR;

    this.loadColectivosRolProyecto(this.data.entidad?.rol?.id);

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

    this.subscriptions.push(
      this.formGroup.get('dedicacion').valueChanges
        .subscribe((dedicacion) => {
          this.checkSelectedDedicacion(dedicacion);
        })
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

    this.translate.get(
      GRUPO_EQUIPO_DEDICACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamDedicacionEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      GRUPO_EQUIPO_PARTICIPACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamParticipacionEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });


    if (this.data?.entidad?.rol) {
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
        rolParticipacion: new FormControl(this.data?.entidad?.rol, Validators.required),
        miembro: new FormControl({
          value: this.data?.entidad?.persona,
          disabled: !this.data.entidad.rol
        }, [
          Validators.required
        ]),
        fechaInicio: new FormControl(this.data?.entidad?.fechaInicio, [
          this.data.fechaFinMax ? DateValidator.isBetween(this.data.fechaInicioMin, this.data.fechaFinMax) :
            DateValidator.minDate(this.data.fechaInicioMin),
          Validators.required
        ]),
        fechaFin: new FormControl(this.data?.entidad?.fechaFin, [
          this.data.fechaFinMax ? DateValidator.isBetween(this.data.fechaInicioMin, this.data.fechaFinMax) :
            DateValidator.minDate(this.data.fechaInicioMin)
        ]),
        dedicacion: new FormControl({ value: this.data?.entidad?.dedicacion, disabled: this.data.isGrupoEspecialInvestigacion }, Validators.required),
        participacion: new FormControl({ value: this.data?.entidad?.participacion, disabled: this.data.isGrupoEspecialInvestigacion },
          [
            Validators.required,
            Validators.pattern('^[0-9]*$'),
            Validators.min(this.data.dedicacionMinimaGrupo),
            Validators.max(99)])
      },
      {
        validators: [
          DateValidator.isAfter('fechaInicio', 'fechaFin', false)
        ]
      }
    );

    if (!this.data.isGrupoEspecialInvestigacion) {
      this.checkSelectedDedicacion(this.data?.entidad?.dedicacion, formGroup);

      this.subscriptions.push(
        combineLatest([
          formGroup.get('miembro').valueChanges
            .pipe(
              startWith(this.data?.entidad?.persona),
              filter((persona: IPersona) => !!persona),
              switchMap((persona: IPersona) => this.findGruposEquipoFromOthersGruposByPersonaRef(persona.id))
            ),
          formGroup.get('fechaInicio').valueChanges
            .pipe(
              startWith(this.data?.entidad?.fechaInicio),
              filter(fechaInicio => !!fechaInicio)
            ),
          formGroup.get('fechaFin').valueChanges
            .pipe(
              startWith(this.data?.entidad?.fechaFin)
            ),
          formGroup.get('participacion').valueChanges
            .pipe(
              startWith(this.data?.entidad?.participacion),
              filter(participacion => !!participacion)
            )
        ]).subscribe(([personaGruposEquipoFromOthersGrupos, fechaInicio, fechaFin, participacion]) =>
          this.checkPersonaParticipacionTotalPeriodo(personaGruposEquipoFromOthersGrupos, fechaInicio, fechaFin, participacion)
        )
      );
    }

    return formGroup;
  }

  protected getValue(): GrupoEquipoModalData {
    this.data.entidad.rol = this.formGroup.get('rolParticipacion').value;
    this.data.entidad.persona = this.formGroup.get('miembro').value;
    this.data.entidad.fechaInicio = this.formGroup.get('fechaInicio').value;
    this.data.entidad.fechaFin = this.formGroup.get('fechaFin').value;
    this.data.entidad.dedicacion = this.formGroup.get('dedicacion').value;
    this.data.entidad.participacion = this.formGroup.get('participacion').value;
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

  private checkSelectedDedicacion(dedicacion: Dedicacion, formGroup?: FormGroup): void {
    if (!formGroup) {
      formGroup = this.formGroup;
    }
    if (dedicacion === Dedicacion.COMPLETA) {
      formGroup.controls.participacion.setValue(100);
      formGroup.controls.participacion.disable({ emitEvent: false });
    } else {
      formGroup.controls.participacion.enable({ emitEvent: false });
      formGroup.controls.participacion.markAsTouched({ onlySelf: true });
    }
  }

  private findGruposEquipoFromOthersGruposByPersonaRef(personaRef: string): Observable<IGrupoEquipo[]> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('personaRef', SgiRestFilterOperator.EQUALS, personaRef)
        .and('grupo.activo', SgiRestFilterOperator.EQUALS, 'true')
        .and('grupo.especialInvestigacion.especialInvestigacion', SgiRestFilterOperator.EQUALS, 'false')
        .and('participacion', SgiRestFilterOperator.IS_NOT_NULL, 'true'),
    };
    return this.grupoEquipoService.findAll(options)
      .pipe(
        map(({ items }) => items),
        mergeMap(gruposEquipo =>
          from(gruposEquipo)
            .pipe(
              mergeMap(grupoEquipo =>
                this.grupoService.findById(grupoEquipo.grupo.id)
                  .pipe(
                    map(grupo => {
                      grupoEquipo.grupo = grupo;
                      return grupoEquipo;
                    })
                  )
              ),
              toArray()
            )
        ),
        map(personaGruposEquipo => personaGruposEquipo.filter(grupoEquipo => this.data.entidad.grupo.id !== grupoEquipo.grupo.id)),
      );
  }

  private checkPersonaParticipacionTotalPeriodo(
    gruposEquipoFromOthersGrupos: IGrupoEquipo[], fechaInicio: DateTime, fechaFin: DateTime, participacion: number
  ): void {
    const miembroForm = this.formGroup.get('miembro');
    if (participacion < this.data.dedicacionMinimaGrupo) {
      this.deleteError(miembroForm, 'participacionTotalPeriodo');
      return;
    }

    const grupoEquipoFromForm = {
      fechaInicio,
      fechaFin,
      grupo: this.data.grupo,
      participacion
    } as IGrupoEquipo;

    const gruposEquipoSolapados = gruposEquipoFromOthersGrupos
      .filter(personaGrupoEquipoFromOtherGrupo =>
        this.hasPeriodosFechasSolapamiento(
          grupoEquipoFromForm.fechaInicio, this.getGrupoEquipoFechaFin(grupoEquipoFromForm),
          personaGrupoEquipoFromOtherGrupo.fechaInicio, this.getGrupoEquipoFechaFin(personaGrupoEquipoFromOtherGrupo)
        ));

    gruposEquipoSolapados.push(grupoEquipoFromForm);
    gruposEquipoSolapados.sort((a, b) => {
      const aFechaFin = this.getGrupoEquipoFechaFin(a);
      const bFechaFin = this.getGrupoEquipoFechaFin(b);
      if (!!!aFechaFin) {
        return 1;
      }
      if (!!!bFechaFin) {
        return -1;
      }
      return aFechaFin.toMillis() - bFechaFin.toMillis();
    });

    const fechasFinDistinct = [
      ...new Set(gruposEquipoSolapados.map(grupoEquipoSolapado => this.getGrupoEquipoFechaFin(grupoEquipoSolapado)))
    ];

    const rangesFromFechasFin = this.buildRangesFromFechasFinOrderAsc(fechasFinDistinct, fechaInicio);

    const hasError = rangesFromFechasFin.some(range => {
      const participacionTotalTramo = gruposEquipoSolapados.filter(grupoEquipoSolapado =>
        this.hasRangeSolapamientoWithPeriodoFechas(
          range.inicio as number, range.fin as number,
          grupoEquipoSolapado.fechaInicio, this.getGrupoEquipoFechaFin(grupoEquipoSolapado)
        )
      ).reduce((participacionTotal, grupoEquipoTramo) =>
        participacionTotal + grupoEquipoTramo.participacion, 0);
      return participacionTotalTramo > PARTICIPACION_MAX;
    });

    hasError ? this.addError(miembroForm, 'participacionTotalPeriodo') : this.deleteError(miembroForm, 'participacionTotalPeriodo');
  }

  private buildRangesFromFechasFinOrderAsc(fechasFin: DateTime[], fechaInicio: DateTime): IRange[] {
    let previousRange: IRange;
    return fechasFin.map(fechaFin => {
      const range = {
        inicio: !!previousRange ? previousRange.fin as number + 1 : fechaInicio.toMillis(),
        fin: !!fechaFin ? fechaFin.toMillis() : Number.MAX_VALUE
      };
      previousRange = range;
      return range;
    });
  }

  private hasPeriodosFechasSolapamiento(
    fechaInicioPeriodo1: DateTime, fechaFinPeriodo1: DateTime, fechaInicioPeriodo2: DateTime, fechaFinPeriodo2: DateTime
  ): boolean {
    return fechaInicioPeriodo2.toMillis() - fechaInicioPeriodo1.toMillis() === 0
      ||
      (
        fechaInicioPeriodo2.toMillis() - fechaInicioPeriodo1.toMillis() > 0 &&
        (!!!fechaFinPeriodo1 || fechaInicioPeriodo2.toMillis() - fechaFinPeriodo1.toMillis() <= 0)
      )
      ||
      (fechaInicioPeriodo2.toMillis() - fechaInicioPeriodo1.toMillis() < 0 &&
        (!!!fechaFinPeriodo2 || fechaFinPeriodo2.toMillis() - fechaInicioPeriodo1.toMillis() >= 0));
  }


  private hasRangeSolapamientoWithPeriodoFechas(
    rangeStart: number, rangeEnd: number, fechaInicioPeriodo2: DateTime, fechaFinPeriodo2: DateTime
  ): boolean {
    return fechaInicioPeriodo2.toMillis() - rangeStart === 0
      ||
      (
        fechaInicioPeriodo2.toMillis() - rangeStart > 0 &&
        (!!!rangeEnd || fechaInicioPeriodo2.toMillis() - rangeEnd <= 0)
      )
      ||
      (fechaInicioPeriodo2.toMillis() - rangeStart < 0 &&
        (!!!fechaFinPeriodo2 || fechaFinPeriodo2.toMillis() - rangeStart >= 0));
  }

  private getGrupoEquipoFechaFin(grupoEquipo: IGrupoEquipo): DateTime | null {
    return grupoEquipo.fechaFin ?? grupoEquipo.grupo.fechaFin;
  }
}
