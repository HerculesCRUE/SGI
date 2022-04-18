import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { SgiError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { IInvencionDocumento } from '@core/models/pii/invencion-documento';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { DialogService } from '@core/services/dialog.service';
import { InvencionDocumentoService } from '@core/services/pii/invencion/invencion-documento/invencion-documento.service';
import { DocumentoService, triggerDownloadToUser } from '@core/services/sgdoc/documento.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { DateTime } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';
import { switchMap, tap } from 'rxjs/operators';
import { InvencionActionService } from '../../invencion.action.service';
import { InvencionDocumentoModalComponent } from '../../modals/invencion-documento-modal/invencion-documento-modal.component';
import { InvencionDocumentoFragment } from './invencion-documento.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const INVENCION_DOCUMENTO_KEY = marker('pii.invencion-documento');
const MSG_ERROR = marker('error.load');
const MSG_CREATE = marker('btn.add.entity');
const MSG_SAVE_SUCCESS = marker('msg.save.entity.success');
const MSG_SAVE_ERROR = marker('error.save.entity');
const MSG_UPDATE_ERROR = marker('error.update.entity');
const MSG_UPDATE_SUCCESS = marker('msg.update.entity.success');
const MSG_DOWNLOAD_ERROR = marker('error.file.download');
const DOCUMENTO_COMUNICACION_KEY = marker('pii.invencion.documentacion-comunicacion');

@Component({
  selector: 'sgi-invencion-documento',
  templateUrl: './invencion-documento.component.html',
  styleUrls: ['./invencion-documento.component.scss']
})
export class InvencionDocumentoComponent extends FragmentComponent implements OnInit, OnDestroy {

  public ROUTE_NAMES = ROUTE_NAMES;

  private subscriptions: Subscription[] = [];
  public formPart: InvencionDocumentoFragment;

  public fxFlexProperties: FxFlexProperties;
  public fxLayoutProperties: FxLayoutProperties;

  public displayedColumns = ['fechaAnadido', 'nombre', 'fichero', 'acciones'];

  public msgParamEntity = {};
  public textoDelete: string;
  public txtCrear: string;
  public txtCrearSuccess: string;
  public txtCrearError: string;
  public txtUpdateSuccess: string;
  public txtUpdateError: string;

  public elementosPagina: number[];
  public totalElementos: number;

  public dataSource = new MatTableDataSource<StatusWrapper<IInvencionDocumento>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  showDocumentosErrorMsg: boolean;
  msgParamDocumentoComunicacionEntity = {};

  constructor(
    public actionService: InvencionActionService,
    private snackBarService: SnackBarService,
    private translate: TranslateService,
    private matDialog: MatDialog,
    private invencionDocumentoService: InvencionDocumentoService,
    private logger: NGXLogger,
    private documentoService: DocumentoService,
    private dialogService: DialogService
  ) {

    super(actionService.FRAGMENT.DOCUMENTOS, actionService);
    this.formPart = this.fragment as InvencionDocumentoFragment;
    this.elementosPagina = [5, 10, 25, 100];
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.subscriptions.push(this.subscribeToInvencionDocumentosChanges());
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  private setupI18N(): void {

    this.translate.get(
      INVENCION_DOCUMENTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      INVENCION_DOCUMENTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoDelete = value);

    this.translate.get(
      INVENCION_DOCUMENTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_CREATE,
          { entity: value, ...this.msgParamEntity }
        );
      })
    ).subscribe((value) => this.txtCrear = value);

    this.translate.get(
      INVENCION_DOCUMENTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SAVE_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.txtCrearSuccess = value);

    this.translate.get(
      INVENCION_DOCUMENTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SAVE_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.txtCrearError = value);

    this.translate.get(
      INVENCION_DOCUMENTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_UPDATE_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.txtUpdateSuccess = value);

    this.translate.get(
      INVENCION_DOCUMENTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_UPDATE_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.txtUpdateError = value);

    this.translate.get(
      DOCUMENTO_COMUNICACION_KEY
    ).subscribe((value) => {
      this.msgParamDocumentoComunicacionEntity = {
        entity: value, ...MSG_PARAMS.GENDER.FEMALE
      };
    });
  }

  public deleteInvencionDocumento(wrapper: StatusWrapper<IInvencionDocumento>, rowIndex?: number): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            const row = this.resolveTableRowIndexMatchingWithDataSource(rowIndex);
            this.formPart.deleteInvencionDocumento(wrapper, row);
          }
        }, (error) => {
          if (error instanceof SgiError) {
            this.snackBarService.showError(error);
          } else {
            this.snackBarService.showError(MSG_ERROR);
          }
        }
      )
    );
  }

  /**
   * Abre un modal para añadir o actualizar un Documento
   * @param invencionDocumento InvencionDocumento
   */
  openModal(invencionDocumento?: IInvencionDocumento, rowIndex?: number): void {

    const row = this.resolveTableRowIndexMatchingWithDataSource(rowIndex);

    const config = {
      panelClass: 'sgi-dialog-container',
      data: invencionDocumento
    };
    const dialogRef = this.matDialog.open(InvencionDocumentoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (result: IInvencionDocumento) => {
        if (result) {
          const savedDocumento: IInvencionDocumento = result.id ? result
            : { ...result, invencionId: this.actionService.id, fechaAnadido: DateTime.now() };
          this.formPart.updateTable(savedDocumento, invencionDocumento, row);
          this.formPart.setChanges(true);
        }
      });
  }

  private resolveTableRowIndexMatchingWithDataSource(rowIndex: number) {
    this.dataSource.sortData(this.dataSource.filteredData, this.dataSource.sort);
    return (this.paginator.pageSize * this.paginator.pageIndex) + rowIndex;
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

  private subscribeToInvencionDocumentosChanges(): Subscription {
    return this.formPart.invencionDocumentos$.subscribe(
      (invencionDocumentos) => {
        this.dataSource.data = invencionDocumentos;
        this.showDocumentosErrorMsg = invencionDocumentos.length === 0;
        if (this.showDocumentosErrorMsg) {
          setTimeout(() =>
            this.formPart.setChanges(false));
        }
      }
    );
  }

}
