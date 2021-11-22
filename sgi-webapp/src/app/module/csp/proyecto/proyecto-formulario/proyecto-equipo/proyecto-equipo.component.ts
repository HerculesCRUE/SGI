import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { VALIDACION_REQUISITOS_EQUIPO_IP_MAP } from '@core/enums/validaciones-requisitos-equipo-ip';
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
import { IProyectoEquipoListado, ProyectoEquipoFragment } from './proyecto-equipo.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const PROYECTO_EQUIPO_MIEMBRO_KEY = marker('csp.proyecto-equipo.miembro');
const MODAL_TITLE_KEY = marker('csp.proyecto-equipo.miembro-equipo');
const TOOLTIP_REQUISITOS_CONVOCATORIA_KEY = marker('csp.proyecto-equipo.tooltip');
const TOOLTIP_REQUISITOS_CONVOCATORIA_FECHA_OBTENCION_KEY = marker('csp.proyecto-equipo.tooltip-fechaObtencion');
const TOOLTIP_REQUISITOS_CONVOCATORIA_FEHCA_MAX_KEY = marker('csp.proyecto-equipo.tooltip-fechaMax');
const TOOLTIP_REQUISITOS_CONVOCATORIA_FECHA_MIN_KEY = marker('csp.proyecto-equipo.tooltip-fechaMin');
const TOOLTIP_REQUISITOS_CONVOCATORIA_NIVEL_ACADEMICO_KEY = marker('csp.proyecto-equipo.tooltip-nivelAcademico');
const TOOLTIP_REQUISITOS_CONVOCATORIA_SEXO_KEY = marker('csp.proyecto-equipo.tooltip-sexo');
const TOOLTIP_REQUISITOS_CONVOCATORIA_DATOS_ACADEMICOS_KEY = marker('csp.proyecto-equipo.tooltip-datosAcademicos');
const TOOLTIP_REQUISITOS_CONVOCATORIA_CATEGORIAS_PROFESIONALES_KEY = marker('csp.proyecto-equipo.tooltip-categoriasProfesionales');
const TOOLTIP_REQUISITOS_CONVOCATORIA_VINCULACION_KEY = marker('csp.proyecto-equipo.tooltip-vinculacion');
const TOOLTIP_REQUISITOS_CONVOCATORIA_NO_INCULACION_KEY = marker('csp.proyecto-equipo.tooltip-noVinculacion');
const TOOLTIP_REQUISITOS_CONVOCATORIA_FECHA_VINCULACION_MAYOR_MAX_KEY = marker('csp.proyecto-equipo.tooltip-fechaVinculacionMayorMax');
const TOOLTIP_REQUISITOS_CONVOCATORIA_FECHA_VINCULACION_MENOR_MIN_KEY = marker('csp.proyecto-equipo.tooltip-fechaVinculacionMenorMin');
const TOOLTIP_REQUISITOS_CONVOCATORIA_NO_FECHAS_KEY = marker('csp.proyecto-equipo.tooltip-noFechas');
const TOOLTIP_REQUISITOS_CONVOCATORIA_EDAD_MAX_KEY = marker('csp.proyecto-equipo.tooltip-edadMax');

