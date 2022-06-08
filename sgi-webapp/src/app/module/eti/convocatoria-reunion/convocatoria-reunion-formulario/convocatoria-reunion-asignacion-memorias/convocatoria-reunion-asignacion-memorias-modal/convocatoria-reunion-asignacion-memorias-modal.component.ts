import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IEvaluacionWithIsEliminable } from '@core/models/eti/evaluacion-with-is-eliminable';
import { IEvaluador } from '@core/models/eti/evaluador';
import { IMemoria } from '@core/models/eti/memoria';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator } from '@sgi/framework/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
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

@Component({
  selector: 'sgi-convocatoria-reunion-asignacion-memorias-modal',
  templateUrl: './convocatoria-reunion-asignacion-memorias-modal.component.html',
  styleUrls: ['./convocatoria-reunion-asignacion-memorias-modal.component.scss']
})
export class ConvocatoriaReunionAsignacionMemoriasModalComponent extends
  DialogFormComponent<ConvocatoriaReunionAsignacionMemoriasModalComponentData> implements OnInit {

  memorias$: Observable<IMemoria[]>;

  evaluador1$: BehaviorSubject<IEvaluador>;
  evaluador2$: BehaviorSubject<IEvaluador>;

  isTipoConvocatoriaSeguimiento: boolean;
  filterMemoriasAsignables: SgiRestFilter;

  msgParamEvaludador1Entity = {};
  msgParamEvaludador2Entity = {};
  msgParamMemoriaEntity = {};
  title: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  readonly displayerMemoria = (memoria: IMemoria): string => (memoria.numReferencia ?? '') + (memoria.titulo ? ' - ' + memoria.titulo : '');

  constructor(
    matDialogRef: MatDialogRef<ConvocatoriaReunionAsignacionMemoriasModalComponent>,
    private memoriaService: MemoriaService,
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
        map(result => this.filterMemoriasAsignadas(result.items))
      );
  }

  /**
   * Recupera un listado de las memorias asignables si la convocatoria es de tipo seguimiento.
   */
  private loadMemoriasAsignablesConvocatoriaSeguimiento(): void {
    this.memorias$ = this.memoriaService.findAllAsignablesTipoConvocatoriaSeguimiento({ filter: this.filterMemoriasAsignables })
      .pipe(
        map(result => this.filterMemoriasAsignadas(result.items))
      );
  }

  /**
   * Recupera un listado de las memorias asignables si la convocatoria es de tipo ordinaria / extraordinaria.
   */
  private loadMemoriasAsignablesConvocatoriaOrdExt(): void {
    this.memorias$ = this.memoriaService.findAllAsignablesTipoConvocatoriaOrdExt({ filter: this.filterMemoriasAsignables })
      .pipe(
        map(result => this.filterMemoriasAsignadas(result.items))
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

  private filterMemoriasAsignadas(memorias: IMemoria[]): IMemoria[] {
    return memorias.filter(memoria => {
      return (!this.data.memoriasAsignadas.some(memoriaAsignada => memoriaAsignada.id === memoria.id));
    });
  }

}
