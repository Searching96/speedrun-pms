# Speedrun PMS Frontend

Production-ready frontend for the Postal Management System (PMS), built with React, TypeScript, Vite, and Shadcn UI.

## 🚀 Features

- **Modern Stack**: React 19, TypeScript 5, Vite 6
- **UI Architecture**: Shadcn UI + Tailwind CSS
- **State Management**: TanStack Query (React Query)
- **Routing**: React Router 7 with Protected Routes
- **Form Handling**: React Hook Form + Zod Validation
- **Authentication**: JWT-based Auth Provider with Auto-Refresh Handling
- **Testing**: Vitest (Unit), React Testing Library (Integration), Playwright (E2E)
- **Quality Gates**: Husky Pre-commit Hooks, ESLint, Prettier

## 🛠️ Getting Started

### Prerequisites

- Node.js 20+
- pnpm 9+

### Installation

```bash
pnpm install
pnpm prepare
```

### Development

```bash
pnpm dev
```

### Environment Variables

Copy `.env.example` to `.env`:

```env
VITE_API_URL=http://localhost:8080/api
```

## 🧪 Testing Handbook

We maintain a high standard of code quality through automated testing.

### Unit & Integration Tests (Vitest)

For components, hooks, and utilities.

```bash
# Run all unit tests
pnpm test

# Run with UI
pnpm test:ui

# Check coverage
pnpm test:coverage
```

**Where to write tests:**
- Components: `src/components/__tests__` or `Component.test.tsx` next to file
- Pages: `src/pages/PageName.test.tsx`
- Utils: `src/lib/util.test.ts`

### End-to-End Tests (Playwright)

For critical user flows (Login, Order Creation, Tracking).

```bash
# Run E2E tests
pnpm test:e2e

# Run with UI
pnpm test:e2e:ui
```

**Location**: `e2e/*.spec.ts`

## 📦 Project Structure

```
src/
├── api/            # API services (barrel exported)
├── components/     # Reusable UI components
│   ├── ui/         # Shadcn primitives
│   └── layout/     # Layout components
├── features/       # Feature-based logic (Auth, Orders)
├── lib/            # Utilities (axios, error handling)
├── pages/          # Application pages (Route targets)
├── hooks/          # Custom hooks
├── types/          # TypeScript interfaces
└── tests/          # Test setup & utilities
```

## 👷 CI/CD & Quality Gates

This project uses Husky to run quality checks on commit:
- Linting
- Formatting
- Type checking

If validation fails, the commit is blocked. Fix errors or use `--no-verify` (not recommended).
