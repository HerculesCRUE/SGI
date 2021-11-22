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
import { ActionFooterComponent } from '@shared/action-footer/action-footer.component';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { IInvencionData } from '../../invencion/invencion.action.service';
import { INVENCION_DATA_KEY } from '../../invencion/invencion.resolver';
import { INVENCION_REPARTO_DATA_KEY } from '../invencion-reparto-data.resolver';
import { InvencionRepartoDatosGeneralesComponent } from '../invencion-reparto-formulario/invencion-reparto-datos-generales/invencion-reparto-datos-generales.component';
import { IInvencionRepartoData, InvencionRepartoActionService } from '../invencion-reparto.action.service';
import { InvencionRepartoDataResolverService } from '../services/invencion-reparto-data-resolver.service';

import { InvencionRepartoCrearComponent } from './invencion-reparto-crear.component';

describe('InvencionRepartoCrearComponent', () => {
  let component: InvencionRepartoCrearComponent;
  let fixture: ComponentFixture<InvencionRepartoCrearComponent>;

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
        InvencionRepartoCrearComponent,
        InvencionRepartoDatosGeneralesComponent,
        ActionFooterComponent
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
        FlexLayoutModule,
        SharedModule,
        LoggerTestingModule
      ],
      providers: [
        InvencionRepartoActionService,
        { provide: ActivatedRoute, useValue: routeMock },
        InvencionRepartoDataResolverService,
        DecimalPipe
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InvencionRepartoCrearComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
