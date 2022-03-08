import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { CvnPipesModule } from '../../pipe/cvn-pipes.module';

import { CvnEstadoComponent } from './cvn-estado.component';

describe('CvnEstadoComponent', () => {
  let component: CvnEstadoComponent;
  let fixture: ComponentFixture<CvnEstadoComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        TestUtils.getIdiomas(),
        CvnPipesModule
      ],
      declarations: [CvnEstadoComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CvnEstadoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
