import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { CLASIFICACION_CVN_MAP } from '@core/enums/clasificacion-cvn';
import { MSG_PARAMS } from '@core/i18n';
import { Estado, ESTADO_MAP } from '@core/models/csp/estado-proyecto';
import { CAUSA_EXENCION_MAP, IProyecto, TIPO_HORAS_ANUALES_MAP } from '@core/models/csp/proyecto';
import { IProyectoIVA } from '@core/models/csp/proyecto-iva';
import { IProyectoProyectoSge } from '@core/models/csp/proyecto-proyecto-sge';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap, tap } from 'rxjs/operators';
import { ProyectoActionService } from '../../proyecto.action.service';
import { ProyectoFichaGeneralFragment } from './proyecto-ficha-general.fragment';

const PROYECTO_ACRONIMO_KEY = marker('csp.proyecto.acronimo');
const PROYECTO_AMBITO_GEOGRAFICO_KEY = marker('csp.proyecto.ambito-geografico');
const PROYECTO_CALCULO_COSTE_KEY = marker('csp.proyecto.calculo-coste-personal');
const PROYECTO_CODIGO_EXTERNO_KEY = marker('csp.proyecto.codigo-externo');
const PROYECTO_CONFIDENCIAL_KEY = marker('csp.proyecto.confidencial');
const PROYECTO_COORDINADOR_EXTERNO_KEY = marker('csp.proyecto.coordinador-externo');
const PROYECTO_CONVOCATORIA_EXTERNA_KEY = marker('csp.proyecto.convocatoria-externa');
const PROYECTO_FECHA_FIN_KEY = marker('csp.proyecto.fecha-fin');
const PROYECTO_FECHA_FIN_DEFINITIVA_KEY = marker('csp.proyecto.fecha-fin-definitiva');
const PROYECTO_FECHA_INICIO_KEY = marker('csp.proyecto.fecha-inicio');
const PROYECTO_FINALIDAD_KEY = marker('csp.proyecto.finalidad');
const PROYECTO_IVA_KEY = marker('csp.proyecto.iva');
const PROYECTO_HORAS_ANUALES_KEY = marker('csp.proyecto.horas-anuales');
const PROYECTO_CAUSA_EXENCION_KEY = marker('csp.proyecto.causa-exencion');
const PROYECTO_MODELO_EJECUCION_KEY = marker('csp.proyecto.modelo-ejecucion');
const PROYECTO_PAQUETE_TRABAJO_KEY = marker('csp.proyecto-paquete-trabajo');
const PROYECTO_PROYECTO_COLABORATIVO_KEY = marker('csp.proyecto.proyecto-colaborativo');
const PROYECTO_PROYECTO_COORDINADO_KEY = marker('csp.proyecto.proyecto-coordinado');
const PROYECTO_TIMESHEET_KEY = marker('csp.proyecto.timesheet');
const PROYECTO_TITULO_KEY = marker('csp.proyecto.titulo');
const PROYECTO_UNIDAD_GESTION_KEY = marker('csp.proyecto.unidad-gestion');
const MSG_PROYECTO_VALUE_CONVOCATORIA = marker('msg.csp.proyecto.value-convocatoria');

@Component({
  selector: 'sgi-proyecto-ficha-general',
  templateUrl: './proyecto-ficha-general.component.html',
  styleUrls: ['./proyecto-ficha-general.component.scss']
})
export class ProyectoFichaGeneralComponent extends FormFragmentComponent<IProyecto> implements OnInit, OnDestroy {

  formPart: ProyectoFichaGeneralFragment;

  fxFlexProperties: FxFlexProperties;
  fxFlexPropertiesOne: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexPropertiesInline: FxFlexProperties;
  fxFlexPropertiesEntidad: FxFlexProperties;

  proyectosSge: StatusWrapper<IProyectoProyectoSge>[];

  displayedColumns = ['iva', 'fechaInicio', 'fechaFin'];
  elementosPagina = [5, 10, 25, 100];

  dataSource = new MatTableDataSource<StatusWrapper<IProyectoIVA>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  private subscriptions = [] as Subscription[];

