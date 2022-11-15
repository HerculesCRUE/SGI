import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { FORMULARIO_SOLICITUD_MAP } from '@core/enums/formulario-solicitud';
import { MSG_PARAMS } from '@core/i18n';
import { IConfiguracionSolicitud } from '@core/models/csp/configuracion-solicitud';
import { IConvocatoriaFase } from '@core/models/csp/convocatoria-fase';
import { IDocumentoRequeridoSolicitud } from '@core/models/csp/documento-requerido-solicitud';
import { ITipoFase } from '@core/models/csp/tipos-configuracion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { ConvocatoriaActionService } from '../../convocatoria.action.service';
import { ConvocatoriaConfiguracionSolicitudesModalComponent, ConvocatoriaConfiguracionSolicitudesModalData } from '../../modals/convocatoria-configuracion-solicitudes-modal/convocatoria-configuracion-solicitudes-modal.component';
import { ConvocatoriaConfiguracionSolicitudesFragment } from './convocatoria-configuracion-solicitudes.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const CONVOCATORIA_CONFIGURACION_SOLICITUD_DOCUMENTO_REQUERIDO_KEY = marker('csp.convocatoria-configuracion-solicitud-documento-requerido');
const CONVOCATORIA_CONFIGURACION_SOLICITUD_FASE_PRESENTACION_KEY = marker('csp.convocatoria-configuracion-solicitud.fase-presentacion');

@Component({
  selector: 'sgi-convocatoria-configuracion-solicitudes',
  templateUrl: './convocatoria-configuracion-solicitudes.component.html',
  styleUrls: ['./convocatoria-configuracion-solicitudes.component.scss']
})
export class ConvocatoriaConfiguracionSolicitudesComponent
  extends FormFragmentComponent<IConfiguracionSolicitud> implements OnInit, OnDestroy {
  formPart: ConvocatoriaConfiguracionSolicitudesFragment;
  fxFlexProperties: FxFlexProperties;
  fxFlexPropertiesOne: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexPropertiesInline: FxFlexProperties;
  fxFlexPropertiesEntidad: FxFlexProperties;

  get FORMULARIO_SOLICITUD_MAP() {
    return FORMULARIO_SOLICITUD_MAP;
  }

  columns = ['nombre', 'descripcion', 'observaciones', 'acciones'];
  numPage = [5, 10, 25, 100];

  dataSource = new MatTableDataSource<StatusWrapper<IDocumentoRequeridoSolicitud>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  private subscriptions: Subscription[] = [];

  configuracionSolicitud: IConfiguracionSolicitud;

  msgParamDocumentoEntity = {};
  msgParamDocumentoEntities = {};
  msgParamFasePresentacionEntity = {};
  textoDelete: string;

  constructor(
    protected actionService: ConvocatoriaActionService,
    public translate: TranslateService,
    private matDialog: MatDialog,
    private dialogService: DialogService
  ) {
    super(actionService.FRAGMENT.CONFIGURACION_SOLICITUDES, actionService);

    this.formPart = this.fragment as ConvocatoriaConfiguracionSolicitudesFragment;

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(32%-10px)';
    this.fxFlexProperties.order = '1';

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
      (wrapper: StatusWrapper<IDocumentoRequeridoSolicitud>, property: string) => {
        switch (property) {
          case 'nombre':
            return wrapper.value.tipoDocumento.nombre;
          case 'descripcion':
            return wrapper.value.tipoDocumento.descripcion;
          case 'observaciones':
            return wrapper.value.observaciones;
          default:
            return wrapper[property];
        }
      };
    this.dataSource.sort = this.sort;

    this.subscriptions.push(this.formPart.documentosRequeridos$.subscribe(elements => {
      this.dataSource.data = elements;
      if (elements.length > 0) {
        this.formGroup.controls.fasePresentacionSolicitudes.disable();
      }
      else {
        this.formGroup.controls.fasePresentacionSolicitudes.enable();
      }
    }));
  }

  private setupI18N(): void {
    this.translate.get(
      CONVOCATORIA_CONFIGURACION_SOLICITUD_DOCUMENTO_REQUERIDO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamDocumentoEntity = { entity: value });

    this.translate.get(
      CONVOCATORIA_CONFIGURACION_SOLICITUD_DOCUMENTO_REQUERIDO_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamDocumentoEntities = { entity: value });

    this.translate.get(
      CONVOCATORIA_CONFIGURACION_SOLICITUD_FASE_PRESENTACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFasePresentacionEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      CONVOCATORIA_CONFIGURACION_SOLICITUD_DOCUMENTO_REQUERIDO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoDelete = value);
  }

  displayerConvocatoriaFase(convocatoriaFase: IConvocatoriaFase): string {
    return convocatoriaFase?.tipoFase?.nombre ?? '';
  }

  /**
   * Abre modal con el modelo convocatoria enlace seleccionada
   * @param wrapper convocatoria enlace
   */
  openModal(wrapper?: StatusWrapper<IDocumentoRequeridoSolicitud>): void {
    const tipoFase: ITipoFase = this.formGroup.controls.fasePresentacionSolicitudes.value.tipoFase;
    if (this.configuracionSolicitud) {
      this.configuracionSolicitud.fasePresentacionSolicitudes.tipoFase = tipoFase;
    }
    const documentosRequerido: IDocumentoRequeridoSolicitud = {
      configuracionSolicitudId: this.fragment.getKey() as number,
      id: undefined,
      observaciones: undefined,
      tipoDocumento: undefined,
    };

    const data: ConvocatoriaConfiguracionSolicitudesModalData = {
      documentoRequerido: wrapper ? wrapper.value : documentosRequerido,
      tipoFaseId: this.formGroup.controls.fasePresentacionSolicitudes.value?.tipoFase?.id,
      modeloEjecucionId: this.actionService.modeloEjecucionId,
      isConvocatoriaVinculada: this.formPart.isConvocatoriaVinculada
    };
    const config = {
      data
    };
    const dialogRef = this.matDialog.open(ConvocatoriaConfiguracionSolicitudesModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (result: ConvocatoriaConfiguracionSolicitudesModalData) => {
        if (result) {
          if (wrapper) {
            if (!wrapper.created) {
              wrapper.setEdited();
            }
            this.formPart.setChanges(true);
          } else {
            this.formPart.addDocumentoRequerido(result.documentoRequerido);
          }
        }
      }
    );
  }

  /**
   * Desactivar documento
   */
  deactivateDocumento(wrapper: StatusWrapper<IDocumentoRequeridoSolicitud>) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteDocumentoRequerido(wrapper);
          }
        }
      )
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }
}
