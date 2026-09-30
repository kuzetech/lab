"""Local Superset configuration for the StarRocks quick-start stack."""

# Superset's MySQL SQL Lab engine imports ``MySQLdb`` on some execution paths.
# StarRocks uses the pure-Python PyMySQL driver in this image, whose documented
# compatibility hook satisfies that import without requiring mysqlclient.
import pymysql

pymysql.install_as_MySQLdb()
