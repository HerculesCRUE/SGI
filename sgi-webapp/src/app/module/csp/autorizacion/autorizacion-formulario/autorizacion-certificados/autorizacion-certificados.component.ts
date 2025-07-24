import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { I18nFieldValue } from '@core/i18n/i18n-field';
import { IAutorizacion } from '@core/models/csp/autorizacion';
import { ICertificadoAutorizacion } from '@core/models/csp/certificado-autorizacion';
import { Estado } from '@core/models/csp/estado-autorizacion';
import { IDocumento } from '@core/models/sgdoc/documento';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { DialogService } from '@core/services/dialog.service';
import { LanguageService } from '@core/services/language.service';
import { DocumentoService, triggerDownloadToUser } from '@core/services/sgdoc/documento.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Observable, of, Subscription } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
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
    private snackBar: SnackBarService,
    private readonly languageService: LanguageService
  ) {
    super(actionService.FRAGMENT.CERTIFICADOS, actionService, translate);
    this.formPart = this.fragment as AutorizacionCertificadosFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<CertificadoAutorizacionListado>, property: string) => {
        switch (property) {
          case 'nombre':
            return this.languageService.getFieldValue(wrapper.value.certificado.nombre);
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

  protected setupI18N(): void {
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
  openModal(wrapper?: StatusWrapper<CertificadoAutorizacionListado>): void {
    const currentDocumentoRef = this.languageService.getFieldValue(wrapper?.value.certificado.documentoRef);
    const documento$: Observable<IDocumento> = currentDocumentoRef ? this.documentoService.getInfoFichero(currentDocumentoRef).pipe(catchError(_ => of(null))) : of(null);

    this.subscriptions.push(
      documento$.pipe(
        switchMap(documento => {
          const modalData: ICertificadoAutorizacionModalData = {
            certificado: wrapper?.value.certificado ??
              {
                autorizacion: {
                  id: this.formPart.getKey()
                } as IAutorizacion
              } as ICertificadoAutorizacion,
            hasSomeOtherCertificadoAutorizacionVisible: this.formPart.certificadosAutorizacion$.value.some(certificado =>
              certificado.value.certificado.visible && !wrapper?.value?.certificado.visible),
            generadoAutomatico: wrapper?.value.generadoAutomatico,
            fechaSolicitud: this.actionService.autorizacionData?.fechaFirstEstado,
            solicitante: this.actionService.autorizacionData?.solicitante,
            documento
          };

          const config = {
            data: modalData
          };

          return this.matDialog.open(AutorizacionCertificadoModalComponent, config).afterClosed();
        })
      ).subscribe(
        (data: ICertificadoAutorizacionModalData) => {
          if (data) {
            const certificadoAutorizacionListado = {
              certificado: data.certificado,
              generadoAutomatico: data.generadoAutomatico
            } as CertificadoAutorizacionListado;
            if (wrapper) {
              wrapper.value.certificado = certificadoAutorizacionListado.certificado;
              wrapper.value.generadoAutomatico = certificadoAutorizacionListado.generadoAutomatico;
              this.formPart.updateCertificado(wrapper, currentDocumentoRef);
            } else {
              this.formPart.addCertificado(certificadoAutorizacionListado);
            }
          }
        }
      )
    )
  }

  downloadFile(documentosRefs: I18nFieldValue[]): void {
    const documento: IDocumento = {
      documentoRef: this.languageService.getFieldValue(documentosRefs)
    } as IDocumento;

    this.subscriptions.push(
      this.documentoService.getInfoFichero(documento.documentoRef).pipe(
        switchMap((documentoInfo: IDocumento) => {
          documento.nombre = documentoInfo.nombre;
          return this.documentoService.downloadFichero(documento.documentoRef);
        }),
      ).subscribe(
        (response) => triggerDownloadToUser(response, documento.nombre),
        (error) => this.snackBar.showError(MSG_DOWNLOAD_ERROR)
      )
    );
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
