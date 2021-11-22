import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Data } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { IInvencion } from '@core/models/pii/invencion';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SOLICITUD_PROTECCION_DATA_KEY } from '../../../solicitud-proteccion-data.resolver';
import { ISolicitudProteccionData, SolicitudProteccionActionService } from '../../../solicitud-proteccion.action.service';
import { SolicitudProteccionProcedimientosDocumentosComponent } from './solicitud-proteccion-procedimientos-documentos.component';


describe('SolicitudProteccionProcedimientosDocumentosComponent', () => {
  let component: SolicitudProteccionProcedimientosDocumentosComponent;
  let fixture: ComponentFixture<SolicitudProteccionProcedimientosDocumentosComponent>;

  const routeData: Data = {
    [SOLICITUD_PROTECCION_DATA_KEY]: {
      invencion: {
        tipoProteccion: {}
      } as IInvencion,
      solicitudesProteccion: []
    } as ISolicitudProteccionData
  };

  const routeMock = TestUtils.buildActivatedRouteMock('0', routeData);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SolicitudProteccionProcedimientosDocumentosComponent],
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        TestUtils.getIdiomas(),
        RouterTestingModule,
        FormsModule,
        ReactiveFormsModule,
        SharedModule,
        TranslateModule,
        SgiAuthModule,
      ],
      providers: [
        SolicitudProteccionActionService,
        SgiAuthService,
        { provide: ActivatedRoute, useValue: routeMock }
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SolicitudProteccionProcedimientosDocumentosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