@Component({
  selector: 'sgi-proyecto-equipo',
  templateUrl: './proyecto-equipo.component.html',
  styleUrls: ['./proyecto-equipo.component.scss']
})
export class ProyectoEquipoComponent extends FragmentComponent implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];
  formPart: ProyectoEquipoFragment;

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['helpIcon', 'numIdentificacion', 'nombre', 'apellidos', 'rolEquipo', 'fechaInicio', 'fechaFin', 'horas', 'acciones'];

  modalTitleEntity: string;
  msgParamEntity = {};
  textoDelete: string;

  dataSource = new MatTableDataSource<StatusWrapper<IProyectoEquipoListado>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get VALIDACION_REQUISITOS_EQUIPO_IP_MAP() {
    return VALIDACION_REQUISITOS_EQUIPO_IP_MAP;
  }

  constructor(
    protected proyectoService: ProyectoService,
    public actionService: ProyectoActionService,
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
      (wrapper: StatusWrapper<IProyectoEquipoListado>, property: string) => {
        switch (property) {
          case 'numIdentificacion':
            return wrapper.value.proyectoEquipo.persona.numeroDocumento;
          case 'nombre':
            return wrapper.value.proyectoEquipo.persona.nombre;
          case 'apellidos':
            return wrapper.value.proyectoEquipo.persona.apellidos;
          case 'rolEquipo':
            return wrapper.value.proyectoEquipo.rolProyecto.nombre;
          case 'fechaInicio':
            return wrapper.value.proyectoEquipo.fechaInicio;
          case 'fechaFin':
            return wrapper.value.proyectoEquipo.fechaFin;
          case 'horas':
            return wrapper.value.proyectoEquipo.horasDedicacion;
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
      TOOLTIP_REQUISITOS_CONVOCATORIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.formPart.msgToolTip = value);

    this.translate.get(
      TOOLTIP_REQUISITOS_CONVOCATORIA_SEXO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.formPart.msgToolTipSexo = value);

    this.translate.get(
      TOOLTIP_REQUISITOS_CONVOCATORIA_DATOS_ACADEMICOS_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.formPart.msgToolTipDatosAcademicos = value);

    this.translate.get(
      TOOLTIP_REQUISITOS_CONVOCATORIA_FECHA_MIN_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.formPart.msgToolTipFechaMin = value);

    this.translate.get(
      TOOLTIP_REQUISITOS_CONVOCATORIA_FECHA_OBTENCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.formPart.msgToolTipFechaObtencion = value);

    this.translate.get(
      TOOLTIP_REQUISITOS_CONVOCATORIA_FEHCA_MAX_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.formPart.msgToolTipFechaMax = value);

    this.translate.get(
      TOOLTIP_REQUISITOS_CONVOCATORIA_NIVEL_ACADEMICO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.formPart.msgToolTipNivelAcademico = value);

    this.translate.get(
      TOOLTIP_REQUISITOS_CONVOCATORIA_CATEGORIAS_PROFESIONALES_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.formPart.msgToolTipCategoriasProfesionales = value);

    this.translate.get(
      TOOLTIP_REQUISITOS_CONVOCATORIA_VINCULACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.formPart.msgToolTipVinculacion = value);

    this.translate.get(
      TOOLTIP_REQUISITOS_CONVOCATORIA_FECHA_VINCULACION_MENOR_MIN_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.formPart.msgToolTipFechaVinculacionMenorMin = value);

    this.translate.get(
      TOOLTIP_REQUISITOS_CONVOCATORIA_FECHA_VINCULACION_MAYOR_MAX_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.formPart.msgToolTipFechaVinculacionMayorMax = value);

    this.translate.get(
      TOOLTIP_REQUISITOS_CONVOCATORIA_NO_FECHAS_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.formPart.msgToolTipNoFechas = value);

    this.translate.get(
      TOOLTIP_REQUISITOS_CONVOCATORIA_EDAD_MAX_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.formPart.msgToolTipEdadMax = value);

    this.translate.get(
      TOOLTIP_REQUISITOS_CONVOCATORIA_NO_INCULACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.formPart.msgToolTipNoVinculacion = value);

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
  openModal(wrapper?: StatusWrapper<IProyectoEquipoListado>, rowIndex?: number): void {
    // Necesario para sincronizar los cambios de orden de registros dependiendo de la ordenación y paginación
    this.dataSource.sortData(this.dataSource.filteredData, this.dataSource.sort);
    const row = (this.paginator.pageSize * this.paginator.pageIndex) + rowIndex;

    const data: MiembroEquipoProyectoModalData = {
      titleEntity: this.modalTitleEntity,
      entidad: wrapper?.value.proyectoEquipo ?? {} as IProyectoEquipo,
      selectedEntidades: this.dataSource.data.map(element => element.value.proyectoEquipo),
      fechaInicioMin: this.actionService.proyecto.fechaInicio,
      fechaFinMax: this.actionService.proyecto.fechaFinDefinitiva ?? this.actionService.proyecto.fechaFin,
      showHorasDedicacion: true,
      isEdit: Boolean(wrapper),
      readonly: this.actionService.readonly
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
    const dialogRef = this.matDialog.open(MiembroEquipoProyectoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: MiembroEquipoProyectoModalData) => {
        if (modalData) {
          if (!wrapper) {
            this.formPart.addProyectoEquipo(
              {
                proyectoEquipo: modalData.entidad as IProyectoEquipo
              } as IProyectoEquipoListado);
          } else if (!wrapper.created) {
            const entidad = new StatusWrapper<IProyectoEquipoListado>(
              {
                proyectoEquipo: modalData.entidad as IProyectoEquipo
              } as IProyectoEquipoListado);
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
  deleteEquipo(wrapper: StatusWrapper<IProyectoEquipoListado>) {
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
