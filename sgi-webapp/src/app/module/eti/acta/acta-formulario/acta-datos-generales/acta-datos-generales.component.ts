import { Component, OnInit, ViewChild } from '@angular/core';
import { MatAutocompleteTrigger } from '@angular/material/autocomplete';
import { Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IActa } from '@core/models/eti/acta';
import { IConvocatoriaReunion } from '@core/models/eti/convocatoria-reunion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ConvocatoriaReunionService } from '@core/services/eti/convocatoria-reunion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiRestListResult } from '@sgi/framework/http/types';
import { NGXLogger } from 'ngx-logger';
import { Observable, Subscription } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { ActaActionService } from '../../acta.action.service';

const MSG_ERROR_INIT = marker('error.load');
const ACTA_CONVOCATORIA_REUNION_KEY = marker('eti.acta.convocatoria-reunion');
const ACTA_HORA_INICIO_KEY = marker('eti.acta.hora-inicio');
const ACTA_HORA_FIN_KEY = marker('eti.acta.hora-fin');
const ACTA_RESUMEN_KEY = marker('eti.acta.resumen');

@Component({
  selector: 'sgi-acta-datos-generales',
  templateUrl: './acta-datos-generales.component.html',
  styleUrls: ['./acta-datos-generales.component.scss']
})
export class ActaDatosGeneralesComponent extends FormFragmentComponent<IActa> implements OnInit {
  @ViewChild(MatAutocompleteTrigger) autocomplete: MatAutocompleteTrigger;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexPropertiesInline: FxFlexProperties;

  convocatoriasReunionFitlered: Observable<IConvocatoriaReunion[]>;
  convocatoriasReunion: IConvocatoriaReunion[];

  suscripciones: Subscription[] = [];

  readonly: boolean;

  msgParamConvocatoriaReunionEntity = {};
  msgParamHoraInicioEntity = {};
  msgParamHoraFinEntity = {};
  msgParamResumenEntity = {};

  constructor(
    private readonly logger: NGXLogger,
    private readonly convocatoriaReunionService: ConvocatoriaReunionService,
    private readonly snackBarService: SnackBarService,
    private actionService: ActaActionService,
    public router: Router,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
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
  }

  ngOnInit() {
    super.ngOnInit();
    this.setupI18N();

    this.readonly = this.actionService.readonly;

    this.suscripciones.push(
      this.convocatoriaReunionService.findConvocatoriasSinActa().subscribe(
        (res: SgiRestListResult<IConvocatoriaReunion>) => {
          this.convocatoriasReunion = res.items;
          this.convocatoriasReunionFitlered = this.formGroup.controls.convocatoriaReunion.valueChanges
            .pipe(
              startWith(''),
              map(value => this.filtro(value))
            );
        },
        (error) => {
          this.logger.error(error);
          this.snackBarService.showError(MSG_ERROR_INIT);
        }
      )
    );
  }

  private setupI18N(): void {
    this.translate.get(
      ACTA_CONVOCATORIA_REUNION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamConvocatoriaReunionEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      ACTA_HORA_INICIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamHoraInicioEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      ACTA_HORA_FIN_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamHoraFinEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      ACTA_RESUMEN_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamResumenEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
  }

  /**
   * Filtra la lista devuelta por el servicio
   *
   * @param value del input para autocompletar
   */
  filtro(value: string | IConvocatoriaReunion): IConvocatoriaReunion[] {
    let filterValue: string;
    if (typeof value === 'string') {
      filterValue = value.toLowerCase();
    } else {
      filterValue = value.codigo.toLowerCase();
    }

    return this.convocatoriasReunion.filter
      (convocatoriaReunion => convocatoriaReunion.codigo.toLowerCase().includes(filterValue));
  }

  /**
   * Devuelve el código de una convocatoria reunión
   * @param convocatoria convocatoria
   * @returns código de una convocatoria reunión
   */
  getConvocatoria(convocatoriaReunion?: IConvocatoriaReunion): string | undefined {
    return typeof convocatoriaReunion === 'string' ? convocatoriaReunion : convocatoriaReunion?.codigo;
  }

  /**
   * Registra el evento de modificación de convocatoria reunión.
   * @param convocatoriaReunion  convocatoria reunión seleccionada
   */
  selectConvocatoriaReunion(convocatoriaReunion: IConvocatoriaReunion | string) {
    this.actionService.setIdConvocatoria(convocatoriaReunion ? (convocatoriaReunion as IConvocatoriaReunion).id : null);
  }

}
