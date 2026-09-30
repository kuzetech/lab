FROM apache/superset:6.0.0

USER root
# The base image's runtime interpreter is /app/.venv/bin/python.  It is a
# deliberately pip-less virtualenv, so install the driver directly in its site-packages.
RUN pip install --no-cache-dir --target /app/.venv/lib/python3.10/site-packages pymysql
COPY --chown=superset:superset init_starrocks.py /app/pythonpath/init_starrocks.py
COPY --chown=superset:superset superset_config.py /app/pythonpath/superset_config.py
COPY --chown=superset:superset assets/gesac-dashboard.zip /app/assets/gesac-dashboard.zip
USER superset
