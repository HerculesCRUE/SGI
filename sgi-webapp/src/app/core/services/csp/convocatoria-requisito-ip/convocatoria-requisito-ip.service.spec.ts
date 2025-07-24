import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ConvocatoriaRequisitoIPService } from './convocatoria-requisito-ip.service';


describe('ConvocatoriaRequisitoIPService', () => {
  let service: ConvocatoriaRequisitoIPService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ConvocatoriaRequisitoIPService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
