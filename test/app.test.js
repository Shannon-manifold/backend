const assert = require('node:assert/strict');
const http = require('node:http');
const { test } = require('node:test');

const { createApp } = require('../src/app');
const { MAX_CODE_BYTES } = require('../src/leanVerifier');

function request(app, { method = 'GET', path = '/', body } = {}) {
  return new Promise((resolve, reject) => {
    const server = app.listen(0, '127.0.0.1', () => {
      const { port } = server.address();
      const payload = body === undefined ? null : JSON.stringify(body);
      const req = http.request(
        {
          hostname: '127.0.0.1',
          port,
          path,
          method,
          headers: payload
            ? {
                'content-type': 'application/json',
                'content-length': Buffer.byteLength(payload),
              }
            : {},
        },
        (res) => {
          const chunks = [];
          res.on('data', (chunk) => chunks.push(chunk));
          res.on('end', () => {
            server.close(() => {
              const text = Buffer.concat(chunks).toString('utf8');
              resolve({
                statusCode: res.statusCode,
                body: text ? JSON.parse(text) : null,
              });
            });
          });
        },
      );

      req.on('error', (error) => {
        server.close(() => reject(error));
      });

      if (payload) {
        req.write(payload);
      }
      req.end();
    });
  });
}

test('GET /lean/health returns 503 when Lean is unavailable', async () => {
  const app = createApp({
    verifier: {
      MAX_CODE_BYTES,
      getLeanHealth: async () => ({
        ready: false,
        lake: null,
        lean: null,
        error: 'Lake is not available.',
      }),
      verifyLeanCode: async () => {
        throw new Error('verifyLeanCode should not be called');
      },
    },
  });

  const response = await request(app, { path: '/lean/health' });

  assert.equal(response.statusCode, 503);
  assert.equal(response.body.ready, false);
  assert.equal(response.body.error, 'Lake is not available.');
});

test('POST /lean/verify rejects a missing code field', async () => {
  const app = createApp();

  const response = await request(app, {
    method: 'POST',
    path: '/lean/verify',
    body: {},
  });

  assert.equal(response.statusCode, 400);
  assert.match(response.body.error, /code/);
});

test('POST /lean/verify is disabled by default in production', async () => {
  const app = createApp({
    env: { NODE_ENV: 'production' },
    verifier: {
      MAX_CODE_BYTES,
      getLeanHealth: async () => ({ ready: true }),
      verifyLeanCode: async () => {
        throw new Error('verifyLeanCode should not be called');
      },
    },
  });

  const response = await request(app, {
    method: 'POST',
    path: '/lean/verify',
    body: { code: 'example : True := by trivial' },
  });

  assert.equal(response.statusCode, 403);
  assert.match(response.body.error, /disabled/);
});
