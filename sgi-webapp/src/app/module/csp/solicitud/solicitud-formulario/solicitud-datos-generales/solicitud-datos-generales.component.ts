import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort, MatSortable } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { FORMULARIO_SOLICITUD_MAP } from '@core/enums/formulario-solicitud';
import { MSG_PARAMS } from '@core/i18n';
import { ESTADO_MAP } from '@core/models/csp/estado-solicitud';
import { ISolicitud } from '@core/models/csp/solicitud';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';
import { SolicitudModalidadEntidadConvocanteModalComponent, SolicitudModalidadEntidadConvocanteModalData } from '../../modals/solicitud-modalidad-entidad-convocante-modal/solicitud-modalidad-entidad-convocante-modal.component';
import { SolicitudActionService } from '../../solicitud.action.service';
import { SolicitudDatosGeneralesFragment, SolicitudModalidadEntidadConvocanteListado } from './solicitud-datos-generales.fragment';

const MSG_ERROR_INIT = marker('error.load');
const SOLICITUD_CODIGO_EXTERNO_KEY = marker('csp.solicitud.codigo-externo');
const SOLICITUD_CONVOCATORIA_EXTERNA_KEY = marker('csp.solicitud.convocatoria-externa');
const SOLICITUD_OBSERVACIONES_KEY = marker('csp.solicitud.observaciones');
const SOLICITUD_UNIDAD_GESTION_KEY = marker('csp.solicitud.unidad-gestion');
const SOLICITUD_ENTIDAD_CONVOCANTE_KEY = marker('csp.solicitud-entidad-convocante');
const SOLICITUD_TITULO_KEY = marker('csp.solicitud.titulo');
const SOLICITUD_FORMULARIO_SOLICITUD_KEY = marker('csp.solicitud.tipo-formulario');
const SOLICITUD_CONVOCATORIA_KEY = marker('csp.solicitud.convocatoria');
const SOLICITUD_SOLICITANTE_KEY = marker('csp.solicitud.solicitante');

@Component({
  selector: 'sgi-solicitud-datos-generales',
  templateUrl: './solicitud-datos-generales.component.html',
  styleUrls: ['./solicitud-datos-generales.component.scss']
})
export class SolicitudDatosGeneralesComponent extends FormFragmentComponent<ISolicitud> implements OnInit, OnDestroy {

  formPart: SolicitudDatosGeneralesFragment;

  fxFlexProperties: FxFlexProperties;
  fxFlexPropertiesOne: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexPropertiesInline: FxFlexProperties;
  fxFlexPropertiesEntidad: FxFlexProperties;

  totalElementos: number;
  displayedColumns: string[];
  elementosPagina: number[];

  msgParamConvocatoriaExternaEntity = {};
  msgParamCodigoExternoEntity = {};
  msgParamEntidadConvocanteEntity = {};
  msgParamObservacionesEntity = {};
  msgParamUnidadGestionEntity = {};
  msgParamTituloEntity = {};
  msgParamFormularioSolicitudEntity = {};
  msgParamConvocatoriaEntity = {};
  msgParamSolicitanteEntity = {};

  dataSourceEntidadesConvocantes: MatTableDataSource<SolicitudModalidadEntidadConvocanteListado>;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  private subscriptions = [] as Subscription[];

  get tipoColectivoSolicitante() {
    return TipoColectivo.SOLICITANTE_CSP;
  }

  get FORMULARIO_SOLICITUD_MAP() {
    return FORMULARIO_SOLICITUD_MAP;
  }