  msgParamAmbitoGeograficoEntity = {};
  msgParamAcronimoEntity = {};
  msgParamCalculoCosteEntity = {};
  msgParamCodigoExternoEntity = {};
  msgParamConfidencialEntity = {};
  msgParamCoordinadorExternoEntity = {};
  msgParamConvocatoriaExternaEntity = {};
  msgParamFechaFinEntity = {};
  msgParamFechaInicioEntity = {};
  msgParamFinalidadEntity = {};
  msgParamHorasAnualesEntity = {};
  msgParamIvaEntity = {};
  msgParamCausaExencionEntity = {};
  msgParamModeloEjecucionEntity = {};
  msgParamPaqueteTrabajoEntity = {};
  msgParamProyectoColaborativoEntity = {};
  msgParamTituloEntity = {};
  msgParamTimesheetEntity = {};
  msgParamUnidadGestionEntity = {};
  msgParamFechaFinDefinitivaEntity = {};
  msgParamProyectoCoordinadoEntity = {};
  textoInfoAmbitoGeograficoConvocatoria: string;
  textoInfoFinalidadConvocatoria: string;
  textoInfoUnidadGestionConvocatoria: string;
  textoInfoModeloEjecucionConvocatoria: string;

  get CLASIFICACION_CVN_MAP() {
    return CLASIFICACION_CVN_MAP;
  }

  get TIPO_HORAS_ANUALES_MAP() {
    return TIPO_HORAS_ANUALES_MAP;
  }

  get CAUSA_EXENCION_MAP() {
    return CAUSA_EXENCION_MAP;
  }

