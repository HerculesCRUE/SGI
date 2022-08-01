import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ConvocatoriaFaseService } from './convocatoria-fase.service';

describe('ConvocatoriaFaseService', () => {
  let service: ConvocatoriaFaseService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule
      ],
      providers: [
      ],
    });
    service = TestBed.inject(ConvocatoriaFaseService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
