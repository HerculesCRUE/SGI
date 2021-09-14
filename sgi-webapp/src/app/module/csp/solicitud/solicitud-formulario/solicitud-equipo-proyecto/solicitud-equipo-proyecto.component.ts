import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { ISolicitudProyectoEquipo } from '@core/models/csp/solicitud-proyecto-equipo';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap, take } from 'rxjs/operators';
import { MiembroEquipoSolicitudModalComponent, MiembroEquipoSolicitudModalData } from '../../../shared/miembro-equipo-solicitud-modal/miembro-equipo-solicitud-modal.component';
import { SOLICITUD_ROUTE_NAMES } from '../../solicitud-route-names';
import { SolicitudActionService } from '../../solicitud.action.service';
import { SolicitudEquipoProyectoFragment } from './solicitud-equipo-proyecto.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const MODAL_TITLE_KEY = marker('csp.solicitud-equipo-proyecto.miembro-equipo');
const SOLICITUD_EQUIPO_PROYECTO_MIEMBRO_KEY = marker('csp.solicitud-equipo-proyecto.miembro');

@Component({
  selector: 'sgi-solicitud-equipo-proyecto',
  templateUrl: './solicitud-equipo-proyecto.component.html',
  styleUrls: ['./solicitud-equipo-proyecto.component.scss']
})
export class SolicitudEquipoProyectoComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  formPart: SolicitudEquipoProyectoFragment;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['persona', 'nombre', 'apellidos', 'rolProyecto', 'acciones'];

  msgParamEntity = {};
  modalTitleEntity: string;
  textoDelete: string;

  dataSource = new MatTableDataSource<StatusWrapper<ISolicitudProyectoEquipo>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    private actionService: SolicitudActionService,
    private router: Router,
    private route: ActivatedRoute,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.EQUIPO_PROYECTO, actionService);
    this.formPart = this.fragment as SolicitudEquipoProyectoFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<ISolicitudProyectoEquipo>, property: string) => {
        switch (property) {
          case 'persona':
            return wrapper.value.persona.numeroDocumento;
          case 'nombre':
            return wrapper.value.persona.nombre;
          case 'apellidos':
            return wrapper.value.persona.apellidos;
          case 'rolProyecto':
            return wrapper.value.rolProyecto.nombre;
          default:
            return wrapper[property];
        }
      };

    this.actionService.datosProyectoComplete$.pipe(
      take(1)
    ).subscribe(
      (complete) => {
        if (!complete) {
          this.router.navigate(['../', SOLICITUD_ROUTE_NAMES.PROYECTO_DATOS], { relativeTo: this.route });
        }
      }
    );

    const subcription = this.formPart.proyectoEquipos$.subscribe(
      (proyectoEquipos) => {
        if (proyectoEquipos.length === 0 ||
          proyectoEquipos.filter(equipo => equipo.value.persona?.id === this.actionService.solicitante?.id).length > 0) {
          this.formPart.setErrors(false);
        } else {
          this.formPart.setErrors(true);
        }
        this.dataSource.data = proyectoEquipos;
      }
    );
    this.subscriptions.push(subcription);
  }

  private setupI18N(): void {
    this.translate.get(
      SOLICITUD_EQUIPO_PROYECTO_MIEMBRO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      MODAL_TITLE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.modalTitleEntity = value);

    this.translate.get(
      SOLICITUD_EQUIPO_PROYECTO_MIEMBRO_KEY,
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

  openModal(wrapper?: StatusWrapper<ISolicitudProyectoEquipo>, rowIndex?: number): void {
    // Necesario para sincronizar los cambios de orden de registros dependiendo de la ordenación y paginación
    this.dataSource.sortData(this.dataSource.filteredData, this.dataSource.sort);
    const row = (this.paginator.pageSize * this.paginator.pageIndex) + rowIndex;

    const data: MiembroEquipoSolicitudModalData = {
      titleEntity: this.modalTitleEntity,
      entidad: wrapper?.value ?? {} as ISolicitudProyectoEquipo,
      selectedEntidades: this.dataSource.data.map(element => element.value),
      mesInicialMin: 1,
      mesFinalMax: this.actionService.duracionProyecto,
      isEdit: Boolean(wrapper),
      index: row,
      readonly: this.formPart.readonly
    };

    if (wrapper) {
      const filtered = Object.assign([], data.selectedEntidades);
      filtered.splice(row, 1);
      data.selectedEntidades = filtered;
    }

    const config = {
      panelClass: 'sgi-dialog-container',
      data
    };
    const dialogRef = this.matDialog.open(MiembroEquipoSolicitudModalComponent, config);
    dialogRef.afterClosed().subscribe((modalData: MiembroEquipoSolicitudModalData) => {
      if (modalData) {
        if (!wrapper) {
          this.formPart.addProyectoEquipo(modalData.entidad as ISolicitudProyectoEquipo);
        } else {
          wrapper.setEdited();
          this.formPart.updateProyectoEquipo(wrapper, modalData.index);
          this.formPart.setChanges(true);
        }
      }
    });
  }

  deleteProyectoEquipo(wrapper: StatusWrapper<ISolicitudProyectoEquipo>) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteProyectoEquipo(wrapper);
          }
        }
      )
    );
  }
}
