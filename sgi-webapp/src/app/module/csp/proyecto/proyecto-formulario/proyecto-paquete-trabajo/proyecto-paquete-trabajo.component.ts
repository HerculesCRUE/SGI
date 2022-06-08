import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoPaqueteTrabajo } from '@core/models/csp/proyecto-paquete-trabajo';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { PaquetesTrabajoModalData, ProyectoPaquetesTrabajoModalComponent } from '../../modals/proyecto-paquetes-trabajo-modal/proyecto-paquetes-trabajo-modal.component';
import { ProyectoActionService } from '../../proyecto.action.service';
import { ProyectoPaqueteTrabajoFragment } from './proyecto-paquete-trabajo.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const PROYECTO_PAQUETE_TRABAJO_KEY = marker('csp.proyecto-paquete-trabajo');

@Component({
  selector: 'sgi-proyecto-paquete-trabajo',
  templateUrl: './proyecto-paquete-trabajo.component.html',
  styleUrls: ['./proyecto-paquete-trabajo.component.scss']
})
export class ProyectoPaqueteTrabajoComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  formPart: ProyectoPaqueteTrabajoFragment;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['nombre', 'fechaInicio', 'fechaFin', 'personaMes', 'descripcion', 'acciones'];

  msgParamEntity = {};
  textoDelete: string;

  dataSource = new MatTableDataSource<StatusWrapper<IProyectoPaqueteTrabajo>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    protected proyectoReunionService: ProyectoService,
    public actionService: ProyectoActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.PAQUETE_TRABAJO, actionService);
    this.formPart = this.fragment as ProyectoPaqueteTrabajoFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IProyectoPaqueteTrabajo>, property: string) => {
        switch (property) {
          case 'nombre':
            return wrapper.value.nombre;
          case 'fechaInicio':
            return wrapper.value.fechaInicio;
          case 'fechaFin':
            return wrapper.value.fechaFin;
          case 'personaMes':
            return wrapper.value.personaMes;
          case 'descripcion':
            return wrapper.value.descripcion;
          default:
            return wrapper[property];
        }
      };
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.paquetesTrabajo$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  private setupI18N(): void {
    this.translate.get(
      PROYECTO_PAQUETE_TRABAJO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      PROYECTO_PAQUETE_TRABAJO_KEY,
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
   * Apertura de modal de paquete proyecto (edición/creación)
   * @param idProyecto Identificador de paquete proyecto a editar.
   */
  openModal(wrapper?: StatusWrapper<IProyectoPaqueteTrabajo>): void {
    const dataModal: PaquetesTrabajoModalData = {
      paquetesTrabajo: this.dataSource.data.map(paquetes => paquetes.value),
      paqueteTrabajo: wrapper ? wrapper.value : {} as IProyectoPaqueteTrabajo,
      fechaInicio: this.actionService.proyecto.fechaInicio,
      fechaFin: this.actionService.proyecto.fechaFin,
      readonly: this.actionService.readonly
    };

    if (wrapper) {
      const index = dataModal.paquetesTrabajo.findIndex((element) => element === wrapper.value);
      if (index >= 0) {
        dataModal.paquetesTrabajo.splice(index, 1);
      }
    }

    const config = {
      data: dataModal,
    };
    const dialogRef = this.matDialog.open(ProyectoPaquetesTrabajoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: PaquetesTrabajoModalData) => {
        if (modalData) {
          if (wrapper) {
            if (!wrapper.created) {
              wrapper.setEdited();
            }
            this.formPart.setChanges(true);
          } else {
            this.formPart.addPaqueteTrabajo(modalData.paqueteTrabajo);
          }
        }
      }
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  /**
   * Eliminar proyecto paquete proyecto
   */
  deletePaqueteTrabajo(wrapper: StatusWrapper<IProyectoPaqueteTrabajo>) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deletePaqueteTrabajo(wrapper);
          }
        }
      )
    );
  }

}
