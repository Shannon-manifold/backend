const assert = require('node:assert/strict');
const { test } = require('node:test');

const {
  getLeanHealth,
  parseLeanJsonDiagnostics,
} = require('../src/leanVerifier');

test('parseLeanJsonDiagnostics keeps valid JSON lines and ignores malformed lines', () => {
  const diagnostics = parseLeanJsonDiagnostics(
    [
      '{"severity":"error","message":"unknown identifier"}',
      'not json',
      '',
      '{"severity":"warning","message":"unused variable"}',
    ].join('\n'),
  );

  assert.deepEqual(diagnostics, [
    { severity: 'error', message: 'unknown identifier' },
    { severity: 'warning', message: 'unused variable' },
  ]);
});

test('getLeanHealth reports not ready when lake cannot be spawned', async () => {
  const health = await getLeanHealth({
    lakeCommand: 'lake-command-that-does-not-exist-for-test',
    timeoutMs: 100,
  });

  assert.equal(health.ready, false);
  assert.equal(health.lake, null);
  assert.equal(health.lean, null);
  assert.match(health.error, /ENOENT|not found|spawn/);
});
