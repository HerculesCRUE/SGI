import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoEquipo } from '@core/models/csp/proyecto-equipo';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { MiembroEquipoProyectoModalComponent, MiembroEquipoProyectoModalData } from '../../../shared/miembro-equipo-proyecto-modal/miembro-equipo-proyecto-modal.component';
import { ProyectoActionService } from '../../proyecto.action.service';
import { ProyectoEquipoFragment } from './proyecto-equipo.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const PROYECTO_EQUIPO_MIEMBRO_KEY = marker('csp.proyecto-equipo.miembro');
const MODAL_TITLE_KEY = marker('csp.proyecto-equipo.miembro-equipo');

@Component({
  selector: 'sgi-proyecto-equipo',
  templateUrl: './proyecto-equipo.component.html',
  styleUrls: ['./proyecto-equipo.component.scss']
})
export class ProyectoEquipoComponent extends FragmentComponent implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];
  formPart: ProyectoEquipoFragment;

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['numIdentificacion', 'nombre', 'apellidos', 'rolEquipo', 'fechaInicio', 'fechaFin', 'horas', 'acciones'];

  modalTitleEntity: string;
  msgParamEntity = {};
  textoDelete: string;

  dataSource = new MatTableDataSource<StatusWrapper<IProyectoEquipo>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    protected proyectoService: ProyectoService,
    private actionService: ProyectoActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.EQUIPO_PROYECTO, actionService);
    this.formPart = this.fragment as ProyectoEquipoFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IProyectoEquipo>, property: string) => {
        switch (property) {
          case 'numIdentificacion':
            return wrapper.value.persona.numeroDocumento;
          case 'nombre':
            return wrapper.value.persona.nombre;
          case 'apellidos':
            return wrapper.value.persona.apellidos;
          case 'rolEquipo':
            return wrapper.value.rolProyecto.nombre;
          case 'fechaInicio':
            return wrapper.value.fechaInicio;
          case 'fechaFin':
            return wrapper.value.fechaFin;
          case 'horas':
            return wrapper.value.horasDedicacion;
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
      PROYECTO_EQUIPO_MIEMBRO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      MODAL_TITLE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.modalTitleEntity = value);

    this.translate.get(
      PROYECTO_EQUIPO_MIEMBRO_KEY,
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
   * Apertura de modal de equipos (edición/creación)
   *
   * @param idEquipo Identificador de equipo a editar.
   */
  openModal(wrapper?: StatusWrapper<IProyectoEquipo>, position?: number): void {
    const data: MiembroEquipoProyectoModalData = {
      titleEntity: this.modalTitleEntity,
      entidad: wrapper?.value ?? {} as IProyectoEquipo,
      selectedEntidades: this.dataSource.data.map(element => element.value),
      fechaInicioMin: this.actionService.proyecto.fechaInicio,
      fechaFinMax: this.actionService.proyecto.fechaFinDefinitiva ?? this.actionService.proyecto.fechaFin,
      showHorasDedicacion: true,
      isEdit: Boolean(wrapper)
    };

    if (wrapper) {
      const filtered = Object.assign([], data.selectedEntidades);
      filtered.splice(position, 1);
      data.selectedEntidades = filtered;
    }

    const config = {
      panelClass: 'sgi-dialog-container',
      data
    };
    const dialogRef = this.matDialog.open(MiembroEquipoProyectoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: MiembroEquipoProyectoModalData) => {
        if (modalData) {
          if (!wrapper) {
            this.formPart.addProyectoEquipo(modalData.entidad as IProyectoEquipo);
          } else if (!wrapper.created) {
            const entidad = new StatusWrapper<IProyectoEquipo>(wrapper.value);
            this.formPart.updateProyectoEquipo(entidad);
          }
        }
      }
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  /**
   * Eliminar proyecto equipo
   */
  deleteEquipo(wrapper: StatusWrapper<IProyectoEquipo>) {
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
