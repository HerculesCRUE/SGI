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
import { SolicitudCrearGuard } from '../../solicitud-crear/solicitud-crear.guard';
import { SolicitudDataResolver } from '../../solicitud-data.resolver';
import { SolicitudProyectoPresupuestoListadoExportService } from '../../solicitud-proyecto-presupuesto-listado-export.service';
import { ISolicitudProyectoPresupuetoModalData, SolicitudProyectoPresupuestoListadoExportModalComponent } from './solicitud-proyecto-presupuesto-listado-export-modal.component';

describe('SolicitudProyectoPresupuestoListadoExportModalComponent', () => {
  let component: SolicitudProyectoPresupuestoListadoExportModalComponent;
  let fixture: ComponentFixture<SolicitudProyectoPresupuestoListadoExportModalComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        SolicitudProyectoPresupuestoListadoExportModalComponent
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
        { provide: MAT_DIALOG_DATA, useValue: {} as ISolicitudProyectoPresupuetoModalData },
        SgiAuthService,
        SolicitudDataResolver,
        SolicitudCrearGuard,
        DecimalPipe,
        SolicitudProyectoPresupuestoListadoExportService,
        LuxonDatePipe,
        DecimalPipe,
        PercentPipe,
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SolicitudProyectoPresupuestoListadoExportModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
