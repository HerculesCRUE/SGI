import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoEquipo } from '@core/models/csp/proyecto-equipo';
import { IProyectoResponsableEconomico } from '@core/models/csp/proyecto-responsable-economico';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { getPersonaEmailListConcatenated } from 'src/app/esb/sgp/shared/pipes/persona-email.pipe';
import { ProyectoResponsableEconomicoModalComponent, ProyectoResponsableEconomicoModalData } from '../../modals/proyecto-responsable-economico-modal/proyecto-responsable-economico-modal.component';
import { ProyectoActionService } from '../../proyecto.action.service';
import { ProyectoResponsableEconomicoFragment } from './proyecto-responsable-economico.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const PROYECTO_EQUIPO_RESPONSABLE_ECONOMICO_KEY = marker('csp.solicitud-proyecto-responsable-economico');

@Component({
  selector: 'sgi-proyecto-responsable-economico',
  templateUrl: './proyecto-responsable-economico.component.html',
  styleUrls: ['./proyecto-responsable-economico.component.scss']
})
export class ProyectoResponsableEconomicoComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  formPart: ProyectoResponsableEconomicoFragment;

  displayedColumns = ['persona', 'nombre', 'apellidos', 'fechaInicio', 'fechaFin', 'acciones'];
  elementsPage = [5, 10, 25, 100];

  msgParamEntity = {};
  textoDelete: string;

  dataSource = new MatTableDataSource<StatusWrapper<IProyectoResponsableEconomico>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  disabledAdd = false;

  constructor(
    protected proyectoService: ProyectoService,
    private actionService: ProyectoActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.REPONSABLE_ECONOMICO, actionService);
    this.formPart = this.fragment as ProyectoResponsableEconomicoFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IProyectoEquipo>, property: string) => {
        switch (property) {
          case 'persona':
            return getPersonaEmailListConcatenated(wrapper.value.persona);
          case 'nombre':
            return wrapper.value.persona.nombre;
          case 'apellidos':
            return wrapper.value.persona.apellidos;
          case 'fechaInicio':
            return wrapper.value.fechaInicio;
          case 'fechaFin':
            return wrapper.value.fechaFin;
          default:
            return wrapper[property];
        }
      };
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.responsablesEconomicos$.subscribe(elements => {
      this.disabledAdd = elements.length === 1 && !!!elements[0].value.fechaInicio;
      this.dataSource.data = elements;
    }));
  }

  private setupI18N(): void {
    this.translate.get(
      PROYECTO_EQUIPO_RESPONSABLE_ECONOMICO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      PROYECTO_EQUIPO_RESPONSABLE_ECONOMICO_KEY,
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

  openModal(wrapper?: StatusWrapper<IProyectoResponsableEconomico>): void {
    const data: ProyectoResponsableEconomicoModalData = {
      entidad: wrapper?.value ?? {} as IProyectoResponsableEconomico,
      selectedEntidades: wrapper
        ? this.dataSource.data.filter(element => element !== wrapper).map(element => element.value)
        : this.dataSource.data.map(element => element.value),
      fechaInicioMin: this.actionService.proyecto.fechaInicio,
      fechaFinMax: this.actionService.proyecto.fechaFinDefinitiva ?? this.actionService.proyecto.fechaFin,
      isEdit: Boolean(wrapper),
      readonly: this.formPart.readonly
    };

    const config: MatDialogConfig = {
      data
    };
    const dialogRef = this.matDialog.open(ProyectoResponsableEconomicoModalComponent, config);
    dialogRef.afterClosed().subscribe((modalData: ProyectoResponsableEconomicoModalData) => {
      if (modalData) {
        if (!wrapper) {
          this.formPart.addResponsableEconomico(modalData.entidad);
        } else {
          this.formPart.updateResponsableEconomico(wrapper);
        }
      }
    });
  }

  deleteResponsableEconomico(wrapper: StatusWrapper<IProyectoResponsableEconomico>) {
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
