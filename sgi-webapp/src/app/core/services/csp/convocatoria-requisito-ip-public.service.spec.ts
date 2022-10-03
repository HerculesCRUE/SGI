import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ConvocatoriaRequisitoIPPublicService } from './convocatoria-requisito-ip-public.service';

describe('ConvocatoriaRequisitoIPPublicService', () => {
  let service: ConvocatoriaRequisitoIPPublicService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ConvocatoriaRequisitoIPPublicService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
