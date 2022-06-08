import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaEntidadFinanciadora } from '@core/models/csp/convocatoria-entidad-financiadora';
import { ISolicitudProyectoEntidadFinanciadoraAjena } from '@core/models/csp/solicitud-proyecto-entidad-financiadora-ajena';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { DialogService } from '@core/services/dialog.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { from, Subscription } from 'rxjs';
import { map, mergeAll, switchMap, take, takeLast } from 'rxjs/operators';
import { EntidadFinanciadoraDataModal, EntidadFinanciadoraModalComponent } from '../../../shared/entidad-financiadora-modal/entidad-financiadora-modal.component';
import { SOLICITUD_ROUTE_NAMES } from '../../solicitud-route-names';
import { SolicitudActionService } from '../../solicitud.action.service';
import { SolicitudProyectoEntidadesFinanciadorasFragment, SolicitudProyectoEntidadFinanciadoraAjenaData } from './solicitud-proyecto-entidades-financiadoras.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const SOLICITUD_PROYECTO_ENTIDAD_FINANCIADORA_KEY = marker('csp.solicitud-entidad-financiadora');
const SOLICITUD_PROYECTO_ENTIDAD_FINANCIADORA_AJENA_KEY = marker('csp.solicitud-entidad-financiadora.ajena');
const SOLICITUD_PROYECTO_ENTIDAD_FINANCIADORA_CONVOCATORIA_KEY = marker('csp.solicitud-entidad-financiadora.convocatoria');
const TITLE_NEW_ENTITY = marker('title.new.entity');

