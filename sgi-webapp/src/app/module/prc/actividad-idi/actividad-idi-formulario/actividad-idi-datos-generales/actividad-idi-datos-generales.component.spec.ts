import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Data } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { PrcSharedModule } from '../../../shared/prc-shared.module';
import { ProduccionCientificaInitializerService } from '../../../shared/produccion-cientifica-initializer.service';
import { IProduccionCientificaData, PRODUCCION_CIENTIFICA_DATA_KEY } from '../../../shared/produccion-cientifica.resolver';
import { ActividadIdiActionService } from '../../actividad-idi.action.service';

import { ActividadIdiDatosGeneralesComponent } from './actividad-idi-datos-generales.component';

describe('ActividadIdiDatosGeneralesComponent', () => {
  let component: ActividadIdiDatosGeneralesComponent;
  let fixture: ComponentFixture<ActividadIdiDatosGeneralesComponent>;

  const routeData: Data = {
    [PRODUCCION_CIENTIFICA_DATA_KEY]: {
      produccionCientifica: {
        id: 1
      }
    } as IProduccionCientificaData
  };
  const routeMock = TestUtils.buildActivatedRouteMock('1', routeData);

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        ActividadIdiDatosGeneralesComponent
      ],
      imports: [
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        FlexModule,
        FormsModule,
        ReactiveFormsModule,
        RouterTestingModule,
        SgiAuthModule,
        LoggerTestingModule,
        SharedModule,
        SgpSharedModule,
        PrcSharedModule
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        ActividadIdiActionService,
        ProduccionCientificaInitializerService,
        SgiAuthService,
        { provide: ActivatedRoute, useValue: routeMock }
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ActividadIdiDatosGeneralesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
