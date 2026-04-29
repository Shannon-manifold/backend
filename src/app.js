const express = require('express');
const cors = require('cors');

const defaultVerifier = require('./leanVerifier');

function createApp({ verifier = defaultVerifier, env = process.env } = {}) {
  const app = express();

  app.use(cors());
  app.use(express.json({ limit: '80kb' }));
  app.use(express.urlencoded({ extended: true, limit: '80kb' }));

  app.get('/', (req, res) => {
    res.json({ message: 'Welcome to the backend API' });
  });

  app.get('/lean/health', async (req, res, next) => {
    try {
      const health = await verifier.getLeanHealth();
      res.status(health.ready ? 200 : 503).json(health);
    } catch (error) {
      next(error);
    }
  });

  app.post('/lean/verify', async (req, res, next) => {
    if (env.NODE_ENV === 'production' && env.LEAN_VERIFY_ENABLED !== 'true') {
      res.status(403).json({
        error: 'Lean verification is disabled in production.',
      });
      return;
    }

    const code = req.body?.code;
    if (typeof code !== 'string') {
      res.status(400).json({
        error: 'Request body must include a string `code` field.',
      });
      return;
    }

    if (Buffer.byteLength(code, 'utf8') > verifier.MAX_CODE_BYTES) {
      res.status(413).json({
        error: `Lean code must be ${verifier.MAX_CODE_BYTES} bytes or smaller.`,
      });
      return;
    }

    try {
      const result = await verifier.verifyLeanCode(code);
      res.status(result.error && result.exitCode === null ? 503 : 200).json(result);
    } catch (error) {
      next(error);
    }
  });

  app.use((error, req, res, next) => {
    if (error.type === 'entity.too.large') {
      res.status(413).json({ error: 'Request body is too large.' });
      return;
    }

    res.status(500).json({ error: 'Internal server error.' });
  });

  return app;
}

module.exports = { createApp };
