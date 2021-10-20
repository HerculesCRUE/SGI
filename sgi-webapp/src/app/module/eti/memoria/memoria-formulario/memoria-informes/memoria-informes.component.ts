import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IInforme } from '@core/models/eti/informe';
import { TIPO_EVALUACION } from '@core/models/eti/tipo-evaluacion';
import { IDocumento } from '@core/models/sgdoc/documento';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { DialogService } from '@core/services/dialog.service';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { DocumentoService, triggerDownloadToUser } from '@core/services/sgdoc/documento.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { MemoriaActionService } from '../../memoria.action.service';
import { MemoriaInformesFragment } from './memoria-informes.fragment';

@Component({
  selector: 'sgi-memoria-informes',
  templateUrl: './memoria-informes.component.html',
  styleUrls: ['./memoria-informes.component.scss']
})
export class MemoriaInformesComponent extends FragmentComponent implements OnInit, OnDestroy {

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns: string[] = ['version', 'acciones'];
  elementosPagina: number[] = [5, 10, 25, 100];

  dataSourceInforme: MatTableDataSource<StatusWrapper<IInforme>> = new MatTableDataSource<StatusWrapper<IInforme>>();

  private formPart: MemoriaInformesFragment;
  private subscriptions: Subscription[] = [];

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    protected readonly dialogService: DialogService,
    protected matDialog: MatDialog,
    protected memoriaService: MemoriaService,
    protected documentoService: DocumentoService,
    actionService: MemoriaActionService
  ) {
    super(actionService.FRAGMENT.VERSIONES, actionService);
    this.formPart = this.fragment as MemoriaInformesFragment;

  }

  ngOnInit(): void {
    super.ngOnInit();
    this.dataSourceInforme = new MatTableDataSource<StatusWrapper<IInforme>>();
    this.dataSourceInforme.paginator = this.paginator;
    this.dataSourceInforme.sort = this.sort;
    this.subscriptions.push(this.formPart?.informes$.subscribe(elements => {
      this.dataSourceInforme.data = elements;
    }));

    this.dataSourceInforme.sortingDataAccessor =
      (wrapper: StatusWrapper<IInforme>, property: string) => {
        switch (property) {
          case 'version':
            return wrapper.value.memoria?.numReferencia + '_v' + wrapper.value.version;
          default:
            return wrapper.value[property];
        }
      };
  }

  b64toBlob(b64Data, contentType = '', sliceSize = 512): Blob {
    const byteCharacters = atob(atob(b64Data));
    const byteArrays = [];

    for (let offset = 0; offset < byteCharacters.length; offset += sliceSize) {
      const slice = byteCharacters.slice(offset, offset + sliceSize);

      const byteNumbers = new Array(slice.length);
      for (let i = 0; i < slice.length; i++) {
        byteNumbers[i] = slice.charCodeAt(i);
      }

      const byteArray = new Uint8Array(byteNumbers);
      byteArrays.push(byteArray);
    }

    const blob = new Blob(byteArrays, { type: contentType });
    return blob;
  }

  downloadFile(documento: IDocumento): void {
    // TODO Arreglar la ñapa
    const downloadLink = document.createElement('a');

    const archivo = documento.archivo;
    const file = this.b64toBlob(archivo, documento.tipo);

    const href = window.URL.createObjectURL(file);
    downloadLink.href = href;
    downloadLink.download = documento.nombre;
    document.body.appendChild(downloadLink);
    downloadLink.click();
    document.body.removeChild(downloadLink);
    window.URL.revokeObjectURL(href);
  }


  /**
   * Visualiza el informe seleccionado.
   * @param documentoRef Referencia del informe..
   */
  visualizarInforme(documentoRef: string) {
    this.documentoService.getInfoFichero(documentoRef).subscribe(documento => {
      this.downloadFile(documento);
    });
  }

  getVersion(informe: IInforme) {
    switch (informe.tipoEvaluacion.id) {
      case TIPO_EVALUACION.MEMORIA:
        return informe.memoria?.numReferencia + '_v' + informe.version;
      case TIPO_EVALUACION.RETROSPECTIVA:
        return informe.memoria?.numReferencia + '_R_v' + informe.version;
      case TIPO_EVALUACION.SEGUIMIENTO_ANUAL:
        return informe.memoria?.numReferencia + '_SA_v' + informe.version;
      case TIPO_EVALUACION.SEGUIMIENTO_FINAL:
        return informe.memoria?.numReferencia + '_SF_v' + informe.version;
      default:
        return '';
    }
  }

  ngOnDestroy(): void {
    this.subscriptions?.forEach(x => x.unsubscribe());
  }
}
