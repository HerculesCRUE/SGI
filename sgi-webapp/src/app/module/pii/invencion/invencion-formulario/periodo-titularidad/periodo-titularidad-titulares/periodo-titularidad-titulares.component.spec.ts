import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Data } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { IPeriodoTitularidad } from '@core/models/pii/periodo-titularidad';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthService } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { InvencionActionService } from '../../../invencion.action.service';
import { INVENCION_DATA_KEY } from '../../../invencion.resolver';
import { PeriodoTitularidadTitularesComponent } from './periodo-titularidad-titulares.component';


describe('PeriodoTitularidadTitularesComponent', () => {
  let component: PeriodoTitularidadTitularesComponent;
  let fixture: ComponentFixture<PeriodoTitularidadTitularesComponent>;

  const routeData: Data = {
    [INVENCION_DATA_KEY]: {} as IPeriodoTitularidad
  };
  const routeMock = TestUtils.buildActivatedRouteMock('1', routeData);
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PeriodoTitularidadTitularesComponent],
      imports: [
        SharedModule,
        TestUtils.getIdiomas(),
        BrowserAnimationsModule,
        MaterialDesignModule,
        FlexModule,
        LoggerTestingModule,
        ReactiveFormsModule,
        HttpClientTestingModule,
        RouterTestingModule
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        InvencionActionService,
        SgiAuthService,
        { provide: ActivatedRoute, useValue: routeMock }
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PeriodoTitularidadTitularesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
