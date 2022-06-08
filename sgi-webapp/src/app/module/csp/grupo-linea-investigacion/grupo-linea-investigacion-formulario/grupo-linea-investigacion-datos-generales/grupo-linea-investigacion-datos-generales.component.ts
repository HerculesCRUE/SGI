import { Component, OnDestroy, OnInit } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IGrupoLineaInvestigacion } from '@core/models/csp/grupo-linea-investigacion';
import { LineaInvestigacionService } from '@core/services/csp/linea-investigacion/linea-investigacion.service';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, Subject, Subscription } from 'rxjs';
import { GrupoLineaInvestigacionActionService } from '../../grupo-linea-investigacion.action.service';
import { GrupoLineaInvestigacionDatosGeneralesFragment } from './grupo-linea-investigacion-datos-generales.fragment';

const GRUPO_LINEA_INVESTIGACION_LINEA_INVESTIGACION_KEY = marker('csp.grupo-linea-investigacion');
const GRUPO_LINEA_INVESTIGACION_FECHA_INICIO_KEY = marker('csp.grupo-linea-investigacion.fecha-inicio');
const GRUPO_LINEA_INVESTIGACION_FECHA_FIN_KEY = marker('csp.grupo-linea-investigacion.fecha-fin');

@Component({
  selector: 'sgi-grupo-linea-investigacion-datos-generales',
  templateUrl: './grupo-linea-investigacion-datos-generales.component.html',
  styleUrls: ['./grupo-linea-investigacion-datos-generales.component.scss']
})
export class GrupoLineaInvestigacionDatosGeneralesComponent extends FormFragmentComponent<IGrupoLineaInvestigacion> implements OnInit, OnDestroy {
  MSG_PARAMS = MSG_PARAMS;
  gruposLineasInvestigacion$: Subject<IGrupoLineaInvestigacion[]> = new BehaviorSubject<IGrupoLineaInvestigacion[]>([]);

  datosGeneralesFragment: GrupoLineaInvestigacionDatosGeneralesFragment;
  msgParamLineaInvestigacionEntity = {};
  msgParamFechaInicioEntity = {};
  msgParamFechaFinEntity = {};
  textoInfoTituloDescriptivo: string;

  private subscriptions: Subscription[] = [];

  constructor(
    private readonly lineaInvestigacionService: LineaInvestigacionService,
    actionService: GrupoLineaInvestigacionActionService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.datosGeneralesFragment = this.fragment as GrupoLineaInvestigacionDatosGeneralesFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      GRUPO_LINEA_INVESTIGACION_LINEA_INVESTIGACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamLineaInvestigacionEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      GRUPO_LINEA_INVESTIGACION_FECHA_FIN_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaFinEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      GRUPO_LINEA_INVESTIGACION_FECHA_INICIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaInicioEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }
}
