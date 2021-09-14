import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConceptoGasto } from '@core/models/csp/concepto-gasto';
import { IConvocatoriaConceptoGasto } from '@core/models/csp/convocatoria-concepto-gasto';
import { IConvocatoriaConceptoGastoCodigoEc } from '@core/models/csp/convocatoria-concepto-gasto-codigo-ec';
import { IPartidaGasto } from '@core/models/csp/partida-gasto';
import { ISolicitudProyectoPresupuesto } from '@core/models/csp/solicitud-proyecto-presupuesto';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ConceptoGastoService } from '@core/services/csp/concepto-gasto.service';
import { ConvocatoriaConceptoGastoService } from '@core/services/csp/convocatoria-concepto-gasto.service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { merge, Observable } from 'rxjs';
import { map, mergeMap, switchMap, tap } from 'rxjs/operators';

export interface ConvocatoriaConceptoGastoCodigoEc extends IConvocatoriaConceptoGastoCodigoEc {
  convocatoriaConceptoGasto: IConvocatoriaConceptoGasto;
}

export interface PartidaGastoDataModal {
  partidaGasto: ISolicitudProyectoPresupuesto;
  convocatoriaId: number;
  readonly: boolean;
}

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const SOLICITUD_PROYECTO_PRESUPUESTO_PARTIDA_GASTO = marker('csp.partida-gasto');
const SOLICITUD_PROYECTO_PRESUPUESTO_PARTIDA_CONCEPTO_GASTO = marker('csp.solicitud-proyecto-presupuesto.concepto-gasto');
const SOLICITUD_PROYECTO_PRESUPUESTO_PARTIDA_OBSERVACIONES =
  marker('csp.solicitud-proyecto-presupuesto.observaciones');
const SOLICITUD_PROYECTO_PRESUPUESTO_PARTIDA_IMPORTE_SOLICITADO =
  marker('csp.solicitud-proyecto-presupuesto.importe-solicitado');
const SOLICITUD_PROYECTO_PRESUPUESTO_PARTIDA_IMPORTE_PRESUPUESTADO =
  marker('csp.solicitud-proyecto-presupuesto.importe-presupuestado');
const TITLE_NEW_ENTITY = marker('title.new.entity');

