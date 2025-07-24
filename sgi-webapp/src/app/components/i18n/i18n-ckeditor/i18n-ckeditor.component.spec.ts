import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { CKEditorModule } from '@ckeditor/ckeditor5-angular';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { I18nCkeditorComponent } from './i18n-ckeditor.component';

describe('CkeditorI18nComponent', () => {
  let fixture: ComponentFixture<I18nCkeditorComponent>;

  beforeEach(waitForAsync(() => {

    TestBed.configureTestingModule({
      declarations: [I18nCkeditorComponent],
      imports: [
        CKEditorModule,
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
      ]
    })
      .compileComponents();

  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(I18nCkeditorComponent);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(fixture).toBeTruthy();
  });
});
