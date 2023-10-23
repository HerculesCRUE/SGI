import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { SelectValue } from '@core/component/select-common/select-common.component';
import { MSG_PARAMS } from '@core/i18n';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { IEvaluacionWithIsEliminable } from '@core/models/eti/evaluacion-with-is-eliminable';
import { IEvaluador } from '@core/models/eti/evaluador';
import { IMemoria } from '@core/models/eti/memoria';
import { EvaluadorService } from '@core/services/eti/evaluador.service';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator } from '@sgi/framework/http';
import { BehaviorSubject, Observable, forkJoin, from, of } from 'rxjs';
import { map, mergeMap, switchMap, toArray } from 'rxjs/operators';
import { DatosAsignacionEvaluacion } from '../../../convocatoria-reunion.action.service';

const MEMORIA_EVALUADOR1_KEY = marker('eti.convocatoria-reunion.memoria.evaludador-1');
const MEMORIA_EVALUADOR2_KEY = marker('eti.convocatoria-reunion.memoria.evaludador-2');
const MEMORIA_KEY = marker('eti.memoria');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface ConvocatoriaReunionAsignacionMemoriasModalComponentData {
  idConvocatoria: number;
  memoriasAsignadas: IMemoria[];
  filterMemoriasAsignables: DatosAsignacionEvaluacion;
  evaluacion: IEvaluacionWithIsEliminable;
  readonly: boolean;
}

interface IMemoriaWithLastEvaluacionModal extends IMemoria {
  evaluacion: IEvaluacion;
}

interface IMsgParamAsignacionEvaluador {
  conflictoIntereses: boolean;
  activo: boolean;
  evaluador: string;
  comite?: string;
  generoComite: string;
}

