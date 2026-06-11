import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  base: '/team-theta-ai-check/',
  plugins: [react()],
  server: {
    port: 5173
  }
});
