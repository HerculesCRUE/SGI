import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { ActionFooterComponent } from '@shared/action-footer/action-footer.component';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ConvocatoriaDatosGeneralesPublicComponent } from '../convocatoria-formulario/convocatoria-datos-generales-public/convocatoria-datos-generales-public.component';
import { ConvocatoriaPublicActionService } from '../convocatoria-public.action.service';
import { ConvocatoriaPublicEditarComponent } from './convocatoria-public-editar.component';

describe('ConvocatoriaPublicEditarComponent', () => {
  let component: ConvocatoriaPublicEditarComponent;
  let fixture: ComponentFixture<ConvocatoriaPublicEditarComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ConvocatoriaPublicEditarComponent,
        ConvocatoriaDatosGeneralesPublicComponent,
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
        ConvocatoriaPublicActionService,
        SgiAuthService
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConvocatoriaPublicEditarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
