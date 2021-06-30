import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ConvocatoriaAreaTematicaService } from './convocatoria-area-tematica.service';


describe('ConvocatoriaAreaTematicaService', () => {
  let service: ConvocatoriaAreaTematicaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ConvocatoriaAreaTematicaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
