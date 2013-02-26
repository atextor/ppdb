ppdb - Private Product Database
===============================

Simple standalone web-based UI for a database containing products, sellers, makers and links to sales.

This is based on SQLite, Jersey (REST interface), Grizzly (Embedded Web Server) and Apache Velocity (Template Engine).

In the current version, this only runs on Linux, as libsqlite4java is employed which depends on native SQLite libraries.


Running
-------

Check out Eclipse project, run de.atextor.ppdb.Server. Open browser at http://localhost:9998/.

Own entries
-----------

Edit products.sqlite with your favorite SQLite editor (Firefox addon SQLite Manager is nice).

Author
------

ppdb was written by Andreas Textor <textor.andreas@googlemail.com>.


