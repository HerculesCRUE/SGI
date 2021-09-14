import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NgModule } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { Estado } from '@core/models/csp/estado-solicitud';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { Mock } from 'protractor/built/driverProviders';
import { CambioEstadoModalComponent, SolicitudCambioEstadoModalComponentData } from './cambio-estado-modal.component';


describe('CambioEstadoModalComponent', () => {
  let component: CambioEstadoModalComponent;
  let fixture: ComponentFixture<CambioEstadoModalComponent>;

  const newData: SolicitudCambioEstadoModalComponentData = {
    estadoActual: Estado.BORRADOR,
    estadoNuevo: undefined,
    fechaEstado: undefined,
    comentario: undefined,
    isInvestigador: undefined,
  };
  const state = {
    idConvocatoria: 1
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CambioEstadoModalComponent],
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        TestUtils.getIdiomas(),
        LoggerTestingModule,
        ReactiveFormsModule,
        SgiAuthModule,
        SharedModule,
        RouterTestingModule,
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        { provide: MatDialogRef, useValue: newData },
        SgiAuthService,
        { provide: MAT_DIALOG_DATA, useValue: newData },
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    spyOnProperty(history, 'state', 'get').and.returnValue(state);
    fixture = TestBed.createComponent(CambioEstadoModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
