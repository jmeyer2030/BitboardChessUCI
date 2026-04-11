@echo off
REM ============================================================================
REM  DriftWood Puzzle Benchmark Runner
REM
REM  Edit the variables below to configure the benchmark, then run this script
REM  from anywhere — it will cd to the project root automatically.
REM
REM  Variables:
REM    PUZZLE_CSV      - Path to the Lichess puzzle CSV (relative to project root)
REM    COUNT           - Number of puzzles to sample and test
REM    TIME_PER_PUZZLE - Milliseconds the engine gets to search each puzzle
REM    SEED            - Random seed for reproducible puzzle sampling
REM    MIN_RATING      - Minimum puzzle rating (inclusive)
REM    MAX_RATING      - Maximum puzzle rating (inclusive)
REM    TT_SIZE         - Transposition table size in bits (2^N entries)
REM ============================================================================

REM === Configuration ===
SET PUZZLE_CSV=benchmarks\puzzle-bench\data\lichess_db_puzzle.csv
SET COUNT=300
SET TIME_PER_PUZZLE=200
SET SEED=42
SET MIN_RATING=2500
SET MAX_RATING=9999
SET TT_SIZE=18

REM === Resolve project root (two levels up from this script) ===
SET "PROJECT_ROOT=%~dp0..\.."
pushd "%PROJECT_ROOT%"

REM === Build the engine (skip tests for speed) ===
echo Building engine...
call mvn package -DskipTests -q
if %ERRORLEVEL% neq 0 (
    echo.
    echo BUILD FAILED. Fix compilation errors before running the benchmark.
    popd
    exit /b 1
)
echo Build complete.
echo.

REM === Locate the jar ===
for %%f in (target\driftwood-*.jar) do SET "JAR=%%f"
if not defined JAR (
    echo ERROR: Could not find target\driftwood-*.jar
    popd
    exit /b 1
)

REM === Run the benchmark ===
java -cp "%JAR%" com.jmeyer.bench.PuzzleBenchmark ^
    --csv "%PUZZLE_CSV%" ^
    --count %COUNT% ^
    --time %TIME_PER_PUZZLE% ^
    --seed %SEED% ^
    --minRating %MIN_RATING% ^
    --maxRating %MAX_RATING% ^
    --ttSize %TT_SIZE%

popd

