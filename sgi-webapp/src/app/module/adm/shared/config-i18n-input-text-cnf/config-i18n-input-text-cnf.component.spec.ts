import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';
import { AdmSharedModule } from '../adm-shared.module';
import { ConfigI18nInputTextCnfComponent } from './config-i18n-input-text-cnf.component';

describe('ConfigI18nInputTextCnfComponent', () => {
  let component: ConfigI18nInputTextCnfComponent;
  let fixture: ComponentFixture<ConfigI18nInputTextCnfComponent>;

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
      declarations: [ConfigI18nInputTextCnfComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfigI18nInputTextCnfComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
