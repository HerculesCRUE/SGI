import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoEntidadConvocante } from '@core/models/csp/proyecto-entidad-convocante';
import { DialogService } from '@core/services/dialog.service';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs/internal/Subscription';
import { switchMap } from 'rxjs/operators';
import { ProyectoEntidadConvocanteModalComponent, ProyectoEntidadConvocanteModalData } from '../../modals/proyecto-entidad-convocante-modal/proyecto-entidad-convocante-modal.component';
import { ProyectoEntidadConvocantePlanPipe } from '../../pipes/proyecto-entidad-convocante-plan.pipe';
import { ProyectoActionService } from '../../proyecto.action.service';
import { ProyectoEntidadesConvocantesFragment } from './proyecto-entidades-convocantes.fragment';

const MSG_ENTITY_DELETE_KEY = marker('msg.delete.entity');
const PROYECTO_ENTIDAD_CONVOCANTE_KEY = marker('csp.proyecto-entidad-convocante');

@Component({
  selector: 'sgi-proyecto-entidades-convocantes',
  templateUrl: './proyecto-entidades-convocantes.component.html',
  styleUrls: ['./proyecto-entidades-convocantes.component.scss']
})
export class ProyectoEntidadesConvocantesComponent extends FragmentComponent implements OnInit, OnDestroy {
  proyectoEntidadesConvocantesFragment: ProyectoEntidadesConvocantesFragment;
  private subscriptions: Subscription[] = [];

  columns = ['nombre', 'cif', 'plan', 'programaConvocatoria', 'programa', 'acciones'];
  elementsPage = [5, 10, 25, 100];

  msgParamEntity = {};
  textoDelete: string;

  dataSource = new MatTableDataSource<IProyectoEntidadConvocante>();

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    protected actionService: ProyectoActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService,
    private proyectoEntidadConvocantePlanPipe: ProyectoEntidadConvocantePlanPipe
  ) {
    super(actionService.FRAGMENT.ENTIDADES_CONVOCANTES, actionService);
    this.proyectoEntidadesConvocantesFragment = this.fragment as ProyectoEntidadesConvocantesFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor = (proyectoEntidadConvocante: IProyectoEntidadConvocante, property: string) => {
      switch (property) {
        case 'nombre': return proyectoEntidadConvocante.entidad.nombre;
        case 'cif': return proyectoEntidadConvocante.entidad.numeroIdentificacion;
        case 'plan': return this.proyectoEntidadConvocantePlanPipe.transform(proyectoEntidadConvocante);
        case 'programaConvocatoria': return proyectoEntidadConvocante.programaConvocatoria?.nombre;
        case 'programa': return proyectoEntidadConvocante.programa?.nombre;
        default: return proyectoEntidadConvocante[property];
      }
    };
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.proyectoEntidadesConvocantesFragment.proyectoEntidadConvocantes$.subscribe(
      (data) => {
        this.dataSource.data = data;
      })
    );
  }

  private setupI18N(): void {
    this.translate.get(
      PROYECTO_ENTIDAD_CONVOCANTE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      PROYECTO_ENTIDAD_CONVOCANTE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ENTITY_DELETE_KEY,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoDelete = value);
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  openModal(value?: IProyectoEntidadConvocante): void {
    const data: ProyectoEntidadConvocanteModalData = {
      proyectoEntidadConvocante: value,
      selectedEmpresas: this.proyectoEntidadesConvocantesFragment.
        proyectoEntidadConvocantes$.value.map((convocanteData) => convocanteData.entidad)
    };
    const config = {
      panelClass: 'sgi-dialog-container',
      data
    };
    const dialogRef = this.matDialog.open(ProyectoEntidadConvocanteModalComponent, config);
    dialogRef.afterClosed().subscribe((entidadConvocante: ProyectoEntidadConvocanteModalData) => {
      if (entidadConvocante) {
        if (value) {
          this.proyectoEntidadesConvocantesFragment.updateProyectoEntidadConvocante(entidadConvocante.proyectoEntidadConvocante);
        } else {
          this.proyectoEntidadesConvocantesFragment.addProyectoEntidadConvocante(entidadConvocante.proyectoEntidadConvocante);
        }
      }
    });
  }

  deleteProyectoEntidadConvocante(data: IProyectoEntidadConvocante) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete, this.msgParamEntity).subscribe(
        (aceptado: boolean) => {
          if (aceptado) {
            this.proyectoEntidadesConvocantesFragment.deleteProyectoEntidadConvocante(data);
          }
        }
      )
    );
  }
}