@Component({
  templateUrl: './partida-gasto-modal.component.html',
  styleUrls: ['./partida-gasto-modal.component.scss']
})
export class PartidaGastoModalComponent extends
  BaseModalComponent<IPartidaGasto, PartidaGastoDataModal> implements OnInit {

  conceptosGasto$: Observable<IConceptoGasto[]>;
  private conceptosGastoCodigoEcPermitidos: ConvocatoriaConceptoGastoCodigoEc[] = [];
  private conceptosGastoCodigoEcNoPermitidos: ConvocatoriaConceptoGastoCodigoEc[] = [];

  dataSourceCodigosEconomicosPermitidos = new MatTableDataSource<ConvocatoriaConceptoGastoCodigoEc>();
  dataSourceCodigosEconomicosNoPermitidos = new MatTableDataSource<ConvocatoriaConceptoGastoCodigoEc>();
  columnsCodigosEconomicosPermitidos = ['conceptoGasto', 'importeMaximo', 'permitidoDesde', 'permitidoHasta', 'codigoEconomico'];
  columnsCodigosEconomicosNoPermitidos = ['conceptoGasto', 'noPermitidoDesde', 'noPermitidoHasta', 'codigoEconomico'];
  @ViewChild('sortCodigosEconomicosPermitidos') sortCodigosEconomicosPermitidos: MatSort;
  @ViewChild('sortCodigosEconomicosNoPermitidos') sortCodigosEconomicosNoPermitidos: MatSort;

  showCodigosEconomicosInfo = false;

  fxFlexProperties: FxFlexProperties;
  fxFlexPropertiesInline: FxFlexProperties;
  fxFlexPropertiesCodigosEconomicos: FxFlexProperties;
  textSaveOrUpdate: string;
  title: string;

  msgParaConceptoGastoEntity = {};
  msgParamObservacionesEntity = {};
  msgParamImporteEntity = {};
  msgParamImportePresupuestadoEntity = {};

  constructor(
    protected snackBarService: SnackBarService,
    public matDialogRef: MatDialogRef<PartidaGastoDataModal>,
    @Inject(MAT_DIALOG_DATA) public data: PartidaGastoDataModal,
    private conceptoGastoService: ConceptoGastoService,
    private convocatoriaService: ConvocatoriaService,
    private convocatoriaConceptoGastoService: ConvocatoriaConceptoGastoService,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, data.partidaGasto);

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(15%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxFlexPropertiesCodigosEconomicos = new FxFlexProperties();
    this.fxFlexPropertiesCodigosEconomicos.sm = '0 1 calc(50%-10px)';
    this.fxFlexPropertiesCodigosEconomicos.md = '0 1 calc(50%-10px)';
    this.fxFlexPropertiesCodigosEconomicos.gtMd = '0 1 calc(50%-10px)';
    this.fxFlexPropertiesCodigosEconomicos.order = '2';

    this.fxFlexPropertiesInline = new FxFlexProperties();
    this.fxFlexPropertiesInline.sm = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.md = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.order = '4';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';

    this.conceptosGasto$ = this.conceptoGastoService.findAll().pipe(
      map((conceptosGasto) => conceptosGasto.items)
    );
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    if (this.data.convocatoriaId) {
      this.subscriptions.push(
        this.getConvocatoriaConceptoGasto(this.data.convocatoriaId).pipe(
          mergeMap(concovariaGastosMap => merge(
            this.getConvocatoriaConceptoGastoCodigoEcPermitidos(this.data.convocatoriaId, concovariaGastosMap).pipe(
              tap(result => this.conceptosGastoCodigoEcPermitidos = result)
            ),
            this.getConvocatoriaConceptoGastoCodigoEcNoPermitidos(this.data.convocatoriaId, concovariaGastosMap).pipe(
              tap(result => this.conceptosGastoCodigoEcNoPermitidos = result)
            )
          )
          )
        ).subscribe()
      );

      this.subscriptions.push(this.formGroup.controls.conceptoGasto.valueChanges.subscribe(
        (conceptoGasto) => {
          this.dataSourceCodigosEconomicosPermitidos.data = this.conceptosGastoCodigoEcPermitidos
            .filter(codigoEconomico => conceptoGasto.id === codigoEconomico.convocatoriaConceptoGasto?.conceptoGasto?.id);
          this.dataSourceCodigosEconomicosNoPermitidos.data = this.conceptosGastoCodigoEcNoPermitidos
            .filter(codigoEconomico => conceptoGasto.id === codigoEconomico.convocatoriaConceptoGasto?.conceptoGasto?.id);

          this.showCodigosEconomicosInfo = this.dataSourceCodigosEconomicosPermitidos.data.length > 0
            || this.dataSourceCodigosEconomicosNoPermitidos.data.length > 0;

          if (this.showCodigosEconomicosInfo) {
            setTimeout(() => {
              this.setTablesSort();
            }, 0);
          }
        })
      );
    }

    this.textSaveOrUpdate = this.data.partidaGasto?.conceptoGasto ? MSG_ACEPTAR : MSG_ANADIR;
  }

  private setupI18N(): void {
    if (this.data.partidaGasto.conceptoGasto) {
      this.translate.get(
        SOLICITUD_PROYECTO_PRESUPUESTO_PARTIDA_GASTO,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        SOLICITUD_PROYECTO_PRESUPUESTO_PARTIDA_GASTO,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).pipe(
        switchMap((value) => {
          return this.translate.get(
            TITLE_NEW_ENTITY,
            { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
          );
        })
      ).subscribe((value) => this.title = value);
    }

    this.translate.get(
      SOLICITUD_PROYECTO_PRESUPUESTO_PARTIDA_CONCEPTO_GASTO,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParaConceptoGastoEntity =
      { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITUD_PROYECTO_PRESUPUESTO_PARTIDA_OBSERVACIONES,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamObservacionesEntity =
      { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });

    this.translate.get(
      SOLICITUD_PROYECTO_PRESUPUESTO_PARTIDA_IMPORTE_SOLICITADO,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamImporteEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITUD_PROYECTO_PRESUPUESTO_PARTIDA_IMPORTE_PRESUPUESTADO,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamImportePresupuestadoEntity =
      { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
  }

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup(
      {
        conceptoGasto: new FormControl(
          {
            value: this.data.partidaGasto.conceptoGasto,
            disabled: this.data.partidaGasto.conceptoGasto || this.data.readonly
          },
          [
            Validators.required
          ]
        ),
        anualidad: new FormControl(this.data.partidaGasto.anualidad,
          [
            Validators.min(0),
            Validators.max(2_147_483_647)
          ]),
        importeSolicitado: new FormControl(this.data.partidaGasto.importeSolicitado,
          [
            Validators.required,
            Validators.min(0),
            Validators.max(2_147_483_647)
          ]),
        importePresupuestado: new FormControl(this.data.partidaGasto.importePresupuestado,
          [
            Validators.required,
            Validators.min(0),
            Validators.max(2_147_483_647)
          ]),
        observaciones: new FormControl(this.data.partidaGasto.observaciones,
          [
            Validators.maxLength(2000)
          ])
      }
    );

    if (this.data.readonly) {
      formGroup.disable();
    }
    return formGroup;
  }

  protected getDatosForm(): IPartidaGasto {
    const entidad = this.data.partidaGasto;
    entidad.conceptoGasto = this.formGroup.controls.conceptoGasto.value;
    entidad.anualidad = this.formGroup.controls.anualidad.value;
    entidad.importeSolicitado = this.formGroup.controls.importeSolicitado.value;
    entidad.importePresupuestado = this.formGroup.controls.importePresupuestado.value;
    entidad.observaciones = this.formGroup.controls.observaciones.value;
    return entidad;
  }

  private getConvocatoriaConceptoGasto(convocatoriaId: number): Observable<Map<number, IConvocatoriaConceptoGasto>> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('convocatoria.id', SgiRestFilterOperator.EQUALS, convocatoriaId.toString())
    };
    return this.convocatoriaConceptoGastoService.findAll(options).pipe(
      map(response => {
        return new Map(
          response.items.map(value => [value.id, value])
        );
      })
    );
  }

  /**
   * Carga todos los ConceptoGastoCodigoEc permitidos de la convocatoria
   *
   * @param convocatoriaId Id de la convocatoria
   */
  private getConvocatoriaConceptoGastoCodigoEcPermitidos(
    convocatoriaId: number,
    convocatoriaConceptoGastoMap: Map<number, IConvocatoriaConceptoGasto>
  ): Observable<ConvocatoriaConceptoGastoCodigoEc[]> {
    return this.convocatoriaService.findAllConvocatoriaConceptoGastoCodigoEcsPermitidos(convocatoriaId).pipe(
      map(conceptoGatosCodigoEc => {
        return conceptoGatosCodigoEc.items.map(conceptoGasto => {
          const data: ConvocatoriaConceptoGastoCodigoEc = {
            codigoEconomico: conceptoGasto.codigoEconomico,
            convocatoriaConceptoGastoId: conceptoGasto.convocatoriaConceptoGastoId,
            convocatoriaConceptoGasto: convocatoriaConceptoGastoMap.get(conceptoGasto.convocatoriaConceptoGastoId),
            fechaFin: conceptoGasto.fechaFin,
            fechaInicio: conceptoGasto.fechaInicio,
            id: conceptoGasto.id,
            observaciones: conceptoGasto.observaciones
          };
          return data;
        });
      })
    );
  }

  /**
   * Carga todos los ConceptoGastoCodigoEc no permitidos de la convocatoria
   *
   * @param convocatoriaId Id de la convocatoria
   */
  private getConvocatoriaConceptoGastoCodigoEcNoPermitidos(
    convocatoriaId: number,
    convocatoriaConceptoGastoMap: Map<number, IConvocatoriaConceptoGasto>
  ): Observable<ConvocatoriaConceptoGastoCodigoEc[]> {
    return this.convocatoriaService.findAllConvocatoriaConceptoGastoCodigoEcsNoPermitidos(convocatoriaId).pipe(
      map(conceptoGatosCodigoEc => {
        return conceptoGatosCodigoEc.items.map(conceptoGasto => {
          const data: ConvocatoriaConceptoGastoCodigoEc = {
            codigoEconomico: conceptoGasto.codigoEconomico,
            convocatoriaConceptoGastoId: conceptoGasto.convocatoriaConceptoGastoId,
            convocatoriaConceptoGasto: convocatoriaConceptoGastoMap.get(conceptoGasto.convocatoriaConceptoGastoId),
            fechaFin: conceptoGasto.fechaFin,
            fechaInicio: conceptoGasto.fechaInicio,
            id: conceptoGasto.id,
            observaciones: conceptoGasto.observaciones
          };
          return data;
        });
      })
    );
  }

  private setTablesSort() {
    this.dataSourceCodigosEconomicosPermitidos.sortingDataAccessor =
      (conceptoGastoCodigoEc: ConvocatoriaConceptoGastoCodigoEc, property: string) => {
        switch (property) {
          case 'conceptoGasto':
            return conceptoGastoCodigoEc.convocatoriaConceptoGasto.conceptoGasto.nombre;
          case 'importeMaximo':
            return conceptoGastoCodigoEc.convocatoriaConceptoGasto.importeMaximo;
          case 'permitidoDesde':
            return conceptoGastoCodigoEc.convocatoriaConceptoGasto.mesInicial;
          case 'permitidoHasta':
            return conceptoGastoCodigoEc.convocatoriaConceptoGasto.mesFinal;
          case 'codigoEconomico':
            return conceptoGastoCodigoEc.codigoEconomico?.id;
          default:
            return conceptoGastoCodigoEc[property];
        }
      };
    this.dataSourceCodigosEconomicosPermitidos.sort = this.sortCodigosEconomicosPermitidos;
    this.dataSourceCodigosEconomicosNoPermitidos.sortingDataAccessor =
      (conceptoGastoCodigoEc: ConvocatoriaConceptoGastoCodigoEc, property: string) => {
        switch (property) {
          case 'conceptoGasto':
            return conceptoGastoCodigoEc.convocatoriaConceptoGasto.conceptoGasto.nombre;
          case 'noPermitidoDesde':
            return conceptoGastoCodigoEc.convocatoriaConceptoGasto.mesInicial;
          case 'noPermitidoHasta':
            return conceptoGastoCodigoEc.convocatoriaConceptoGasto.mesFinal;
          case 'codigoEconomico':
            return conceptoGastoCodigoEc.codigoEconomico?.id;
          default:
            return conceptoGastoCodigoEc[property];
        }
      };
    this.dataSourceCodigosEconomicosNoPermitidos.sort = this.sortCodigosEconomicosNoPermitidos;
  }

}
