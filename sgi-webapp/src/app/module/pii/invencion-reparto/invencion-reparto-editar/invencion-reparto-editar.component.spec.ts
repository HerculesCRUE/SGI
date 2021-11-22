import { DecimalPipe } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexLayoutModule, FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Data } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthService } from '@sgi/framework/auth';
import { ActionFooterComponent } from '@shared/action-footer/action-footer.component';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { IInvencionData } from '../../invencion/invencion.action.service';
import { INVENCION_DATA_KEY } from '../../invencion/invencion.resolver';
import { INVENCION_REPARTO_DATA_KEY } from '../invencion-reparto-data.resolver';
import { IInvencionRepartoData, InvencionRepartoActionService } from '../invencion-reparto.action.service';
import { InvencionRepartoDataResolverService } from '../services/invencion-reparto-data-resolver.service';

import { InvencionRepartoEditarComponent } from './invencion-reparto-editar.component';

describe('InvencionRepartoEditarComponent', () => {
  let component: InvencionRepartoEditarComponent;
  let fixture: ComponentFixture<InvencionRepartoEditarComponent>;

  const routeData: Data = {
    [INVENCION_REPARTO_DATA_KEY]: {
      reparto: {
        id: undefined
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
        InvencionRepartoEditarComponent,
        ActionFooterComponent
      ],
      imports: [
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        FlexModule,
        FormsModule,
        ReactiveFormsModule,
        RouterTestingModule,
        FlexLayoutModule,
        SharedModule
      ],
      providers: [
        InvencionRepartoActionService,
        SgiAuthService,
        { provide: ActivatedRoute, useValue: routeMock },
        InvencionRepartoDataResolverService,
        DecimalPipe
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InvencionRepartoEditarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
