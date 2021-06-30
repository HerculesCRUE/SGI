import { Component, OnDestroy, OnInit } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoSocio } from '@core/models/csp/proyecto-socio';
import { IRolSocio } from '@core/models/csp/rol-socio';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { RolSocioService } from '@core/services/csp/rol-socio.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { merge, Observable, Subscription } from 'rxjs';
import { map, startWith, tap } from 'rxjs/operators';
import { ProyectoSocioActionService } from '../../proyecto-socio.action.service';
import { ProyectoSocioDatosGeneralesFragment } from './proyecto-socio-datos-generales.fragment';

const MSG_ERROR_INIT = marker('error.load');
const PROYECTO_SOCIO_IMPORTE_CONCEDIDO_KEY = marker('csp.proyecto-socio.importe-concedido');
const PROYECTO_SOCIO_IMPORTE_PRESUPUESTO_KEY = marker('csp.proyecto-socio.importe-presupuesto');
const PROYECTO_SOCIO_FECHA_FIN_KEY = marker('csp.proyecto-socio.fecha-fin');
const PROYECTO_SOCIO_FECHA_INICIO_KEY = marker('csp.proyecto-socio.fecha-inicio');
const PROYECTO_SOCIO_NUMERO_INVESTIGADOR_KEY = marker('csp.proyecto-socio.num-investigadores');
const PROYECTO_SOCIO_ROL_SOCIO_KEY = marker('csp.proyecto-socio.rol-socio');
const PROYECTO_SOCIO_SOCIO_KEY = marker('csp.proyecto-socio.socio');
const PROYECTO_SOCIO_PERIODO_PARTICIPACION_KEY = marker('title.csp.proyecto-socio.periodo-participacion');

@Component({
  selector: 'sgi-solicitud-proyecto-socio-datos-generales',
  templateUrl: './proyecto-socio-datos-generales.component.html',
  styleUrls: ['./proyecto-socio-datos-generales.component.scss']
})
export class ProyectoSocioDatosGeneralesComponent extends FormFragmentComponent<IProyectoSocio>
  implements OnInit, OnDestroy {
  formPart: ProyectoSocioDatosGeneralesFragment;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexProperties: FxFlexProperties;
  private subscriptions: Subscription[] = [];
  private rolSocioFiltered: IRolSocio[] = [];
  rolSocios$: Observable<IRolSocio[]>;

  msgParamEntity = {};
  msgParamRolSocioEntity = {};
  msgParamNumInvestigadoresEntity = {};
  msgParamImportePresupuestoEntity = {};
  msgParamImporteConcedidoEntity = {};
  msgParamFechaInicioEntity = {};
  msgParamFechaFinEntity = {};
  msgParamPeriodoParticipacionEntity = {};

  constructor(
    private readonly logger: NGXLogger,
    protected actionService: ProyectoSocioActionService,
    private snackBarService: SnackBarService,
    private rolSocioService: RolSocioService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.formPart = this.fragment as ProyectoSocioDatosGeneralesFragment;
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(36%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(32%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.loadRolProyectos();

    this.setupI18N();

    this.subscriptions.push(
      merge(
        this.formGroup.get('empresa').valueChanges,
        this.formGroup.get('fechaInicio').valueChanges,
        this.formGroup.get('fechaFin').valueChanges
      ).pipe(
        tap(() => this.checkOverlapsPeriodosParticipacion())
      ).subscribe()
    );
  }

  private setupI18N(): void {
    this.translate.get(
      PROYECTO_SOCIO_IMPORTE_CONCEDIDO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamImporteConcedidoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      PROYECTO_SOCIO_IMPORTE_PRESUPUESTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamImportePresupuestoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      PROYECTO_SOCIO_FECHA_FIN_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaFinEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      PROYECTO_SOCIO_FECHA_INICIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaInicioEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      PROYECTO_SOCIO_NUMERO_INVESTIGADOR_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamNumInvestigadoresEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      PROYECTO_SOCIO_NUMERO_INVESTIGADOR_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamNumInvestigadoresEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      PROYECTO_SOCIO_ROL_SOCIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamRolSocioEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_SOCIO_SOCIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_SOCIO_PERIODO_PARTICIPACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamPeriodoParticipacionEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });
  }

  private loadRolProyectos(): void {
    const subscription = this.rolSocioService.findAll().pipe(
      map(result => result.items)
    ).subscribe(
      res => {
        this.rolSocioFiltered = res;
        this.rolSocios$ = this.formGroup.get('rolSocio').valueChanges.pipe(
          startWith(''),
          map(value => this.filtroRolProyecto(value))
        );
      },
      error => {
        this.logger.error(error);
        this.snackBarService.showError(MSG_ERROR_INIT);
      }
    );
    this.subscriptions.push(subscription);
  }

  private filtroRolProyecto(value: string): IRolSocio[] {
    const filterValue = value.toString().toLowerCase();
    return this.rolSocioFiltered.filter(
      rolSocio => rolSocio.nombre.toLowerCase().includes(filterValue));
  }

  getNombreRolSocio(rolProyecto?: IRolSocio): string {
    return typeof rolProyecto === 'string' ? rolProyecto : rolProyecto?.nombre;
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  private checkOverlapsPeriodosParticipacion(): void {
    const empresaForm = this.formGroup.get('empresa');
    const fechaInicioForm = this.formGroup.get('fechaInicio');
    const fechaFinForm = this.formGroup.get('fechaFin');

    const proyectoSocios = this.actionService.proyectoSocios.filter(
      element => element.empresa.id === empresaForm.value.id
        && element.id !== this.formPart.proyectoSocio.id);


    const fechaInicio = fechaInicioForm.value ? fechaInicioForm.value.toMillis() : Number.MIN_VALUE;
    const fechaFin = fechaFinForm.value ? fechaFinForm.value.toMillis() : Number.MAX_VALUE;


    const ranges = proyectoSocios.map(proyectoSocio => {
      return {
        inicio: proyectoSocio.fechaInicio ? proyectoSocio.fechaInicio.toMillis() : Number.MIN_VALUE,
        fin: proyectoSocio.fechaFin ? proyectoSocio.fechaFin.toMillis() : Number.MAX_VALUE
      };
    });

    if (ranges.some(r => fechaInicio <= r.fin && r.inicio <= fechaFin)) {
      if (fechaInicioForm.value) {
        fechaInicioForm.setErrors({ overlapped: true });
        fechaInicioForm.markAsTouched({ onlySelf: true });
      }

      if (fechaFinForm.value) {
        fechaFinForm.setErrors({ overlapped: true });
        fechaFinForm.markAsTouched({ onlySelf: true });
      }

      if (!fechaInicioForm.value && !fechaFinForm.value) {
        empresaForm.setErrors({ overlapped: true });
        empresaForm.markAsTouched({ onlySelf: true });
      } else if (empresaForm.errors) {
        delete empresaForm.errors.overlapped;
        empresaForm.updateValueAndValidity({ onlySelf: true });
      }

    } else {
      if (fechaInicioForm.errors) {
        delete fechaInicioForm.errors.overlapped;
        fechaInicioForm.updateValueAndValidity({ onlySelf: true });
      }

      if (fechaFinForm.errors) {
        delete fechaFinForm.errors.overlapped;
        fechaFinForm.updateValueAndValidity({ onlySelf: true });
      }

      if (empresaForm.errors) {
        delete empresaForm.errors.overlapped;
        empresaForm.updateValueAndValidity({ onlySelf: true });
      }
    }
  }
}
