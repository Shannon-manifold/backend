const { spawn } = require('node:child_process');
const fs = require('node:fs/promises');
const os = require('node:os');
const path = require('node:path');

const PROJECT_ROOT = path.resolve(__dirname, '..');
const MAX_CODE_BYTES = 64 * 1024;
const MAX_OUTPUT_BYTES = 200 * 1024;
const DEFAULT_TIMEOUT_MS = 10_000;

function parseLeanJsonDiagnostics(output) {
  return output
    .split(/\r?\n/)
    .map((line) => line.trim())
    .filter(Boolean)
    .flatMap((line) => {
      try {
        return [JSON.parse(line)];
      } catch {
        return [];
      }
    });
}

function runProcess(command, args, options = {}) {
  const {
    cwd = PROJECT_ROOT,
    timeoutMs = DEFAULT_TIMEOUT_MS,
    outputLimitBytes = MAX_OUTPUT_BYTES,
  } = options;

  return new Promise((resolve) => {
    const stdoutChunks = [];
    const stderrChunks = [];
    let outputBytes = 0;
    let timedOut = false;
    let outputTruncated = false;
    let settled = false;

    const child = spawn(command, args, {
      cwd,
      shell: false,
      windowsHide: true,
    });

    const finish = (result) => {
      if (settled) {
        return;
      }
      settled = true;
      clearTimeout(timeout);
      resolve({
        ...result,
        stdout: Buffer.concat(stdoutChunks).toString('utf8'),
        stderr: Buffer.concat(stderrChunks).toString('utf8'),
        timedOut,
        outputTruncated,
      });
    };

    const appendChunk = (chunks, chunk) => {
      const nextBytes = outputBytes + chunk.length;
      const remaining = outputLimitBytes - outputBytes;

      if (remaining > 0) {
        chunks.push(chunk.subarray(0, remaining));
      }

      if (nextBytes > outputLimitBytes && !outputTruncated) {
        outputTruncated = true;
        child.kill('SIGKILL');
      }

      outputBytes = nextBytes;
    };

    const timeout = setTimeout(() => {
      timedOut = true;
      child.kill('SIGKILL');
    }, timeoutMs);

    child.stdout.on('data', (chunk) => {
      appendChunk(stdoutChunks, chunk);
    });

    child.stderr.on('data', (chunk) => {
      appendChunk(stderrChunks, chunk);
    });

    child.on('error', (error) => {
      finish({
        exitCode: null,
        signal: null,
        error: error.message,
      });
    });

    child.on('close', (exitCode, signal) => {
      finish({
        exitCode,
        signal,
        error: null,
      });
    });
  });
}

function firstNonEmptyLine(...values) {
  return values
    .flatMap((value) => String(value || '').split(/\r?\n/))
    .map((line) => line.trim())
    .find(Boolean) || null;
}

async function getLeanHealth(options = {}) {
  const { cwd = PROJECT_ROOT, lakeCommand = 'lake', timeoutMs = 3_000 } = options;
  const lake = await runProcess(lakeCommand, ['--version'], { cwd, timeoutMs });

  if (lake.error || lake.exitCode !== 0) {
    return {
      ready: false,
      lake: null,
      lean: null,
      error: lake.error || firstNonEmptyLine(lake.stderr, lake.stdout) || 'Lake is not available.',
    };
  }

  const lean = await runProcess(lakeCommand, ['env', 'lean', '--version'], {
    cwd,
    timeoutMs,
  });

  return {
    ready: lean.exitCode === 0 && !lean.error,
    lake: firstNonEmptyLine(lake.stdout, lake.stderr),
    lean: firstNonEmptyLine(lean.stdout, lean.stderr),
    error:
      lean.exitCode === 0 && !lean.error
        ? null
        : lean.error || firstNonEmptyLine(lean.stderr, lean.stdout) || 'Lean is not available.',
  };
}

async function verifyLeanCode(code, options = {}) {
  if (typeof code !== 'string') {
    throw new TypeError('Lean code must be a string.');
  }

  if (Buffer.byteLength(code, 'utf8') > MAX_CODE_BYTES) {
    const error = new Error(`Lean code must be ${MAX_CODE_BYTES} bytes or smaller.`);
    error.code = 'LEAN_CODE_TOO_LARGE';
    throw error;
  }

  const {
    cwd = PROJECT_ROOT,
    lakeCommand = 'lake',
    timeoutMs = DEFAULT_TIMEOUT_MS,
    outputLimitBytes = MAX_OUTPUT_BYTES,
  } = options;

  const startedAt = process.hrtime.bigint();
  const tempDir = await fs.mkdtemp(path.join(os.tmpdir(), 'lean-submission-'));
  const submissionPath = path.join(tempDir, 'Submission.lean');

  try {
    await fs.writeFile(submissionPath, code, 'utf8');

    const result = await runProcess(
      lakeCommand,
      ['env', 'lean', '--json', submissionPath],
      {
        cwd,
        timeoutMs,
        outputLimitBytes,
      },
    );

    const durationMs = Number((process.hrtime.bigint() - startedAt) / 1_000_000n);
    const diagnostics = parseLeanJsonDiagnostics(`${result.stdout}\n${result.stderr}`);
    const valid =
      result.exitCode === 0 &&
      !result.error &&
      !result.timedOut &&
      !result.outputTruncated;

    return {
      valid,
      exitCode: result.exitCode,
      diagnostics,
      stdout: result.stdout,
      stderr: result.stderr,
      durationMs,
      timedOut: result.timedOut,
      outputTruncated: result.outputTruncated,
      error: result.error,
    };
  } finally {
    await fs.rm(tempDir, { recursive: true, force: true });
  }
}

module.exports = {
  DEFAULT_TIMEOUT_MS,
  MAX_CODE_BYTES,
  MAX_OUTPUT_BYTES,
  getLeanHealth,
  parseLeanJsonDiagnostics,
  verifyLeanCode,
};
