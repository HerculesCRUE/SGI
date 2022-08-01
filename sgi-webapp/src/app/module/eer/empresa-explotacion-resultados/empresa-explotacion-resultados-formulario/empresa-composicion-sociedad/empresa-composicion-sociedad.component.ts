import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IEmpresaComposicionSociedad, TIPO_APORTACION_COMPOSICION_SOCIEDAD_MAP } from '@core/models/eer/empresa-composicion-sociedad';
import { DialogService } from '@core/services/dialog.service';
import { EmpresaComposicionSociedadService } from '@core/services/eer/empresa-composicion-sociedad/empresa-composicion-sociedad.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { EmpresaExplotacionResultadosActionService } from '../../empresa-explotacion-resultados.action.service';
import { EmpresaComposicionSociedadModalData, EmpresaComposicionSociedadModalComponent } from '../../modals/empresa-composicion-sociedad-modal/empresa-composicion-sociedad-modal.component';
import { EmpresaComposicionSociedadFragment } from './empresa-composicion-sociedad.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const EMPRESA_EQUIPO_MIEMBRO_KEY = marker('eer.empresa-composicion-sociedad.miembro');
const MODAL_TITLE_KEY = marker('eer.empresa-composicion-sociedad.miembro');

@Component({
  selector: 'sgi-empresa-composicion-sociedad',
  templateUrl: './empresa-composicion-sociedad.component.html',
  styleUrls: ['./empresa-composicion-sociedad.component.scss']
})
export class EmpresaComposicionSociedadComponent extends FragmentComponent implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];
  formPart: EmpresaComposicionSociedadFragment;

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['miembro', 'fechaInicio', 'fechaFin', 'participacion', 'capitalSocial', 'tipoAportacion', 'acciones'];

  modalTitleEntity: string;
  msgParamEntity = {};
  textoDelete: string;
  textoNoDelete: string;

  dataSource = new MatTableDataSource<StatusWrapper<IEmpresaComposicionSociedad>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get TIPO_APORTACION_MAP() {
    return TIPO_APORTACION_COMPOSICION_SOCIEDAD_MAP;
  }

  constructor(
    protected empresaComposicionSociedadService: EmpresaComposicionSociedadService,
    public actionService: EmpresaExplotacionResultadosActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.COMPOSICION_SOCIEDAD, actionService);
    this.formPart = this.fragment as EmpresaComposicionSociedadFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IEmpresaComposicionSociedad>, property: string) => {
        switch (property) {
          case 'miembro':
            return wrapper.value.miembroSociedadEmpresa ?
              wrapper.value.miembroSociedadEmpresa.nombre : wrapper.value.miembroSociedadPersona.nombre;
          default:
            return wrapper[property];
        }
      };
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.composicionesSociedad$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  private setupI18N(): void {
    this.translate.get(
      EMPRESA_EQUIPO_MIEMBRO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      MODAL_TITLE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.modalTitleEntity = value);

    this.translate.get(
      EMPRESA_EQUIPO_MIEMBRO_KEY,
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
   * Apertura de modal de composición sociedad (edición/creación)
   *
   * @param wrapper StatusWrapper<IEmpresaComposicionSociedad>
   * @param rowIndex índice de la fila del listado
   */
  openModal(wrapper?: StatusWrapper<IEmpresaComposicionSociedad>, rowIndex?: number): void {
    // Necesario para sincronizar los cambios de orden de registros dependiendo de la ordenación y paginación
    this.dataSource.sortData(this.dataSource.filteredData, this.dataSource.sort);
    const row = (this.paginator.pageSize * this.paginator.pageIndex) + rowIndex;

    const data: EmpresaComposicionSociedadModalData = {
      titleEntity: this.modalTitleEntity,
      entidad: wrapper?.value ?? {} as IEmpresaComposicionSociedad,
      selectedEntidades: this.dataSource.data.map(element => element.value),
    };

    if (wrapper) {
      const filtered = Object.assign([], data.selectedEntidades);
      filtered.splice(row, 1);
      data.selectedEntidades = filtered;
    }

    const config: MatDialogConfig = {
      data
    };
    const dialogRef = this.matDialog.open(EmpresaComposicionSociedadModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: EmpresaComposicionSociedadModalData) => {
        if (modalData) {
          if (!wrapper) {
            modalData.entidad.empresa = this.actionService.empresa;
            this.formPart.addEmpresaComposicionSociedad(modalData.entidad as IEmpresaComposicionSociedad);
          } else if (!wrapper.created) {
            const entidad = new StatusWrapper<IEmpresaComposicionSociedad>(modalData.entidad as IEmpresaComposicionSociedad);
            this.formPart.updateEmpresaComposicionSociedad(entidad);
          }
        }
      }
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  /**
   * Eliminar la composición de la sociedad de la empresa
   * @param wrapper StatusWrapper<IEmpresaComposicionSociedad
   */
  deleteComposicionSociedad(wrapper: StatusWrapper<IEmpresaComposicionSociedad>) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteEmpresaComposicionSociedad(wrapper);
          }
        }
      )
    );

  }

}
