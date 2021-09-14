import { Component, OnInit } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { CargoComite } from '@core/models/eti/cargo-comite';
import { IComite } from '@core/models/eti/comite';
import { IEvaluador } from '@core/models/eti/evaluador';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { CargoComiteService } from '@core/services/eti/cargo-comite.service';
import { ComiteService } from '@core/services/eti/comite.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiRestListResult } from '@sgi/framework/http';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { EvaluadorActionService } from '../../evaluador.action.service';
import { EvaluadorDatosGeneralesFragment } from './evaluador-datos-generales.fragment';

const MSG_ERROR_INIT_ = marker('error.load');
const EVALUDADOR_COMITE_KEY = marker('label.eti.comite');
const EVALUADOR_FECHA_ALTA_KEY = marker('eti.evaluador.fecha-alta');
const EVALUADOR_FECHA_BAJA_KEY = marker('eti.evaluador.fecha-baja');
const EVALUADOR_CARGO_COMITE_KEY = marker('eti.evaluador.cargo-comite');
const EVALUADOR_RESUMEN_KEY = marker('eti.evaluador.resumen');
const EVALUADOR_PERSONA_KEY = marker('title.eti.search.user');

@Component({
  selector: 'sgi-evaluador-datos-generales',
  templateUrl: './evaluador-datos-generales.component.html',
  styleUrls: ['./evaluador-datos-generales.component.scss']
})
export class EvaluadorDatosGeneralesComponent extends FormFragmentComponent<IEvaluador> implements OnInit {
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexPropertiesInline: FxFlexProperties;

  comites: IComite[] = [];
  cargosComite: CargoComite[] = [];
  evaluador: IEvaluador;
  filteredComites: Observable<IComite[]>;
  filteredCargosComite: Observable<CargoComite[]>;

  msgParamComiteEntity = {};
  msgParamFechaAltaEntity = {};
  msgParamFechaBajaEntity = {};
  msgParamCargoComiteEntity = {};
  msgParamResumenEntity = {};
  msgParamPersonaEntity = {};

  datosGeneralesFragment: EvaluadorDatosGeneralesFragment;

  isEditForm: boolean;

  get tipoColectivoEvaluador() {
    return TipoColectivo.EVALUADOR_ETICA;
  }

  constructor(
    private logger: NGXLogger,
    private comiteService: ComiteService,
    private cargoComiteService: CargoComiteService,
    private snackBarService: SnackBarService,
    actionService: EvaluadorActionService,
    private translate: TranslateService
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.datosGeneralesFragment = this.fragment as EvaluadorDatosGeneralesFragment;

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(50%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(22%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
    this.isEditForm = this.datosGeneralesFragment.isEdit();

    this.fxFlexPropertiesInline = new FxFlexProperties();
    this.fxFlexPropertiesInline.sm = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.md = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.order = '3';
  }

  ngOnInit() {
    super.ngOnInit();
    this.setupI18N();

    this.cargarSelectorComites();
    this.cargarSelectorCargosComite();
  }

  private setupI18N(): void {
    this.translate.get(
      EVALUDADOR_COMITE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamComiteEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      EVALUADOR_CARGO_COMITE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamCargoComiteEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      EVALUADOR_FECHA_ALTA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaAltaEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      EVALUADOR_FECHA_BAJA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaBajaEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE });

    this.translate.get(
      EVALUADOR_RESUMEN_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamResumenEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      EVALUADOR_PERSONA_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamPersonaEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });
  }

  cargarSelectorComites() {
    this.comiteService.findAll().subscribe(
      (res) => {
        this.comites = res.items;
        this.filteredComites = this.formGroup.controls.comite.valueChanges
          .pipe(
            startWith(''),
            map(value => this.filterComite(value))
          );
      },
      (error) => {
        this.logger.error(error);
        this.snackBarService.showError(MSG_ERROR_INIT_);
      }
    );
  }

  cargarSelectorCargosComite() {
    this.cargoComiteService.findAll().subscribe(
      (res: SgiRestListResult<CargoComite>) => {
        this.cargosComite = res.items;
        this.filteredCargosComite = this.formGroup.controls.cargoComite.valueChanges
          .pipe(
            startWith(''),
            map(value => this.filterCargoComite(value))
          );
      },
      (error) => {
        this.logger.error(error);
        this.snackBarService.showError(MSG_ERROR_INIT_);
      }
    );
  }

  /**
   * Devuelve el nombre de un comité.
   * @param comite comités
   * returns nombre comité
   */
  getComite(comite: IComite): string {
    return comite?.comite;
  }

  /**
   * Devuelve el nombre de un comité.
   * @param comite comités
   * returns nombre comité
   */
  getCargoComite(cargoComite: CargoComite): string {
    return cargoComite?.nombre;
  }

  /**
   * Filtro de campo autocompletable comité.
   * @param value value a filtrar (string o nombre comité).
   * @returns lista de comités filtrados.
   */
  private filterComite(value: string | IComite): IComite[] {
    let filterValue: string;
    if (typeof value === 'string') {
      filterValue = value.toLowerCase();
    } else {
      filterValue = value.comite.toLowerCase();
    }

    return this.comites.filter
      (comite => comite.comite.toLowerCase().includes(filterValue));
  }

  /**
   * Filtro de campo autocompletable cargo comité.
   * @param value value a filtrar (string o nombre cargo comité).
   * @returns lista de cargos comité filtrados.
   */
  private filterCargoComite(value: string | CargoComite): CargoComite[] {
    let filterValue: string;
    if (typeof value === 'string') {
      filterValue = value.toLowerCase();
    } else {
      filterValue = value.nombre.toLowerCase();
    }

    return this.cargosComite.filter
      (cargoComite => cargoComite.nombre.toLowerCase().includes(filterValue));
  }

}
