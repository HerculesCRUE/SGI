import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { PeticionEvaluacionListadoInvComponent } from './peticion-evaluacion-listado-inv.component';
import { RouterTestingModule } from '@angular/router/testing';
import { MaterialDesignModule } from '@material/material-design.module';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import TestUtils from '@core/utils/test-utils';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { FlexLayoutModule } from '@angular/flex-layout';

describe('PeticionEvaluacionListadoInvComponent', () => {
  let component: PeticionEvaluacionListadoInvComponent;
  let fixture: ComponentFixture<PeticionEvaluacionListadoInvComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        BrowserAnimationsModule,
        TestUtils.getIdiomas(),
        FlexLayoutModule,
        FormsModule,
        ReactiveFormsModule,
        SharedModule
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() }
      ],
      declarations: [PeticionEvaluacionListadoInvComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PeticionEvaluacionListadoInvComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
