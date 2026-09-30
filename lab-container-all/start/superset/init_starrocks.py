"""Register StarRocks as a Superset database connection, idempotently."""

import os

from superset.app import create_app


DATABASE_NAME = "StarRocks"
host = os.environ.get("STARROCKS_HOST", "starrocks")
port = os.environ.get("STARROCKS_PORT", "9030")
# StarRocks' default root user is passwordless inside this isolated Compose network.
uri = f"mysql+pymysql://root@{host}:{port}/?charset=utf8mb4"

app = create_app()
with app.app_context():
    from superset import db
    from superset.models.core import Database

    database = (
        db.session.query(Database).filter_by(database_name=DATABASE_NAME).one_or_none()
    )
    if database is None:
        database = Database(database_name=DATABASE_NAME)
        db.session.add(database)

    database.set_sqlalchemy_uri(uri)
    database.expose_in_sqllab = True
    database.allow_ctas = False
    database.allow_cvas = False
    database.allow_dml = False
    db.session.commit()

print(f"Registered Superset datasource: {DATABASE_NAME} ({host}:{port})")
