import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IDocumentacionConvocatoriaReunion } from '@core/models/eti/documentacion-convocatoria-reunion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { DialogService } from '@core/services/dialog.service';
import { DocumentoService, triggerDownloadToUser } from '@core/services/sgdoc/documento.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Observable, Subscription, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { ConvocatoriaReunionActionService } from '../../convocatoria-reunion.action.service';
import { ConvocatoriaReunionDocumentacionConvocatoriaReunionModalComponent, ConvocatoriaReunionDocumentacionConvocatoriaReunionModalData } from '../../modals/convocatoria-reunion-documentacion-convocatoria-reunion-modal/convocatoria-reunion-documentacion-convocatoria-reunion-modal.component';
import { ConvocatoriaReunionDocumentacionFragment } from './convocatoria-reunion-documentacion.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const DOCUMENTACION_KEY = marker('eti.documentacion');
const DOCUMENTO_KEY = marker('eti.convocatoria-reunion.documento');

@Component({
  selector: 'sgi-convocatoria-reunion-documentacion',
  templateUrl: './convocatoria-reunion-documentacion.component.html',
  styleUrls: ['./convocatoria-reunion-documentacion.component.scss']
})
export class ConvocatoriaReunionDocumentacionComponent extends FragmentComponent implements OnInit, OnDestroy {

  private formPart: ConvocatoriaReunionDocumentacionFragment;

  private subscriptions: Subscription[] = [];

  readonly dataSourceDocumentoConvocatoriaReunion = new MatTableDataSource<StatusWrapper<IDocumentacionConvocatoriaReunion>>();
  readonly dataSourceSeguimientoAnual = new MatTableDataSource<StatusWrapper<IDocumentacionConvocatoriaReunion>>();
  readonly dataSourceSeguimientoFinal = new MatTableDataSource<StatusWrapper<IDocumentacionConvocatoriaReunion>>();
  readonly dataSourceRetrospectiva = new MatTableDataSource<StatusWrapper<IDocumentacionConvocatoriaReunion>>();

  @ViewChild('paginatorDocumentacionConvocatoriaReunion', { static: true }) private paginatorDocumentacionConvocatoriaReunion: MatPaginator;

  @ViewChild('sortDocumentacionConvocatoriaReunion', { static: true }) private sortDocumentacionConvocatoriaReunion: MatSort;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumnsDocumentoConvocatoriaReunion: string[] = ['nombre', 'fichero', 'acciones'];
  elementosPaginaDocumentoConvocatoriaReunion: number[] = [5, 10, 25, 100];

  msgParamDocumentoEntity = {};
  private textoDelete: string;

  private sortingDataAccesor = (wrapper: StatusWrapper<IDocumentacionConvocatoriaReunion>, property: string) => {
    switch (property) {
      case 'nombre':
        return wrapper.value?.nombre;
      default:
        return wrapper.value[property];
    }
  }

  get isReadonly(): boolean {
    return this.formPart.isReadonly;
  }

  constructor(
    private readonly dialogService: DialogService,
    private matDialog: MatDialog,
    private actionService: ConvocatoriaReunionActionService,
    private readonly documentoService: DocumentoService,
    private readonly translate: TranslateService) {

    super(actionService.FRAGMENT.DOCUMENTACION, actionService);

    this.formPart = this.fragment as ConvocatoriaReunionDocumentacionFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.dataSourceDocumentoConvocatoriaReunion.paginator = this.paginatorDocumentacionConvocatoriaReunion;
    this.dataSourceDocumentoConvocatoriaReunion.sort = this.sortDocumentacionConvocatoriaReunion;
    this.subscriptions.push(this.formPart.documentacionesConvocatoriaReunion$.subscribe(elements => {
      this.dataSourceDocumentoConvocatoriaReunion.data = elements;
    }));

    this.dataSourceDocumentoConvocatoriaReunion.sortingDataAccessor = this.sortingDataAccesor;
  }

  private setupI18N(): void {
    this.translate.get(
      DOCUMENTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamDocumentoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      DOCUMENTACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoDelete = value);
  }

  openModalDocumentacion(wrapper?: StatusWrapper<IDocumentacionConvocatoriaReunion>): void {
    const data: ConvocatoriaReunionDocumentacionConvocatoriaReunionModalData = {
      convocatoriaReunionId: this.fragment.getKey() as number,
      readonly: this.formPart.isReadonly,
      documentacion: wrapper?.value,
      nuevo: !wrapper
    };
    const config = {
      data
    };

    const dialogRef = this.matDialog.open(ConvocatoriaReunionDocumentacionConvocatoriaReunionModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (documentacion: IDocumentacionConvocatoriaReunion) => {
        if (documentacion) {
          if (wrapper) {
            this.formPart.updateDocumento(documentacion, wrapper);
          } else {
            this.formPart.addDocumento(documentacion);
          }
        }
      });
  }

  deleteDocumentacion(wrappedDocumentacion: StatusWrapper<IDocumentacionConvocatoriaReunion>): void {
    const dialogSubscription = this.dialogService.showConfirmation(
      this.textoDelete
    ).pipe(switchMap((accept) => {
      if (accept) {
        return of(this.formPart.deleteDocumento(wrappedDocumentacion));
      }
      return of();
    })).subscribe();

    this.subscriptions.push(dialogSubscription);
  }

  ngOnDestroy(): void {
    this.subscriptions?.forEach(x => x.unsubscribe());
  }

  downloadDocumentacion(documentacionConvocatoriaReunion: IDocumentacionConvocatoriaReunion) {
    let action$: Observable<Blob>;
    if (documentacionConvocatoriaReunion.documento.nombre) {
      action$ = this.documentoService.downloadFichero(documentacionConvocatoriaReunion.documento.documentoRef);
    }
    else {
      action$ = this.documentoService.getInfoFichero(documentacionConvocatoriaReunion.documento.documentoRef).pipe(
        switchMap(documento => {
          documentacionConvocatoriaReunion.documento = documento;
          return this.documentoService.downloadFichero(documento.documentoRef);
        })
      );
    }
    this.subscriptions.push(action$.subscribe(
      (response) => triggerDownloadToUser(response, documentacionConvocatoriaReunion.documento.nombre)
    ));
  }

}
