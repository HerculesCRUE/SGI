import { DecimalPipe, PercentPipe } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { LuxonDatePipe } from '@shared/luxon-date-pipe';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ConvocatoriaAreaTematicaListadoExportService } from '../../convocatoria-area-tematica-listado-export.service';
import { ConvocatoriaCalendarioJustificacionListadoExportService } from '../../convocatoria-calendario-justificacion-listado-export.service';
import { ConvocatoriaConceptoGastoListadoExportService } from '../../convocatoria-concepto-gasto-listado-export.service';
import { ConvocatoriaConfiguracionSolicitudListadoExportService } from '../../convocatoria-configuracion-solicitud-listado-export.service';
import { ConvocatoriaEnlaceListadoExportService } from '../../convocatoria-enlace-listado-export.service';
import { ConvocatoriaEntidadConvocanteListadoExportService } from '../../convocatoria-entidad-convocante-listado-export.service';
import { ConvocatoriaEntidadFinanciadoraListadoExportService } from '../../convocatoria-entidad-financiadora-listado-export.service';
import { ConvocatoriaFaseListadoExportService } from '../../convocatoria-fase-listado-export.service';
import { ConvocatoriaGeneralListadoExportService } from '../../convocatoria-general-listado-export.service';
import { ConvocatoriaHitoListadoExportService } from '../../convocatoria-hito-listado-export.service';
import { ConvocatoriaListadoExportService } from '../../convocatoria-listado-export.service';
import { ConvocatoriaPartidaPresupuestariaListadoExportService } from '../../convocatoria-partida-presupuestaria-listado-export.service';
import { ConvocatoriaPeriodoSeguimientoListadoExportService } from '../../convocatoria-periodo-seguimiento-listado-export.service';
import { ConvocatoriaRequisitoEquipoListadoExportService } from '../../convocatoria-requisito-equipo-listado-export.service';
import { ConvocatoriaRequisitoIPListadoExportService } from '../../convocatoria-requisito-ip-listado-export.service';
import { ConvocatoriaListadoExportModalComponent, IConvocatoriaListadoModalData } from './convocatoria-listado-export-modal.component';

describe('ConvocatoriaListadoExportModalComponent', () => {
  let component: ConvocatoriaListadoExportModalComponent;
  let fixture: ComponentFixture<ConvocatoriaListadoExportModalComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        ConvocatoriaListadoExportModalComponent
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
        { provide: MAT_DIALOG_DATA, useValue: {} as IConvocatoriaListadoModalData },
        SgiAuthService,
        ConvocatoriaListadoExportService,
        ConvocatoriaGeneralListadoExportService,
        ConvocatoriaAreaTematicaListadoExportService,
        ConvocatoriaEntidadConvocanteListadoExportService,
        ConvocatoriaEntidadFinanciadoraListadoExportService,
        ConvocatoriaEnlaceListadoExportService,
        ConvocatoriaFaseListadoExportService,
        ConvocatoriaCalendarioJustificacionListadoExportService,
        ConvocatoriaPeriodoSeguimientoListadoExportService,
        ConvocatoriaHitoListadoExportService,
        ConvocatoriaRequisitoIPListadoExportService,
        ConvocatoriaRequisitoEquipoListadoExportService,
        ConvocatoriaConceptoGastoListadoExportService,
        ConvocatoriaPartidaPresupuestariaListadoExportService,
        ConvocatoriaConfiguracionSolicitudListadoExportService,
        LuxonDatePipe,
        DecimalPipe,
        PercentPipe,
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConvocatoriaListadoExportModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
