import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IFuenteFinanciacion } from '@core/models/csp/fuente-financiacion';
import { ITipoAmbitoGeografico } from '@core/models/csp/tipo-ambito-geografico';
import { ITipoOrigenFuenteFinanciacion } from '@core/models/csp/tipo-origen-fuente-financiacion';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { TipoAmbitoGeograficoService } from '@core/services/csp/tipo-ambito-geografico.service';
import { TipoOrigenFuenteFinanciacionService } from '@core/services/csp/tipo-origen-fuente-financiacion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiRestListResult } from '@sgi/framework/http/types';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, Observable } from 'rxjs';
import { map, startWith, switchMap } from 'rxjs/operators';

const MSG_ERROR_INIT = marker('error.load');
const FUENTE_FINANCIACION_KEY = marker('csp.fuente-financiacion');
const FUENTE_FINANCIACION_NOMBRE_KEY = marker('csp.fuente-financiacion.nombre');
const FUENTE_FINANCIACION_AMBITO_GEOGRAFICO_KEY = marker('csp.fuente-financiacion.ambito-geografico');
const FUENTE_FINANCIACION_ORIGEN_KEY = marker('csp.fuente-financiacion.origen');
const FUENTE_FINANCIACION_FONDO_ESTRUCTURAL_KEY = marker('csp.fuente-financiacion.fondo-estructural');
const TITLE_NEW_ENTITY = marker('title.new.entity');
const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');

@Component({
  templateUrl: './fuente-financiacion-modal.component.html',
  styleUrls: ['./fuente-financiacion-modal.component.scss']
})
export class FuenteFinanciacionModalComponent extends
  BaseModalComponent<IFuenteFinanciacion, FuenteFinanciacionModalComponent> implements OnInit, OnDestroy {
  fxLayoutProperties: FxLayoutProperties;
  public fuenteFinanciacion: IFuenteFinanciacion;

  ambitosGeograficos$: BehaviorSubject<ITipoAmbitoGeografico[]> = new BehaviorSubject<ITipoAmbitoGeografico[]>([]);
  origenes$: BehaviorSubject<ITipoOrigenFuenteFinanciacion[]> = new BehaviorSubject<ITipoOrigenFuenteFinanciacion[]>([]);

  msgParamAmbitoEntity = {};
  msgParamNombreEntity = {};
  msgParamOrigenEntity = {};
  msgParamFondoEstructuralEntity = {};
  title: string;

  textSaveOrUpdate: string;

  constructor(
    private readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<FuenteFinanciacionModalComponent>,
    private ambitoGeograficoService: TipoAmbitoGeograficoService,
    private tipoOrigenFuenteFinanciacionService: TipoOrigenFuenteFinanciacionService,
    @Inject(MAT_DIALOG_DATA) fuenteFinanciacion: IFuenteFinanciacion,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, fuenteFinanciacion);
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.layoutAlign = 'row';
    if (fuenteFinanciacion) {
      this.fuenteFinanciacion = { ...fuenteFinanciacion };
    } else {
      this.fuenteFinanciacion = { activo: true } as IFuenteFinanciacion;
    }
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.loadAmbitosGeograficos();
    this.loadOrigenes();
  }

  private setupI18N(): void {
    this.translate.get(
      FUENTE_FINANCIACION_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      FUENTE_FINANCIACION_AMBITO_GEOGRAFICO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamAmbitoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      FUENTE_FINANCIACION_ORIGEN_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamOrigenEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      FUENTE_FINANCIACION_FONDO_ESTRUCTURAL_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFondoEstructuralEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.fuenteFinanciacion.nombre) {

      this.translate.get(
        FUENTE_FINANCIACION_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);

      this.textSaveOrUpdate = MSG_ACEPTAR;
    } else {
      this.translate.get(
        FUENTE_FINANCIACION_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).pipe(
        switchMap((value) => {
          return this.translate.get(
            TITLE_NEW_ENTITY,
            { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
          );
        })
      ).subscribe((value) => this.title = value);

      this.textSaveOrUpdate = MSG_ANADIR;
    }

  }

  private loadAmbitosGeograficos() {
    this.subscriptions.push(
      this.ambitoGeograficoService.findAll().subscribe(
        (res: SgiRestListResult<ITipoAmbitoGeografico>) => this.ambitosGeograficos$.next(res.items),
        (error) => {
          this.logger.error(error);
          this.snackBarService.showError(MSG_ERROR_INIT);
        }
      )
    );
  }

  private loadOrigenes() {
    this.subscriptions.push(
      this.tipoOrigenFuenteFinanciacionService.findAll().subscribe(
        (res: SgiRestListResult<ITipoOrigenFuenteFinanciacion>) => this.origenes$.next(res.items),
        (error) => {
          this.logger.error(error);
          this.snackBarService.showError(MSG_ERROR_INIT);
        }
      )
    );
  }

  protected getDatosForm(): IFuenteFinanciacion {
    this.fuenteFinanciacion.nombre = this.formGroup.controls.nombre.value;
    this.fuenteFinanciacion.descripcion = this.formGroup.controls.descripcion.value;
    this.fuenteFinanciacion.tipoAmbitoGeografico = this.formGroup.controls.ambitoGeografico.value;
    this.fuenteFinanciacion.tipoOrigenFuenteFinanciacion = this.formGroup.controls.origen.value;
    this.fuenteFinanciacion.fondoEstructural = this.formGroup.controls.fondoEstructural.value;
    return this.fuenteFinanciacion;
  }

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      nombre: new FormControl(this.fuenteFinanciacion?.nombre),
      descripcion: new FormControl(this.fuenteFinanciacion?.descripcion),
      ambitoGeografico: new FormControl(this.fuenteFinanciacion?.tipoAmbitoGeografico),
      origen: new FormControl(this.fuenteFinanciacion?.tipoOrigenFuenteFinanciacion),
      fondoEstructural: new FormControl(this.fuenteFinanciacion?.id ? this.fuenteFinanciacion.fondoEstructural : true),
    });
    return formGroup;
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
  }
}
