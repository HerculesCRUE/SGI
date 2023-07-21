import { DecimalPipe } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { IBaseExportModalData } from '@core/component/base-export/base-export-modal-data';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SolicitudEntidadConvocanteListadoExportService } from '../../solicitud-entidad-convocante-listado-export.service';
import { SolicitudFooterListadoExportService } from '../../solicitud-footer-listado-export.service';
import { SolicitudGeneralListadoExportService } from '../../solicitud-general-listado-export.service';
import { SolicitudHeaderListadoExportService } from '../../solicitud-header-listado-export.service';
import { SolicitudListadoExportService } from '../../solicitud-listado-export.service';
import { SolicitudProyectoAreaConocimientoListadoExportService } from '../../solicitud-proyecto-area-conocimiento-listado-export.service';
import { SolicitudProyectoClasificacionListadoExportService } from '../../solicitud-proyecto-clasificacion-listado-export.service';
import { SolicitudProyectoEntidadFinanciadoraListadoExportService } from '../../solicitud-proyecto-entidad-financiadora-listado-export.service';
import { SolicitudProyectoEquipoListadoExportService } from '../../solicitud-proyecto-equipo-listado-export.service';
import { SolicitudProyectoFichaGeneralListadoExportService } from '../../solicitud-proyecto-ficha-general-listado-export.service';
import { SolicitudProyectoResponsableEconomicoListadoExportService } from '../../solicitud-proyecto-responsable-economico-listado-export.service';
import { SolicitudProyectoSocioListadoExportService } from '../../solicitud-proyecto-socio-listado-export.service';
import { SolicitudRrhhListadoExportService } from '../../solicitud-rrhh-listado-export.service';
import { SolicitudListadoExportModalComponent } from './solicitud-listado-export-modal.component';

describe('SolicitudListadoExportModalComponent', () => {
  let component: SolicitudListadoExportModalComponent;
  let fixture: ComponentFixture<SolicitudListadoExportModalComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        SolicitudListadoExportModalComponent
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
        SgiAuthService,
        SolicitudListadoExportService,
        DecimalPipe,
        SolicitudGeneralListadoExportService,
        SolicitudEntidadConvocanteListadoExportService,
        SolicitudProyectoFichaGeneralListadoExportService,
        SolicitudProyectoAreaConocimientoListadoExportService,
        SolicitudProyectoClasificacionListadoExportService,
        SolicitudProyectoEquipoListadoExportService,
        SolicitudProyectoResponsableEconomicoListadoExportService,
        SolicitudProyectoSocioListadoExportService,
        SolicitudProyectoEntidadFinanciadoraListadoExportService,
        SolicitudRrhhListadoExportService,
        SolicitudHeaderListadoExportService,
        SolicitudFooterListadoExportService
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SolicitudListadoExportModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
