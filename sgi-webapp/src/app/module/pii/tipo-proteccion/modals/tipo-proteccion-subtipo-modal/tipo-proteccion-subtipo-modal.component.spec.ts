import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ITipoProteccion } from '@core/models/pii/tipo-proteccion';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { TipoProteccionActionService } from '../../tipo-proteccion.action.service';
import { TipoProteccionSubtipoModalComponent } from './tipo-proteccion-subtipo-modal.component';


describe('TipoProteccionSubtipoModalComponent', () => {
  let component: TipoProteccionSubtipoModalComponent;
  let fixture: ComponentFixture<TipoProteccionSubtipoModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TipoProteccionSubtipoModalComponent],
      imports: [
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        MatDialogModule,
        FlexModule,
        FormsModule,
        ReactiveFormsModule,
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        { provide: MatDialogRef, useValue: {} as ITipoProteccion },
        { provide: MAT_DIALOG_DATA, useValue: {} as ITipoProteccion },
        TipoProteccionActionService
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TipoProteccionSubtipoModalComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
