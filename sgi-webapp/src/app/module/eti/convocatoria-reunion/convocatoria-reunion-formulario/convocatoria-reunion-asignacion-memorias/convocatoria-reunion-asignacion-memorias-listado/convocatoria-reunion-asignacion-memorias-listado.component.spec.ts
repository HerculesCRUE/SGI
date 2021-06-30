import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { ConvocatoriaReunionActionService } from '../../../convocatoria-reunion.action.service';
import { ConvocatoriaReunionAsignacionMemoriasListadoComponent } from './convocatoria-reunion-asignacion-memorias-listado.component';

describe('ConvocatoriaReunionAsignacionMemoriasListadoComponent', () => {
  let component: ConvocatoriaReunionAsignacionMemoriasListadoComponent;
  let fixture: ComponentFixture<ConvocatoriaReunionAsignacionMemoriasListadoComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ConvocatoriaReunionAsignacionMemoriasListadoComponent],
      imports: [
        TestUtils.getIdiomas(),
        FormsModule,
        ReactiveFormsModule,
        MaterialDesignModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        FlexModule,
        RouterTestingModule
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        ConvocatoriaReunionActionService
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConvocatoriaReunionAsignacionMemoriasListadoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
