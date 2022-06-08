import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { DialogService } from '@core/services/dialog.service';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs/internal/Subscription';
import { switchMap } from 'rxjs/operators';
import { ConvocatoriaActionService } from '../../convocatoria.action.service';
import { ConvocatoriaEntidadConvocanteModalComponent, ConvocatoriaEntidadConvocanteModalData } from '../../modals/convocatoria-entidad-convocante-modal/convocatoria-entidad-convocante-modal.component';
import { ConvocatoriaEntidadConvocanteData, ConvocatoriaEntidadesConvocantesFragment } from './convocatoria-entidades-convocantes.fragment';

const MSG_DELETE = marker('msg.deactivate.entity');
const CONVOCATORIA_ENTIDAD_CONVOCANTE_KEY = marker('csp.convocatoria-entidad-convocante');

@Component({
  selector: 'sgi-convocatoria-entidades-convocantes',
  templateUrl: './convocatoria-entidades-convocantes.component.html',
  styleUrls: ['./convocatoria-entidades-convocantes.component.scss']
})
export class ConvocatoriaEntidadesConvocantesComponent extends FragmentComponent implements OnInit, OnDestroy {
  formPart: ConvocatoriaEntidadesConvocantesFragment;
  private subscriptions: Subscription[] = [];

  columns = ['nombre', 'cif', 'plan', 'programa', 'itemPrograma', 'acciones'];
  elementsPage = [5, 10, 25, 100];

  msgParamEntity = {};
  textoDeactivate: string;

  dataSource = new MatTableDataSource<ConvocatoriaEntidadConvocanteData>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    protected actionService: ConvocatoriaActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService,
  ) {
    super(actionService.FRAGMENT.ENTIDADES_CONVOCANTES, actionService);
    this.formPart = this.fragment as ConvocatoriaEntidadesConvocantesFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.data$.subscribe(
      (data) => {
        this.dataSource.data = data;
      })
    );
  }

  private setupI18N(): void {
    this.translate.get(
      CONVOCATORIA_ENTIDAD_CONVOCANTE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      CONVOCATORIA_ENTIDAD_CONVOCANTE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoDeactivate = value);


  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  openModal(value?: ConvocatoriaEntidadConvocanteData): void {
    const data: ConvocatoriaEntidadConvocanteModalData = {
      entidadConvocanteData: value,
      selectedEmpresas: this.dataSource.data.map((convocanteData) => convocanteData.empresa),
      readonly: this.formPart.isConvocatoriaVinculada
    };

    const config = {
      data
    };
    const dialogRef = this.matDialog.open(ConvocatoriaEntidadConvocanteModalComponent, config);
    dialogRef.afterClosed().subscribe((entidadConvocante: ConvocatoriaEntidadConvocanteModalData) => {
      if (entidadConvocante) {
        if (value) {
          this.formPart.updateConvocatoriaEntidadConvocante(entidadConvocante.entidadConvocanteData);
        } else {
          this.formPart.addConvocatoriaEntidadConvocante(entidadConvocante.entidadConvocanteData);
        }
      }
    });
  }

  deleteConvocatoriaEntidadConvocante(data: ConvocatoriaEntidadConvocanteData) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDeactivate).subscribe(
        (aceptado: boolean) => {
          if (aceptado) {
            this.formPart.deleteConvocatoriaEntidadConvocante(data);
          }
        }
      )
    );
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }
}
