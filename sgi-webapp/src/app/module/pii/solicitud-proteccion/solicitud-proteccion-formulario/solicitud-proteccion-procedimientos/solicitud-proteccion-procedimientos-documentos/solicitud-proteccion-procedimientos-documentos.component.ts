import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IProcedimientoDocumento } from '@core/models/pii/procedimiento-documento';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { DialogService } from '@core/services/dialog.service';
import { SolicitudProteccionProcedimientoDocumentoService } from '@core/services/pii/solicitud-proteccion/solicitud-proteccion-procedimiento-documento/solicitud-proteccion-procedimiento-documento.service';
import { DocumentoService, triggerDownloadToUser } from '@core/services/sgdoc/documento.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';
import { filter, switchMap } from 'rxjs/operators';
import { SolicitudProteccionProcedimientoDocumentoModalComponent } from '../../../modals/solicitud-proteccion-procedimiento-documento-modal/solicitud-proteccion-procedimiento-documento-modal.component';
import { SolicitudProteccionActionService } from '../../../solicitud-proteccion.action.service';
import { SolicitudProteccionProcedimientosFragment } from '../solicitud-proteccion-procedimientos.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const MSG_DOWNLOAD_ERROR = marker('error.file.download');
const SOLICITUD_PROTECCION_PROCEDIMIENTO_DOCUMENTO_KEY = marker('pii.solicitud-proteccion.procedimiento-documento');

@Component({
  selector: 'sgi-solicitud-proteccion-procedimientos-documentos',
  templateUrl: './solicitud-proteccion-procedimientos-documentos.component.html',
  styleUrls: ['./solicitud-proteccion-procedimientos-documentos.component.scss']
})
export class SolicitudProteccionProcedimientosDocumentosComponent extends FragmentComponent implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];
  formPart: SolicitudProteccionProcedimientosFragment;

  public fxFlexProperties: FxFlexProperties;
  public fxLayoutProperties: FxLayoutProperties;

  public displayedColumns = ['fechaAnadido', 'nombre', 'fichero', 'acciones'];

  public msgParamEntity = {};
  public txtCrear: string;
  public txtCrearSuccess: string;
  public txtCrearError: string;
  public txtUpdateSuccess: string;
  public txtUpdateError: string;
  public msgDelete: string;
  public textoDelete: string;

  public elementosPagina: number[];
  public totalElementos: number;

  public dataSource = new MatTableDataSource<StatusWrapper<IProcedimientoDocumento>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  showDocumentosErrorMsg: boolean;
  msgParamDocumentoComunicacionEntity = {};

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    protected actionService: SolicitudProteccionActionService,
    private snackBarService: SnackBarService,
    private translate: TranslateService,
    private matDialog: MatDialog,
    private solicitudProteccionProcedimientoDocumentoService: SolicitudProteccionProcedimientoDocumentoService,
    private logger: NGXLogger,
    private documentoService: DocumentoService,
    private dialogService: DialogService
  ) {
    super(actionService.FRAGMENT.PROCEDIMIENTOS, actionService);
    this.formPart = this.fragment as SolicitudProteccionProcedimientosFragment;
    this.elementosPagina = [5, 10, 25, 100];
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.procedimientoDocumentos$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  deleteProcedimientoDocumento(wrapper: StatusWrapper<IProcedimientoDocumento>) {
    this.showConfirmationMessage(this.msgDelete, () => {
      this.formPart.deleteProcedimientoDocumento(wrapper);
    });
  }

  private showConfirmationMessage(message: string, callback) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(message)
        .pipe(
          filter(response => !!response)
        )
        .subscribe(
          () => callback()
        )
    );
  }

  private setupI18N(): void {

    this.translate.get(
      SOLICITUD_PROTECCION_PROCEDIMIENTO_DOCUMENTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })).subscribe((value) => this.msgDelete = value);

    this.translate.get(
      SOLICITUD_PROTECCION_PROCEDIMIENTO_DOCUMENTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      SOLICITUD_PROTECCION_PROCEDIMIENTO_DOCUMENTO_KEY,
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

  /**
   * Apertura del modal de {@link IProcedimientoDocumento}
   */
  openModal(procedimientoDocumento: StatusWrapper<IProcedimientoDocumento>): void {
    if (!procedimientoDocumento) {
      procedimientoDocumento = this.formPart.createEmptyProcedimientoDocumento();
    }

    const config = {
      panelClass: 'sgi-dialog-container',
      data: procedimientoDocumento,
    };
    const dialogRef = this.matDialog.open(SolicitudProteccionProcedimientoDocumentoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (result: StatusWrapper<IProcedimientoDocumento>) => {
        if (!result) {
          return;
        }
        if (result.created) {
          this.formPart.addProcedimientoDocumento(result);
        } else {
          this.formPart.editProcedimientoDocumento(result);
        }
      });
  }

  public downloadFile(documentoRef: string, fileName: string): void {
    this.subscriptions.push(this.documentoService.downloadFichero(documentoRef).subscribe(
      (data) => {
        triggerDownloadToUser(data, fileName);
      }, () => {
        this.snackBarService.showError(MSG_DOWNLOAD_ERROR);
      }
    ));
  }

}
