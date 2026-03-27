import { RenderMode, ServerRoute } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  // Static pages can be prerendered
  {
    path: '',
    renderMode: RenderMode.Prerender,
  },
  {
    path: 'auth/**',
    renderMode: RenderMode.Prerender,
  },
  {
    path: 'unauthorized',
    renderMode: RenderMode.Prerender,
  },
  // Dynamic pages render on client only
  {
    path: '**',
    renderMode: RenderMode.Client,
  },
];
