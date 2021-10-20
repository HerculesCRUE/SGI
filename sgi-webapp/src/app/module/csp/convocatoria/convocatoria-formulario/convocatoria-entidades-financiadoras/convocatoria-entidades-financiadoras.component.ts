import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaEntidadFinanciadora } from '@core/models/csp/convocatoria-entidad-financiadora';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { DialogService } from '@core/services/dialog.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { forkJoin, of, Subscription } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { EntidadFinanciadoraDataModal, EntidadFinanciadoraModalComponent } from '../../../shared/entidad-financiadora-modal/entidad-financiadora-modal.component';
import { ConvocatoriaActionService } from '../../convocatoria.action.service';
import { ConvocatoriaEntidadesFinanciadorasFragment } from './convocatoria-entidades-financiadoras.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const CONVOCATORIA_ENTIDAD_FINANCIADORA_KEY = marker('csp.convocatoria-entidad-financiadora');
const TITLE_NEW_ENTITY = marker('title.new.entity');

@Component({
  selector: 'sgi-convocatoria-entidades-financiadoras',
  templateUrl: './convocatoria-entidades-financiadoras.component.html',
  styleUrls: ['./convocatoria-entidades-financiadoras.component.scss']
})
export class ConvocatoriaEntidadesFinanciadorasComponent extends FragmentComponent implements OnInit, OnDestroy {
  formPart: ConvocatoriaEntidadesFinanciadorasFragment;
  private subscriptions: Subscription[] = [];

  columns = ['nombre', 'cif', 'fuenteFinanciacion', 'ambito', 'tipoFinanciacion',
    'porcentajeFinanciacion', 'importeFinanciacion', 'acciones'];
  elementsPage = [5, 10, 25, 100];

  msgParamEntity = {};
  textoDelete: string;
  textoTitleModalNew: string;
  textoTitleModal: string;

  dataSource = new MatTableDataSource<StatusWrapper<IConvocatoriaEntidadFinanciadora>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  selectedEmpresas: IEmpresa[];

  constructor(
    protected actionService: ConvocatoriaActionService,
    private matDialog: MatDialog,
    private empresaService: EmpresaService,
    private dialogService: DialogService,
    private readonly translate: TranslateService,
  ) {
    super(actionService.FRAGMENT.ENTIDADES_FINANCIADORAS, actionService);
    this.formPart = this.fragment as ConvocatoriaEntidadesFinanciadorasFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.getDataSource();
  }

  private setupI18N(): void {
    this.translate.get(
      CONVOCATORIA_ENTIDAD_FINANCIADORA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      CONVOCATORIA_ENTIDAD_FINANCIADORA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoDelete = value);

    this.translate.get(
      CONVOCATORIA_ENTIDAD_FINANCIADORA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          TITLE_NEW_ENTITY,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoTitleModalNew = value);

    this.translate.get(
      CONVOCATORIA_ENTIDAD_FINANCIADORA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.textoTitleModal = value);
  }

  private getDataSource(): void {
    this.dataSource.data = [];
    this.selectedEmpresas = [];
    this.subscriptions.push(
      this.formPart.convocatoriaEntidadesFinanciadoras$.pipe(
        switchMap(wrappers => {
          return forkJoin(wrappers.map(
            wrapper => {
              return this.empresaService.findById(wrapper.value.empresa.id).pipe(
                map(empresa => {
                  this.selectedEmpresas.push(empresa);
                  wrapper.value.empresa = empresa;
                  return wrapper;
                }),
                catchError(() => {
                  return of(wrapper);
                })
              );
            })
          );
        })
      ).subscribe(elements => {
        this.dataSource.data = elements;
      })
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  openModal(wrapper?: StatusWrapper<IConvocatoriaEntidadFinanciadora>): void {
    const data: EntidadFinanciadoraDataModal = {
      title: wrapper ? this.textoTitleModal : this.textoTitleModalNew,
      entidad: wrapper ? wrapper.value : {} as IConvocatoriaEntidadFinanciadora,
      readonly: this.formPart.isConvocatoriaVinculada
    };
    const config = {
      panelClass: 'sgi-dialog-container',
      data
    };
    const dialogRef = this.matDialog.open(EntidadFinanciadoraModalComponent, config);
    dialogRef.afterClosed().subscribe(entidadFinanciadora => {
      if (entidadFinanciadora) {
        if (!wrapper) {
          this.formPart.addConvocatoriaEntidadFinanciadora(entidadFinanciadora);
        } else if (wrapper.value.id) {
          const entidad = new StatusWrapper<IConvocatoriaEntidadFinanciadora>(wrapper.value);
          this.formPart.updateConvocatoriaEntidadFinanciadora(entidad);
        }
      }
    }
    );
  }

  deleteConvocatoriaEntidadFinanciadora(wrapper: StatusWrapper<IConvocatoriaEntidadFinanciadora>) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado: boolean) => {
          if (aceptado) {
            const empresa = wrapper.value.empresa;
            this.selectedEmpresas = this.selectedEmpresas.filter(x => x.id !== empresa.id);
            const entidad = new StatusWrapper<IConvocatoriaEntidadFinanciadora>(wrapper.value);
            this.formPart.deleteConvocatoriaEntidadFinanciadora(entidad);
            this.getDataSource();
          }
        }
      )
    );
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }
}
