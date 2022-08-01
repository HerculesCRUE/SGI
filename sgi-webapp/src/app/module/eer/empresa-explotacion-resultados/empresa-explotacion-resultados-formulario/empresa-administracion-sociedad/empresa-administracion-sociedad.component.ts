import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IEmpresaAdministracionSociedad, TipoAdministracion, TIPO_ADMINISTRACION_SOCIEDAD_MAP } from '@core/models/eer/empresa-administracion-sociedad';
import { DialogService } from '@core/services/dialog.service';
import { EmpresaAdministracionSociedadService } from '@core/services/eer/empresa-administracion-sociedad/empresa-administracion-sociedad.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { EmpresaExplotacionResultadosActionService } from '../../empresa-explotacion-resultados.action.service';
import { EmpresaAdministracionSociedadModalComponent, EmpresaAdministracionSociedadModalData } from '../../modals/empresa-administracion-sociedad-modal/empresa-administracion-sociedad-modal.component';
import { EmpresaAdministracionSociedadFragment } from './empresa-administracion-sociedad.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const EMPRESA_EQUIPO_MIEMBRO_KEY = marker('eer.empresa-administracion-sociedad.miembro');
const MODAL_TITLE_KEY = marker('eer.empresa-administracion-sociedad.miembro');

@Component({
  selector: 'sgi-empresa-administracion-sociedad',
  templateUrl: './empresa-administracion-sociedad.component.html',
  styleUrls: ['./empresa-administracion-sociedad.component.scss']
})
export class EmpresaAdministracionSociedadComponent extends FragmentComponent implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];
  formPart: EmpresaAdministracionSociedadFragment;

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['tipoAdministracion', 'nombre', 'apellidos', 'email', 'fechaInicio', 'fechaFin', 'entidad', 'acciones'];

  modalTitleEntity: string;
  msgParamEntity = {};
  textoDelete: string;
  textoNoDelete: string;

  dataSource = new MatTableDataSource<StatusWrapper<IEmpresaAdministracionSociedad>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get TIPO_ADMINISTRACION_MAP() {
    return TIPO_ADMINISTRACION_SOCIEDAD_MAP;
  }

  constructor(
    protected empresaAdministracionSociedadService: EmpresaAdministracionSociedadService,
    public actionService: EmpresaExplotacionResultadosActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.ADMINISTRACION_SOCIEDAD, actionService);
    this.formPart = this.fragment as EmpresaAdministracionSociedadFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IEmpresaAdministracionSociedad>, property: string) => {
        switch (property) {
          case 'miembro':
            return wrapper.value.miembroEquipoAdministracion ?
              wrapper.value.miembroEquipoAdministracion.nombre : '';
          default:
            return wrapper[property];
        }
      };
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.administracionesSociedad$.subscribe(elements => {
      this.dataSource.data = elements;
      if (elements.length > 0) {
        this.validateTipoAdministracion();
      }
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
   * @param wrapper StatusWrapper<IEmpresaAdministracionSociedad>
   * @param rowIndex índice de la fila del listado
   */
  openModal(wrapper?: StatusWrapper<IEmpresaAdministracionSociedad>, rowIndex?: number): void {
    // Necesario para sincronizar los cambios de orden de registros dependiendo de la ordenación y paginación
    this.dataSource.sortData(this.dataSource.filteredData, this.dataSource.sort);
    const row = (this.paginator.pageSize * this.paginator.pageIndex) + rowIndex;

    const data: EmpresaAdministracionSociedadModalData = {
      titleEntity: this.modalTitleEntity,
      entidad: wrapper?.value ?? {} as IEmpresaAdministracionSociedad,
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
    const dialogRef = this.matDialog.open(EmpresaAdministracionSociedadModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: EmpresaAdministracionSociedadModalData) => {
        if (modalData) {
          if (!wrapper) {
            modalData.entidad.empresa = this.actionService.empresa;
            this.formPart.addEmpresaAdministracionSociedad(modalData.entidad as IEmpresaAdministracionSociedad);
          } else if (!wrapper.created) {
            const entidad = new StatusWrapper<IEmpresaAdministracionSociedad>(modalData.entidad as IEmpresaAdministracionSociedad);
            this.formPart.updateEmpresaAdministracionSociedad(entidad);
            this.validateTipoAdministracion();
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
   * @param wrapper StatusWrapper<IEmpresaAdministracionSociedad
   */
  deleteAdministracionSociedad(wrapper: StatusWrapper<IEmpresaAdministracionSociedad>) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteEmpresaAdministracionSociedad(wrapper);
            this.validateTipoAdministracion();
          }
        }
      )
    );

  }

  private validateTipoAdministracion() {
    const entidades = this.dataSource.data.map(element => element.value);

    const countAdminUnico = entidades.filter(entidad => entidad.tipoAdministracion === TipoAdministracion.ADMINISTRADOR_UNICO).length;
    const countAdminSolidario = entidades.filter(entidad => entidad.tipoAdministracion === TipoAdministracion.ADMINISTRADOR_SOLIDARIO).length;
    const countAdminMancomunado = entidades.filter(entidad => entidad.tipoAdministracion === TipoAdministracion.ADMINISTRADOR_MANCOMUNADO).length;
    const countConsejoAdmin = entidades.filter(entidad => entidad.tipoAdministracion === TipoAdministracion.CONSEJO_ADMINISTRACION).length;

    let err = true;
    if (countAdminUnico === 1 || countAdminSolidario >= 2 || countAdminMancomunado >= 2 || countConsejoAdmin >= 3) {
      err = false;
    }

    if (err) {
      this.formPart.setComplete(false);
      this.formPart.setErrors(true);
    } else {
      this.formPart.setComplete(true);
      this.formPart.setErrors(false);
    }
  }

}
