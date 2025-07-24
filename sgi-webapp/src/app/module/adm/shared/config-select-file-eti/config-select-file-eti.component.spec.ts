import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';
import { AdmSharedModule } from '../adm-shared.module';
import { ConfigSelectFileEtiComponent } from './config-select-file-eti.component';

describe('ConfigSelectFileEtiComponent', () => {
  let component: ConfigSelectFileEtiComponent;
  let fixture: ComponentFixture<ConfigSelectFileEtiComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        HttpClientTestingModule,
        MaterialDesignModule,
        TestUtils.getIdiomas(),
        SharedModule,
        AdmSharedModule
      ],
      declarations: [ConfigSelectFileEtiComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfigSelectFileEtiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