  get ESTADO_MAP() {
    return ESTADO_MAP;
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    private readonly logger: NGXLogger,
    protected actionService: SolicitudActionService,
    private snackBarService: SnackBarService,
    private matDialog: MatDialog,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.formPart = this.fragment as SolicitudDatosGeneralesFragment;

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

    this.elementosPagina = [5, 10, 25, 100];
    this.displayedColumns = ['entidadConvocante', 'plan', 'programaConvocatoria', 'modalidadSolicitud', 'acciones'];
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.dataSourceEntidadesConvocantes = new MatTableDataSource<SolicitudModalidadEntidadConvocanteListado>();

    this.dataSourceEntidadesConvocantes.sortingDataAccessor =
      (entidadConvocanteModalidad: SolicitudModalidadEntidadConvocanteListado, property: string) => {
        switch (property) {
          case 'entidadConvocante':
            return entidadConvocanteModalidad.entidadConvocante.entidad.nombre;
          case 'plan':
            return entidadConvocanteModalidad.plan.nombre;
          case 'programaConvocatoria':
            return entidadConvocanteModalidad.entidadConvocante.programa.nombre;
          case 'modalidadSolicitud':
            return entidadConvocanteModalidad.modalidad?.value.programa?.nombre;
          default:
            return entidadConvocanteModalidad[property];
        }
      };

    this.subscriptions.push(this.formPart.entidadesConvocantesModalidad$.subscribe(elements => {
      // Bind on new data where convocatoria is selected
      if (this.formGroup.controls.convocatoria.value) {
        this.dataSourceEntidadesConvocantes.paginator = this.paginator;
        this.sort?.sort(({ id: 'entidadConvocante', start: 'asc' }) as MatSortable);
        this.dataSourceEntidadesConvocantes.sort = this.sort;
      }
      this.dataSourceEntidadesConvocantes.data = elements;
    }));
  }

  private setupI18N(): void {
    this.translate.get(
      SOLICITUD_CODIGO_EXTERNO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamCodigoExternoEntity =
      { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITUD_CONVOCATORIA_EXTERNA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamConvocatoriaExternaEntity =
      { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITUD_OBSERVACIONES_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamObservacionesEntity =
      { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });

    this.translate.get(
      SOLICITUD_UNIDAD_GESTION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamUnidadGestionEntity =
      { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITUD_ENTIDAD_CONVOCANTE_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamEntidadConvocanteEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE });

    this.translate.get(
      SOLICITUD_TITULO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTituloEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITUD_FORMULARIO_SOLICITUD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFormularioSolicitudEntity =
      { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITUD_CONVOCATORIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamConvocatoriaEntity =
      { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITUD_SOLICITANTE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamSolicitanteEntity =
      { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  /**
   * Apertura de modal de modadidad solicitud
   *
   * @param entidadConvocanteModalidad EntidadConvocanteModalidad que se carga en el modal para modificarlo.
   */
  openModalSelectModalidad(entidadConvocanteModalidad: SolicitudModalidadEntidadConvocanteListado): void {
    const data: SolicitudModalidadEntidadConvocanteModalData = {
      entidad: entidadConvocanteModalidad.entidadConvocante.entidad,
      plan: entidadConvocanteModalidad.plan,
      programa: entidadConvocanteModalidad.entidadConvocante.programa,
      modalidad: entidadConvocanteModalidad.modalidad?.value,
      readonly: this.formPart.readonly
    };

    const config = {
      panelClass: 'sgi-dialog-container',
      data
    };

    const dialogRef = this.matDialog.open(SolicitudModalidadEntidadConvocanteModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (entidadConvocanteModalidadModal: SolicitudModalidadEntidadConvocanteModalData) => {

        if (!entidadConvocanteModalidadModal) {
          return;
        }

        if (!entidadConvocanteModalidad.modalidad) {
          this.formPart.addSolicitudModalidad(entidadConvocanteModalidadModal.modalidad);
        } else if (!entidadConvocanteModalidadModal.modalidad) {
          this.formPart.deleteSolicitudModalidad(entidadConvocanteModalidad.modalidad);
        } else if (!entidadConvocanteModalidad.modalidad.created) {
          this.formPart.updateSolicitudModalidad(entidadConvocanteModalidadModal.modalidad);
        }
      }
    );
  }
}
