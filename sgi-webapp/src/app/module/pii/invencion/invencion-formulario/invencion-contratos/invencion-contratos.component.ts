import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DIALOG_BUTTON_STYLE } from '@block/dialog/dialog.component';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { ISectorLicenciado } from '@core/models/pii/sector-licenciado';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { IPersona } from '@core/models/sgp/persona';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { InvencionActionService } from '../../invencion.action.service';
import { ISectorLicenciadoModalData, SectorLicenciadoModalComponent } from '../../modals/sector-licenciado-modal/sector-licenciado-modal.component';
import { IContratoAsociadoTableData, InvencionContratosFragment } from './invencion-contratos.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const SECTOR_LICENCIADO_KEY = marker('pii.invencion-contrato.sector-licenciado');
const MSG_PENDING_CHANGES = marker('msg.pii.sector-licenciado.unsaved');
const MSG_BUTTON_CONTINUE = marker('btn.continue');
const MSG_BUTTON_CANCEL = marker('btn.cancel');

@Component({
  selector: 'sgi-invencion-contratos',
  templateUrl: './invencion-contratos.component.html',
  styleUrls: ['./invencion-contratos.component.scss']
})
export class InvencionContratosComponent extends FragmentComponent implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];
  msgParamSectorLicenciadoEntity = {};
  textoDelete: string;
  formPart: InvencionContratosFragment;
  displayColumnsContratosAsociados = ['fecha', 'nombre', 'entidadFinanciadora', 'investigadorResponsable'];
  displayColumnsSectoresLicenciados = ['pais', 'sector', 'fechaInicioLicencia', 'fechaFinLicencia', 'exclusividad', 'acciones'];
  elementsPage = [5, 10, 25, 100];
  selectedContratoAsociado: IContratoAsociadoTableData;

  dataSourceContratosAsociados = new MatTableDataSource<IContratoAsociadoTableData>();
  dataSourceSectoresLicenciados = new MatTableDataSource<StatusWrapper<ISectorLicenciado>>();
  @ViewChild('paginatorContratosAsociados', { static: true }) paginatorContratosAsociados: MatPaginator;
  @ViewChild('sortContratosAsociados', { static: true }) sortContratosAsociados: MatSort;
  paginatorSectoresLicenciados: MatPaginator;
  sortSectoresLicenciados: MatSort;
  @ViewChild('paginatorSectoresLicenciados', { static: false }) set initPaginatorSectoresLicenciados(
    paginatorSectoresLicenciados: MatPaginator) {
    this.paginatorSectoresLicenciados = paginatorSectoresLicenciados;
    this.dataSourceSectoresLicenciados.paginator = this.paginatorSectoresLicenciados;
  }
  @ViewChild('sortSectoresLicenciados', { static: false }) set initSortSectoresLicenciados(sortSectoresLicenciados: MatSort) {
    this.sortSectoresLicenciados = sortSectoresLicenciados;
    this.dataSourceSectoresLicenciados.sortingDataAccessor =
      (wrapper: StatusWrapper<ISectorLicenciado>, property: string) => {
        switch (property) {
          case 'pais':
            return wrapper.value.pais.nombre;
          case 'sector':
            return wrapper.value.sectorAplicacion.nombre;
          case 'fechaInicioLicencia':
            return wrapper.value.fechaInicioLicencia;
          case 'fechaFinLicencia':
            return wrapper.value.fechaFinLicencia;
          case 'exclusividad':
            return wrapper.value.exclusividad;
          default:
            return wrapper[property];
        }
      };
    this.dataSourceSectoresLicenciados.sort = this.sortSectoresLicenciados;
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    protected actionService: InvencionActionService,
    private readonly translate: TranslateService,
    private readonly dialogService: DialogService,
    private readonly matDialog: MatDialog,
  ) {
    super(actionService.FRAGMENT.CONTRATOS, actionService);
    this.formPart = this.fragment as InvencionContratosFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.initContratosAsociadosTable();
    this.initSectoresLicenciadosTable();
    this.initSelectedContratoAsociado();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  getFirstEntidadFinanciadora([firstElement]: IEmpresa[]): IEmpresa {
    return firstElement;
  }

  getEntidadesFinanciadorasTooltip(entidadesFinanciadoras: IEmpresa[]): string {
    return entidadesFinanciadoras.reduce((prev, current, index) =>
      index === 0 ? prev + current.nombre : prev + ', ' + current.nombre
      , '');
  }

  getInventorResponsableCompleteName(investigadorPrincipal: IPersona): string {
    return investigadorPrincipal ? (investigadorPrincipal.nombre ?? '') + ' ' + (investigadorPrincipal.apellidos ?? '') : '';
  }

  selectContratoAsociado(contratoAsociado: IContratoAsociadoTableData): void {
    if (contratoAsociado.contrato.id === this.selectedContratoAsociado.contrato.id) {
      return;
    }
    if (!this.formPart.hasFragmentChangesPending()) {
      this.formPart.notifySelectedContratoAsociado(contratoAsociado);
    } else {
      this.openChangesPendingModal(contratoAsociado);
    }
  }

  private openChangesPendingModal(contratoAsociado: IContratoAsociadoTableData): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(
        MSG_PENDING_CHANGES, {}, MSG_BUTTON_CONTINUE, MSG_BUTTON_CANCEL,
        DIALOG_BUTTON_STYLE.BTN_STYLE_WARN, DIALOG_BUTTON_STYLE.BTN_STYLE_ACCENT).subscribe(
          (aceptado) => {
            if (aceptado) {
              this.formPart.notifySelectedContratoAsociado(contratoAsociado);
            }
          }
        )
    );
  }

  deleteSectorLicenciado(wrapper: StatusWrapper<ISectorLicenciado>): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteSectorLicenciado(wrapper);
          }
        }
      )
    );
  }

  openModal(wrapper?: StatusWrapper<ISectorLicenciado>, rowIndex?: number): void {
    const data: ISectorLicenciadoModalData = {
      sectorLicenciado: wrapper ? wrapper.value : {} as ISectorLicenciado,
      readonly: !this.formPart.hasEditPerm(),
      existingEntities: this.dataSourceSectoresLicenciados.data.map(elem => elem.value)
    };

    const config: MatDialogConfig = {
      panelClass: 'sgi-dialog-container',
      data,
      minWidth: '500px',
    };

    const dialogRef = this.matDialog.open(SectorLicenciadoModalComponent, config);
    dialogRef.afterClosed().subscribe((sectorLicenciado: ISectorLicenciado) => {
      if (sectorLicenciado) {
        if (wrapper) {
          const row = this.resolveTableRowIndexMatchingWithDataSource(rowIndex);
          this.formPart.updateSectorLicenciado(sectorLicenciado, row);
        } else {
          this.formPart.addSectorLicenciado(sectorLicenciado);
        }
      }
    });
  }

  private resolveTableRowIndexMatchingWithDataSource(rowIndex: number): number {
    this.dataSourceSectoresLicenciados.sortData(this.dataSourceSectoresLicenciados.filteredData, this.dataSourceSectoresLicenciados.sort);
    return (this.paginatorSectoresLicenciados.pageSize * this.paginatorSectoresLicenciados.pageIndex) + rowIndex;
  }

  private initContratosAsociadosTable(): void {
    this.dataSourceContratosAsociados.paginator = this.paginatorContratosAsociados;
    this.dataSourceContratosAsociados.sortingDataAccessor =
      (contratosAsociado: IContratoAsociadoTableData, property: string) => {
        switch (property) {
          case 'fecha':
            return contratosAsociado.contrato?.fechaInicio;
          case 'nombre':
            return contratosAsociado.contrato?.titulo;
          case 'entidadFinanciadora':
            return this.getFirstEntidadFinanciadora(contratosAsociado.entidadesFinanciadoras);
          case 'investigadorResponsable':
            return this.getInventorResponsableCompleteName(contratosAsociado.investigadorPrincipal);
          default:
            return contratosAsociado[property];
        }
      };
    this.dataSourceContratosAsociados.sort = this.sortContratosAsociados;
    this.getContratosAsociadosTableData();
  }

  private getContratosAsociadosTableData(): void {
    this.subscriptions.push(this.formPart.getContratosAsociados$()
      .subscribe(elements => this.dataSourceContratosAsociados.data = elements));
  }

  private initSectoresLicenciadosTable(): void {
    this.subscriptions.push(this.formPart.getSectoresLicenciados$()
      .subscribe(elements => this.dataSourceSectoresLicenciados.data = elements));
  }

  private initSelectedContratoAsociado(): void {
    this.subscriptions.push(
      this.formPart.getSelectedContratoAsociado$().subscribe(
        selectedContratoAsociado => this.selectedContratoAsociado = selectedContratoAsociado)
    );
  }

  private setupI18N(): void {
    this.translate.get(
      SECTOR_LICENCIADO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamSectorLicenciadoEntity = { entity: value });

    this.translate.get(
      SECTOR_LICENCIADO_KEY,
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
