import { DecimalPipe } from '@angular/common';
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
import { IInvencionData } from '../../../invencion/invencion.action.service';
import { INVENCION_DATA_KEY } from '../../../invencion/invencion.resolver';
import { INVENCION_REPARTO_DATA_KEY } from '../../invencion-reparto-data.resolver';
import { IInvencionRepartoData, InvencionRepartoActionService } from '../../invencion-reparto.action.service';
import { InvencionRepartoDataResolverService } from '../../services/invencion-reparto-data-resolver.service';

import { InvencionRepartoEquipoInventorComponent } from './invencion-reparto-equipo-inventor.component';

describe('InvencionRepartoEquipoInventorComponent', () => {
  let component: InvencionRepartoEquipoInventorComponent;
  let fixture: ComponentFixture<InvencionRepartoEquipoInventorComponent>;

  const routeData: Data = {
    [INVENCION_REPARTO_DATA_KEY]: {
      reparto: {
        id: 1
      },
    } as IInvencionRepartoData
  };
  const parentRouteData: Data = {
    [INVENCION_DATA_KEY]: {
      invencion: {
        id: 111
      }
    } as IInvencionData
  };
  const routeMock = TestUtils.buildActivatedRouteMock('1', routeData, parentRouteData);

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        InvencionRepartoEquipoInventorComponent
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
        SharedModule
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        { provide: ActivatedRoute, useValue: routeMock },
        InvencionRepartoActionService,
        InvencionRepartoDataResolverService,
        DecimalPipe,
        SgiAuthService
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InvencionRepartoEquipoInventorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
