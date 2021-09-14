import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IInvencion } from '@core/models/pii/invencion';
import { IInvencionDocumento } from '@core/models/pii/invencion-documento';
import { IInvencionSectorAplicacion } from '@core/models/pii/invencion-sector-aplicacion';
import { ITipoProteccion } from '@core/models/pii/tipo-proteccion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { DialogService } from '@core/services/dialog.service';
import { TipoProteccionService } from '@core/services/pii/tipo-proteccion/tipo-proteccion.service';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { DateTime } from 'luxon';
import { Observable, Subscription } from 'rxjs';
import { filter, map, mergeMap, switchMap, tap } from 'rxjs/operators';
import { AreaConocimientoDataModal, AreaConocimientoModalComponent } from 'src/app/esb/sgo/shared/area-conocimiento-modal/area-conocimiento-modal.component';
import { InvencionActionService } from '../../invencion.action.service';
import { InvencionDocumentoModalComponent } from '../../modals/invencion-documento-modal/invencion-documento-modal.component';
import { SectorAplicacionModalComponent, SectorAplicacionModalData } from '../../modals/sector-aplicacion-modal/sector-aplicacion-modal.component';
import { IInvencionAreaConocimientoListado, InvencionDatosGeneralesFragment } from './invencion-datos-generales.fragment';

const MSG_DELETE_KEY = marker('msg.delete.entity');
const INVENCION_TITULO_KEY = marker('pii.invencion.titulo');
const INVENCION_FECHACOMUNICACION_KEY = marker('pii.invencion.fecha-comunicacion');
const INVENCION_DESCRIPCION_KEY = marker('pii.invencion.descripcion');
const INVENCION_TIPOPROTECCION_KEY = marker('pii.invencion.tipo-proteccion');
const INVENCION_COMENTARIOS_KEY = marker('pii.invencion.comentarios');
const SECTOR_APLICACION_KEY = marker('pii.sector-aplicacion');
const AREA_PROCEDENCIA_KEY = marker('pii.invencion.area-procedencia');
const DOCUMENTO_COMUNICACION_KEY = marker('pii.invencion.documentacion-comunicacion');
const MSG_DELETE_FILE_ERROR = marker('error.file.delete');

