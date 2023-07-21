import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { TipoOrigenFuenteFinanciacionService } from './tipo-origen-fuente-financiacion.service';


describe('TipoOrigenFuenteFinanciacionService', () => {
  let service: TipoOrigenFuenteFinanciacionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(TipoOrigenFuenteFinanciacionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
