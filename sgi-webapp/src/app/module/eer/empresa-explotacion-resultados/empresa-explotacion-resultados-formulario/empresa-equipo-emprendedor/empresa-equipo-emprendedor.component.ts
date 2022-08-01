import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IEmpresaEquipoEmprendedor } from '@core/models/eer/empresa-equipo-emprendedor';
import { DialogService } from '@core/services/dialog.service';
import { EmpresaEquipoEmprendedorService } from '@core/services/eer/empresa-equipo-emprendedor/empresa-equipo-emprendedor.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { getPersonaEmailListConcatenated } from 'src/app/esb/sgp/shared/pipes/persona-email.pipe';
import { EmpresaExplotacionResultadosActionService } from '../../empresa-explotacion-resultados.action.service';
import { EmpresaEquipoEmprendedorModalComponent, EmpresaEquipoEmprendedorModalData } from '../../modals/empresa-equipo-emprendedor-modal/empresa-equipo-emprendedor-modal.component';
import { EmpresaEquipoEmprendedorFragment, IEmpresaEquipoEmprendedorListado } from './empresa-equipo-emprendedor.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const EMPRESA_EQUIPO_MIEMBRO_KEY = marker('eer.empresa-equipo-emprendedor.miembro');
const MODAL_TITLE_KEY = marker('eer.empresa-equipo-emprendedor.miembro');

@Component({
  selector: 'sgi-empresa-equipo-emprendedor',
  templateUrl: './empresa-equipo-emprendedor.component.html',
  styleUrls: ['./empresa-equipo-emprendedor.component.scss']
})
export class EmpresaEquipoEmprendedorComponent extends FragmentComponent implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];
  formPart: EmpresaEquipoEmprendedorFragment;

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['nombre', 'apellidos', 'persona', 'categoria', 'entidad', 'acciones'];

  modalTitleEntity: string;
  msgParamEntity = {};
  textoDelete: string;
  textoNoDelete: string;

  dataSource = new MatTableDataSource<StatusWrapper<IEmpresaEquipoEmprendedorListado>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    protected empresaEquipoEmprendedorService: EmpresaEquipoEmprendedorService,
    public actionService: EmpresaExplotacionResultadosActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.EQUIPO_EMPRENDEDOR, actionService);
    this.formPart = this.fragment as EmpresaEquipoEmprendedorFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IEmpresaEquipoEmprendedorListado>, property: string) => {
        switch (property) {
          case 'persona':
            return getPersonaEmailListConcatenated(wrapper.value.miembroEquipo);
          case 'nombre':
            return wrapper.value.miembroEquipo.nombre;
          case 'apellidos':
            return wrapper.value.miembroEquipo.apellidos;
          case 'categoria':
            return wrapper.value.categoriaProfesional?.nombre;
          case 'entidad':
            return wrapper.value.miembroEquipo.entidad?.nombre;
          default:
            return wrapper[property];
        }
      };
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.equipos$.subscribe(elements => {
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
   * Apertura de modal del equipo emprendedor (edición/creación)
   *
   * @param wrapper StatusWrapper<IEmpresaEquipoEmprendedor>
   * @param rowIndex índice de la fila del listado
   */
  openModal(wrapper?: StatusWrapper<IEmpresaEquipoEmprendedor>, rowIndex?: number): void {
    // Necesario para sincronizar los cambios de orden de registros dependiendo de la ordenación y paginación
    this.dataSource.sortData(this.dataSource.filteredData, this.dataSource.sort);
    const row = (this.paginator.pageSize * this.paginator.pageIndex) + rowIndex;

    const data: EmpresaEquipoEmprendedorModalData = {
      titleEntity: this.modalTitleEntity,
      entidad: wrapper?.value ?? {} as IEmpresaEquipoEmprendedor,
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
    const dialogRef = this.matDialog.open(EmpresaEquipoEmprendedorModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: EmpresaEquipoEmprendedorModalData) => {
        if (modalData) {
          if (!wrapper) {
            modalData.entidad.empresa = this.actionService.empresa;
            this.formPart.addEmpresaEquipoEmprendedor(modalData.entidad as IEmpresaEquipoEmprendedorListado);
          }
        }
      }
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  /**
   * Eliminar equipo emprendedor de la empresa
   * @param wrapper StatusWrapper<IEmpresaEquipoEmprendedorListado
   */
  deleteEquipo(wrapper: StatusWrapper<IEmpresaEquipoEmprendedorListado>) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteEmpresaEquipoEmprendedor(wrapper);
          }
        }
      )
    );

  }

}
