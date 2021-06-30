import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IEvaluacionWithIsEliminable } from '@core/models/eti/evaluacion-with-is-eliminable';
import { IEvaluador } from '@core/models/eti/evaluador';
import { IMemoria } from '@core/models/eti/memoria';
import { IPersona } from '@core/models/sgp/persona';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { EvaluadorService } from '@core/services/eti/evaluador.service';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map, shareReplay, startWith, switchMap } from 'rxjs/operators';
import { DatosAsignacionEvaluacion } from '../../../convocatoria-reunion.action.service';

const MSG_ERROR_LOAD = marker('error.load');
const MSG_ERROR_EVALUADOR_REPETIDO = marker('error.eti.convocatoria-reunion.memoria.evaluador.duplicate');
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
  BaseModalComponent<ConvocatoriaReunionAsignacionMemoriasModalComponentData, ConvocatoriaReunionAsignacionMemoriasModalComponent> implements OnInit, OnDestroy {
  fxLayoutProperties: FxLayoutProperties;

  evaluadores: IEvaluador[];
  memorias: IMemoria[];

  filteredEvaluadoresEvaluador1: Observable<IEvaluador[]>;
  filteredEvaluadoresEvaluador2: Observable<IEvaluador[]>;
  filteredMemorias: Observable<IMemoria[]>;

  isTipoConvocatoriaSeguimiento: boolean;
  filterMemoriasAsignables: SgiRestFilter;

  isEdit = false;

  msgParamEvaludador1Entity = {};
  msgParamEvaludador2Entity = {};
  msgParamMemoriaEntity = {};
  title: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    private logger: NGXLogger,
    public matDialogRef: MatDialogRef<ConvocatoriaReunionAsignacionMemoriasModalComponent>,
    private evaluadorService: EvaluadorService,
    private memoriaService: MemoriaService,
    private personaService: PersonaService,
    protected snackBarService: SnackBarService,
    @Inject(MAT_DIALOG_DATA) public data: ConvocatoriaReunionAsignacionMemoriasModalComponentData,
    private translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, data);

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
    this.buildFilter();
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

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

    this.isEdit = this.data.evaluacion?.memoria ? true : false;

    this.loadEvaluadores();
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
    this.subscriptions.push(this.memoriaService
      .findAllMemoriasAsignablesConvocatoria(this.data.idConvocatoria)
      .subscribe(
        (response: SgiRestListResult<IMemoria>) => {
          this.memorias = response.items;

          // Eliminar de la lista las memorias que ya están asignadas
          this.memorias = this.memorias.filter(
            (memoria: IMemoria) => {
              return (!this.data.memoriasAsignadas.some(e => e.id === memoria.id));
            }
          );

          this.filteredMemorias = this.formGroup.controls.memoria.valueChanges
            .pipe(
              startWith(''),
              map(value => this._filterMemoria(value))
            );
        },
        (error) => {
          this.logger.error(error);
          this.snackBarService.showError(MSG_ERROR_LOAD);
        }
      ));
  }

  /**
   * Recupera un listado de las memorias asignables si la convocatoria es de tipo seguimiento.
   */
  private loadMemoriasAsignablesConvocatoriaSeguimiento(): void {
    this.subscriptions.push(this.memoriaService
      .findAllAsignablesTipoConvocatoriaSeguimiento({ filter: this.filterMemoriasAsignables })
      .subscribe(
        (response: SgiRestListResult<IMemoria>) => {
          this.memorias = response.items;

          // Eliminar de la lista las memorias que ya están asignadas
          this.memorias = this.memorias.filter(
            (memoria: IMemoria) => {
              return (!this.data.memoriasAsignadas.some(e => e.id === memoria.id));
            }
          );

          this.filteredMemorias = this.formGroup.controls.memoria.valueChanges
            .pipe(
              startWith(''),
              map(value => this._filterMemoria(value))
            );
        },
        (error) => {
          this.logger.error(error);
          this.snackBarService.showError(MSG_ERROR_LOAD);
        }
      ));
  }

  /**
   * Recupera un listado de las memorias asignables si la convocatoria es de tipo ordinaria / extraordinaria.
   */
  private loadMemoriasAsignablesConvocatoriaOrdExt(): void {
    this.subscriptions.push(this.memoriaService
      .findAllAsignablesTipoConvocatoriaOrdExt({ filter: this.filterMemoriasAsignables })
      .subscribe(
        (response: SgiRestListResult<IMemoria>) => {
          this.memorias = response.items;

          // Eliminar de la lista las memorias que ya están asignadas
          this.memorias = this.memorias.filter(
            (memoria: IMemoria) => {
              return (!this.data.memoriasAsignadas.some(e => e.id === memoria.id));
            }
          );

          this.filteredMemorias = this.formGroup.controls.memoria.valueChanges
            .pipe(
              startWith(''),
              map(value => this._filterMemoria(value))
            );
        },
        (error) => {
          this.logger.error(error);
          this.snackBarService.showError(MSG_ERROR_LOAD);
        }
      ));
  }

  /**
   * Recupera un listado de los evaluadores que hay en el sistema.
   */
  private loadEvaluadores(): void {
    let evaluadoresMemoriaSeleccionada$ = null;
    if (this.isEdit) {
      evaluadoresMemoriaSeleccionada$ = this.getEvaluadoresMemoria(this.data.evaluacion?.memoria);
    } else {
      evaluadoresMemoriaSeleccionada$ =
        this.formGroup.controls.memoria.valueChanges.pipe(
          switchMap((memoria: IMemoria | string) => {
            if (typeof memoria === 'string' || !memoria.id) {
              return of([]);
            }
            return this.getEvaluadoresMemoria(memoria);
          }),
          shareReplay(1));
    }

    this.subscriptions.push(evaluadoresMemoriaSeleccionada$.subscribe(
      (evaluadores: IEvaluador[]) => {
        this.evaluadores = evaluadores;

        this.filteredEvaluadoresEvaluador1 = this.formGroup.controls.evaluador1.valueChanges
          .pipe(
            startWith(''),
            map(value => this._filterEvaluador(value))
          );
      },
      (error) => {
        this.logger.error(error);
        this.snackBarService.showError(MSG_ERROR_LOAD);
      }
    ));

    this.subscriptions.push(evaluadoresMemoriaSeleccionada$.subscribe(
      (evaluadores: IEvaluador[]) => {
        this.evaluadores = evaluadores;

        this.filteredEvaluadoresEvaluador2 = this.formGroup.controls.evaluador2.valueChanges
          .pipe(
            startWith(''),
            map(value => this._filterEvaluador(value))
          );
      },
      (error) => {
        this.logger.error(error);
        this.snackBarService.showError(MSG_ERROR_LOAD);
      }
    ));
  }

  private getEvaluadoresMemoria(memoria: IMemoria): Observable<IEvaluador[]> {
    return this.evaluadorService.findAllMemoriasAsignablesConvocatoria(memoria.comite.id, memoria.id)
      .pipe(
        switchMap((response) => {

          if (response.items) {
            const evaluadores = response.items;

            const personaIdsEvaluadores = new Set<string>(evaluadores.map((convocante: IEvaluador) => convocante.persona.id));

            if (personaIdsEvaluadores.size === 0) {
              return of([]);
            }

            const evaluadoresWithDatosPersona$ = this.personaService.findAllByIdIn([...personaIdsEvaluadores]).pipe(
              map((result: SgiRestListResult<IPersona>) => {
                const personas = result.items;

                evaluadores.forEach((evaluador: IEvaluador) => {
                  const datosPersonaEvaluador = personas.find(
                    (persona: IPersona) => evaluador.persona.id === persona.id
                  );
                  evaluador.persona = datosPersonaEvaluador;
                });

                return evaluadores;
              }));

            return evaluadoresWithDatosPersona$;
          } else {
            return of([]);
          }
        })
      );
  }

  /**
   * Filtro de campo autocompletable memoria.
   * @param value value a filtrar (string o memoria).
   * @returns lista de memorias filtrada.
   */
  private _filterMemoria(value: string | IMemoria): IMemoria[] {
    if (!value) {
      return this.memorias;
    }

    let filterValue: string;
    if (typeof value === 'string') {
      filterValue = value.toLowerCase();
    } else {
      filterValue = (value.numReferencia + ' - ' + value.titulo).toLowerCase();
    }

    return this.memorias.filter
      (memoria => (memoria.numReferencia + ' - ' + memoria.titulo).toLowerCase().includes(filterValue));
  }

  /**
   * Filtro de campo autocompletable evaluador.
   * @param value value a filtrar (string o evaluador).
   * @returns lista de evaluadores filtrada.
   */
  private _filterEvaluador(value: string | IEvaluador): IEvaluador[] {
    if (!value) {
      return this.evaluadores;
    }

    let filterValue: string;
    if (typeof value === 'string') {
      filterValue = value.toLowerCase();
    } else {
      filterValue = (value.persona.nombre + ' ' + value.persona.apellidos).toLowerCase();
    }

    return this.evaluadores.filter
      (evaluador =>
        (evaluador.persona.nombre + ' ' + evaluador.persona.apellidos)
          .toLowerCase().includes(filterValue));
  }

  /**
   * Devuelve el nombre completo del evaluador
   * @param evaluador Evaluador
   *
   * @returns nombre completo del evaluador
   */
  getEvaluador(evaluador: IEvaluador): string {
    return evaluador ? evaluador.persona.nombre + ' ' + evaluador.persona.apellidos : '';
  }

  /**
   * Devuelve la referencia y el titulo de la memoria
   * @param memoria Memoria
   *
   * @returns referencia y titulo memoria
   */
  getMemoria(memoria: IMemoria): string {
    return memoria ? (memoria.numReferencia + (memoria.titulo ? ' - ' + memoria.titulo : '')) : '';
  }

  /**
   * Confirmar asignación
   */
  saveOrUpdate(): void {
    const modalData: ConvocatoriaReunionAsignacionMemoriasModalComponentData = this.getDatosForm();
    if (modalData.evaluacion.evaluador1?.persona?.id === modalData.evaluacion.evaluador2?.persona?.id) {
      this.snackBarService.showError(MSG_ERROR_EVALUADOR_REPETIDO);
      return;
    }
    super.saveOrUpdate();
  }

  protected getDatosForm(): ConvocatoriaReunionAsignacionMemoriasModalComponentData {
    this.data.evaluacion.memoria = this.formGroup.controls.memoria.value;
    this.data.evaluacion.evaluador1 = this.formGroup.controls.evaluador1.value;
    this.data.evaluacion.evaluador2 = this.formGroup.controls.evaluador2.value;
    return this.data;
  }

  protected getFormGroup(): FormGroup {
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

  ngOnDestroy(): void {
    super.ngOnDestroy();
  }
}
