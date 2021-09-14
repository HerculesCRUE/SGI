import { Component, OnDestroy, OnInit } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IMemoria } from '@core/models/eti/memoria';
import { ITipoMemoria } from '@core/models/eti/tipo-memoria';
import { IPersona } from '@core/models/sgp/persona';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ComiteService } from '@core/services/eti/comite.service';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, Subject, Subscription } from 'rxjs';
import { MemoriaActionService } from '../../memoria.action.service';
import { MemoriaDatosGeneralesFragment } from './memoria-datos-generales.fragment';

const TEXT_USER_TITLE = marker('eti.solicitante');
const TEXT_USER_BUTTON = marker('btn.eti.search.solicitante');
const MEMORIA_COMITE_KEY = marker('label.eti.comite');
const MEMORIA_ORIGINAL_KEY = marker('eti.memoria.original');
const MEMORIA_TIPO_KEY = marker('eti.memoria.tipo');
const MEMORIA_CODIGO_ORGANO_COMPETENTE = marker('eti.memoria.codigo-organo-compentente');
const MEMORIA_TITULO_DESCRIPTIVO = marker('eti.memoria.titulo-descriptivo');
const INFO_TITLE_DESCRIPTIVO = marker('eti.memoria.info.titulo-descriptivo');

@Component({
  selector: 'sgi-memoria-datos-generales',
  templateUrl: './memoria-datos-generales.component.html',
  styleUrls: ['./memoria-datos-generales.component.scss']
})
export class MemoriaDatosGeneralesComponent extends FormFragmentComponent<IMemoria> implements OnInit, OnDestroy {

  fxFlexProperties: FxFlexProperties;
  fxFlexPropertiesInline: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexPropertiesTextarea: FxFlexProperties;

  tiposMemoria$: Subject<ITipoMemoria[]> = new BehaviorSubject<ITipoMemoria[]>([]);
  memorias$: Subject<IMemoria[]> = new BehaviorSubject<IMemoria[]>([]);

  datosGeneralesFragment: MemoriaDatosGeneralesFragment;
  textoUsuarioLabel = TEXT_USER_TITLE;
  textoUsuarioInput = TEXT_USER_TITLE;
  textoUsuarioButton = TEXT_USER_BUTTON;
  msgParamComiteEntity = {};
  msgParamTipoMemoriaEntity = {};
  msgParamOrginalEntity = {};
  msgParamCodigoOrganoCompenteteEntity = {};
  msgParamTituloDescriptivoEntity = {};
  textoInfoTituloDescriptivo: string;

  private subscriptions: Subscription[] = [];

  constructor(
    private readonly comiteService: ComiteService,
    actionService: MemoriaActionService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.datosGeneralesFragment = this.fragment as MemoriaDatosGeneralesFragment;

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(50%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(22%-10px)';
    this.fxFlexProperties.order = '1';

    this.fxFlexPropertiesTextarea = new FxFlexProperties();
    this.fxFlexPropertiesTextarea.sm = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesTextarea.md = '0 1 calc(50%-10px)';
    this.fxFlexPropertiesTextarea.gtMd = '0 1 calc(44%-10px)';
    this.fxFlexPropertiesTextarea.order = '1';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.subscriptions.push(this.formGroup.controls.comite.valueChanges.subscribe(
      (comite) => {
        this.datosGeneralesFragment.onComiteChange(comite);
        if (comite) {
          this.subscriptions.push(this.comiteService.findTipoMemoria(comite.id).subscribe(
            (res) => {
              this.tiposMemoria$.next(res.items);
            }
          ));
        }
        else {
          this.tiposMemoria$.next([]);
        }
      }
    ));
    this.subscriptions.push(this.formGroup.controls.tipoMemoria.valueChanges.subscribe(
      (tipoMemoria) => {
        this.datosGeneralesFragment.onTipoMemoriaChange(tipoMemoria);
        if (this.datosGeneralesFragment.showMemoriaOriginal) {
          this.comiteService.findMemoriasComitePeticionEvaluacion(
            this.formGroup.controls.comite.value.id, this.datosGeneralesFragment.idPeticionEvaluacion).subscribe(
              (response) => {
                this.memorias$.next(response.items);
              }
            );
        } else {
          this.memorias$.next([]);
        }
      }
    ));
  }

  private setupI18N(): void {
    this.translate.get(
      MEMORIA_COMITE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamComiteEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      MEMORIA_TIPO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTipoMemoriaEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      MEMORIA_ORIGINAL_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamOrginalEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      MEMORIA_CODIGO_ORGANO_COMPETENTE,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamCodigoOrganoCompenteteEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      MEMORIA_TITULO_DESCRIPTIVO,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTituloDescriptivoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      INFO_TITLE_DESCRIPTIVO,
    ).subscribe((value) => this.textoInfoTituloDescriptivo = value);
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  displayerPersonaResponsable(personaResponsable: IPersona): string {
    return personaResponsable && personaResponsable.id ?
      `${personaResponsable?.nombre} ${personaResponsable?.apellidos} (${personaResponsable?.numeroDocumento})` : null;
  }

  displayerMemoria(memoria: IMemoria): string {
    return memoria?.numReferencia;
  }
}
