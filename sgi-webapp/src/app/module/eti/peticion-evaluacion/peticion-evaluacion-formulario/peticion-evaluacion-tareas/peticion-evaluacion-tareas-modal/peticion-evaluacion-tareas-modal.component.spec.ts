import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { PeticionEvaluacionTareasModalComponent } from './peticion-evaluacion-tareas-modal.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialDesignModule } from '@material/material-design.module';
import TestUtils from '@core/utils/test-utils';
import { RouterTestingModule } from '@angular/router/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SnackBarService } from '@core/services/snack-bar.service';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FlexModule, FlexLayoutModule } from '@angular/flex-layout';
import { SharedModule } from '@shared/shared.module';

describe('PeticionEvaluacionTareasModalComponent', () => {
  let component: PeticionEvaluacionTareasModalComponent;
  let fixture: ComponentFixture<PeticionEvaluacionTareasModalComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [PeticionEvaluacionTareasModalComponent],
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
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        {
          provide: MatDialogRef, useValue: {}
        },
        {
          provide: MAT_DIALOG_DATA, useValue: {}
        },
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PeticionEvaluacionTareasModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
