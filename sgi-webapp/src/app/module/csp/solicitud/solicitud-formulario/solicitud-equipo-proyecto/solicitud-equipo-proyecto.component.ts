import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { VALIDACION_REQUISITOS_EQUIPO_IP_MAP } from '@core/enums/validaciones-requisitos-equipo-ip';
import { MSG_PARAMS } from '@core/i18n';
import { ISolicitudProyectoEquipo } from '@core/models/csp/solicitud-proyecto-equipo';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap, take } from 'rxjs/operators';
import { getPersonaEmailListConcatenated } from 'src/app/esb/sgp/shared/pipes/persona-email.pipe';
import { MiembroEquipoSolicitudModalComponent, MiembroEquipoSolicitudModalData } from '../../../shared/miembro-equipo-solicitud-modal/miembro-equipo-solicitud-modal.component';
import { SOLICITUD_ROUTE_NAMES } from '../../solicitud-route-names';
import { SolicitudActionService } from '../../solicitud.action.service';
import { ISolicitudProyectoEquipoListado, SolicitudEquipoProyectoFragment } from './solicitud-equipo-proyecto.fragment';

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
  displayedColumns = ['helpIcon', 'persona', 'nombre', 'apellidos', 'rolProyecto', 'acciones'];

  msgParamEntity = {};
  modalTitleEntity: string;
  textoDelete: string;

  dataSource = new MatTableDataSource<StatusWrapper<ISolicitudProyectoEquipoListado>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get VALIDACION_REQUISITOS_EQUIPO_IP_MAP() {
    return VALIDACION_REQUISITOS_EQUIPO_IP_MAP;
  }

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
      (wrapper: StatusWrapper<ISolicitudProyectoEquipoListado>, property: string) => {
        switch (property) {
          case 'persona':
            return getPersonaEmailListConcatenated(wrapper.value.solicitudProyectoEquipo.persona);
          case 'nombre':
            return wrapper.value.solicitudProyectoEquipo.persona.nombre;
          case 'apellidos':
            return wrapper.value.solicitudProyectoEquipo.persona.apellidos;
          case 'rolProyecto':
            return wrapper.value.solicitudProyectoEquipo.rolProyecto.nombre;
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
          proyectoEquipos.filter(equipo =>
            equipo.value.solicitudProyectoEquipo.persona?.id === this.actionService.solicitante?.id).length > 0) {
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

  openModal(wrapper?: StatusWrapper<ISolicitudProyectoEquipoListado>, rowIndex?: number): void {
    // Necesario para sincronizar los cambios de orden de registros dependiendo de la ordenación y paginación
    this.dataSource.sortData(this.dataSource.filteredData, this.dataSource.sort);
    const row = (this.paginator.pageSize * this.paginator.pageIndex) + rowIndex;

    const data: MiembroEquipoSolicitudModalData = {
      titleEntity: this.modalTitleEntity,
      entidad: wrapper?.value.solicitudProyectoEquipo ?? {} as ISolicitudProyectoEquipo,
      selectedEntidades: this.dataSource.data.map(element => element.value.solicitudProyectoEquipo),
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

    const config: MatDialogConfig = {
      data
    };
    const dialogRef = this.matDialog.open(MiembroEquipoSolicitudModalComponent, config);
    dialogRef.afterClosed().subscribe((modalData: MiembroEquipoSolicitudModalData) => {
      if (modalData) {
        this.checkAvisoSolicitante();
        if (!wrapper) {
          const solicitudProyectoEquipoNew = {
            solicitudProyectoEquipo: modalData.entidad as ISolicitudProyectoEquipo
          } as ISolicitudProyectoEquipoListado;
          this.formPart.addProyectoEquipo(solicitudProyectoEquipoNew);
        } else {
          wrapper.setEdited();
          this.formPart.updateProyectoEquipo(wrapper, modalData.index);
          this.formPart.setChanges(true);
        }
      }
    });
  }

  deleteProyectoEquipo(wrapper: StatusWrapper<ISolicitudProyectoEquipoListado>) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.checkAvisoSolicitante();
            this.formPart.deleteProyectoEquipo(wrapper);
          }
        }
      )
    );
  }

  private checkAvisoSolicitante() {
    const solicitudProyectoEquipos = this.formPart.proyectoEquipos$.value.map(wp => wp.value.solicitudProyectoEquipo);
    const existsSolicitante = solicitudProyectoEquipos.some(
      solicitudProyectoEquipo => solicitudProyectoEquipo.persona?.id === this.actionService.solicitante?.id);
    if (!existsSolicitante) {
      this.formPart.setErrors(true);
    } else {
      this.formPart.setErrors(false);
    }
  }
}