@Component({
  selector: 'sgi-invencion-datos-generales',
  templateUrl: './invencion-datos-generales.component.html',
  styleUrls: ['./invencion-datos-generales.component.scss']
})
export class InvencionDatosGeneralesComponent extends FormFragmentComponent<IInvencion> implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];
  formPart: InvencionDatosGeneralesFragment;
  fxFlexPropertiesInline: FxFlexProperties;
  fxFlexProperties33: FxFlexProperties;
  fxFlexProperties66: FxFlexProperties;
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  msgParamTituloEntity = {};
  msgParamFechaComunicacionEntity = {};
  msgParamDescripcionEntity = {};
  msgParamTipoProteccionEntity = {};
  msgParamComentariosEntity = {};
  msgParamSectorAplicacionEntity = {};
  msgParamAreaConocimientoEntity = {};
  msgParamDocumentoComunicacionEntity = {};
  textoDeleteSectorAplicacion: string;
  textoDeleteAreaConocimiento: string;
  textoDeleteDocumento: string;
  showSectorAplicacionErrorMsg: boolean;
  showAreaConocimientoErrorMsg: boolean;
  showDocumentosErrorMsg: boolean;

  sectorAplicacionDataSource = new MatTableDataSource<StatusWrapper<IInvencionSectorAplicacion>>();
  displayedColumnsSectorAplicacion = ['sector', 'acciones'];
  @ViewChild('sortSectorAplicacion', { static: true }) sortSectorAplicacion: MatSort;

  areaConocimientoDataSource = new MatTableDataSource<StatusWrapper<IInvencionAreaConocimientoListado>>();
  displayedColumnsAreaConocimiento = ['niveles', 'nivelSeleccionado', 'acciones'];
  @ViewChild('sortAreaConocimiento', { static: true }) sortAreaConocimiento: MatSort;

  documentoDataSource = new MatTableDataSource<StatusWrapper<IInvencionDocumento>>();
  displayedColumnsDocumento = ['fechaAnadido', 'nombre', 'fichero', 'acciones'];
  @ViewChild('sortDocumento', { static: true }) sortDocumento: MatSort;

  readonly tiposProteccion$: Observable<ITipoProteccion[]>;
  subtiposProteccion$: Observable<ITipoProteccion[]>;

  get isEdit() {
    return this.formPart.isEdit();
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    readonly actionService: InvencionActionService,
    private readonly translate: TranslateService,
    private readonly tipoProteccionService: TipoProteccionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly documentoService: DocumentoService,
    private readonly snackBar: SnackBarService,
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.formPart = this.fragment as InvencionDatosGeneralesFragment;
    this.initFlexProperties();
    this.tiposProteccion$ = this.tipoProteccionService.findAll().pipe(map(({ items }) => items));
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.configSectorAplicacionSort();
    this.configAreaConocimientoSort();
    this.configDocumentoSort()
    this.subscribeToSectoresAplicacion();
    this.subscribeToAreasConocimiento();
    this.subscribeToDocumentos();
    this.subtiposProteccion$ = this.formGroup.controls.tipoProteccion.valueChanges.pipe(
      tap(_ => this.resetSubtipoProteccionControl()),
      switchMap(
        ({ id }) => this.tipoProteccionService.findSubtipos(id).pipe(
          map(({ items }) => items)
        )));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  private subscribeToSectoresAplicacion(): void {
    this.subscriptions.push(this.formPart.getSectoresAplicacion$()
      .pipe(
        tap(elements => this.showSectorAplicacionErrorMsg = elements.length === 0)
      )
      .subscribe(elements => {
        this.sectorAplicacionDataSource.data = elements;
      }));
  }

  private subscribeToAreasConocimiento(): void {
    this.subscriptions.push(this.formPart.getAreasConocimiento$()
      .pipe(
        tap(elements => this.showAreaConocimientoErrorMsg = elements.length === 0)
      )
      .subscribe(elements => {
        this.areaConocimientoDataSource.data = elements;
      }));
  }

  private subscribeToDocumentos(): void {
    this.subscriptions.push(this.formPart.getDocumentos$()
      .pipe(
        tap(elements => this.showDocumentosErrorMsg = elements.length === 0)
      )
      .subscribe(elements => {
        this.documentoDataSource.data = elements;
      }));
  }

  private resetSubtipoProteccionControl(): void {
    this.formGroup.controls.subtipoProteccion.reset();
  }

  private configSectorAplicacionSort(): void {
    this.sectorAplicacionDataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IInvencionSectorAplicacion>, property: string) => {
        switch (property) {
          case 'sector':
            return wrapper.value.sectorAplicacion.nombre;
          default:
            return wrapper.value[property];
        }
      };
    this.sectorAplicacionDataSource.sort = this.sortSectorAplicacion;
  }

  private configAreaConocimientoSort(): void {
    this.areaConocimientoDataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IInvencionAreaConocimientoListado>, property: string) => {
        switch (property) {
          case 'niveles':
            return wrapper.value.nivelesTexto;
          case 'nivelSeleccionado':
            return wrapper.value.nivelSeleccionado.nombre;
          default:
            return wrapper[property];
        }
      };
    this.areaConocimientoDataSource.sort = this.sortAreaConocimiento;
  }

  private configDocumentoSort(): void {
    this.documentoDataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IInvencionDocumento>, property: string) => {
        switch (property) {
          case 'fechaAnadido':
            return wrapper.value.fechaAnadido;
          case 'nombre':
            return wrapper.value.nombre;
          case 'fichero':
            return wrapper.value.documento.nombre;
          default:
            return wrapper[property];
        }
      };
    this.documentoDataSource.sort = this.sortAreaConocimiento;
  }

  /**
   * Desasociar un sector de aplicación de la inveción
   * @param wrapper 
   */
  deleteSectorAplicacion(wrapper: StatusWrapper<IInvencionSectorAplicacion>) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDeleteSectorAplicacion).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteSectorAplicacion(wrapper);
          }
        }
      )
    );
  }

  /**
   * Apertura de modal de sector aplicación
   */
  openModalSectorAplicacion(): void {
    const data: SectorAplicacionModalData = {
      selectedEntidades: this.sectorAplicacionDataSource.data.map(element => element.value.sectorAplicacion),
    };

    const config = {
      panelClass: 'sgi-dialog-container',
      data
    };
    const dialogRef = this.matDialog.open(SectorAplicacionModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (sectorAplicacionSeleccionado) => {
        if (sectorAplicacionSeleccionado) {
          this.formPart.addSectorAplicacion(sectorAplicacionSeleccionado);
        }
      }
    );
  }

  /**
 * Desasociar el área de conocimiento
 *
 * @param wrapper el área
 */
  deleteAreaConocimiento(wrapper: StatusWrapper<IInvencionAreaConocimientoListado>): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDeleteAreaConocimiento).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteAreaConocimiento(wrapper);
          }
        }
      )
    );
  }

  /**
   * Apertura de modal de Areas Conocimiento 
   */
  openModalAreaConocimiento(): void {
    const data: AreaConocimientoDataModal = {
      selectedAreasConocimiento: this.areaConocimientoDataSource.data.map(wrapper => wrapper.value.nivelSeleccionado),
      multiSelect: true
    };
    const config = {
      panelClass: 'sgi-dialog-container',
      data
    };
    const dialogRef = this.matDialog.open(AreaConocimientoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (areas) => {
        if (areas && areas.length > 0) {
          this.formPart.addAreaConocimiento(areas);
        }
      }
    );
  }

  /**
   * Eliminar Documento de la Invencion
   * @param wrapper el documento
   */
  deleteDocumento(wrapper: StatusWrapper<IInvencionDocumento>): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDeleteDocumento)
        .pipe(
          filter(aceptado => aceptado),
          mergeMap(() => this.documentoService.eliminarFichero(wrapper.value.documento.documentoRef))
        ).subscribe(
          () => {
            this.formPart.deleteDocumento(wrapper);
          },
          () => this.snackBar.showError(MSG_DELETE_FILE_ERROR)
        )
    );
  }

  /**
   * Apertura de modal de Documentos
   */
  openModalDocumento(): void {
    const config = {
      panelClass: 'sgi-dialog-container',
    };
    const dialogRef = this.matDialog.open(InvencionDocumentoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (result: IInvencionDocumento) => {
        if (result) {
          result.fechaAnadido = DateTime.now();
          this.formPart.addDocumento(result);
        }
      }
    );
  }

  private setupI18N(): void {
    this.translate.get(
      INVENCION_TITULO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTituloEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      INVENCION_FECHACOMUNICACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaComunicacionEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      INVENCION_DESCRIPCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamDescripcionEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      INVENCION_TIPOPROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTipoProteccionEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      INVENCION_COMENTARIOS_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamComentariosEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });

    this.translate.get(
      SECTOR_APLICACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe(
      (value) => {
        this.msgParamSectorAplicacionEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR };
        this.translate.get(
          MSG_DELETE_KEY,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        ).subscribe((valueDelete) => this.textoDeleteSectorAplicacion = valueDelete);
      }
    );

    this.translate.get(
      AREA_PROCEDENCIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe(
      (value) => {
        this.msgParamAreaConocimientoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR };
        this.translate.get(
          MSG_DELETE_KEY,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        ).subscribe((valueDelete) => this.textoDeleteAreaConocimiento = valueDelete);
      }
    );

    this.translate.get(
      DOCUMENTO_COMUNICACION_KEY
    ).subscribe((value) => {
      this.msgParamDocumentoComunicacionEntity = {
        entity: value, ...MSG_PARAMS.GENDER.FEMALE
      };
      this.translate.get(
        MSG_DELETE_KEY,
        { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
      ).subscribe((valueDelete) => this.textoDeleteDocumento = valueDelete);
    });
  }

  private initFlexProperties() {
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.xs = 'column';

    this.fxFlexProperties33 = new FxFlexProperties();
    this.fxFlexProperties33.sm = '0 1 calc(33%-10px)';
    this.fxFlexProperties33.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties33.gtMd = '0 1 calc(33%-10px)';
    this.fxFlexProperties33.order = '2';

    this.fxFlexProperties66 = new FxFlexProperties();
    this.fxFlexProperties66.sm = '0 1 calc(66%-10px)';
    this.fxFlexProperties66.md = '0 1 calc(66%-10px)';
    this.fxFlexProperties66.gtMd = '0 1 calc(66%-10px)';
    this.fxFlexProperties66.order = '1';
  }
}
