import { IInvencion } from '@core/models/pii/invencion';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Data } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { SolicitudProteccionPaisesValidadosComponent } from './solicitud-proteccion-paises-validados.component';
import { ISolicitudProteccionData, SolicitudProteccionActionService } from '../../../solicitud-proteccion.action.service';
import { SOLICITUD_PROTECCION_DATA_KEY } from '../../../solicitud-proteccion-data.resolver';


describe('SolicitudProteccionPaisesValidadosComponent', () => {
  let component: SolicitudProteccionPaisesValidadosComponent;
  let fixture: ComponentFixture<SolicitudProteccionPaisesValidadosComponent>;

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
      declarations: [SolicitudProteccionPaisesValidadosComponent],
      imports: [
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        FlexModule,
        FormsModule,
        LoggerTestingModule,
        RouterTestingModule,
        ReactiveFormsModule,
        SharedModule
      ],
      providers: [
        SolicitudProteccionActionService,
        { provide: ActivatedRoute, useValue: routeMock }
      ],
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SolicitudProteccionPaisesValidadosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
