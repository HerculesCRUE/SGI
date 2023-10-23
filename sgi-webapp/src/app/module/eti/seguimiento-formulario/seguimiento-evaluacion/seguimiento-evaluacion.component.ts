import { AfterViewInit, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { DICTAMEN, IDictamen } from '@core/models/eti/dictamen';
import { IMemoria } from '@core/models/eti/memoria';
import { TIPO_EVALUACION } from '@core/models/eti/tipo-evaluacion';
import { IDocumento } from '@core/models/sgdoc/documento';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { TipoEvaluacionService } from '@core/services/eti/tipo-evaluacion.service';
import { DocumentoService, triggerDownloadToUser } from '@core/services/sgdoc/documento.service';
import { Observable, of, Subscription } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { SeguimientoFormularioActionService } from '../seguimiento-formulario.action.service';
import { SeguimientoListadoAnteriorMemoriaComponent } from '../seguimiento-listado-anterior-memoria/seguimiento-listado-anterior-memoria.component';
import { SeguimientoEvaluacionFragment } from './seguimiento-evaluacion.fragment';

@Component({
  selector: 'sgi-seguimiento-evaluacion',
  templateUrl: './seguimiento-evaluacion.component.html',
  styleUrls: ['./seguimiento-evaluacion.component.scss']
})
export class SeguimientoEvaluacionComponent extends FormFragmentComponent<IMemoria> implements AfterViewInit, OnInit, OnDestroy {

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexPropertiesInline: FxFlexProperties;

  @ViewChild('evaluaciones') evaluaciones: SeguimientoListadoAnteriorMemoriaComponent;

  dictamenes$: Observable<IDictamen[]>;
  suscriptions: Subscription[] = [];

  formPart: SeguimientoEvaluacionFragment;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    public actionService: SeguimientoFormularioActionService,
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

    this.formPart = this.fragment as SeguimientoEvaluacionFragment;

    this.dictamenes$ = this.formPart.evaluacion$.pipe(
      switchMap(evaluacion => {
        if (evaluacion) {
          return evaluacionService.findAllDictamenEvaluacion(
            evaluacion.id
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
    this.suscriptions.push(this.formGroup.controls.dictamen.valueChanges.subscribe((dictamen) => {
      this.actionService.setDictamen(dictamen);
    }));
  }

  ngAfterViewInit(): void {
    this.evaluaciones.memoriaId = this.actionService.getEvaluacion()?.memoria?.id;
    this.evaluaciones.evaluacionId = this.actionService.getEvaluacion()?.id;
    this.evaluaciones.ngAfterViewInit();
  }

  ngOnDestroy(): void {
    this.suscriptions.forEach((suscription) => suscription.unsubscribe());
  }

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

    if (this.formPart.evaluacion?.tipoEvaluacion?.id === TIPO_EVALUACION.SEGUIMIENTO_ANUAL) {
      return this.formGroup.controls.dictamen.value?.id === DICTAMEN.SOLICITUD_MODIFICACIONES_SEG_ANUAL;
    } else if (this.formPart.evaluacion?.tipoEvaluacion?.id === TIPO_EVALUACION.SEGUIMIENTO_FINAL) {
      return this.formGroup.controls.dictamen.value?.id === DICTAMEN.SOLICITUD_ACLARACIONES_SEG_FINAL;
    }

    return false;
  }

}
