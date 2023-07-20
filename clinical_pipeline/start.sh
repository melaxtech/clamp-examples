#!/bin/sh
cd $APP_HOME
echo "args : $*"
echo "start nginx"
nginx
echo "start note book"
nohup jupyter notebook --NotebookApp.notebook_dir /app/notes --allow-root --ip 0.0.0.0 > notebook.log &
echo "check license"
if [ -f "/app/license/CLAMP.LICENSE" ]; then
  echo "found license /app/license/CLAMP.LICENSE."
  mkdir -p /app/BOOT-INF/classes/clamp/license/control/
  cp /app/license/CLAMP.LICENSE BOOT-INF/classes/clamp/license/control/
  jar uvf app.jar /app/BOOT-INF/classes/clamp/license/control/CLAMP.LICENSE
  echo "set license done."
fi
java -Xmx16g -jar app.jar  --spring.config.location=/application.properties $*
