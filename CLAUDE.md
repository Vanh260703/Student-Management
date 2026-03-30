# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
npm run dev      # Start development server (Vite, port 5173)
npm run build    # Production build → dist/
npm run preview  # Preview production build locally
npm run lint     # Run ESLint
```

## Environment

Copy `.env` and set `VITE_API_URL` (default: `http://localhost:8080/api/v2`).

## Architecture

**Stack**: React 19 + Vite 8, JavaScript (JSX, not TypeScript), Tailwind CSS v4 (`@tailwindcss/vite`), React Router v7, Zustand, Axios, React Hook Form + Zod, react-toastify, Recharts, dayjs.

**Entry**: `index.html` → `src/main.jsx` → `<App />` → `<RouterProvider>` + `<ToastContainer>`

### Routing & Auth (`src/routes/`)
- `ProtectedRoute.jsx` — checks `isAuthenticated` + `allowedRoles` from Zustand, redirects to `/login` or `/unauthorized`
- `index.jsx` — full route tree; three role-based groups: ROLE_ADMIN, ROLE_TEACHER, ROLE_STUDENT
- `RoleRedirect.jsx` — `/` redirects to the role's home dashboard

### State Management (`src/store/`)
- `authStore.js` — Zustand store for user/token; `setAuth()`, `logout()`, `updateUser()`; syncs to `localStorage`
- `notificationStore.js` — Zustand store for notifications + unread count; polls every 30 s via `NotificationBell`

### API Layer (`src/api/`)
- `axiosInstance.js` — base URL from `VITE_API_URL`, auto-attaches Bearer token, queues 401 retries and calls `/auth/refresh-token`; redirects to `/login` on refresh failure
- One file per domain: `auth.js`, `dashboard.js`, `users.js`, `classes.js`, `grades.js`, `attendance.js`, `payments.js`, `notifications.js`, `profile.js`

### Layout (`src/components/layout/`)
- `AppLayout.jsx` — shared shell: collapsible sidebar (mobile overlay), sticky header; used by all three role layouts via re-export
- `Sidebar.jsx` — nav items keyed by `user.role`; logout clears store + localStorage
- `Header.jsx` — avatar, role label, `NotificationBell`
- `NotificationBell.jsx` — dropdown with last 5 notifications; polls unread count

### Pages (`src/pages/`)
- `auth/` — LoginPage, ForgotPasswordPage, ResetPasswordPage
- `admin/` — Dashboard (stats + Recharts), UserManagement (CRUD, block/unblock, search, pagination)
- `teacher/` — Dashboard, Classes, Grades (per-class grade entry + publish), Attendance (date picker + status toggle), Profile
- `student/` — Dashboard, Classes (my enrollments + enroll/unenroll from available), Grades (transcript), Payments (tuition + MoMo), Profile (with avatar upload)
- `NotificationsPage.jsx` — shared across roles; mark-as-read, delete
- `RoleRedirect.jsx`, `UnauthorizedPage.jsx`

### Common Components (`src/components/common/`)
- `StatCard.jsx` — icon + label + value card used in all dashboards
- `Modal.jsx` — accessible modal (Escape to close, backdrop click)
- `Pagination.jsx` — page buttons for server-side pagination
- `LoadingSpinner.jsx` — centered spinner with message

### Path Alias
`@/` maps to `src/` (configured in `vite.config.js`). Use `@/api/...`, `@/store/...` etc throughout.
