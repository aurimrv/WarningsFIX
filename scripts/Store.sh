#####################################################
#####    armazenar dados de duração de execução no BD
#####################################################

JARS_DIR=$WARNING_HOME/parser/StoreTime/lib 
DIR_TMP=$WARNING_HOME/tmp					   
cd $JARS_DIR

ls *.jar >& $DIR_TMP/libs-jar-store.txt

JARS="";

for JAR in `cat $DIR_TMP/libs-jar-store.txt` 
do JARS=$JARS_DIR/$JAR:$JARS
done


cd /home/vr/Dropbox/WarningsFix/parser/StoreTime/bin
CLASSPATH=$CLASSPATH:$JARS
java -cp $CLASSPATH Store $1 $2

#####################################################
#####           Terminando                      #####  
#####################################################
