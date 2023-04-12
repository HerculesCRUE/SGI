import { AfterViewInit, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { DICTAMEN, IDictamen } from '@core/models/eti/dictamen';
import { IMemoria } from '@core/models/eti/memoria';
import { TIPO_EVALUACION } from '@core/models/eti/tipo-evaluacion';
import { TIPO_MEMORIA } from '@core/models/eti/tipo-memoria';
import { IDocumento } from '@core/models/sgdoc/documento';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { TipoEvaluacionService } from '@core/services/eti/tipo-evaluacion.service';
import { DocumentoService, triggerDownloadToUser } from '@core/services/sgdoc/documento.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable, of, Subscription } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { EvaluacionFormularioActionService } from '../evaluacion-formulario.action.service';
import {
  EvaluacionListadoAnteriorMemoriaComponent
} from '../evaluacion-listado-anterior-memoria/evaluacion-listado-anterior-memoria.component';
import { EvaluacionEvaluacionFragment } from './evaluacion-evaluacion.fragment';

const EVALUACION_DICTAMEN_KEY = marker('eti.dictamen');
const EVALUACION_COMENTARIO_KEY = marker('eti.evaluacion-evaluador.comentario');

@Component({
  selector: 'sgi-evaluacion-evaluacion',
  templateUrl: './evaluacion-evaluacion.component.html',
  styleUrls: ['./evaluacion-evaluacion.component.scss']
})
export class EvaluacionEvaluacionComponent extends FormFragmentComponent<IMemoria> implements AfterViewInit, OnInit, OnDestroy {

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexPropertiesInline: FxFlexProperties;

  @ViewChild('evaluaciones') evaluaciones: EvaluacionListadoAnteriorMemoriaComponent;

  suscriptions: Subscription[] = [];
  dictamenes$: Observable<IDictamen[]>;

  formPart: EvaluacionEvaluacionFragment;

  msgParamDictamenEntity = {};
  msgParamComentarioEntity = {};

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    public actionService: EvaluacionFormularioActionService,
    protected tipoEvaluacionService: TipoEvaluacionService,
    private readonly translate: TranslateService,
    private readonly evaluacionService: EvaluacionService,
    private readonly documentoService: DocumentoService
  ) {
    super(actionService.FRAGMENT.EVALUACIONES, actionService);
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(32%-10px)';
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

    this.formPart = this.fragment as EvaluacionEvaluacionFragment;

    this.dictamenes$ = this.formPart.evaluacion$.pipe(
      switchMap(evaluacion => {
        if (evaluacion) {
          return tipoEvaluacionService.findAllDictamenByTipoEvaluacionAndRevisionMinima(
            evaluacion.tipoEvaluacion.id,
            evaluacion.esRevMinima
          ).pipe(
            map(response => response.items)
          );
        }
        return of([]);
      })
    );
  }

  ngOnInit() {
    super.ngOnInit();
    this.setupI18N();
    this.suscriptions.push(this.formGroup.controls.dictamen.valueChanges.subscribe((dictamen) => {
      this.actionService.setDictamen(dictamen);
    }));
  }

  private setupI18N(): void {
    this.translate.get(
      EVALUACION_DICTAMEN_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamDictamenEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      EVALUACION_COMENTARIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamComentarioEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
  }

  ngAfterViewInit(): void {
    this.evaluaciones.memoriaId = this.actionService.getEvaluacion()?.memoria?.id;
    this.evaluaciones.evaluacionId = this.actionService.getEvaluacion()?.id;
    this.evaluaciones.ngAfterViewInit();
  }

  /**
   * Visualiza el informe de evaluación seleccionado.
   * @param idEvaluacion id de la evaluacion del informe
   */
  visualizarInforme(idEvaluacion: number): void {
    const documento: IDocumento = {} as IDocumento;
    this.evaluacionService.getDocumentoEvaluacion(idEvaluacion).pipe(
      switchMap((documentoInfo: IDocumento) => {
        documento.nombre = documentoInfo.nombre;
        return this.documentoService.downloadFichero(documentoInfo.documentoRef);
      })
    ).subscribe(response => {
      triggerDownloadToUser(response, documento.nombre);
    });
  }

  enableBtnVisualizarInforme(): boolean {
    const hasDictamenAndNotEdited = !!this.formPart.evaluacion?.dictamen?.id
      && (this.formGroup.controls.dictamen.value?.id === this.formPart.evaluacion.dictamen.id);

    if (!hasDictamenAndNotEdited) {
      return false;
    }

    if (this.formPart.evaluacion?.tipoEvaluacion?.id === TIPO_EVALUACION.MEMORIA) {
      const tipoMemoriaWithInforme = [
        TIPO_MEMORIA.NUEVA,
        TIPO_MEMORIA.RATIFICACION,
        TIPO_MEMORIA.MODIFICACION,
      ].includes(this.formPart.evaluacion?.memoria?.tipoMemoria?.id);
      const tipoDictamenConInforme = [
        DICTAMEN.FAVORABLE,
        DICTAMEN.PDTE_CORRECCIONES,
        DICTAMEN.FAVORABLE_PDTE_REV_MINIMA
      ].includes(this.formGroup.controls.dictamen.value?.id);
      return hasDictamenAndNotEdited && tipoMemoriaWithInforme && tipoDictamenConInforme;
    } else if (this.formPart.evaluacion?.tipoEvaluacion?.id === TIPO_EVALUACION.RETROSPECTIVA) {
      const tipoDictamenWithInforme = [
        DICTAMEN.FAVORABLE_RETROSPECTIVA
      ].includes(this.formGroup.controls.dictamen.value?.id);
      return hasDictamenAndNotEdited && tipoDictamenWithInforme;
    }

    return false;
  }

  ngOnDestroy(): void {
    this.suscriptions.forEach((suscription) => suscription.unsubscribe());
  }

}