@Component({
  selector: 'sgi-convocatoria-reunion-asignacion-memorias-modal',
  templateUrl: './convocatoria-reunion-asignacion-memorias-modal.component.html',
  styleUrls: ['./convocatoria-reunion-asignacion-memorias-modal.component.scss']
})
export class ConvocatoriaReunionAsignacionMemoriasModalComponent extends
  DialogFormComponent<ConvocatoriaReunionAsignacionMemoriasModalComponentData> implements OnInit {

  memorias$: Observable<IMemoriaWithLastEvaluacionModal[]>;

  evaluador1$: BehaviorSubject<IEvaluador>;
  evaluador2$: BehaviorSubject<IEvaluador>;

  isTipoConvocatoriaSeguimiento: boolean;
  filterMemoriasAsignables: SgiRestFilter;

  msgParamEvaludador1Entity = {};
  msgParamEvaludador2Entity = {};
  msgParamMemoriaEntity = {};
  msgParamEvaludador1Asignacion = {} as IMsgParamAsignacionEvaluador;
  msgParamEvaludador2Asignacion = {} as IMsgParamAsignacionEvaluador;
  title: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  readonly displayerMemoria = (memoria: IMemoria): string => (memoria.numReferencia ?? '') + (memoria.titulo ? ' - ' + memoria.titulo : '');

  constructor(
    matDialogRef: MatDialogRef<ConvocatoriaReunionAsignacionMemoriasModalComponent>,
    private memoriaService: MemoriaService,
    private personaService: PersonaService,
    private evaluadorService: EvaluadorService,
    @Inject(MAT_DIALOG_DATA) public data: ConvocatoriaReunionAsignacionMemoriasModalComponentData,
    private translate: TranslateService
  ) {
    super(matDialogRef, !!data.evaluacion?.memoria);

    this.buildFilter();
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.evaluador1$ = new BehaviorSubject<IEvaluador>(this.data.evaluacion.evaluador1);
    this.formGroup.controls.evaluador1.valueChanges.subscribe(value => {
      this.evaluador1$.next(value);
    });
    this.evaluador2$ = new BehaviorSubject<IEvaluador>(this.data.evaluacion.evaluador2);
    this.formGroup.controls.evaluador2.valueChanges.subscribe(value => {
      this.evaluador2$.next(value);
    });

    if (this.data.idConvocatoria) {
      this.loadMemoriasAsignablesConvocatoria();
    } else if (this.data.filterMemoriasAsignables && this.data.filterMemoriasAsignables.idComite &&
      this.data.filterMemoriasAsignables.idTipoConvocatoria &&
      this.data.filterMemoriasAsignables.fechaLimite) {
      if (this.isTipoConvocatoriaSeguimiento) {
        this.loadMemoriasAsignablesConvocatoriaSeguimiento();
      } else {
        this.loadMemoriasAsignablesConvocatoriaOrdExt();
      }
    }
  }

  private setupI18N(): void {
    this.translate.get(
      MEMORIA_EVALUADOR1_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEvaludador1Entity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      MEMORIA_EVALUADOR2_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEvaludador2Entity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      MEMORIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamMemoriaEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.data.evaluacion.memoria) {
      this.translate.get(
        MEMORIA_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        MEMORIA_KEY,
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
  }

  /**
   * Construye los filtros necesarios para la búsqueda de las memorias asignables.
   *
   */
  private buildFilter(): void {
    if (this.data.filterMemoriasAsignables && this.data.filterMemoriasAsignables.idComite &&
      this.data.filterMemoriasAsignables.idTipoConvocatoria &&
      this.data.filterMemoriasAsignables.fechaLimite) {
      this.isTipoConvocatoriaSeguimiento = (this.data.filterMemoriasAsignables.idTipoConvocatoria === 3) ? true : false;

      this.filterMemoriasAsignables = new RSQLSgiRestFilter('comite.id', SgiRestFilterOperator.EQUALS, this.data.filterMemoriasAsignables.idComite.toString())
        .and('fechaEnvioSecretaria', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(this.data.filterMemoriasAsignables.fechaLimite));
    }
    else {
      this.filterMemoriasAsignables = undefined;
    }
  }

  /**
   * Recupera un listado de los memorias asignables a la convocatoria.
   */
  private loadMemoriasAsignablesConvocatoria(): void {
    this.memorias$ = this.memoriaService.findAllMemoriasAsignablesConvocatoria(this.data.idConvocatoria)
      .pipe(
        map(result => this.filterMemoriasAsignadas(result.items)),
        map(memorias => memorias.map(memoria => memoria as IMemoriaWithLastEvaluacionModal)),
        switchMap(memorias => this.fillEvaluacionIfLastEvaluacionMemoriaPendienteCorrecciones(memorias))
      );
  }

  /**
   * Recupera un listado de las memorias asignables si la convocatoria es de tipo seguimiento.
   */
  private loadMemoriasAsignablesConvocatoriaSeguimiento(): void {
    this.memorias$ = this.memoriaService.findAllAsignablesTipoConvocatoriaSeguimiento({ filter: this.filterMemoriasAsignables })
      .pipe(
        map(result => this.filterMemoriasAsignadas(result.items)),
        map(memorias => memorias.map(memoria => memoria as IMemoriaWithLastEvaluacionModal)),
        switchMap(memorias => this.fillEvaluacionIfLastEvaluacionMemoriaPendienteCorrecciones(memorias))
      );
  }

  /**
   * Recupera un listado de las memorias asignables si la convocatoria es de tipo ordinaria / extraordinaria.
   */
  private loadMemoriasAsignablesConvocatoriaOrdExt(): void {
    this.memorias$ = this.memoriaService.findAllAsignablesTipoConvocatoriaOrdExt({ filter: this.filterMemoriasAsignables })
      .pipe(
        map(result => this.filterMemoriasAsignadas(result.items)),
        map(memorias => memorias.map(memoria => memoria as IMemoriaWithLastEvaluacionModal)),
        switchMap(memorias => this.fillEvaluacionIfLastEvaluacionMemoriaPendienteCorrecciones(memorias))
      );
  }

  protected getValue(): ConvocatoriaReunionAsignacionMemoriasModalComponentData {
    this.data.evaluacion.memoria = this.formGroup.controls.memoria.value;
    this.data.evaluacion.evaluador1 = this.formGroup.controls.evaluador1.value;
    this.data.evaluacion.evaluador2 = this.formGroup.controls.evaluador2.value;
    return this.data;
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      memoria: new FormControl(this.data.evaluacion.memoria, [Validators.required]),
      evaluador1: new FormControl(this.data.evaluacion.evaluador1),
      evaluador2: new FormControl(this.data.evaluacion.evaluador2),
    });
    if (this.data.readonly) {
      formGroup.disable();
    }

    return formGroup;
  }

  public selectEvaluador1IfLastEvaluacionMemoriaPendienteCorrecciones(options: SelectValue<IEvaluador>[]): void {
    this.msgParamEvaludador1Asignacion = {} as IMsgParamAsignacionEvaluador;

    const evaluador1Memoria = (this.formGroup.controls.memoria.value as IMemoriaWithLastEvaluacionModal)?.evaluacion?.evaluador1;
    if (options?.length && !!evaluador1Memoria) {
      const evaluadorFind = options.map(option => option.item).find(evaluador => evaluador?.persona?.id === evaluador1Memoria?.persona?.id);
      if (evaluadorFind) {
        this.formGroup.controls.evaluador1.setValue(evaluadorFind);
      } else {
        this.subscriptions.push(
          forkJoin({
            isActivo: this.evaluadorService.isActivo(evaluador1Memoria.id, evaluador1Memoria.comite.id),
            persona: this.personaService.findById(evaluador1Memoria.persona.id)
          }).subscribe(({ isActivo, persona }) => {
            this.msgParamEvaludador1Asignacion.activo = isActivo;
            this.msgParamEvaludador1Asignacion.conflictoIntereses = !isActivo;
            this.msgParamEvaludador1Asignacion.evaluador = `${persona.nombre} ${persona.apellidos}`;

            if (!isActivo) {
              const comite = (this.formGroup.controls.memoria.value as IMemoriaWithLastEvaluacionModal)?.evaluacion.convocatoriaReunion.comite;
              this.msgParamEvaludador1Asignacion.comite = comite.nombreInvestigacion;
              this.msgParamEvaludador1Asignacion.generoComite = comite.genero === 'F' ? MSG_PARAMS.GENDER.FEMALE.gender : MSG_PARAMS.GENDER.MALE.gender;
            }
          })
        )
      }
    }
  }

  public selectEvaluador2IfLastEvaluacionMemoriaPendienteCorrecciones(options: SelectValue<IEvaluador>[]): void {
    this.msgParamEvaludador2Asignacion = {} as IMsgParamAsignacionEvaluador;

    const evaluador2Memoria = (this.formGroup.controls.memoria.value as IMemoriaWithLastEvaluacionModal)?.evaluacion?.evaluador2;
    if (options?.length && !!evaluador2Memoria) {
      const evaluadorFind = options.map(option => option.item).find(evaluador => evaluador?.persona?.id === evaluador2Memoria?.persona?.id);
      if (evaluadorFind) {
        this.formGroup.controls.evaluador2.setValue(evaluadorFind);
      } else {
        this.subscriptions.push(
          forkJoin({
            isActivo: this.evaluadorService.isActivo(evaluador2Memoria.id, evaluador2Memoria.comite.id),
            persona: this.personaService.findById(evaluador2Memoria.persona.id)
          }).subscribe(({ isActivo, persona }) => {
            this.msgParamEvaludador2Asignacion.activo = isActivo;
            this.msgParamEvaludador2Asignacion.conflictoIntereses = !isActivo;
            this.msgParamEvaludador2Asignacion.evaluador = `${persona.nombre} ${persona.apellidos}`;

            if (!isActivo) {
              const comite = (this.formGroup.controls.memoria.value as IMemoriaWithLastEvaluacionModal)?.evaluacion.convocatoriaReunion.comite;
              this.msgParamEvaludador2Asignacion.comite = comite.nombreInvestigacion;
              this.msgParamEvaludador2Asignacion.generoComite = comite.genero === 'F' ? MSG_PARAMS.GENDER.FEMALE.gender : MSG_PARAMS.GENDER.MALE.gender;
            }
          })
        )
      }
    }
  }

  private filterMemoriasAsignadas(memorias: IMemoria[]): IMemoria[] {
    return memorias.filter(memoria => {
      return (!this.data.memoriasAsignadas.some(memoriaAsignada => memoriaAsignada.id === memoria.id));
    });
  }

  private fillEvaluacionIfLastEvaluacionMemoriaPendienteCorrecciones(memorias: IMemoriaWithLastEvaluacionModal[]): Observable<IMemoriaWithLastEvaluacionModal[]> {
    return from(memorias).pipe(
      mergeMap(memoria => this.memoriaService.isLastEvaluacionMemoriaPendienteCorrecciones(memoria.id).pipe(
        switchMap(isPendienteCorrecciones => {
          if (isPendienteCorrecciones) {
            return this.memoriaService.getLastEvaluacionMemoria(memoria.id).pipe(
              map(evaluacion => {
                memoria.evaluacion = evaluacion;
                return memoria;
              })
            )
          }
          return of(memoria);
        })
      )),
      toArray(),
      map(() => {
        return memorias;
      })
    );
  }

}
