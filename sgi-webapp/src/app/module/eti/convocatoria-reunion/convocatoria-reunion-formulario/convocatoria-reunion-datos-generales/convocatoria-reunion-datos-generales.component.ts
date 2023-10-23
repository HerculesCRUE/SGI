import { Component, OnDestroy, OnInit } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IAsistente } from '@core/models/eti/asistente';
import { IComite } from '@core/models/eti/comite';
import { IConvocatoriaReunion } from '@core/models/eti/convocatoria-reunion';
import { IEvaluador } from '@core/models/eti/evaluador';
import { IPersona } from '@core/models/sgp/persona';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ConvocatoriaReunionService } from '@core/services/eti/convocatoria-reunion.service';
import { EvaluadorService } from '@core/services/eti/evaluador.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { SgiCkEditorConfig } from '@shared/sgi-ckeditor-config';
import Editor from 'ckeditor5-custom-build/build/ckeditor';
import { NGXLogger } from 'ngx-logger';
import { Observable, Subscription, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { ConvocatoriaReunionActionService } from '../../convocatoria-reunion.action.service';
import { ConvocatoriaReunionDatosGeneralesFragment } from './convocatoria-reunion-datos-generales.fragment';

const MSG_ERROR_LOAD = marker('error.load');
const CONVOCATORIA_COMITE_KEY = marker('label.eti.comite');
const CONVOCATORIA_FECHA_EVALUACION_KEY = marker('eti.convocatoria-reunion.fecha-evaluacion');
const CONVOCATORIA_FECHA_LIMITE_KEY = marker('error.eti.convocatoria-reunion.fecha-limite');
const CONVOCATORIA_TIPO_KEY = marker('eti.convocatoria-reunion.tipo');
const CONVOCATORIA_HORA_INICIO_KEY = marker('eti.convocatoria-reunion.hora-inicio');
const CONVOCATORIA_HORA_INICIO_SEGUNDA_KEY = marker('eti.convocatoria-reunion.hora-inicio-segunda');
const CONVOCATORIA_LUGAR_KEY = marker('eti.convocatoria-reunion.lugar');
const CONVOCATORIA_ORDEN_DIA_KEY = marker('eti.convocatoria-reunion.orden-dia');
const CONVOCATORIA_CONVOCANTES_KEY = marker('eti.convocatoria-reunion.convocantes');

@Component({
  selector: 'sgi-convocatoria-reunion-datos-generales',
  templateUrl: './convocatoria-reunion-datos-generales.component.html',
  styleUrls: ['./convocatoria-reunion-datos-generales.component.scss']
})
export class ConvocatoriaReunionDatosGeneralesComponent extends FormFragmentComponent<IConvocatoriaReunion> implements OnInit, OnDestroy {
  public readonly CkEditor = Editor;
  public readonly configCkEditor = SgiCkEditorConfig.defaultConfig

  fxFlexProperties: FxFlexProperties;
  fxFlexPropertiesInline: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  formFragment: ConvocatoriaReunionDatosGeneralesFragment;
  disableComite: boolean;
  private subscriptions: Subscription[] = [];

  msgParamComiteEntity = {};
  msgParamFechaEvaluacionEntity = {};
  msgParamFechaLimiteEntity = {};
  msgParamTipoEntity = {};
  msgParamHoraInicioEntity = {};
  msgParamHoraInicioSegundaEntity = {};
  msgParamLugarEntity = {};
  msgParamOrdenDiaEntity = {};
  msgParamConvocantesEntity = {};

  constructor(
    private logger: NGXLogger,
    private evaluadorService: EvaluadorService,
    private snackBarService: SnackBarService,
    private personaService: PersonaService,
    private actionService: ConvocatoriaReunionActionService,
    private convocatoriaReunionService: ConvocatoriaReunionService,
    private translate: TranslateService
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.formFragment = this.fragment as ConvocatoriaReunionDatosGeneralesFragment;

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(32.7%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxFlexPropertiesInline = new FxFlexProperties();
    this.fxFlexPropertiesInline.sm = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.md = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.order = '3';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit() {
    super.ngOnInit();
    this.setupI18N();

    if (this.actionService.hasMemoriasAssigned()) {
      this.formGroup.controls.comite.disable({ onlySelf: true });
    }

    // Inicializa los combos
    if (!this.formFragment.isEdit()) {
      this.getConvocantesComite();
    }
  }

  private setupI18N(): void {
    this.translate.get(
      CONVOCATORIA_COMITE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamComiteEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      CONVOCATORIA_FECHA_EVALUACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaEvaluacionEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      CONVOCATORIA_FECHA_LIMITE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaLimiteEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      CONVOCATORIA_TIPO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTipoEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      CONVOCATORIA_HORA_INICIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamHoraInicioEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      CONVOCATORIA_HORA_INICIO_SEGUNDA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamHoraInicioSegundaEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      CONVOCATORIA_ORDEN_DIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamOrdenDiaEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      CONVOCATORIA_LUGAR_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamLugarEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      CONVOCATORIA_CONVOCANTES_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamConvocantesEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });
  }

  ngOnDestroy() {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  /**
   * Recupera el listado de convocantes correspondiente al comite seleccionado.
   */
  getConvocantesComite(): void {
    const convocantesSelectSubscription = this.formGroup.controls.comite.valueChanges.pipe(
      switchMap((comite: IComite | string) => {
        if (typeof comite === 'string' || !comite.id) {
          return of([]);
        }
        return this.getConvocantes(comite);
      })
    ).subscribe(
      (convocantes) => {
        this.formFragment.evaluadoresComite = convocantes;
      },
      (error) => {
        this.logger.error(error);
        this.snackBarService.showError(MSG_ERROR_LOAD);
      }
    );
    this.subscriptions.push(convocantesSelectSubscription);
  }

  private getConvocantes(comite: IComite): Observable<IEvaluador[]> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('comite.id', SgiRestFilterOperator.EQUALS, comite.id.toString())
    };
    return this.evaluadorService.findAll(options)
      .pipe(
        switchMap((listadoConvocantes) => {
          const personaIdsConvocantes = new Set<string>(listadoConvocantes.items.map((convocante: IEvaluador) => convocante.persona.id));
          const convocantesWithDatosPersona$ = this.personaService.findAllByIdIn([...personaIdsConvocantes])
            .pipe(
              map((personas: SgiRestListResult<IPersona>) => {
                return this.loadDatosPersona(personas, listadoConvocantes.items);
              })
            );
          return convocantesWithDatosPersona$;
        }),
        switchMap((convocantes) => {
          this.getAsistentes(convocantes);
          return of(convocantes);
        })
      );
  }

  /**
   * Carga los datos personales de los evaluadores
   *
   * @param listado Listado de personas
   * @param evaluadores Evaluadores
   */
  private loadDatosPersona(listado: SgiRestListResult<IPersona>, evaluadores: IEvaluador[]): IEvaluador[] {
    const personas = listado.items;
    evaluadores.forEach((convocante) => {
      const datosPersonaConvocante = personas.find((persona: IPersona) => convocante.persona.id === persona.id);
      convocante.persona = datosPersonaConvocante;
    });
    return evaluadores;
  }

  /**
   * Carga los asistentes de la convocatoria dentro del formGroup
   *
   * @param convocantes Convocantes
   */
  private getAsistentes(convocantes: IEvaluador[]): Observable<SgiRestListResult<IAsistente>> {
    const id = this.actionService?.convocatoriaReunion?.id;
    if (id) {
      return this.convocatoriaReunionService.findAsistentes(id).pipe(
        switchMap((asistentes: SgiRestListResult<IAsistente>) => {
          const ids = asistentes.items.map((convocante: IAsistente) => convocante.evaluador.id);
          const asistentesFormGroup = [];
          convocantes.forEach((evaluador: IEvaluador) => {
            if (ids.includes(evaluador.id)) {
              asistentesFormGroup.push(evaluador);
            }
          });
          this.formGroup.get('convocantes').setValue(asistentesFormGroup);
          return of(asistentes);
        })
      );
    } else {
      this.formGroup.get('convocantes').setValue(convocantes);
    }
    return of();
  }

  /**
   * Recupera los convocantes seleccionados en el formulario
   *
   * @return lista de convocantes
   */
  getDatosConvocantesFormulario(): IEvaluador[] {
    const convocantes: IEvaluador[] = this.formGroup.controls.convocantes.value;
    return convocantes;
  }
}
