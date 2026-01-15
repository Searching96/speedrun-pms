import { defineConfig } from 'vitest/config';
import react from '@vitejs/plugin-react';
import path from 'path';

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [react()],
    resolve: {
        alias: {
            '@': path.resolve(__dirname, './src'),
        },
    },
    test: {
        globals: true,
        environment: 'jsdom',
        setupFiles: './src/tests/setup.ts',
        css: true,
        exclude: ['**/node_modules/**', '**/dist/**', '**/e2e/**', '**/cypress/**'],
        coverage: {
            provider: 'v8',
            reporter: ['text', 'json', 'html'],
            exclude: [
                'node_modules/',
                'src/tests/',
                '**/*.test.{ts,tsx}',
                '**/*.spec.{ts,tsx}',
                '**/types/',
                '**/*.config.{ts,js}',
            ],
        },
    },
});
