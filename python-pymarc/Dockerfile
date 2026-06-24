# syntax=docker/dockerfile:1

# Python is interpreted, so unlike the Java image there's no separate compile
# step to split out — a single slim stage installs the dependency and runs.
FROM python:3.13-slim
WORKDIR /app

# Install the dependency on its own layer so it caches when only main.py changes.
# (pymarc is the sole runtime dependency declared in pyproject.toml.)
RUN pip install --no-cache-dir "pymarc>=5.2.0"

COPY main.py .

# main.py's default data path is ../marc-records/... relative to the script,
# which resolves to /marc-records once that volume is mounted (see compose).
ENTRYPOINT ["python", "main.py"]
