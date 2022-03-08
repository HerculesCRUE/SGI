import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IAutorizacion } from '@core/models/csp/autorizacion';
import { ICertificadoAutorizacion } from '@core/models/csp/certificado-autorizacion';
import { Estado } from '@core/models/csp/estado-autorizacion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { DialogService } from '@core/services/dialog.service';
import { DocumentoService, triggerDownloadToUser } from '@core/services/sgdoc/documento.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { AutorizacionActionService } from '../../autorizacion.action.service';
import { AutorizacionCertificadoModalComponent, ICertificadoAutorizacionModalData } from '../autorizacion-certificado-modal/autorizacion-certificado-modal.component';
import { AutorizacionCertificadosFragment, CertificadoAutorizacionListado } from './autorizacion-certificados.fragment';

const MSG_DOWNLOAD_ERROR = marker('error.file.download');
const MSG_DELETE = marker('msg.delete.entity');
const CERTIFICADO_KEY = marker('csp.certificado-autorizacion');

@Component({
  selector: 'sgi-autorizacion-certificados',
  templateUrl: './autorizacion-certificados.component.html',
  styleUrls: ['./autorizacion-certificados.component.scss']
})
export class AutorizacionCertificadosComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  formPart: AutorizacionCertificadosFragment;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns = ['nombre', 'publico', 'acciones'];
  elementosPagina = [5, 10, 25, 100];

  dataSource = new MatTableDataSource<StatusWrapper<CertificadoAutorizacionListado>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  msgParamEntity = {};
  textoDelete: string;
  textoCrear: string;

  get Estado() {
    return Estado;
  }

  constructor(
    public actionService: AutorizacionActionService,
    private readonly translate: TranslateService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private documentoService: DocumentoService,
    private snackBar: SnackBarService
  ) {
    super(actionService.FRAGMENT.CERTIFICADOS, actionService);
    this.formPart = this.fragment as AutorizacionCertificadosFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<CertificadoAutorizacionListado>, property: string) => {
        switch (property) {
          case 'nombre':
            return wrapper.value.certificado.nombre;
          case 'publico':
            return wrapper.value.certificado.visible;
          default:
            return wrapper[property];
        }
      };
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.certificadosAutorizacion$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  private setupI18N(): void {
    this.translate.get(
      CERTIFICADO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      CERTIFICADO_KEY,
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
   * Apertura de modal de Cetificados Autorizacion
   */
  openModal(value?: StatusWrapper<CertificadoAutorizacionListado>): void {
    const data = {
      certificado: value?.value.certificado ??
        {
          autorizacion: {
            id: this.formPart.getKey()
          } as IAutorizacion
        } as ICertificadoAutorizacion,
      hasSomeOtherCertificadoAutorizacionVisible: this.formPart.certificadosAutorizacion$.value.some(certificado =>
        certificado.value.certificado.visible && !value?.value?.certificado.visible),
      generadoAutomatico: value?.value.generadoAutomatico,
    } as ICertificadoAutorizacionModalData;
    const config = {
      panelClass: 'sgi-dialog-container',
      data
    };
    const dialogRef = this.matDialog.open(AutorizacionCertificadoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (data: ICertificadoAutorizacionModalData) => {
        if (data) {
          const certificadoAutorizacionListado = {
            certificado: data.certificado,
            generadoAutomatico: data.generadoAutomatico
          } as CertificadoAutorizacionListado;
          if (data.certificado.id) {
            this.formPart.updateCertificado(new StatusWrapper<CertificadoAutorizacionListado>(certificadoAutorizacionListado));
          } else if (value) {
            this.formPart.createCertificado(new StatusWrapper<CertificadoAutorizacionListado>(certificadoAutorizacionListado));
          } else {
            this.formPart.addCertificado(certificadoAutorizacionListado);
          }
        }
      }
    );
  }

  downloadFile(value: ICertificadoAutorizacion): void {
    this.subscriptions.push(this.documentoService.downloadFichero(value.documento.documentoRef).subscribe(
      (data) => {
        triggerDownloadToUser(data, value.documento.nombre);
      },
      () => {
        this.snackBar.showError(MSG_DOWNLOAD_ERROR);
      }
    ));
  }

  deleteCertificado(wrapper: StatusWrapper<CertificadoAutorizacionListado>): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteCertificado(wrapper);
          }
        }
      )
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

}
