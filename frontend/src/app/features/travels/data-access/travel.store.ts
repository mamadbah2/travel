import { computed, inject } from '@angular/core';
import {
  patchState,
  signalStore,
  withComputed,
  withMethods,
  withState,
} from '@ngrx/signals';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { pipe, switchMap, tap } from 'rxjs';
import { TravelResponse } from '../../../shared/models/api.models';
import { TravelService } from './travel.service';

interface PaginationState {
  page: number;
  size: number;
  totalPages: number;
  totalElements: number;
  first: boolean;
  last: boolean;
}

interface TravelState {
  travels: TravelResponse[];
  selectedTravel: TravelResponse | null;
  pagination: PaginationState;
  searchQuery: string;
  loading: boolean;
  error: string | null;
}

const initialState: TravelState = {
  travels: [],
  selectedTravel: null,
  pagination: { page: 0, size: 12, totalPages: 0, totalElements: 0, first: true, last: true },
  searchQuery: '',
  loading: false,
  error: null,
};

export const TravelStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withComputed((store) => ({
    hasResults: computed(() => store.travels().length > 0),
    isEmpty: computed(() => !store.loading() && store.travels().length === 0),
  })),
  withMethods((store, travelService = inject(TravelService)) => ({
    loadTravels: rxMethod<{ page?: number; search?: string }>(
      pipe(
        tap(({ page, search }) => {
          patchState(store, {
            loading: true,
            error: null,
            searchQuery: search ?? store.searchQuery(),
            pagination: { ...store.pagination(), page: page ?? 0 },
          });
        }),
        switchMap(({ page, search }) => {
          const p = page ?? 0;
          const q = search ?? store.searchQuery();
          const size = store.pagination().size;
          return q
            ? travelService.searchTravels(q, p, size)
            : travelService.getTravels(p, size);
        }),
        tap({
          next: (response) => {
            patchState(store, {
              travels: response.content,
              pagination: {
                page: response.page,
                size: response.size,
                totalPages: response.totalPages,
                totalElements: response.totalElements,
                first: response.first,
                last: response.last,
              },
              loading: false,
            });
          },
          error: (err: any) => {
            patchState(store, {
              loading: false,
              error: err?.error?.message || 'Failed to load travels',
            });
          },
        }),
      ),
    ),

    loadTravel: rxMethod<string>(
      pipe(
        tap(() => patchState(store, { loading: true, error: null })),
        switchMap((id) => travelService.getTravelById(id)),
        tap({
          next: (travel) => {
            patchState(store, { selectedTravel: travel, loading: false });
          },
          error: (err: any) => {
            patchState(store, {
              loading: false,
              error: err?.error?.message || 'Failed to load travel',
            });
          },
        }),
      ),
    ),

    clearSelectedTravel(): void {
      patchState(store, { selectedTravel: null });
    },
  })),
);
