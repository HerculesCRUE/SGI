import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { EntidadFinanciadoraDataModal, EntidadFinanciadoraModalComponent } from '../../../shared/entidad-financiadora-modal/entidad-financiadora-modal.component';
import { SolicitiudPresupuestoModalComponent, SolicitudPresupuestoModalData } from '../../../shared/solicitud-presupuesto-modal/solicitud-presupuesto-modal.component';
import { ProyectoActionService } from '../../proyecto.action.service';
import { IEntidadFinanciadora, ProyectoEntidadesFinanciadorasFragment } from './proyecto-entidades-financiadoras.fragment';

const MSG_NUEVO = marker('title.new.entity');
const MSG_DELETE = marker('msg.delete.entity');
const PROYECTO_ENTIDAD_FINANCIADORA_KEY = marker('csp.proyecto-entidad-financiadora');
const PROYECTO_ENTIDAD_FINANCIADORA_AJENA_KEY = marker('csp.proyecto-entidad-financiadora-ajena');

@Component({
  selector: 'sgi-proyecto-entidades-financiadoras',
  templateUrl: './proyecto-entidades-financiadoras.component.html',
  styleUrls: ['./proyecto-entidades-financiadoras.component.scss']
})
export class ProyectoEntidadesFinanciadorasComponent extends FragmentComponent implements OnInit, OnDestroy {
  formPart: ProyectoEntidadesFinanciadorasFragment;
  private subscriptions: Subscription[] = [];

  private columns = ['nombre', 'cif', 'fuenteFinanciacion', 'ambito', 'tipoFinanciacion',
    'porcentajeFinanciacion', 'importeFinanciacion', 'acciones'];
  private elementsPage = [5, 10, 25, 100];

  msgParamEntity = {};
  msgParamEntityAjena = {};
  private msgCrear: string;
  private msgCrearAjena: string;
  private modalTitle: string;
  private modalTitleAjena: string;
  textoDeleteEntidadPropia: string;
  textoDeleteEntidadAjena: string;

  columnsPropias = [...this.columns];
  columnsAjenas = [...this.columns];
  elementsPagePropias = [...this.elementsPage];
  elementsPageAjenas = [...this.elementsPage];

  dataSourcePropias = new MatTableDataSource<StatusWrapper<IEntidadFinanciadora>>();
  dataSourceAjenas = new MatTableDataSource<StatusWrapper<IEntidadFinanciadora>>();
  @ViewChild('paginatorPropias', { static: true }) paginatorPropias: MatPaginator;
  @ViewChild('sortPropias', { static: true }) sortPropias: MatSort;
  @ViewChild('paginatorAjenas', { static: true }) paginatorAjenas: MatPaginator;
  @ViewChild('sortAjenas', { static: true }) sortAjenas: MatSort;

  constructor(
    protected actionService: ProyectoActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.ENTIDADES_FINANCIADORAS, actionService);
    this.formPart = this.fragment as ProyectoEntidadesFinanciadorasFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.dataSourcePropias.paginator = this.paginatorPropias;
    this.dataSourcePropias.sort = this.sortPropias;
    this.dataSourceAjenas.paginator = this.paginatorAjenas;
    this.dataSourceAjenas.sort = this.sortAjenas;
    this.subscriptions.push(
      this.formPart.entidadesPropias$.subscribe((elements) => this.dataSourcePropias.data = elements)
    );
    this.subscriptions.push(
      this.formPart.entidadesAjenas$.subscribe((elements) => this.dataSourceAjenas.data = elements)
    );
  }

  private setupI18N(): void {
    this.translate.get(
      PROYECTO_ENTIDAD_FINANCIADORA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      PROYECTO_ENTIDAD_FINANCIADORA_AJENA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntityAjena = { entity: value });

    this.translate.get(
      PROYECTO_ENTIDAD_FINANCIADORA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.modalTitle = value);

    this.translate.get(
      PROYECTO_ENTIDAD_FINANCIADORA_AJENA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.modalTitleAjena = value);

    this.translate.get(
      PROYECTO_ENTIDAD_FINANCIADORA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_NUEVO,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.msgCrear = value);

    this.translate.get(
      PROYECTO_ENTIDAD_FINANCIADORA_AJENA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_NUEVO,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.msgCrearAjena = value);

    this.translate.get(
      PROYECTO_ENTIDAD_FINANCIADORA_AJENA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoDeleteEntidadAjena = value);

    this.translate.get(
      PROYECTO_ENTIDAD_FINANCIADORA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoDeleteEntidadPropia = value);
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  openModal(targetPropias: boolean, wrapper?: StatusWrapper<IEntidadFinanciadora>): void {
    const data: EntidadFinanciadoraDataModal = {
      title: targetPropias
        ? (wrapper == null ? this.msgCrear : this.modalTitle)
        : (wrapper == null ? this.msgCrearAjena : this.modalTitleAjena),
      entidad: wrapper ? wrapper.value : {} as IEntidadFinanciadora,
      readonly: this.formPart.readonly
    };
    const config = {
      data
    };
    const dialogRef = this.matDialog.open(EntidadFinanciadoraModalComponent, config);
    dialogRef.afterClosed().subscribe(entidadFinanciadora => {
      if (entidadFinanciadora) {
        if (!wrapper) {
          this.formPart.addEntidadFinanciadora(entidadFinanciadora, targetPropias);
        } else if (!wrapper.created) {
          const entidad = new StatusWrapper<IEntidadFinanciadora>(wrapper.value);
          this.formPart.updateEntidadFinanciadora(entidad, targetPropias);
        }
      }
    });
  }

  deleteEntidadFinanciadora(targetPropias: boolean, wrapper: StatusWrapper<IEntidadFinanciadora>) {
    const msg = targetPropias ? this.textoDeleteEntidadPropia : this.textoDeleteEntidadAjena;
    this.subscriptions.push(
      this.dialogService.showConfirmation(msg).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteEntidadFinanciadora(wrapper, targetPropias);
          }
        }
      )
    );
  }

  showPresupuesto(wrapper: StatusWrapper<IEntidadFinanciadora>) {
    const data: SolicitudPresupuestoModalData = {
      idSolicitudProyecto: this.formPart.solicitudId,
      entidadId: wrapper.value.empresa.id,
      presupuestos: [],
      global: false
    };
    const config = {
      data
    };
    this.matDialog.open(SolicitiudPresupuestoModalComponent, config);
  }
}
