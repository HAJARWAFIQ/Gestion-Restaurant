import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreerCreneauComponent } from './creer-creneau.component';

describe('CreerCreneauComponent', () => {
  let component: CreerCreneauComponent;
  let fixture: ComponentFixture<CreerCreneauComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreerCreneauComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreerCreneauComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
