import { test, expect } from '@playwright/test';

test.describe('Login Flow', () => {
    test('should show validation errors for empty form', async ({ page }) => {
        await page.goto('/login');

        const submitButton = page.getByRole('button', { name: /sign in/i });
        await submitButton.click();

        await expect(page.getByText('Username is required')).toBeVisible();
        await expect(page.getByText('Password is required')).toBeVisible();
    });

    test('should navigate to home on successful login', async ({ page }) => {
        // Note: This relies on the real backend being available or mocking network requests
        // For a smoother "production foundation", we usually mock the API in Playwright too

        // Mock the API responses
        await page.route('**/api/auth/login', async route => {
            await route.fulfill({
                status: 200,
                contentType: 'application/json',
                body: JSON.stringify({
                    success: true,
                    data: { token: 'mock-jwt-token' }
                })
            });
        });

        await page.route('**/api/users/me', async route => {
            await route.fulfill({
                status: 200,
                contentType: 'application/json',
                body: JSON.stringify({
                    success: true,
                    data: { id: '1', username: 'e2e-user', role: 'ADMIN', fullName: 'E2E User' }
                })
            });
        });

        await page.goto('/login');

        await page.getByLabel('Username').fill('testuser');
        await page.getByLabel('Password').fill('password123');
        await page.getByRole('button', { name: /sign in/i }).click();

        // Should be redirected to dashboard
        await expect(page).toHaveURL('/');
        await expect(page.getByText('Dashboard')).toBeVisible();
        await expect(page.getByText('E2E User')).toBeVisible();
    });
});
