import { computed, inject } from '@angular/core';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { pipe, switchMap, tap } from 'rxjs';
import { UserResponse, UserRole, UserStatus } from '../../../shared/models/api.models';
import { AdminService } from './admin.service';

interface AdminUsersState {
  users: UserResponse[];
  pagination: {
    page: number;
    size: number;
    totalPages: number;
    totalElements: number;
    first: boolean;
    last: boolean;
  };
  searchQuery: string;
  filterRole: UserRole | null;
  filterStatus: UserStatus | null;
  loading: boolean;
  error: string | null;
}

const initialState: AdminUsersState = {
  users: [],
  pagination: { page: 0, size: 20, totalPages: 0, totalElements: 0, first: true, last: true },
  searchQuery: '',
  filterRole: null,
  filterStatus: null,
  loading: false,
  error: null,
};

export const AdminUsersStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withComputed((store) => ({
    isEmpty: computed(() => !store.loading() && store.users().length === 0),
    activeFilter: computed(() => {
      if (store.searchQuery()) return 'search';
      if (store.filterRole()) return 'role';
      if (store.filterStatus()) return 'status';
      return 'all';
    }),
  })),
  withMethods((store, adminService = inject(AdminService)) => ({
    loadUsers: rxMethod<{ page?: number; search?: string; role?: UserRole | null; status?: UserStatus | null }>(
      pipe(
        tap(({ page, search, role, status }) => {
          patchState(store, {
            loading: true,
            error: null,
            pagination: { ...store.pagination(), page: page ?? 0 },
            searchQuery: search ?? store.searchQuery(),
            filterRole: role !== undefined ? role : store.filterRole(),
            filterStatus: status !== undefined ? status : store.filterStatus(),
          });
        }),
        switchMap(({ page, search, role, status }) => {
          const p = page ?? 0;
          const s = store.pagination().size;
          const q = search ?? store.searchQuery();
          const r = role !== undefined ? role : store.filterRole();
          const st = status !== undefined ? status : store.filterStatus();

          if (q) return adminService.searchUsers(q, p, s);
          if (r) return adminService.getUsersByRole(r, p, s);
          if (st) return adminService.getUsersByStatus(st, p, s);
          return adminService.getUsers(p, s);
        }),
        tap({
          next: (response) => {
            patchState(store, {
              users: response.content,
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
              error: err?.error?.message || 'Failed to load users',
            });
          },
        }),
      ),
    ),

    updateUserInList(updated: UserResponse): void {
      patchState(store, {
        users: store.users().map((u) => (u.id === updated.id ? updated : u)),
      });
    },

    removeUserFromList(id: string): void {
      patchState(store, {
        users: store.users().filter((u) => u.id !== id),
      });
    },

    clearFilters(): void {
      patchState(store, { searchQuery: '', filterRole: null, filterStatus: null });
    },
  })),
);