@Component({
  selector: 'sgi-solicitud-proyecto-entidades-financiadoras',
  templateUrl: './solicitud-proyecto-entidades-financiadoras.component.html',
  styleUrls: ['./solicitud-proyecto-entidades-financiadoras.component.scss']
})
export class SolicitudProyectoEntidadesFinanciadorasComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  formPart: SolicitudProyectoEntidadesFinanciadorasFragment;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumnsEntidadesFinanciadorasConvocatoria = [
    'nombre',
    'cif',
    'fuenteFinanciacion',
    'ambito',
    'tipoFinanciacion',
    'porcentajeFinanciacion',
    'importeFinanciacion'
  ];

  displayedColumnsEntidadesFinanciadorasAjenas = [
    'nombre',
    'cif',
    'fuenteFinanciacion',
    'ambito',
    'tipoFinanciacion',
    'porcentajeFinanciacion',
    'importeFinanciacion',
    'acciones'
  ];

  elementosPagina: number[] = [5, 10, 25, 100];

  msgParamEntity = {};
  msgParamAjenaEntity = {};
  msgParamConvocatoriaEntity = {};
  textoDelete: string;
  title: string;
  titleNew: string;

  dataSourceEntidadesFinanciadorasConvocatoria = new MatTableDataSource<IConvocatoriaEntidadFinanciadora>();
  @ViewChild('paginatorEntidadesFinanciadorasConvocatoria', { static: true }) paginatorEntidadesFinanciadorasConvocatoria: MatPaginator;
  @ViewChild('sortEntidadesFinanciadorasConvocatoria', { static: true }) sortEntidadesFinanciadorasConvocatoria: MatSort;

  dataSourceEntidadesFinanciadorasAjenas = new MatTableDataSource<StatusWrapper<SolicitudProyectoEntidadFinanciadoraAjenaData>>();
  @ViewChild('paginatorEntidadesFinanciadorasAjenas', { static: true }) paginatorEntidadesFinanciadorasAjena: MatPaginator;
  @ViewChild('sortEntidadesFinanciadorasAjenas', { static: true }) sortEntidadesFinanciadorasAjena: MatSort;

  constructor(
    private actionService: SolicitudActionService,
    private router: Router,
    private route: ActivatedRoute,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private solicitudService: SolicitudService,
    private empresaService: EmpresaService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.ENTIDADES_FINANCIADORAS, actionService);
    this.formPart = this.fragment as SolicitudProyectoEntidadesFinanciadorasFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.actionService.datosProyectoComplete$.pipe(
      take(1)
    ).subscribe(
      (complete) => {
        if (!complete) {
          this.router.navigate(['../', SOLICITUD_ROUTE_NAMES.PROYECTO_DATOS], { relativeTo: this.route });
        }
      }
    );

    this.dataSourceEntidadesFinanciadorasConvocatoria.paginator = this.paginatorEntidadesFinanciadorasConvocatoria;
    this.dataSourceEntidadesFinanciadorasConvocatoria.sortingDataAccessor =
      (entidadFinanciadora: IConvocatoriaEntidadFinanciadora, property: string) => {
        switch (property) {
          case 'nombre':
            return entidadFinanciadora.empresa?.nombre;
          case 'cif':
            return entidadFinanciadora.empresa?.numeroIdentificacion;
          case 'fuenteFinanciacion':
            return entidadFinanciadora.fuenteFinanciacion?.nombre;
          case 'ambito':
            return entidadFinanciadora.fuenteFinanciacion?.tipoAmbitoGeografico.nombre;
          case 'tipoFinanciacion':
            return entidadFinanciadora.tipoFinanciacion?.nombre;
          case 'porcentajeFinanciacion':
            return entidadFinanciadora.porcentajeFinanciacion;
          case 'importeFinanciacion':
            return entidadFinanciadora.importeFinanciacion;
          default:
            return entidadFinanciadora[property];
        }
      };
    this.dataSourceEntidadesFinanciadorasConvocatoria.sort = this.sortEntidadesFinanciadorasConvocatoria;

    this.dataSourceEntidadesFinanciadorasAjenas.paginator = this.paginatorEntidadesFinanciadorasAjena;
    this.dataSourceEntidadesFinanciadorasAjenas.sortingDataAccessor =
      (entidadFinanciadora: StatusWrapper<SolicitudProyectoEntidadFinanciadoraAjenaData>, property: string) => {
        switch (property) {
          case 'nombre':
            return entidadFinanciadora.value.empresa?.nombre;
          case 'cif':
            return entidadFinanciadora.value.empresa?.numeroIdentificacion;
          case 'fuenteFinanciacion':
            return entidadFinanciadora.value.fuenteFinanciacion?.nombre;
          case 'ambito':
            return entidadFinanciadora.value.fuenteFinanciacion?.tipoAmbitoGeografico.nombre;
          case 'tipoFinanciacion':
            return entidadFinanciadora.value.tipoFinanciacion?.nombre;
          case 'porcentajeFinanciacion':
            return entidadFinanciadora.value.porcentajeFinanciacion;
          default:
            return entidadFinanciadora[property];
        }
      };
    this.dataSourceEntidadesFinanciadorasAjenas.sort = this.sortEntidadesFinanciadorasAjena;

    const subscriptionEntidadesFinanciadorasConvocatoria = this.solicitudService
      .findEntidadesFinanciadorasConvocatoriaSolicitud(this.formPart.getKey() as number)
      .pipe(
        map(result => result.items),
        switchMap((entidadesFinanciadoras) => {
          return from(entidadesFinanciadoras)
            .pipe(
              map((entidadesFinanciadora) => {
                return this.empresaService.findById(entidadesFinanciadora.empresa.id)
                  .pipe(
                    map(empresa => {
                      entidadesFinanciadora.empresa = empresa;
                      return entidadesFinanciadora;
                    }),
                  );

              }),
              mergeAll(),
              map(() => {
                return entidadesFinanciadoras;
              })
            );
        }),
        takeLast(1)
      ).subscribe((result) => {
        this.dataSourceEntidadesFinanciadorasConvocatoria.data = result;
      });

    this.subscriptions.push(subscriptionEntidadesFinanciadorasConvocatoria);

    const subscriptionEntidadesFinanciadorasAjenas = this.formPart.entidadesFinanciadoras$
      .subscribe((entidadesFinanciadoras) => {
        this.dataSourceEntidadesFinanciadorasAjenas.data = entidadesFinanciadoras;
      });
    this.subscriptions.push(subscriptionEntidadesFinanciadorasAjenas);
  }

  private setupI18N(): void {
    this.translate.get(
      SOLICITUD_PROYECTO_ENTIDAD_FINANCIADORA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      SOLICITUD_PROYECTO_ENTIDAD_FINANCIADORA_AJENA_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamAjenaEntity = { entity: value });

    this.translate.get(
      SOLICITUD_PROYECTO_ENTIDAD_FINANCIADORA_CONVOCATORIA_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamConvocatoriaEntity = { entity: value });

    this.translate.get(
      SOLICITUD_PROYECTO_ENTIDAD_FINANCIADORA_KEY,
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
      SOLICITUD_PROYECTO_ENTIDAD_FINANCIADORA_AJENA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          TITLE_NEW_ENTITY,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.titleNew = value);

    this.translate.get(
      SOLICITUD_PROYECTO_ENTIDAD_FINANCIADORA_AJENA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.title = value);
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  openModal(wrapper?: StatusWrapper<SolicitudProyectoEntidadFinanciadoraAjenaData>): void {
    const data: EntidadFinanciadoraDataModal = {
      title: wrapper ? this.title : this.titleNew,
      entidad: wrapper ? wrapper.value : {} as SolicitudProyectoEntidadFinanciadoraAjenaData,
      readonly: this.formPart.readonly
    };

    const config = {
      data
    };

    const dialogRef = this.matDialog.open(EntidadFinanciadoraModalComponent, config);
    dialogRef.afterClosed().subscribe((entidadFinanciadora) => {
      if (entidadFinanciadora) {
        if (!wrapper) {
          this.formPart.addSolicitudProyectoEntidadFinanciadora(entidadFinanciadora);
        } else {
          const entidad = new StatusWrapper<SolicitudProyectoEntidadFinanciadoraAjenaData>(wrapper.value);
          this.formPart.updateSolicitudProyectoEntidadFinanciadora(entidad);
        }
      }
    });
  }

  deleteEntidadFinanciadora(wrapper: StatusWrapper<ISolicitudProyectoEntidadFinanciadoraAjena>) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteSolicitudProyectoEntidadFinanciadora(wrapper);
          }
        }
      )
    );
  }
}