  get ESTADO_MAP() {
    return ESTADO_MAP;
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  private obligatorioTimesheet: boolean;
  requiredHoras = false;

  constructor(
    protected actionService: ProyectoActionService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.FICHA_GENERAL, actionService);
    this.formPart = this.fragment as ProyectoFichaGeneralFragment;

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(32%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxFlexPropertiesEntidad = new FxFlexProperties();
    this.fxFlexPropertiesEntidad.sm = '0 1 calc(36%-10px)';
    this.fxFlexPropertiesEntidad.md = '0 1 calc(36%-10px)';
    this.fxFlexPropertiesEntidad.gtMd = '0 1 calc(36%-10px)';
    this.fxFlexPropertiesEntidad.order = '3';

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(36%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(32%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxFlexPropertiesInline = new FxFlexProperties();
    this.fxFlexPropertiesInline.sm = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.md = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.order = '4';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.setupI18N();

    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IProyectoIVA>, property: string) => {
        switch (property) {
          case 'iva':
            return wrapper.value.iva;
          case 'fechaInicio':
            return wrapper.value.fechaInicio;
          case 'fechaFin':
            return wrapper.value.fechaFin;
          default:
            return wrapper[property];
        }
      };
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.proyectoIva$.subscribe(elements => {
      this.dataSource.data = elements;
    }));

    if (!this.fragment.isEdit()) {
      this.formGroup.controls.estado.setValue(Estado.BORRADOR);
    }

    this.subscriptions.push(this.formGroup.controls.costeHora.valueChanges.pipe(
      tap(() => this.validarCoste())
    ).subscribe());

    this.subscriptions.push(this.formGroup.controls.timesheet.valueChanges.pipe(
      tap(() => this.validarTimesheet())
    ).subscribe());

    if (!this.formPart?.isInvestigador) {
      this.subscriptions.push(
        this.formPart.finalidadConvocatoria$.subscribe(
          (value) => {
            if (value) {
              this.setTextoInfoFinalidadConvocatoria();
            }
          }
        ));

      this.subscriptions.push(
        this.formPart.ambitoGeograficoConvocatoria$.subscribe(
          (value) => {
            if (value) {
              this.setTextoInfoAmbitoGeograficoConvocatoria();
            }
          }
        ));

      this.subscriptions.push(
        this.formPart.unidadGestionConvocatoria$.subscribe(
          (value) => {
            if (value && this.formPart.unidadGestionConvocatoria?.nombre) {
              this.setTextoInfoUnidadGestionConvocatoria();
            }
          }
        ));
    }
    this.subscriptions.push(
      this.formPart.modeloEjecucionConvocatoria$.subscribe(
        (value) => {
          if (value) {
            this.setTextoInfoModeloEjecucionConvocatoria();
          }
        }
      ));
    this.subscriptions.push(this.actionService.proyectosSge$.subscribe(elements => {
      this.proyectosSge = elements;
      this.formPart.getFormGroup().controls.codigosSge.patchValue(elements, { onlySelf: true, emitEvent: false });
    }));
  }

  private setupI18N(): void {

    this.translate.get(
      PROYECTO_TITULO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTituloEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_CODIGO_EXTERNO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamCodigoExternoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      PROYECTO_FECHA_INICIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaInicioEntity =
      { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_FECHA_FIN_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaFinEntity =
      { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_FECHA_FIN_DEFINITIVA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaFinDefinitivaEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE });

    this.translate.get(
      PROYECTO_ACRONIMO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamAcronimoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      PROYECTO_CONVOCATORIA_EXTERNA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamConvocatoriaExternaEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE });

    this.translate.get(
      PROYECTO_UNIDAD_GESTION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamUnidadGestionEntity =
      { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_MODELO_EJECUCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamModeloEjecucionEntity =
      { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_FINALIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFinalidadEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_AMBITO_GEOGRAFICO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamAmbitoGeograficoEntity =
      { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_CONFIDENCIAL_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamConfidencialEntity =
      { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_PROYECTO_COORDINADO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamProyectoCoordinadoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_PROYECTO_COLABORATIVO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamProyectoColaborativoEntity =
      { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_COORDINADOR_EXTERNO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamCoordinadorExternoEntity =
      { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_TIMESHEET_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTimesheetEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_PAQUETE_TRABAJO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamPaqueteTrabajoEntity =
      { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_CALCULO_COSTE_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamCalculoCosteEntity =
      { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });

    this.translate.get(
      PROYECTO_HORAS_ANUALES_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamHorasAnualesEntity =
      { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });

    this.translate.get(
      PROYECTO_CAUSA_EXENCION_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamCausaExencionEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });

    this.translate.get(
      PROYECTO_IVA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamIvaEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
  }


  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  /**
   * En caso de ser NO timesheet
   * y SI calculo coste
   * Error en el formulario
   */
  private validarTimesheet() {
    if (this.formGroup.controls.timesheet.value === false && this.formGroup.controls.costeHora.value === true) {
      this.formGroup.controls.timesheet.setErrors({ invalid: true });
      this.formGroup.controls.timesheet.markAsTouched({ onlySelf: true });
    }
  }

  /**
   * En caso de activar coste hora personal
   * Habilitamos horas anuales
   */
  private validarCoste() {
    if (this.formGroup.controls.costeHora.value) {
      this.requiredHoras = true;
      if (this.formGroup.controls.timesheet.value === null || this.formGroup.controls.timesheet.value === false) {
        this.formGroup.controls.timesheet.setErrors({ invalid: true });
        this.formGroup.controls.timesheet.markAsTouched({ onlySelf: true });
        this.obligatorioTimesheet = true;
        this.requiredHoras = true;
      } else {
        this.obligatorioTimesheet = false;

        if (this.formGroup.controls.costeHora.value === true) {
          this.requiredHoras = true;
        } else {
          this.requiredHoras = false;
        }
        this.formGroup.controls.timesheet.updateValueAndValidity({ onlySelf: true });
      }
    } else {
      if (this.formGroup.controls.tipoHorasAnuales.value) {
        this.formGroup.controls.tipoHorasAnuales.patchValue(null);
        this.requiredHoras = false;
        this.formGroup.controls.tipoHorasAnuales.setValidators(null);
        this.formGroup.controls.tipoHorasAnuales.updateValueAndValidity({ onlySelf: true });
      }
      if (this.formGroup.controls.timesheet.value === false) {
        this.obligatorioTimesheet = false;
        this.requiredHoras = false;
        this.formGroup.controls.timesheet.updateValueAndValidity({ onlySelf: true });
      }
      this.requiredHoras = false;
      this.formGroup.controls.tipoHorasAnuales.setValidators(null);
      this.formGroup.controls.tipoHorasAnuales.updateValueAndValidity({ onlySelf: true });
    }
  }

  /**
   * Validacion timesheet por estado u obligatorio
   */
  validacionTimesheet() {
    if (this.formPart.abiertoRequired === true || this.obligatorioTimesheet === true) {
      return true;
    } else {
      return false;
    }
  }

  setTextoInfoFinalidadConvocatoria() {
    this.translate.get(
      this.formPart.finalidadConvocatoria.nombre,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_PROYECTO_VALUE_CONVOCATORIA,
          { valueConvocatoria: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoInfoFinalidadConvocatoria = value);
  }

  setTextoInfoAmbitoGeograficoConvocatoria() {
    this.translate.get(
      this.formPart.ambitoGeograficoConvocatoria.nombre,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_PROYECTO_VALUE_CONVOCATORIA,
          { valueConvocatoria: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoInfoAmbitoGeograficoConvocatoria = value);
  }

  setTextoInfoUnidadGestionConvocatoria() {
    this.translate.get(
      this.formPart.unidadGestionConvocatoria.nombre,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_PROYECTO_VALUE_CONVOCATORIA,
          { valueConvocatoria: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoInfoUnidadGestionConvocatoria = value);
  }

  setTextoInfoModeloEjecucionConvocatoria() {
    this.translate.get(
      this.formPart.modeloEjecucionConvocatoria.nombre,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_PROYECTO_VALUE_CONVOCATORIA,
          { valueConvocatoria: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoInfoModeloEjecucionConvocatoria = value);
  }
}
