OS_RUN="linux"
if [[ $OSTYPE == 'linux'* ]]; then
	OS_RUN="linux"
elif [[ $OSTYPE == 'darwin'* ]]; then
	if [[ `uname -m` == 'arm'* ]]; then
		OS_RUN="macos-arm"
	else
		OS_RUN="macos"
	fi
fi
echo "Running program on" $OS_RUN

cd libs
ls --file-type *.jar > jars.txt
cd $OS_RUN
ls --file-type *.jar > jars.txt

cd ..
cd ..

libraries=`awk '{printf "libs/"$1":",$0}' libs/jars.txt``awk -v os_run=$OS_RUN '{printf "libs/"os_run"/"$1":",$0}' libs/$OS_RUN/jars.txt`

javaFiles=`find . -name "*.java" -print`

javac -cp $libraries:src/:res/ $javaFiles
java -cp $libraries:src/:res/ com/countgandi/game/Main
