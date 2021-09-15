REM REMOTE_DEBUG: '-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005'
set JAVA_OPTS=%REMOTE_DEBUG% -Dfile.encoding=utf-8 -Dspring.profiles.active="development"