import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FlexModule, FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Data } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { IInvencion } from '@core/models/pii/invencion';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthService } from '@sgi/framework/auth';
import { ActionFooterComponent } from '@shared/action-footer/action-footer.component';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SOLICITUD_PROTECCION_DATA_KEY } from '../solicitud-proteccion-data.resolver';
import { ISolicitudProteccionData, SolicitudProteccionActionService } from '../solicitud-proteccion.action.service';

import { SolicitudProteccionEditarComponent } from './solicitud-proteccion-editar.component';

describe('SolicitudProteccionEditarComponent', () => {
  let component: SolicitudProteccionEditarComponent;
  let fixture: ComponentFixture<SolicitudProteccionEditarComponent>;

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
      declarations: [
        SolicitudProteccionEditarComponent,
        ActionFooterComponent
      ],
      imports: [
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        FlexModule,
        ReactiveFormsModule,
        RouterTestingModule,
        FlexLayoutModule,
        SharedModule
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
    fixture = TestBed.createComponent(SolicitudProteccionEditarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
