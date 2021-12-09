import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { ISolicitudProyectoResponsableEconomico } from '@core/models/csp/solicitud-proyecto-responsable-economico';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { getPersonaEmailListConcatenated } from 'src/app/esb/sgp/shared/pipes/persona-email.pipe';
import { SolicitudProyectoResponsableEconomicoModalComponent, SolicitudProyectoResponsableEconomicoModalData } from '../../modals/solicitud-proyecto-responsable-economico-modal/solicitud-proyecto-responsable-economico-modal.component';
import { SolicitudActionService } from '../../solicitud.action.service';
import { SolicitudProyectoResponsableEconomicoFragment } from './solicitud-proyecto-responsable-economico.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const SOLICITUD_PROYECTO_EQUIPO_RESPONSABLE_ECONOMICO_KEY = marker('csp.solicitud-proyecto-responsable-economico');

@Component({
  selector: 'sgi-solicitud-proyecto-responsable-economico',
  templateUrl: './solicitud-proyecto-responsable-economico.component.html',
  styleUrls: ['./solicitud-proyecto-responsable-economico.component.scss']
})
export class SolicitudProyectoResponsableEconomicoComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  formPart: SolicitudProyectoResponsableEconomicoFragment;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns = ['persona', 'nombre', 'apellidos', 'acciones'];
  elementsPage = [5, 10, 25, 100];

  msgParamEntity = {};
  textoDelete: string;

  dataSource = new MatTableDataSource<StatusWrapper<ISolicitudProyectoResponsableEconomico>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    private actionService: SolicitudActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.RESPONSABLE_ECONOMICO, actionService);
    this.formPart = this.fragment as SolicitudProyectoResponsableEconomicoFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<ISolicitudProyectoResponsableEconomico>, property: string) => {
        switch (property) {
          case 'persona':
            return getPersonaEmailListConcatenated(wrapper.value.persona);
          case 'nombre':
            return wrapper.value.persona.nombre;
          case 'apellidos':
            return wrapper.value.persona.apellidos;
          default:
            return wrapper[property];
        }
      };

    this.dataSource.sort = this.sort;

    this.subscriptions.push(this.formPart.responsablesEconomicos$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  private setupI18N(): void {
    this.translate.get(
      SOLICITUD_PROYECTO_EQUIPO_RESPONSABLE_ECONOMICO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      SOLICITUD_PROYECTO_EQUIPO_RESPONSABLE_ECONOMICO_KEY,
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

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  openModal(wrapper?: StatusWrapper<ISolicitudProyectoResponsableEconomico>, rowIndex?: number): void {
    // Necesario para sincronizar los cambios de orden de registros dependiendo de la ordenación y paginación
    this.dataSource.sortData(this.dataSource.filteredData, this.dataSource.sort);
    const row = (this.paginator.pageSize * this.paginator.pageIndex) + rowIndex;

    const data: SolicitudProyectoResponsableEconomicoModalData = {
      entidad: wrapper?.value ?? {} as ISolicitudProyectoResponsableEconomico,
      selectedEntidades: this.dataSource.data.map(element => element.value),
      mesMax: this.actionService.duracionProyecto,
      isEdit: Boolean(wrapper),
      readonly: this.formPart.readonly
    };

    if (wrapper) {
      const filtered = Object.assign([], data.selectedEntidades);
      filtered.splice(row, 1);
      data.selectedEntidades = filtered;
    }

    const config: MatDialogConfig = {
      panelClass: 'sgi-dialog-container',
      minWidth: '700px',
      data
    };
    const dialogRef = this.matDialog.open(SolicitudProyectoResponsableEconomicoModalComponent, config);
    dialogRef.afterClosed().subscribe((modalData: SolicitudProyectoResponsableEconomicoModalData) => {
      if (modalData) {
        if (!wrapper) {
          this.formPart.addResponsableEconomico(modalData.entidad);
        } else {
          this.formPart.updateResponsableEconomico(wrapper);
        }
      }
    });
  }

  deleteResponsableEconomico(wrapper: StatusWrapper<ISolicitudProyectoResponsableEconomico>) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteResponsableEconomico(wrapper);
          }
        }
      )
    );
  }
}
