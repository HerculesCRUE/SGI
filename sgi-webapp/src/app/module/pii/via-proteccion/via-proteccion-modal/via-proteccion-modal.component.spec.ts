import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { IViaProteccion } from '@core/models/pii/via-proteccion';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { ViaProteccionModalComponent } from './via-proteccion-modal.component';

describe('ViaProteccionModalComponent', () => {
  let component: ViaProteccionModalComponent;
  let fixture: ComponentFixture<ViaProteccionModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ViaProteccionModalComponent],
      imports: [
        BrowserAnimationsModule,
        TestUtils.getIdiomas(),
        HttpClientTestingModule,
        LoggerTestingModule,
        SharedModule,
        MaterialDesignModule,
        ReactiveFormsModule
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        { provide: MatDialogRef, useValue: {} as IViaProteccion },
        { provide: MAT_DIALOG_DATA, useValue: {} as IViaProteccion }
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ViaProteccionModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
