import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { TravelStore } from '../../data-access/travel.store';
import { TravelCardComponent } from '../../ui/travel-card/travel-card.component';
import { PaginationComponent } from '../../../../shared/ui/pagination/pagination.component';

@Component({
  selector: 'app-travel-catalog-page',
  imports: [FormsModule, TravelCardComponent, PaginationComponent],
  templateUrl: './travel-catalog-page.component.html',
  styleUrl: './travel-catalog-page.component.css',
})
export class TravelCatalogPageComponent {
  readonly store = inject(TravelStore);
  readonly searchInput = signal('');
  private searchTimeout: ReturnType<typeof setTimeout> | null = null;

  constructor() {
    this.store.loadTravels({});
  }

  onSearchChange(value: string): void {
    this.searchInput.set(value);
    if (this.searchTimeout) clearTimeout(this.searchTimeout);
    this.searchTimeout = setTimeout(() => {
      this.store.loadTravels({ search: value, page: 0 });
    }, 300);
  }

  onPageChange(page: number): void {
    this.store.loadTravels({ page });
  }
}
