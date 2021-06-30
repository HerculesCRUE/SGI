import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ConvocatoriaEditarComponent } from './convocatoria-editar.component';
import { ConvocatoriaDatosGeneralesComponent } from '../convocatoria-formulario/convocatoria-datos-generales/convocatoria-datos-generales.component';
import { ActionFooterComponent } from '@shared/action-footer/action-footer.component';
import TestUtils from '@core/utils/test-utils';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MaterialDesignModule } from '@material/material-design.module';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { ConvocatoriaActionService } from '../convocatoria.action.service';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';

describe('ConvocatoriaEditarComponent', () => {
  let component: ConvocatoriaEditarComponent;
  let fixture: ComponentFixture<ConvocatoriaEditarComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ConvocatoriaEditarComponent,
        ConvocatoriaDatosGeneralesComponent,
        ActionFooterComponent]
      ,
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
        SgiAuthModule,
      ],
      providers: [
        ConvocatoriaActionService,
        SgiAuthService
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConvocatoriaEditarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
