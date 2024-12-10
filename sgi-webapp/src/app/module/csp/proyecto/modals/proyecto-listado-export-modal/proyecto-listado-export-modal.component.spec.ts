import { DecimalPipe, PercentPipe } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { IBaseExportModalData } from '@core/component/base-export/base-export-modal-data';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { LuxonDatePipe } from '@shared/luxon-date-pipe';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ProyectoAreaConocimientoListadoExportService } from '../../proyecto-area-conocimiento-listado-export.service';
import { ProyectoCalendarioFacturacionListadoExportService } from '../../proyecto-calendario-facturacion-listado-export.service';
import { ProyectoCalendarioJustificacionListadoExportService } from '../../proyecto-calendario-justificacion-listado-export.service';
import { ProyectoClasificacionListadoExportService } from '../../proyecto-clasificacion-listado-export.service';
import { ProyectoConceptoGastoListadoExportService } from '../../proyecto-concepto-gasto-listado-export.service';
import { ProyectoConvocatoriaListadoExportService } from '../../proyecto-convocatoria-listado-export.service';
import { ProyectoEntidadConvocanteListadoExportService } from '../../proyecto-entidad-convocante-listado-export.service';
import { ProyectoEntidadFinanciadoraListadoExportService } from '../../proyecto-entidad-financiadora-listado-export.service';
import { ProyectoEntidadGestoraListadoExportService } from '../../proyecto-entidad-gestora-listado-export.service';
import { ProyectoEquipoListadoExportService } from '../../proyecto-equipo-listado-export.service';
import { ProyectoFooterListadoExportService } from '../../proyecto-footer-listado-export.service';
import { ProyectoGeneralListadoExportService } from '../../proyecto-general-listado-export.service';
import { ProyectoGruposInvestigacionIpListadoExportService } from '../../proyecto-grupos-investigacion-ips-listado-export.service';
import { ProyectoHeaderListadoExportService } from '../../proyecto-header-listado-export.service';
import { ProyectoListadoExportService } from '../../proyecto-listado-export.service';
import { ProyectoPartidaPresupuestariaListadoExportService } from '../../proyecto-partida-presupuestaria-listado-export.service';
import { ProyectoPeriodoSeguimientoListadoExportService } from '../../proyecto-periodo-seguimiento-listado-export.service';
import { ProyectoPresupuestoListadoExportService } from '../../proyecto-presupuesto-listado-export.service';
import { ProyectoProrrogaListadoExportService } from '../../proyecto-prorroga-listado-export.service';
import { ProyectoRelacionListadoExportService } from '../../proyecto-relacion-listado-export.service';
import { ProyectoResponsableEconomicoListadoExportService } from '../../proyecto-responsable-economico-listado-export.service';
import { ProyectoSocioListadoExportService } from '../../proyecto-socio-listado-export.service';
import { ProyectoSolicitudListadoExportService } from '../../proyecto-solicitud-listado-export.service';
import { ProyectoListadoExportModalComponent } from './proyecto-listado-export-modal.component';

describe('ProyectoListadoExportModalComponent', () => {
  let component: ProyectoListadoExportModalComponent;
  let fixture: ComponentFixture<ProyectoListadoExportModalComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        ProyectoListadoExportModalComponent
      ],
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        BrowserAnimationsModule,
        TranslateModule,
        TestUtils.getIdiomas(),
        FlexLayoutModule,
        FormsModule,
        ReactiveFormsModule,
        LoggerTestingModule,
        SharedModule
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        { provide: MatDialogRef, useValue: TestUtils.buildDialogActionMatDialogRef() },
        { provide: MAT_DIALOG_DATA, useValue: {} as IBaseExportModalData },
        DecimalPipe,
        LuxonDatePipe,
        PercentPipe,
        ProyectoAreaConocimientoListadoExportService,
        ProyectoCalendarioFacturacionListadoExportService,
        ProyectoCalendarioJustificacionListadoExportService,
        ProyectoClasificacionListadoExportService,
        ProyectoConceptoGastoListadoExportService,
        ProyectoConvocatoriaListadoExportService,
        ProyectoEntidadConvocanteListadoExportService,
        ProyectoEntidadFinanciadoraListadoExportService,
        ProyectoEntidadGestoraListadoExportService,
        ProyectoEquipoListadoExportService,
        ProyectoFooterListadoExportService,
        ProyectoGeneralListadoExportService,
        ProyectoGruposInvestigacionIpListadoExportService,
        ProyectoHeaderListadoExportService,
        ProyectoListadoExportService,
        ProyectoPartidaPresupuestariaListadoExportService,
        ProyectoPeriodoSeguimientoListadoExportService,
        ProyectoPresupuestoListadoExportService,
        ProyectoProrrogaListadoExportService,
        ProyectoRelacionListadoExportService,
        ProyectoResponsableEconomicoListadoExportService,
        ProyectoSocioListadoExportService,
        ProyectoSolicitudListadoExportService,
        SgiAuthService
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProyectoListadoExportModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
