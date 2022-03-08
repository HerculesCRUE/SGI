import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { RolProyectoColectivoService } from './rol-proyecto-colectivo.service';

describe('RolProyectoColectivoService', () => {
  let service: RolProyectoColectivoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(RolProyectoColectivoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
