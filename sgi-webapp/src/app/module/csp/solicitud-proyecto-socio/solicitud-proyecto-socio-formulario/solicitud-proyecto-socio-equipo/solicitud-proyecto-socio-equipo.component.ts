import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { ISolicitudProyectoSocioEquipo } from '@core/models/csp/solicitud-proyecto-socio-equipo';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { MiembroEquipoSolicitudModalComponent, MiembroEquipoSolicitudModalData } from '../../../shared/miembro-equipo-solicitud-modal/miembro-equipo-solicitud-modal.component';
import { SolicitudProyectoSocioActionService } from '../../solicitud-proyecto-socio.action.service';
import { SolicitudProyectoSocioEquipoFragment } from './solicitud-proyecto-socio-equipo.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const MODAL_TITLE_KEY = marker('csp.solicitud-proyecto-socio-equipo.miembro-equipo');
const SOLICITUD_PROYECTO_SOCIO_EQUIPO_MIEMBRO_KEY = marker('csp.solicitud-proyecto-socio-equipo.miembro');

@Component({
  selector: 'sgi-solicitud-proyecto-socio-equipo',
  templateUrl: './solicitud-proyecto-socio-equipo.component.html',
  styleUrls: ['./solicitud-proyecto-socio-equipo.component.scss']
})
export class SolicitudProyectoSocioEquipoComponent extends FragmentComponent implements OnInit, OnDestroy {
  formPart: SolicitudProyectoSocioEquipoFragment;
  private subscriptions: Subscription[] = [];

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['persona', 'nombre', 'apellidos', 'rolProyecto', 'acciones'];

  msgParamEntity = {};
  modalTitleEntity: string;
  textoDelete: string;

  elementsPage = [5, 10, 25, 100];

  dataSource = new MatTableDataSource<StatusWrapper<ISolicitudProyectoSocioEquipo>>();
  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;

  constructor(
    private actionService: SolicitudProyectoSocioActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.EQUIPO, actionService);
    this.formPart = this.fragment as SolicitudProyectoSocioEquipoFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    const subcription = this.formPart.solicitudProyectoSocioEquipos$.subscribe(
      (proyectoEquipos) => {
        this.dataSource.data = proyectoEquipos;
      }
    );
    this.subscriptions.push(subcription);
    this.dataSource.sort = this.sort;
    this.dataSource.sortingDataAccessor = (wrapper, property) => {
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
          return wrapper.value[property];
      }
    };
  }

  private setupI18N(): void {
    this.translate.get(
      SOLICITUD_PROYECTO_SOCIO_EQUIPO_MIEMBRO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      MODAL_TITLE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.modalTitleEntity = value);

    this.translate.get(
      SOLICITUD_PROYECTO_SOCIO_EQUIPO_MIEMBRO_KEY,
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

  openModal(wrapper?: StatusWrapper<ISolicitudProyectoSocioEquipo>, rowIndex?: number): void {
    // Necesario para sincronizar los cambios de orden de registros dependiendo de la ordenación y paginación
    this.dataSource.sortData(this.dataSource.filteredData, this.dataSource.sort);
    const row = (this.paginator.pageSize * this.paginator.pageIndex) + rowIndex;

    const data: MiembroEquipoSolicitudModalData = {
      titleEntity: this.modalTitleEntity,
      entidad: wrapper?.value ?? {} as ISolicitudProyectoSocioEquipo,
      selectedEntidades: this.dataSource.data.map(element => element.value),
      mesInicialMin: this.actionService.mesInicio,
      mesFinalMax: this.actionService.mesFin ?? this.actionService.solicitudProyectoDuracion,
      isEdit: Boolean(wrapper),
      index: row,
      readonly: this.formPart.readonly
    };

    if (wrapper) {
      data.selectedEntidades.splice(row, 1);
    }

    const config = {
      panelClass: 'sgi-dialog-container',
      data
    };
    const dialogRef = this.matDialog.open(MiembroEquipoSolicitudModalComponent, config);
    dialogRef.afterClosed().subscribe((modalData: MiembroEquipoSolicitudModalData) => {
      if (modalData) {
        if (!wrapper) {
          this.formPart.addProyectoEquipoSocio(modalData.entidad as ISolicitudProyectoSocioEquipo);
        } else if (!wrapper.created) {
          const entidad = new StatusWrapper<ISolicitudProyectoSocioEquipo>(wrapper.value);
          this.formPart.updateProyectoEquipoSocio(entidad);
        }
      }
    });
  }

  deleteProyectoEquipo(wrapper: StatusWrapper<ISolicitudProyectoSocioEquipo>): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteProyectoEquipoSocio(wrapper);
          }
        }
      )
    );
  }
}
