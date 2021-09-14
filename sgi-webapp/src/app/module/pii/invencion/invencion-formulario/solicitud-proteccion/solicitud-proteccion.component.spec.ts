import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Data } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { SolicitudProteccionService } from '@core/services/pii/invencion/solicitud-proteccion/solicitud-proteccion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { IInvencionData, InvencionActionService } from '../../invencion.action.service';
import { INVENCION_DATA_KEY } from '../../invencion.resolver';

import { SolicitudProteccionComponent } from './solicitud-proteccion.component';

describe('SolicitudProteccionComponent', () => {
  let component: SolicitudProteccionComponent;
  let fixture: ComponentFixture<SolicitudProteccionComponent>;
  const routeData: Data = {
    [INVENCION_DATA_KEY]: {} as IInvencionData
  };
  const routeMock = TestUtils.buildActivatedRouteMock('1', routeData);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SolicitudProteccionComponent],
      imports: [
        RouterTestingModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        BrowserAnimationsModule,
        TestUtils.getIdiomas(),
        FlexLayoutModule,
        ReactiveFormsModule,
        SharedModule,
        SgpSharedModule,
        SgiAuthModule
      ],
      providers: [
        SgiAuthService,
        InvencionService,
        InvencionActionService,
        SolicitudProteccionService,
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        { provide: ActivatedRoute, useValue: routeMock }
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SolicitudProteccionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

});
