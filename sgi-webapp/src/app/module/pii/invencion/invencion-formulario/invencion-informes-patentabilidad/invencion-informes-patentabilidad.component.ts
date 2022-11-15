import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IInformePatentabilidad } from '@core/models/pii/informe-patentabilidad';
import { DialogService } from '@core/services/dialog.service';
import { DocumentoService, triggerDownloadToUser } from '@core/services/sgdoc/documento.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { InvencionActionService } from '../../invencion.action.service';
import { IInformePatentabilidadModalData, InformePatentabilidadModalComponent } from '../../modals/informe-patentabilidad-modal/informe-patentabilidad-modal.component';
import { InvencionInformesPatentabilidadFragment } from './invencion-informes-patentabilidad.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const INVENCION_INFORME_PATENTABILIDAD_KEY = marker('pii.invencion-informe-patentabilidad');
const MSG_DOWNLOAD_ERROR = marker('error.file.download');

@Component({
  selector: 'sgi-invencion-informes-patentabilidad',
  templateUrl: './invencion-informes-patentabilidad.component.html',
  styleUrls: ['./invencion-informes-patentabilidad.component.scss']
})
export class InvencionInformesPatentabilidadComponent extends FragmentComponent implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];
  formPart: InvencionInformesPatentabilidadFragment;

  displayedColumns = ['fecha', 'nombre', 'fichero', 'entidadCreadora', 'resultado', 'acciones'];
  elementosPagina = [5, 10, 25, 100];

  dataSource = new MatTableDataSource<StatusWrapper<IInformePatentabilidad>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  msgParamEntity = {};
  textoDelete: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    protected actionService: InvencionActionService,
    private readonly translate: TranslateService,
    private readonly snackBarService: SnackBarService,
    private readonly dialogService: DialogService,
    private readonly matDialog: MatDialog,
    private readonly documentoService: DocumentoService,
    private readonly logger: NGXLogger,
  ) {
    super(actionService.FRAGMENT.INFORME_PATENTABILIDAD, actionService);
    this.formPart = this.fragment as InvencionInformesPatentabilidadFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.initInformesPatentabilidadTable();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  deleteInformePatentabilidad(informePatentabilidad: StatusWrapper<IInformePatentabilidad>): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteInformePatentabilidad(informePatentabilidad);
          }
        }
      )
    );
  }

  openModal(wrapper?: StatusWrapper<IInformePatentabilidad>, rowIndex?: number): void {
    const row = this.resolveTableRowIndexMatchingWithDataSource(rowIndex);
    const data: IInformePatentabilidadModalData = {
      informePatentabilidad: wrapper ? wrapper.value : {} as IInformePatentabilidad,
      readonly: !this.formPart.hasEditPerm()
    };

    const config: MatDialogConfig = {
      data
    };

    const currentDocumentoRef = data.informePatentabilidad?.documento?.documentoRef;

    const dialogRef = this.matDialog.open(InformePatentabilidadModalComponent, config);
    dialogRef.afterClosed().subscribe((informePatentabilidad: IInformePatentabilidad) => {
      if (informePatentabilidad) {
        if (wrapper) {
          this.formPart.updateInformePatentabilidad(informePatentabilidad, currentDocumentoRef, row);
        } else {
          this.formPart.addInformePatentabilidad(informePatentabilidad);
        }
      }
    });
  }

  downloadFile(wrapper: StatusWrapper<IInformePatentabilidad>): void {
    this.subscriptions.push(this.documentoService.downloadFichero(wrapper.value.documento.documentoRef).subscribe(
      (data) => {
        triggerDownloadToUser(data, wrapper.value.documento.nombre);
      },
      (error) => {
        this.logger.error(error);
        this.snackBarService.showError(MSG_DOWNLOAD_ERROR);
      }
    ));
  }

  hasInformePatentabilidadDocumentoReady(informePatentabilidad: IInformePatentabilidad): boolean {
    return typeof informePatentabilidad?.documento?.nombre === 'string';
  }

  private resolveTableRowIndexMatchingWithDataSource(rowIndex: number) {
    this.dataSource.sortData(this.dataSource.filteredData, this.dataSource.sort);
    return (this.paginator.pageSize * this.paginator.pageIndex) + rowIndex;
  }


  private initInformesPatentabilidadTable(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IInformePatentabilidad>, property: string) => {
        switch (property) {
          case 'fecha':
            return wrapper.value.fecha;
          case 'nombre':
            return wrapper.value.nombre;
          case 'fichero':
            return wrapper.value.documento.nombre;
          case 'entidadCreadora':
            return wrapper.value.entidadCreadora.nombre;
          case 'resultado':
            return wrapper.value.resultadoInformePatentabilidad.nombre;
          default:
            return wrapper[property];
        }
      };
    this.dataSource.sort = this.sort;
    this.getInformesPatentabilidadTableData();
  }

  private getInformesPatentabilidadTableData(): void {
    this.subscriptions.push(this.formPart.getInformesPatentabilidad$()
      .subscribe(elements => this.dataSource.data = elements));
  }

  private setupI18N(): void {
    this.translate.get(
      INVENCION_INFORME_PATENTABILIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      INVENCION_INFORME_PATENTABILIDAD_KEY,
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
}
